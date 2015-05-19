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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.contextdatareceiver.JSONManager;
import eu.musesproject.server.continuousrealtimeeventprocessor.model.*;
import eu.musesproject.server.knowledgerefinementsystem.model.*;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.scheduler.ModuleType;

import org.apache.log4j.Logger;

import eu.musesproject.server.scheduler.ModuleType;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Applications;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.DeviceType;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.RiskInformation;
import eu.musesproject.server.entity.Roles;
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
				
				logger.info(eventID);
				
				/* Previous event is the last event the user made */
				String user = event.getUser().getUserId();
				Date day = event.getDate();
				String time = event.getTime().toString();
				List<SimpleEvents> userLastEvents = dbManager.findEventsByUserId(user, day.toString(), time, Boolean.TRUE);
				if (userLastEvents.size() > 0) {
					BigInteger lastEvent = new BigInteger(userLastEvents.get(userLastEvents.size() - 1).getEventId());
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
					logEntry.setDecisionId(decisionID);
				}
				
				/* User behaviour as next event_id */
				List<SimpleEvents> userNextEvent = dbManager.findEventsByUserId(user, day.toString(), time, Boolean.FALSE);
				BigInteger nextEvent = new BigInteger(userNextEvent.get(0).getEventId());
				logEntry.setUserBehaviourId(nextEvent);
				
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
				List<RiskInformation> riskTreatment = dbManager.findRiskInformationByEventId(event.getEventId());
				if (riskTreatment.size() > 0){
					BigInteger treatment = new BigInteger(riskTreatment.get(0).getRiskInformationId());
					logEntry.setRiskTreatment(treatment.intValue());
				} else {
					//logger.warn("Risk Treatment not found, or this event did not cause a security violation, assigning 0...");
					logEntry.setRiskTreatment(0);					
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
			//List<Decision> decisions = dbManager.findDecisionById(decisionID);
			List<Decision> decisions = dbManager.findDecisionById("558");
			if (decisions.size() > 0) {
				label = decisions.get(0).getValue();
				pattern.setLabel(label);
			} else {
				//pattern.setLabel(null);
				/* Secret solution while not having data */
				if (dbManager.findSecurityViolationByEventId(eventID) != null){
					pattern.setLabel("STRONGDENY");
				} else {
					pattern.setLabel("GRANTED");
				}
			}
		} else {
			/* Secret solution while not having data */
			if (dbManager.findSecurityViolationByEventId(eventID) != null){
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
				pattern.setDecisionCause("");
			}
		} else {
			pattern.setDecisionCause("");
		}
				
		/* Finding the type of the event */
		EventType eventTypeId = event.getEventType();
		String eventType = eventTypeId.getEventTypeKey();
		if (eventType != null) {
			pattern.setEventType(eventType);
		} else {
			pattern.setEventType("");
		}
		
		/* Is the event a simple event or a complex event? */
		String eventLevel = eventTypeId.getEventLevel();
		if (eventLevel != null) {
			pattern.setEventLevel(eventLevel);
		} else {
			pattern.setEventLevel("");
		}
		
		/* Storing only the username */
		Users user = event.getUser();
		String username = user.getUsername();
		if (username != null) {
			pattern.setUsername(username);
		} else {
			pattern.setUsername("");
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
			pattern.setUserTrustValue(0);
			pattern.setDeviceTrustValue(0);
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
			pattern.setUserRole("");
		}
		
		/* Detection time of the event */
		Date eventDate = event.getDate();
		Time eventTime = event.getTime();
		Date eventDetection = new Date(eventDate.getYear(), eventDate.getMonth(), eventDate.getDate(), eventTime.getHours(), eventTime.getMinutes(), eventTime.getSeconds());
		if (eventDetection.toString() != null) {
			pattern.setEventTime(eventDetection);
		} else {
			pattern.setEventTime(new Date());
		}
		
		/* Was MUSES in silent or verbose mode? */
		if (eventDate.getDay() < 16 && eventDate.getMonth() <= 3) {
			pattern.setSilentMode(1);
		} else {
			pattern.setSilentMode(0);
		}
		
		/* Obtaining the type of device */
		Devices userDeviceId = event.getDevice();
		DeviceType deviceType = userDeviceId.getDeviceType();
		String userDeviceType = deviceType.getType();
		if (userDeviceType != null) {
			pattern.setDeviceType(userDeviceType);
		} else {
			pattern.setDeviceType("");
		}
		
		/* Device characteristics */
		// OS
		String userDeviceOS = userDeviceId.getOS_name().concat(userDeviceId.getOS_version());
		if (userDeviceOS != null) {
			pattern.setDeviceOS(userDeviceOS);
		} else {
			pattern.setDeviceOS("");
		}
		// Certificate of device
		byte[] deviceCertificate = userDeviceId.getCertificate();
		if (deviceCertificate.length > 0) {
			pattern.setDeviceHasCertificate(1);
		} else {
			pattern.setDeviceHasCertificate(0);
		}		
		// Device security level
		short deviceSecLevel = userDeviceId.getSecurityLevel();
		pattern.setDeviceSecurityLevel(deviceSecLevel);
		// Device company owned or employee owned
		String deviceOwner = userDeviceId.getOwnerType();
		if (deviceOwner != null) {
			pattern.setDeviceOwnedBy(deviceOwner);
		} else {
			pattern.setDeviceOwnedBy("EMPLOYEE");
		}
		
		/* Characteristics of the application which the user was using at the time of the event */
		Applications eventApp = event.getApplication();
		// App name and version
		String appName = eventApp.getName().concat(eventApp.getVersion());
		if (appName != null) {
			pattern.setAppName(appName);
		} else {
			pattern.setAppName("");
		}
		// Vendor of the app
		String appVendor = eventApp.getVendor();
		if (appVendor != null) {
			pattern.setAppVendor(appVendor);
		} else {
			pattern.setAppVendor("");
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
			pattern.setAssetName("");
		}
		// Asset value
		double assetValue = eventAsset.getValue();
		pattern.setAssetValue(assetValue);
		// Confidentiality level of the asset
		String assetConfidentialLevel = eventAsset.getConfidentialLevel();
		if (assetConfidentialLevel != null) {
			pattern.setAssetConfidentialLevel(assetConfidentialLevel);
		} else {
			pattern.setAssetConfidentialLevel(assetConfidentialLevel);
		}
		// Where is the asset located
		String assetLocation = eventAsset.getLocation();
		if (assetLocation != null) {
			pattern.setAssetLocation(assetLocation);
		} else {
			pattern.setAssetLocation("");
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
	 * Info DM
	 * 
	 *    The Data Miner needs a list of clue patterns, in other words, already expected patterns (currently supported by Muses
	 *    Security Rules) that are prone to be adapted to other related events (or slight modifications of current events) that
	 *    might be happening at the same time.
	 * 
	 * 
	 * @param patterns
	 * 
	 * 
	 * @return void
	 */
	
	public void updateCluePatterns(Pattern[] patterns){
		
	}

}
