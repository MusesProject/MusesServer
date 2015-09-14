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

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.PatternsKrs;
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


}
