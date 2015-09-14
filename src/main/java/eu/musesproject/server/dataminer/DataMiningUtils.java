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

import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Applications;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.Roles;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.scheduler.ModuleType;

import org.apache.log4j.Logger;

/**
 * The Class DataMiningUtils.
 * 
 * @author Paloma de las Cuevas (UGR)
 * @version Aug 30, 2015
 */
public class DataMiningUtils {
	
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(DataMiner.class);
	
	/**
	  * obtainLabel - For every event, an access request is built by the Event Processor, so the RT2AE can make a final decision about it.
	  * 			  This method obtains the associated label to an event.
	  *
	  * @param accessRequestId Is the access request number associated to an event.
	  * 
	  * @return label
	  * 
	  */
	public String obtainLabel(String accessRequestId){
		
		List<Decision> decisions = dbManager.findDecisionByAccessRequestId(accessRequestId);
		if (decisions.size() > 0) {
			return decisions.get(0).getValue();
		} else {
			return "ALLOW";
		}
		
	}
	
	/**
	  * obtainDecisionCause - For every event, it may be several security violations, and each one contains the cause of the violation.
	  * 					  This method returns the decision cause for an event, either if the security violation is linked to a decision id
	  * 					  or not.
	  *
	  * @param accessRequestId	Is the access request number associated to an event.
	  * @param eventId			The event Id
	  * 
	  * @return decisionCause
	  * 
	  */
	public String obtainDecisionCause(String accessRequestId, String eventId){
		
		List<Decision> decisions = dbManager.findDecisionByAccessRequestId(accessRequestId);
		if (decisions.size() > 0) {
			String decisionId = decisions.get(0).getValue();
			SecurityViolation securityViolation = dbManager.findSecurityViolationByDecisionId(decisionId);
			Pattern p = Pattern.compile("<(.+?)>(.+?)</(.+?)>");
			if (securityViolation != null) {
				Matcher matcher = p.matcher(securityViolation.getConditionText());
				if (matcher.find()) {
					return matcher.group(1);
				} else {
					return null;
				}
			} else {
				List<SecurityViolation> secViolations = dbManager.findSecurityViolationByEventId(eventId);
				if (secViolations.size() > 0) {
					return null;
				} else {
					return "ALLOW";
				}
			}			
		} else {
			return null;						
		}		 
		
	}
	
	/**
	  * obtainEventType - For every event, this method obtains its type.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return eventType
	  * 
	  */
	public String obtainEventType(SimpleEvents event){
		
		EventType eventTypeId = event.getEventType();
		String eventType = null;
		eventType = eventTypeId.getEventTypeKey();
		return eventType;
		
	}
	
	/**
	  * obtainEventLevel - For every event, this method obtains its level (simple or complex).
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return eventLevel
	  * 
	  */
	public String obtainEventLevel(SimpleEvents event){
		
		EventType eventTypeId = event.getEventType();
		String eventLevel = null;
		eventLevel = eventTypeId.getEventLevel();
		return eventLevel;
		
	}	
	
	/**
	  * obtainUsername - For every event, this method obtains who is responsible for the event, but maintaining the anonymity.
	  *
	  * @param user The user as a User object.
	  * 
	  * @return username
	  * 
	  */
	public String obtainUsername(Users user){
		
		String username = null;
		username = user.getUsername();
		return username;
		
	}
	
	/**
	  * passwdLength - This method measures the length of the user password.
	  *
	  * @param user The user as a User object.
	  * 
	  * @return passwordLength
	  * 
	  */
	public int passwdLength(Users user){
		
		int passwordLength = 0;
		String userPassword = user.getPassword();
		// Characters (numbers, letters, and symbols) in the password
		if (userPassword != null) {
			passwordLength = 0;userPassword.length();
		}
		return passwordLength;
	}
	
	/**
	  * passwdDigits - This method measures the number of digits (0-9) in the user password.
	  *
	  * @param user The user as a User object.
	  * 
	  * @return digitsCount
	  * 
	  */
	public int passwdDigits(Users user){
		
		int digitsCount = 0;
		String userPassword = user.getPassword();
		String digits = "\\d";
		Pattern digitPattern = Pattern.compile(digits);
		Matcher digitsMatcher = digitPattern.matcher(userPassword);		
		while (digitsMatcher.find()) {
			digitsCount++;
		}
		return digitsCount;
	}
	
