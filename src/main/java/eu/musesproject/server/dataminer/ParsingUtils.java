/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.dataminer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.SecurityRules;
import eu.musesproject.server.scheduler.ModuleType;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 UGR
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

/**
 * The Class ParsingUtils.
 * 
 * @author Paloma de las Cuevas (UGR)
 * @version June 8, 2015
 */

public class ParsingUtils {
	
	private Logger logger = Logger.getLogger(DataMiner.class);
	private static DBManager dbManager = new DBManager(ModuleType.KRS);

	public ParsingUtils() {
		
	}
	
	/**
	 * Method JRipParser in which weka classifier rules are parsed for the extraction of their
	 * conditions and classes (label applied to patterns which have been classified by that rule)
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * 
	 * @return ruleList List of rules in a format that can be compared to the existing set of rules
	 */
	
	public List<String> JRipParser(String classifierRules){
		
		List<String> ruleList = new ArrayList<String>();
		String ruleJRip = "\\(?(\\w+)([\\s\\>\\=\\<]+)([\\w\\.]+)\\)?";
		String lines[] = classifierRules.split("\\r?\\n");
		int i = 0;
		
		Pattern JRipPattern = Pattern.compile(ruleJRip);
		for (i = 1; i < lines.length; i++) {
			Matcher JRipMatcher = JRipPattern.matcher(lines[i]);
			String rule = "";
			while (JRipMatcher.find()) {
				if(JRipMatcher.group(1).contentEquals("label")) {
					// Label
					rule += "THEN";
					rule += JRipMatcher.group(3);
					ruleList.add(rule);
				} else {
					// Attribute name
					rule += JRipMatcher.group(1);
					/* Relationship, JRipMatcher.group(2) can be =, <, <=, >, >= */
					rule += JRipMatcher.group(2);
					// Value
					rule += JRipMatcher.group(3);
					rule += "AND";
				}
			}
		}
		
		return ruleList;		
		
	}
	
	/**
	 * Method PARTParser in which weka classifier rules are parsed for the extraction of their
	 * conditions and classes (label applied to patterns which have been classified by that rule)
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * 
	 * @return ruleList List of rules in a format that can be compared to the existing set of rules
	 */
	
	public List<String> PARTParser(String classifierRules){
		
		List<String> ruleList = new ArrayList<String>();
		String rulePART = "(\\w+)([\\s\\>\\=\\<]+)(\\w+)\\s?(AND|\\:)?\\s?(\\w*)";
		String lines[] = classifierRules.split("\\r?\\n");
		int i = 0;
		
		Pattern PARTPattern = Pattern.compile(rulePART);
		String rule = "";
		for (i = 1; i < lines.length; i++) {
			Matcher PARTMatcher = PARTPattern.matcher(lines[i]);
			while (PARTMatcher.find()) {					
				// Attribute name
				rule += PARTMatcher.group(1);
				/* Relationship, PARTMatcher.group(2) can be =, <, <=, >, >= */
				rule += PARTMatcher.group(2);
				// Value
				rule += PARTMatcher.group(3);
				rule += "AND";
				if (!PARTMatcher.group(5).isEmpty()) {
					// Label
					rule += "THEN";
					rule += PARTMatcher.group(5);
					ruleList.add(rule);
					rule = "";
				}
			}
		}
		
		return ruleList;		
		
	}
	
	/**
	 * Method J48Parser in which weka classifier rules are parsed for the extraction of their
	 * conditions and classes (label applied to patterns which have been classified by that rule)
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * 
	 * @return ruleList List of rules in a format that can be compared to the existing set of rules
	 */
	
	public List<String> J48Parser(String classifierRules){
		
		List<String> ruleList = new ArrayList<String>();
		String ruleJ48 = "(\\|*\\s*)(\\w+)([\\s\\>\\=\\<]+)(\\w+)\\s?\\:?\\s?(\\w*)";
		String lines[] = classifierRules.split("\\r?\\n");
		int i = 0;
		
		Pattern J48Pattern = Pattern.compile(ruleJ48);
		String rule = "";
		for (i = 1; i < lines.length; i++) {
			Matcher J48Matcher = J48Pattern.matcher(lines[i]);
			while (J48Matcher.find()) {
				// Attribute name
				J48Matcher.group(2);
				/* Relationship, J48Matcher.group(2) can be =, <, <=, >, >= */
				J48Matcher.group(3);
				// Value
				J48Matcher.group(4);
				if (!J48Matcher.group(5).isEmpty()) {
					// Label
					J48Matcher.group(5);
				}
			}
		}
		
		return ruleList;		
		
	}
	
	/**
	 * Method REPTreeParser in which weka classifier rules are parsed for the extraction of their
	 * conditions and classes (label applied to patterns which have been classified by that rule)
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * 
	 * @return ruleList List of rules in a format that can be compared to the existing set of rules
	 */
	
