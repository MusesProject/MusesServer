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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Clue;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceSecurityState;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.PolicyCompliance;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.RiskTreatment;
import eu.musesproject.server.risktrust.Rt2ae;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.Threat;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;

public class Rt2aeServerImpl implements Rt2ae {

	private final int RISK_TREATMENT_SIZE = 20;
	private Logger logger = Logger.getLogger(Rt2aeServerImpl.class.getName());
	private DBManager dbManager = new DBManager(ModuleType.RT2AE);
	private RiskPolicy riskPolicy;

	/**

	* DecideBasedOnRiskPolicy is a function whose aim is to compute a Decision based on RiskPolicy.

	* @param accessRequest the access request

	* @param context the context

	*/  
	@SuppressWarnings({ "null", "static-access" })
	@Override
	public Decision decideBasedOnRiskPolicy(AccessRequest accessRequest, PolicyCompliance policyCompliance, Context context) {
		// TODO Auto-generated method stub  

		return decideBasedOnRiskPolicy_testing_version(accessRequest,policyCompliance, context);
	}  
	
	/**
	 * 
	 * This function is the version 6 of the decideBasedOnRiskPolicy. This
	 * version computes the Decision based on the AccessRequest computing the
	 * threats and their probabilities as well as accepting opportunities as
	 * well if needed. It stores certain elements in the DB if needed. It also
	 * compares against a risk policy.
	 * 
	 * @param accessRequest
	 *            the access request
	 * @return Decision
	 * 
	 */
	public Decision decideBasedOnRiskPolicy_version_6(
			AccessRequest accessRequest, RiskPolicy rPolicy) {

		// function variables and assignments

		double costOpportunity = 0.0;
		double combinedProbabilityThreats = 1.0;
		double combinedProbabilityOpportunities = 1.0;
		double singleThreatProbabibility = 0.0;
		double singleOpportunityProbability = 0.0;
		int opcount = 0;
		int threatcount = 0;

		riskPolicy = rPolicy;
		EventProcessorImpl eventProcessorImpl = new EventProcessorImpl();

		List<Asset> requestedAssets = new ArrayList<Asset>(
				Arrays.asList(accessRequest.getRequestedCorporateAsset()));

		List<Clue> clues = new ArrayList<Clue>();

		// infer clues from the access request

		for (Asset asset : requestedAssets) {

			clues = eventProcessorImpl.getCurrentClues(accessRequest,
					accessRequest.getUser().getUsertrustvalue(), accessRequest
							.getDevice().getDevicetrustvalue());

			Clue userName = new Clue();
			userName.setName(accessRequest.getUser().toString()); // TODO
																	// temporary
																	// solution,
																	// users
																	// must have
																	// a
																	// nickname
																	// or unique
																	// identifier!!
			clues.add(userName);

			Clue assetName = new Clue();
			assetName.setName(asset.getTitle());
			clues.add(assetName);

			for (Clue clue : clues) {
				logger.info("The clue associated with Asset "
						+ asset.getTitle() + " is " + clue.getName() + "\n");
			}
		}

		List<eu.musesproject.server.entity.Threat> currentThreats = new ArrayList<eu.musesproject.server.entity.Threat>();
		String threatName = "";

		// combine clues with the asset and the user to generate a single threat

		for (Clue clue : clues) {

			threatName = threatName + clue.getName();

		}

		eu.musesproject.server.entity.Threat threat = new eu.musesproject.server.entity.Threat();
		threat.setDescription("Threat" + threatName);
		threat.setProbability(0.5);
		eu.musesproject.server.entity.Outcome o = new eu.musesproject.server.entity.Outcome();
		o.setDescription("Compromised Asset");
		o.setCostbenefit(-requestedAssets.iterator().next().getValue());
		threat.setOutcomes(new ArrayList<eu.musesproject.server.entity.Outcome>(
				Arrays.asList(o)));

		// check if the threat already exists in the database

		boolean exists = false;
		List<eu.musesproject.server.entity.Threat> dbThreats = dbManager
				.getThreats();
		eu.musesproject.server.entity.Threat existingThreat = new eu.musesproject.server.entity.Threat();

		for (eu.musesproject.server.entity.Threat threat2 : dbThreats) {
			if (threat2.getDescription().equalsIgnoreCase(
					threat.getDescription())) {
				exists = true;
				existingThreat = threat2;
			}
		}

		// if doesn't exist, insert a new one

		if (!exists) {

			int oC = threat.getOccurences() + 1;
			threat.setOccurences(oC);
			currentThreats.add(threat);
			dbManager.setThreats(currentThreats);

			logger.info("The newly created Threat from the Clues is: "
					+ threat.getDescription() + " with probability "
					+ threat.getProbability()
					+ " for the following outcome: \""
					+ threat.getOutcomes().iterator().next().getDescription()
					+ "\" with the following potential cost (in kEUR): "
					+ threat.getOutcomes().iterator().next().getCostbenefit()
					+ "\n");

			// if already exists, update occurrences and update it in the
			// database

		} else {

			int oC = existingThreat.getOccurences() + 1;
			existingThreat.setOccurences(oC);
			currentThreats.add(existingThreat);

			logger.info("Occurences: " + existingThreat.getOccurences()
					+ " - Bad Count: " + existingThreat.getBadOutcomeCount());

			dbManager.setThreats(currentThreats);

			logger.info("The inferred Threat from the Clues is: "
					+ existingThreat.getDescription()
					+ " with probability "
					+ existingThreat.getProbability()
					+ " for the following outcome: \""
					+ existingThreat.getOutcomes().iterator().next()
							.getDescription()
					+ "\" with the following potential cost (in kEUR): "
					+ existingThreat.getOutcomes().iterator().next()
							.getCostbenefit() + "\n");

		}

		// infer some probabilities from the threats and opportunities (if
		// present)

		for (eu.musesproject.server.entity.Threat t : currentThreats) {

			costOpportunity += t.getOutcomes().iterator().next()
					.getCostbenefit();

			if (t.getOutcomes().iterator().next().getCostbenefit() < 0) {

				combinedProbabilityThreats = combinedProbabilityThreats
						* t.getProbability();
				singleThreatProbabibility = singleThreatProbabibility
						+ t.getProbability();
				threatcount++;

			} else {

				combinedProbabilityOpportunities = combinedProbabilityOpportunities
						* t.getProbability();
				singleOpportunityProbability = singleOpportunityProbability
						+ t.getProbability();
				opcount++;

			}
		}

		if (threatcount > 1)
			singleThreatProbabibility = singleThreatProbabibility
					- combinedProbabilityThreats;
		if (opcount > 1)
			singleOpportunityProbability = singleOpportunityProbability
					- combinedProbabilityOpportunities;

		// log some useful info

		logger.info("Decission data is: ");
		logger.info("- Risk Policy threshold: " + riskPolicy.getRiskvalue());
		logger.info("- Cost Oportunity: " + costOpportunity);
		logger.info("- Combined Probability of the all possible Threats happening together: "
				+ combinedProbabilityThreats);
		logger.info("- Combined Probability of the all the possible Opportunities happening together: "
				+ combinedProbabilityOpportunities);
		logger.info("- Combined Probability of only one of the possible Threats happening: "
				+ singleThreatProbabibility);
		logger.info("- Combined Probability of only one of the possible Opportunities happening: "
				+ singleOpportunityProbability);
		logger.info("Making a decision...");
		logger.info(".");
		logger.info("..");
		logger.info("...");

		// compute the decision based on the risk policy, the threat
		// probabilities, the user trust level and the cost benefit

		if (riskPolicy.getRiskvalue() == 0.0) {

			return Decision.GRANTED_ACCESS;

		}

		if (riskPolicy.getRiskvalue() == 1.0) {

			return Decision.STRONG_DENY_ACCESS;

		}

		if ((combinedProbabilityThreats + ((Double) 1.0 - accessRequest
				.getUser().getUsertrustvalue().getValue())) / 2 <= riskPolicy
				.getRiskvalue()) {

			return Decision.GRANTED_ACCESS;

		} else {

			if (costOpportunity > 0)

				return Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;

			else

				return Decision.STRONG_DENY_ACCESS;

		}
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
		Decision decision = Decision.STRONG_DENY_ACCESS;
		
		if (accessRequest.getRequestedCorporateAsset().getValue() <= 1000000 ) {
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
			
			Random r = new Random();
			int valeur = 0 + r.nextInt(100 - 0);

			accessRequest.getUser().getUsertrustvalue().setValue(valeur);
			accessRequest.getDevice().getDevicetrustvalue().setValue(valeur);
			
			List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			if (threats.isEmpty()){			
				
				decision = Decision.GRANTED_ACCESS; 
				return decision;
			}else{
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
				RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
				if (riskTreatments.length > 0){
					riskTreatments[0] = riskTreatment;	
				}				
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
				return decision;
				
			}
			
		}
		else
		{
			
			decision = Decision.STRONG_DENY_ACCESS;
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
			
			Decision decision = Decision.STRONG_DENY_ACCESS;

			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Clue> clue  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
	
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			
			
			
			if (threats.isEmpty()){			
				 decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						if(threats.get(i).getType().equalsIgnoreCase("Wi-Fi sniffing")  && threats.get(i).getProbability()>0.5){ 
								
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							return decision;
						
						}
						if(threats.get(i).getType().equalsIgnoreCase("Malware") && threats.get(i).getProbability()>0.5){
							
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment1 = new RiskTreatment("Your device seems to have a Malware,please scan you device with an Antivirus or use another device");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment1;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Spyware") && threats.get(i).getProbability()>0.5){ 
							
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Spyware,please scan you device with an Antivirus or use another device");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Unsecure Connexion") && threats.get(i).getProbability()>0.5 ){ 
							
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
							if (riskTreatments.length > 0){
								riskTreatments[0] = riskTreatment;	
							}				
							riskCommunication.setRiskTreatment(riskTreatments);
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
							return decision;
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Jailbroken") && threats.get(i).getProbability()>0.5){ 
							
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Device under attack") && threats.get(i).getProbability()>0.5){ 
							
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						
						decision = Decision.GRANTED_ACCESS;; 
						return decision;
						
					
					
					}
				}
				
				
				
				
				
			}
			decision = Decision.GRANTED_ACCESS;; 
			return decision;
			
			
			
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
		
