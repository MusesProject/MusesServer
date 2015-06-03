/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.dataminer;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 UGR
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import eu.musesproject.server.scheduler.ModuleType;

import org.apache.log4j.Logger;

import weka.attributeSelection.AttributeSelection;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.*;
import weka.classifiers.evaluation.Evaluation;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Applications;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.Roles;
import eu.musesproject.server.entity.SecurityRules;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.SystemLogKrs;
import eu.musesproject.server.entity.Users;

/**
 * The Class DataMiner.
 * 
 * @author Sergio Zamarripa (S2)
 * @version Sep 30, 2013
 */
public class DataMiner {
	
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(DataMiner.class);
	
	public List<SimpleEvents> getSimpleEvents() {
		
		List<SimpleEvents> Events = dbManager.getEvent();
		
		return Events;
	}
	
	
	
	/**
	 * Info DB
	 * 
	 *   Interaction with the database, retrieving events in bulk, and fills the system_log_krs table in the server database. This table helps the CSO having an overview of the status of the system.
	 * 
	 * 
	 * @param events Complete list of simple events, stored in the simple_events table of the database.
	 * 
	 * 
	 * @return void
	 */
	
	public void retrievePendingEvents(List<SimpleEvents> events){
		
		//List<SimpleEvents> Events = dbManager.getEvent();
		
		/* Fields in system_log_krs:
		 * previous_event_id, current_event_id, decision_id, user_behaviour_id,
		 * security_incident_id, device_security_state, risk_treatment, start_time,
		 * finish_time.
		 */
		
		List<SystemLogKrs> list = new ArrayList<SystemLogKrs>();
		
		if (events.size() > 0) {
			Iterator<SimpleEvents> i = events.iterator();
			
			while (i.hasNext()) {
				
				SystemLogKrs logEntry = new SystemLogKrs();
				SimpleEvents event = i.next();
				BigInteger eventID = new BigInteger(event.getEventId());
				logEntry.setCurrentEventId(eventID);
				
				//logger.info(eventID);
				
				/* Previous event is the last event the user made */
				String user = event.getUser().getUserId();
				Date day = event.getDate();
				String time = event.getTime().toString();
				SimpleEvents userLastEvent = dbManager.findEventsByUserId(user, day.toString(), time, Boolean.TRUE);
				if (userLastEvent != null) {
					BigInteger lastEvent = new BigInteger(userLastEvent.getEventId());
					logEntry.setPreviousEventId(lastEvent);
				} else {
					//logger.warn("No previous events by this user, assigning 0...");
					logEntry.setPreviousEventId(BigInteger.ZERO);
				}
				
				
				/* Looking for decision_id in table access_request */
				BigInteger decisionID = BigInteger.ZERO;
				List<AccessRequest> accessRequests = dbManager.findAccessRequestByEventId(eventID.toString());
				if (accessRequests.size() == 1) {
					decisionID = accessRequests.get(0).getDecisionId();
					logEntry.setDecisionId(decisionID);
				} else {
					//logger.warn("Decision Id not found, assigning 0...");
					logEntry.setDecisionId(BigInteger.ZERO);
				}
				
				/* User behaviour as next event_id */
				SimpleEvents userNextEvent = dbManager.findEventsByUserId(user, day.toString(), time, Boolean.FALSE);
				if (userNextEvent != null) {
					BigInteger nextEvent = new BigInteger(userNextEvent.getEventId());
					logEntry.setUserBehaviourId(nextEvent);
				} else {
					//logger.warn("No more events by this user after this one, assigning 0...");
					logEntry.setUserBehaviourId(BigInteger.ZERO);
				}				
				
				/* Looking if that event caused a security violation */
				List<SecurityViolation> securityViolations = dbManager.findSecurityViolationByEventId(event.getEventId());
				if (securityViolations.size() > 0) {
					BigInteger securityIncident = new BigInteger(securityViolations.get(0).getSecurityViolationId());
					logEntry.setSecurityIncidentId(securityIncident);
				} else {
					//logger.warn("Security violation not found, or this event did not cause a security violation, assigning 0...");
					logEntry.setSecurityIncidentId(BigInteger.ZERO);
				}
				
				/* Checking the device security state of the device */
				logEntry.setDeviceSecurityState(BigInteger.ZERO);
				
				/* Looking for the risk treatment in case the event caused a security violation */
				String riskTreatment = securityViolations.get(0).getMessage();
				if (riskTreatment != null){
					logEntry.setRiskTreatment(riskTreatment);
				} else {
					//logger.warn("Risk Treatment not found, or this event did not cause a security violation, assigning 0...");
					logEntry.setRiskTreatment(null);					
				}
								
				/* Time when the event was detected in the device */
				Date eventDate = event.getDate();
				logEntry.setStartTime(eventDate);
				
				/* Time when was received and processed in the server */
				logEntry.setFinishTime(eventDate);
				
				list.add(logEntry);
				
			}
		}else{
			logger.error("There are not simple events in the database, system_log_krs cannot be filled.");
		}
		
		dbManager.setSystemLogKRS(list);
		
	}
	
