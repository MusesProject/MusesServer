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


import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.RiskCommunication;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.Users;
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
import eu.musesproject.server.risktrust.OpportunityDescriptor;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.PolicyCompliance;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.RiskTreatment;
import eu.musesproject.server.risktrust.Rt2ae;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.SolvingRiskTreatment;
import eu.musesproject.server.risktrust.Threat;
import eu.musesproject.server.risktrust.TrustValue;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;

public class Rt2aeServerImpl implements Rt2ae {

	private final int RISK_TREATMENT_SIZE = 20;
	private Logger logger = Logger.getLogger(Rt2aeServerImpl.class.getName());
	private static DBManager dbManager = new DBManager(ModuleType.RT2AE);
	private RiskPolicy riskPolicy = new RiskPolicy();//Sending e-mail with virus


	/*private String sendingemail = "Sending e-mail with virus\nYour system is infected with a virus and you want to\n send an attachment via e-mail.\n This may cause critical system failure and puts the\n receiver at risk. Remove the virus first.";
	private String saveconfidentieldocument = "Saving confidential document\n You want to save a confidential document on your device.\n If you loose your\n device, other people may be able to\n access the document.";
	private String antivirusnotrunning = "Your Antivirus is not running on your device\nPlease launch your Antivirus\n In order to protect your device";*/
	private String opensensitivedocumentinunsecurenetwork = "Opening sensitive document in unsecure network\n You are connected to an unsecure network and try\n to open a sensitive document.\n Information sent over this network is not encrypted\n and might be visible to other people.\n Switch to a secure network.";
	private String privateloungewifi = "Please go to the private lounge secure Wi-Fi";
	private String wifisniffing = "Wi-Fi sniffing";
	private String strongdenythreat = "There is too much risk in your context situation, the probability of a threat leading to a security incident is too high ";
	private String malwarerisktreatment = "Your device seems to have a Malware,please scan you device with an Antivirus or use another device";

	/**

	* DecideBasedOnRiskPolicy is a function whose aim is to compute a Decision based on RiskPolicy.

	* @param accessRequest the access request

	* @param context the context

	*/  
	@Override
	public Decision decideBasedOnRiskPolicy(AccessRequest accessRequest, PolicyCompliance policyCompliance, Context context) {
		// TODO Auto-generated method stub  
		RiskPolicy rPolicy = new RiskPolicy();
		
		logger.info("RT2AE computes the Decision...");
		String decisionId="";
		//String accessrequestId="";
		String threatId="";

		logger.info("RT2AE: receives DENY policyCompliance from EP");
		
		
		

		Decision decision = Decision.STRONG_DENY_ACCESS;
		if(policyCompliance.getResult().equals(policyCompliance.DENY)){ 
			
			
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
				userName.setName(accessRequest.getUser().getUsername()); 
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
				
				try {
					threatId = dbManager.setThreat(threat);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
				}

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

				try {
					threatId = dbManager.setThreat(existingThreat);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
				}


				logger.info("The inferred Threat from the Clues is: "
						+ existingThreat.getDescription()
						+ " with probability "
						+ existingThreat.getProbability()
						+ " for the following outcome: \""
						+ existingThreat.getOutcomes().iterator().next().getDescription()
						+ "\" with the following potential cost (in kEUR): "
						+ existingThreat.getOutcomes().iterator().next().getCostbenefit()
						+ "\n");

			}

			decision.setInformation(policyCompliance.getReason());
			if (policyCompliance.getReason().equalsIgnoreCase("AccessRequest Disable Accessibility")){
				decision.setSolving_risktreatment(8);	
			}
			ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
			eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
			eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();

			try {
			
				accessrequest1.setAssetId(BigInteger.valueOf(1));//TO DO Adding assetId from EP
				accessrequest1.setModification(new Date());
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setThreatId(Integer.valueOf(existingThreat.getThreatId()));
				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				
				
				
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				dbManager.setAccessRequest(accessrequest1);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
			}
			
			
			
			try {
				decision1.setAccessRequest(accessrequest1);
				decision1.setInformation(decision.getInformation());
				decision1.setValue("STRONGDENY");
				decision1.setTime(new java.util.Date());
				decisionId = dbManager.setDecision(decision1);
				decision.setId(decisionId);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecision:"+e.getLocalizedMessage());
			}
			
			
			
			try {
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				dbManager.setDecisionTrustvalues(decisiontrustvalues);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
			}
			
			
			
			return decision;
		} else{
			
			logger.info("RT2AE: receives ALLOW policyCompliance from EP");


			if(accessRequest.getRequestedCorporateAsset().getConfidential_level().equalsIgnoreCase("PUBLIC")){
				
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
					userName.setName(accessRequest.getUser().getUsername()); 
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
					
					try {
						threatId = dbManager.setThreat(threat);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
					}

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

					try {
						threatId = dbManager.setThreat(existingThreat);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
					}


					logger.info("The inferred Threat from the Clues is: "
							+ existingThreat.getDescription()
							+ " with probability "
							+ existingThreat.getProbability()
							+ " for the following outcome: \""
							+ existingThreat.getOutcomes().iterator().next().getDescription()
							+ "\" with the following potential cost (in kEUR): "
							+ existingThreat.getOutcomes().iterator().next().getCostbenefit()
							+ "\n");

				}

				
				
				ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				
				try {
					decision = Decision.GRANTED_ACCESS;
					accessrequest1.setAssetId(BigInteger.valueOf(1));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());
					accessrequest1.setThreatId(Integer.valueOf(existingThreat.getThreatId()));
					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					dbManager.setAccessRequest(accessrequest1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
			
				
				
				
				try {
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("GRANTED");
					decision1.setTime(new java.util.Date());
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecision:"+e.getLocalizedMessage());
				}
				
				
				
				
				try {
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;
			}
			riskPolicy.setRiskvalue(0.5);
			return decideBasedOnRiskPolicy_version_6(accessRequest, rPolicy);
		}
	}  
	
