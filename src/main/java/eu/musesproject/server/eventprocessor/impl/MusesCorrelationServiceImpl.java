/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */

/**
 * The Class MusesCorrelationEngineImpl.
 * 
 * @author Sergio Zamarripa (S2)
 * @version 26 sep 2013
 */

package eu.musesproject.server.eventprocessor.impl;

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
import eu.musesproject.server.continuousrealtimeeventprocessor.IMusesCorrelationService;
import eu.musesproject.server.risktrust.Device;

/**
 * The Class MusesCorrelationServiceImpl
 * 
 * @author Sergio Zamarripa (S2)
 * @version Sep 24, 2013
 */

public class MusesCorrelationServiceImpl implements IMusesCorrelationService{

	@Override
	public void insertDeviceInfoInWorkingMemory(Device device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertEventInWorkingMemory(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEventToProcess(Event event, Device device) {
		// TODO Auto-generated method stub
		
	}
	
}