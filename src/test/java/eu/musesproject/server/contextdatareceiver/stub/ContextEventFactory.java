package eu.musesproject.server.contextdatareceiver.stub;

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

import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.util.ActionTypes;
import eu.musesproject.server.eventprocessor.util.EventTypes;

public class ContextEventFactory {

	public static ContextEvent createFileObserverContextEvent(){
			ContextEvent fileEvent = new ContextEvent();
			fileEvent.setType(EventTypes.FILEOBSERVER);
			Map<String,String> properties = new HashMap<String, String>();
			properties.put("id","0");
			properties.put("path", "/home/user/patent.doc");
			fileEvent.setTimestamp(new Date().getTime());
			properties.put("event",ActionTypes.OPEN);
			fileEvent.setProperties(properties);
			return fileEvent;
	}
	
	public static ContextEvent createConnectivityContext(){
		ContextEvent connEvent = new ContextEvent();
		connEvent.setType(EventTypes.CONNECTIVITY);
		Map<String,String> properties = new HashMap<String,String>();
		properties.put("id", "1");
		properties.put("airplaneMode", "false");
		properties.put("bluetoothConnected", BluetoothState.TRUE.toString());
		properties.put("BSSID", "34234");
		properties.put("hiddenSSID","false");
		properties.put("mobileConnected", "true");
		properties.put("networkId", "0");
		properties.put("wifiConnected", "true");
		properties.put("wifiEnabled", "true");
		properties.put("wifiNeighbors", "250");
		connEvent.setTimestamp(new Date().getTime());
		connEvent.setProperties(properties);
		return connEvent;
}

}
