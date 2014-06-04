package eu.musesproject.server.rt2ae;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 UNIGE
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


import java.util.List;
import java.util.Random;

import eu.musesproject.client.model.actuators.RiskCommunication;
import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceSecurityState;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.RiskTreatment;
import eu.musesproject.server.risktrust.Rt2ae;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.Threat;
import eu.musesproject.server.risktrust.TrustValue;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;

import org.apache.log4j.Logger;


public class Rt2aeServerImpl implements Rt2ae {

	private final int RISK_TREATMENT_SIZE = 20;
	private Logger logger = Logger.getLogger(Rt2aeServerImpl.class.getName());

	
	/**

	* DecideBasedOnRiskPolicy is a function whose aim is to compute a Decision based on RiskPolicy.

	* @param accessRequest the access request

	* @param context the context

	*/  
	@SuppressWarnings({ "null", "static-access" })
	@Override
	public Decision decideBasedOnRiskPolicy(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub  
		
		return decideBasedOnRiskPolicy_version_5(accessRequest, context);
	}  
      
	/**  
	 * This function is the version 1 of the decideBasedOnRiskPolicy. This version computes the Decision based on the Context and the AccessRequest
	 * 
	 * @param accessRequest
	 * @param context            
	 * @return   
	 */
	public Decision decideBasedOnRiskPolicy_version_1(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
		
		if (accessRequest.getRequestedCorporateAsset().getValue() <= 1000000 ) {
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
			
			Random r = new Random();
			int valeur = 0 + r.nextInt(100 - 0);

			accessRequest.getUser().getUsertrustvalue().setValue(valeur);
			accessRequest.getDevice().getDevicetrustvalue().setValue(valeur);
			
			List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			if (threats.isEmpty()){			
				
				Decision decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
				RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
				if (riskTreatments.length > 0){
					riskTreatments[0] = riskTreatment;	
				}				
				riskCommunication.setRiskTreatment(riskTreatments);
				//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
				return decision;
				
			}
			
		}
		else
		{
			
			Decision decision = Decision.STRONG_DENY_ACCESS;
			return decision;
		}
	}
	
	
	/**
	 * This function is the version 2 of the decideBasedOnRiskPolicy. This version computes the Decision based on the Context and the list of Threats
	 * 
	 * @param accessRequest
	 * @param context
	 * @return
	 */
	public Decision decideBasedOnRiskPolicy_version_2(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
		
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			
			
			if (threats.isEmpty()){			
				Decision decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						//System.out.println(threats.get(i));
						if(threats.get(i).getType() == "Wi-Fi sniffing" && threats.get(i).getProbability()>0.5){ 
								
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							return decision;
						
						}
						if(threats.get(i).getType() == "Malware" && threats.get(i).getProbability()>0.5){
							
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							
						}
						if(threats.get(i).getType() == "Spyware" && threats.get(i).getProbability()>0.5){ 
							
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							
						}
						if(threats.get(i).getType() == "Unsecure Connexion" && threats.get(i).getProbability()>0.5 ){ 
							
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							return decision;
							
						}
						if(threats.get(i).getType() == "Jailbroken" && threats.get(i).getProbability()>0.5){ 
							
							Decision decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()>0.5){ 
							
							Decision decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						
						Decision decision = Decision.GRANTED_ACCESS;; 
						return decision;
						
						/*if(threats.get(i).getType()=="Phishing scams"){ 
							
						
						}*/
					
					}
				}
				
				
				
				
				
			}
			Decision decision = Decision.GRANTED_ACCESS;; 
			return decision;
			
			
			/*Decision decision = Decision.STRONG_DENY_ACCESS;
			return decision;*/
		
	}
	
	
	/**
	 * This function is the version 3 of the decideBasedOnRiskPolicy. This version computes the Decision based on the Context and the list of Threats and the Outcome
	 * 
	 * @param accessRequest
	 * @param context
	 * @return
	 */
	@SuppressWarnings({ "unused", "static-access" })
	public Decision decideBasedOnRiskPolicy_version_3(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
		
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			
			
			if (threats.isEmpty()){			
				Decision decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						//System.out.println(threats.get(i));
						if(threats.get(i).getType() == "Wi-Fi sniffing" && threats.get(i).getProbability()>0.5){ 
							
							Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							
							if(probability.getValue()<=0.5){	
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5% of chances that the asset that you wan to access will lose 0.5% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	

								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
								decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
						if(threats.get(i).getType() == "Malware" && threats.get(i).getProbability()>0.5){
							Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							if(probability.getValue()<=0.5){
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
								RiskTreatment riskTreatment1 = new RiskTreatment("TThere is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	

								}		
							
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
						if(threats.get(i).getType() == "Spyware" && threats.get(i).getProbability()>0.5){ 
							Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							
							if(probability.getValue()<=0.5){
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;
									riskTreatments[1] = riskTreatment1;	

								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
							
						}
						if(threats.get(i).getType() == "Unsecure Connexion" && threats.get(i).getProbability()>0.5 ){ 
							Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/3);

							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							if(probability.getValue()<=0.5){
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	

								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
								decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
							
						}
						if(threats.get(i).getType() == "Jailbroken" && threats.get(i).getProbability()>0.5){ 
							
							Decision decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()>0.5){ 
							
							Decision decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						
						Decision decision = Decision.GRANTED_ACCESS;; 
						return decision;
							
					}
				}
				
			}
			Decision decision = Decision.GRANTED_ACCESS;; 
			return decision;
			
			
			
		
	}
	
	
	/**
	 * This function is the version 4 of the decideBasedOnRiskPolicy. This version computes the Decision based on the Context and the list of Threats and the Outcome and the trust value
	 * 
	 * @param accessRequest
	 * @param context
	 * @return
	 */
	@SuppressWarnings({ "unused", "static-access" })
	public Decision decideBasedOnRiskPolicy_version_4(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
		
			Random r = new Random();
			int valeur = 0 + r.nextInt(100 - 0);
	
			accessRequest.getUser().getUsertrustvalue().setValue(valeur);
			accessRequest.getDevice().getDevicetrustvalue().setValue(valeur);
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			
			if (threats.isEmpty()){			
				Decision decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						//System.out.println(threats.get(i));
						if(threats.get(i).getType() == "Wi-Fi sniffing" && threats.get(i).getProbability()>0.5){ 
							
							Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							
							if(probability.getValue()<=0.5){	
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5% of chances that the asset that you wan to access will lose 0.5% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	

								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
								decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								if(accessRequest.getUser().getUsertrustvalue().getValue() >0.5 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.5){
									
									eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
									RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
									RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
									RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5% of chances that the asset that you wan to access will lose 0.5% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");

									if (riskTreatments.length > 0){
										riskTreatments[0] = riskTreatment;	
										riskTreatments[1] = riskTreatment1;	

									}				
									riskCommunication.setRiskTreatment(riskTreatments);
									//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
									Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
									decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
									
									
								}else{
									Decision decision = Decision.STRONG_DENY_ACCESS;
									return decision;
								}
								
							}
						}
						if(threats.get(i).getType() == "Malware" && threats.get(i).getProbability()>0.5){
							Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							if(probability.getValue()<=0.5){
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	

								}		
							
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
						if(threats.get(i).getType() == "Spyware" && threats.get(i).getProbability()>0.5){ 
							Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							
							if(probability.getValue()<=0.5){
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;
									riskTreatments[1] = riskTreatment1;	

								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
							
						}
						if(threats.get(i).getType() == "Unsecure Connexion" && threats.get(i).getProbability()>0.5 ){ 
							Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/3);

							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							if(probability.getValue()<=0.5){
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	

								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
								decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								
								if(accessRequest.getUser().getUsertrustvalue().getValue() >0.5 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.5){
									
									eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
									RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
									RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
									RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5% of chances that the asset that you wan to access will lose 0.5% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
									if (riskTreatments.length > 0){
										riskTreatments[0] = riskTreatment;	
										riskTreatments[1] = riskTreatment1;	
	
									}				
									riskCommunication.setRiskTreatment(riskTreatments);
									//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
									Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
									decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								
									//decision.getRiskCommunication().getRiskTreatment()[1].getTextualDescription();
								}else{
									Decision decision = Decision.STRONG_DENY_ACCESS;
									return decision;
								}
							}
							
						}
						if(threats.get(i).getType() == "Jailbroken" && threats.get(i).getProbability()>0.5){ 
							
							Decision decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()>0.5){ 
							
							Decision decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						
						Decision decision = Decision.GRANTED_ACCESS;; 
						return decision;
							
					}
				}
				
			}
			Decision decision = Decision.GRANTED_ACCESS;; 
			return decision;
			
			
			/*Decision decision = Decision.STRONG_DENY_ACCESS;
			return decision;*/
		
	}
	
	
	

	
	
	
	
	
	
	
	
	/**
	 * This function is the version of the decideBasedOnRiskPolicy for the demo Demo_Hambourg. 
	 * 
	 * @param accessRequest
	 * @param context
	 * @return
	 */
	
	public Decision decideBasedOnRiskPolicy_version_Demo_Hambourg(AccessRequest accessRequest, ConnectivityEvent connEvent) {
		// TODO Auto-generated method stub
		if (!connEvent.getWifiEncryption().equals("WPA2")){
			eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
			RiskTreatment [] riskTreatments = new RiskTreatment[1];
			RiskTreatment riskTreatment = new RiskTreatment("Action not allowed. Please, change WIFI encryption to WPA2");
			
			riskTreatments[0] = riskTreatment;	
			riskCommunication.setRiskTreatment(riskTreatments);
			Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
			decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
			logger.info("Decision: MAYBE_ACCESS");
			logger.info("RiskTreatment: Action not allowed. Please, change WIFI encryption to WPA2");
			return decision;
		}else{
	
			Decision decision = Decision.GRANTED_ACCESS;
			logger.info("Decision: GRANTED_ACCESS");
			return decision;
		}
		
	}
	
	/**
	 * This function is the version 5 of the decideBasedOnRiskPolicy. This version computes the Decision based on the value of the Asset,the Context and the list of Threats and the Outcome and the trust value
	 * 
	 * @param accessRequest
	 * @param context
	 * @return
	 */
	
	public Decision decideBasedOnRiskPolicy_version_5(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
			//public,internal,confidentiality,strictlyconfidential
			
			List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			if(accessRequest.getRequestedCorporateAsset().getConfidential_level()=="public"){
				Decision decision = Decision.GRANTED_ACCESS;
				logger.info("Decision: GRANTED_ACCESS");
				return decision;	
			}
			
			if(accessRequest.getRequestedCorporateAsset().getConfidential_level()=="internal"){
				if (threats.isEmpty()){			
					Decision decision = Decision.GRANTED_ACCESS;
					logger.info("Decision: GRANTED_ACCESS");
					return decision;
				}else{
					return computeDecisionInternalAsset( accessRequest, context);

				}
				
			}

			if(accessRequest.getRequestedCorporateAsset().getConfidential_level()=="confidential"){
				if (!threats.isEmpty()){			
					Decision decision = Decision.GRANTED_ACCESS;
					logger.info("Decision: GRANTED_ACCESS");
					return decision;
				}else{
					return computeDecisionConfidentialAsset(accessRequest, context);

				}
				
			}
			
			if(accessRequest.getRequestedCorporateAsset().getConfidential_level()=="strictlyconfidential"){
				if (threats.isEmpty()){			
					Decision decision = Decision.GRANTED_ACCESS;
					logger.info("Decision: GRANTED_ACCESS");
					return decision;
				}else{
				return computeDecisionStrictlyConfidentialAsset( accessRequest, context);
				}
			}
			
			Decision decision = Decision.GRANTED_ACCESS;
			logger.info("Decision: GRANTED_ACCESS");
			return decision;
			
			
			/*Decision decision = Decision.STRONG_DENY_ACCESS;
			return decision;*/
		
	}
	
	
	/**

	* WarnDeviceSecurityStateChange is a function whose aim is to check that if the DeviceSecurityState has changed.

	* @param deviceSecurityState the device security state

	*/
	@Override
	public void warnDeviceSecurityStateChange(DeviceSecurityState deviceSecurityState) {
		// TODO Auto-generated method stub

		if( deviceSecurityState == null)
		{
			
			System.out.println(" The deviceSecurityState object is null");
		}
		else{
			
			System.out.println(" The deviceSecurityState object is not null");
		}
		
	}

	/**

	* WarnUserSeemsInvolvedInSecurityIncident is a function whose aim is to check that if the user seems involved in security incident.

	* @param user the user

	* @param probability the probability

	* @param securityIncident the security incident

	*/
	@Override
	public void warnUserSeemsInvolvedInSecurityIncident(User user,Probability probability, SecurityIncident securityIncident) {
		// TODO Auto-generated method stub
			
			Random r = new Random();
			double assetvalue = 0 + r.nextInt(1000000);
			/**
			 * asset.getvalue():securityIncident.getCostBenefit()
			 */			
			if(securityIncident.getCostBenefit() == 0){
				/**
				 * the security incident has not cost
				 */	
				
			}else {
				/**
				 * security incident has a cost
				 */	
				double pourcentage = securityIncident.getCostBenefit()/assetvalue;
				UserTrustValue u = new UserTrustValue();
				u.setValue(user.getUsertrustvalue().getValue()-user.getUsertrustvalue().getValue()*pourcentage);
				user.setUsertrustvalue(u);
	
				
			}
						
		

	}
	
	
	
	
	public Decision computeDecisionInternalAsset(AccessRequest accessRequest,Context context){
		
		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				//System.out.println(threats.get(i));
				if(threats.get(i).getType() == "Wi-Fi sniffing" && threats.get(i).getProbability()<=0.5){ 
					
					Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.5){	
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is less that 50% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							logger.info("Decision: MAYBE_ACCESS");
							return decision;
						}else{
							if(accessRequest.getUser().getUsertrustvalue().getValue() >0.5 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.5){
								
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is more than 50% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	
	
								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
								
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
							
						}
					}
				}
				if(threats.get(i).getType() == "Malware" && threats.get(i).getProbability()<=0.5){
					Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/3);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.5){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50% of chances that the asset that you wan to access will lose around 33% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}		
						
						riskCommunication.setRiskTreatment(riskTreatments);
						//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
						Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
						decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
						logger.info("Decision: UPTOYOU_ACCESS");
						return decision;
						}else{
							Decision decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType() == "Spyware" && threats.get(i).getProbability()<0.3){ 
					Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.5){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5%  of chances that the asset that you wan to access will lose 0.5% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							logger.info("Decision: UPTOYOU_ACCESS");
							return decision;
						}else{
							Decision decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
					
				}
				if(threats.get(i).getType() == "Unsecure Connexion" && threats.get(i).getProbability()<0.5 ){ 
					Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/5);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.5){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50%  of chances that the asset that you wan to access will lose 20% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							logger.info("Decision: MAYBE_ACCESS");
							return decision;
						}else{
							
							if(accessRequest.getUser().getUsertrustvalue().getValue() >0.5 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.5){
								
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5% of chances that the asset that you wan to access will lose 0.5% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	
	
								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
							
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
						}
					}
					
				}
				if(threats.get(i).getType() == "Jailbroken" && threats.get(i).getProbability()>0){ 
					
					Decision decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()>0){ 
					
					Decision decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				
				Decision decision = Decision.STRONG_DENY_ACCESS;
				logger.info("Decision: STRONG_DENY_ACCESS");
				return decision;
					
			}
		}
		Decision decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
	}
	
	
	
	public Decision computeDecisionConfidentialAsset(AccessRequest accessRequest,Context context){
		
		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		Outcome requestPotentialOutcomes = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
		Probability probabilitys = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcomes, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				//System.out.println(threats.get(i));
				if(threats.get(i).getType() == "Wi-Fi sniffing" && threats.get(i).getProbability()<0.3){ 
					
					Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.3){	
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 30% of chances that the asset that you want to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							logger.info("Decision: MAYBE_ACCESS");
							return decision;
						}else{
							if(accessRequest.getUser().getUsertrustvalue().getValue() >0.7 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.7){
								
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 0.5% of chances that the asset that you wan to access will lose 0.5% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	
	
								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
								
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
							
						}
					}
				}
				if(threats.get(i).getType() == "Malware" && threats.get(i).getProbability()<0.3){
					Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.3){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 30%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}		
						
						riskCommunication.setRiskTreatment(riskTreatments);
						//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
						Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
						decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
						logger.info("Decision: UPTOYOU_ACCESS");
						return decision;
						}else{
							Decision decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType() == "Spyware" && threats.get(i).getProbability()<0.2){ 
					Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: UPTOYOU_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.3){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 30%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							logger.info("Decision: UPTOYOU_ACCESS");
							return decision;
						}else{
							Decision decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType() == "Unsecure Connexion" && threats.get(i).getProbability()<0.3 ){ 
					Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/5);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.3){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 30%  of chances that the asset that you wan to access will lose 20% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							logger.info("Decision: MAYBE_ACCESS");
							return decision;
						}else{
							
							if(accessRequest.getUser().getUsertrustvalue().getValue() >0.7 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.7){
								
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 30% of chances that the asset that you wan to access will lose 20% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	
	
								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
							
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
						}
					}
					
				}
				if(threats.get(i).getType() == "Jailbroken" && threats.get(i).getProbability()<0.3){ 
					
					Decision decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()<0.3){ 
					
					Decision decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				
				Decision decision = Decision.STRONG_DENY_ACCESS;
				logger.info("Decision: STRONG_DENY_ACCESS");

				return decision;
					
			}
		}
		Decision decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
	}
	
	
	public Decision computeDecisionStrictlyConfidentialAsset(AccessRequest accessRequest,Context context){
		
		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				//System.out.println(threats.get(i));
				if(threats.get(i).getType() == "Wi-Fi sniffing" && threats.get(i).getProbability()<0.1){ 
					
					Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.1){	
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 10% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							logger.info("Decision: MAYBE_ACCESS");
							return decision;
						}else{
							if(accessRequest.getUser().getUsertrustvalue().getValue() >0.5 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.5){
								
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 10% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	
	
								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
								
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
							
						}
					}
				}
				if(threats.get(i).getType() == "Malware" && threats.get(i).getProbability()<0.1){
					Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.1){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 10%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}		
						
						riskCommunication.setRiskTreatment(riskTreatments);
						//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
						Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
						decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
						logger.info("Decision: UPTOYOU_ACCESS");
						return decision;
						}else{
							Decision decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType() == "Spyware" && threats.get(i).getProbability()>0.1){ 
					Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.1){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 10%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							logger.info("Decision: UPTOYOU_ACCESS");
							return decision;
						}else{
							Decision decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType() == "Unsecure Connexion" && threats.get(i).getProbability()>0.1 ){ 
					Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/3);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						Decision decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.1){
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							RiskTreatment riskTreatment1 = new RiskTreatment("There is around 10%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");
	
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
								riskTreatments[1] = riskTreatment1;	
	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
							Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							logger.info("Decision: MAYBE_ACCESS");
							return decision;
						}else{
							
							if(accessRequest.getUser().getUsertrustvalue().getValue() >0.9 && accessRequest.getDevice().getDevicetrustvalue().getValue()>0.9){
								
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 10% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
								if (riskTreatments.length > 0){
									riskTreatments[0] = riskTreatment;	
									riskTreatments[1] = riskTreatment1;	
	
								}				
								riskCommunication.setRiskTreatment(riskTreatments);
								//Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
								Decision decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
							
							}else{
								Decision decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
						}
					}
				}
				if(threats.get(i).getType() == "Jailbroken" && threats.get(i).getProbability()<0.1){ 
					
					Decision decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()<0.1){ 
					
					Decision decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				
				Decision decision = Decision.STRONG_DENY_ACCESS;
				logger.info("Decision: STRONG_DENY_ACCESS");
				return decision;
					
			}
		}
		Decision decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
	}
	
	/**
	 * First version of computeOutcomeProbability. This version just return a probability 
	 * about the Outcome by setting a random value to the probability
	 *  
	 * 
	 * */
	public Probability computeOutcomeProbability(Outcome requestPotentialOutcome, AccessRequest accessRequest,UserTrustValue userTrustValue, DeviceTrustValue deviceTrustValue) {
		// TODO Auto-generated method stub
		Probability probability = new Probability();
		probability.setEventname(requestPotentialOutcome.getDescription());
		
		Random r = new Random();
		double randomValue =  r.nextDouble();
		probability.setValue(randomValue);
		return probability;
	}
	
	public static void main (String [] arg){
		
		Rt2aeServerImpl rt2ae = new Rt2aeServerImpl();

		rt2ae = new Rt2aeServerImpl();
		
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setId(1);
		User user = new User();
		UserTrustValue usertrustvalue = new UserTrustValue();  
		usertrustvalue.setValue(0);
		user.setUsertrustvalue(usertrustvalue);
		accessRequest.setUser(user);
		Device device = new Device();
		DeviceTrustValue devicetrustvalue = new DeviceTrustValue();
		devicetrustvalue.setValue(0);
		device.setDevicetrustvalue(devicetrustvalue);    
		accessRequest.setDevice(device);
		      
		Asset requestedCorporateAsset = new Asset();  
		requestedCorporateAsset.setValue(1000000);
		requestedCorporateAsset.setConfidential_level("confidential");

		accessRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		Context context = new Context();

		
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy_version_5(accessRequest, context);

		
   }
	
	
	
	

}