	/**
	  * passwdLetters - This method measures the number of letters (a-z) in the user password.
	  *
	  * @param user The user as a User object.
	  * 
	  * @return lettersCount
	  * 
	  */
	public int passwdLetters(Users user){
		
		int lettersCount = 0;
		String userPassword = user.getPassword();
		String letters = "[a-zA-Z]";
		Pattern letterPattern = Pattern.compile(letters);
		Matcher lettersMatcher = letterPattern.matcher(userPassword);		
		while (lettersMatcher.find()) {
			lettersCount++;
		}
		return lettersCount;
	}
	
	/**
	  * passwdCapLetters - This method measures the number of capital letters (A-Z) in the user password.
	  *
	  * @param user The user as a User object.
	  * 
	  * @return capLettersCount
	  * 
	  */
	public int passwdCapLetters(Users user){
		
		int capLettersCount = 0;
		String userPassword = user.getPassword();
		String capLetters = "[A-Z]";
		Pattern capLetterPattern = Pattern.compile(capLetters);
		Matcher capLettersMatcher = capLetterPattern.matcher(userPassword);		
		while (capLettersMatcher.find()) {
			capLettersCount++;
		}
		return capLettersCount;
	}
	
	/**
	  * obtainingUserTrust - This method gathers the user trust value associated to the user, at the time of the decision.
	  *
	  * @param accessRequestId Is the access request number associated to an event.
	  * 
	  * @return trustValue
	  * 
	  */
	public double obtainingUserTrust(String accessRequestId){
		
		double trustValue = Double.NaN;
		List<Decision> decisions = dbManager.findDecisionByAccessRequestId(accessRequestId);
		if (decisions.size() > 0) {
			String decisionId = decisions.get(0).getValue();
			List<DecisionTrustvalues> trustValues = dbManager.findDecisionTrustValuesByDecisionId(decisionId);
			if (trustValues.size() > 0) {
				trustValue = trustValues.get(0).getUsertrustvalue();			
			}
		}
		return trustValue;
	}
	
	/**
	  * obtainingDeviceTrust - This method gathers the user trust value associated to the device, at the time of the decision.
	  *
	  * @param accessRequestId Is the access request number associated to an event.
	  * 
	  * @return trustValue
	  * 
	  */
	public double obtainingDeviceTrust(String accessRequestId){
		
		double trustValue = Double.NaN;
		List<Decision> decisions = dbManager.findDecisionByAccessRequestId(accessRequestId);
		if (decisions.size() > 0) {
			String decisionId = decisions.get(0).getValue();
			List<DecisionTrustvalues> trustValues = dbManager.findDecisionTrustValuesByDecisionId(decisionId);
			if (trustValues.size() > 0) {
				trustValue = trustValues.get(0).getDevicetrustvalue();			
			}
		}
		return trustValue;
	}	
	
	/**
	  * obtainUserRole - This method obtains the role of the user who made the event.
	  *
	  * @param user The user as a User object.
	  * 
	  * @return role
	  * 
	  */
	public String obtainUserRole(Users user){
		
		int userRoleId = user.getRoleId();
		Roles userRole = dbManager.getRoleById(userRoleId);
		String userRoleName = null;
		if (userRole != null) {
			userRoleName = userRole.getName();
		}
		return userRoleName;
	}	
	
	/**
	  * obtainTimestamp - This method obtains the detection time of the event.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return eventDetection
	  * 
	  */
	public Date obtainTimestamp(SimpleEvents event){
		
		Date eventDate = event.getDate();
		Time eventTime = event.getTime();
		Date eventDetection = new Date(eventDate.getYear(), eventDate.getMonth(), eventDate.getDate(), eventTime.getHours(), eventTime.getMinutes(), eventTime.getSeconds());
		return eventDetection;
	}	
	
	/**
	  * silentModeTrials1 - This method checks if the device was in silent mode or verbose mode during first trials. It returns 1 if it was
	  * 					in silent mode, and 0 if it was in verbose mode.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return silentMode
	  * 
	  */
	public int silentModeTrials1(SimpleEvents event){
		
		Date eventDate = event.getDate();
		Time eventTime = event.getTime();
		Date eventDetection = new Date(eventDate.getYear(), eventDate.getMonth(), eventDate.getDate(), eventTime.getHours(), eventTime.getMinutes(), eventTime.getSeconds());
		if (eventDetection.getDay() <= 2 && eventDetection.getMonth() <= 7) {
			return 1;
		} else {
			return 0;
		}
	}	
	
