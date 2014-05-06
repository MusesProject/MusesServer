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
	
	private final String testJSONMessage = ""	
+"	{ \"action\" : { \"properties\" : { \"method\" : \"post\","
+"        \"protocol\" : \"https\","
+"        \"resourceId\" : \"file1.png\","
+"        \"url\" : \"https://...\""
+"      },"
+"    \"timestamp\" : 1399037674634,"
+"    \"type\" : \"open\""
+"  },"
+"\"requesttype\" : \"local_decision\","
+"\"sensor\" : { \"CONTEXT_SENSOR_APP\" : { \"appname\" : \"Muses Aware App\","
+"        \"backgroundprocess\" : \"[com.android.phone, com.google.process.gapps, system, com.android.bluetooth, com.google.process.gapps, com.google.android.talk, com.android.bluetooth, com.android.systemui, com.google.android.music:main,             com.sec.android.app.gamehub, com.android.phone, com.tgrape.android.radar, com.google.process.location, com.android.systemui, com.sec.android.widgetapp.at.hero.accuweather, com.android.phone, com.sec.pcw, com.sec.esdk.elm, com.google.process.location, com.android.location.fused, com.sec.android.inputmethod, com.google.process.gapps, android.process.media, com.google.android.music:main, com.android.phone, com.sec.phone, com.google.process.location, com.sec.factory, com.google.process.location, com.google.android.gms, com.android.phone, com.wssyncmldm, com.google.android.music:main, org.simalliance.openmobileapi.service:remote, eu.musesproject.client, com.android.MtpApplication, com.google.process.location, com.google.android.music:main, com.google.android.music:main, eu.musesproject.client, com.sec.android.pagebuddynotisvc, com.android.phone, system, com.google.process.location, com.google.process.location, com.google.android.gms, com.google.android.music:main, com.google.process.gapps, com.google.android.music:main, com.android.bluetooth]\","
+"        \"id\" : \"2\","
+"        \"timestamp\" : 1399036877111,"
+"        \"type\" : \"CONTEXT_SENSOR_APP\""
+"      },"
+"    \"CONTEXT_SENSOR_CONNECTIVITY\" : { \"airplanemode\" : \"false\","
+"        \"bluetoothconnected\" : \"FALSE\","
+"        \"bssid\" : \"f8:1a:67:83:71:58\","
+"        \"hiddenssid\" : \"false\","
+"        \"id\" : \"3\","
+"        \"networkid\" : \"2\","
+"        \"timestamp\" : 1399036881208,"
+"        \"type\" : \"CONTEXT_SENSOR_CONNECTIVITY\","
+"        \"wificonnected\" : \"true\","
+"        \"wifienabled\" : \"true\","
+"        \"wifiencryption\" : \"unknown\","
+"        \"wifineighbors\" : \"6\""
+"      },"
+"    \"CONTEXT_SENSOR_DEVICE_PROTECTION\" : { \"passwordprotected\" : \"true\","
+"        \"patternprotected\" : \"true\","
+"        \"trustedavinstalled\" : \"true\","
+"        \"isrooted\" : \"false\","
+"        \"id\" : \"1\","
+"        \"timestamp\" : 1399036881212,"
+"        \"type\" : \"CONTEXT_SENSOR_DEVICE_PROTECTION\""

+"      }"
+"  }"
+"}";
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
	  * testProcessEvent - JUnit test case whose aim is to test the redirection of an incoming event to be processed by the CRTEP
	  *
	  * @param none 
	  * 
	  */
	
	 public final void testStartProcessor(){

		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		assertNotNull(des);
	}
	
	/**
	  * testJsonParse - JUnit test case whose aim is to test transformation of a JSON string received from the Connection Manager into the original Context Event
	  *
	  * @param none 
	  * 
	  */
	
	public final void testJsonParse(){
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		List<ContextEvent> list = JSONManager.processJSONMessage(testJSONMessage);
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
			des = EventProcessorImpl.getMusesEngineService();
		}
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			assertNotNull(contextEvent);
			Event formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			des.insertFact(formattedEvent);
		}
	}
	
	

}
