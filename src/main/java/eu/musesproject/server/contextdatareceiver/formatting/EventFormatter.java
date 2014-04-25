package eu.musesproject.server.contextdatareceiver.formatting;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.util.ActionTypes;
import eu.musesproject.server.eventprocessor.util.EventTypes;



public class EventFormatter {
	
	public static Event formatContextEvent(ContextEvent contextEvent){
		Event cepFileEvent = null;
		if (contextEvent.getType().equals(EventTypes.FILEOBSERVER)){
			cepFileEvent = convertToFileObserverEvent(contextEvent);			
		}else if (contextEvent.getType().equals(EventTypes.CONNECTIVITY)){
			cepFileEvent = convertToConnectivityEvent(contextEvent);
		}else if (contextEvent.getType().equals(EventTypes.CONNECTIVITY)){
			cepFileEvent = convertToConnectivityEvent(contextEvent);
		}
		cepFileEvent.setType(contextEvent.getType());
		return (Event)cepFileEvent;
		
	}
	public static ConnectivityEvent convertToConnectivityEvent(ContextEvent contextEvent){			
			ConnectivityEvent cepFileEvent = new ConnectivityEvent();
			Map<String,String> properties = contextEvent.getProperties();
			cepFileEvent.setAirplaneMode(Boolean.valueOf(properties.get("airplaneMode")));
			cepFileEvent.setBluetoothConnected(properties.get("bluetoothConnected"));
			//cepFileEvent.setId(Integer.valueOf(properties.get("id")));
			cepFileEvent.setType(contextEvent.getType());
			cepFileEvent.setBssid(properties.get("BSSID"));
			cepFileEvent.setHiddenSSID(Boolean.valueOf(properties.get("hiddenSSID")));
			cepFileEvent.setMobileConnected(Boolean.valueOf(properties.get("mobileConnected")));
			cepFileEvent.setWifiConnected(Boolean.valueOf(properties.get("wifiConnected")));
			cepFileEvent.setWifiEnabled(Boolean.valueOf(properties.get("wifiEnabled")));
			cepFileEvent.setNetworkId(Integer.valueOf(properties.get("networkId")));
			cepFileEvent.setWifiNeighbors(Integer.valueOf(properties.get("wifiNeighbors")));
			cepFileEvent.setTimestamp(contextEvent.getTimestamp());		
			//cepFileEvent.setUid(properties.get("id"));
			return cepFileEvent;
	}
	
	public static FileObserverEvent convertToFileObserverEvent(ContextEvent contextEvent){
		FileObserverEvent cepFileEvent = new FileObserverEvent();
		Map<String,String> properties = contextEvent.getProperties();
		if (properties.get("event")!=null){//TODO Changes for System test
			cepFileEvent.setEvent(properties.get("event"));
		}		
		cepFileEvent.setEvent(properties.get("method"));//TODO Changes for System test
		if (properties.get("id")!=null){//TODO Changes for System test
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		}		
		cepFileEvent.setType(contextEvent.getType());
		cepFileEvent.setPath(properties.get("path"));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setUid(properties.get("id"));
		return cepFileEvent;
	}

}