	/**
	  * silentModeTrials2 - This method checks if the device was in silent mode or verbose mode during second trials. It returns 1 if it was
	  * 					in silent mode, and 0 if it was in verbose mode.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return silentMode
	  * 
	  */
	public int silentModeTrials2(SimpleEvents event){
		
		Date eventDate = event.getDate();
		Time eventTime = event.getTime();
		Date eventDetection = new Date(eventDate.getYear(), eventDate.getMonth(), eventDate.getDate(), eventTime.getHours(), eventTime.getMinutes(), eventTime.getSeconds());
		if (eventDetection.getDay() <= 2 && eventDetection.getMonth() <= 7) {
			return 1;
		} else {
			return 0;
		}
	}	
	
	/**
	  * obtainDeviceModel - This method obtains the device model.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return deviceModel
	  * 
	  */
	public String obtainDeviceModel(SimpleEvents event){
		
		Devices userDeviceId = event.getDevice();
		String deviceModel = null;
		if (userDeviceId != null) {
			deviceModel = userDeviceId.getDeviceModel();
		}
		return deviceModel;
	}	
	
	/**
	  * obtainDeviceOS - This method obtains the OS of the device. It returns a string made from the name of the OS and the version.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return deviceOS
	  * 
	  */
	public String obtainDeviceOS(SimpleEvents event){
		
		Devices userDeviceId = event.getDevice();
		String deviceOS = null;
		if (userDeviceId.getOS_name()!=null && userDeviceId.getOS_version() != null){
			deviceOS = userDeviceId.getOS_name().concat(userDeviceId.getOS_version());
		}
		return deviceOS;
	}	
	
	/**
	  * obtainDeviceCertificate - This method for the certificate of the device. It returns 1 if it finds a certificate in the DB, and 0 if
	  * 						  it does not.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return validCertificate
	  * 
	  */
	public int obtainDeviceCertificate(SimpleEvents event){
		
		Devices userDeviceId = event.getDevice();
		byte[] deviceCertificate = userDeviceId.getCertificate();
		if ((deviceCertificate != null) && (deviceCertificate.length > 0)) {
			return 1;
		} else {
			return 0;
		}
	}	
	
	/**
	  * obtainDeviceOwner - This method looks for the certificate of the device. It returns 1 if it finds a certificate in the DB, and 0 if
	  * 					it does not.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return deviceOwner
	  * 
	  */
	public String obtainDeviceOwner(SimpleEvents event){
		
		Devices userDeviceId = event.getDevice();
		String deviceOwner = null;
		if (userDeviceId != null) {
			deviceOwner = userDeviceId.getOwnerType();
		}
		return deviceOwner;
	}	
	
	/**
	  * obtainAppName - This method gathers characteristics of the application which the user was using at the time of the event.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return appName
	  * 
	  */
	public String obtainAppName(SimpleEvents event){
		
		Applications eventApp = event.getApplication();
		String appName = null;
		if (eventApp.getName() != null && eventApp.getVersion() != null) {
			appName = eventApp.getName().concat(eventApp.getVersion());
		}
		return appName;
	}	
	
	/**
	  * obtainAppVendor - This method gathers characteristics of the application which the user was using at the time of the event.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return appVendor
	  * 
	  */
	public String obtainAppVendor(SimpleEvents event){
		
		Applications eventApp = event.getApplication();
		String appVendor = null;
		appVendor = eventApp.getVendor();
		return appVendor;
	}	
	
	/**
	  * obtainMusesAwareness - This method checks if the application is MUSES aware or not.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return appMusesAware
	  * 
	  */
	public int obtainMusesAwareness(SimpleEvents event){
		
		Applications eventApp = event.getApplication();
		int appMusesAware = 0;
		if (eventApp != null) {
			appMusesAware = eventApp.getIs_MUSES_aware();
		}
		return appMusesAware;
	}	
	
	/**
	  * obtainAssetName - This method gathers characteristics of the asset that the event is trying to access to.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return assetName
	  * 
	  */
	public String obtainAssetName(SimpleEvents event){
		
		Assets eventAsset = event.getAsset();
		String assetName = null;
		if (eventAsset != null) {
			assetName = eventAsset.getTitle();
		}
		return assetName;
	}	
	
