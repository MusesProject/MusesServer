/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.contextdatareceiver;

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


import java.util.Iterator;
import java.util.List;

import eu.musesproject.client.model.contextmonitoring.Event;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.connectionmanager.IConnectionManager;
import eu.musesproject.server.connectionmanager.StubConnectionManager;
import eu.musesproject.server.contextdatareceiver.formatting.EventFormatter;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.db.eventcorrelation.StubEventCorrelationData;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;


/**
 * Class UserContextEventDataReceiver
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */

public class UserContextEventDataReceiver {
	
	private static UserContextEventDataReceiver INSTANCE = new UserContextEventDataReceiver();
	private StubEventCorrelationData data = null;
	
	private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new UserContextEventDataReceiver();
        }
    }
 
    public static UserContextEventDataReceiver getInstance() {
        createInstance();
        return INSTANCE;
    }
	
	/**
	 * Info M
	 * 
	 *  Regarding events to be stored, these will be stored based on several criteria, for example, in order to compute 
	 *  user trust, we should store any event associated to a user action (Event objects whose instance type is UserEvent).
	 *  This method will be in charge of classifying the concrete databases where the event must be stored 
	 * 
	 * @param event
	 * 
	 * 
	 * @return void
	 */
	
	public void storeEvent(Event event){
		StubEventCorrelationData data = getEventCorrelationData();
		data.addEvent(event);
	}
	
	public boolean isConnectionManagerActive(){
		StubConnectionManager stubConnectionManager = (StubConnectionManager)getConnectionManager();		
		return stubConnectionManager.isActive();
	}
	
	public IConnectionManager getConnectionManager(){
		return new StubConnectionManager();
	}
	
	public StubEventCorrelationData getEventCorrelationData(){
		if (data == null){
			return new StubEventCorrelationData();
		}else{
			return data;
		}
	}
	
	public eu.musesproject.server.eventprocessor.correlator.model.owl.Event formatEvent(ContextEvent contextEvent){
		eu.musesproject.server.eventprocessor.correlator.model.owl.Event formattedEvent = null;		
		formattedEvent = EventFormatter.formatContextEvent(contextEvent); 
		return formattedEvent;
	}
	
	public void processContextEventList(List<ContextEvent> list){
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent event = (ContextEvent) iterator.next();
			eu.musesproject.server.eventprocessor.correlator.model.owl.Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(event);
			EventProcessor processor = null;
			MusesCorrelationEngineImpl engine = null;
			DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
			if (des==null){
				processor = new EventProcessorImpl();
				engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
				des = EventProcessorImpl.getMusesEngineService();
			}
			des.insertFact(formattedEvent);
		}
	}

}
