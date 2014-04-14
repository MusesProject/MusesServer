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

import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.client.model.decisiontable.ActionType;
import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.risktrust.CorporateUserAccessRequestDecision;
import eu.musesproject.server.risktrust.Decision;
import eu.musesproject.server.risktrust.RiskCommunication;
import eu.musesproject.server.risktrust.RiskTreatment;
import junit.framework.TestCase;

public class TestPolicyRulesSelector extends TestCase {

	
	/**
	  * testComputePolicyBasedOnDecisions - JUnit test case whose aim is to test the creation of a policy based on decisions from the RT2AE
	  *
	  * @param decisions - array with a list of decisions to apply to concrete event combinations
	  * 
	  */
	public final void testComputePolicyBasedOnDecisions() {
		
		PolicySelector policySelector = new PolicySelector();
		Decision[] decisions = new Decision[1];
	
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new RiskCommunication();
		RiskTreatment [] riskTreatments = new RiskTreatment[1];
		RiskTreatment riskTreatment = new RiskTreatment("Please go to the private lounge secure Wi-Fi");
		riskTreatments[0] = riskTreatment;
		riskCommunication.setRiskTreatment(riskTreatments);
		Decision decision = Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS;
		Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS.setRiskCommunication(riskCommunication);
		decisions[0] = decision;
		
		//First, we build the action associated to the decision to be taken
		String action = ActionType.OPEN;
		
		PolicyDT policyDT = policySelector.computePolicyBasedOnDecisions(decisions, action);
		assertNotNull(policyDT);
		assertNotNull(policyDT.getRawPolicy());
	}

	/**
	  * testApplyPolicyOnDevice - JUnit test case whose aim is to test the connection with the Policy Transmitter to send a policy to a concrete device
	  *
	  * @param policyDT - Set of policy decision entries
	  * @param device - Target device, identified by MUSES
	  * 
	  */
	public final void testApplyPolicyOnDevice() {
		assertTrue(true);
	}

}
