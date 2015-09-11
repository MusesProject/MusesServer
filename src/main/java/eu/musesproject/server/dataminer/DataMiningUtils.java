/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.dataminer;

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

import java.util.List;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Decision;
import eu.musesproject.server.entity.PatternsKrs;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.scheduler.ModuleType;

import org.apache.log4j.Logger;

/**
 * The Class DataMiningUtils.
 * 
 * @author Paloma de las Cuevas (UGR)
 * @version Aug 30, 2015
 */
public class DataMiningUtils {
	
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(DataMiner.class);
	
	/**
	  * obtainLabel - 
	  *
	  * @param accessRequestId
	  * 
	  * @return label
	  * 
	  */
	public String obtainLabel(String accessRequestId){
		
		List<Decision> decisions = dbManager.findDecisionByAccessRequestId(accessRequestId);
		if (decisions.size() > 0) {
			return decisions.get(0).getValue();
		} else {
			return "ALLOW";
		}
		
	}


}
