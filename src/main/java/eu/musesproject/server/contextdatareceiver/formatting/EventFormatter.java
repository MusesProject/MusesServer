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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.icu.util.StringTokenizer;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AddNoteEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ChangeSecurityPropertyEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.DeviceProtectionEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.EmailEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileAccessPermissionEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.LocationEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Opportunity;
import eu.musesproject.server.eventprocessor.correlator.model.owl.PackageObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.PasswordEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.SensorAppEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.USBDeviceConnectedEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.UserBehaviorEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.VirusCleanedEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.VirusFoundEvent;
import eu.musesproject.server.eventprocessor.util.EventTypes;



public class EventFormatter {
	

	public static Event formatContextEvent(ContextEvent contextEvent){
		Event cepFileEvent = null;
		if (contextEvent != null){
			if (contextEvent.getType() != null) {
				if ((contextEvent.getType().equals(EventTypes.FILEOBSERVER))||(contextEvent.getType().equals(EventTypes.FILEOBSERVER_SENSOR))) {
					cepFileEvent = convertToFileObserverEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.CONNECTIVITY)) {
					cepFileEvent = convertToConnectivityEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.DEVICE_PROTECTION)) {
					cepFileEvent = convertToDeviceProtectionEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.APP)) {
					//cepFileEvent = new Event();// TODO Manage CONTEXT_SENSOR_APP event information
					cepFileEvent = convertToSensorAppEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.PACKAGE)) {
					cepFileEvent = convertToPackageObserverEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.APPOBSERVER)){
					cepFileEvent = convertToAppObserverEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.USERBEHAVIOR)){
					cepFileEvent = convertToUserBehaviorEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.SEND_MAIL)){
					cepFileEvent = convertToEmailEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.VIRUS_FOUND)){
					cepFileEvent = convertToVirusFoundEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.VIRUS_CLEANED)){
					cepFileEvent = convertToVirusCleanedEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.CHANGE_SECURITY_PROPERTY)){
					cepFileEvent = convertToChangeSecurityPropertyEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.SAVE_ASSET)){
					cepFileEvent = convertToFileObserverSaveEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.LOCATION)){
					cepFileEvent = convertToLocationEvent(contextEvent);
				}else if (contextEvent.getType().equals(EventTypes.USER_ENTERED_PASSWORD_FIELD)){
					cepFileEvent = convertToPasswordEvent(contextEvent);
				}else if (contextEvent.getType().equals(EventTypes.USB_DEVICE_CONNECTED)){
					cepFileEvent = convertToUSBDeviceConnectedEvent(contextEvent);
				}else if (contextEvent.getType().equals(EventTypes.ADD_NOTE)){
					cepFileEvent = convertToAddNoteEvent(contextEvent);

				}else if (contextEvent.getType().equals(EventTypes.OPPORTUNITY)){
					cepFileEvent = convertToOpportunityEvent(contextEvent);

				}else if (contextEvent.getType().equals(EventTypes.FILE_ACCESS_PERMISSION)) {
					cepFileEvent = convertToFileAccessPermissionEvent(contextEvent);
				}else {
					cepFileEvent = new Event();// Any other unsupported sensor
					Logger.getLogger(EventFormatter.class).error("Unsupported sensor:"+contextEvent.getType());
				}
			}else{
				Logger.getLogger(EventFormatter.class).error("ContextEvent type is null");
			}
		}else{
			Logger.getLogger(EventFormatter.class).error("ContextEvent is null in formatContextEvent");
		}
		
		if (cepFileEvent!=null){
			Logger.getLogger(EventFormatter.class).info("Formatted event:"+ cepFileEvent.getClass());
		}else{
			Logger.getLogger(EventFormatter.class).info("Formatted event is null for context event:" + contextEvent );
		}
		
		
		if (cepFileEvent instanceof DeviceProtectionEvent){
			DeviceProtectionEvent dEvent = (DeviceProtectionEvent) cepFileEvent;
			Logger.getLogger(EventFormatter.class).info("isPasswordProtected:"+dEvent.getIsPasswordProtected());
			Logger.getLogger(EventFormatter.class).info("isPatternProtected:"+dEvent.getIsPatternProtected());
		}else if (cepFileEvent instanceof ChangeSecurityPropertyEvent){
			ChangeSecurityPropertyEvent dEvent = (ChangeSecurityPropertyEvent) cepFileEvent;
			Logger.getLogger(EventFormatter.class).info("isPasswordProtected:"+dEvent.getIsPasswordProtected());
			Logger.getLogger(EventFormatter.class).info("isPatternProtected:"+dEvent.getIsPatternProtected());
			Logger.getLogger(EventFormatter.class).info("accessibilityEnabled:"+dEvent.getAccessibilityEnabled());
		}else if (cepFileEvent instanceof FileObserverEvent){
			FileObserverEvent dEvent = (FileObserverEvent) cepFileEvent;
			Logger.getLogger(EventFormatter.class).info("resourceType:"+dEvent.getResourceType());
			Logger.getLogger(EventFormatter.class).info("event:"+dEvent.getEvent());
		}
		//Set sessionId, if available in the properties
		Map<String,String> properties = contextEvent.getProperties();
		String sessionId = properties.get("sessionId");
		if (sessionId != null){
			cepFileEvent.setSessionId(sessionId);
		}
		return (Event)cepFileEvent;
		
	}
	
	private static Event convertToFileAccessPermissionEvent(
			ContextEvent contextEvent) {
		FileAccessPermissionEvent cepFileEvent = new FileAccessPermissionEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		if (properties.get("id")!=null){
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		}
		
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		
		cepFileEvent.setCanRead((Boolean.valueOf(properties
				.get("canRead"))));
		cepFileEvent.setCanCreate((Boolean.valueOf(properties
				.get("cancreate"))));
		cepFileEvent.setCanDelete((Boolean.valueOf(properties
				.get("candelete"))));
		cepFileEvent.setCanModify((Boolean.valueOf(properties
				.get("canmodify"))));
		cepFileEvent.setCanExecute((Boolean.valueOf(properties
				.get("canexecute"))));

		
		return cepFileEvent;
	}

	private static Event convertToAddNoteEvent(ContextEvent contextEvent) {
		AddNoteEvent cepFileEvent = new AddNoteEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		if (properties.get("id")!=null)
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setId_event(properties.get("id_event"));
		cepFileEvent.setId_user(properties.get("id_user"));
		cepFileEvent.setDescription(properties.get("description"));
		cepFileEvent.setTitle(properties.get("title"));
		return cepFileEvent;
	}
	
	private static Event convertToOpportunityEvent(ContextEvent contextEvent) {
		Opportunity cepFileEvent = new Opportunity();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		cepFileEvent.setDeviceId(properties.get(JSONIdentifiers.AUTH_DEVICE_ID));
		cepFileEvent.setLossDescription(properties.get(JSONIdentifiers.OPPORTUNITY_LOSS_DESCRIPTION));
		cepFileEvent.setLossCost(properties.get(JSONIdentifiers.OPPORTUNITY_LOSS_EUROS));
		cepFileEvent.setTime(properties.get(JSONIdentifiers.OPPORTUNITY_TIME));
		cepFileEvent.setDecisionId(Integer.valueOf(properties.get(JSONIdentifiers.DECISION_IDENTIFIER)));
		return cepFileEvent;
	}

	private static Event convertToUSBDeviceConnectedEvent(
			ContextEvent contextEvent) {
		USBDeviceConnectedEvent cepFileEvent = new USBDeviceConnectedEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		if (properties.get("id")!=null)
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setConnectedViaUSB(Boolean.valueOf(properties
				.get("connected_via_usb")));
		
		return cepFileEvent;
	}

	private static Event convertToPasswordEvent(ContextEvent contextEvent) {
		PasswordEvent cepFileEvent = new PasswordEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		if (properties.get("id")!=null)
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
				
		if (properties.get("properties")!=null){
			if (getElement(properties.get("properties"),"packagename")!=null){
				cepFileEvent.setPackageName(getElement(properties.get("properties"),
									"packagename"));
					
			}
		}else{
			cepFileEvent.setPackageName(properties.get("packagename"));
		}

		
		return cepFileEvent;
	}

	
	private static Event convertToLocationEvent(ContextEvent contextEvent) {
		LocationEvent cepFileEvent = new LocationEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setIsWithinZone(properties.get("isWithinZone"));
		
		return cepFileEvent;
	}

	private static Event convertToSensorAppEvent(ContextEvent contextEvent) {
		SensorAppEvent cepFileEvent = new SensorAppEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		if (properties.get("id")!=null){
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		}
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setBackgroundProcess(properties.get("backgroundprocess"));
		cepFileEvent.setAppVersion(properties.get("appversion"));
		cepFileEvent.setAppName(properties.get("appname"));
		cepFileEvent.setPackageName(properties.get("packagename"));
		
		return cepFileEvent;
	}

	private static Event convertToFileObserverSaveEvent(
			ContextEvent contextEvent) {
		FileObserverEvent cepFileEvent = new FileObserverEvent();
		Map<String,String> properties = contextEvent.getProperties();
		if (getElement(properties.get("properties"), "fileevent")!=null){
			//cepFileEvent.setEvent(getElement(properties.get("properties"), "fileevent"));
			cepFileEvent.setEvent(properties.get("event"));
		}
	
		cepFileEvent.setType(contextEvent.getType());
		
		
		if (properties.get("properties")!=null){
			if (getElement(properties.get("properties"),"resourceName")!=null){
					cepFileEvent.setResourceName(getElement(properties.get("properties"),
									"resourceName"));
					
			}
		}else{
			cepFileEvent.setResourceName(properties.get("resourceName"));
		}

		
		if (getElement(properties.get("properties"),"resourceType")!=null){
			cepFileEvent.setResourceType(getElement(properties.get("properties"),
					"resourceType"));
		}
		
		cepFileEvent.setPath(getElement(properties.get("properties"), "path"));
		//cepFileEvent.setId(Integer.valueOf(getElement(properties.get("properties"), "id")));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		//cepFileEvent.setUid(getElement(properties.get("properties"), "id"));
		return cepFileEvent;
	}

	private static Event convertToChangeSecurityPropertyEvent(
			ContextEvent contextEvent) {

		ChangeSecurityPropertyEvent cepFileEvent = new ChangeSecurityPropertyEvent();
		Map<String, String> prop = contextEvent.getProperties();
		Map<String, String> properties = null;
		JSONObject propJSON;
		try {
			propJSON = new JSONObject(prop.get("properties"));

			properties = new HashMap<String, String>();
			for (Iterator iterator = propJSON.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = propJSON.getString(key);
				properties.put(key, value);
			}

			cepFileEvent.setType(EventTypes.CHANGE_SECURITY_PROPERTY);
			cepFileEvent.setTimestamp(contextEvent.getTimestamp());
			cepFileEvent.setIsPasswordProtected(Boolean.valueOf(properties
					.get("ispasswordprotected")));
			cepFileEvent.setIsPatternProtected(Boolean.valueOf(properties
					.get("ispatternprotected")));
			cepFileEvent.setIsTrustedAntivirusInstalled(Boolean
					.valueOf(properties.get("istrustedantivirusinstalled")));
			cepFileEvent.setIpAddress(properties.get("ipaddress"));
			try{
			cepFileEvent.setScreenTimeoutInSeconds(Integer.valueOf(properties
					.get("screentimeoutinseconds")));
			} catch(NumberFormatException nfe){
				Logger.getLogger(EventFormatter.class).info("Value of screentimeoutinseconds is not a number. Setting max value.");
				cepFileEvent.setScreenTimeoutInSeconds(2147483647);//Max int value: 2^31-1
			}
			cepFileEvent.setAccessibilityEnabled(Boolean.valueOf(properties
					.get("accessibilityenabled")));
			cepFileEvent
					.setIsRooted(Boolean.valueOf(properties.get("isrooted")));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cepFileEvent;
	}

	private static Event convertToVirusFoundEvent(ContextEvent contextEvent) {
		VirusFoundEvent cepFileEvent = new VirusFoundEvent();
		Map<String,String> prop = contextEvent.getProperties();
		Map<String,String> properties = null;
		JSONObject propJSON;
		try {
			propJSON = new JSONObject(prop.get("properties"));

			properties = new HashMap<String, String>();
			for (Iterator iterator = propJSON.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = propJSON.getString(key);
				properties.put(key, value);
			}

			cepFileEvent.setType(EventTypes.VIRUS_FOUND);
			cepFileEvent.setTimestamp(contextEvent.getTimestamp());
			cepFileEvent.setPath(properties.get("path"));
			cepFileEvent.setName(properties.get("name"));
			cepFileEvent.setSeverity(properties.get("severity"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cepFileEvent;
	}
	
	private static Event convertToVirusCleanedEvent(ContextEvent contextEvent) {
		VirusCleanedEvent cepFileEvent = new VirusCleanedEvent();
		Map<String,String> prop = contextEvent.getProperties();
		Map<String,String> properties = null;
		JSONObject propJSON;
		try {
			propJSON = new JSONObject(prop.get("properties"));

			properties = new HashMap<String, String>();
			for (Iterator iterator = propJSON.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = propJSON.getString(key);
				properties.put(key, value);
			}

			cepFileEvent.setType(EventTypes.VIRUS_CLEANED);
			cepFileEvent.setTimestamp(contextEvent.getTimestamp());
			cepFileEvent.setPath(properties.get("path"));
			cepFileEvent.setName(properties.get("name"));
			cepFileEvent.setSeverity(properties.get("severity"));
			cepFileEvent.setCleanType(properties.get("clean_type"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return cepFileEvent;
	}
	
	private static Event convertToEmailEvent(ContextEvent contextEvent) {
		EmailEvent cepFileEvent = new EmailEvent();
		Map<String,String> prop = contextEvent.getProperties();
		Map<String,String> properties = null;
		JSONObject propJSON;
		try {
			propJSON = new JSONObject(prop.get("properties"));

			properties = new HashMap<String, String>();
			for (Iterator iterator = propJSON.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = propJSON.getString(key);
				properties.put(key, value);
			}
			cepFileEvent.setType(EventTypes.SEND_MAIL);
			cepFileEvent.setTimestamp(contextEvent.getTimestamp());
			cepFileEvent.setFrom(properties.get("from"));
			cepFileEvent.setTo(properties.get("to"));
			cepFileEvent.setCc(properties.get("cc"));
			cepFileEvent.setBcc(properties.get("bcc"));
			cepFileEvent.setSubject(properties.get("subject"));
			cepFileEvent.setNumberAttachments(Integer.valueOf(properties
					.get("noAttachments")));
			String attachmentInfo = properties.get("attachmentInfo");
			StringTokenizer tokenizer = new StringTokenizer(attachmentInfo, ";");
			while (tokenizer.hasMoreTokens()) {
				String attachment = tokenizer.nextToken();
				StringTokenizer tokenAttach = new StringTokenizer(attachment,
						",");
				String firstToken = tokenAttach.nextToken();
				if (tokenAttach.hasMoreTokens()) {
					cepFileEvent.setAttachmentName(firstToken); // FIXME We have
																// to provide a
																// mechanism to
																// store info
																// associated to
																// more than one
																// attachment
					cepFileEvent.setAttachmentType(tokenAttach.nextToken());
					cepFileEvent.setAttachmentSize(tokenAttach.nextToken());
				} else {
					cepFileEvent.setAttachmentType(firstToken);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cepFileEvent;
	}
	private static Event convertToUserBehaviorEvent(ContextEvent contextEvent) {
		UserBehaviorEvent cepFileEvent = new UserBehaviorEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setAction(properties.get("action"));
		cepFileEvent.setType(EventTypes.USERBEHAVIOR);
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		return cepFileEvent;
	}
	private static PackageObserverEvent convertToPackageObserverEvent(ContextEvent contextEvent) {
		PackageObserverEvent cepFileEvent = new PackageObserverEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		if (properties.get("id")!=null){
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		}
		
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setInstalledApps(properties.get("installedapps"));
		
		return cepFileEvent;
	}
	private static DeviceProtectionEvent convertToDeviceProtectionEvent(
			ContextEvent contextEvent) {
		DeviceProtectionEvent cepFileEvent = new DeviceProtectionEvent();
		Map<String,String> properties = contextEvent.getProperties();
		cepFileEvent.setType(contextEvent.getType());
		//cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());	
		cepFileEvent.setIsPasswordProtected(Boolean.valueOf(properties.get("ispasswordprotected")));
		cepFileEvent.setIsPatternProtected(Boolean.valueOf(properties.get("patternprotected")));
		cepFileEvent.setTrustedAntivirusInstalled(Boolean.valueOf(properties.get("istrustedantivirusinstalled")));
		cepFileEvent.setIsRooted(Boolean.valueOf(properties.get("isrooted")));
		cepFileEvent.setRootPermissionGiven(Boolean.valueOf("isrootpermissiongiven"));
		cepFileEvent.setIpaddress(properties.get("ipaddress"));
		if (properties.get("screentimeoutinseconds")!=null){
			cepFileEvent.setScreenTimeoutInSeconds(Integer.valueOf(properties.get("screentimeoutinseconds")));
		}
		return cepFileEvent;
	}
	public static ConnectivityEvent convertToConnectivityEvent(ContextEvent contextEvent){			
			ConnectivityEvent cepFileEvent = new ConnectivityEvent();
			Map<String,String> properties = contextEvent.getProperties();
			cepFileEvent.setAirplaneMode(Boolean.valueOf(properties.get("airplanemode")));
			cepFileEvent.setBluetoothConnected(properties.get("bluetoothconnected"));
			//cepFileEvent.setId(Integer.valueOf(properties.get("id")));
			cepFileEvent.setType(contextEvent.getType());
			cepFileEvent.setBssid(properties.get("bssid"));
			cepFileEvent.setHiddenSSID(Boolean.valueOf(properties.get("hiddenssid")));
			cepFileEvent.setMobileConnected(Boolean.valueOf(properties.get("mobileConnected")));
			cepFileEvent.setWifiConnected(Boolean.valueOf(properties.get("wificonnected")));
			cepFileEvent.setWifiEnabled(Boolean.valueOf(properties.get("wifienabled")));
			cepFileEvent.setNetworkId(String.valueOf(properties.get("networkid")));
			if (properties.get("wifineighbors")!=null){
				cepFileEvent.setWifiNeighbors(Integer.valueOf(properties.get("wifineighbors")));
			}	
			cepFileEvent.setWifiEncryption(properties.get("wifiencryption"));
			cepFileEvent.setTimestamp(contextEvent.getTimestamp());		
			//cepFileEvent.setUid(properties.get("id"));
			return cepFileEvent;
	}
	
	public static FileObserverEvent convertToFileObserverEvent(
			ContextEvent contextEvent) {
		String resourcePath = null;
		String resourceType = null;
		FileObserverEvent cepFileEvent = new FileObserverEvent();
		Map<String, String> properties = contextEvent.getProperties();
		if (properties.get("event") != null) {// TODO Changes for System test
			cepFileEvent.setEvent(properties.get("event"));
		}

		if (properties.get("id") != null) {// TODO Changes for System test
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		}
		cepFileEvent.setType(contextEvent.getType());

		if (properties.get("properties")!=null){
			cepFileEvent.setPath(getElement(properties.get("properties"),
					"path"));
		
		}else{
			cepFileEvent.setPath(properties.get("path"));
		}
		if (properties.get("properties")!=null){
			if (getElement(properties.get("properties"),"resourceName")!=null){
					cepFileEvent.setResourceName(getElement(properties.get("properties"),
									"resourceName"));
					
			}
		}else{
			cepFileEvent.setResourceName(properties.get("resourceName"));
		}
		
		
	
		
		if (cepFileEvent.getPath()==null){
			resourcePath = getElement(properties.get("properties"), "resourcePath");
			if (resourcePath != null) {
				cepFileEvent.setPath(resourcePath);
			}
		}
			

		if (properties.get("properties")!=null){
				resourceType = getElement(properties.get("properties"), "resourceType");
				if (resourceType != null) {
					cepFileEvent.setResourceType(resourceType);
				}
			}else{
				cepFileEvent.setResourceType(properties.get("resourceType"));
		}

		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setUid(properties.get("id"));
		
		
		return cepFileEvent;
	}
	private static String getElement(String properties, String element) {
		String result = null;
		try {
			JSONObject root = new JSONObject(properties);
			result = root.getString(element);
		} catch (JSONException e) {
			Logger.getLogger(EventFormatter.class).error("Error while trying to get JSON Object:"+e.getLocalizedMessage());
		}
		return result;
		
	}
	public static FileObserverEvent convertToFileObserverEvent(ContextEvent contextEvent, String action){
		FileObserverEvent cepFileEvent = new FileObserverEvent();
		Map<String,String> properties = contextEvent.getProperties();
		
		cepFileEvent.setEvent(action.toString());
		if (properties.get("id")!=null){//TODO Changes for System test
			cepFileEvent.setId(Integer.valueOf(properties.get("id")));
		}
		if (getElement(properties.get("properties"),"resourceName")!=null){
			cepFileEvent.setResourceName(getElement(properties.get("properties"),
					"resourceName"));
		}
		if (getElement(properties.get("properties"),"resourceType")!=null){
			cepFileEvent.setResourceType(getElement(properties.get("properties"),
					"resourceType"));
		}
		cepFileEvent.setType(EventTypes.FILEOBSERVER);
		cepFileEvent.setPath(properties.get("path"));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setUid(properties.get("id"));
		return cepFileEvent;
	}	
	
	public static AppObserverEvent convertToAppObserverEvent(ContextEvent contextEvent){
		AppObserverEvent cepFileEvent = new AppObserverEvent();
		Map<String,String> properties = contextEvent.getProperties();
		
		if (properties.get("event")!=null){//TODO Changes for System test
			cepFileEvent.setEvent(properties.get("event"));
		}
		
		cepFileEvent.setType(EventTypes.APPOBSERVER);
		cepFileEvent.setAppPackage(getElement(properties.get("properties"), "packagename"));
		cepFileEvent.setName(getElement(properties.get("properties"), "appname"));
		//cepFileEvent.setVersion(getElement(properties.get("properties"), "version"));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setUid(properties.get("id"));
		return cepFileEvent;
	}


}