	public List<String> REPTreeParser(String classifierRules){
		
		List<String> ruleList = new ArrayList<String>();
		String ruleREPTree = "(\\|*\\s*)(\\w+)([\\s\\>\\=\\<]+)(\\w+\\.?\\w*)\\s?\\:?\\s?(\\w*)";
		String lines[] = classifierRules.split("\\r?\\n");
		int i = 0;
		
		Pattern REPTreePattern = Pattern.compile(ruleREPTree);
		for (i = 1; i < lines.length; i++) {
			Matcher REPTreeMatcher = REPTreePattern.matcher(lines[i]);
			while (REPTreeMatcher.find()) {
				// Attribute name
				REPTreeMatcher.group(2);
				/* Relationship, REPTreeMatcher.group(2) can be =, <, <=, >, >= */
				REPTreeMatcher.group(3);
				// Value
				REPTreeMatcher.group(4);
				if (!REPTreeMatcher.group(5).isEmpty()) {
					// Label
					REPTreeMatcher.group(5);
				}
			}
		}
		
		return ruleList;		
		
	}
	
	/**
	 * Method DBRulesParser in which rules that are in the database, and written in Drools language
	 * are parsed for the extraction of their conditions and classes (label applied to patterns 
	 * which have been classified by that rule)
	 * 
	 * @param none
	 * 
	 * @return ruleList List of rules in a format that can be compared to classifier rules
	 */
	public List<String> DBRulesParser() {
		
		List<String> ruleList = new ArrayList<String>();
		List<String> ruleContent = new ArrayList<String>();
		List<SecurityRules> dbRules = dbManager.getSecurityRulesByStatus("VALIDATED");
		int i = 0;
		int ruleStarts = 0;
		int ruleEnds = 0;
		
		if (dbRules != null) {
			Iterator<SecurityRules> it = dbRules.iterator();
			while (it.hasNext()) {
				SecurityRules dbRule = it.next();				
				String[] lines = dbRule.getDescription().split("\\r?\\n");
				for (i = 0; i < lines.length; i++) {
					if (lines[i].contentEquals("when")) {
						ruleStarts = i;
					} else if (lines[i].matches("\\s+int\\sid")) {
						ruleEnds = i;
					}
				}
				for (i = ruleStarts; i <= ruleEnds; i++) {
					ruleContent.add(lines[i]);
				}
				/* Type of rule? */
				if (dbRule.getDescription().contains("ChangeSecurityPropertyEvent")) {
					
				} else if (dbRule.getDescription().contains("AppObserverEvent")) {
					
				} else if (dbRule.getDescription().contains("FileObserverEvent")) {
					
				} else if (dbRule.getDescription().contains("LocationEvent")) {
					// TODO: fill in when I read Zones					
				} else if (dbRule.getDescription().contains("EmailEvent")) {
					
				}
			}
		}
		
		return ruleList;
		
	}
	
	/**
	 * Method SecurityPropertyRuleParser for parsing rules which conditions are like:
	 * when
		e: ChangeSecurityPropertyEvent(isTrustedAntivirusInstalled==false)
		...
	 * 
	 * @param droolsRule Rule in the Drools format "when <conditions>* then <action>"
	 * 
	 * @return parsedRule Final rule in the format that is needed
	 */
	public String SecurityPropertyRuleParser(List<String> droolsRule) {
		
		String parsedRule = null;
		
		return parsedRule;
	}
	
	/**
	 * Method AppObserverRuleParser for parsing rules which conditions are like:
	 * when
		e: AppObserverEvent(name=="Wifi Analyzer",event=="open_application")
		...
	 * 
	 * @param droolsRule Rule in the Drools format "when <conditions>* then <action>"
	 * 
	 * @return parsedRule Final rule in the format that is needed
	 */
	public String AppObserverRuleParser(List<String> droolsRule) {
		
		String parsedRule = null;
		
		return parsedRule;
	}
	
	/**
	 * Method FileObserverRuleParser for parsing rules which conditions are like:
	 * when
		e: FileObserverEvent(event=="open_asset", resourceType=="CONFIDENTIAL")
		conn: ConnectivityEvent(wifiConnected==true,wifiEnabled==true, wifiEncryption not matches ".*WPA2.*")
		...
	 * 
	 * @param droolsRule Rule in the Drools format "when <conditions>* then <action>"
	 * 
	 * @return parsedRule Final rule in the format that is needed
	 */
	public String FileObserverRuleParser(List<String> droolsRule) {
		
		String parsedRule = null;
		
		return parsedRule;
	}
	
	/**
	 * Method EmailRuleParser for parsing rules which conditions are like:
	 * when
		v: VirusFoundEvent()
		e:EmailEvent(numberAttachments>0)
		...
	 * 
	 * @param droolsRule Rule in the Drools format "when <conditions>* then <action>"
	 * 
	 * @return parsedRule Final rule in the format that is needed
	 */
	public String EmailRuleParser(List<String> droolsRule) {
		
		String parsedRule = null;
		
		return parsedRule;
	}

}