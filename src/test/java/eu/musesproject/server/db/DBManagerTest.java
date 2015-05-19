package eu.musesproject.server.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.AccessRequest;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.Clue;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.DecisionTrustvalues;
import eu.musesproject.server.entity.Outcome;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.RiskCommunication;
import eu.musesproject.server.entity.RiskInformation;
import eu.musesproject.server.entity.RiskPolicy;
import eu.musesproject.server.entity.RiskTreatment;
import eu.musesproject.server.entity.Roles;
import eu.musesproject.server.entity.SecurityViolation;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.SystemLogKrs;
import eu.musesproject.server.entity.Threat;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.scheduler.ModuleType;

public class DBManagerTest {
	
	static DBManager dbmanager = null;

	
	@BeforeClass
	public  static void setUpBeforeClass() throws Exception {
		ModuleType module = null;
		dbmanager = new DBManager(module);

	}
	
	@AfterClass
	public  static void setUpAfterClass() throws Exception {
		ModuleType module = null;
		//dbmanager.close();

	}

	@Test
	public void testGetUsers() {
		List<Users> List = dbmanager.getUsers();
		if (List.size()>0){
			Iterator<Users> i = List.iterator();
			while(i.hasNext()){
				Users user = i.next();
				assertNotNull(user);
			}
		}else{
			fail("There is not any User in the database,please first try to store User in the database");
		}			
		
	}

	@Test
	public void testSetUsers() {
		List<Users> list = new ArrayList<Users>();
		Users user = new Users();
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
		List<Assets> List = dbmanager.getAssets();
		if (List.size()>0){
			Iterator<Assets> i = List.iterator();
			while(i.hasNext()){
				Assets asset = i.next();
				assertNotNull(asset);
			}
		}else{
			fail("There is not any Asset in the database,please first try to store Asset in the database");
		}			
		
	}

	@Test
	public void testFindAssetByTitle() {
		String title ="Patent";
		List<Assets> assets = dbmanager.findAssetByTitle(title);
		if(assets.size()>0)
			assertTrue(true);
		else
			fail("There is not any Asset in the database with this title,please first try with another title");


	}

