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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.decisiontable.ActionType;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.entity.ConnectionConfig;
import eu.musesproject.server.entity.MusesConfig;
import eu.musesproject.server.entity.SensorConfiguration;
import eu.musesproject.server.entity.Zone;
import eu.musesproject.server.eventprocessor.util.EventTypes;


public class JSONManager {
	public static Logger logger = Logger.getLogger(JSONManager.class.getName());
	
	/**
	 * Method to be called by the ConnectionManager/DataHandler when the message is originated from the sensors
	 * @param String root of the JSON message
	 * @return void
	 * @throws JSONException
	 * 
	 */
	
	public static List<ContextEvent> processJSONMessage(String message,	String requestType) {
		// Action action = null;
		Map<String, String> properties = null;
		ContextEvent contextEvent = null;
		String username = null;
		String deviceId = null;
		List<ContextEvent> resultList = new ArrayList<ContextEvent>();
		if (requestType.equals(RequestType.UPDATE_CONTEXT_EVENTS)) {
			Logger.getLogger(JSONManager.class)
					.log(Level.INFO,
							"Update context events JSONMessage received: Processing message...");
		} else if ((requestType.equals(RequestType.ONLINE_DECISION)||(requestType.equals(RequestType.LOCAL_DECISION)))) {// TODO Remove LOCAL_DECISION when sensors are updated conveniently
			Logger.getLogger(JSONManager.class)
					.log(Level.INFO,
							"Online decision JSONMessage received: Processing message...");

			try {
				// Process the root JSON object
				JSONObject root = new JSONObject(message);
				
				// Get the action part
				JSONObject actionJson = root
						.getJSONObject(JSONIdentifiers.ACTION_IDENTIFIER);

				contextEvent = extractActionContextEvent(actionJson);
				if (contextEvent != null){
					logger.log(Level.INFO, "Correct action extraction for message:" + message);
					resultList.add(contextEvent);
				}else{
					logger.log(Level.INFO, "Unsupported json message:" + message);
				}
				// Get the List<ContextEvent> from each sensor
				JSONObject sensorJson = root
						.getJSONObject(JSONIdentifiers.SENSOR_IDENTIFIER);

				for (Iterator iterator = sensorJson.keys(); iterator.hasNext();) {
					String contextEventType = (String) iterator.next();
					JSONObject contextEventJson = sensorJson
							.getJSONObject(contextEventType);
					contextEvent = extractContextEvent(contextEventJson);
					Logger.getLogger(JSONManager.class.getName()).log(
							Level.INFO, "A new event has been received.");
					printContextEventInfo(contextEvent);
					resultList.add(contextEvent);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (requestType.equals(RequestType.USER_ACTION)) {
			
			// Process the root JSON object
			JSONObject root;
			try {
				root = new JSONObject(message);
				// TODO Get the behavior part
				JSONObject behaviorJson = root
						.getJSONObject(JSONIdentifiers.USER_BEHAVIOR);
				contextEvent = new ContextEvent();
				contextEvent.setType(EventTypes.USERBEHAVIOR);
				properties = new HashMap<String,String>();
				for (Iterator iterator = behaviorJson.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if ((!key.equals(ContextEvent.KEY_TYPE))&&(!key.equals(ContextEvent.KEY_TIMESTAMP))){
						String value = behaviorJson.getString(key);
						properties.put(key, value);
					}
				}
				contextEvent.setProperties(properties);
				Logger.getLogger(JSONManager.class.getName()).log(
						Level.INFO, "A new event has been received.");
				printContextEventInfo(contextEvent);
				resultList.add(contextEvent);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return resultList;

	}
	
	public static List<ContextEvent> processJSONMessage(String message) {
		// Action action = null;
		Map<String, String> properties = null;
		ContextEvent contextEvent = null;
		String username = null;
		String deviceId = null;
		String requestType = null;
		JSONObject root = null;

		try{
			// Process the root JSON object
			root = new JSONObject(message);
			requestType = root.getString(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER);
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<ContextEvent> resultList = new ArrayList<ContextEvent>();
		if (requestType.equals(RequestType.UPDATE_CONTEXT_EVENTS)) {
			Logger.getLogger(JSONManager.class)
					.log(Level.INFO,
							"Update context events JSONMessage received: Processing message...");
		} else if ((requestType.equals(RequestType.ONLINE_DECISION)||(requestType.equals(RequestType.LOCAL_DECISION)))) {// TODO Remove LOCAL_DECISION when sensors are updated conveniently
			Logger.getLogger(JSONManager.class)
					.log(Level.INFO,
							"Online decision JSONMessage received: Processing message...");

			try {
				
				// Get the action part
				JSONObject actionJson = root
						.getJSONObject(JSONIdentifiers.ACTION_IDENTIFIER);

				contextEvent = extractActionContextEvent(actionJson);
				resultList.add(contextEvent);

				// Get the List<ContextEvent> from each sensor
				JSONObject sensorJson = root
						.getJSONObject(JSONIdentifiers.SENSOR_IDENTIFIER);

				for (Iterator iterator = sensorJson.keys(); iterator.hasNext();) {
					String contextEventType = (String) iterator.next();
					JSONObject contextEventJson = sensorJson
							.getJSONObject(contextEventType);
					contextEvent = extractContextEvent(contextEventJson);
					Logger.getLogger(JSONManager.class.getName()).log(
							Level.INFO, "A new event has been received.");
					printContextEventInfo(contextEvent);
					resultList.add(contextEvent);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (requestType.equals(RequestType.USER_ACTION)) {
			
			try {
				// TODO Get the behavior part
				JSONObject behaviorJson = root
						.getJSONObject(JSONIdentifiers.USER_BEHAVIOR);
				contextEvent = new ContextEvent();
				contextEvent.setType(EventTypes.USERBEHAVIOR);
				properties = new HashMap<String,String>();
				for (Iterator iterator = behaviorJson.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if ((!key.equals(ContextEvent.KEY_TYPE))&&(!key.equals(ContextEvent.KEY_TIMESTAMP))){
						String value = behaviorJson.getString(key);
						properties.put(key, value);
					}
				}
				contextEvent.setProperties(properties);
				Logger.getLogger(JSONManager.class.getName()).log(
						Level.INFO, "A new event has been received.");
				printContextEventInfo(contextEvent);
				resultList.add(contextEvent);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return resultList;

	}
	
	
	
	public static void printContextEventInfo(ContextEvent contextEvent){
		Map<String, String> properties = null;
		if ((contextEvent!=null)&&(contextEvent.getType()!=null)){
			Logger.getLogger(JSONManager.class.getName()).log(Level.INFO, "		Event type:" + contextEvent.getType());
		}
		properties = contextEvent.getProperties();
		if (properties != null){
			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				Logger.getLogger(JSONManager.class.getName()).log(Level.INFO, "		" + entry.getKey() + "/" + entry.getValue());
			}
		}
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
	
	/**
	 * conversion of JSONObject of each one of the context events in the list of each sensor
	 * @param JSONObject 
	 * @return ContextEvent 
	 * @throws JSONException
	 */
	private static ContextEvent extractActionContextEvent(JSONObject contextEventJson) throws JSONException {
	
		ContextEvent contextEvent = null;
		Map<String,String> properties = null;
		String contextEventType = null;
		String value = null;
		
		try{
			contextEventType = contextEventJson.getString(ContextEvent.KEY_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
			//TODO Tweaked for the case where type of the action is not completed, remove this when it is fixed
			contextEventType = "open_asset";
			
		}
		try{
			if ((contextEventJson != null)&&(contextEventType != null)){
				contextEvent = new ContextEvent();
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
				if (contextEventType.equals(ActionType.OPEN_ASSET)||contextEventType.equals(ActionType.ACCESS)||contextEventType.equals(ActionType.OPEN)){
					contextEvent.setType(EventTypes.FILEOBSERVER);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.OPEN_APPLICATION)){
					contextEvent.setType(EventTypes.APPOBSERVER);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.UNINSTALL)){
					contextEvent.setType(EventTypes.APPOBSERVER);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.SEND_MAIL)){
					contextEvent.setType(EventTypes.SEND_MAIL);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.VIRUS_FOUND)){
					contextEvent.setType(EventTypes.VIRUS_FOUND);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.VIRUS_CLEANED)){
					contextEvent.setType(EventTypes.VIRUS_CLEANED);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.SECURITY_PROPERTY_CHANGED)){
					contextEvent.setType(EventTypes.CHANGE_SECURITY_PROPERTY);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.SAVE_ASSET)){
					contextEvent.setType(EventTypes.SAVE_ASSET);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.UPDATE)){
					Logger.getLogger(JSONManager.class).log(Level.INFO, "Action type for update of events");
					return null; //This is not a concrete type of action, it just reflects that the list of events is an update_events request type
				}else{
					Logger.getLogger(JSONManager.class).log(Level.INFO, "Action not supported for json:"+contextEventJson.toString());
					return null;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			//TODO Tweaked for the case where type of the action is not completed
			
			return null;
		}

		return contextEvent;
	}
	
	public static JSONObject createJSON(String requestType, String authResult, String authMessage) {
		JSONObject root = new JSONObject();
		try {

            root.put(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER, requestType);
            
            root.put(JSONIdentifiers.AUTH_RESULT, authResult);
            
            root.put(JSONIdentifiers.AUTH_MESSAGE, authMessage);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return root;
	}
	
	public static JSONObject createConfigUpdateJSON(String requestType, MusesConfig config, List<SensorConfiguration> sensorConfig, ConnectionConfig connConfig, List<Zone> zoneConfig) {
		JSONObject root = new JSONObject();
		try {

            root.put(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER, requestType);
            String configXML = "";
            configXML += xmlProperty(JSONIdentifiers.SILENT_MODE, String.valueOf(config.getSilentMode()));
            if (config.getConfigName()!=null){
            	configXML += xmlProperty(JSONIdentifiers.CONFIG_NAME, config.getConfigName());
            }
            root.put(JSONIdentifiers.MUSES_CONFIG,XML.toJSONObject(configXML));
            
            //Zone configuration
            String zoneConfigXML = "";
            if (zoneConfig != null){
            	for (Iterator iterator = zoneConfig.iterator(); iterator.hasNext();) {
            	
            	Zone zone = (Zone) iterator.next();
				zoneConfigXML += "<"+JSONIdentifiers.ZONE+">";
				zoneConfigXML += xmlProperty(JSONIdentifiers.ZONE_ID, StringEscapeUtils.escapeXml(String.valueOf(zone.getZoneId())));
				zoneConfigXML += xmlProperty(JSONIdentifiers.DESCRIPTION, StringEscapeUtils.escapeXml(String.valueOf(zone.getDescription())));
				zoneConfigXML += xmlProperty(JSONIdentifiers.LONGITUD, StringEscapeUtils.escapeXml(String.valueOf(zone.getLongitud())));
				zoneConfigXML += xmlProperty(JSONIdentifiers.LATITUDE, StringEscapeUtils.escapeXml(String.valueOf(zone.getLatitude())));
				zoneConfigXML += xmlProperty(JSONIdentifiers.RADIUS, StringEscapeUtils.escapeXml(String.valueOf(zone.getRadius())));
				zoneConfigXML += "</"+JSONIdentifiers.ZONE+">";
            	}		
            }
            
            root.put(JSONIdentifiers.ZONE_CONFIG,XML.toJSONObject(zoneConfigXML));
            
            //Sensor configuration
            String sensorConfigXML = "";
            for (Iterator iterator = sensorConfig.iterator(); iterator
					.hasNext();) {
				SensorConfiguration sensorConfiguration = (SensorConfiguration) iterator
						.next();
				sensorConfigXML += "<"+JSONIdentifiers.SENSOR_PROPERTY+">";
				sensorConfigXML += xmlProperty(JSONIdentifiers.SENSOR_TYPE, StringEscapeUtils.escapeXml(String.valueOf(sensorConfiguration.getSensorType())));
				sensorConfigXML += xmlProperty(JSONIdentifiers.KEY, StringEscapeUtils.escapeXml(String.valueOf(sensorConfiguration.getKeyProperty())));
				sensorConfigXML += xmlProperty(JSONIdentifiers.VALUE, StringEscapeUtils.escapeXml(String.valueOf(sensorConfiguration.getValueProperty())));
				sensorConfigXML += "</"+JSONIdentifiers.SENSOR_PROPERTY+">";
			}

            root.put(JSONIdentifiers.SENSOR_CONFIGURATION, XML.toJSONObject(sensorConfigXML));
            
            //Connection configuration
            String connConfigXML = "";
            connConfigXML += xmlProperty(JSONIdentifiers.TIMEOUT, connConfig.getTimeout());
            connConfigXML += xmlProperty(JSONIdentifiers.POLL_TIMEOUT, connConfig.getPollTimeout());
            connConfigXML += xmlProperty(JSONIdentifiers.SLEEP_POLL_TIMEOUT, connConfig.getSleepPollTimeout());
            connConfigXML += xmlProperty(JSONIdentifiers.POLLING_ENABLED, connConfig.getPollingEnabled());
            connConfigXML += xmlProperty(JSONIdentifiers.LOGIN_ATTEMPTS, connConfig.getLoginAttempts());
            
            root.put(JSONIdentifiers.CONNECTION_CONFIG,XML.toJSONObject(connConfigXML));
            
            
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return root;
	}
	
	private static String xmlProperty(String tag, String value){
		return "<"+tag+">"+value+"</"+tag+">";
	}
	
	private static String xmlProperty(String tag, int value){
		return "<"+tag+">"+value+"</"+tag+">";
	}

}
