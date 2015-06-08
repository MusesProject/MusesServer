/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.dataminer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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

	public ParsingUtils() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Method classifierParser in which weka classifier rules are parsed for the extraction of their
	 * conditions and classes (label applied to patterns which have been classified by that rule)
	 * 
	 * @param classifierRules Rules obtained by the classifier
	 * 
	 * @return ruleList List of rules in a format that can be compared to the existing set of rules
	 */
	
	public List<String> classifierParser(String classifierRules){
		
		List<String> ruleList = new ArrayList<String>();
		String ruleJRip = "\\((\\w+)([\\s\\>\\=\\<]+)([\\w\\.]+)\\)";
		String labelJRip = "\\=\\>\\s\\w+\\=(\\w+)";
		String rulePART = "(\\w+)([\\s\\>\\=\\<]+)(\\w+)\\s?(AND|\\:)?\\s?(\\w*)";
		String ruleJ48 = "\\|*\\s*(\\w+)([\\s\\>\\=\\<]+)(\\w+)\\s?\\:?\\s?(\\w*)";
		String ruleREPTree = "";
		String lines[] = classifierRules.split("\\r?\\n");
		int i = 0;
		
		if (lines[0].contains("JRIP")) {
			Pattern JRipPattern = Pattern.compile(ruleJRip);
			Pattern JRipLabelPattern = Pattern.compile(labelJRip);
			for (i = 1; i < lines.length; i++) {
				Matcher JRipMatcher = JRipPattern.matcher(lines[i]);
				Matcher JRipLabelMatcher = JRipLabelPattern.matcher(lines[i]);
				while (JRipMatcher.find()) {
					// Attribute name
					JRipMatcher.group(1);
					/* Relationship, JRipMatcher.group(2) can be =, <, <=, >, >= */
					JRipMatcher.group(2);
					// Value
					JRipMatcher.group(3);
				}
				if (JRipLabelMatcher.find()) {
					// Label
					JRipLabelMatcher.group(1);
				}
			}
		}
		if (lines[0].contains("PART")) {
			Pattern PARTPattern = Pattern.compile(rulePART);
			for (i = 1; i < lines.length; i++) {
				Matcher PARTMatcher = PARTPattern.matcher(lines[i]);
				while (PARTMatcher.find()) {					
					// Attribute name
					PARTMatcher.group(1);
					/* Relationship, PARTMatcher.group(2) can be =, <, <=, >, >= */
					PARTMatcher.group(2);
					// Value
					PARTMatcher.group(3);
					if (!PARTMatcher.group(5).isEmpty()) {
						// Label
						PARTMatcher.group(5);
					}
				}
			}
			
		}
		if (lines[0].contains("J48")) {
			Pattern J48Pattern = Pattern.compile(ruleJ48);
			for (i = 1; i < lines.length; i++) {
				Matcher J48Matcher = J48Pattern.matcher(lines[i]);
				while (J48Matcher.find()) {
					logger.info(J48Matcher.group(1));
					logger.info(J48Matcher.group(2));
					logger.info(J48Matcher.group(3));
					// Attribute name
					J48Matcher.group(1);
					/* Relationship, PARTMatcher.group(2) can be =, <, <=, >, >= */
					J48Matcher.group(2);
					// Value
					J48Matcher.group(3);
					if (!J48Matcher.group(4).isEmpty()) {
						logger.info(J48Matcher.group(4));
						// Label
						J48Matcher.group(4);
					}
				}
			}
			
		}
		if (lines[0].contains("REPTree")) {
			
		}
		
		return ruleList;		
		
	}

}
