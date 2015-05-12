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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;

import eu.musesproject.server.continuousrealtimeeventprocessor.model.*;
import eu.musesproject.server.knowledgerefinementsystem.model.*;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.scheduler.ModuleType;

import org.apache.log4j.Logger;

import eu.musesproject.server.scheduler.ModuleType;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.RiskInformation;
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
	@SuppressWarnings("unused")
	private void minePatterns(SimpleEvents event){
		
		List<PatternsKrs> patternList = new ArrayList<PatternsKrs>();
		PatternsKrs pattern = new PatternsKrs();
		
		/* Obtaining decision (label of the pattern) by obtaining first the AccessRequest related to that event, and then the decision related to it */
		String eventID = event.getEventId();
		String label;
		List<AccessRequest> accessRequests = dbManager.findAccessRequestByEventId(eventID);
		if (accessRequests.size() > 0) {
			String decisionID = accessRequests.get(0).getDecisionId().toString();
			List<Decision> decisions = dbManager.findDecisionById(decisionID);
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
				pattern.setDecisionCause("unknown");
			}
		} else {
			pattern.setDecisionCause(null);
		}
		
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
