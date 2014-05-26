package eu.musesproject.server.eventprocessor.impl;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;

import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.continuousrealtimeeventprocessor.IMusesCorrelationEngine;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.engine.TemporalDroolsEngineServiceImpl;
import eu.musesproject.server.eventprocessor.correlator.engine.changeset.notifficator.DroolsEngineResourceNotifier;
import eu.musesproject.server.eventprocessor.correlator.global.GlobalCreator;
import eu.musesproject.server.eventprocessor.correlator.global.GlobalCreatorImpl;
import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.eventprocessor.util.Constants;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.AdditionalProtection;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.Threat;
import eu.musesproject.server.risktrust.UserTrustValue;

public class EventProcessorImpl implements EventProcessor {
	
	private static volatile DroolsEngineService des = null;
	private Logger logger = Logger.getLogger(EventProcessorImpl.class);
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	
	/**
	 * This method receives an Outcome, composed by a description of the cause or type of the threats (e.g. data leak), with a value representing the cost of the type of threats.
	 * 
	 * Based on that and the UserTrustValue and DeviceTrustValue and accessRequest, the method computes the probability that the Outcome might occur with the associated cost.
	 * 
	 * 
	 * */

	@Override
	public Probability computeOutcomeProbability(
			Outcome requestPotentialOutcome, AccessRequest accessRequest,
			UserTrustValue userTrustValue, DeviceTrustValue deviceTrustValue) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Threat> getCurrentThreats(AccessRequest accessRequest,
			UserTrustValue userTrustValue, DeviceTrustValue deviceTrustValue) {
		
		return Rt2aeGlobal.getThreatsByRequestId(accessRequest.getId());

	}
	
	@Override
	public List<AdditionalProtection> getCurrentAdditionalProtections(AccessRequest accessRequest,
			UserTrustValue userTrustValue, DeviceTrustValue deviceTrustValue) {
		
		return Rt2aeGlobal.getProtectionsByRequestId(accessRequest.getId());
	

	}

	@Override
	public void initializeEventProcessor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logDeniedAccessRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logNegativeOutcomeBasedOnTheAchievedOpportunity() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logPositiveOutcomeBasedOnTheAchievedOpportunity() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logUserAccessedAsset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logUserDecidedToAccessInSpiteOfRisk() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logUserMadeAccessRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logUserSeemsInvolvedInSecurityIncident() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logUserSuccessfullyAppliedRiskTreatment() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpResourceNotifier(IMusesCorrelationEngine des, String drlUrl) {
		// TODO Auto-generated method stub

	}

	@Override
	public IMusesCorrelationEngine startTemporalCorrelation(String relativeRulePath) {
		
		logger.info("Starting temporal correlation...");
		MusesCorrelationEngineImpl engine = new MusesCorrelationEngineImpl();		
		URL urlRulePath = getClass().getResource(relativeRulePath);
		String drlRulePath = Constants.FILE_PROTOCOL + urlRulePath.getPath();
		
		//First,
		Properties props = new Properties();
		props.setProperty(Constants.DROOLS_INTERVAL, Constants.DROOLS_INTERVAL_VALUE);
		ResourceChangeScanner service = ResourceFactory
				.getResourceChangeScannerService();
		ResourceChangeScannerConfiguration rconf = service
				.newResourceChangeScannerConfiguration(props);
		service.configure(rconf);
		service.start();
		ResourceFactory.getResourceChangeNotifierService().start();
		
		GlobalCreator gc = new GlobalCreatorImpl();

		EventProcessorImpl.des = new TemporalDroolsEngineServiceImpl(drlRulePath,Constants.CORRELATOR_NAME, gc.createGlobalContexts(), null,"source/main/resources/");

		DroolsEngineResourceNotifier dern = new DroolsEngineResourceNotifier(des);

		ResourceFactory.getResourceChangeNotifierService()
					.subscribeResourceChangeListener(dern,
							ResourceFactory.newUrlResource(drlRulePath));
		boolean error = ((TemporalDroolsEngineServiceImpl)des).engineInError();
		if (error){
			return null;
		}
		logger.info("Correlator started!");
		return engine;
	}
		
	public static DroolsEngineService getMusesEngineService(){
		return des;
	}

}