	/**
	  * obtainAssetValue - This method gathers characteristics of the asset that the event is trying to access to.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return assetValue
	  * 
	  */
	public double obtainAssetValue(SimpleEvents event){
		
		Assets eventAsset = event.getAsset();
		double assetValue = Double.NaN;
		if (eventAsset != null) {
			assetValue = eventAsset.getValue();
		}
		return assetValue;
	}	
	
	/**
	  * obtainAssetConfidentiality - This method gathers characteristics of the asset that the event is trying to access to.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return assetConf
	  * 
	  */
	public String obtainAssetConfidentiality(SimpleEvents event){
		
		Assets eventAsset = event.getAsset();
		String assetConf = null;
		if (eventAsset != null) {
			assetConf = eventAsset.getConfidentialLevel();
		}
		return assetConf;
	}	
	
	/**
	  * obtainAssetLocation - This method gathers characteristics of the asset that the event is trying to access to.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return assetLocation
	  * 
	  */
	public String obtainAssetLocation(SimpleEvents event){
		
		Assets eventAsset = event.getAsset();
		String assetLocation = null;
		if (eventAsset != null) {
			assetLocation = eventAsset.getLocation();
		}
		return assetLocation;
	}	
	
	/**
	  * readConfigurationJSON - This method extracts information about the last known configuration of the device.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return List<Integer> configValues
	  * 
	  */
	public List<Integer> readConfigurationJSON(SimpleEvents event){
		
		List<Integer> configValues = new ArrayList<Integer>();
		Devices userDeviceId = event.getDevice();
		Date eventDate = event.getDate();
		SimpleEvents configEvent = dbManager.findDeviceConfigurationBySimpleEvent(Integer.parseInt(userDeviceId.getDeviceId()), eventDate.toString());
		String configData = configEvent.getData();
		// configData format is like:
		// {event=security_property_changed, properties={"id":"1",
		// "ispasswordprotected":"true","isrootpermissiongiven":"false",
		// "screentimeoutinseconds":"300","musesdatabaseexists":"true",
		// "isrooted":"false","accessibilityenabled":"false",
		// "istrustedantivirusinstalled":"false","ipaddress":"172.17.1.52"}}
		String configFormat = "\\\"?(\\w+)\\\"?[\\:\\=]\\\"?(\\w+)\\\"?";
		Pattern configPattern = Pattern.compile(configFormat);
		Matcher configMatcher = configPattern.matcher(configData);
		while (configMatcher.find()) {
			if (configMatcher.group(1).equalsIgnoreCase("ispasswordprotected")) {
				if (configMatcher.group(2).equalsIgnoreCase("true")) {
					configValues.add(1);
				} else {
					configValues.add(0);
				}
			} else if (configMatcher.group(1).equalsIgnoreCase("screentimeoutinseconds")) {
				BigInteger time = BigInteger.valueOf(Integer.parseInt(configMatcher.group(2)));
				configValues.add(Integer.parseInt(configMatcher.group(2)));
			} else if (configMatcher.group(1).equalsIgnoreCase("isscreanlocked")) {
				configValues.set(configValues.size()-1, 0);
			} else if (configMatcher.group(1).equalsIgnoreCase("isrooted")) {
				if (configMatcher.group(2).equalsIgnoreCase("true")) {
					configValues.add(1);;
				} else {
					configValues.add(0);
				}
			} else if (configMatcher.group(1).equalsIgnoreCase("accessibilityenabled")) {
				if (configMatcher.group(2).equalsIgnoreCase("true")) {
					configValues.add(1);
				} else {
					configValues.add(0);
				}
			} else if (configMatcher.group(1).equalsIgnoreCase("istrustedantivirusinstalled")) {
				if (configMatcher.group(2).equalsIgnoreCase("true")) {
					configValues.add(1);
				} else {
					configValues.add(0);
				}
			} 			
			
		}		
		
		return configValues;
	}	
	
