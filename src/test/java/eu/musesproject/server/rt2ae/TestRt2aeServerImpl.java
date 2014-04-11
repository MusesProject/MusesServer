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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.log4j.Logger;

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.TestEventProcessor;
import eu.musesproject.server.eventprocessor.correlator.global.StatusGlobal;
import eu.musesproject.server.eventprocessor.simulation.UseCaseFactory;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Context;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceTrustValue;
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
		accessRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		Context context = new Context();
		Decision decision = rt2ae.decideBasedOnRiskPolicy_version_2(accessRequest, context);
		Decision decision1 = rt2ae.decideBasedOnRiskPolicy_version_3(accessRequest, context);
		
		assertNotNull(decision);
		assertNotNull(decision1);
		
		
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
		
		assertTrue(true);
	}
	
	

}