			Decision decision = Decision.STRONG_DENY_ACCESS;

			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			
			
			
			if (threats.isEmpty()){			
				decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						if(threats.get(i).getType().equalsIgnoreCase("Wi-Fi sniffing") && threats.get(i).getProbability()>0.5){ 
							
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
								decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
								decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
						if(threats.get(i).getType().equalsIgnoreCase("Malware") && threats.get(i).getProbability()>0.5){
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
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
						if(threats.get(i).getType().equalsIgnoreCase("Spyware") && threats.get(i).getProbability()>0.5){ 
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Unsecure Connexion") && threats.get(i).getProbability()>0.5 ){ 
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
								decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
								decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Jailbroken") && threats.get(i).getProbability()>0.5){ 
							
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Device under attack") && threats.get(i).getProbability()>0.5){ 
							
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						
						decision = Decision.GRANTED_ACCESS;; 
						return decision;
							
					}
				}
				
			}
			decision = Decision.GRANTED_ACCESS;; 
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
	public Decision decideBasedOnRiskPolicy_version_4(AccessRequest accessRequest, PolicyCompliance policyCompliance, Context context) {
		// TODO Auto-generated method stub
			
			Decision decision = Decision.STRONG_DENY_ACCESS;

			Random r = new Random();
			int valeur = 0 + r.nextInt(100 - 0);
	
			accessRequest.getUser().getUsertrustvalue().setValue(valeur);
			accessRequest.getDevice().getDevicetrustvalue().setValue(valeur);
			
			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
				
			List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			
			
			if (threats.isEmpty()){			
				decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						if(threats.get(i).getType().equalsIgnoreCase("Wi-Fi sniffing") && threats.get(i).getProbability()>0.5){ 
							
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
								decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
									decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
									decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
									
									
								}else{
									decision = Decision.STRONG_DENY_ACCESS;
									return decision;
								}
								
							}
						}
						if(threats.get(i).getType().equalsIgnoreCase("Malware") && threats.get(i).getProbability()>0.5){
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
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
						if(threats.get(i).getType().equalsIgnoreCase("Spyware") && threats.get(i).getProbability()>0.5){ 
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								return decision;
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Unsecure Connexion") && threats.get(i).getProbability()>0.5 ){ 
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
								decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
									decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
									decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								
								
								}else{
									decision = Decision.STRONG_DENY_ACCESS;
									return decision;
								}
							}
							
						}
						if(threats.get(i).getType().equalsIgnoreCase("Jailbroken") && threats.get(i).getProbability()>0.5){ 
							
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()>0.5){ 
							
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
							
						}
						