	/**
	 * 
	 * This function is the version 7 of the decideBasedOnRiskPolicy. This
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
	public Decision decideBasedOnRiskPolicy_version_7(
			AccessRequest accessRequest, RiskPolicy rPolicy) {

		// function variables and assignments

		double costOpportunity = 0.0;
		double combinedProbabilityThreats = 1.0;
		double combinedProbabilityOpportunities = 1.0;
		double singleThreatProbabibility = 0.0;
		double singleOpportunityProbability = 0.0;
		int opcount = 0;
		int threatcount = 0;

		
		Decision decision = Decision.STRONG_DENY_ACCESS;
		
		String decisionId="";


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
			userName.setName(accessRequest.getUser().getUsername()); 
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
			
			try {
				dbManager.setThreat(threat);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
			}

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

			try {
				dbManager.setThreat(existingThreat);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
			}


			logger.info("The inferred Threat from the Clues is: "
					+ existingThreat.getDescription()
					+ " with probability "
					+ existingThreat.getProbability()
					+ " for the following outcome: \""
					 + "\n");

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
		//logger.info("- Risk Policy threshold: " + riskPolicy.getRiskvalue());
		logger.info("- Cost Oportunity: " + costOpportunity);
		logger.info("- Combined Probability of the all possible Threats happening together: "
				+ combinedProbabilityThreats);
		//logger.info("- Combined Probability of the all the possible Opportunities happening together: "
			//	+ combinedProbabilityOpportunities);
		logger.info("- Combined Probability of only one of the possible Threats happening: "
				+ singleThreatProbabibility);
		//logger.info("- Combined Probability of only one of the possible Opportunities happening: "
			//	+ singleOpportunityProbability);
		logger.info("Making a decision...");
		logger.info(".");
		logger.info("..");
		logger.info("...");

		// compute the decision based on the risk policy, the threat
		// probabilities, the user trust level and the cost benefit
		
		
		
	
		
		Double trustvalue = (accessRequest
				.getUser().getUsertrustvalue().getValue()+accessRequest.getDevice().getDevicetrustvalue().getValue())/2;
		
		
		if (riskPolicy.getRiskvalue() == 0.0) {
			return Decision.GRANTED_ACCESS;
		}
		if (riskPolicy.getRiskvalue() == 1.0) {
			return Decision.STRONG_DENY_ACCESS;
		}
		
		if(((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 > riskPolicy
				.getRiskvalue()) && ((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 < 0.7) ){
		
			
			if (clues.get(0).getName().contains("Virus")){
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				decision.setSolving_risktreatment(1);
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS:Your device seems to have a Virus,please scan you device with an Antivirus or use another device");
				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("MAYBE");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Virus detection");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription());
 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;
				
			}
			
			if (clues.get(0).getName().contains("Antivirus is not running")){
				
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				
				
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				decision.setSolving_risktreatment(4);
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS:Your Antivirus is not running on your device,please launch your Antivirus in order to protect your device");
				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("MAYBE");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Virus detection");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription()); 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;
				
			}
			
			if (clues.get(0).getName().contains("UnsecureWifi:Encryption without WPA2 protocol might be unsecure")){
				
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.setSolving_risktreatment(2);
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS: You are connected to an unsecure network, please connect to a secure network");
				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("MAYBE");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Unsecure network");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription()); 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				return decision;
				
			}
			
			if (clues.get(0).getName().equalsIgnoreCase("Attempt to save a file in a monitored folder")) {
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				
				decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
				logger.info("Decision: UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION");
				
							
					
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
				decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);

				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());
				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("UPTOYOU");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Saving file");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription()); 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;

			}
			
			decision = Decision.GRANTED_ACCESS; 
			logger.info("Decision: GRANTED_ACCESS");
			
			ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
			eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
			eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
			
			
			try {
				
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				dbManager.setAccessRequests(accessRequests);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
			}
			
			
			try {
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("GRANTED");
				decision1.setTime(new java.util.Date());
				decisionId = dbManager.setDecision(decision1);
				decision.setId(decisionId);


			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
			}
			
			
			ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

			eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
			decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
			decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
			decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
			
			decisiontrustvalues.add(decisiontrustvalue);
			
			try {
				dbManager.setDecisionTrustvalues(decisiontrustvalues);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
			}
			

			return decision;

		} else {
			
			if ((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 <= riskPolicy
					.getRiskvalue()) {
				
				
				if (clues.get(0).getName().contains("Virus")){
					
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getFrench());
						riskTreatments[0] = riskTreatment;	

					}
					
					riskCommunication.setRiskTreatment(riskTreatments);
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
					logger.info("Decision: UPTOYOU");
					logger.info("RISKTREATMENTS:Your device seems to have a Virus,please scan you device with an Antivirus or use another device");
					
					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Virus detection");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					
					return decision;
					
				}
				
				if (clues.get(0).getName().contains("Antivirus is not running")){
					
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getFrench());
						riskTreatments[0] = riskTreatment;	

					}	
					riskCommunication.setRiskTreatment(riskTreatments);
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
					logger.info("Decision: UPTOYOU");
					logger.info("RISKTREATMENTS:Your Antivirus is not running on your device,please launch your Antivirus in order to protect your device");
					
					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Antivirus not running");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					
					return decision;
					
				}
				
				if (clues.get(0).getName().contains("UnsecureWifi:Encryption without WPA2 protocol might be unsecure")){
					
					
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getFrench());
						riskTreatments[0] = riskTreatment;	

					}	
					riskCommunication.setRiskTreatment(riskTreatments);
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
					logger.info("Decision: UPTOYOU");
					logger.info("RISKTREATMENTS: You are connected to an unsecure network, please connect to a secure network");
					
					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Unsecure network");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					return decision;
					
				}
				
				if (clues.get(0).getName().equalsIgnoreCase("Attempt to save a file in a monitored folder")) {
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getFrench());
						riskTreatments[0] = riskTreatment;	

					}
					
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					logger.info("Decision: UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION");
					
								
						
					riskCommunication.setRiskTreatment(riskTreatments);
						
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					logger.info("Decision: UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION");
					
								
						
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);

					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());
					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Saving file");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					
					return decision;

				}
				
				decision = Decision.GRANTED_ACCESS; 
				logger.info("Decision: GRANTED_ACCESS");
				
				ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				
				
				try {
					
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				
				try {
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("GRANTED");
					decision1.setTime(new java.util.Date());
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				

				return decision;
				
				
				
			}

			
		}
		
		if ((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 > 0.7) {
			

			ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
			eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
			eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();

			try {
			
				accessrequest1.setAssetId(BigInteger.valueOf(1));//TO DO Adding assetId from EP
				accessrequest1.setModification(new Date());
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setThreatId(Integer.valueOf(existingThreat.getThreatId()));
				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				
				
				
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				dbManager.setAccessRequest(accessrequest1);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
			}
			
			
			
			try {
				decision1.setAccessRequest(accessrequest1);
				decision = Decision.STRONG_DENY_ACCESS;
				decision.setInformation(strongdenythreat);
				decision1.setInformation(decision.getInformation());
				decision1.setValue("STRONGDENY");
				decision1.setTime(new java.util.Date());
				decisionId = dbManager.setDecision(decision1);
				decision.setId(decisionId);


			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecision:"+e.getLocalizedMessage());
			}
			
			
			
			try {
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				dbManager.setDecisionTrustvalues(decisiontrustvalues);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
			}
			
			
			return decision;
		}
		
		ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
		eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
		eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
		
		try {
			decision = Decision.GRANTED_ACCESS;
			accessrequest1.setAssetId(BigInteger.valueOf(1));
			accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
			accessrequest1.setAction(accessRequest.getAction());
			accessrequest1.setModification(new Date());
			accessrequest1.setThreatId(Integer.valueOf(existingThreat.getThreatId()));
			accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
			ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
			accessRequests.add(accessrequest1);
			 dbManager.setAccessRequest(accessrequest1);

		} catch (Exception e) {
			logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
		}
	
		
		
		
		try {
			decision1.setAccessRequest(accessrequest1);
			decision1.setValue("GRANTED");
			decision1.setTime(new java.util.Date());
			decisionId = dbManager.setDecision(decision1);
			decision.setId(decisionId);


		} catch (Exception e) {
			logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecision:"+e.getLocalizedMessage());
		}
		
		
		
		
		try {
			
			ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

			eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
			decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
			decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
			decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
			
			decisiontrustvalues.add(decisiontrustvalue);
			dbManager.setDecisionTrustvalues(decisiontrustvalues);

		} catch (Exception e) {
			logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
		}
		
		
		return decision;

	}
      
	
	/*********----------------------------**************/
	
	
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

		
		Decision decision = Decision.STRONG_DENY_ACCESS;
		
