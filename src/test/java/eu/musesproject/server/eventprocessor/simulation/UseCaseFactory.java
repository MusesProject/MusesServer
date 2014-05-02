package eu.musesproject.server.eventprocessor.simulation;

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






import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;
import eu.musesproject.server.contextdatareceiver.stub.ContextEventFactory;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.util.ActionTypes;
import eu.musesproject.server.eventprocessor.util.EventTypes;


public class UseCaseFactory {
	
	public static final String ROLE_USER = "User";
	public static final String ROLE_EMPLOYEE = "Employee";
	public static final String ROLE_MANAGER = "Manager";
	public static final String CHILD = "C";
	public static final String DESCENDANTS = "D";
	
	public static List<ContextEvent> sequenceUserConnectingIntranet(){
		List<ContextEvent> sequenceList = new ArrayList<ContextEvent>();
		ContextEvent connEvent = new ContextEvent();
		connEvent.setType(EventTypes.CONNECTIVITY);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("id", "3");
		properties.put("airplanemode", "false");
		properties.put("bluetoothconnected", BluetoothState.TRUE.toString());
		properties.put("bssid", "AcmeIntranet");
		properties.put("hiddenssid","true");
		properties.put("mobileconnected", "true");
		properties.put("networkid", "1");
		properties.put("wificonnected", "true");
		properties.put("wifienabled", "true");
		properties.put("wifineighbors", "0");
		connEvent.setTimestamp(new Date().getTime());
		connEvent.setProperties(properties);
		
		sequenceList.add(connEvent);
		return sequenceList;
	}
	
	public static List<ContextEvent>  sequenceConnectionChanges(){
		List<ContextEvent> sequenceList = new ArrayList<ContextEvent>();
		ContextEvent connEvent1 = new ContextEvent();
		connEvent1.setType(EventTypes.CONNECTIVITY);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("id", "4");
		properties.put("airplanemode", "false");
		properties.put("bluetoothconnected", BluetoothState.TRUE.toString());
		properties.put("bssid", "Orange");
		properties.put("hiddenssid","true");
		properties.put("mobileconnected", "true");
		properties.put("networkid", "4");
		properties.put("wificonnected", "true");
		properties.put("wifienabled", "true");
		properties.put("wifineighbors", "0");
		connEvent1.setTimestamp(new Date().getTime());
		connEvent1.setProperties(properties);
		
		ContextEvent connEvent2 = new ContextEvent();
		connEvent2.setType(EventTypes.CONNECTIVITY);
		properties.put("bluetoothconnected", BluetoothState.FALSE.toString());
		connEvent2.setTimestamp(new Date().getTime());
		connEvent2.setProperties(properties);
		
		sequenceList.add(connEvent1);
		sequenceList.add(connEvent2);
		return sequenceList;
	}
	
	public static List<ContextEvent> sequenceUnsecureWifi(){
		List<ContextEvent> sequenceList = new ArrayList<ContextEvent>();
		ContextEvent connEvent = new ContextEvent();
		connEvent.setType(EventTypes.CONNECTIVITY);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("id", "5");
		properties.put("airplanemode", "false");
		properties.put("bluetoothconnected", BluetoothState.TRUE.toString());
		properties.put("bssid", "Airport");
		properties.put("hiddenssid","false");
		properties.put("mobileconnected", "true");
		properties.put("networkid", "1");
		properties.put("wificonnected", "true");
		properties.put("wifienabled", "true");
		properties.put("wifineighbors", "200");
		connEvent.setTimestamp(new Date().getTime());
		connEvent.setProperties(properties);
		
		sequenceList.add(connEvent);
		return sequenceList;
	}
	
	public static List<ContextEvent> sequenceUnsafeCommSettings(){
		
		ContextEvent fileEvent = ContextEventFactory.createFileObserverContextEvent();

		
		List<ContextEvent> sequenceList = new ArrayList<ContextEvent>();
		ContextEvent connEvent = new ContextEvent();
		connEvent.setType(EventTypes.CONNECTIVITY);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("id", "6");
		properties.put("airplanemode", "false");
		properties.put("bluetoothconnected", BluetoothState.TRUE.toString());
		properties.put("bssid", "Airport");
		properties.put("hiddenssid","false");
		properties.put("mobileconnected", "true");
		properties.put("networkid", "1");
		properties.put("wificonnected", "true");
		properties.put("wifienabled", "true");
		properties.put("wifineighbors", "200");
		connEvent.setTimestamp(new Date().getTime());
		connEvent.setProperties(properties);
		
		sequenceList.add(fileEvent);
		sequenceList.add(connEvent);
		return sequenceList;
	}

	public static List<ContextEvent> sequenceSensitiveInfoInFile() {
		List<ContextEvent> sequenceList = new ArrayList<ContextEvent>();
		ContextEvent fileEvent = new ContextEvent();
		fileEvent.setType(EventTypes.FILEOBSERVER);
		Map<String,String> properties = new HashMap<String, String>();
		properties.put("id","0");
		properties.put("path", "/server/company/assets/confidential/patent.doc");
		fileEvent.setTimestamp(new Date().getTime());
		properties.put("event",ActionTypes.OPEN);
		fileEvent.setProperties(properties);
		
		sequenceList.add(fileEvent);
		return sequenceList;
		
	}
	
	public static String getUserByRole(String role){
		String result = null;
		if (role.equals(ROLE_USER)){
			result = "bob";
		}else if (role.equals(ROLE_EMPLOYEE)){
			result = "alice";
		}else if (role.equals(ROLE_MANAGER)){
			result = "peter";
		}
		return result;
	}
	

	
	

}