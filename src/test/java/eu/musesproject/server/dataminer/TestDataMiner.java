package eu.musesproject.server.dataminer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.log4j.Logger;

import weka.core.Instance;
import weka.core.Instances;
import eu.musesproject.server.dataminer.DataMiner;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.scheduler.ModuleType;

public class TestDataMiner {
	
	static DataMiner dm = new DataMiner();
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
	
	@BeforeClass
	public  static void setUpBeforeClass() throws Exception {

	}
	
	@AfterClass
	public  static void setUpAfterClass() throws Exception {

	}
	
	/**
	 * testDataMiner - JUnit test which tests the complete functionality of the Data Miner, obtaining the final output
	 */
	@Test
	public final void testDataMiner() {
		dm.ruleComparison();
	}
	
	/**
	  * testRetrieveUnprocessed - JUnit test case whose aim is to test unprocessed data retrieval from the database
	  *
	  * @param none 
	  * 
	  */
	/*@Test
	public final void testRetrievePendingEvents() {
		
		List<SimpleEvents> eventList = dbManager.getEvent();
		
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
		List<SimpleEvents> list = dbManager.getEvent();
		List<PatternsKrs> outPatternList = new ArrayList<PatternsKrs>();
		if (list.size()>0){
			Iterator<SimpleEvents> i = list.iterator();
			while(i.hasNext()) {
				SimpleEvents event = i.next();
				List<PatternsKrs> inPatternList = dm.minePatterns(event);
				Iterator<PatternsKrs> j = inPatternList.iterator();
				while(j.hasNext()) {
					PatternsKrs pattern = j.next();
					outPatternList.add(pattern);
					assertNotNull(pattern);
				}			
			}
			dbManager.setPatternsKRS(outPatternList);
		}else{
			fail("There are not simple events in the database, please create some events first.");
		}
	}
	
	/**
	  * testBuildInstancesFromPatterns - JUnit test case whose aim is to test if instances are being properly built from DB data
	  *
	  * @param none 
	  * 
	  */
	@Test
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
	}
		
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
	  * testApriori - JUnit test case whose aim is to test execution of A Priori association algorithms over the built dataset
	  *
	  * @param none 
	  * 
	  */
	@Test
	public final void testApriori() {
		List<PatternsKrs> patternList = dbManager.getPatternsKRS();
		String associationRules = null;
		if (patternList.size()>0){
			Instances data = dm.buildInstancesFromPatterns(patternList);
			if (data != null) {
				int[] indexesReview = new int[data.numAttributes()];
                indexesReview[0] = 11; // User role
                indexesReview[1] = 13; // Device Type
                indexesReview[2] = 14; // Device OS
                indexesReview[3] = 18; // Device Owner
                indexesReview[4] = 38; // Label
                associationRules = dm.associationRules(data, indexesReview);
                if (associationRules != null) {
                	System.out.println("A priori rules:\n"+associationRules);
                }
			} else {
				fail("Instances not being properly built.");
			}
						
		} else {
			fail("There are no patterns in the table.");
		}
	}	
	

}
