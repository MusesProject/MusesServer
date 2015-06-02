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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;

import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.continuousrealtimeeventprocessor.IMusesCorrelationEngine;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.CorporatePolicies;
import eu.musesproject.server.entity.SecurityRules;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.engine.TemporalDroolsEngineServiceImpl;
import eu.musesproject.server.eventprocessor.correlator.engine.changeset.notifficator.DroolsEngineResourceNotifier;
import eu.musesproject.server.eventprocessor.correlator.global.GlobalCreator;
import eu.musesproject.server.eventprocessor.correlator.global.GlobalCreatorImpl;
import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.eventprocessor.util.Constants;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.AdditionalProtection;
import eu.musesproject.server.risktrust.Clue;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.Outcome;
import eu.musesproject.server.risktrust.Probability;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;

public class EventProcessorImpl implements EventProcessor {
	
	private static volatile DroolsEngineService des = null;
	private Logger logger = Logger.getLogger(EventProcessorImpl.class);
	private DBManager dbmanager = new DBManager(ModuleType.EP);
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
	public List<Clue> getCurrentClues(AccessRequest accessRequest,
			UserTrustValue userTrustValue, DeviceTrustValue deviceTrustValue) {
		
		List<eu.musesproject.server.risktrust.Clue> clues = Rt2aeGlobal.getCluesByRequestId(accessRequest.getId());
		logger.info("Number of clues:"+clues.size());
		if (clues.size()==0){
			Clue clue = new Clue();
			clue.setName("NOT-AVAILABLE-CLUES");
			clues.add(clue);
		}
		return clues;

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
		URL urlRulePath = getClass().getClassLoader().getResource(relativeRulePath);
		//URL urlRulePath = getClass().getResource(relativeRulePath);
		InputStream  is= getClass().getResourceAsStream("/drl");
		if (is!=null){
			logger.info("Input stream is NOT null");
		}else{
			logger.info("Input stream is NULL");
		}
		logger.info("URL rule path:" + urlRulePath.getPath());
		String drlRulePath = Constants.FILE_PROTOCOL + urlRulePath.getPath();
		
		logger.info("DRL absolute rule path:"+drlRulePath);
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
		
		storePoliciesOnStartup();
		storeRulesOnStartup();
		
		return engine;
	}
	
	public void storePoliciesOnStartup() {
		
		InputStream[] policyInputStream = null;
		File[] policyFiles = null;
		byte[] data = null;
		
		dbmanager.removeAllCorporatePolicies();
		try {
			policyInputStream = getResourceInputStreamListing(EventProcessorImpl.class, "policies");
			policyFiles = getResourceFileListing(EventProcessorImpl.class, "policies");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CorporatePolicies policy = new CorporatePolicies();
		
		for (int i = 0; i < policyInputStream.length; i++) {
			policy.setName(policyFiles[i].getPath());
			policy.setDescriptionEn(String.valueOf(i));
			try {
				data = istoByteArray(policyInputStream[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			policy.setFile(data);
			policy.setDate(new Date());
			dbmanager.setCorporatePolicy(policy);
		}
		
	}
	
	public void storeRulesOnStartup() {
		
		InputStream[] ruleInputStream = null;
		File[] ruleFiles = null;
		byte[] data = null;
		
		//dbmanager.removeAllSecurityRules();
		try {
			ruleInputStream = getResourceInputStreamListing(EventProcessorImpl.class, "drl/security-corporate-rules");
			ruleFiles = getResourceFileListing(EventProcessorImpl.class, "drl/security-corporate-rules");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SecurityRules rule = new SecurityRules();
		
		for (int i = 0; i < ruleInputStream.length; i++) {
			rule.setName(ruleFiles[i].getPath());
			try {
				data = istoByteArray(ruleInputStream[i]);
				rule.setDescription(new String(data, "UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			rule.setFile(data);
			rule.setModification(new Date());
			byte[] refined = new byte[1];
			refined[0]=0;
			rule.setRefined(refined);
			rule.setStatus(Constants.VALIDATED);
			dbmanager.setSecurityRule(rule);
		}
		
	}
	
	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	File[] getResourceFileListing(Class clazz, String path) throws URISyntaxException, IOException {
		File[] result = null;
	      URL dirURL = clazz.getClassLoader().getResource(path);
	      if (dirURL != null && dirURL.getProtocol().equals("file")) {

	        String[] list = new File(dirURL.toURI()).list();
	        result = new File[list.length];
	        for (int i = 0; i < list.length; i++) {
	        	File file = new File(list[i].toString());
	        	result[i] = file;				
			}
	      } 
	      return result;
	  }
	
	InputStream[] getResourceInputStreamListing(Class clazz, String path) throws URISyntaxException, IOException {
		InputStream[] result = null;
	      URL dirURL = clazz.getClassLoader().getResource(path);
	      if (dirURL != null && dirURL.getProtocol().equals("file")) {

	        String[] list = new File(dirURL.toURI()).list();
	        result = new InputStream[list.length];
	        for (int i = 0; i < list.length; i++) {
	        	InputStream is = clazz.getClassLoader().getResourceAsStream(path+"/"+list[i].toString());	        	
	        	result[i] = is;				
			}
	      } 
	      return result;
	  }
	
	byte[] istoByteArray(InputStream is) throws IOException{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}
		
	public static DroolsEngineService getMusesEngineService(){
		return des;
	}

}
