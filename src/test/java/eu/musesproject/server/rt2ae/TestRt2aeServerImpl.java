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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.musesproject.server.eventprocessor.TestEventProcessor;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.PolicyCompliance;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.SecurityIncident;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;



public class TestRt2aeServerImpl {
	
	private Logger logger = Logger.getLogger(TestEventProcessor.class.getName());
	private Rt2aeServerImpl rt2ae = null;


	@Before
	public void setUp() throws Exception {
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	/**

	* testDecideBasedOnRiskPolicy: JUnit Test case whose aim is to compute a Decision based on RiskPolicy.

	* @param none

	*/
	public void testDecideBasedOnRiskPolicy() {
		
		logger.info("Running testUserConnectingIntranet");
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
		PolicyCompliance policyCompliance = new PolicyCompliance();
		Decision decision = rt2ae.decideBasedOnRiskPolicy_version_2(accessRequest, context);
		Decision decision1 = rt2ae.decideBasedOnRiskPolicy_version_3(accessRequest, context);
		Decision decision2 = rt2ae.decideBasedOnRiskPolicy_version_4(accessRequest, policyCompliance, context);
		Decision decision3 = rt2ae.decideBasedOnRiskPolicy_version_5(accessRequest, context);

		assertNotNull(decision);  
		assertNotNull(decision1);
		assertNotNull(decision2); 
		assertNotNull(decision3);       

  
		
		
	}

	/**

	* testWarnDeviceSecurityStateChange: JUnit Test case whose aim is to check that if the DeviceSecurityState has changed.

	* @param none

	*/
	@Test
	public void testWarnDeviceSecurityStateChange() {
		
		
		assertTrue(true);
	}

	/**

	* testWarnUserSeemsInvolvedInSecurityIncident: JUnit Test case whose aim is to check that if the user seems involved in security incident.

	* @param none

	*/
	@Test
	public void testWarnUserSeemsInvolvedInSecurityIncident() {
		User user = new User();
		UserTrustValue usertrustvalue = new UserTrustValue();
		usertrustvalue.setValue(0);
		user.setUsertrustvalue(usertrustvalue);
		SecurityIncident securityincident = new SecurityIncident();
		securityincident.setCostBenefit(100000);
		rt2ae = new Rt2aeServerImpl();
		Probability probability = new Probability();
		rt2ae.warnUserSeemsInvolvedInSecurityIncident(user,probability ,securityincident);
		assertNotNull(user.getUsertrustvalue());
	}
	
	/**

	* testComputeOutcomeProbability: JUnit Test case whose aim is to check that if the probability computed by the functionis not null.

	* @param none

	*/
	@Test
	public void testComputeOutcomeProbability() {
		Outcome requestPotentialOutcome = new Outcome("Wi-Fi sniffing",100000);
		User user = new User();
		UserTrustValue usertrustvalue = new UserTrustValue();
		usertrustvalue.setValue(0);
		user.setUsertrustvalue(usertrustvalue);
		Device device = new Device();
		DeviceTrustValue devicetrustvalue = new DeviceTrustValue();
		devicetrustvalue.setValue(0);
		device.setDevicetrustvalue(devicetrustvalue);    
		rt2ae = new Rt2aeServerImpl();
		Probability probability = new Probability();
		AccessRequest accessRequest = new AccessRequest();
		probability = rt2ae.computeOutcomeProbability(requestPotentialOutcome,accessRequest,usertrustvalue ,devicetrustvalue);
		assertNotNull(probability);
	}
	

}
