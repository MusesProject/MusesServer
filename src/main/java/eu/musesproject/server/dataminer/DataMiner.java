/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.dataminer;


import eu.musesproject.server.continuousrealtimeeventprocessor.model.*;
import eu.musesproject.server.knowledgerefinementsystem.model.*;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.User;

/**
 * The Class DataMiner.
 * 
 * @author Sergio Zamarripa (S2)
 * @version Sep 30, 2013
 */
public class DataMiner {
	
	
	/**
	 * Info DB
	 * 
	 *   Interaction with the database, retrieving events in bulk, looking for new patterns to be mined. 
	 *   These patterns are the basis for change adaptation, since the system is meant to detect uncoded/unpredicted situations 
	 *   that the system is yet not prepared for. This method calls retrievePendingEventsInDB from EventCorrelationData. Once the
	 *   method retrieves the Events, the pattern mining process starts (minePatterns), looking for new patterns.
	 * 
	 * 
	 * @param device
	 * 
	 * @param user
	 * 
	 * @return void
	 */
	
	public void retrievePendingEvents( Device device, User user){
		
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
