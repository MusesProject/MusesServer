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
		assertNotNull(transmitter.sendPolicyDT(policy, device));
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
       assertNotNull(transmitter.sendPolicyDT(policy, device));
	}
	
	
	public final void testCorrectnessPolicyDT() {
		String requestType = null;
		try {
			String jsonPolicy = "{\"muses-device-policy\":{\"files\":{\"action\":{\"allow\":{\"id\":\"\"},\"type\":\"open\"}},\"revision\":1,\"schema-version\":1},\"requesttype\":\"update_policies\"}";
			JSONObject requestJSON = new JSONObject(jsonPolicy);
			requestType = requestJSON.getString(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER);
			System.out.println("Request Type:"+requestType);

       } catch (JSONException je) {
           je.printStackTrace();
       } 
       assertEquals(RequestType.UPDATE_POLICIES, requestType);
	}
	

	

}