	@Test
	public void testSetAssets() {
		List<Assets> list = new ArrayList<Assets>();
		Assets asset = new Assets();
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
	public void testGetDecisions() {
		List<Decision> List = dbmanager.getDecisions();
		if (List.size()>0 || List == null){
			Iterator<Decision> i = List.iterator();
			while(i.hasNext()){
				Decision decision = i.next();
				assertNotNull(decision);
			}
		}else{
			fail("There is not any Decision in the database,please first try to store Decision in the database");
		}			
		
	}
	
	@Test
	public void testSetDecisions() {
		List<Decision> list = new ArrayList<Decision>();
		Decision decision = new Decision();
		String opensensitivedocumentinunsecurenetwork = "You are trying to open a sensitive document, but you are connected with an unsecured WiFi.\n Other people can observe what you transmit. Switch to a secure WiFi first.";	

		
		decision.setAccessRequest(dbmanager.findAccessRequestById("80").get(0));
		
		ArrayList<RiskCommunication> riskcommunications = new ArrayList<RiskCommunication>();
		
		RiskCommunication riskcommunication = new RiskCommunication();

		riskcommunication.setDescription("JunitTest");
		
		riskcommunications.add(riskcommunication);
		dbmanager.setRiskCommunications(riskcommunication);
		
		//decision.setRiskCommunication(dbmanager.findRiskCommunicationById(900).get(0));
		
		decision.setRiskCommunication(riskcommunication);
		
		List<RiskTreatment> risktreatments = new ArrayList<RiskTreatment>();

		RiskTreatment risktreatment = new RiskTreatment();
		risktreatment.setDescription(opensensitivedocumentinunsecurenetwork); 
		risktreatment.setRiskCommunication(riskcommunication);
		risktreatments.add(risktreatment);
		
		dbmanager.setRiskTreatments(risktreatments);

		//riskcommunication.setRiskTreatments(risktreatments);
		//decision.setRiskCommunication(riskcommunication);
		decision.setValue("GRANTED");
		//decision.setInformation("test");
		//decision.setSolvingRisktreatment(2);
		decision.setTime(new Time(new Date().getTime()));
		//decision.setAccessRequest(accessRequest);
		
		
		list.add(decision);
		dbmanager.setDecisions(list);	
		
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
		//dbmanager.setClues(list); FIXME	
		
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
	
	@Test
	public void testSetSimpleEvents() {
		List<SimpleEvents> list = new ArrayList<SimpleEvents>();
		SimpleEvents event = new SimpleEvents();
		event.setEventType(dbmanager.getEventTypeByKey(EventTypes.LOG_IN));
		event.setUser(dbmanager.getUserByUsername("muses"));
		event.setData("jsonstring");
		event.setApplication(dbmanager.getApplicationByName("musesawaew"));
		event.setAsset(dbmanager.getAssetByLocation("Geneva"));
		event.setDate(new Date());
		event.setDevice(dbmanager.getDeviceByIMEI("9aa326e4fd9ccf61"));
		event.setTime(new Time(new Date().getTime()));
		list.add(event);
		dbmanager.setSimpleEvents(list);

	}
	
	@Test
	public void testSetSystemLogKRS() {
		List<SystemLogKrs> list = new ArrayList<SystemLogKrs>();
		SystemLogKrs logEntry = new SystemLogKrs();
		SecureRandom random = new SecureRandom();
		logEntry.setPreviousEventId(new BigInteger(30, random));
		logEntry.setCurrentEventId(new BigInteger(30, random));
		logEntry.setDecisionId(new BigInteger(30, random));
		logEntry.setUserBehaviourId(new BigInteger(30, random));
		logEntry.setSecurityIncidentId(new BigInteger(30, random));
		logEntry.setDeviceSecurityState(new BigInteger(30, random));
		logEntry.setRiskTreatment(3);
		logEntry.setStartTime(new Date());
		logEntry.setFinishTime(new Date());
		
		list.add(logEntry);
		dbmanager.setSystemLogKRS(list);
		
	}
	
	@Test
	public void testFindAccessRequestByEventId() {
		String eventID = "2";
		List<AccessRequest> accessRequests = dbmanager.findAccessRequestByEventId(eventID);
		if(accessRequests.size()>0)
			assertTrue(true);
		else
			fail("There is not any Access Request corresponding to that event_id.");
	}
	
	@Test
	public void testFindEventsByUserId() {
		String userID = "1";
		String day = "2015-01-09";
		String time = "17:00:00";
		/* To test if it looks for last events */
		List<SimpleEvents> events = dbmanager.findEventsByUserId(userID, day, time, Boolean.TRUE);
		/* To test if it looks for next event */
		//List<SimpleEvents> events = dbmanager.findEventsByUserId(userID, day, time, Boolean.FALSE);
		if(events.size()>0)
			assertTrue(true);
		else
			fail("There is not any Simple Events corresponding to that user_id, in those dates.");
	}
	
	@Test
	public void testSetPatternsKRS() {
		List<PatternsKrs> list = new ArrayList<PatternsKrs>();
		PatternsKrs logEntry = new PatternsKrs();
		logEntry.setActivatedAccount(0);
		logEntry.setAppMUSESAware(0);
		logEntry.setAppName("");
		logEntry.setAppVendor("");
		logEntry.setAssetConfidentialLevel("NONE");
		logEntry.setAssetLocation("");
		logEntry.setAssetName("");
		logEntry.setAssetValue(0);
		logEntry.setDecisionCause("");
		logEntry.setDeviceHasAccessibility(0);
		logEntry.setDeviceHasAntivirus(0);
		logEntry.setDeviceHasCertificate(0);
		logEntry.setDeviceHasPassword(0);
		logEntry.setDeviceIsRooted(0);
		logEntry.setDeviceOS("");
		logEntry.setDeviceOwnedBy("");
		logEntry.setDeviceScreenTimeout(BigInteger.ZERO);
		short zero = 0;
		logEntry.setDeviceSecurityLevel(zero);
		logEntry.setDeviceTrustValue(0);
		logEntry.setDeviceType("");
		logEntry.setEventLevel("");
		logEntry.setEventTime(new Date());
		logEntry.setEventType("");
		logEntry.setLabel("GRANTED");
		logEntry.setLettersInPassword(0);
		logEntry.setMailContainsBCC(0);
		logEntry.setMailContainsCC(0);
		logEntry.setMailHasAttachment(0);
		logEntry.setMailRecipientAllowed(0);
		logEntry.setNumbersInPassword(0);
		logEntry.setPasswdHasCapitalLetters(0);
		logEntry.setPasswordLength(0);
		logEntry.setUsername("");
		logEntry.setUserRole("");
		logEntry.setUserTrustValue(0);
		
		list.add(logEntry);
		dbmanager.setPatternsKRS(list);		
	}
	
	@Test
	public void testGetPatternsKRS() {
		List<PatternsKrs> List = dbmanager.getPatternsKRS();
		if (List.size()>0){
			Iterator<PatternsKrs> i = List.iterator();
			while(i.hasNext()){
				PatternsKrs pattern = i.next();
				assertNotNull(pattern);
			}
		}else{
			fail("There is not any pattern in the database, please start Data Mining process.");
		}			
		
	}
	
	@Test
	public void testFindDecisionTrustValuesByDecisionId() {
		String decisionID = "545";
		List<DecisionTrustvalues> trustValues = dbmanager.findDecisionTrustValuesByDecisionId(decisionID);
		if(trustValues.size()>0)
			assertTrue(true);
		else
			fail("There is not any Decision TrustValue corresponding to that decision_id.");
	}
	
	@Test
	public void testGetRoleById() {
		int roleID = 145;
		Roles role = dbmanager.getRoleById(roleID);
		if(role != null)
			assertTrue(true);
		else
			fail("There is not any Role corresponding to that role_id.");
	}

}
