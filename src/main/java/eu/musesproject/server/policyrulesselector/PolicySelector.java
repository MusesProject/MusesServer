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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.hp.hpl.jena.util.FileManager;

import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;

/**
 * Class PolicySelector
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */
public class PolicySelector {
	
	
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
	
	public PolicyDT computePolicyBasedOnDecisions( Decision[] decisions, String action){ //Create device policy based on decision
				
		PolicyDT resultPolicyDT = new PolicyDT();
		String jsonDevicePolicy = null;
		if (decisions.length > 0){//TODO This is a sample policy selection, hence the selection of concrete policies based on decisions is yet to be done
			Decision decision = decisions[0];
			jsonDevicePolicy = getJSONDevicePolicy(decision, action);
		}else{
			jsonDevicePolicy = "<empty/>";
		}
		resultPolicyDT.setRawPolicy(jsonDevicePolicy);
		return resultPolicyDT;
	}	
	
	public PolicyDT computePolicyBasedOnDecisions( Decision[] decisions, String action, Asset asset){ //Create device policy based on decision
		
		PolicyDT resultPolicyDT = new PolicyDT();
		String jsonDevicePolicy = null;
		if (decisions.length > 0){//TODO This is a sample policy selection, hence the selection of concrete policies based on decisions is yet to be done
			Decision decision = decisions[0];
			jsonDevicePolicy = getJSONDevicePolicy(decision, action, asset);
		}else{
			jsonDevicePolicy = "<empty/>";
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
            je.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		} finally{
			
			try {
			    if (br != null) {
			    	br.close();
			    }
			  }catch (IOException e) {
			    e.printStackTrace();
			  
			  }
			try {
				if (in != null) {
				   	in.close();
				}
			}catch (IOException e) {
			    e.printStackTrace();
			  
			}
			try {
				if (is != null) {
				   	is.close();
				}
			}catch (IOException e) {
			    e.printStackTrace();
			  
			}  			
		}
        
        return jsonDevicePolicy;
	}
	
	private String getJSONDevicePolicy(Decision decision, String action){
		String jsonDevicePolicy = null;
		BufferedReader br = null;
		InputStream in = null;
		InputStreamReader is = null;
		String policyContent = null;
		try {
			policyContent = getPolicyDTHeader();
			policyContent += getActionSection(decision, action);
			policyContent += getPolicyDTBottom();
            JSONObject xmlJSONObj = XML.toJSONObject(policyContent);
            jsonDevicePolicy = xmlJSONObj.toString();
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e){
        	jsonDevicePolicy = "<errorBuildingPolicy/>";
        } finally{			
			try {
			    if (br != null) {
			    	br.close();
			    }
			  }catch (IOException e) {
			    e.printStackTrace();
			  
			  }
			try {
				if (in != null) {
				   	in.close();
				}
			}catch (IOException e) {
			    e.printStackTrace();
			  
			}
			try {
				if (is != null) {
				   	is.close();
				}
			}catch (IOException e) {
			    e.printStackTrace();
			  
			}  			
		}
        
        return jsonDevicePolicy;
	}
	
	private String getJSONDevicePolicy(Decision decision, String action, Asset asset){
		String jsonDevicePolicy = null;
		BufferedReader br = null;
		InputStream in = null;
		InputStreamReader is = null;
		String policyContent = null;
		try {
			policyContent = getPolicyDTHeader();
			policyContent += getActionSection(decision, action, asset);
			policyContent += getPolicyDTBottom();
            JSONObject xmlJSONObj = XML.toJSONObject(policyContent);
            jsonDevicePolicy = xmlJSONObj.toString();
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (Exception e){
        	jsonDevicePolicy = "<errorBuildingPolicy/>";
        } finally{			
			try {
			    if (br != null) {
			    	br.close();
			    }
			  }catch (IOException e) {
			    e.printStackTrace();
			  
			  }
			try {
				if (in != null) {
				   	in.close();
				}
			}catch (IOException e) {
			    e.printStackTrace();
			  
			}
			try {
				if (is != null) {
				   	is.close();
				}
			}catch (IOException e) {
			    e.printStackTrace();
			  
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
	
	private String getActionSection(Decision decision, String action){
		String result = null;
		
		result = "<files>";
		result += "<action>";
		result += "<type>"+action+"</type>";
		if (decision.equals(Decision.GRANTED_ACCESS)){
			result += "<allow><!-- Allow these URLs (could be regular expressions) -->";
			result += "<id></id>"; //TODO Add resource identification
			result += "</allow>";
		}else if (decision.equals(Decision.STRONG_DENY_ACCESS)){
			result += "<deny><!-- Allow these URLs (could be regular expressions) -->";
			result += "<id></id>"; //TODO Add resource identification
			result += "</deny>";
		}else if (decision.equals(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS)){
			result += "<deny><!-- Allow these URLs (could be regular expressions) -->";
			result += "<id></id>"; //TODO Add resource identification
			result += "</deny>";
		}
		result += "</action>";
		result += "</files>";		
		
		return result;
	}
	
	private String getActionSection(Decision decision, String action, Asset asset){
		String result = null;
		
		result = "<files>";
		result += "<action>";
		result += "<type>"+action+"</type>";
		if (decision.equals(Decision.GRANTED_ACCESS)){
			result += "<allow><!-- Allow these URLs (could be regular expressions) -->";
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
			}			
			result += "</allow>";
		}else if (decision.equals(Decision.STRONG_DENY_ACCESS)){
			result += "<deny><!-- Allow these URLs (could be regular expressions) -->";
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
			}	
			result += "</deny>";
		}else if (decision.equals(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS)){
			result += "<deny><!-- Allow these URLs (could be regular expressions) -->";
			if ((asset != null)){
				result += "<id>"+asset.getId()+"</id>";
				result += "<path>"+asset.getLocation()+"</path>";
				if (decision.getCondition()!=null){
					result += "<condition>"+decision.getCondition()+"</condition>";
				}
			}	
			result += "</deny>";
		}
		result += "</action>";
		result += "</files>";		
		
		return result;
	}
	

}
