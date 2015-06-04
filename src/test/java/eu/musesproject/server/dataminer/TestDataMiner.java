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

import weka.core.Instance;
import weka.core.Instances;
import eu.musesproject.server.dataminer.DataMiner;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AdditionalProtection;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.SecurityIncident;
import eu.musesproject.server.entity.SystemLogKrs;
import eu.musesproject.server.entity.ThreatClue;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.scheduler.ModuleType;

public class TestDataMiner {
	
	static DataMiner dm = new DataMiner();
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
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
	/*@Test
	public final void testDataMining() {		
		List<SimpleEvents> list = dm.getSimpleEvents();
		List<PatternsKrs> patternList = new ArrayList<PatternsKrs>();
		if (list.size()>0){
			Iterator<SimpleEvents> i = list.iterator();
			while(i.hasNext()){
				SimpleEvents event = i.next();
				PatternsKrs pattern = dm.minePatterns(event);
				patternList.add(pattern);
				assertNotNull(pattern);				
			}
			dbManager.setPatternsKRS(patternList);
		}else{
			fail("There are not simple events in the database, please create some events first.");
		}
	}*/
	
	/**
	  * testBuildInstancesFromPatterns - JUnit test case whose aim is to test if instances are being properly built from DB data
	  *
	  * @param none 
	  * 
	  */
	/*@Test
	public final void testBuildInstancesFromPatterns() {		
		List<PatternsKrs> patternList = dbManager.getPatternsKRS();
		if (patternList.size()>0){
			Instances data = dm.buildInstancesFromPatterns(patternList);
			if (data != null) {
				Iterator<Instance> i = data.iterator();
				while(i.hasNext()){
					Instance instance = i.next();
					//System.out.println(instance);
					assertNotNull(instance);				
				}
			} else {
				fail("Instances not being properly built.");
			}
						
		} else {
			fail("There are no patterns in the table.");
		}
	}*/
		
	/**
	  * testDataClassification - JUnit test case whose aim is to test execution of current classification algorithms over the built dataset
	  *
	  * @param none 
	  * 
	  */
	@Test
	public final void testDataClassification() {
		List<PatternsKrs> patternList = dbManager.getPatternsKRS();
		if (patternList.size()>0){
			Instances data = dm.buildInstancesFromPatterns(patternList);
			if (data != null) {
				int[] redIndexes = dm.featureSelection(data);
				int[] indexes = new int[data.numAttributes()];
				for (int i = 0; i < data.numAttributes(); i++) {
					indexes[i] = i;
				}
				System.out.println("=== Results before feature selection ===");
				dm.dataClassification(data, indexes);
				if (indexes.length > 0) {
					System.out.println("=== Results after feature selection ===");
					dm.dataClassification(data, redIndexes);
				} else {
					fail("Feature selection not being properly performed");
				}
			} else {
				fail("Instances not being properly built.");
			}
						
		} else {
			fail("There are no patterns in the table.");
		}
	}
	
	/**
	  * testClassifierParser - JUnit test case whose aim is to test if the regular expressions in classifierParser
	  * are correctly built
	  *
	  * @param none 
	  * 
	  */
	@Test
	public final void testClassifierParser() {
		String ruleJRip = "JRIP rules:\n===========\n\n(event_type = SECURITY_PROPERTY_CHANGED) and (device_screen_timeout <= 30) => label=STRONGDENY (18457.0/5980.0)";
		String rulePART = "PART decision list\n------------------\n\ndevice_screen_timeout <= 30 AND\ndevice_is_rooted <= 0 AND\nsilent_mode > 0: STRONGDENY (13985.0/4947.0)";
		String ruleJ48 = "J48 pruned tree\n------------------\n\nevent_type = SECURITY_PROPERTY_CHANGED\n|   device_is_rooted <= 0\n|   |   silent_mode > 0\n"+
		"|   |   |   device_screen_timeout > 30\n   |   |   |   passwd_has_capital_letters <= 3\n|   |   |   |   |   device_has_password <= 0: STRONGDENY (2001.0/774.0)\n"+
				"|   |   |   |   |   device_has_password > 0\n|   |   |   |   |   |   password_length <= 6: GRANTED (67.0)\n"+
		"|   |   |   |   |   |   password_length > 6\n|   |   |   |   |   |   |   device_screen_timeout <= 300: GRANTED (7462.0/3292.0)\n"+
				"|   |   |   |   |   |   |   device_screen_timeout > 300: STRONGDENY (194.0/89.0)";
		String ruleREPTree = "REPTree\n============\n\nevent_type = SECURITY_PROPERTY_CHANGED\n|   passwd_has_capital_letters >= 1.5\n"+
				"|   |   silent_mode < 0.5 : STRONGDENY (3194/747) [1569/354]\n|   |   silent_mode >= 0.5\n"+
				"|   |   |   device_screen_timeout < 90\n|   |   |   |   device_screen_timeout >= 45\n"+
				"|   |   |   |   |   device_is_rooted < 0.5\n|   |   |   |   |   |   letters_in_password < 6.5\n"+
				"|   |   |   |   |   |   |   passwd_has_capital_letters < 3.5\n|   |   |   |   |   |   |   |   device_has_password < 0.5 : STRONGDENY (144/66) [72/36]\n"+
				"|   |   |   |   |   |   |   |   device_has_password >= 0.5 : GRANTED (220/105) [79/37]";
		
		
		List<String> ruleListJRip = dm.classifierParser(ruleJRip);
		List<String> ruleListPART = dm.classifierParser(rulePART);
		List<String> ruleListJ48 = dm.classifierParser(ruleJ48);
		List<String> ruleListREPTree = dm.classifierParser(ruleREPTree);
		
		if (ruleListJRip != null || ruleListPART != null || ruleListJ48 != null || ruleListREPTree != null) {
			Iterator<String> i1 = ruleListJRip.iterator();
			Iterator<String> i2 = ruleListPART.iterator();
			Iterator<String> i3 = ruleListJ48.iterator();
			Iterator<String> i4 = ruleListREPTree.iterator();
			
			while (i1.hasNext()) {
				String rule = i1.next();
				assertNotNull(rule);
			}
			while (i2.hasNext()) {
				String rule = i2.next();
				assertNotNull(rule);
			}
			while (i3.hasNext()) {
				String rule = i3.next();
				assertNotNull(rule);
			}
			while (i4.hasNext()) {
				String rule = i4.next();
				assertNotNull(rule);
			}
		} else {
			fail("Rules not being properly parsed");
		}
		
	}

}