						decision = Decision.GRANTED_ACCESS;; 
						return decision;
							
					}
				}
				
			}
			decision = Decision.GRANTED_ACCESS;; 
			return decision;
			
			
			
		
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
			Decision decision = Decision.STRONG_DENY_ACCESS;

			EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
			
			List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
			
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			if(accessRequest.getRequestedCorporateAsset().getConfidential_level().equalsIgnoreCase("public")){
				decision = Decision.GRANTED_ACCESS;
				logger.info("Decision: GRANTED_ACCESS");
				
				return decision;	
			}
			
			if(accessRequest.getRequestedCorporateAsset().getConfidential_level().equalsIgnoreCase("internal")){
				if (threats.isEmpty()){			
					decision = Decision.GRANTED_ACCESS;
					logger.info("Decision: GRANTED_ACCESS");
					return decision;
				}else{
					return computeDecisionInternalAsset( accessRequest, context);

				}
				
			}

			if(accessRequest.getRequestedCorporateAsset().getConfidential_level().equalsIgnoreCase("confidential")){
				if (!threats.isEmpty()){			
					decision = Decision.GRANTED_ACCESS;
					logger.info("Decision: GRANTED_ACCESS");
					return decision;
				}else{
					return computeDecisionConfidentialAsset(accessRequest, context);

				}
				
			}
			
			if(accessRequest.getRequestedCorporateAsset().getConfidential_level().equalsIgnoreCase("strictlyconfidential")){
				if (threats.isEmpty()){			
					decision = Decision.GRANTED_ACCESS;
					logger.info("Decision: GRANTED_ACCESS");
					return decision;
				}else{
				return computeDecisionStrictlyConfidentialAsset( accessRequest, context);
				}
			}
			
			decision = Decision.GRANTED_ACCESS;
			logger.info("Decision: GRANTED_ACCESS");
			return decision;
			
		
	}
	
	
	
	
	
	
	
	public Decision computeDecisionInternalAsset(AccessRequest accessRequest,Context context){
		
		Decision decision = Decision.STRONG_DENY_ACCESS;

		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
		
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				if(threats.get(i).getType().equalsIgnoreCase("Wi-Fi sniffing")  && threats.get(i).getProbability()<=0.5){ 
					
					Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					
					if(probability == null){
						logger.info("Decision: STRONG_DENY_ACCESS");
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
							logger.info("Decision: MAYBE_ACCESS");
							decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication); 
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								logger.info("Decision: UPTOYOU_ACCESS");
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								return decision;
								
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
							
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Malware")  && threats.get(i).getProbability()<=0.5){
					Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/3);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
						decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
						logger.info("Decision: UPTOYOU_ACCESS");
						decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
						return decision;
						}else{
							decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Spyware") && threats.get(i).getProbability()<0.3){ 
					Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							logger.info("Decision: UPTOYOU_ACCESS");
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							return decision;
						}else{
							decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
					
				}
				if(threats.get(i).getType().equalsIgnoreCase("Unsecure Connexion") && threats.get(i).getProbability()<0.5 ){ 
					Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/5);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
							
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
						}
					}
					
				}
				if(threats.get(i).getType().equalsIgnoreCase("Jailbroken") && threats.get(i).getProbability()>0){ 
					
					decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()>0){ 
					
					decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				
				decision = Decision.STRONG_DENY_ACCESS;
				logger.info("Decision: STRONG_DENY_ACCESS");
				return decision;
					
			}
		}
		decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
	}
	
	
	
	public Decision computeDecisionConfidentialAsset(AccessRequest accessRequest,Context context){
		
		Decision decision = Decision.STRONG_DENY_ACCESS;

		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
		
		Outcome requestPotentialOutcomes = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
		Probability probabilitys = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcomes, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				if(threats.get(i).getType().equalsIgnoreCase("Wi-Fi sniffing") && threats.get(i).getProbability()<0.3){ 
					
					Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
								
								
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
							
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Malware") && threats.get(i).getProbability()<0.3){
					Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
						decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
						decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
						logger.info("Decision: UPTOYOU_ACCESS");
						return decision;
						}else{
							decision = Decision.STRONG_DENY_ACCESS;
							return decision;
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Spyware") && threats.get(i).getProbability()<0.2){ 
					Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
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
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							logger.info("Decision: UPTOYOU_ACCESS");
							return decision;
						}else{
							decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Unsecure Connexion") && threats.get(i).getProbability()<0.3 ){ 
					Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/5);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
							
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								return decision;
							}
						}
					}
					
				}
				if(threats.get(i).getType().equalsIgnoreCase("Jailbroken") && threats.get(i).getProbability()<0.3){ 
					
					decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				if(threats.get(i).getType()=="Device under attack" && threats.get(i).getProbability()<0.3){ 
					
					decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				
				decision = Decision.STRONG_DENY_ACCESS;
				logger.info("Decision: STRONG_DENY_ACCESS");
				return decision;
					
			}
		}
		decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
	}
	
	
	public Decision computeDecisionStrictlyConfidentialAsset(AccessRequest accessRequest,Context context){
		
		Decision decision = Decision.STRONG_DENY_ACCESS;

		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		List<Clue> clues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		
		List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
		
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				if(threats.get(i).getType().equalsIgnoreCase("Wi-Fi sniffing") && threats.get(i).getProbability()<0.1){ 
					
					Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing", -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
								
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
							
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Malware") && threats.get(i).getProbability()<0.1){
					Outcome requestPotentialOutcome = new Outcome("Malware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
						decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
						decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
						logger.info("Decision: UPTOYOU_ACCESS");
						return decision;
						}else{
							decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Spyware")  && threats.get(i).getProbability()>0.1){ 
					Outcome requestPotentialOutcome = new Outcome("Spyware", -accessRequest.getRequestedCorporateAsset().getValue()/2);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
							decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
							logger.info("Decision: UPTOYOU_ACCESS");
							return decision;
						}else{
							decision = Decision.STRONG_DENY_ACCESS;
							logger.info("Decision: STRONG_DENY_ACCESS");
							return decision;
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Unsecure Connexion") && threats.get(i).getProbability()>0.1 ){ 
					Outcome requestPotentialOutcome = new Outcome("Unsecure Connexion", -accessRequest.getRequestedCorporateAsset().getValue()/3);

					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
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
							decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
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
								decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
								decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication); 
								logger.info("Decision: UPTOYOU_ACCESS");
								return decision;
							
							}else{
								decision = Decision.STRONG_DENY_ACCESS;
								logger.info("Decision: STRONG_DENY_ACCESS");
								return decision;
							}
						}
					}
				}
				if(threats.get(i).getType().equalsIgnoreCase("Jailbroken") && threats.get(i).getProbability()<0.1){ 
					
					decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				if(threats.get(i).getType().equalsIgnoreCase("Device under attack") && threats.get(i).getProbability()<0.1){ 
					
					decision = Decision.STRONG_DENY_ACCESS;
					logger.info("Decision: STRONG_DENY_ACCESS");
					return decision;
					
				}
				
				decision = Decision.STRONG_DENY_ACCESS;
				logger.info("Decision: STRONG_DENY_ACCESS");
				return decision;
					
			}
		}
		decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
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
		Decision decision = Decision.STRONG_DENY_ACCESS;

		if (!connEvent.getWifiEncryption().equals("WPA2")){
			eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
			RiskTreatment [] riskTreatments = new RiskTreatment[1];
			RiskTreatment riskTreatment = new RiskTreatment("Action not allowed. Please, change WIFI encryption to WPA2");
			
			riskTreatments[0] = riskTreatment;	
			riskCommunication.setRiskTreatment(riskTreatments);
			decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
			decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
			logger.info("Decision: MAYBE_ACCESS");
			logger.info("RiskTreatment: Action not allowed. Please, change WIFI encryption to WPA2");
			return decision;
		}else{
	
			decision = Decision.GRANTED_ACCESS;
			logger.info("Decision: GRANTED_ACCESS");
			return decision;
		}
		
	}
	
	
	/**
	 * This function is the version of the decideBasedOnRiskPolicy for the demo Demo_Hambourg. 
	 * 
	 * @param accessRequest
	 * @param context
	 * @return  
	 */
	
	public Decision decideBasedOnRiskPolicy_testing_version(AccessRequest accessRequest, PolicyCompliance policyCompliance, Context context) {
		// TODO Auto-generated method stub
		Decision decision = Decision.STRONG_DENY_ACCESS;
		
		EventProcessorImpl eventprocessorimpl = new EventProcessorImpl();
		
		if(!policyCompliance.equals(policyCompliance.DENY)){
		
		List<Clue> listclues  = eventprocessorimpl.getCurrentClues(accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
		if (accessRequest.getRequestedCorporateAsset().getConfidential_level()=="PUBLIC"){
			decision = Decision.GRANTED_ACCESS;
			logger.info("Decision: GRANTED_ACCESS");
			logger.info("The confidential level of the asset is PUBLIC");
			return decision;
		}
		for (int i = 0; i < listclues.size(); i++) {
			if (listclues.get(i).getName().equalsIgnoreCase("Virus")){
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				RiskTreatment riskTreatment = new RiskTreatment("Your device seems to have a Virus,please scan you device with an Antivirus or use another device");
				riskTreatments[0] = riskTreatment;	
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				decision.setCondition("<noAttachments>1</noAttachments>");//TODO Manage this programmatically
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS:Your device seems to have a Virus,please scan you device with an Antivirus or use another device");
				return decision;
			}
		}
		decision = Decision.GRANTED_ACCESS;
		logger.info("Decision: GRANTED_ACCESS");
		return decision;
		
		}else{
			
			decision = Decision.STRONG_DENY_ACCESS;
			logger.info("Decision: STRONG_DENY_ACCESS");
			return decision;
			
			
		}
		
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
		
		RiskPolicy rPolicy = new RiskPolicy();
		
		
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy_version_6(accessRequest, rPolicy);
		
		System.out.println("Decision: "+decision2.toString());
		
   }
	
	

}