	/**
	  * minePatterns - Method for filling the patterns_krs table in the database. Each row of this table consists of all interesting information related to an event.
	  *
	  * @param none 
	  * 
	  */
	public PatternsKrs minePatterns(SimpleEvents event){
		
		PatternsKrs pattern = new PatternsKrs();
		
		/* Obtaining decision (label of the pattern) by obtaining first the AccessRequest related to that event, and then the decision related to it */
		String eventID = event.getEventId();
		String decisionID = null;
		String label;
		List<AccessRequest> accessRequests = dbManager.findAccessRequestByEventId(eventID);
		if (accessRequests.size() > 0) {
			decisionID = accessRequests.get(0).getDecisionId().toString();
			List<Decision> decisions = null;
			decisions = dbManager.findDecisionById(decisionID);
			//List<Decision> decisions = dbManager.findDecisionById("558");
			if (decisions != null) {
				label = decisions.get(0).getValue();
				pattern.setLabel(label);
			} else {
				/* Secret solution while not having data */
				if (dbManager.findSecurityViolationByEventId(eventID) != null){
					pattern.setLabel("STRONGDENY");
				} else {
					pattern.setLabel("GRANTED");
				}
			}
		} else {
			/* Secret solution while not having data */
			if (dbManager.findSecurityViolationByEventId(eventID).size() > 0){
				pattern.setLabel("STRONGDENY");
			} else {
				pattern.setLabel("GRANTED");
			}
		}
		
		/* Obtaining decision cause */
		if (pattern.getLabel().contentEquals("STRONGDENY")){
			List<SecurityViolation> secViolations = dbManager.findSecurityViolationByEventId(eventID);
			if (secViolations.size() > 0) {
				Pattern p = Pattern.compile("<(.+?)>(.+?)</(.+?)>");
				Matcher matcher = p.matcher(secViolations.get(0).getConditionText());
				matcher.find();
				String decisionCause = matcher.group(1);
				pattern.setDecisionCause(decisionCause);
			} else {
				pattern.setDecisionCause(null);
			}
		} else {
			pattern.setDecisionCause(null);
		}
				
		/* Finding the type of the event */
		EventType eventTypeId = event.getEventType();
		String eventType = eventTypeId.getEventTypeKey();
		if (eventType != null) {
			pattern.setEventType(eventType);
		} else {
			pattern.setEventType(null);
		}
		
		/* Is the event a simple event or a complex event? */
		String eventLevel = eventTypeId.getEventLevel();
		if (eventLevel != null) {
			pattern.setEventLevel(eventLevel);
		} else {
			pattern.setEventLevel(null);
		}
		
		/* Storing only the username */
		Users user = event.getUser();
		String username = user.getUsername();
		if (username != null) {
			pattern.setUsername(username);
		} else {
			pattern.setUsername(null);
		}
		
		/* Extracting information from the password */
		String userPassword = user.getPassword();
		// Characters in the password
		int passwordLength = userPassword.length();
		pattern.setPasswordLength(passwordLength);
		// Lexical information about the password
		String digits = "\\d";
		String letters = "[a-zA-Z]";
		String capLetters = "[A-Z]";
		Pattern digitPattern = Pattern.compile(digits);
		Pattern letterPattern = Pattern.compile(letters);
		Pattern capLetterPattern = Pattern.compile(capLetters);
		Matcher digitsMatcher = digitPattern.matcher(userPassword);
		Matcher lettersMatcher = letterPattern.matcher(userPassword);
		Matcher capLettersMatcher = capLetterPattern.matcher(userPassword);
		
		int digitsCount = 0;
		int lettersCount = 0;
		int capLettersCount = 0;
		
		while (digitsMatcher.find()) {
			digitsCount++;
		}
		while (lettersMatcher.find()) {
			lettersCount++;
		}
		while (capLettersMatcher.find()) {
			capLettersCount++;
		}
		
		pattern.setNumbersInPassword(digitsCount);
		pattern.setLettersInPassword(lettersCount);
		pattern.setPasswdHasCapitalLetters(capLettersCount);
		
		/* Obtaining the user and device trust values at the time the event was thrown */
		List<DecisionTrustvalues> trustValues = dbManager.findDecisionTrustValuesByDecisionId("545");
		//List<DecisionTrustvalues> trustValues = dbManager.findDecisionTrustValuesByDecisionId(decisionID);
		double userTrustValue, deviceTrustValue;
		if (trustValues.size() > 0) {
			userTrustValue = trustValues.get(0).getUsertrustvalue();
			deviceTrustValue = trustValues.get(0).getDevicetrustvalue();
			pattern.setUserTrustValue(userTrustValue);
			pattern.setDeviceTrustValue(deviceTrustValue);
		} else {
			pattern.setUserTrustValue(Double.NaN);
			pattern.setDeviceTrustValue(Double.NaN);
		}
		
		/* Checking if the user account is activated */
		int activatedAccount = user.getEnabled();
		pattern.setActivatedAccount(activatedAccount);
		
		/* Obtaining the role of the user inside the company */
		int userRoleId = user.getRoleId();
		//Roles userRole = dbManager.getRoleById(userRoleId);
		Roles userRole = dbManager.getRoleById(145);
		String userRoleName = userRole.getName();
		if (userRoleName != null) {
			pattern.setUserRole(userRoleName);
		} else {
			pattern.setUserRole(null);
		}
		
		/* Detection time of the event */
		Date eventDate = event.getDate();
		Time eventTime = event.getTime();
		Date eventDetection = new Date(eventDate.getYear(), eventDate.getMonth(), eventDate.getDate(), eventTime.getHours(), eventTime.getMinutes(), eventTime.getSeconds());
		if (eventDetection.toString() != null) {
			pattern.setEventTime(eventDetection);
		} else {
			pattern.setEventTime(null);
		}
		
		/* Was MUSES in silent or verbose mode? */		
		if (eventDetection.getDay() < 16 && eventDetection.getMonth() <= 3) {
			pattern.setSilentMode(1);
		}
		if (eventDetection.getDay() >= 16 && eventDetection.getMonth() == 3) {
			pattern.setSilentMode(0);
		}
		if (eventDetection.getMonth() > 3) {
			pattern.setSilentMode(0);
		}
		
		/* Obtaining the model of device */
		Devices userDeviceId = event.getDevice();
		String deviceModel = userDeviceId.getDeviceModel();
		if (deviceModel != null) {
			pattern.setDeviceType(deviceModel);
		} else {
			pattern.setDeviceType(null);
		}
		
		/* Device characteristics */
		// OS
		String userDeviceOS = userDeviceId.getOS_name().concat(userDeviceId.getOS_version());
		if (userDeviceOS != null) {
			pattern.setDeviceOS(userDeviceOS);
		} else {
			pattern.setDeviceOS(null);
		}
		// Certificate of device
		byte[] deviceCertificate = userDeviceId.getCertificate();
		if (deviceCertificate.length > 0) {
			pattern.setDeviceHasCertificate(1);
		} else {
			pattern.setDeviceHasCertificate(0);
		}
		// Device company owned or employee owned
		String deviceOwner = userDeviceId.getOwnerType();
		if (deviceOwner != null) {
			pattern.setDeviceOwnedBy(deviceOwner);
		} else {
			pattern.setDeviceOwnedBy(null);
		}
		
		/* Characteristics of the application which the user was using at the time of the event */
		Applications eventApp = event.getApplication();
		// App name and version
		String appName = eventApp.getName().concat(eventApp.getVersion());
		if (appName != null) {
			pattern.setAppName(appName);
		} else {
			pattern.setAppName(null);
		}
		// Vendor of the app
		String appVendor = eventApp.getVendor();
		if (appVendor != null) {
			pattern.setAppVendor(appVendor);
		} else {
			pattern.setAppVendor(null);
		}
		// Is the application MUSES Aware?
		int appMusesAware = eventApp.getIs_MUSES_aware();
		pattern.setAppMUSESAware(appMusesAware);
		
		/* Asset that the event is trying to access to */
		Assets eventAsset = event.getAsset();
		// Name of the asset
		String assetName = eventAsset.getTitle();
		if (assetName != null) {
			pattern.setAssetName(assetName);
		} else {
			pattern.setAssetName(null);
		}
		// Asset value
		double assetValue = eventAsset.getValue();
		pattern.setAssetValue(assetValue);
		// Confidentiality level of the asset
		String assetConfidentialLevel = eventAsset.getConfidentialLevel();
		if (assetConfidentialLevel != null) {
			pattern.setAssetConfidentialLevel(assetConfidentialLevel);
		} else {
			pattern.setAssetConfidentialLevel(null);
		}
		// Where is the asset located
		String assetLocation = eventAsset.getLocation();
		if (assetLocation != null) {
			pattern.setAssetLocation(assetLocation);
		} else {
			pattern.setAssetLocation(null);
		}
		
		/* Rest of parameters that have to be obtained from the JSON */
		// This solution of reading from the csv is strictly strictly processing the data from trials #1
		File configFile = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {		
			configFile = new File ("/home/paloma/MUSES/Trials/devicesconfiguration.csv");
			fr = new FileReader (configFile);
			br = new BufferedReader(fr);
			String line;
	        while((line=br.readLine())!=null) {
	        	String[] content = line.split(",");
	        	/* content[1] = "ispasswordprotected"
	        	 * content[2] = "isrooted"
	        	 * content[3] = "screentimeoutinseconds"
	        	 * content[4] = "istrustedantivirusinstalled"
	        	 * content[5] = "accessibilityenabled"
	        	 * */
	        	if (content[0].equals(userDeviceId.getDeviceId())) {
	        		pattern.setDeviceHasPassword(Integer.parseInt(content[1]));
	        		BigInteger time = BigInteger.valueOf(Integer.parseInt(content[3]));
	        		pattern.setDeviceScreenTimeout(time);
	        		pattern.setDeviceHasAccessibility(Integer.parseInt(content[5]));
	        		pattern.setDeviceIsRooted(Integer.parseInt(content[2]));
	        	}
	        }
		} catch(Exception e) {
			e.printStackTrace();
	    }
		try{                   
            if( null != fr ){  
               fr.close();    
            }                 
         }catch (Exception e2){
            e2.printStackTrace();
         }
        
		/* If the user is sending an email */
		/* Data in event_type_if = 11
		 * {event=ACTION_SEND_MAIL, properties={"to":"the.reiceiver@generic.com,
		 * another.direct.receiver@generic.com","noAttachments":"1",
		 * "subject":"MUSES sensor status subject","bcc":"hidden.reiceiver@generic.com",
		 * "from":"max.mustermann@generic.com","attachmentInfo":"pdf",
		 * "cc":"other.listener@generic.com, 2other.listener@generic.com"}}
		*/
		
		if (eventTypeId.getEventTypeId() == 11) {
			String mailJSON =  	"\\\"to\\\"\\:\\\"(.*)\\\",\\\"noAttachments\\\"\\:\\\"(.*)\\\",\\\"subject\\\"\\:\\\"(.*)\\\",\\\"bcc\\\"\\:\\\"(.*)\\\",\\\"attachmentInfo\\\"\\:\\\"(.*)\\\",\\\"from\\\"\\:\\\"(.*)\\\",\\\"cc\\\"\\:\\\"(.*)\\\"";
			Pattern mailPattern = Pattern.compile(mailJSON);
			Matcher matcherMail = mailPattern.matcher(event.getData());
			if (matcherMail.find()) {
				if (matcherMail.group(4).equals("none")) {
					pattern.setMailContainsBCC(0);
				} else {
					pattern.setMailContainsBCC(1);
				}

				if (matcherMail.group(7).equals("none")) {
					pattern.setMailContainsCC(0);
				} else {
					pattern.setMailContainsCC(1);
				}				
				pattern.setMailHasAttachment(Integer.parseInt(matcherMail.group(2)));
				pattern.setMailRecipientAllowed(1);
			}
			
		}
		
		return pattern;
		
	}
	
