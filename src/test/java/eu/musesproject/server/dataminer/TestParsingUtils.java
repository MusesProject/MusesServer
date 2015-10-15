package eu.musesproject.server.dataminer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.scheduler.ModuleType;

public class TestParsingUtils {
	
	static ParsingUtils parser = new ParsingUtils();
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
	private Logger logger = Logger.getLogger(TestParsingUtils.class);

	@BeforeClass
	public  static void setUpBeforeClass() throws Exception {

	}
	
	@AfterClass
	public  static void setUpAfterClass() throws Exception {

	}
	
	/**
	  * testClassifierParser - JUnit test case whose aim is to test if the regular expressions in
	  * classifierParser are correctly built
	  *
	  * @param none 
	  * 
	  */
	@Test
	public final void testClassifierParser() {
		String ruleJRip = "JRIP rules:\n===========\n\n(event_type = SECURITY_PROPERTY_CHANGED) and (device_screen_timeout <= 30) => label=STRONGDENY (18457.0/5980.0)\n"+
				"(event_type = SECURITY_PROPERTY_CHANGED) and (passwd_has_capital_letters >= 2) and (device_screen_timeout >= 120) and (letters_in_password >= 7) => label=STRONGDENY (3198.0/911.0)";
		String rulePART = "PART decision list\n------------------\n\ndevice_screen_timeout <= 30 AND\ndevice_is_rooted <= 0 AND\nsilent_mode > 0: STRONGDENY (13985.0/4947.0)\n"+
				"letters_in_password > 3 AND\npasswd_has_capital_letters > 1 AND\ndevice_is_rooted <= 0 AND\ndevice_screen_timeout > 60 AND\nletters_in_password > 6: STRONGDENY (2723.0/773.0)";
		String ruleJ48 = "J48 pruned tree\n------------------\n\nevent_type = SECURITY_PROPERTY_CHANGED\n|   device_is_rooted <= 0\n|   |   silent_mode > 0\n"+
		"|   |   |   device_screen_timeout > 30\n|   |   |   |   passwd_has_capital_letters <= 3\n|   |   |   |   |   device_has_password <= 0: STRONGDENY (2001.0/774.0)\n"+
				"|   |   |   |   |   device_has_password > 0\n|   |   |   |   |   |   password_length <= 6: GRANTED (67.0)\n"+
		"|   |   |   |   |   |   password_length > 6\n|   |   |   |   |   |   |   device_screen_timeout <= 300: GRANTED (7462.0/3292.0)\n"+
				"|   |   |   |   |   |   |   device_screen_timeout > 300: STRONGDENY (194.0/89.0)";
		String ruleREPTree = "REPTree\n============\n\nevent_type = SECURITY_PROPERTY_CHANGED\n|   passwd_has_capital_letters >= 1.5\n"+
				"|   |   silent_mode < 0.5 : STRONGDENY (3194/747) [1569/354]\n|   |   silent_mode >= 0.5\n"+
				"|   |   |   device_screen_timeout < 90\n|   |   |   |   device_screen_timeout >= 45\n"+
				"|   |   |   |   |   device_is_rooted < 0.5\n|   |   |   |   |   |   letters_in_password < 6.5\n"+
				"|   |   |   |   |   |   |   passwd_has_capital_letters < 3.5\n|   |   |   |   |   |   |   |   device_has_password < 0.5 : STRONGDENY (144/66) [72/36]\n"+
				"|   |   |   |   |   |   |   |   device_has_password >= 0.5 : GRANTED (220/105) [79/37]";
		
		
		List<String> ruleListJRip = parser.JRipParser(ruleJRip);
		List<String> ruleListPART = parser.PARTParser(rulePART);
		List<String> ruleListJ48 = parser.J48Parser(ruleJ48);
		List<String> ruleListREPTree = parser.REPTreeParser(ruleREPTree);
		
		if (ruleListJRip != null || ruleListPART != null || ruleListJ48 != null || ruleListREPTree != null) {
			Iterator<String> i1 = ruleListJRip.iterator();
			Iterator<String> i2 = ruleListPART.iterator();
			Iterator<String> i3 = ruleListJ48.iterator();
			Iterator<String> i4 = ruleListREPTree.iterator();
			
			while (i1.hasNext()) {
				String rule = i1.next();
				logger.info("JRIP rule: "+rule);
				assertNotNull(rule);
			}
			while (i2.hasNext()) {
				String rule = i2.next();
				logger.info("PART rule: "+rule);
				assertNotNull(rule);
			}
			while (i3.hasNext()) {
				String rule = i3.next();
				logger.info("J48 rule: "+rule);
				assertNotNull(rule);
			}
			while (i4.hasNext()) {
				String rule = i4.next();
				logger.info("REPTree rule: "+rule);
				assertNotNull(rule);
			}
		} else {
			fail("Rules not being properly parsed");
		}
		
	}
	
	/**
	  * testDBRulesParser - JUnit test case whose aim is to test if the regular expressions in 
	  * DBRulesParser are correctly built
	  *
	  * @param none 
	  * 
	  */
	@Test
	public final void testDBRulesParser() {
		List<String> DBRules = parser.DBRulesParser();
		if (DBRules != null) {
			Iterator<String> i = DBRules.iterator();
			while (i.hasNext()) {
				String rule = i.next();
				logger.info("DB rule: "+rule);
				assertNotNull(rule);
			}
		} else {
			fail ("Rules not being properly parsed");
		}
		
	}

}
