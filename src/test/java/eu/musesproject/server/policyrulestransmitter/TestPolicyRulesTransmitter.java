package eu.musesproject.server.policyrulestransmitter;

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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mockito.Mock;

import com.hp.hpl.jena.util.FileManager;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.connectionmanager.IConnectionManager;
import eu.musesproject.server.risktrust.Device;
import junit.framework.TestCase;

public class TestPolicyRulesTransmitter extends TestCase {
	
	

	
	/**
	  * testSendPolicyDT - JUnit test case whose aim is to test the communication method to send a set of policy decision entries to a concrete device
	  *
	  * @param policyDT - Set of policy decision entries
	  * @param device - Target device, identified by MUSES
	  * 
	  */
	public final void testSendPolicyDTV1() {
		String dataToSend = null;
		String sessionId = "572H562LH72472OU4K";
		try {
			InputStream in = FileManager.get().open("devpolicies/muses-device-policy-prototype.xml");
			InputStreamReader is = new InputStreamReader(in);
			StringBuilder sb=new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			
			while(read != null) {			    
			    sb.append(read);
			    read = br.readLine();
			}
			String fileContent = sb.toString();
            JSONObject xmlJSONObj = XML.toJSONObject(fileContent);
            dataToSend = xmlJSONObj.toString();
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
        PolicyDT policy = new PolicyDT();
        policy.setRawPolicy(dataToSend);
        Device device = new Device();
		PolicyTransmitter transmitter = new PolicyTransmitter();
		assertNotNull(transmitter.sendPolicyDT(policy, device, sessionId));
	}
	
	/**
	  * testSendPolicyDT - JUnit test case whose aim is to test the communication method to send a set of policy decision entries to a concrete device
	  *
	  * @param policyDT - Set of policy decision entries
	  * @param device - Target device, identified by MUSES
	  * 
	  */
	public final void testSendPolicyDT() {
		String dataToSend = null;
		String sessionId = "572H562LH72472OU4K";
		try {
			String xmlPolicy = "{\"muses-device-policy\":{\"files\":{\"action\":{\"allow\":{\"id\":\"\"},\"type\":\"open\"}},\"revision\":1,\"schema-version\":1},\"requesttype\":\"update_policies\"}";
			JSONObject xmlJSONObj = XML.toJSONObject(xmlPolicy);
			dataToSend = xmlJSONObj.toString();
       } catch (JSONException je) {
           je.printStackTrace();
       } 
       PolicyDT policy = new PolicyDT();
       policy.setRawPolicy(dataToSend);
       Device device = new Device();
       PolicyTransmitter transmitter = new PolicyTransmitter();
       assertNotNull(transmitter.sendPolicyDT(policy, device, sessionId));
	}
	
	
	public final void testCorrectnessPolicyDT() {
		String requestType = null;
		String policy = null;
		try {
			
			//String action = "\"action\":{\"allow\":{\"id\":\"1234567\"}";
			String action = "\"action\":{\"allow\":{\"id\":\"1234567\"},\"type\":\"open\"}";
			String resource = "\"resource\":{\"id\":\"12345\",\"description\":\"X-Project Brochure\",\"path\":\"/company-repo/commercial/brochures/xproject/brochure.pdf\",\"resourceType\":\"document\"}";
			String subject = "\"subject\":{\"id\":\"44444\",\"description\":\"user1\",\"role\":{\"id\":\"1\",\"description\":\"consultancy\"}}";
			String riskCommunication = "\"riskCommunication\":{\"id\":\"1\",\"communication_sequence\":\"1\",\"riskTreatment\":{\"id\":\"1\",\"textualdescription\":\"You are not allowed to open this file, due to your current connection properties, please connect through a secure Wifi to use this resource\"}}";			
			//String jsonPolicy = "{\"muses-device-policy\":{\"files\":{"+resource+","+subject+","+riskCommunication+",\"action\":{\"allow\":{\"id\":\"1234567\"},\"type\":\"open\"}},\"antivirus\":{\"allow\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"updated\":true,\"deny\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"check-interval\":\"day\",\"required\":true},\"revision\":1,\"physical\":{\"micro-allowed\":true,\"camera-allowed\":true},\"apps\":{\"installed\":{\"installed-blacklist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]},\"must-be-updated\":true,\"check-interval\":\"day\",\"allow-install\":true,\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}},\"installed-whitelist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]}},\"permissions\":{\"allow\":{\"concurrent\":{\"perm\":[\"FOO\",\"BAR\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"deny\":{\"concurrent\":{\"perm\":[\"DISK_ACCESS\",\"NETWORK_ACCESS\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"running\":{\"allow\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"deny\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}}},\"phoning\":\"\",\"web\":{\"allow-ftp\":false,\"allow-insecure\":true,\"plugins\":{\"allow\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]},\"deny\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]}},\"urls\":{\"allow\":{\"url\":[\"http://www.unige.ch\",\"http://www.s2grupo.es\",\"ALL\"]},\"deny\":{\"url\":[\"http://thepiratebay.sx\",\"http://mininova.org\",\"ALL\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"password\":{\"password-must-be-robust\":true,\"require\":{\"visual\":{},\"pin\":{},\"password\":{}}},\"network\":{\"connection-3g\":{\"allow-3g\":true,\"carriers\":{\"allow\":\"\",\"deny\":\"\"},\"allow-roaming\":true},\"wifi\":{\"allow-wifi\":true,\"allow\":{\"algo\":[\"WPA2 PSK\",\"WPA2 ENTERPRISE\",\"ALL\"]},\"deny\":{\"algo\":[\"NONE\",\"WEP\",\"ALL\"]}},\"require-vpn\":false},\"storage\":{\"data-classification\":{\"all-home-files-encrypted\":false,\"all-work-files-encrypted\":true,\"all-files-classified\":true},\"must-encrypt-primary-storage\":true,\"allow-extra-storage\":true,\"contacts-classification\":\"\",\"must-encrypt-extra-storage\":true},\"schema-version\":1},\"requesttype\":\"update_policies\"}";
			//String jsonPolicy = "{\"muses-device-policy\":{\"files\":{"+resource+","+subject+","+riskCommunication+","+action+",\"type\":\"open\"}},\"antivirus\":{\"allow\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"updated\":true,\"deny\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"check-interval\":\"day\",\"required\":true},\"revision\":1,\"physical\":{\"micro-allowed\":true,\"camera-allowed\":true},\"apps\":{\"installed\":{\"installed-blacklist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]},\"must-be-updated\":true,\"check-interval\":\"day\",\"allow-install\":true,\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}},\"installed-whitelist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]}},\"permissions\":{\"allow\":{\"concurrent\":{\"perm\":[\"FOO\",\"BAR\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"deny\":{\"concurrent\":{\"perm\":[\"DISK_ACCESS\",\"NETWORK_ACCESS\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"running\":{\"allow\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"deny\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}}},\"phoning\":\"\",\"web\":{\"allow-ftp\":false,\"allow-insecure\":true,\"plugins\":{\"allow\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]},\"deny\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]}},\"urls\":{\"allow\":{\"url\":[\"http://www.unige.ch\",\"http://www.s2grupo.es\",\"ALL\"]},\"deny\":{\"url\":[\"http://thepiratebay.sx\",\"http://mininova.org\",\"ALL\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"password\":{\"password-must-be-robust\":true,\"require\":{\"visual\":{},\"pin\":{},\"password\":{}}},\"network\":{\"connection-3g\":{\"allow-3g\":true,\"carriers\":{\"allow\":\"\",\"deny\":\"\"},\"allow-roaming\":true},\"wifi\":{\"allow-wifi\":true,\"allow\":{\"algo\":[\"WPA2 PSK\",\"WPA2 ENTERPRISE\",\"ALL\"]},\"deny\":{\"algo\":[\"NONE\",\"WEP\",\"ALL\"]}},\"require-vpn\":false},\"storage\":{\"data-classification\":{\"all-home-files-encrypted\":false,\"all-work-files-encrypted\":true,\"all-files-classified\":true},\"must-encrypt-primary-storage\":true,\"allow-extra-storage\":true,\"contacts-classification\":\"\",\"must-encrypt-extra-storage\":true},\"schema-version\":1},\"requesttype\":\"update_policies\"}";
			String jsonPolicy = "{\"muses-device-policy\":{\"files\":{"+resource+","+subject+","+riskCommunication+","+action+"},\"antivirus\":{\"allow\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"updated\":true,\"deny\":{\"app\":[\"The UUID of the AV app\",\"The UUID of the AV app\",\"ALL\"]},\"check-interval\":\"day\",\"required\":true},\"revision\":1,\"physical\":{\"micro-allowed\":true,\"camera-allowed\":true},\"apps\":{\"installed\":{\"installed-blacklist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]},\"must-be-updated\":true,\"check-interval\":\"day\",\"allow-install\":true,\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}},\"installed-whitelist\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"]}},\"permissions\":{\"allow\":{\"concurrent\":{\"perm\":[\"FOO\",\"BAR\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"deny\":{\"concurrent\":{\"perm\":[\"DISK_ACCESS\",\"NETWORK_ACCESS\"]},\"perm\":[\"NETWORK_ACCESS\",\"SMS_SEND\",\"ALL\"]},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"running\":{\"allow\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"deny\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\",\"ALL\"],\"concurrent\":{\"app\":[\"The UUID of the app\",\"The UUID of the app\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}}},\"phoning\":\"\",\"web\":{\"allow-ftp\":false,\"allow-insecure\":true,\"plugins\":{\"allow\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]},\"deny\":{\"plugin\":[\"The ID of the plugin\",\"The ID of the plugin\",\"ALL\"]}},\"urls\":{\"allow\":{\"url\":[\"http://www.unige.ch\",\"http://www.s2grupo.es\",\"ALL\"]},\"deny\":{\"url\":[\"http://thepiratebay.sx\",\"http://mininova.org\",\"ALL\"]}},\"on-violation\":{\"notify-user\":{},\"notify-soc\":{}}},\"password\":{\"password-must-be-robust\":true,\"require\":{\"visual\":{},\"pin\":{},\"password\":{}}},\"network\":{\"connection-3g\":{\"allow-3g\":true,\"carriers\":{\"allow\":\"\",\"deny\":\"\"},\"allow-roaming\":true},\"wifi\":{\"allow-wifi\":true,\"allow\":{\"algo\":[\"WPA2 PSK\",\"WPA2 ENTERPRISE\",\"ALL\"]},\"deny\":{\"algo\":[\"NONE\",\"WEP\",\"ALL\"]}},\"require-vpn\":false},\"storage\":{\"data-classification\":{\"all-home-files-encrypted\":false,\"all-work-files-encrypted\":true,\"all-files-classified\":true},\"must-encrypt-primary-storage\":true,\"allow-extra-storage\":true,\"contacts-classification\":\"\",\"must-encrypt-extra-storage\":true},\"schema-version\":1},\"requesttype\":\"update_policies\"}";
			JSONObject requestJSON = new JSONObject(jsonPolicy);
			requestType = requestJSON.getString(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER);
			System.out.println("JSON policy:"+jsonPolicy);
			System.out.println("Request Type:"+requestType);
			
			
			
