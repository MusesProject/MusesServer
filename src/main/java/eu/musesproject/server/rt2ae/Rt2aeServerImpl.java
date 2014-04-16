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
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.DeviceSecurityState;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.RiskTreatment;
import eu.musesproject.server.risktrust.Rt2ae;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.Threat;
import eu.musesproject.server.risktrust.User;

public class Rt2aeServerImpl implements Rt2ae {

	private final int RISK_TREATMENT_SIZE = 20;
	
	/**

	* DecideBasedOnRiskPolicy is a function whose aim is to compute a Decision based on RiskPolicy.

	* @param accessRequest the access request

	* @param context the context

	*/  
	@SuppressWarnings({ "null", "static-access" })
	@Override
	public Decision decideBasedOnRiskPolicy(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub  
		
		return decideBasedOnRiskPolicy_version_4(accessRequest, context);
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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");

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
								RiskTreatment riskTreatment1 = new RiskTreatment("TThere is around 50%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

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
		
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Threat> threats  = eventprocessorimpl.getCurrentThreats(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			//Probability responsePotentialOutcome = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			Random r = new Random();
			int valeur = 0 + r.nextInt(100 - 0);

			accessRequest.getUser().getUsertrustvalue().setValue(valeur);
			accessRequest.getDevice().getDevicetrustvalue().setValue(valeur);
			
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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");

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
								if(accessRequest.getUser().getUsertrustvalue().getValue() >50 && accessRequest.getDevice().getDevicetrustvalue().getValue()>50){
									
									eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
									RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
									RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
									RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");

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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

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
								RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50%  of chances that the asset that you wan to access will lose 50% of this value if you access it with this device,please scan you device with an Antivirus or use another device");

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
								
								if(accessRequest.getUser().getUsertrustvalue().getValue() >50 && accessRequest.getDevice().getDevicetrustvalue().getValue()>50){
									
									eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
									RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
									RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
									RiskTreatment riskTreatment1 = new RiskTreatment("There is around 50% of chances that the asset that you wan to access will lose 50% of this value if you access it with by using this Wi-Fi connection,please try to connect to a secure Wi-Fi connection");
	
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
		

	}

}
