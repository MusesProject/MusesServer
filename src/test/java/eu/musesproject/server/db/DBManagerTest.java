package eu.musesproject.server.db;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Asset;
import eu.musesproject.server.entity.Clue;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.Threat;
import eu.musesproject.server.entity.User;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManagerTest {
	
	static DBManager dbmanager = null;

	
	@BeforeClass
	public  static void setUpBeforeClass() throws Exception {
		ModuleType module = null;
		dbmanager = new DBManager(module);
		dbmanager.open();

	}
	
	@AfterClass
	public  static void setUpAfterClass() throws Exception {
		ModuleType module = null;
		dbmanager.close();

	}

	@Test
	public void testGetUsers() {
		List<User> List = dbmanager.getUsers();
		if (List.size()>0){
			Iterator<User> i = List.iterator();
			while(i.hasNext()){
				User user = i.next();
				assertNotNull(user);
			}
		}else{
			fail("There is not any User in the database,please first try to store User in the database");
		}			
		
	}

	@Test
	public void testSetUsers() {
		List<User> list = new ArrayList<User>();
		User user = new User();
		user.setEmail("user@muse.eu");
		user.setName("Pinkman");
		user.setSurname("Jesse");
		SecureRandom random = new SecureRandom();

		user.setUsername(new BigInteger(30, random).toString(5));
		user.setEmail("jesse.pinkman@muses.eu");
		user.setPassword("walterwhite");
		user.setTrustValue(0.9999);
		user.setRoleId(0);
		user.setEnabled(0);
		list.add(user);
		dbmanager.setUsers(list);	
		
		/*List<User> listusers = dbmanager.findUserByUsername(user.getUsername());

		if(listusers.size()>0)
			assertTrue(true);
		else
			fail("The User was not inserted in the database");*/
		
	}

	@Test
	public void testGetAssets() {
		List<Asset> List = dbmanager.getAssets();
		if (List.size()>0){
			Iterator<Asset> i = List.iterator();
			while(i.hasNext()){
				Asset asset = i.next();
				assertNotNull(asset);
			}
		}else{
			fail("There is not any Asset in the database,please first try to store Asset in the database");
		}			
		
	}

	@Test
	public void testFindAssetByTitle() {
		String title ="Patent";
		List<Asset> assets = dbmanager.findAssetByTitle(title);
		if(assets.size()>0)
			assertTrue(true);
		else
			fail("There is not any Asset in the database with this title,please first try with another title");


	}

	@Test
	public void testSetAssets() {
		List<Asset> list = new ArrayList<Asset>();
		Asset asset = new Asset();
		asset.setDescription("Asset_Unige");
		asset.setConfidentialLevel("PUBLIC");
		asset.setTitle("Patent");
		asset.setValue(0);
		asset.setLocation("Geneva");
		list.add(asset);
		dbmanager.setAssets(list);	
		
		/*List<Asset> listassets = dbmanager.findAssetByTitle(asset.getTitle());

		if(listassets.size()>0)
			assertTrue(true);
		else
			fail("The Asset was not inserted in the database");*/
		
	}

	@Test
	public void testGetClues() {
		List<Clue> List = dbmanager.getClues();
		if (List.size()>0){
			Iterator<Clue> i = List.iterator();
			while(i.hasNext()){
				Clue clue = i.next();
				assertNotNull(clue);
			}
		}else{
			fail("There is not any Clue in the database,please first try to store Clue in the database");
		}	
		
	}

	@Test
	public void testSetClues() {
		List<Clue> list = new ArrayList<Clue>();
		Clue clue = new Clue();
		clue.setValue("Wi-FI");
	
		
		list.add(clue);
		dbmanager.setClues(list);	
		
		/*List<Clue> listclues = dbmanager.findClueByValue(clue.getValue());
		

		if(listclues.size()>0)
			assertTrue(true);
		else
			fail("The Asset was not inserted in the database");*/
		
	}
	
	@Test
	public void testGetOutcomes() {
		List<Outcome> List = dbmanager.getOutcomes();
		if (List.size()>0){
			Iterator<Outcome> i = List.iterator();
			while(i.hasNext()){
				Outcome outcome = i.next();
				assertNotNull(outcome);
			}
		}else{
			fail("There is not any Outcome in the database,please first try to store Outcome in the database");
		}	
		
	}

	

	@Test
	public void testGetThreats() {
		List<Threat> List = dbmanager.getThreats();
		if (List.size()>0){
			Iterator<Threat> i = List.iterator();
			while(i.hasNext()){
				Threat threat = i.next();
				assertNotNull(threat);
			}
		}else{
			fail("There is not any Threat in the database,please first try to store Threat in the database");
		}	
		
	}

	@Test
	public void testFindThreatbydescription() {
		String description ="threat";
		List<Threat> threats = dbmanager.findThreatbydescription(description);
		if(threats.size()>0)
			assertTrue(true);
		else
			fail("There is not any Threat in the database with this description,please first try with another description");	}

	@Test
	public void testSetThreats() {
		Outcome outcome = new Outcome();
		List<Outcome> outcomes = new ArrayList<Outcome>();
		outcome.setCostbenefit(0);
		SecureRandom random = new SecureRandom();
		outcome.setDescription("outcome");
		
		outcomes.add(outcome);
		Threat threat = new Threat();
		threat.setBadOutcomeCount(0);
		threat.setOutcomes(outcomes);
		threat.setDescription("test2");
		threat.setProbability(0);
		
		List<Threat> threats = new ArrayList<Threat>();
		threats.add(threat);
		dbmanager.setThreats(threats);	

		
	}

	@Test
	public void testGetRiskPolicies() {
		List<RiskPolicy> List = dbmanager.getRiskPolicies();
		if (List.size()>0){
			Iterator<RiskPolicy> i = List.iterator();
			while(i.hasNext()){
				RiskPolicy riskpolicy = i.next();
				assertNotNull(riskpolicy);
			}
		}else{
			fail("There is not any RiskPolicy in the database,please first try to store RiskPolicy in the database");
		}
		
	}
	
	@Test
	public void testGetAccessRequests() {
		List<AccessRequest> List = dbmanager.getAccessRequests();
		if (List.size()>0){
			Iterator<AccessRequest> i = List.iterator();
			while(i.hasNext()){
				AccessRequest accessrequest = i.next();
				assertNotNull(accessrequest);
			}
		}else{
			fail("There is not any AccessRequest in the database,please first try to store AccessRequest in the database");
		}
		
	}

	@Test
	public void testSetRiskPolicies() {
		List<RiskPolicy> list = new ArrayList<RiskPolicy>();
		RiskPolicy riskpolicy = new RiskPolicy();
		riskpolicy.setDescription("myrsikpolicy");
		riskpolicy.setRiskvalue(0);
	
		
		list.add(riskpolicy);
		dbmanager.setRiskPolicies(list);
		
	}

	

}
