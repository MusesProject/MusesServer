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
import java.util.Enumeration;
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

import weka.associations.Apriori;
import weka.attributeSelection.AttributeSelection;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
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
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.util.Constants;

/**
 * The Class DataMiner.
 *
 * @author Sergio Zamarripa (S2) & Paloma de las Cuevas (UGR)
 * @version Sep 30, 2013
 */
public class DataMiner {
   
    private static DBManager dbManager = new DBManager(ModuleType.KRS);
    private static final String MUSES_TAG = "MUSES_TAG";
    private static ParsingUtils parser = new ParsingUtils();
    private static DataMiningUtils dmUtils = new DataMiningUtils();
    private Logger logger = Logger.getLogger(DataMiner.class);
   
    /**
     * Method ruleComparison in which existing security rules are compared with rules obtained by the
     * classifier in dataClassification() method. It proposes new rules to the Knowledge Compiler.
     * This is the main output of the Data Miner.
     *
     *
     * @return void
     */
   
    public void ruleComparison(){
       
        List<PatternsKrs> patternList = dbManager.getPatternsKRS();
        List<String> classifierRules = null;
        List<String> droolsRules = null;
        List<SecurityRules> alreadyDraftRules = dbManager.getSecurityRulesByStatus(Constants.DRAFT);
        if (patternList.size()>0){
            logger.info("Initialising Data Miner rule generation...");
            Instances data = this.buildInstancesFromPatterns(patternList);
            if (data != null) {
                int[] indexes = new int[data.numAttributes()];
                int[] selectedIndexes = this.featureSelection(data);
                for (int i = 0; i < data.numAttributes(); i++) {
                    indexes[i] = i;
                }
                int[] indexesReview = new int[data.numAttributes()];
                indexesReview[0] = 11;
                indexesReview[1] = 13;
                indexesReview[2] = 14;
                indexesReview[3] = 18;
                indexesReview[4] = 38;
                logger.info("Classifying...");
                String notParsedClassifierRules = this.dataClassification(data, indexesReview);
                String[] ruleLines = notParsedClassifierRules.split("\\n+");
                logger.info("Obtaining rules from association algorithm...");
                String associationRules = this.associationRules(data, indexesReview);
                logger.info("Obtaining rules from best classifier...");
                if (ruleLines[0].contains("JRIP")) {
                    classifierRules = parser.JRipParser(notParsedClassifierRules);
                } else if (ruleLines[0].contains("PART")) {
                    classifierRules = parser.PARTParser(notParsedClassifierRules);
                } else if (ruleLines[0].contains("J48")) {
                    classifierRules = parser.J48Parser(notParsedClassifierRules);
                } else if (ruleLines[0].contains("REPTree")) {
                    classifierRules = parser.REPTreeParser(notParsedClassifierRules);
                }
                logger.info("Obtaining rules from DB...");
                droolsRules = parser.DBRulesParser();
                logger.info("Comparing...");
                if (classifierRules != null && droolsRules != null) {
                    Iterator<String> i1 = droolsRules.iterator();
                    Iterator<String> i2 = classifierRules.iterator();
                    boolean same = false;
                    while (i1.hasNext()) {
                        String dbRule = i1.next();
                        //logger.info("DB rule-----"+dbRule);
                        while (i2.hasNext()) {
                            String proposedRule = i2.next();
                            //logger.info(proposedRule);
                            same = parser.isAlike(dbRule, proposedRule);
                            if (!same && alreadyDraftRules.size() > 0) {
                                Iterator<SecurityRules> i3 = alreadyDraftRules.iterator();
                                while (i3.hasNext()) {
                                    SecurityRules draftRule = i3.next();
                                    String ruleString = draftRule.getDescription();
                                    //logger.info(ruleString);
                                    same = parser.isAlike(proposedRule, ruleString);
                                    if (same) {
                                    	//logger.info(ruleString+" VS. "+proposedRule+" ARE THE SAME? ->"+same);
                                    	break;
                                    }
                                }
                            }
                            if (!same) {
                                SecurityRules finalRule = new SecurityRules();
                                finalRule.setDescription(proposedRule);
                                finalRule.setStatus(Constants.DRAFT);
                                finalRule.setModification(new Date());
                                finalRule.setName("Proposed Rule by Data Miner");
                                byte[] refined = new byte[1];
                                refined[0] = 0;
                                finalRule.setRefined(refined);
                                dbManager.setSecurityRule(finalRule);
                            }
                        }
                    }
                }
               
                if (indexes.length > 0) {
                	logger.info("=== Results after feature selection ===");
                    this.dataClassification(data, selectedIndexes);
                } else {
                    logger.error("Feature selection not being properly performed");
                }
            } else {
                logger.error("Instances not being properly built.");
            }
                       
        } else {
            logger.error("There are no patterns in the table.");
        }
       
       
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
                BigInteger eventID = null;
                String user = null;
                if ((event != null)&&(event.getEventId() != null)){
                    eventID = new BigInteger(event.getEventId());
                    user = event.getUser().getUserId();
                }   
                logEntry.setCurrentEventId(eventID);
               
                /* Previous event is the last event the user made */                 
                Date day = null;
                String time = null;
                if ((event != null) && (event.getDate() != null)) {
                    day = event.getDate();
                    time = event.getTime().toString();
                    SimpleEvents userLastEvent = dbManager.findEventsByUserId(user, day.toString(), time, Boolean.TRUE);
                    if (userLastEvent != null) {
                        BigInteger lastEvent = new BigInteger(userLastEvent.getEventId());
                        logEntry.setPreviousEventId(lastEvent);
                    } else {
                        //logger.warn("No previous events by this user, assigning 0...");
                        logEntry.setPreviousEventId(BigInteger.ZERO);
                    }
                } else {
                    logEntry.setPreviousEventId(BigInteger.ZERO);
                }
               
               
                /* Looking for decision_id in table access_request */
                BigInteger decisionID = BigInteger.ZERO;
   
                if (eventID == null){
                    eventID = BigInteger.valueOf(0);// Control added by S2
                }
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
                Devices device = event.getDevice();               
                logEntry.setDeviceSecurityState(BigInteger.valueOf((long) device.getTrustValue()));
               
                /* Looking for the risk treatment in case the event caused a security violation */
                String riskTreatment = null;
                if (securityViolations.size()>0)
                    riskTreatment = securityViolations.get(0).getMessage();
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
      * @param event The simple event over which the data mining is going to be performed
      *
      * @return pattern The built pattern to be stored in the database
      *
      */
    @SuppressWarnings("null")
    public List<PatternsKrs> minePatterns(SimpleEvents event){
       
    	List<PatternsKrs> patternList = new ArrayList<PatternsKrs>();
        logger.info(event.getEventId());
       
        /* Important variables and objects for the DM process, and common to several methods */
        String eventID = event.getEventId();
        Users user = event.getUser();
       
        /* 1 event -> * decisions/access requests */   
        /* Then, for each event, the data mining process must be launched once per access request */
        List<AccessRequest> accessRequests = dbManager.findAccessRequestByEventId(eventID);
        int i = 0;
        do {
        	PatternsKrs pattern = new PatternsKrs();
            if (accessRequests.size() > 0) {
                String accessRequestId = accessRequests.get(i).getAccessRequestId();           
                pattern.setLabel(dmUtils.obtainLabel(accessRequestId));
                pattern.setDecisionCause(dmUtils.obtainDecisionCause(accessRequestId, eventID));
                Double d = dmUtils.obtainingUserTrust(accessRequestId);
                if (!d.isNaN()) {
                    pattern.setUserTrustValue(dmUtils.obtainingUserTrust(accessRequestId));
                }
                d = dmUtils.obtainingDeviceTrust(accessRequestId);
                if (!d.isNaN()) {
                    pattern.setDeviceTrustValue(dmUtils.obtainingDeviceTrust(accessRequestId));
                }
            } else {
                pattern.setLabel(dmUtils.obtainLabel("0"));
                pattern.setDecisionCause(dmUtils.obtainDecisionCause("0", eventID));
                Double d = dmUtils.obtainingUserTrust("0");
                if (!d.isNaN()) {
                    pattern.setUserTrustValue(dmUtils.obtainingUserTrust("0"));
                }
                d = dmUtils.obtainingDeviceTrust("0");
                if (!d.isNaN()) {
                    pattern.setDeviceTrustValue(dmUtils.obtainingDeviceTrust("0"));
                }
            }
            pattern.setEventType(dmUtils.obtainEventType(event));
            pattern.setEventLevel(dmUtils.obtainEventLevel(event));
            pattern.setUsername(dmUtils.obtainUsername(user));
            pattern.setPasswordLength(dmUtils.passwdLength(user));
            pattern.setNumbersInPassword(dmUtils.passwdDigits(user));
            pattern.setLettersInPassword(dmUtils.passwdLetters(user));
            pattern.setPasswdHasCapitalLetters(dmUtils.passwdCapLetters(user));
            pattern.setActivatedAccount(user.getEnabled());
            pattern.setUserRole(dmUtils.obtainUserRole(user));
            pattern.setEventTime(dmUtils.obtainTimestamp(event));
            pattern.setSilentMode(dmUtils.silentModeTrials1(event));
            pattern.setSilentMode(dmUtils.silentModeTrials2(event));
            pattern.setDeviceType(dmUtils.obtainDeviceModel(event));
            pattern.setDeviceOS(dmUtils.obtainDeviceOS(event));
            pattern.setDeviceOwnedBy(dmUtils.obtainDeviceOwner(event));
            pattern.setDeviceHasCertificate(dmUtils.obtainDeviceCertificate(event));
            pattern.setAppName(dmUtils.obtainAppName(event));
            pattern.setAppVendor(dmUtils.obtainAppVendor(event));
            pattern.setAppMUSESAware(dmUtils.obtainMusesAwareness(event));
            pattern.setAssetName(dmUtils.obtainAssetName(event));
            Double d = dmUtils.obtainAssetValue(event);
            if (!d.isNaN()) {
                pattern.setAssetValue(dmUtils.obtainAssetValue(event));
            }
            pattern.setAssetConfidentialLevel(dmUtils.obtainAssetConfidentiality(event));
            pattern.setAssetLocation(dmUtils.obtainAssetLocation(event));
            List<Integer> configList = dmUtils.readConfigurationJSON(event);
            if (configList.size() >= 5) {
                pattern.setDeviceHasPassword(configList.get(0));
                pattern.setDeviceScreenTimeout(BigInteger.valueOf(configList.get(1).intValue()));
                pattern.setDeviceIsRooted(configList.get(2));
                pattern.setDeviceHasAccessibility(configList.get(3));
                pattern.setDeviceHasAntivirus(configList.get(4));
            }
            List<Integer> mailList = dmUtils.readMailJSON(event);
            if (mailList.size() >= 4) {
                pattern.setMailContainsBCC(mailList.get(0));
                pattern.setMailContainsCC(mailList.get(1));
                pattern.setMailRecipientAllowed(mailList.get(2));
                pattern.setMailHasAttachment(mailList.get(3));
            }
            List<String> wifiList = dmUtils.readAssetJSON(event);
            if (wifiList.size() >= 4) {
                pattern.setWifiEncryption(wifiList.get(0));
                pattern.setBluetoothConnected(Integer.parseInt(wifiList.get(1)));
                pattern.setWifiEnabled(Integer.parseInt(wifiList.get(2)));
                pattern.setWifiConnected(Integer.parseInt(wifiList.get(3)));
            }
           
            i++;
            patternList.add(pattern);
        } while (i < accessRequests.size());
        
        return patternList;
       
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
        List<String> wifiEncryptions = dbManager.getDistinctWifiEncryptions();
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
        atts.add(new Attribute("wifiEncryption", wifiEncryptions));
        atts.add(new Attribute("wifiEnabled"));
        atts.add(new Attribute("wifiConnected"));
        atts.add(new Attribute("bluetoothConnected"));
        atts.add(new Attribute("label", allLabels));
        data = new Instances("patternsData", atts, 0);

        Iterator<PatternsKrs> i = dbPatterns.iterator();
        while(i.hasNext()){
            PatternsKrs pattern = i.next();
            double[] vals = new double[data.numAttributes()];
           
            String eventType = pattern.getEventType();
            if (eventType == null) {
                continue;
            } else {
                if (eventType.contentEquals("SECURITY_PROPERTY_CHANGED") ||
                        eventType.contentEquals("ACTION_REMOTE_FILE_ACCESS") ||
                        eventType.contentEquals("ACTION_APP_OPEN") ||
                        eventType.contentEquals("ACTION_SEND_MAIL") ||
                        eventType.contentEquals("SAVE_ASSET") ||
                        eventType.contentEquals("VIRUS_FOUND") ||
                        eventType.contentEquals("CONTEXT_SENSOR_PACKAGE") ||
                        eventType.contentEquals("CONTEXT_SENSOR_CONNECTIVITY") ||
                        eventType.contentEquals("CONTEXT_SENSOR_PERIPHERAL") ||
                        eventType.contentEquals("CONTEXT_SENSOR_DEVICE_PROTECTION") ||
                        eventType.contentEquals("CONFIGURATION_CHANGE") ||
                        eventType.contentEquals("SECURITY_INCIDENT") ||
                        eventType.contentEquals("user_entered_password_field") ||
                        eventType.contentEquals("CONTEXT_SENSOR_PERIPHERAL") ||
                        eventType.contentEquals("USER_BEHAVIOR") ||
                        eventType.contentEquals("CONTEXT_SENSOR_APP")) {
                   
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
                    if (deviceModel == null || deviceModel.equalsIgnoreCase("domemodel") || deviceModel.equalsIgnoreCase("1222")) {
                        vals[13] = Utils.missingValue();
                    } else {
                        vals[13] = deviceTypes.indexOf(deviceModel);
                    }
                    String deviceOS = pattern.getDeviceOS();
                    if (deviceOS == null || deviceOS.equalsIgnoreCase("a0")) {
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
                    BigInteger time = pattern.getDeviceScreenTimeout();
                    if (time == null) {
                    	vals[20] = Utils.missingValue();
                    } else {
                    	vals[20] = pattern.getDeviceScreenTimeout().doubleValue();
                    }
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
                    if (assetName == null || assetName.equalsIgnoreCase("")) {
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
                    if (assetLocation == null || assetLocation.equalsIgnoreCase("")) {
                        vals[29] = Utils.missingValue();
                    } else {
                        vals[29] = assetLocations.indexOf(assetLocation);
                    }
                    vals[30] = pattern.getMailRecipientAllowed();
                    vals[31] = pattern.getMailContainsCC();
                    vals[32] = pattern.getMailContainsBCC();
                    vals[33] = pattern.getMailHasAttachment();
                    String wifiEncryption = pattern.getWifiEncryption();
                    if (wifiEncryption == null) {
                        vals[34] = Utils.missingValue();
                    } else {
                        vals[34] = wifiEncryptions.indexOf(wifiEncryption);
                    }
                    vals[35] = pattern.getWifiEnabled();
                    vals[36] = pattern.getWifiConnected();
                    vals[37] = pattern.getBluetoothConnected();
                    String label = pattern.getLabel();
                    if (label == null) {
                        vals[38] = Utils.missingValue();
                    } else {
                        vals[38] = allLabels.indexOf(label);
                    }
                   
                    data.add(new DenseInstance(1.0, vals));
                }               
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
     * @param data The instances from patterns_krs table
     *
     * @return indexes The indexes of the attributes selected by the algorithm
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
            logger.info("Selected Features: "+Utils.arrayToString(indexes));
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
     * @return classifierRules Output of the classifier, consisting of rules
     */
   
    public String dataClassification(Instances data, int[] indexes){
       
        String classifierRules = null;
        Instances newData = data;
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1";
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indexes);
        remove.setInvertSelection(true);
        try {
        	remove.setOptions(options);
            remove.setInputFormat(data);
            newData = Filter.useFilter(data, remove);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        /*Enumeration<Attribute> atts = newData.enumerateAttributes();
       
        while (atts.hasMoreElements()) {
            logger.info(atts.nextElement().toString());
        } */      
       
        double percentageCorrect = 0;
       
        /* (1) J48 */
        String[] optionsJ48 = new String[1];
        optionsJ48[0] = "-U";            // unpruned tree
        J48 treeJ48 = new J48();         // new instance of tree
        try {
            //treeJ48.setOptions(optionsJ48);     // set the options
            treeJ48.buildClassifier(newData);   // build classifier
           
            Evaluation eval = new Evaluation(newData);
            eval.crossValidateModel(treeJ48, newData, 10, new Random(1));
            percentageCorrect = eval.pctCorrect();
            System.out.println("Percentage of correctly classified instances for J48 classifier: "+eval.pctCorrect());
            classifierRules = treeJ48.toString();
            //System.out.println(treeJ48.toString());
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
                classifierRules = treeJRip.toString();
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
                classifierRules = treePART.toString();
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
            //treeREPTree.setOptions(optionsREPTree);     // set the options
            treeREPTree.buildClassifier(newData);   // build classifier
           
            Evaluation eval = new Evaluation(newData);
            eval.crossValidateModel(treeREPTree, newData, 10, new Random(1));
            if (eval.pctCorrect() > percentageCorrect) {
                percentageCorrect = eval.pctCorrect();
                classifierRules = treeREPTree.toString();
            }
            System.out.println("Percentage of correctly classified instances for REPTree classifier: "+eval.pctCorrect());
            //System.out.println(treeREPTree.toSource("prueba"));
            //System.out.println(treeREPTree.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return classifierRules;
       
    }
   
    /**
     * Method associationRules in which first we erase the attributes that were not selected by
     * featureSelection method and then obtains the set of rules through association algorithms.
     *
     * @param data The original set of instances
     * @param indexes The selected indexes by the feature selection algorithm
     *
     * @return associationRules Output of the algorithm, consisting of rules
     */
   
    public String associationRules(Instances data, int[] indexes){
       
    	String associationRules = null;
        Instances newData = data;
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1";
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indexes);
        remove.setInvertSelection(true);
        try {
        	remove.setOptions(options);
            remove.setInputFormat(data);
            newData = Filter.useFilter(data, remove);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Instances filteredData = newData;
    	NumericToNominal filter = new NumericToNominal();
    	try {
    		filter.setOptions(options);
			filter.setInputFormat(filteredData);
			filteredData = Filter.useFilter(newData, filter);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	
    	Apriori aprioriObj = new Apriori();
    	aprioriObj.setNumRules(500);
    	try {
			aprioriObj.buildAssociations(filteredData);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	associationRules = aprioriObj.toString();
    	//System.out.println("A Priori Rules: "+associationRules);
       
        return associationRules;
       
    }
   

}