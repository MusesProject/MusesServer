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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.util.EventTypes;


public class JSONManager {
	public Logger logger = Logger.getLogger(JSONManager.class.getName());
	
	/**
	 * Method to be called by the ConnectionManager/DataHandler when the message is originated from the sensors
	 * @param String root of the JSON message
	 * @return void
	 * @throws JSONException
	 */
	public static List<ContextEvent> processJSONMessage(String message){
		//Action action = null;
		Map<String,String> properties = null;
		ContextEvent contextEvent = null;
		List<ContextEvent> resultList = new ArrayList<ContextEvent>();
		Logger.getLogger(JSONManager.class).info("JSONMessage received: Processing message...");
		try {
			// Process the root JSON object
			JSONObject root = new JSONObject(message);		
			//TODO Get the action part
			JSONObject actionJson = root.getJSONObject(JSONIdentifiers.ACTION_IDENTIFIER);
			

			contextEvent = extractContextEvent(actionJson);
			resultList.add(contextEvent);

			
			// Get the List<ContextEvent> from each sensor			
			JSONObject sensorJson = root.getJSONObject(JSONIdentifiers.SENSOR_IDENTIFIER);

			for (Iterator iterator = sensorJson.keys(); iterator.hasNext();) {
				String contextEventType = (String) iterator.next();
				JSONObject contextEventJson = sensorJson.getJSONObject(contextEventType);
				contextEvent = extractContextEvent(contextEventJson);
				resultList.add(contextEvent);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return resultList;
		
	}
	
	

	
	/**
	 * conversion of JSONObject of each one of the context events in the list of each sensor
	 * @param JSONObject 
	 * @return ContextEvent 
	 * @throws JSONException
	 */
	private static ContextEvent extractContextEvent(JSONObject contextEventJson) throws JSONException {

		//TODO Retrieve context event of each JSONObject		
		ContextEvent contextEvent = null;
		Map<String,String> properties = null;
		String contextEventType = null;
		String value = null;
		
		try{
			contextEventType = contextEventJson.getString(ContextEvent.KEY_TYPE);
		
			if ((contextEventJson != null)&&(contextEventType != null)){
				contextEvent = new ContextEvent();
				contextEvent.setType(contextEventType);
				contextEvent.setTimestamp(contextEventJson.getLong(ContextEvent.KEY_TIMESTAMP));
				properties = new HashMap<String,String>();
				for (Iterator iterator = contextEventJson.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if ((!key.equals(ContextEvent.KEY_TYPE))&&(!key.equals(ContextEvent.KEY_TIMESTAMP))){
						value = contextEventJson.getString(key);
						properties.put(key, value);
					}
				}
				contextEvent.setProperties(properties);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return contextEvent;
	}

}
