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


import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.client.model.contextmonitoring.Event;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.connectionmanager.IConnectionManager;
import eu.musesproject.server.connectionmanager.StubConnectionManager;
import eu.musesproject.server.contextdatareceiver.formatting.EventFormatter;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.db.eventcorrelation.StubEventCorrelationData;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Devices;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.scheduler.ModuleType;


/**
 * Class UserContextEventDataReceiver
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */

public class UserContextEventDataReceiver {
	
	private static DBManager dbManager = new DBManager(ModuleType.EP);
	private static final String MUSES_TAG = "MUSES_TAG";
	private static UserContextEventDataReceiver INSTANCE = new UserContextEventDataReceiver();
	private StubEventCorrelationData data = null;
	private Logger logger = Logger.getLogger(UserContextEventDataReceiver.class);
	
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
	
	public void processContextEventList(List<ContextEvent> list, String currentSessionId, String username, String deviceId, int requestId){
		
	
		logger.info("processContextEventList: Processing list of "+list.size()+" elements.");
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent event = (ContextEvent) iterator.next();
			eu.musesproject.server.eventprocessor.correlator.model.owl.Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(event);
			EventProcessor processor = null;
			MusesCorrelationEngineImpl engine = null;
			DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		
			if (des==null){
				processor = new EventProcessorImpl();
				engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
				des = EventProcessorImpl.getMusesEngineService();
			}else{
				logger.info("DroolsEngine Service already available");
			}
			if (formattedEvent != null){
				formattedEvent.setSessionId(currentSessionId);
				formattedEvent.setUsername(username);
				formattedEvent.setDeviceId(deviceId);
				//if (requestId != 0){
					formattedEvent.setHashId(requestId);
				//}
				logger.info("Inserting event into the WM:"+formattedEvent);
				try{
					des.insertFact(formattedEvent);
				}catch(NullPointerException e){
					logger.info("formatter Event not inserted due to NullPointerException");
				}
			}else{
				logger.error("Formatted event is null.");
			}
			
			//Database storage of simple events
			
			storeEvent(formattedEvent.getType(), username, "musesawaew", deviceId, "Geneva", event.getProperties().toString());//TODO Identify application and asset for the whole range of event types
		}
	}
	
	public static void storeEvent(String eventType, String username,
			String applicationName, String deviceId, String assetLocation,
			String rawEvent) {

		// Database insertion

		List<SimpleEvents> list = new ArrayList<SimpleEvents>();
		SimpleEvents event = new SimpleEvents();
		event.setEventType(dbManager.getEventTypeByKey(eventType));
		if (event.getEventType() == null){
			Logger.getLogger(UserContextEventDataReceiver.class).error("Event type not found in database, associate to key:"+ eventType);
		}
		Users user = dbManager.getUserByUsername(username);
		if (user != null){
			event.setUser(user);
		}else{
			event.setUser(dbManager.getUserByUsername("notfound"));
		}
		
		event.setData(rawEvent);
		Logger.getLogger(UserContextEventDataReceiver.class).info("Application name:"+ applicationName);
		event.setApplication(dbManager.getApplicationByName(applicationName));
		event.setAsset(dbManager.getAssetByLocation(assetLocation));
		event.setDate(new Date());
		Devices device = dbManager.getDeviceByIMEI(deviceId);
		if (device == null){
			device = new Devices();
			device.setName(username);
			device.setImei(deviceId);
			device.setDeviceType(dbManager.getDeviceTypes().get(0));
			dbManager.setDevice(device);
		}
		event.setDevice(device);
		event.setTime(new Time(new Date().getTime()));
		event.setSource(dbManager.getSourceByName("EP"));
		event.setKRS_can_access(1);
		event.setEP_can_access(1);
		event.setRT2AE_can_access(1);
		list.add(event);
		dbManager.setSimpleEvents(list);

	}

}
