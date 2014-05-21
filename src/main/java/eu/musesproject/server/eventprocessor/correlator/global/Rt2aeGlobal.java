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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.eventprocessor.composers.AccessRequestComposer;
import eu.musesproject.server.eventprocessor.composers.AdditionalProtectionComposer;
import eu.musesproject.server.eventprocessor.composers.ThreatComposer;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AdditionalProtection;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Threat;
import eu.musesproject.server.policyrulesselector.PolicySelector;
import eu.musesproject.server.policyrulestransmitter.PolicyTransmitter;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.rt2ae.Rt2aeServerImpl;

public class Rt2aeGlobal {
	
	private String status = null;
	private Logger logger = Logger.getLogger(Rt2aeGlobal.class.getName());
	private static List<Threat> threats = new ArrayList<Threat>();
	private static List<AccessRequest> requests = new ArrayList<AccessRequest>();
	private static List<AdditionalProtection> additionalProtections = new ArrayList<AdditionalProtection>();

	public void setStatus(String st) {
		status = st;
	}

	public String getStatus() {
		return status;
	}
	
	public AccessRequest composeAccessRequest(Event event){
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();
		Context context = new Context();//TODO This context should be extracted from the event
		Decision decision = rt2aeServer.decideBasedOnRiskPolicy(composedRequest, context);
		logger.info(decision.toString());
		logger.info("Session id:"+event.getSessionId());
		return composedRequest;
	}
	
	public int composeAccessRequest(FileObserverEvent event){
		Decision[] decisions = new Decision[1];
		
		AccessRequest composedRequest = AccessRequestComposer.composeAccessRequest(event);
		composedRequest.setId(requests.size()+1);
		Rt2aeServerImpl rt2aeServer = new Rt2aeServerImpl();
		Context context = new Context();//TODO This context should be extracted from the event
		//Retrieve the decision associated to current composedRequest
		Decision decision = rt2aeServer.decideBasedOnRiskPolicy(composedRequest, context);
		decisions[0] = decision;
		
		//Select the most appropriate policy according to the decision and the action of the request		
		logger.info("Session id:"+event.getSessionId());
		PolicySelector policySelector = new PolicySelector();
		logger.info("Rt2aeGlobal request action:"+composedRequest.getAction());
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(decisions, composedRequest.getAction());
		logger.info(policyDT.getRawPolicy());
		logger.info(decision.toString());
		requests.add(composedRequest);
		
		//Send policy
		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.info("Device Policy is now sent:"+policyDT.getRawPolicy());
		
		return composedRequest.getId();
	}
	
	public Threat composeThreat(eu.musesproject.server.eventprocessor.correlator.model.owl.AccessRequest request, Event event){
		Threat composedThreat = ThreatComposer.composeThreat(request.getId(),event);
		//TODO Complete the composition of threat attributes, based on the information of the event
		threats.add(composedThreat);
		return composedThreat;
	}
	
	public AdditionalProtection composeAdditionalProtection(eu.musesproject.server.eventprocessor.correlator.model.owl.AccessRequest request, Event event){
		AdditionalProtection additionalProtection = AdditionalProtectionComposer.composeAdditionalProtection(request.getId(),event);		
		additionalProtection.setRequestId(request.getId());		
		additionalProtections.add(additionalProtection);
		return additionalProtection;
	}
	
	public static List<eu.musesproject.server.risktrust.Threat> getThreatsByRequestId(int requestId){
		List<eu.musesproject.server.risktrust.Threat> result = new ArrayList<eu.musesproject.server.risktrust.Threat>();
		eu.musesproject.server.risktrust.Threat aux = null;
		for (Iterator iterator = threats.iterator(); iterator.hasNext();) {
			Threat threat = (Threat) iterator.next();
			if (threat.getRequestId()==requestId){
				aux = convertThreat(threat);
				result.add(aux);
			}			
		}
		return result;
	}
	