		String decisionId="";


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
			userName.setName(accessRequest.getUser().getUsername()); 
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
			
			try {
				dbManager.setThreat(threat);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
			}

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

			try {
				dbManager.setThreat(existingThreat);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setThreat:"+e.getLocalizedMessage());
			}


			logger.info("The inferred Threat from the Clues is: "
					+ existingThreat.getDescription()
					+ " with probability "
					+ existingThreat.getProbability()
					+ " for the following outcome: \""
					 + "\n");

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
		//logger.info("- Risk Policy threshold: " + riskPolicy.getRiskvalue());
		logger.info("- Cost Oportunity: " + costOpportunity);
		logger.info("- Combined Probability of the all possible Threats happening together: "
				+ combinedProbabilityThreats);
		//logger.info("- Combined Probability of the all the possible Opportunities happening together: "
			//	+ combinedProbabilityOpportunities);
		logger.info("- Combined Probability of only one of the possible Threats happening: "
				+ singleThreatProbabibility);
		//logger.info("- Combined Probability of only one of the possible Opportunities happening: "
			//	+ singleOpportunityProbability);
		logger.info("Making a decision...");
		logger.info(".");
		logger.info("..");
		logger.info("...");

		// compute the decision based on the risk policy, the threat
		// probabilities, the user trust level and the cost benefit
		
		
		
	
		
