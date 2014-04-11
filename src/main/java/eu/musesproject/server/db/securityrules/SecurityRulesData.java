/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.db.securityrules;

import eu.musesproject.server.continuousrealtimeeventprocessor.model.Rule;
import eu.musesproject.server.continuousrealtimeeventprocessor.model.SecurityRule;

/**
 * The Class SecurityRulesData.
 * 
 * @author Jean-Marc Seigneur (UNIGE)
 * @version Sep 24, 2013
 */
public class SecurityRulesData {
	
	
	/**
	 * Info DB
	 * 
	 *  Retrieve currently updated rules are the ones meant to be used to detect security incidents that might be against policies. 
	 *  This method is called by IMusesCorrelationEngine during server startup or whenever new rules are available by MusKRS.
	 * 
	 * @param rules 
	 * 
	 * @return void
	 */
	
	public void retrieveUpdatedRules(Rule[] rules){
		
	}
	
	/**
	 * Info KN
	 * 
	 *  Modify existing rule
	 * 
	 * @param id 
	 * @param rule 
	 * 
	 * @return void
	 */
	
	public void modifySecurityRule(Integer id, SecurityRule rule){
		
	}
	
	/**
	 * Info KN
	 * 
	 *  Insert new rule
	 * 
	 * @param rule 
	 * 
	 * @return id of the created rule
	 */
	
	public Integer insertSecurityRule(SecurityRule rule){
		return null;
	}
	
	
	/**
	 * Info KN
	 * 
	 *  Remove existing rule
	 * 
	 * @param rule 
	 * 
	 * @return boolean result of the action
	 */
	
	public boolean removeSecurityRule(SecurityRule rule){
		return false;
	}

}