	public static List<eu.musesproject.server.risktrust.AdditionalProtection> getProtectionsByRequestId(int requestId){
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
	
	

	private static eu.musesproject.server.risktrust.Threat convertThreat(Threat threat) {
		eu.musesproject.server.risktrust.Threat result = new eu.musesproject.server.risktrust.Threat();
		result.setAssetId(threat.getAssetId());
		result.setId(threat.getId());
		result.setProbability(threat.getProbability());
		result.setTimestamp(threat.getTimestamp());
		result.setType(threat.getType());
		return result;
	}
	
	private static eu.musesproject.server.risktrust.AdditionalProtection convertAdditionalProtection(AdditionalProtection protection) {
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
		
		return requests.get(requestId-1);
				
	}
	
	public int sendDefaultDevicePolicy(Event event){

		String defaultPolicy = "{\"muses-device-policy\":{\"files\":{\"resource\":{\"id\":\"12345\",\"description\":\"X-Project Brochure\",\"path\":\"/company-repo/commercial/brochures/xproject/brochure.pdf\",\"resourceType\":\"document\"},\"subject\":{\"id\":\"44444\",\"description\":\"user1\",\"role\":{\"id\":\"1\",\"description\":\"consultancy\"}},\"riskCommunication\":{\"id\":\"1\",\"communication_sequence\":\"1\",\"riskTreatment\":{\"id\":\"1\",\"textualdescription\":\"You are not allowed to open this file, due to your current connection properties, please connect through a secure Wifi to use this resource\"}},\"action\":{\"allow\":{\"id\":\"1234567\"},\"type\":\"open\"}},\"antivirus\":{\"allow\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"updated\":true,\"deny\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"check-interval\":\"day\",\"required\":true},\"revision\":1,\"physical\":{\"micro-allowed\":true,\"camera-allowed\":true},\"apps\":{\"installed\":{\"installed-blacklist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]},\"must-be-updated\":true,\"check-interval\":\"day\",\"allow-install\":true,\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}},\"installed-whitelist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]}},\"permissions\":{\"allow\":{\"concurrent\":{\"perm\":[\"FOO\",\"BAR\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"deny\":{\"concurrent\":{\"perm\":[\"DISK_ACCESS\",\"NETWORK_ACCESS\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"running\":{\"allow\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"deny\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}}},\"phoning\":\"\",\"web\":{\"allow-ftp\":false,\"allow-insecure\":true,\"plugins\":{\"allow\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]},\"deny\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]}},\"urls\":{\"allow\":{\"url\":[\"http://www.unige.ch\",\"http://www.s2grupo.es\",\"ALL\"]},\"deny\":{\"url\":[\"http://thepiratebay.sx\",\"http://mininova.org\",\"ALL\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"password\":{\"password-must-be-robust\":true,\"require\":{\"visual\":{},\"pin\":{},\"password\":{}}},\"network\":{\"connection-3g\":{\"allow-3g\":true,\"carriers\":{\"allow\":\"\",\"deny\":\"\"},\"allow-roaming\":true},\"wifi\":{\"allow-wifi\":true,\"allow\":{\"algo\":[\"WPA2 PSK\",\"WPA2 ENTERPRISE\",\"ALL\"]},\"deny\":{\"algo\":[\"NONE\",\"WEP\",\"ALL\"]}},\"require-vpn\":false},\"storage\":{\"data-classification\":{\"all-home-files-encrypted\":false,\"all-work-files-encrypted\":true,\"all-files-classified\":true},\"must-encrypt-primary-storage\":true,\"allow-extra-storage\":true,\"contacts-classification\":\"\",\"must-encrypt-extra-storage\":true},\"schema-version\":1},\"requesttype\":\"update_policies\"}";
		
		PolicyDT policyDT = new PolicyDT();
		policyDT.setRawPolicy(defaultPolicy);
		//Send policy		
		Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		transmitter.sendPolicyDT(policyDT, device, event.getSessionId());
		logger.info("Device Policy is now sent:"+policyDT.getRawPolicy());
		
		return 1;
	}
	
}
