/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.db.securityrules;

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