		Double trustvalue = (accessRequest
				.getUser().getUsertrustvalue().getValue()+accessRequest.getDevice().getDevicetrustvalue().getValue())/2;
		
		if(((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 > riskPolicy
				.getRiskvalue()) && ((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 < 0.7) ){
		
			
			if (clues.get(0).getName().contains("Virus")){
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				decision.setSolving_risktreatment(1);
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS:Your device seems to have a Virus,please scan you device with an Antivirus or use another device");
				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("MAYBE");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Virus detection");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription());
 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;
				
			}
			
			if (clues.get(0).getName().contains("Antivirus is not running")){
				
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				
				
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				decision.setSolving_risktreatment(4);
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS:Your Antivirus is not running on your device,please launch your Antivirus in order to protect your device");
				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("MAYBE");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Virus detection");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription()); 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;
				
			}
			
			if (clues.get(0).getName().contains("UnsecureWifi:Encryption without WPA2 protocol might be unsecure")){
				
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
				decision.setSolving_risktreatment(2);
				decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
				logger.info("Decision: MAYBE_ACCESS");
				logger.info("RISKTREATMENTS: You are connected to an unsecure network, please connect to a secure network");
				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("MAYBE");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Unsecure network");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription()); 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				return decision;
				
			}
			
			if (clues.get(0).getName().equalsIgnoreCase("Attempt to save a file in a monitored folder")) {
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[1];
				
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getSpanish());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getGerman());
					riskTreatments[0] = riskTreatment;	

				}
				if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
					RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getFrench());
					riskTreatments[0] = riskTreatment;	

				}
				
				decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
				logger.info("Decision: UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION");
				
							
					
				riskCommunication.setRiskTreatment(riskTreatments);
				decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
				decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);

				
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());
				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				
				try {
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("UPTOYOU");
				decision1.setTime(new java.util.Date());
				RiskCommunication riskcommunication1 = new RiskCommunication();
				riskcommunication1.setDescription("Saving file");
				
				try {
					dbManager.setRiskCommunications(riskcommunication1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
				}
				
				
				List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
				eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
				risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription()); 
				risktreatment1.setRiskCommunication(riskcommunication1);
				risktreatments1.add(risktreatment1);
				
				try {
					dbManager.setRiskTreatments(risktreatments1);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
				}
				
				decision1.setRiskCommunication(riskcommunication1);
				List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
				list.add(decision1);
				
				try {
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				
				
				return decision;

			}
			
			decision = Decision.GRANTED_ACCESS; 
			logger.info("Decision: GRANTED_ACCESS");
			
			ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
			eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
			eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
			
			
			try {
				
				accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setModification(new Date());

				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				dbManager.setAccessRequests(accessRequests);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
			}
			
			
			try {
				decision1.setAccessRequest(accessrequest1);
				decision1.setValue("GRANTED");
				decision1.setTime(new java.util.Date());
				decisionId = dbManager.setDecision(decision1);
				decision.setId(decisionId);


			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
			}
			
			
			ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

			eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
			decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
			decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
			decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
			
			decisiontrustvalues.add(decisiontrustvalue);
			
			try {
				dbManager.setDecisionTrustvalues(decisiontrustvalues);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
			}
			

			return decision;

		} else {
			
			if ((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 <= riskPolicy
					.getRiskvalue()) {
				
				
				if (clues.get(0).getName().contains("Virus")){
					
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getFrench());
						riskTreatments[0] = riskTreatment;	

					}
					
					riskCommunication.setRiskTreatment(riskTreatments);
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
					logger.info("Decision: UPTOYOU");
					logger.info("RISKTREATMENTS:Your device seems to have a Virus,please scan you device with an Antivirus or use another device");
					
					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Virus detection");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.VIRUS_FOUND).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					
					return decision;
					
				}
				
				if (clues.get(0).getName().contains("Antivirus is not running")){
					
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getFrench());
						riskTreatments[0] = riskTreatment;	

					}	
					riskCommunication.setRiskTreatment(riskTreatments);
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
					logger.info("Decision: UPTOYOU");
					logger.info("RISKTREATMENTS:Your Antivirus is not running on your device,please launch your Antivirus in order to protect your device");
					
					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Antivirus not running");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ANTIVIRUS_IS_NOT_RUNNING).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					
					return decision;
					
				}
				
				if (clues.get(0).getName().contains("UnsecureWifi:Encryption without WPA2 protocol might be unsecure")){
					
					
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getFrench());
						riskTreatments[0] = riskTreatment;	

					}	
					riskCommunication.setRiskTreatment(riskTreatments);
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);
					logger.info("Decision: UPTOYOU");
					logger.info("RISKTREATMENTS: You are connected to an unsecure network, please connect to a secure network");
					
					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Unsecure network");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.UNSECURE_WIFI_ENCRYPTION_WITHOUT_WPA2).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					return decision;
					
				}
				
				if (clues.get(0).getName().equalsIgnoreCase("Attempt to save a file in a monitored folder")) {
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					RiskTreatment [] riskTreatments = new RiskTreatment[1];
					
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("en")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("es")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getSpanish());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("de")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getGerman());
						riskTreatments[0] = riskTreatment;	

					}
					if(dbManager.getUserByUsername(accessRequest.getUser().getUsername()).getLanguage().equalsIgnoreCase("fr")){
						RiskTreatment riskTreatment = new RiskTreatment(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getFrench());
						riskTreatments[0] = riskTreatment;	

					}
					
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					logger.info("Decision: UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION");
					
								
						
					riskCommunication.setRiskTreatment(riskTreatments);
						
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					logger.info("Decision: UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION");
					
								
						
					decision = Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION;
					decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION.setRiskCommunication(riskCommunication);

					eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
					eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());
					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					
					try {
						dbManager.setAccessRequests(accessRequests);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
					}
					
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("UPTOYOU");
					decision1.setTime(new java.util.Date());
					RiskCommunication riskcommunication1 = new RiskCommunication();
					riskcommunication1.setDescription("Saving file");
					
					try {
						dbManager.setRiskCommunications(riskcommunication1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskCommunications:"+e.getLocalizedMessage());
					}
					
					
					List<eu.musesproject.server.entity.RiskTreatment> risktreatments1 = new ArrayList<eu.musesproject.server.entity.RiskTreatment>();
					eu.musesproject.server.entity.RiskTreatment risktreatment1 = new eu.musesproject.server.entity.RiskTreatment();
					risktreatment1.setDescription(dbManager.getRisktreatments(SolvingRiskTreatment.ATTEMPT_TO_SAVE_A_FILE_IN_A_MONITORED_FOLDER).getDescription()); 
					risktreatment1.setRiskCommunication(riskcommunication1);
					risktreatments1.add(risktreatment1);
					
					try {
						dbManager.setRiskTreatments(risktreatments1);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setRiskTreatments:"+e.getLocalizedMessage());
					}
					
					decision1.setRiskCommunication(riskcommunication1);
					List<eu.musesproject.server.entity.Decision> list = new ArrayList<eu.musesproject.server.entity.Decision>();
					list.add(decision1);
					
					try {
						decisionId = dbManager.setDecision(decision1);
						decision.setId(decisionId);


					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
					}
					
					ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

					eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
					decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
					decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
					decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
					
					decisiontrustvalues.add(decisiontrustvalue);
					
					try {
						dbManager.setDecisionTrustvalues(decisiontrustvalues);

					} catch (Exception e) {
						logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
					}
					
					
					return decision;

				}
				
				decision = Decision.GRANTED_ACCESS; 
				logger.info("Decision: GRANTED_ACCESS");
				
				ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
				eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
				eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
				
				
				try {
					
					accessrequest1.setAssetId(BigInteger.valueOf(accessRequest.getRequestedCorporateAsset().getId()));
					accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
					accessrequest1.setAction(accessRequest.getAction());
					accessrequest1.setModification(new Date());

					accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
					ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
					accessRequests.add(accessrequest1);
					dbManager.setAccessRequests(accessRequests);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
				}
				
				
				try {
					decision1.setAccessRequest(accessrequest1);
					decision1.setValue("GRANTED");
					decision1.setTime(new java.util.Date());
					decisionId = dbManager.setDecision(decision1);
					decision.setId(decisionId);


				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisions:"+e.getLocalizedMessage());
				}
				
				
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				
				try {
					dbManager.setDecisionTrustvalues(decisiontrustvalues);

				} catch (Exception e) {
					logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
				}
				

				return decision;
				
				
				
			}

			
		}
		
		if ((combinedProbabilityThreats + ((Double) 1.0-trustvalue) )/2 > 0.7) {
			

			ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
			eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
			eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();

			try {
			
				accessrequest1.setAssetId(BigInteger.valueOf(1));//TO DO Adding assetId from EP
				accessrequest1.setModification(new Date());
				accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
				accessrequest1.setAction(accessRequest.getAction());
				accessrequest1.setThreatId(Integer.valueOf(existingThreat.getThreatId()));
				accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
				
				
				
				ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
				accessRequests.add(accessrequest1);
				dbManager.setAccessRequest(accessrequest1);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
			}
			
			
			
			try {
				decision1.setAccessRequest(accessrequest1);
				decision = Decision.STRONG_DENY_ACCESS;
				decision.setInformation(strongdenythreat);
				decision1.setInformation(decision.getInformation());
				decision1.setValue("STRONGDENY");
				decision1.setTime(new java.util.Date());
				decisionId = dbManager.setDecision(decision1);
				decision.setId(decisionId);


			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecision:"+e.getLocalizedMessage());
			}
			
			
			
			try {
				ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

				eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
				decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
				decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
				decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
				
				decisiontrustvalues.add(decisiontrustvalue);
				dbManager.setDecisionTrustvalues(decisiontrustvalues);

			} catch (Exception e) {
				logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
			}
			
			
			return decision;
		}
		
		ArrayList<eu.musesproject.server.entity.Decision> listDecisions = new ArrayList<eu.musesproject.server.entity.Decision>();
		eu.musesproject.server.entity.Decision decision1 = new eu.musesproject.server.entity.Decision();
		eu.musesproject.server.entity.AccessRequest accessrequest1 = new eu.musesproject.server.entity.AccessRequest();
		
		try {
			decision = Decision.GRANTED_ACCESS;
			accessrequest1.setAssetId(BigInteger.valueOf(1));
			accessrequest1.setEventId(BigInteger.valueOf(accessRequest.getEventId()));
			accessrequest1.setAction(accessRequest.getAction());
			accessrequest1.setModification(new Date());
			accessrequest1.setThreatId(Integer.valueOf(existingThreat.getThreatId()));
			accessrequest1.setUserId(new BigInteger(accessRequest.getUser().getUserId()));
			ArrayList<eu.musesproject.server.entity.AccessRequest> accessRequests = new ArrayList<eu.musesproject.server.entity.AccessRequest>() ;
			accessRequests.add(accessrequest1);
			 dbManager.setAccessRequest(accessrequest1);

		} catch (Exception e) {
			logger.error("Please, check database persistence:An error has produced while calling dbManager.setAccessRequests:"+e.getLocalizedMessage());
		}
	
		
		
		
		try {
			decision1.setAccessRequest(accessrequest1);
			decision1.setValue("GRANTED");
			decision1.setTime(new java.util.Date());
			decisionId = dbManager.setDecision(decision1);
			decision.setId(decisionId);


		} catch (Exception e) {
			logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecision:"+e.getLocalizedMessage());
		}
		
		
		
		
		try {
			
			ArrayList<eu.musesproject.server.entity.DecisionTrustvalues> decisiontrustvalues = new ArrayList<eu.musesproject.server.entity.DecisionTrustvalues>();

			eu.musesproject.server.entity.DecisionTrustvalues decisiontrustvalue = new eu.musesproject.server.entity.DecisionTrustvalues();
			decisiontrustvalue.setDevicetrustvalue(accessRequest.getDevice().getDevicetrustvalue().getValue());
			decisiontrustvalue.setUsertrustvalue(accessRequest.getUser().getUsertrustvalue().getValue());
			decisiontrustvalue.setDecisionId(Integer.parseInt(decisionId));
			
			decisiontrustvalues.add(decisiontrustvalue);
			dbManager.setDecisionTrustvalues(decisiontrustvalues);

		} catch (Exception e) {
			logger.error("Please, check database persistence:An error has produced while calling dbManager.setDecisionTrustvalues:"+e.getLocalizedMessage());
		}
		
		
		return decision;

	}
	
	
	/*********----------------------------************/
	
	
	
	
	
	
	
	
	
	
	
	
	/**  
	 * This function is the version 1 of the decideBasedOnRiskPolicy. This version computes the Decision based on the Context and the AccessRequest
	 * 
	 * @param accessRequest
	 * @param context            
	 * @return   
	 */
	@SuppressWarnings("static-access")
	public Decision decideBasedOnRiskPolicy_version_1(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
		Decision decision = Decision.STRONG_DENY_ACCESS;
		
		if (accessRequest.getRequestedCorporateAsset().getValue() <= 1000000 ) {
			
			
			Random r = new Random();
			int valeur = 0 + r.nextInt(100 - 0);

			accessRequest.getUser().getUsertrustvalue().setValue(valeur);
			accessRequest.getDevice().getDevicetrustvalue().setValue(valeur);
			
			
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			if (threats.isEmpty()){			
				
				decision = Decision.GRANTED_ACCESS; 
				return decision;
			}else{
				
				eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
				RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
				RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
	@SuppressWarnings("static-access")
	public Decision decideBasedOnRiskPolicy_version_2(AccessRequest accessRequest,Context context) {
		// TODO Auto-generated method stub
			
			Decision decision = Decision.STRONG_DENY_ACCESS;

	
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			
			
			
			if (threats.isEmpty()){			
				 decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						if(threats.get(i).getType().equalsIgnoreCase(wifisniffing)  && threats.get(i).getProbability()>0.5){ 
								
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
							RiskTreatment riskTreatment1 = new RiskTreatment(malwarerisktreatment);
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
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
				
			
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			
			
			
			if (threats.isEmpty()){			
				decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						if(threats.get(i).getType().equalsIgnoreCase(wifisniffing) && threats.get(i).getProbability()>0.5){ 
							
							Outcome requestPotentialOutcome = new Outcome(wifisniffing, -accessRequest.getRequestedCorporateAsset().getValue()/2);
							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							
							if(probability.getValue()<=0.5){	
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(malwarerisktreatment);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
							
			List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
			
			
			
			if (threats.isEmpty()){			
				decision = Decision.GRANTED_ACCESS;; 
				return decision;
			}else{
				
				for (int i = 0; i < threats.size(); i++) {
					
					if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
						if(threats.get(i).getType().equalsIgnoreCase(wifisniffing) && threats.get(i).getProbability()>0.5){ 
							
							Outcome requestPotentialOutcome = new Outcome(wifisniffing, -accessRequest.getRequestedCorporateAsset().getValue()/2);
							Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
							
							if(probability.getValue()<=0.5){	
								eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
								RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
									RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(malwarerisktreatment);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
									RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
		
		
		List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
		
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				if(threats.get(i).getType().equalsIgnoreCase(wifisniffing)  && threats.get(i).getProbability()<=0.5){ 
					
					Outcome requestPotentialOutcome = new Outcome(wifisniffing, -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					
					if(probability == null){
						logger.info("Decision: STRONG_DENY_ACCESS");
						decision = Decision.STRONG_DENY_ACCESS;
						return decision;
					}else{
						if(probability.getValue()<=0.5){	
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
							RiskTreatment riskTreatment = new RiskTreatment(malwarerisktreatment);
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
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
		
		
		List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
				
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				if(threats.get(i).getType().equalsIgnoreCase(wifisniffing) && threats.get(i).getProbability()<0.3){ 
					
					Outcome requestPotentialOutcome = new Outcome(wifisniffing, -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.3){	
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
							RiskTreatment riskTreatment = new RiskTreatment(malwarerisktreatment);
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
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
		
		
		List<Threat> threats = new ArrayList<Threat>(); //TODO Change threats by clues
		
		
		for (int i = 0; i < threats.size(); i++) {
			
			if(threats.get(i).getAssetId() == accessRequest.getRequestedCorporateAsset().getId()){
				if(threats.get(i).getType().equalsIgnoreCase(wifisniffing) && threats.get(i).getProbability()<0.1){ 
					
					Outcome requestPotentialOutcome = new Outcome(wifisniffing, -accessRequest.getRequestedCorporateAsset().getValue()/2);
					Probability probability = eventprocessorimpl.computeOutcomeProbability(requestPotentialOutcome, accessRequest, accessRequest.getUser().getUsertrustvalue(), accessRequest.getDevice().getDevicetrustvalue());
					if(probability == null){
						decision = Decision.STRONG_DENY_ACCESS;
						logger.info("Decision: STRONG_DENY_ACCESS");
						return decision;
					}else{
						if(probability.getValue()<=0.1){	
							eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
							RiskTreatment [] riskTreatments = new RiskTreatment[RISK_TREATMENT_SIZE];
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
							RiskTreatment riskTreatment = new RiskTreatment(malwarerisktreatment);
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
							RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
								RiskTreatment riskTreatment = new RiskTreatment(privateloungewifi);
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
		
		if(!policyCompliance.getResult().equals(policyCompliance.DENY)){
		
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
	* WarnDeviceSecurityStateChange is a function whose aim is to update the device trust value based on the new DeviceSecurityState.
	* @param deviceSecurityState the device security state
	*/
	@Override
	public void warnDeviceSecurityStateChange(DeviceSecurityState deviceSecurityState) {

		//if(dbManager.findDeviceById(Integer.toString(deviceSecurityState.getDevice_id())).size()!=0){
		
		logger.info("RT2AE.......: "+deviceSecurityState.getDevice_id());

		if(dbManager.getDeviceByIMEI(new String(deviceSecurityState.getDevice_id().toByteArray()))!=null){
			//eu.musesproject.server.entity.Devices device = dbManager.findDeviceById(Integer.toString(deviceSecurityState.getDevice_id())).get(0);
			eu.musesproject.server.entity.Devices device = dbManager.getDeviceByIMEI(new String(deviceSecurityState.getDevice_id().toByteArray()));
	
			double countadditionalprotection = device.getAdditionalProtections().size();
	
			double countclues = deviceSecurityState.getClues().size();
			
			deviceSecurityState.getDevice_id();
			List<Clue> listclues = deviceSecurityState.getClues();
			if(listclues.size()<=5){
				device.setTrustValue(countadditionalprotection/(countadditionalprotection+countclues+1));
			}else{
				device.setTrustValue(countadditionalprotection/(countadditionalprotection+countclues));
			}
			try {
				
				
				dbManager.persist(device);
				
	
			} catch (Exception e) {
				// TODO: handle exception
			}
		
		}else{
			logger.info("The device is not stored in the database, make sure that your device is registred,please contact the CSO");
			
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
			
			eu.musesproject.server.entity.Decision decision = dbManager.findDecisionById(String.valueOf(securityIncident.getDecisionid())).get(0);
			
			eu.musesproject.server.entity.Threat threat =  dbManager.findThreatById(String.valueOf(decision.getAccessRequest().getThreatId())).get(0);
			
			threat.setBadOutcomeCount(threat.getBadOutcomeCount()+1);
			
			
			threat.setProbability(threat.getBadOutcomeCount()/threat.getOccurences());
			dbManager.setThreat(threat);
			
			eu.musesproject.server.entity.Users musesUser = dbManager.getUserByUsername(securityIncident.getUser().getSurname());
			
			eu.musesproject.server.entity.Devices musesDevice = dbManager.findDeviceById(String.valueOf(securityIncident.getDeviceid())).get(0);

			List<eu.musesproject.server.entity.Devices> musesDevices = new ArrayList<eu.musesproject.server.entity.Devices>();
			
			List<eu.musesproject.server.entity.Users> users = new ArrayList<eu.musesproject.server.entity.Users>();

			musesDevice.setTrustValue(1-threat.getProbability());
			musesDevices.add(musesDevice);	
			dbManager.setDevice(musesDevice);

			musesUser.setTrustValue(1-threat.getProbability());
			users.add(musesUser);
			dbManager.setUsers(users);

			
			
			
					
	}

	public void storingUserBehavior(eu.musesproject.server.entity.UserBehaviour userbehaviour) {
		
		if(userbehaviour!=null){
			logger.info("RT2AE receives the user behaviour....");
			try {
				logger.info("RT2AE storing the user behaviour....");

				dbManager.setUserBehaviour(userbehaviour);
				

			} catch (Exception e) {
				logger.error("Something happens storing the user behaviour");
			}
		}else{
			logger.info("The user behaviour is null, make sure that the user behaviour is not null");

			}
		
		
	}
	
	/**
	 * Updates trust in user given negative outcome.
	 * 
	 * @param user1
	 *            the user1
	 * @param opportunityDescriptor
	 *            the opportunity descriptor
	 */
	public void updatesTrustInUserGivenNegativeOutcome(User user1,
			OpportunityDescriptor opportunityDescriptor) {
		eu.musesproject.server.entity.Users musesUser = dbManager.getUserByUsername(user1.getUsername());
		List<eu.musesproject.server.entity.Users> users = new ArrayList<eu.musesproject.server.entity.Users>();

		UserTrustValue usertrustvalue = new UserTrustValue();
		usertrustvalue.setValue(0.0);
		if(user1.getUsertrustvalue().getValue() <= 0)
			user1.setUsertrustvalue(usertrustvalue );
		else{
			UserTrustValue t = new UserTrustValue();//new TrustValue((user1.getUsertrustvalue().getValue() - 0.05));
			t.setValue(user1.getUsertrustvalue().getValue()-0.05);
			
			user1.setUsertrustvalue(t);
			
		}
		musesUser.setTrustValue(user1.getUsertrustvalue().getValue());
		users.add(musesUser);
		dbManager.setUsers(users);
	}

	/**
	 * Updates trust in user given positive outcome.
	 * 
	 * @param user1
	 *            the user1
	 * @param opportunityDescriptor
	 *            the opportunity descriptor
	 */
	public void updatesTrustInUserGivenPositiveOutcome(User user1,
			OpportunityDescriptor opportunityDescriptor) {
		//System.out.println("Former users trust value is: " + user1.getUsertrustvalue().getValue());
		eu.musesproject.server.entity.Users musesUser = dbManager.getUserByUsername(user1.getUsername());
		List<eu.musesproject.server.entity.Users> users = new ArrayList<eu.musesproject.server.entity.Users>();

		UserTrustValue usertrustvalue = new UserTrustValue();
		usertrustvalue.setValue(1.0);
		if(user1.getUsertrustvalue().getValue() >= 1)
			user1.setUsertrustvalue(usertrustvalue);
		else{
			UserTrustValue t = new UserTrustValue();//TrustValue t = new TrustValue((user1.getTrustValue().getValue() + 0.1));
			t.setValue(user1.getUsertrustvalue().getValue()+0.1);

			user1.setUsertrustvalue(t);
			}
		System.out.println("New users trust value is: " + user1.getUsertrustvalue().getValue());
		//GuiMain.getPersistenceManager().setSimUsers(new ArrayList<SimUser>(Arrays.asList(user1)));
		
		musesUser.setTrustValue(user1.getUsertrustvalue().getValue());
		users.add(musesUser);
		dbManager.setUsers(users);
	}
	
		
	
	

}
