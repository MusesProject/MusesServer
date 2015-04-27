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

import java.util.Iterator;
import java.util.List;

import eu.musesproject.server.continuousrealtimeeventprocessor.model.*;
import eu.musesproject.server.knowledgerefinementsystem.model.*;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.scheduler.ModuleType;

import org.apache.log4j.Logger;

import eu.musesproject.server.scheduler.ModuleType;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Users;

/**
 * The Class DataMiner.
 * 
 * @author Sergio Zamarripa (S2)
 * @version Sep 30, 2013
 */
public class DataMiner {
	
	private static DBManager dbManager = new DBManager(ModuleType.KRS);
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(DataMiner.class);
	
	public List<SimpleEvents> getSimpleEvents() {
		
		List<SimpleEvents> Events = dbManager.getEvent();
		
		return Events;
	}
	
	
	
	/**
	 * Info DB
	 * 
	 *   Interaction with the database, retrieving events in bulk, and fills the system_log_krs table in the server database. This table helps the CSO having an overview of the status of the system.
	 * 
	 * 
	 * @param events Complete list of simple events, stored in the simple_events table of the database.
	 * 
	 * 
	 * @return void
	 */
	
	public void retrievePendingEvents(List<SimpleEvents> events){
		
		
		
	}
	
	@SuppressWarnings("unused")
	private void minePatterns(Event[] events, Device device, User user){
		
	}
	
	
	/**
	 * Info DM
	 * 
	 *    The Data Miner needs a list of clue patterns, in other words, already expected patterns (currently supported by Muses
	 *    Security Rules) that are prone to be adapted to other related events (or slight modifications of current events) that
	 *    might be happening at the same time.
	 * 
	 * 
	 * @param patterns
	 * 
	 * 
	 * @return void
	 */
	
	public void updateCluePatterns(Pattern[] patterns){
		
	}

}
