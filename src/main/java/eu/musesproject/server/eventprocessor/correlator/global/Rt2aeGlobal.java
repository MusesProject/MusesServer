package eu.musesproject.server.eventprocessor.correlator.global;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 S2 Grupo
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.connectionmanager.ConnectionManager;
import eu.musesproject.server.contextdatareceiver.JSONManager;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.DeviceType;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.composers.AccessRequestComposer;
import eu.musesproject.server.eventprocessor.composers.AdditionalProtectionComposer;
import eu.musesproject.server.eventprocessor.composers.ClueComposer;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AdditionalProtection;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.UserBehaviorEvent;
import eu.musesproject.server.policyrulesselector.PolicySelector;
import eu.musesproject.server.policyrulestransmitter.PolicyTransmitter;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Clue;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceSecurityState;
import eu.musesproject.server.risktrust.PolicyCompliance;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.RiskTreatment;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.rt2ae.Rt2aeServerImpl;
import eu.musesproject.server.scheduler.ModuleType;

public class Rt2aeGlobal {
	
	private static final String MUSES_TAG = "MUSES_TAG";
	private String status = null;
	private Logger logger = Logger.getLogger(Rt2aeGlobal.class.getName());
	private static List<Clue> clues = new ArrayList<Clue>();
	private static List<Clue> deviceSecurityClues = new ArrayList<Clue>();
	private static List<AccessRequest> requests = new ArrayList<AccessRequest>();
	private static List<AdditionalProtection> additionalProtections = new ArrayList<AdditionalProtection>();
	private DBManager dbManager = new DBManager(ModuleType.EP);
	
	private static Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();

	public void setStatus(String st) {
		status = st;
	}

	public String getStatus() {
		return status;
	}
	
	public AccessRequest composeAccessRequest(Event event){
		logger.info("[composeAccessRequest] Event");
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		//Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();
		Context context = new Context();//TODO This context should be extracted from the event
		PolicyCompliance policyCompliance = new PolicyCompliance();
		
		Decision decision = rt2aeServer.decideBasedOnRiskPolicy(composedRequest, policyCompliance, context);
		logger.info("		" + decision.toString());
		logger.info("		" + "Session id:"+event.getSessionId());
		return composedRequest;
	}

	
	public Clue deviceSecurityStateChange(Event event, String name, String type){
		Devices device = null;
		logger.info("[deviceSecurityStateChange]");
		Clue composedClue = ClueComposer.composeClue(event, name, type);
		deviceSecurityClues.add(composedClue);
		DeviceSecurityState deviceSecurityState = new DeviceSecurityState();
		//Manage device in database
		Devices deviceInstance = dbManager.getDeviceByIMEI(event.getDeviceId());
		if (deviceInstance==null) {
			device = new Devices();
			device.setImei(event.getDeviceId());
			device.setName(event.getDeviceId());
			DeviceType deviceType = new DeviceType();
			deviceType.setDeviceTypeId(1222);//TODO manage device type conveniently
			device.setDeviceType(deviceType);
			dbManager.persist(device);
		}/*else{
			device = deviceInstance;
		}*/
		//FIXME no function right now change to current implementation
		logger.info("EVENT PROCESSOR.......: "+event.getDeviceId());

		if (event.getDeviceId()!=null){
			//deviceSecurityState.setDevice_id(Integer.valueOf(event.getDeviceId()));
			
			deviceSecurityState.setDevice_id(new BigInteger(event.getDeviceId()));
		}
		deviceSecurityState.setClues(deviceSecurityClues);
		try{
			rt2aeServer.warnDeviceSecurityStateChange(deviceSecurityState);
		}catch(java.lang.IllegalStateException e){
			logger.error("Please, check database:An error has produced while calling RT2AE server: warnDeviceSecurityStateChange:"+e.getLocalizedMessage());
		}
		
		//dbManager.close();
		return composedClue;
	}
	
