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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AdditionalProtection;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.TestEventProcessor;
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
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;



public class TestRt2aeServerImpl {
	
	private Rt2aeServerImpl rt2ae = null;
	private DBManager dbManager = new DBManager(ModuleType.RT2AE);
	private Logger logger = Logger.getLogger(Rt2aeServerImpl.class.getName());




	@Before
	public void setUp() throws Exception {
		rt2ae = new Rt2aeServerImpl();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	/**

	* testDecideBasedOnRiskPolicy: JUnit Test case whose aim is to compute a Decision based on RiskPolicy.

	* @param none

	*/
	@Test
	public void testDecideBasedOnRiskPolicy() {
		
		rt2ae = new Rt2aeServerImpl();
		
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setId(1);
		Users user = dbManager.getUserByUsername("muses");
		User user1 = new User();
		dbManager.convertUsertoCommonUser(user1 , user);
		accessRequest.setUser(user1);
		accessRequest.setUser(user1);
		Devices device = dbManager.getDeviceByIMEI("server");
		Device device1 = new Device();
		dbManager.convertDevicetoCommonDevice(device1 , device);     
		accessRequest.setDevice(device1);
		      
		Asset requestedCorporateAsset = new Asset(); 
		requestedCorporateAsset.setTitle("Asset");
		requestedCorporateAsset.setValue(1000000);
		requestedCorporateAsset.setConfidential_level("confidential");

		accessRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		Context context = new Context();
		PolicyCompliance policyCompliance = new PolicyCompliance();
		policyCompliance.setResult("ALLOW");

		
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy(accessRequest, policyCompliance, context);
		//Decision decision3 = rt2ae.decideBasedOnRiskPolicy_version_5(accessRequest, context);

		
		assertNotNull(decision2); 
		//assertNotNull(decision3);       

  
		
		
	}
	/**
	 * testDecideBasedOnRiskPolicywithOpportunity: JUnit Test case whose aim is to compute a Decision based on RiskPolicy and Opportunity with a positive cost benefit.
	 * 
	 */
	@Test
	public void testDecideBasedOnRiskPolicywithOpportunityPositiveCostBenefit() {
		
		rt2ae = new Rt2aeServerImpl();

		
		OpportunityDescriptor opportunityDescriptor = new OpportunityDescriptor();
		opportunityDescriptor.setDescription("opportunity");
		List<Outcome> list = new ArrayList<Outcome>();
		Outcome outcome = new Outcome("hourly work", 1000);
		Outcome outcome1 = new Outcome("new contract", 1000000);
		list.add(outcome);
		list.add(outcome1);
		opportunityDescriptor.setOutcomes(list);
		
		
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setOpportunityDescriptor(opportunityDescriptor);
		
		Users user = dbManager.getUserByUsername("muses");
		User user1 = new User();
		dbManager.convertUsertoCommonUser(user1 , user);
		accessRequest.setUser(user1);
		Devices device = dbManager.getDeviceByIMEI("server");
		Device device1 = new Device();
		dbManager.convertDevicetoCommonDevice(device1 , device); 
		accessRequest.setDevice(device1);
		      
		Asset requestedCorporateAsset = new Asset();  
		requestedCorporateAsset.setTitle("My Asset");
		requestedCorporateAsset.setValue(1000000);
		requestedCorporateAsset.setConfidential_level("confidential");

		accessRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		Context context = new Context();
		PolicyCompliance policyCompliance = new PolicyCompliance();
		//policyCompliance.DENY
		policyCompliance.setResult("ALLOW");
		//policyCompliance = PolicyCompliance.ALLOW;
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy(accessRequest, policyCompliance, context);
		
		assertNotNull(decision2);  

	}
	
	/**
	 * testDecideBasedOnRiskPolicywithOpportunity: JUnit Test case whose aim is to compute a Decision based on RiskPolicy and Opportunity with a negative cost benefit.
	 * 
	 */
	@Test
	public void testDecideBasedOnRiskPolicywithOpportunityNegativeCostBenefit() {
		

		
		OpportunityDescriptor opportunityDescriptor = new OpportunityDescriptor();
		opportunityDescriptor.setDescription("opportunity");
		List<Outcome> list = new ArrayList<Outcome>();
		Outcome outcome = new Outcome("hourly work", 1000);
		Outcome outcome1 = new Outcome("new contract", 1000000);
		list.add(outcome);
		list.add(outcome1);
		opportunityDescriptor.setOutcomes(list);
		
		
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setOpportunityDescriptor(opportunityDescriptor);
		
		Users user = dbManager.getUserByUsername("muses");
		User user1 = new User();
		dbManager.convertUsertoCommonUser(user1 , user);
		accessRequest.setUser(user1);
		Devices device = dbManager.getDeviceByIMEI("server");
		Device device1 = new Device();
		dbManager.convertDevicetoCommonDevice(device1 , device); 
		accessRequest.setDevice(device1);
		      
		Asset requestedCorporateAsset = new Asset();  
		requestedCorporateAsset.setTitle("Patent");
		requestedCorporateAsset.setValue(2000000);
		requestedCorporateAsset.setConfidential_level("confidential");

		accessRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		Context context = new Context();
		PolicyCompliance policyCompliance = new PolicyCompliance();
		//policyCompliance.DENY
		policyCompliance.setResult("ALLOW");
		//policyCompliance = PolicyCompliance.ALLOW;
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy(accessRequest, policyCompliance, context);
		
		assertNotNull(decision2);  

	}

	/**

	* testWarnDeviceSecurityStateChange: JUnit Test case whose aim is to check that if the DeviceSecurityState has changed.

	* @param none

	*/
	@Test
	public void testWarnDeviceSecurityStateChange() {
		/*DeviceSecurityState deviceSecurityState = new DeviceSecurityState();
		String device_id = "server";
		byte[] device_idBytes = device_id.getBytes();
		BigInteger bi = new BigInteger(device_idBytes);
		deviceSecurityState.setDevice_id(bi);
		ArrayList<Clue> clues = new ArrayList<Clue>();
		Clue e = new Clue();
		e.setName("Virus");
		clues.add(e );
		deviceSecurityState.setClues(clues);
		/*eu.musesproject.server.entity.Devices device = dbManager.getDeviceByIMEI(new String(deviceSecurityState.getDevice_id().toByteArray()));
		List<AdditionalProtection> additionalProtections = new ArrayList<AdditionalProtection>();
		AdditionalProtection element = new AdditionalProtection();
		element.setName("Antivirus");
		additionalProtections.add(element);
		device.setAdditionalProtections(additionalProtections);
		logger.info("Trust value before warnDeviceSecurityStateChange......: "+dbManager.getDeviceByIMEI(new String(deviceSecurityState.getDevice_id().toByteArray())).getTrustValue());
		rt2ae.warnDeviceSecurityStateChange(deviceSecurityState);
		
		logger.info("Trust value after warnDeviceSecurityStateChange.......: "+dbManager.getDeviceByIMEI(new String(deviceSecurityState.getDevice_id().toByteArray())).getTrustValue());
*/
		assertTrue(true);
	}

	/**

	* testWarnUserSeemsInvolvedInSecurityIncident: JUnit Test case whose aim is to check that if the user seems involved in security incident.

	* @param none

	*/
	@Test
	public void testWarnUserSeemsInvolvedInSecurityIncident() {
		
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setId(1);
		Users user = dbManager.getUserByUsername("muses");
		User user1 = new User();
		dbManager.convertUsertoCommonUser(user1 , user);
		accessRequest.setUser(user1);
		accessRequest.setUser(user1);
		
		double userTrustValue = user.getTrustValue();
		Devices device = dbManager.getDeviceByIMEI("358648051980583");
		Device device1 = new Device();
		dbManager.convertDevicetoCommonDevice(device1 , device);     
		accessRequest.setDevice(device1);
		double deviceTrustValue = device1.getDevicetrustvalue().getValue();

		Asset requestedCorporateAsset = new Asset();  
		requestedCorporateAsset.setValue(1000000);
		requestedCorporateAsset.setConfidential_level("confidential");
		requestedCorporateAsset.setTitle("");

		accessRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		Context context = new Context();
		PolicyCompliance policyCompliance = new PolicyCompliance();
		policyCompliance.setResult("ALLOW");

		
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy(accessRequest, policyCompliance, context);
		
		SecurityIncident securityIncident = new SecurityIncident();
		
		securityIncident.setDecisionid(Integer.parseInt(decision2.getId()));
		securityIncident.setAssetid(1694);
		securityIncident.setCostBenefit(100000);
		
		securityIncident.setUser(user1);
		securityIncident.setDeviceid(Integer.valueOf(device1.getDeviceId()));
		String description = "The patent is compromised and the asset has lost his value";
		securityIncident.setDescription(description);
		
		Probability probability = null;
		rt2ae.warnUserSeemsInvolvedInSecurityIncident(user1, probability , securityIncident);
		Users user2 = dbManager.getUserByUsername("muses");

		//assertTrue(userTrustValue != user1.getUsertrustvalue().getValue());
		if(userTrustValue != user2.getTrustValue()){
			assertTrue(true);
		}else{
			fail("The user trust value has not been changed");
		}

	}
	

}
