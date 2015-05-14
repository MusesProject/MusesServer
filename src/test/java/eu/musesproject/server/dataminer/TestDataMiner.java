package eu.musesproject.server.dataminer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.log4j.Logger;

import eu.musesproject.server.dataminer.DataMiner;
import eu.musesproject.server.entity.AdditionalProtection;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.RiskInformation;
import eu.musesproject.server.entity.SecurityIncident;
import eu.musesproject.server.entity.SystemLogKrs;
import eu.musesproject.server.entity.ThreatClue;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Users;

public class TestDataMiner {
	
	static DataMiner dm = new DataMiner();
	private Logger logger = Logger.getLogger(TestDataMiner.class);
	
	@BeforeClass
	public  static void setUpBeforeClass() throws Exception {

	}
	
	@AfterClass
	public  static void setUpAfterClass() throws Exception {

	}

	@Test
	public void testGetSimpleEvents() {
		
		List<SimpleEvents> List = dm.getSimpleEvents();
		
		/* Uncomment for studying which info is useful or interesting, no errors
		logger.info("---User info---");
		logger.info("Username-"+List.get(0).getUser().getUsername());
		logger.info("Trust value-"+List.get(0).getUser().getTrustValue());
		logger.info("Role-"+List.get(0).getUser().getRoleId());
		logger.info("Activated account-"+List.get(0).getUser().getEnabled());
		logger.info("---Date info---");
		logger.info(List.get(0).getDate().toString());
		logger.info("---Time info---");
		logger.info(List.get(0).getTime().toString());
		logger.info("---Event type info---");
		logger.info("Level-"+List.get(0).getEventType().getEventLevel());
		logger.info("Key-"+List.get(0).getEventType().getEventTypeKey());
		logger.info("---Asset info---");
		logger.info("Confidential level-"+List.get(0).getAsset().getConfidentialLevel());
		logger.info("Location-"+List.get(0).getAsset().getLocation());
		logger.info("Title-"+List.get(0).getAsset().getTitle());
		logger.info("Value-"+List.get(0).getAsset().getValue());
		logger.info("---Device info---");
		logger.info("Certificate-"+List.get(0).getDevice().getCertificate());
		logger.info("Id-"+List.get(0).getDevice().getDeviceId());
		logger.info("Type-"+List.get(0).getDevice().getDeviceType().getType());
		logger.info("OS-"+List.get(0).getDevice().getOS_name());
		logger.info("OS version-"+List.get(0).getDevice().getOS_version());
		logger.info("Security level-"+List.get(0).getDevice().getSecurityLevel());
		logger.info("Trust value-"+List.get(0).getDevice().getTrustValue());
		logger.info("---Duration info---");
		logger.info(List.get(0).getDuration());
		logger.info("---App info---");
		logger.info("MUSES aware-"+List.get(0).getApplication().getIs_MUSES_aware());
		logger.info("Name-"+List.get(0).getApplication().getName());
		logger.info("Vendor-"+List.get(0).getApplication().getVendor());
		logger.info("Version-"+List.get(0).getApplication().getVersion());
		logger.info("Type-"+List.get(0).getApplication().getAppType().getType());
		logger.info("---Access info---");
		logger.info("EP-"+List.get(0).getEP_can_access());
		logger.info("KRS-"+List.get(0).getKRS_can_access());
		logger.info("RT2AE-"+List.get(0).getRT2AE_can_access());*/
		
		/* GIVES ERROR
		List<AdditionalProtection> protections = List.get(0).getAdditionalProtections();
		logger.info(protections.size());
		if (protections.size()>0){
			logger.info("---Additional protections info---");
			logger.info("Id-"+protections.get(0).getAccessRequestId());
			logger.info("Name-"+protections.get(0).getName());
			logger.info("Access request id-"+protections.get(0).getAccessRequestId());
		} else {
			logger.info("No Additional protections");
		}
		
		List<RiskInformation> riskInfo = List.get(0).getRiskInformations();
		if (riskInfo.size()>0){
			logger.info("---Risk information info---");
			logger.info("Probability-"+riskInfo.get(0).getProbability());
			logger.info("Id-"+riskInfo.get(0).getRiskInformationId());
		} else {
			logger.warn("No Risk Information");
		}
		
		
		List<SecurityIncident> incidents = List.get(0).getSecurityIncidents();
		if (incidents.size()>0) {
			logger.info("---Security incidents info---");
			logger.info("Nombre-"+incidents.get(0).getName());
			logger.info("Id-"+incidents.get(0).getSecurityIncidentId());
			logger.info("Modification-"+incidents.get(0).getModification());
		} else {
			logger.warn("No Security incidents associated");
		}
		
		List<ThreatClue> threats = List.get(0).getThreatClues();
		if (threats.size()>0) {
			logger.info("---Threat clues info---");
			logger.info("Data-"+List.get(0).getData());
			logger.info("Duration-"+List.get(0).getDuration());
			logger.info("Id-"+List.get(0).getEventId());
		} else {
			logger.warn("No Threat clues");
		}*/
			
		if (List.size()>0){
			Iterator<SimpleEvents> i = List.iterator();
			while(i.hasNext()){
				SimpleEvents event = i.next();
				assertNotNull(event);
			}
		}else{
			fail("There are not simple events in the database, please create some events first.");
		}					
		
	}
	
	/**
	  * testRetrieveUnprocessed - JUnit test case whose aim is to test unprocessed data retrieval from the database
	  *
	  * @param none 
	  * 
	  */
	/*@Test
	public final void testRetrievePendingEvents() {
		
		List<SimpleEvents> eventList = dm.getSimpleEvents();
		
		if (eventList.size()>0){
			dm.retrievePendingEvents(eventList);
		}else{
			fail("There are not simple events in the database, please create some events first.");
		}
	}*/

	/**
	  * testDataMining - JUnit test case whose aim is to test execution of current data mining algorithms over retrieved data
	  *
	  * @param none 
	  * 
	  */
	@Test
	public final void testDataMining() {
		List<SimpleEvents> List = dm.getSimpleEvents();
		if (List.size()>0){
			Iterator<SimpleEvents> i = List.iterator();
			while(i.hasNext()){
				SimpleEvents event = i.next();
				PatternsKrs pattern = dm.minePatterns(event);
				assertNotNull(pattern);
			}
		}else{
			fail("There are not simple events in the database, please create some events first.");
		}
	}

}