	//public Clue composeClue(Event event, String name, String type){
	public eu.musesproject.server.eventprocessor.correlator.model.owl.Clue composeClue(Event event, String name, String type){ 
		logger.info("[composeClue]");
		eu.musesproject.server.eventprocessor.correlator.model.owl.Clue factClue = new eu.musesproject.server.eventprocessor.correlator.model.owl.Clue();
		Clue composedClue = ClueComposer.composeClue(event, name, type);
		//TODO Complete the composition of threat attributes, based on the information of the event
		clues.add(composedClue);
		factClue.setName(composedClue.getName());
		factClue.setTimestamp(composedClue.getTimestamp());
		factClue.setEvent_date(new Date(System.currentTimeMillis()));
		return factClue;
	}
	
	public AdditionalProtection composeAdditionalProtection(eu.musesproject.server.eventprocessor.correlator.model.owl.AccessRequest request, Event event){
		logger.info("[composeAdditionalProtection]");
		AdditionalProtection additionalProtection = AdditionalProtectionComposer.composeAdditionalProtection(request.getId(),event);		
		additionalProtection.setRequestId(request.getId());		
		additionalProtections.add(additionalProtection);
		return additionalProtection;
	}
	
	public static List<eu.musesproject.server.risktrust.Clue> getCluesByRequestId(int requestId){
		Logger.getLogger(Rt2aeGlobal.class).info("[getCluesByRequestId]:"+requestId);
		List<eu.musesproject.server.risktrust.Clue> result = new ArrayList<eu.musesproject.server.risktrust.Clue>();
		eu.musesproject.server.risktrust.Clue aux = null;
		
		long eventId = 0;
		
		for (Iterator<AccessRequest> iterator = requests.iterator(); iterator.hasNext();) {
			AccessRequest accessRequest = (AccessRequest) iterator.next();
			Logger.getLogger(Rt2aeGlobal.class).info("[getCluesByRequestId]:accessRequest.getId()"+accessRequest.getId());
			if (accessRequest.getId()==requestId){
				eventId = accessRequest.getEventId();
			}
		}
		
		for (Iterator<Clue> iterator = clues.iterator(); iterator.hasNext();) {
			Clue clue = (Clue) iterator.next();
			Logger.getLogger(Rt2aeGlobal.class).info("[getCluesByRequestId]:clue.getId-"+clue.getId()+" eventId:"+(int)eventId);
			if (clue.getId()==(int)eventId){
				clue.setRequestId(requestId);
				aux = convertClue(clue);
				result.add(aux);
			}			
		}
		return result;
	}
	
	public static List<eu.musesproject.server.risktrust.AdditionalProtection> getProtectionsByRequestId(int requestId){
		Logger.getLogger(Rt2aeGlobal.class).info("[getProtectionsByRequestId]");
		List<eu.musesproject.server.risktrust.AdditionalProtection> result = new ArrayList<eu.musesproject.server.risktrust.AdditionalProtection>();
		eu.musesproject.server.risktrust.AdditionalProtection aux = null;
		for (Iterator iterator = additionalProtections.iterator(); iterator.hasNext();) {
			AdditionalProtection protection = (AdditionalProtection) iterator.next();
			if (protection.getRequestId()==requestId){
				aux = convertAdditionalProtection(protection);
				result.add(aux);
			}			
		}
		return result;
	}
	
	

	private static eu.musesproject.server.risktrust.Clue convertClue(Clue clue) {
		Logger.getLogger(Rt2aeGlobal.class).info("[convertClue]");
		eu.musesproject.server.risktrust.Clue result = new eu.musesproject.server.risktrust.Clue();
		result.setAssetId(clue.getAssetId());
		result.setId(clue.getId());
		result.setTimestamp(clue.getTimestamp());
		result.setType(clue.getType());
		result.setRequestId(clue.getRequestId());
		result.setName(clue.getName());
		Logger.getLogger(Rt2aeGlobal.class).info("    assetId:"+clue.getAssetId()+" id:"+clue.getId()+" name:"+clue.getName()+" timestamp:"+clue.getTimestamp()+" type:"+clue.getType()+" requestId:"+clue.getRequestId());
		return result;
	}
	
