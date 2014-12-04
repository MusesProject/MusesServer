/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.policyrulesselector;

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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.hp.hpl.jena.util.FileManager;

import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.RiskTreatment;

/**
 * Class PolicySelector
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */
public class PolicySelector {
	
	private Logger logger = Logger.getLogger(PolicySelector.class.getName());
	
	
	/**
	 * Info RT
	 * 
	 *  Once the RT2AE makes a decision/s regarding the current request, these decisions (which will be based on a list of 
	 *  permissions and/or actions) must be applied on the device. Hence, the policy selector is in charge of building Decision
	 *   Table modifications/additions to be applied on the device
	 * 
	 * @param decisions
	 * 
	 * @return policyDT
	 */
	
	public PolicyDT computePolicyBasedOnDecisions( String requestId, Decision[] decisions, String action){ //Create device policy based on decision
		String empty = "<empty/>";
		PolicyDT resultPolicyDT = new PolicyDT();
		String jsonDevicePolicy = null;
		if (decisions.length > 0){//TODO This is a sample policy selection, hence the selection of concrete policies based on decisions is yet to be done
			Decision decision = decisions[0];
			if (decision!=null){
				jsonDevicePolicy = getJSONDevicePolicy(requestId, decision, action);
			}else{
				logger.info("		DECISION returned by RT2AE IS NULL");
				jsonDevicePolicy = empty;
			}			
		}else{
			jsonDevicePolicy = empty;
		}
		resultPolicyDT.setRawPolicy(jsonDevicePolicy);
		return resultPolicyDT;
	}	
	
	public PolicyDT computePolicyBasedOnDecisions( int requestId,  Decision[] decisions, String action, Asset asset){ //Create device policy based on decision
		String empty = "<empty/>";
		PolicyDT resultPolicyDT = new PolicyDT();
		String jsonDevicePolicy = null;
		if (decisions.length > 0){//TODO This is a sample policy selection, hence the selection of concrete policies based on decisions is yet to be done
			Decision decision = decisions[0];
			if (decision!=null){
				jsonDevicePolicy = getJSONDevicePolicy(requestId, decision, action, asset);
			}else{
				logger.info("		DECISION returned by RT2AE IS NULL");
				jsonDevicePolicy = empty;
			}
			
		}else{
			jsonDevicePolicy = empty;
		}
		resultPolicyDT.setRawPolicy(jsonDevicePolicy);
		return resultPolicyDT;
	}
	
	/**
	 * Info RT
	 * 
	 *  Once the policy decision table has been computed, this method applies this policy to the device
	 * 
	 * @param policy
	 * 
	 * @param device
	 * 
	 * @return void
	 */
	
	@SuppressWarnings("unused")
	private void applyPolicyOnDevice( PolicyDT policy, Device device){

	}
	