	/**
	  * readMailJSON - This method extracts information about the characteristics of a mail being sent.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return List<Integer> mailValues
	  * 
	  */
	public List<Integer> readMailJSON(SimpleEvents event){
		
		List<Integer> mailValues = new ArrayList<Integer>();
		EventType eventTypeId = event.getEventType();
		/* Data in event_type_if = 11
		 * {event=ACTION_SEND_MAIL, properties={"to":"the.reiceiver@generic.com,
		 * another.direct.receiver@generic.com","noAttachments":"1",
		 * "subject":"MUSES sensor status subject","bcc":"hidden.reiceiver@generic.com",
		 * "from":"max.mustermann@generic.com","attachmentInfo":"pdf",
		 * "cc":"other.listener@generic.com, 2other.listener@generic.com"}}
		*/		
		if ((eventTypeId!=null)&&(eventTypeId.getEventTypeId() == 11)) {
			String mailJSON = "\\\"(\\w+)\\\"\\:\\\"(.*)\\\"[\\,\\}]";
			String mailFormat = "[\\w\\.\\_]+\\@([\\w\\.\\_]+)";
			Pattern mailPattern = Pattern.compile(mailJSON);
			Pattern mailFormatPattern = Pattern.compile(mailFormat);
			Matcher matcherMail = mailPattern.matcher(event.getData());
			if (matcherMail.find()) {
				Matcher matcherMailFormat = mailFormatPattern.matcher(matcherMail.group(2));
				if (matcherMail.group(1).equalsIgnoreCase("bcc")) {
					while (matcherMailFormat.find()) {
						if (this.isRecipientAllowed(matcherMailFormat.group(2))) {
							mailValues.add(1);
						} else {
							mailValues.add(0);
						}
					}
				} else if (matcherMail.group(1).equalsIgnoreCase("cc")) {
					while (matcherMailFormat.find()) {
						if (this.isRecipientAllowed(matcherMailFormat.group(2))) {
							mailValues.add(1);
						} else {
							mailValues.add(0);
						}
					}
				} else if (matcherMail.group(1).equalsIgnoreCase("to")) {
					while (matcherMailFormat.find()) {
						if (this.isRecipientAllowed(matcherMailFormat.group(2))) {
							mailValues.add(1);
						} else {
							mailValues.add(0);
						}
					}
				} else if (matcherMail.group(1).equalsIgnoreCase("noAttachments")) {
					mailValues.add(Integer.parseInt(matcherMail.group(2)));
				}
			}
			
		}		
		
		return mailValues;
	}
	
	/**
	 * Method isRecipientAllowed, which checks if the mail address server is allowed by the company.
	 *
	 * @param server Server of the mail address like in name@server.com.
	 * 
	 * 
	 * @return boolean True if it is allowed, false if not.
	 * 
	 */
	public boolean isRecipientAllowed(String server) {
		
		if (server.equalsIgnoreCase("generic.com")) {
			return true;
		} else {		
			return false;
		}
	}	
	
	/**
	  * readAssetJSON - This method extracts information about the characteristics of a mail being sent.
	  *
	  * @param event The event as a Simple Events object.
	  * 
	  * @return List<String> wifiValues
	  * 
	  */
	public List<String> readAssetJSON(SimpleEvents event){
		
		List<String> wifiValues = new ArrayList<String>();
		EventType eventTypeId = event.getEventType();
		/* {id=3, wifiencryption=[WPA2-PSK-TKIP+CCMP][ESS], bssid=24:a4:3c:04:ae:09, 
		 * bluetoothconnected=FALSE, wifienabled=true, wifineighbors=6, hiddenssid=false, 
		 * networkid=1, wificonnected=true, airplanemode=false}
		 */
		if ((eventTypeId!=null)&&(eventTypeId.getEventTypeId() == 8)) {
			String wifiJSON = "(\\w+)\\=([\\w\\[\\]\\-\\+\\:\\d]+)";
			Pattern wifiPattern = Pattern.compile(wifiJSON);
			Matcher matcherWifi = wifiPattern.matcher(event.getData());
			if (matcherWifi.find()) {
				if(matcherWifi.group(1).equalsIgnoreCase("wifiencryption")) {
					logger.info(matcherWifi.group(2));
					wifiValues.add(matcherWifi.group(2));
				} else if (matcherWifi.group(1).equalsIgnoreCase("bluetoothconnected")) {
					if(matcherWifi.group(2).equalsIgnoreCase("true")) {
						wifiValues.add("1");
					} else {
						wifiValues.add("0");
					}
				} else if (matcherWifi.group(1).equalsIgnoreCase("wifienabled")) {
					if(matcherWifi.group(3).contentEquals("true")) {
						wifiValues.add("1");
					} else {
						wifiValues.add("0");
					}
				} else if (matcherWifi.group(1).equalsIgnoreCase("wificonnected")) {
					if(matcherWifi.group(6).contentEquals("true")) {
						wifiValues.add("1");
					} else {
						wifiValues.add("0");
					}
				}
			}
			
		}		
		
		return wifiValues;
	}


}