			policy = requestJSON.getString(JSONIdentifiers.DEVICE_POLICY);
			
			System.out.println(policy);
			
			JSONObject policyJSON = new JSONObject(policy);
			
			String files = policyJSON.getString("files");
			
			JSONObject filesJSON = new JSONObject(files);
			System.out.println(files);
			
			//Create decision table entry containing
				//Action
			String actionString = filesJSON.getString("action");
			JSONObject actionJSON = new JSONObject(actionString);
			String allowAction = actionJSON.getString("allow");
			JSONObject allowActionJSON = new JSONObject(allowAction);
			String idResourceAllowed = allowActionJSON.getString("id");
			System.out.println("Allowed:"+idResourceAllowed);
			String typeAction = actionJSON.getString("type");
			System.out.println("Action type:"+typeAction);
			//Insert action in db, if it does not exist
			//Insert decision in db with the same description, if it does not exist
			
				//Resource
			String resources = filesJSON.getString("resource");
			JSONObject resourcesJSON = new JSONObject(resources);			
			String typeResource = resourcesJSON.getString("resourceType");
			//Check if resourceType exists. If not, insert it and use its id for resource
			String idResource = resourcesJSON.getString("id");
			String descResource = resourcesJSON.getString("description");
			String pathResource = resourcesJSON.getString("path");
			System.out.println("Resource info:"+idResource+"-"+descResource+"-"+pathResource+"-"+typeResource);
			//Insert resource in db, if it does not exist
				//Decision
				//Subject
			String subjectString = filesJSON.getString("subject");
			JSONObject subjectJSON = new JSONObject(subjectString);
			String roleSubject = subjectJSON.getString("role");
			JSONObject roleJSON = new JSONObject(roleSubject);
			String idRole = roleJSON.getString("id");
			String descRole = roleJSON.getString("description");
			//Check if role exists. If not, insert it and use its id for subject
			String idSubject = subjectJSON.getString("id");
			String descSubject = subjectJSON.getString("description");

			System.out.println("Subject info:"+idSubject+"-"+descSubject+"-"+idRole+"-"+descRole);
			//Insert subject in db, if it does not exist
				//RiskCommunication
			
			String communicationString = filesJSON.getString("riskCommunication");
			JSONObject commJSON = new JSONObject(communicationString);
			String treatmentComm = commJSON.getString("riskTreatment");
			JSONObject treatmentJSON = new JSONObject(treatmentComm);
			String idTreatment = treatmentJSON.getString("id");
			String descTreatment = treatmentJSON.getString("textualdescription");
			//Check if treatment exists. If not, insert it and use its id for resource
			String idComm = commJSON.getString("id");
			String seqComm = commJSON.getString("communication_sequence");
			System.out.println("Risk Communication info:"+idComm+"-"+seqComm+"-"+idTreatment+"-"+descTreatment);
			//Insert riskCommunication in db, if it does not exist
			
			//At the end, with all the inserted ids, update the decision table


       } catch (JSONException je) {
           je.printStackTrace();
       } 
       assertEquals(RequestType.UPDATE_POLICIES, requestType);
	}
	

	

}