	private String getFullJSONDevicePolicy(){//TODO This is a sample policy selection, hence the selection of concrete policies based on decisions is yet to be done
		String jsonDevicePolicy = null;
		BufferedReader br = null;
		InputStream in = null;
		InputStreamReader is = null;
		try {
			in = FileManager.get().open("devpolicies/muses-device-policy-prototype.xml");
			is = new InputStreamReader(in);
			StringBuilder sb=new StringBuilder();
			br = new BufferedReader(is);
			String read = br.readLine();
			
			while(read != null) {			    
			    sb.append(read);
			    read = br.readLine();
			}
			String fileContent = sb.toString();
            JSONObject xmlJSONObj = XML.toJSONObject(fileContent);
            jsonDevicePolicy = xmlJSONObj.toString();
        } catch (JSONException je) {
        	logger.error("JSONException:" + je.getCause());
        } catch (IOException e) {
        	logger.error("IOException:" + e.getCause());
		} finally{
			
			try {
			    if (br != null) {
			    	br.close();
			    }
			  }catch (IOException e) {
				  logger.error("IOException:" + e.getCause());
			  
			  }
			try {
				if (in != null) {
				   	in.close();
				}
			}catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			}
			try {
				if (is != null) {
				   	is.close();
				}
			}catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			}  			
		}
        
        return jsonDevicePolicy;
	}
	
	private String getJSONDevicePolicy(String requestId, Decision decision, String action){
		String jsonDevicePolicy = null;
		BufferedReader br = null;
		InputStream in = null;
		InputStreamReader is = null;
		String policyContent = null;
		try {
			policyContent = getPolicyDTHeader();
			policyContent += getActionSection(decision, action, requestId);
			policyContent += getPolicyDTBottom();
            JSONObject xmlJSONObj = XML.toJSONObject(policyContent);
            jsonDevicePolicy = xmlJSONObj.toString();
        } catch (JSONException je) {
        	logger.error("JSONException:" + je.getCause());
        } catch (Exception e){
        	jsonDevicePolicy = "<errorBuildingPolicy/>";
        } finally{			
			try {
			    if (br != null) {
			    	br.close();
			    }
			  }catch (IOException e) {
				  logger.error("IOException:" + e.getCause());
			  
			  }
			try {
				if (in != null) {
				   	in.close();
				}
			}catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			}
			try {
				if (is != null) {
				   	is.close();
				}
			}catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			}  			
		}
        
        return jsonDevicePolicy;
	}
	
	private String getJSONDevicePolicy(int requestId, Decision decision, String action, Asset asset){
		String errorBuildingPolicy = "<errorBuildingPolicy/>";
		String jsonDevicePolicy = null;
		BufferedReader br = null;
		InputStream in = null;
		InputStreamReader is = null;
		String policyContent = null;
		try {
			policyContent = getPolicyDTHeader();
			policyContent += getActionSection(decision, action, requestId, asset);
			policyContent += getPolicyDTBottom();
            JSONObject xmlJSONObj = XML.toJSONObject(policyContent);
            jsonDevicePolicy = xmlJSONObj.toString();
        } catch (JSONException je) {
        	logger.error("JSONException:" + je.getCause());
        } catch (Exception e){
        	jsonDevicePolicy = errorBuildingPolicy;
        } finally{			
			try {
			    if (br != null) {
			    	br.close();
			    }
			  }catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			  }
			try {
				if (in != null) {
				   	in.close();
				}
			}catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			}
			try {
				if (is != null) {
				   	is.close();
				}
			}catch (IOException e) {
				logger.error("IOException:" + e.getCause());
			  
			}  			
		}
        
        return jsonDevicePolicy;
	}
	
	private String getPolicyDTHeader(){
		String header ="<requesttype>"+RequestType.UPDATE_POLICIES+"</requesttype><muses-device-policy schema-version=\"1.0\">"+"<!--The device will update its policy if this number is greater than the stored one    -->"+"<revision>1.0</revision>";
		return header;
	}
	
	private String getPolicyDTBottom(){
		return "</muses-device-policy>";
	}
	
	private String getActionSection(Decision decision, String action, String requestId){
		String result = null;
		String allowIni = "<allow><!-- Allow these URLs (could be regular expressions) -->";
		String denyIni = "<deny><!-- Allow these URLs (could be regular expressions) -->";
		String upToYouIni = "<up-to-you><!-- Allow these URLs (could be regular expressions) -->";
		String allowEnd = "</allow>";
		String denyEnd = "</deny>";
		String upToYouEnd = "</up-to-you>"; 
		String id =  "<id></id>";
		result = "<files>";
		result += "<action>";
		result += "<type>"+action+"</type>";
		if (requestId != null){
			result = "<request_id>"+requestId+"</request_id>";
		}
		if (decision.equals(Decision.GRANTED_ACCESS)){
			result += allowIni;
			result += id; //TODO Add resource identification
			result += allowEnd;
		}else if (decision.equals(Decision.STRONG_DENY_ACCESS)){
			result += denyIni;
			result += id; //TODO Add resource identification
			result += denyEnd;
		}else if (decision.equals(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS)){
			result += denyIni;
			result += id; //TODO Add resource identification
			result += denyEnd;
		}else if (decision.equals(Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION)){
			result += upToYouIni;
			result += id; //TODO Add resource identification
			result += upToYouEnd;
		}
		result += "</action>";
		result += "</files>";		
		
		return result;
	}
	
	private String getActionSection(Decision decision, String action, int requestId, Asset asset){
		String result = null;
		String allowIni = "<allow><!-- Allow these URLs (could be regular expressions) -->";
		String denyIni = "<deny><!-- Allow these URLs (could be regular expressions) -->";
		String upToYouIni = "<up-to-you><!-- Allow these URLs (could be regular expressions) -->";
		String maybeIni = "<maybe><!-- Allow these URLs (could be regular expressions) -->";
		String allowEnd = "</allow>";
		String denyEnd = "</deny>";
		String upToYouEnd = "</up-to-you>"; 
		result = "<files>";
		result += "<action>";
		result += "<type>"+action+"</type>";
		if (requestId != 0){
			result +="<request_id>"+requestId+"</request_id>";
		}
		if (decision.equals(Decision.GRANTED_ACCESS)){
			result += allowIni;
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
				result += "<condition>"+decision.getCondition()+"</condition>";
				result += "<riskTreatment>Allowed</riskTreatment>";
			}			
			result += allowEnd;
		}else if (decision.equals(Decision.STRONG_DENY_ACCESS)){
			result += denyIni;
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
				result += "<condition>"+decision.getCondition()+"</condition>";
				if (decision.getInformation()!=null){
					result += "<riskTreatment>"+decision.getInformation()+"</riskTreatment>";
				}
			}	
			result += denyEnd;
		}else if (decision.equals(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS)){
			result += maybeIni;
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
				if (decision.getCondition()!=null){
					result += "<condition>"+decision.getCondition()+"</condition>";
				}
				if (decision.getInformation()!=null){
					result += "<riskTreatment>"+decision.getInformation()+"</riskTreatment>";
				}else if (decision.getRiskCommunication()!=null){					
					RiskTreatment[] rt = decision.getRiskCommunication().getRiskTreatment();
					if (rt!=null){
						if (rt.length>0){
							if (rt[0].getTextualDescription()!=null){
								result += "<riskTreatment>"+rt[0].getTextualDescription()+"</riskTreatment>";
							}
						}
					}
				}
			}	
			result += "</maybe>";
		}else if (decision.equals(Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION)){
			result += upToYouIni;
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
				if (decision.getCondition()!=null){
					result += "<condition>"+decision.getCondition()+"</condition>";
				}
				if (decision.getInformation()!=null){
					result += "<riskTreatment>"+decision.getInformation()+"</riskTreatment>";
				}
			}	
			result += upToYouEnd;
		}
		result += "</action>";
		result += "</files>";		
		
		return result;
	}
	

}
