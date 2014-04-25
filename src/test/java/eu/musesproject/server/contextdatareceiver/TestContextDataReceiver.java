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

import junit.framework.TestCase;
import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.connectionmanager.StubConnectionManager;
import eu.musesproject.server.contextdatareceiver.stub.ContextEventFactory;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;

public class TestContextDataReceiver extends TestCase {
	
	private final String testJSONMessage = "{" +
			"\""+JSONIdentifiers.ACTION_IDENTIFIER+"\" : "+
   "{"+
   "	\""+JSONIdentifiers.ACTION_TYPE+"\" : \"ACTION_REMOTE_FILE_ACCESS\","+
   "	\""+JSONIdentifiers.ACTION_TIMESTAMP+"\" : \"1389885147\","+
   "	\""+JSONIdentifiers.PROPERTIES_IDENTIFIER+"\": {"+
   "		\"protocol\"   : \"https\","+
   "		\"url\"        : \"https://...\","+
   "		\"resourceId\" : \"file1.png\","+
   "		\"method\"     : \"post\""+
   "	}"+
   "},"+
   "\""+JSONIdentifiers.SENSOR_IDENTIFIER+"\" : {"+
   "	\"connectivity\" : {"+
   "		\""+ContextEvent.KEY_TYPE+"\" : \"CONTEXT_SENSOR_APP\","+
   "		\"timestamp\" : \"1389885147\","+
   "		\"mobileConnected\": \"false\","+
   "		\"wifiEnabled\"    : \"true\","+
   "		\"wifiConnected\"  : \"true\","+
   "		\"wifiNeighbors\"  : \"3\","+
   "		\"hiddenSSID\"     : \"false\","+
   "		\"BSSID\"          : \"01:23:45:67:89:AB\","+
   "		\"networkId\"      : \"1\","+
   "		\"bluetoothConnected\": \"false\","+
   "		\"airplaneMode\"   : \"false\""+
   "	}" +
   //","+
   //"	\"another sensor\" : {"+
   //"	},"+
   "}"+
   "}";

	/**
	  * testStoreEvent - JUnit test case whose aim is to test the storage of an incoming event from the Connection Manager
	  *
	  * @param none
	  * 
	  */
	public final void testStoreEvent() {
		
		UserContextEventDataReceiver receiver = UserContextEventDataReceiver.getInstance();
		StubConnectionManager stubConnectionManager = (StubConnectionManager)receiver.getConnectionManager();
		stubConnectionManager.notifyEvent();
		assertNotNull(receiver.getEventCorrelationData());
	}

	/**
	  * testIsConnectionManagerActive - JUnit test case whose aim is to test the correct activation of the Connection Manager
	  *
	  * @param none
	  * 
	  */
	public final void testIsConnectionManagerActive() {
		UserContextEventDataReceiver receiver = UserContextEventDataReceiver.getInstance();
		assertTrue(receiver.isConnectionManagerActive());
	}
	
	
	/**
	  * testFormatEvent - JUnit test case whose aim is to test transformation of a context event to a Complex Event Processing fact,
	  * which would be ready to be inserted into the MUSES Event Processor working memory
	  *
	  * @param none 
	  * 
	  */
	
	public final void testFormatEvent(){
		//ContextEvent event = ContextEventFactory.createFileObserverContextEvent();//TODO Test File Observer when it implements Context Event
		ContextEvent event = ContextEventFactory.createConnectivityContext();
		Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(event);
		assertNotNull(formattedEvent);
	}
	
	/**
	  * testProcessEvent - JUnit test case whose aim is to test the redirection of an incoming event to be processed by the CRTEP
	  *
	  * @param none 
	  * 
	  */
	
	 public final void testProcessEvent(){
		ContextEvent event = ContextEventFactory.createConnectivityContext();
		Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(event);
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		des.insertFact(formattedEvent);
		
		ContextEvent fileEvent = ContextEventFactory.createFileObserverContextEvent();
		Event formattedfileEvent = UserContextEventDataReceiver.getInstance().formatEvent(fileEvent);
		des.insertFact(formattedfileEvent);
		assertNotNull(des);
	}
	
	/**
	  * testProcessEvent - JUnit test case whose aim is to test the redirection of an incoming event to be processed by the CRTEP
	  *
	  * @param none 
	  * 
	  */
	
	public final void testProcessEvent1(){//TODO Changes for System test
		ContextEvent event = ContextEventFactory.createConnectivityContext1();
		Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(event);
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		des.insertFact(formattedEvent);
		
		ContextEvent fileEvent = ContextEventFactory.createFileObserverContextEvent();
		Event formattedfileEvent = UserContextEventDataReceiver.getInstance().formatEvent(fileEvent);
		des.insertFact(formattedfileEvent);
		assertNotNull(des);
	}
	/**
	  * testJsonParse - JUnit test case whose aim is to test transformation of a JSON string received from the Connection Manager into the original Context Event
	  *
	  * @param none 
	  * 
	  */
	
	public final void testJsonParse(){
		List<ContextEvent> list = JSONManager.processJSONMessage(testJSONMessage);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			des.insertFact(formattedEvent);
		}
	}	
	

}
