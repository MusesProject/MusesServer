/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 */
package eu.musesproject.server.db.eventcorrelation;

import eu.musesproject.server.continuousrealtimeeventprocessor.model.Event;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.User;

/**
 * Class EventCorrelationData
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */

public class EventCorrelationData {
	
	

	/**
	 * Info DB
	 * 
	 *  Retrieve events from the same user and device, that have not been mined yet.
	 *  This method is called by Data Miner in order to acquire input data to compute new patterns.
	 * 
	 * @param device
	 * 
	 * @param user
	 * 
	 * @return Event[]
	 */
	
	public Event[] retrievePendingEvents( Device device, User user){
		return null;
	}	

}