	private static eu.musesproject.server.risktrust.AdditionalProtection convertAdditionalProtection(AdditionalProtection protection) {
		Logger.getLogger(Rt2aeGlobal.class).info("[convertAdditionalProtection]");
		eu.musesproject.server.risktrust.AdditionalProtection result = new eu.musesproject.server.risktrust.AdditionalProtection();
		result.setAssetId(protection.getAssetId());
		result.setId(protection.getId());
		result.setProbability(protection.getProbability());
		result.setTimestamp(protection.getTimestamp());
		result.setType(protection.getType());
		result.setPasswordProtected(protection.isPasswordProtected());
		result.setPatternProtected(protection.isPatternProtected());
		result.setRooted(protection.isRooted());
		result.setTrustedAVInstalled(protection.isTrustedAVInstalled());
		return result;
	}
	
	public static AccessRequest getRequestById(int requestId){
		Logger.getLogger(Rt2aeGlobal.class).info("[getRequestById]");
		return requests.get(requestId-1);
				
	}
	
	public int sendDefaultDevicePolicy(Event event){
		logger.info("[sendDefaultDevicePolicy]");
		String defaultPolicy = "{\"muses-device-policy\":{\"files\":{\"resource\":{\"id\":\"12345\",\"description\":\"X-Project Brochure\",\"path\":\"/company-repo/commercial/brochures/xproject/brochure.pdf\",\"resourceType\":\"document\"},\"subject\":{\"id\":\"44444\",\"description\":\"user1\",\"role\":{\"id\":\"1\",\"description\":\"consultancy\"}},\"riskCommunication\":{\"id\":\"1\",\"communication_sequence\":\"1\",\"riskTreatment\":{\"id\":\"1\",\"textualdescription\":\"You are not allowed to open this file, due to your current connection properties, please connect through a secure Wifi to use this resource\"}},\"action\":{\"allow\":{\"id\":\"1234567\"},\"type\":\"open\"}},\"antivirus\":{\"allow\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"updated\":true,\"deny\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"check-interval\":\"day\",\"required\":true},\"revision\":1,\"physical\":{\"micro-allowed\":true,\"camera-allowed\":true},\"apps\":{\"installed\":{\"installed-blacklist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]},\"must-be-updated\":true,\"check-interval\":\"day\",\"allow-install\":true,\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}},\"installed-whitelist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]}},\"permissions\":{\"allow\":{\"concurrent\":{\"perm\":[\"FOO\",\"BAR\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"deny\":{\"concurrent\":{\"perm\":[\"DISK_ACCESS\",\"NETWORK_ACCESS\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"running\":{\"allow\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"deny\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}}},\"phoning\":\"\",\"web\":{\"allow-ftp\":false,\"allow-insecure\":true,\"plugins\":{\"allow\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]},\"deny\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]}},\"urls\":{\"allow\":{\"url\":[\"http://www.unige.ch\",\"http://www.s2grupo.es\",\"ALL\"]},\"deny\":{\"url\":[\"http://thepiratebay.sx\",\"http://mininova.org\",\"ALL\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"password\":{\"password-must-be-robust\":true,\"require\":{\"visual\":{},\"pin\":{},\"password\":{}}},\"network\":{\"connection-3g\":{\"allow-3g\":true,\"carriers\":{\"allow\":\"\",\"deny\":\"\"},\"allow-roaming\":true},\"wifi\":{\"allow-wifi\":true,\"allow\":{\"algo\":[\"WPA2 PSK\",\"WPA2 ENTERPRISE\",\"ALL\"]},\"deny\":{\"algo\":[\"NONE\",\"WEP\",\"ALL\"]}},\"require-vpn\":false},\"storage\":{\"data-classification\":{\"all-home-files-encrypted\":false,\"all-work-files-encrypted\":true,\"all-files-classified\":true},\"must-encrypt-primary-storage\":true,\"allow-extra-storage\":true,\"contacts-classification\":\"\",\"must-encrypt-extra-storage\":true},\"schema-version\":1},\"requesttype\":\"update_policies\"}";
		
		PolicyDT policyDT = new PolicyDT();
		policyDT.setRawPolicy(defaultPolicy);
		//Send policy		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());
		
		return 1;
	}
	
	public int decide(FileObserverEvent event, ConnectivityEvent connEvent){//Simulate response from RT2AE, for demo purposes
		logger.info("[decide]");
		Decision[] decisions = new Decision[1];
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		requests.add(composedRequest);
		//Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();
		//Context context = new Context();//TODO This context should be extracted from the event
		//Simulate response from RT2AE, for demo purposes
		Decision decision = testDecideBasedOnRisk(composedRequest, connEvent);
		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("		Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.log(Level.INFO, MUSES_TAG + "Request action: "+composedRequest.getAction());
		logger.info("		Rt2aeGlobal request action:"+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(event.getHashId(),decisions, composedRequest.getAction(), composedRequest.getRequestedCorporateAsset());
		logger.log(Level.INFO, MUSES_TAG + " Selecting policy action: "+composedRequest.getAction());
		logger.info(policyDT.getRawPolicy());
		logger.info(decision.toString());
		//requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.log(Level.INFO, MUSES_TAG + " Now sending policy:"+policyDT.getRawPolicy());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());
		
		return composedRequest.getId();
	}
	

	public int composeAccessRequest(FileObserverEvent event, ConnectivityEvent connEvent, String mode, String condition){//Simulate response from RT2AE, for demo purposes
		logger.info("[composeAccessRequest] event,conn");
		Decision[] decisions = new Decision[1];
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		requests.add(composedRequest);
		//Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();
		Context context = new Context();//TODO This context should be extracted from the event
		//Simulate response from RT2AE, for demo purposes
		PolicyCompliance policyCompliance = policyCompliance(composedRequest, "",  connEvent, mode, condition); //TODO Fix empty string
		
		Decision decision = rt2aeServer.decideBasedOnRiskPolicy(composedRequest, policyCompliance, context);
		
		// Control based on policy compliance
		// TODO Disable when RT2AE is implemented
		if (policyCompliance.getResult().equals(PolicyCompliance.MAYBE)) {
			decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
		} else if (policyCompliance.getResult().equals(PolicyCompliance.DENY)) {
			decision = Decision.STRONG_DENY_ACCESS;
		} else if (policyCompliance.getResult().equals(PolicyCompliance.ALLOW)) {
			decision = Decision.GRANTED_ACCESS;
		}
		decision.setCondition(condition);

		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("		Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.log(Level.INFO, MUSES_TAG + " Request action:"+composedRequest.getAction());
		logger.info("		Rt2aeGlobal request action:"+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(event.getHashId(),decisions, composedRequest.getAction(), composedRequest.getRequestedCorporateAsset());
		logger.log(Level.INFO, MUSES_TAG + " Selecting policy action: "+composedRequest.getAction());
		logger.info("		" + policyDT.getRawPolicy());
		logger.info("		" + decision.toString());
		//requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.log(Level.INFO, MUSES_TAG + " Now sending policy:"+policyDT.getRawPolicy());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());
		
		return composedRequest.getId();
	}
	
	public int deny(AppObserverEvent event, String message){//Simulate response from RT2AE, for demo purposes
		logger.info("[deny]");
		Decision[] decisions = new Decision[1];
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		requests.add(composedRequest);
		Decision decision = null;
		
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		RiskTreatment [] riskTreatments = new RiskTreatment[1];
		RiskTreatment riskTreatment = new RiskTreatment(message);
		
		riskTreatments[0] = riskTreatment;	
		riskCommunication.setRiskTreatment(riskTreatments);
		decision = Decision.STRONG_DENY_ACCESS;
		decision.STRONG_DENY_ACCESS.setRiskCommunication(riskCommunication); 
		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("		Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.log(Level.INFO, MUSES_TAG + " Request action: "+composedRequest.getAction());
		logger.info("		Rt2aeGlobal request action:"+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(event.getHashId(),decisions, composedRequest.getAction(), composedRequest.getRequestedCorporateAsset());
		logger.log(Level.INFO, MUSES_TAG + " Selecting policy action: "+composedRequest.getAction());
		logger.info("		" + policyDT.getRawPolicy());
		logger.info("		" + decision.toString());
		//requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());
		logger.log(Level.INFO, MUSES_TAG + " Now sending policy:"+policyDT.getRawPolicy());

		return composedRequest.getId();
	}
	
	public int composeAccessRequest(Event event, String message, String mode, String condition){//Simulate response from RT2AE, for demo purposes
		logger.info("[composedAccessRequest]");
		Decision[] decisions = new Decision[1];
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		requests.add(composedRequest);
		//Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();
		Decision decision = null;
		Context context = new Context();
		
		//Store security violation in db
		
		storeComplexEvent(event, message, mode, condition, composedRequest.getEventId());
		
		PolicyCompliance policyCompliance = policyCompliance(composedRequest, message, event, mode, condition);
		try{
			decision = rt2aeServer.decideBasedOnRiskPolicy(composedRequest, policyCompliance, context);
		}catch(javax.persistence.EntityExistsException e){
			logger.error("Please, check database persistence:An error has produced while calling RT2AE server: decideBasedOnRiskPolicy:"+e.getLocalizedMessage());
		}catch(Exception e){
			logger.error("An error has produced while calling RT2AE server: decideBasedOnRiskPolicy:"+e.getLocalizedMessage());
		}		
		//Control based on policy compliance
		
		if (decision == null){
			if (policyCompliance.getResult().equals(PolicyCompliance.MAYBE)){
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
			}else if (policyCompliance.getResult().equals(PolicyCompliance.DENY)){
				decision = Decision.STRONG_DENY_ACCESS;
			}else if (policyCompliance.getResult().equals(PolicyCompliance.ALLOW)){
				decision = Decision.GRANTED_ACCESS;
			}
		}
		decision.setCondition(condition);
		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("		Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.info("		Rt2aeGlobal request action:"+composedRequest.getAction());
		logger.log(Level.INFO, MUSES_TAG + " Request action: "+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(event.getHashId(),decisions, composedRequest.getAction(), composedRequest.getRequestedCorporateAsset());
		logger.log(Level.INFO, MUSES_TAG + " Selecting policy action: "+composedRequest.getAction());

		logger.info("		" + policyDT.getRawPolicy());
		logger.info("		" + decision.toString());
		//requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.log(Level.INFO, MUSES_TAG + " Now sending policy:"+policyDT.getRawPolicy());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());

		return composedRequest.getId();
	}
	
	
	public int allow(AppObserverEvent event){//Simulate response from RT2AE, for demo purposes
		logger.info("[allow]");
		Decision[] decisions = new Decision[1];
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		requests.add(composedRequest);
		Decision decision = null;
		
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		RiskTreatment [] riskTreatments = new RiskTreatment[1];
		RiskTreatment riskTreatment = new RiskTreatment("Allow");
		
		riskTreatments[0] = riskTreatment;	
		riskCommunication.setRiskTreatment(riskTreatments);
		decision = Decision.GRANTED_ACCESS;
		decision.GRANTED_ACCESS.setRiskCommunication(riskCommunication); 
		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("		Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.info("		Rt2aeGlobal request action:"+composedRequest.getAction());
		logger.log(Level.INFO, MUSES_TAG + " Request action: "+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(event.getHashId(),decisions, composedRequest.getAction(), composedRequest.getRequestedCorporateAsset());
		logger.log(Level.INFO, MUSES_TAG + " Selecting policy action: "+composedRequest.getAction());

		logger.info("		" + policyDT.getRawPolicy());
		logger.info("		" + decision.toString());
		//requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());
		logger.log(Level.INFO, MUSES_TAG + " Now sending policy:"+policyDT.getRawPolicy());

		return composedRequest.getId();
	}

	private PolicyCompliance policyCompliance(AccessRequest composedRequest, String message, Event event, String mode, String condition) {
		logger.info("[policyCompliance]");
		PolicyCompliance compliance = new PolicyCompliance();
		compliance.setRequestId(composedRequest.getId());
		
		if (mode.equals("DECIDE")){
		
			if (event instanceof ConnectivityEvent){
				ConnectivityEvent connEvent = (ConnectivityEvent)event;
				if (!connEvent.getWifiEncryption().contains("WPA2")){
					compliance.setResult(PolicyCompliance.MAYBE);
					compliance.setCompliance(false);
					//compliance.setReason("Action not allowed. Please, change WIFI encryption to WPA2");
					compliance.setReason(message);
					compliance.setCondition("wifiencryption!=WPA2");
				}else{
					compliance.setResult(PolicyCompliance.ALLOW);
					compliance.setCompliance(true);
					//compliance.setReason("Action allowed");
					compliance.setReason(message);
				}
			}
		}else if (mode.equals("DENY")){
			if (event instanceof AppObserverEvent){
				//AppObserverEvent appEvent = (AppObserverEvent)event;
				//compliance.setReason("Action not allowed. Blacklisted application");
				compliance.setReason(message);
			}else{
				//compliance.setReason("Action not allowed");
				compliance.setReason(message);
			}
			compliance.setResult(PolicyCompliance.DENY);
			compliance.setCompliance(false);
			
		}else if (mode.equals("ALLOW")){
			compliance.setResult(PolicyCompliance.ALLOW);
			compliance.setCompliance(true);
			//compliance.setReason("Action allowed because it is compliant with policies. To be confirmed by RT2AE");
			compliance.setReason(message);
		}
		
		logger.info("		Compliance for access request "+compliance.getRequestId()+" :"+compliance.getResult()+" "+compliance.getReason());
		
		return compliance;
	}
	
	
	
	
	private Decision testDecideBasedOnRisk(AccessRequest composedRequest,	ConnectivityEvent connEvent) {//TODO Demo purposes: RT2AE by-pass
		logger.info("[policyCompliance]");
		Decision decision = null;
		
		logger.info(connEvent.getWifiEncryption());
		if (!connEvent.getWifiEncryption().equals("WPA2")){
			eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
			RiskTreatment [] riskTreatments = new RiskTreatment[1];
			RiskTreatment riskTreatment = new RiskTreatment("Action not allowed. Please, change WIFI encryption to WPA2");
			
			riskTreatments[0] = riskTreatment;	
			riskCommunication.setRiskTreatment(riskTreatments);
			decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
			decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
			decision.setCondition("wifiencryption!=WPA2");
		}else{
			eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
			RiskTreatment [] riskTreatments = new RiskTreatment[1];
			RiskTreatment riskTreatment = new RiskTreatment("Action allowed.");
			
			riskTreatments[0] = riskTreatment;	
			riskCommunication.setRiskTreatment(riskTreatments);
			decision = Decision.GRANTED_ACCESS;
			decision.GRANTED_ACCESS.setRiskCommunication(riskCommunication); 
		}
		
		return decision;
	}
	
	public int allow(FileObserverEvent event, ConnectivityEvent connEvent){//Simulate response from RT2AE, for demo purposes
		logger.info("[allow]");
		Decision[] decisions = new Decision[1];
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		requests.add(composedRequest);
		Decision decision = null;
		
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		RiskTreatment [] riskTreatments = new RiskTreatment[1];
		RiskTreatment riskTreatment = new RiskTreatment("Action allowed.");
		
		riskTreatments[0] = riskTreatment;	
		riskCommunication.setRiskTreatment(riskTreatments);
		decision = Decision.GRANTED_ACCESS;
		decision.GRANTED_ACCESS.setRiskCommunication(riskCommunication); 
		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("		Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.info("		Rt2aeGlobal request action:"+composedRequest.getAction());
		logger.log(Level.INFO, MUSES_TAG + " Request action: "+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(event.getHashId(),decisions, composedRequest.getAction(), composedRequest.getRequestedCorporateAsset());
		logger.log(Level.INFO, MUSES_TAG + " Selecting policy action: "+composedRequest.getAction());

		logger.info("		" + policyDT.getRawPolicy());
		logger.info("		" + decision.toString());
		//requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.info("		Device Policy is now sent:"+policyDT.getRawPolicy());
		logger.log(Level.INFO, MUSES_TAG + " Now sending policy:"+policyDT.getRawPolicy());

		return composedRequest.getId();
	}
	
	public void notifySecurityIncident(Probability probability, SecurityIncident securityIncident){
		//Pre-requisites: MUSES UI reports a security incident associated to a concrete user
		
		//First, look for previous decisions that might be related the the current security incident
		//Second, get the information about the associated user (userTrustValue)
		
		rt2aeServer.warnUserSeemsInvolvedInSecurityIncident(securityIncident.getUser(), probability, securityIncident);
	}
	
	public void notifyUserBehavior(Event event){
		logger.info("[notifyUserBehavior] Event");
		if (event instanceof UserBehaviorEvent){
			UserBehaviorEvent userEvent = (UserBehaviorEvent)event;
			logger.info("		" + "UserBehavior sent to RT2AE:"+userEvent.getAction());
		}	
		//rt2aeServer.warn		
		
	}
	
	public static Rt2aeServerImpl getRt2aeServer(){
		return rt2aeServer;
	}
	
	public void storeComplexEvent(Event event, String message, String mode, String condition) {

		// Database insertion

		SecurityViolation securityViolation = new SecurityViolation();
		securityViolation.setConditionText(condition);
		//securityViolation.setDecisionId(decisionId);
		securityViolation.setDetection(new Date());
		
		securityViolation.setDeviceId(new BigInteger(dbManager.getDeviceByIMEI(event.getDeviceId()).getDeviceId()));

		securityViolation.setEventId(new BigInteger("2"));
		securityViolation.setMessage(message);
		securityViolation.setModeText(mode);
		Users user = dbManager.getUserByUsername(event.getUsername());
		if (user != null){
			securityViolation.setUserId(new BigInteger(user.getUserId()));
		}
		dbManager.setSecurityViolation(securityViolation);

	}
	
	public void storeComplexEvent(Event event, String message, String mode, String condition, Long eventId) {

		// Database insertion

		SecurityViolation securityViolation = new SecurityViolation();
		securityViolation.setConditionText(condition);
		securityViolation.setDetection(new Date());
		
		securityViolation.setDeviceId(new BigInteger(dbManager.getDeviceByIMEI(event.getDeviceId()).getDeviceId()));

		securityViolation.setEventId(BigInteger.valueOf(eventId));
		securityViolation.setMessage(message);
		securityViolation.setModeText(mode);
		Users user = dbManager.getUserByUsername(event.getUsername());
		if (user != null){
			securityViolation.setUserId(new BigInteger(user.getUserId()));
		}
		dbManager.setSecurityViolation(securityViolation);

	}
	
	public int wipeDevice(Event event, String sessionId){
		logger.info("[wipeDevice]");
		
		ConnectionManager connManager = ConnectionManager.getInstance();
		logger.info("		Session id:"+sessionId);
		//PolicySelector policySelector = new PolicySelector();
		
		Devices device = dbManager.getDeviceByIMEI(event.getDeviceId());

		
		JSONObject response = JSONManager.createWipeDeviceJSON(device);
		logger.log(Level.INFO, response.toString());
		logger.log(Level.INFO, MUSES_TAG +  " Response to send:"+response.toString() );
		logger.log(Level.INFO, MUSES_TAG +  " sessionID: "+ sessionId);
		connManager.sendData(sessionId, response.toString()); 
		return 1;
	}
	
	
}