	/**
	 * Method buildInstancesFromPattern, in which data inside patterns_krs table is taken and then 
	 * transformed into Instances data type, so Weka can manage them.
	 *
	 * @param dbPatterns List with all rows in patterns_krs table.
	 * 
	 * 
	 * @return newData The ordered set of instances to use with Weka methods.
	 * 
	 */
	public Instances buildInstancesFromPatterns (List<PatternsKrs> dbPatterns) {
		
		Instances data = null;
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		List<String> decisionCauses = dbManager.getDistinctDecisionCauses();
		List<String> eventTypes = dbManager.getDistinctEventTypes();
		List<String> eventLevels = dbManager.getDistinctEventLevels();
		List<String> usernames = dbManager.getDistinctUsernames();
		List<String> userRoles = dbManager.getDistinctUserRoles();
		List<String> deviceTypes = dbManager.getDistinctDeviceType();
		List<String> deviceOSs = dbManager.getDistinctDeviceOS();
		List<String> deviceOwners = dbManager.getDistinctDeviceOwnedBy();
		List<String> appNames = dbManager.getDistinctAppName();
		List<String> appVendors = dbManager.getDistinctAppVendor();
		List<String> assetNames = dbManager.getDistinctAssetName();
		List<String> assetConfidentialLevels = dbManager.getDistinctAssetConfidentialLevel();
		List<String> assetLocations = dbManager.getDistinctAssetLocation();
		List<String> allLabels = dbManager.getDistinctLabels();
		atts.add(new Attribute("decision_cause", decisionCauses));
		atts.add(new Attribute("silent_mode"));
		atts.add(new Attribute("event_type", eventTypes));
		atts.add(new Attribute("event_level", eventLevels));
		atts.add(new Attribute("username", usernames));
		atts.add(new Attribute("password_length"));
		atts.add(new Attribute("letters_in_password"));
		atts.add(new Attribute("numbers_in_password"));
		atts.add(new Attribute("passwd_has_capital_letters"));
		atts.add(new Attribute("user_trust_value"));
		atts.add(new Attribute("activated_account"));
		atts.add(new Attribute("user_role", userRoles));
		atts.add(new Attribute("event_detection", "yyyy-MM-dd HH:mm:ss"));
		atts.add(new Attribute("device_type", deviceTypes));
		atts.add(new Attribute("device_OS", deviceOSs));
		atts.add(new Attribute("device_has_antivirus"));
		atts.add(new Attribute("device_has_certificate"));
		atts.add(new Attribute("device_trust_value"));
		atts.add(new Attribute("device_owned_by", deviceOwners));
		atts.add(new Attribute("device_has_password"));
		atts.add(new Attribute("device_screen_timeout"));
		atts.add(new Attribute("device_has_accessibility"));
		atts.add(new Attribute("device_is_rooted"));
		atts.add(new Attribute("app_name", appNames));
		atts.add(new Attribute("app_vendor", appVendors));
		atts.add(new Attribute("app_is_MUSES_aware"));
		atts.add(new Attribute("asset_name", assetNames));
		atts.add(new Attribute("asset_value"));
		atts.add(new Attribute("asset_confidential_level", assetConfidentialLevels));
		atts.add(new Attribute("asset_location", assetLocations));
		atts.add(new Attribute("mail_recipient_allowed"));
		atts.add(new Attribute("mail_contains_cc_allowed"));
		atts.add(new Attribute("mail_contains_bcc_allowed"));
		atts.add(new Attribute("mail_has_attachment"));
		atts.add(new Attribute("label", allLabels));
		data = new Instances("patternsData", atts, 0);

		Iterator<PatternsKrs> i = dbPatterns.iterator();
		while(i.hasNext()){
			PatternsKrs pattern = i.next();
			double[] vals = new double[data.numAttributes()];
			
			String eventType = pattern.getEventType();
			if (eventType.contentEquals("SECURITY_PROPERTY_CHANGED") || 
					eventType.contentEquals("ACTION_REMOTE_FILE_ACCESS") || 
					eventType.contentEquals("ACTION_APP_OPEN") || 
					eventType.contentEquals("ACTION_SEND_MAIL") || 
					eventType.contentEquals("SAVE_ASSET") || 
					eventType.contentEquals("VIRUS_FOUND") ) {
				
				String decisionCause = pattern.getDecisionCause();
				if (decisionCause == null) {
					vals[0] = Utils.missingValue();
				} else {
					vals[0] = decisionCauses.indexOf(decisionCause);
				}
				vals[1] = pattern.getSilentMode();			
				vals[2] = eventTypes.indexOf(eventType);
				String eventLevel = pattern.getEventLevel();
				if (eventLevel == null) {
					vals[3] = Utils.missingValue();
				} else {
					vals[3] = eventLevels.indexOf(eventLevel);
				}
				String username = pattern.getUsername();
				if (username == null) {
					vals[4] = Utils.missingValue();
				} else {
					vals[4] = usernames.indexOf(username);
				}			
				vals[5] = pattern.getPasswordLength();
				vals[6] = pattern.getLettersInPassword();
				vals[7] = pattern.getNumbersInPassword();
				vals[8] = pattern.getPasswdHasCapitalLetters();
				Double userTrust = pattern.getUserTrustValue();
				if (userTrust.isNaN()) {
					vals[9] = Utils.missingValue();
				} else {
					vals[9] = userTrust;
				}
				vals[10] = pattern.getActivatedAccount();
				String userRole = pattern.getUserRole();
				if (userRole == null) {
					vals[11] = Utils.missingValue();
				} else {
					vals[11] = userRoles.indexOf(userRole);
				}
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String strDate = sdf.format(pattern.getEventTime());
					vals[12] = data.attribute(12).parseDate(strDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String deviceModel = pattern.getDeviceType();
				if (deviceModel == null) {
					vals[13] = Utils.missingValue();
				} else {
					vals[13] = deviceTypes.indexOf(deviceModel);
				}
				String deviceOS = pattern.getDeviceOS();
				if (deviceOS == null) {
					vals[14] = Utils.missingValue();
				} else {
					vals[14] = deviceOSs.indexOf(deviceOS);
				}
				vals[15] = pattern.getDeviceHasAntivirus();
				vals[16] = pattern.getDeviceHasCertificate();
				Double deviceTrust = pattern.getDeviceTrustValue();
				if (deviceTrust.isNaN()) {
					vals[17] = Utils.missingValue();
				} else {
					vals[17] = deviceTrust;
				}
				String deviceOwner = pattern.getDeviceOwnedBy();
				if (deviceOwner == null) {
					vals[18] = Utils.missingValue();
				} else {
					vals[18] = deviceOwners.indexOf(deviceOwner);
				}
				vals[19] = pattern.getDeviceHasPassword();
				vals[20] = pattern.getDeviceScreenTimeout().doubleValue();
				vals[21] = pattern.getDeviceHasAccessibility();
				vals[22] = pattern.getDeviceIsRooted();
				String appName = pattern.getAppName();
				if (appName == null) {
					vals[23] = Utils.missingValue();
				} else {
					vals[23] = appNames.indexOf(appName);
				}
				String appVendor = pattern.getAppVendor();
				if (appVendor == null) {
					vals[24] = Utils.missingValue();
				} else {
					vals[24] = appVendors.indexOf(appVendor);
				}
				vals[25] = pattern.getAppMUSESAware();
				String assetName = pattern.getAssetName();
				if (assetName == null) {
					vals[26] = Utils.missingValue();
				} else {
					vals[26] = assetNames.indexOf(assetName);
				}
				vals[27] = pattern.getAssetValue();
				String assetConfidentialLevel = pattern.getAssetConfidentialLevel();
				if (assetConfidentialLevel == null) {
					vals[28] = Utils.missingValue();
				} else {
					vals[28] = assetConfidentialLevels.indexOf(assetConfidentialLevel);
				}
				String assetLocation = pattern.getAssetLocation();
				if (assetLocation == null) {
					vals[29] = Utils.missingValue();
				} else {
					vals[29] = assetLocations.indexOf(assetLocation);
				}
				vals[30] = pattern.getMailRecipientAllowed();
				vals[31] = pattern.getMailContainsCC();
				vals[32] = pattern.getMailContainsBCC();
				vals[33] = pattern.getMailHasAttachment();
				String label = pattern.getLabel();
				if (label == null) {
					vals[34] = Utils.missingValue();
				} else {
					vals[34] = allLabels.indexOf(label);
				}
				
				data.add(new DenseInstance(1.0, vals));
				
			}
		}
		
		/* As there will be missing data, is important to deal with it before continue working with the instances */
		ReplaceMissingValues replaceMissingValues = new ReplaceMissingValues();
		Instances newData = null;
		try {
			replaceMissingValues.setInputFormat(data);
			newData = Filter.useFilter(data, replaceMissingValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// OPTIONAL, only if we want the ARFF file
		/*ArffSaver saver = new ArffSaver();
		saver.setInstances(newData);
		try {
			saver.setFile(new File("./data/test.arff"));
			saver.setDestination(new File("./data/test.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		
		return newData;
	}
	
	
	/**
	 * Method featureSelection, which uses an algorithm to select the most representative features of
	 * the data in patterns_krs table
	 * 
	 * @param none
	 * 
	 * @return void
	 */
	
	public int[] featureSelection(Instances data){
		
		int[] indexes = null;
		AttributeSelection attsel = new AttributeSelection();
		//FuzzyRoughSubsetEval eval = new FuzzyRoughSubsetEval();
		//HillClimber search = new HillClimber();
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		attsel.setEvaluator(eval);
		attsel.setSearch(search);
		try {
			attsel.SelectAttributes(data);
			indexes = attsel.selectedAttributes();
			System.out.println(Utils.arrayToString(indexes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return indexes;
		
	}
	
	/**
	 * Method dataClassification in which first we erase the attributes that were not selected by
	 * featureSelection method and then performs classification over the rest of the data
	 * 
	 * @param data The original set of instances
	 * @param indexes The selected indexes by the feature selection algorithm
	 * 
	 * @return classifierIndex The classifier with higher percentage of correctly classified instances
	 */
	
	public int dataClassification(Instances data, int[] indexes){
		
		int classifierIndex = 0;
		Instances newData = data;
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(indexes);
		remove.setInvertSelection(true);
		try {
			remove.setInputFormat(data);
			newData = Filter.useFilter(data, remove);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double percentageCorrect = 0;
		
		/* (1) J48 */
		String[] optionsJ48 = new String[1];
		optionsJ48[0] = "-U";            // unpruned tree
		J48 treeJ48 = new J48();         // new instance of tree
		try {
			treeJ48.setOptions(optionsJ48);     // set the options
			treeJ48.buildClassifier(newData);   // build classifier
			
			Evaluation eval = new Evaluation(newData);
			eval.crossValidateModel(treeJ48, newData, 10, new Random(1));
			percentageCorrect = eval.pctCorrect();
			System.out.println("Percentage of correctly classified instances for J48 classifier: "+eval.pctCorrect());
			//System.out.println(tree.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* (2) JRip */
		String[] optionsJRip = new String[1];
		optionsJRip[0] = "-P";            // unpruned tree
		JRip treeJRip = new JRip();         // new instance of tree
		try {
			treeJRip.setOptions(optionsJRip);     // set the options
			treeJRip.buildClassifier(newData);   // build classifier
			
			Evaluation eval = new Evaluation(newData);
			eval.crossValidateModel(treeJRip, newData, 10, new Random(1));
			if (eval.pctCorrect() > percentageCorrect) {
				percentageCorrect = eval.pctCorrect();
				classifierIndex = 1;
			}
			System.out.println("Percentage of correctly classified instances for JRip classifier: "+eval.pctCorrect());
			//System.out.println(treeJRip.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* (3) PART */
		String[] optionsPART = new String[1];
		optionsPART[0] = "-U";            // unpruned tree
		PART treePART = new PART();         // new instance of tree
		try {
			treePART.setOptions(optionsPART);     // set the options
			treePART.buildClassifier(newData);   // build classifier
			
			Evaluation eval = new Evaluation(newData);
			eval.crossValidateModel(treePART, newData, 10, new Random(1));
			if (eval.pctCorrect() > percentageCorrect) {
				percentageCorrect = eval.pctCorrect();
				classifierIndex = 2;
			}
			System.out.println("Percentage of correctly classified instances for PART classifier: "+eval.pctCorrect());
			//System.out.println(treePART.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* (4) REPTree */
		String[] optionsREPTree = new String[1];
		optionsREPTree[0] = "-P";            // unpruned tree
		REPTree treeREPTree = new REPTree();         // new instance of tree
		try {
			treeREPTree.setOptions(optionsREPTree);     // set the options
			treeREPTree.buildClassifier(newData);   // build classifier
			
			Evaluation eval = new Evaluation(newData);
			eval.crossValidateModel(treeREPTree, newData, 10, new Random(1));
			if (eval.pctCorrect() > percentageCorrect) {
				percentageCorrect = eval.pctCorrect();
				classifierIndex = 3;
			}
			System.out.println("Percentage of correctly classified instances for REPTree classifier: "+eval.pctCorrect());
			//System.out.println(treeREPTree.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return classifierIndex;
		
	}
	
	/**
	 * Method classifierParser in which weka classifier rules are parsed for the extraction of their
	 * conditions and classes (label applied to patterns which have been classified by that rule)
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * @param classifierName 
	 * 
	 * @return void
	 */
	
	public List<String> classifierParser(String classifierRules, int classifierIndex){
		
		List<String> ruleList = new ArrayList<String>();
		String ruleJRip = "^\\(\\w+[\\s\\>\\=\\<]+[\\w\\.]+\\)";
		String rulePART1 = "^(\\w+)[\\s\\>\\=\\<]+\\w+\\sAND$";
		String rulePART2 = "^(\\w+)[\\s\\>\\=\\<]+\\w+\\:\\s\\w+\\s\\((\\d+)\\.\\d+\\/?(\\d*)\\.*\\d*\\)$";
		String ruleJ48 = "";
		String ruleREPTree = "";
		
		/* (0) J48
		 * (1) JRip
		 * (2) PART
		 * (3) REPTree
		 */
		switch(classifierIndex) {
		
		case 1:
			
		
		}
		
		
		//Pattern mailPattern = Pattern.compile(mailJSON);
		//Matcher matcherMail = mailPattern.matcher(event.getData());
		//if (matcherMail.find()
		
		return ruleList;		
		
	}

	
	/**
	 * Method ruleComparison in which existing security rules are compared with rules obtained by the
	 * classifier in dataClassification() method. It proposes new rules to the Knowledge Compiler.
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * @param securityRules Existing rules in the DB
	 * 
	 * @return void
	 */
	
	public void ruleComparison(List<String> classifierRules, List<SecurityRules> securityRules){
		
		
	}

}
