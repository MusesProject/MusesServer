/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 */
package eu.musesproject.server.db.eventcorrelation;

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
