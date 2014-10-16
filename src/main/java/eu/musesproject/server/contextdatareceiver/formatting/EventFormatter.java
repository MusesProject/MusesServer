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

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ChangeSecurityPropertyEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConnectivityEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.DeviceProtectionEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.EmailEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.PackageObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.UserBehaviorEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.VirusFoundEvent;
import eu.musesproject.server.eventprocessor.util.EventTypes;



public class EventFormatter {
	
	
	
	public static Event formatContextEvent(ContextEvent contextEvent){
		Event cepFileEvent = null;
		if (contextEvent != null){
			if (contextEvent.getType() != null) {
				if (contextEvent.getType().equals(EventTypes.FILEOBSERVER)) {
					cepFileEvent = convertToFileObserverEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.CONNECTIVITY)) {
					cepFileEvent = convertToConnectivityEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.DEVICE_PROTECTION)) {
					cepFileEvent = convertToDeviceProtectionEvent(contextEvent);
				} else if (contextEvent.getType().equals("CONTEXT_SENSOR_APP")) {
					cepFileEvent = new Event();// TODO Manage CONTEXT_SENSOR_APP event information
				} else if (contextEvent.getType().equals("CONTEXT_SENSOR_PACKAGE")) {
					cepFileEvent = convertToPackageObserverEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.APPOBSERVER)){
					cepFileEvent = convertToAppObserverEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.USERBEHAVIOR)){
					cepFileEvent = convertToUserBehaviorEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.SEND_MAIL)){
					cepFileEvent = convertToEmailEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.VIRUS_FOUND)){
					cepFileEvent = convertToVirusFoundEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.CHANGE_SECURITY_PROPERTY)){
					cepFileEvent = convertToChangeSecurityPropertyEvent(contextEvent);
				} else if (contextEvent.getType().equals(EventTypes.SAVE_ASSET)){
					cepFileEvent = convertToFileObserverSaveEvent(contextEvent);
				} else {
					cepFileEvent = new Event();// Any other unsupported sensor
					Logger.getLogger(EventFormatter.class).error("Unsupported sensor:"+contextEvent.getType());
				}
			}else{
				Logger.getLogger(EventFormatter.class).error("ContextEvent type is null");
			}
		}else{
			Logger.getLogger(EventFormatter.class).error("ContextEvent is null in formatContextEvent");
		}
		
		Logger.getLogger(EventFormatter.class).info("Formatted event:"+ cepFileEvent.getClass());
		
		if (cepFileEvent instanceof DeviceProtectionEvent){
			DeviceProtectionEvent dEvent = (DeviceProtectionEvent) cepFileEvent;
			Logger.getLogger(EventFormatter.class).info("isPasswordProtected:"+dEvent.getIsPasswordProtected());
		}else if (cepFileEvent instanceof ChangeSecurityPropertyEvent){
			ChangeSecurityPropertyEvent dEvent = (ChangeSecurityPropertyEvent) cepFileEvent;
			Logger.getLogger(EventFormatter.class).info("isPasswordProtected:"+dEvent.getIsPasswordProtected());
			Logger.getLogger(EventFormatter.class).info("accessibilityEnabled:"+dEvent.getAccessibilityEnabled());
		}
		return (Event)cepFileEvent;
		
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
		
		cepFileEvent.setPath(getElement(properties.get("properties"), "path"));
		cepFileEvent.setId(Integer.valueOf(getElement(properties.get("properties"), "id")));
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setUid(getElement(properties.get("properties"), "id"));
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

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cepFileEvent.setType(EventTypes.CHANGE_SECURITY_PROPERTY);
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setIsPasswordProtected(Boolean.valueOf(properties.get("ispasswordprotected")));
		cepFileEvent.setTrustedAntivirusInstalled(Boolean.valueOf("istrustedantivirusinstalled"));
		cepFileEvent.setIpAddress(properties.get("ipaddress"));
		cepFileEvent.setScreenTimeoutInSeconds(Integer.valueOf(properties.get("screentimeoutinseconds")));
		cepFileEvent.setAccessibilityEnabled(Boolean.valueOf(properties.get("accessibilityenabled")));
		
		
		return cepFileEvent;
	}

	private static Event convertToVirusFoundEvent(ContextEvent contextEvent) {
		VirusFoundEvent cepFileEvent = new VirusFoundEvent();
		Map<String,String> prop = contextEvent.getProperties();
		Map<String,String> properties = null;
		JSONObject propJSON;
		try {
			propJSON = new JSONObject(prop.get("properties"));
		
			properties = new HashMap<String,String>();
			for (Iterator iterator = propJSON.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = propJSON.getString(key);
				properties.put(key, value);
			}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cepFileEvent.setType(EventTypes.VIRUS_FOUND);
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setPath(properties.get("path"));
		cepFileEvent.setName(properties.get("name"));
		cepFileEvent.setSeverity(properties.get("severity"));
				
		return cepFileEvent;
	}
	
	private static Event convertToEmailEvent(ContextEvent contextEvent) {
		EmailEvent cepFileEvent = new EmailEvent();
		Map<String,String> prop = contextEvent.getProperties();
		Map<String,String> properties = null;
		JSONObject propJSON;
		try {
			propJSON = new JSONObject(prop.get("properties"));
		
			properties = new HashMap<String,String>();
			for (Iterator iterator = propJSON.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = propJSON.getString(key);
				properties.put(key, value);
			}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cepFileEvent.setType(EventTypes.SEND_MAIL);
		cepFileEvent.setTimestamp(contextEvent.getTimestamp());
		cepFileEvent.setFrom(properties.get("from"));
		cepFileEvent.setTo(properties.get("to"));
		cepFileEvent.setCc(properties.get("cc"));
		cepFileEvent.setBcc(properties.get("bcc"));
		cepFileEvent.setSubject(properties.get("subject"));
		cepFileEvent.setNumberAttachments(Integer.valueOf(properties.get("noAttachments")));
		String attachmentInfo = properties.get("attachmentInfo");
		StringTokenizer tokenizer = new StringTokenizer(attachmentInfo, ";");
		while (tokenizer.hasMoreTokens()){
			String attachment = tokenizer.nextToken();
			StringTokenizer tokenAttach = new StringTokenizer(attachment,",");
			String firstToken = tokenAttach.nextToken();
			if (tokenAttach.hasMoreTokens()){
				cepFileEvent.setAttachmentName(firstToken); //FIXME We have to provide a mechanism to store info associated to more than one attachment
				cepFileEvent.setAttachmentType(tokenAttach.nextToken());
				cepFileEvent.setAttachmentSize(tokenAttach.nextToken());
			}else{
				cepFileEvent.setAttachmentType(firstToken);
			}
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
		cepFileEvent.setId(Integer.valueOf(properties.get("id")));
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
		cepFileEvent.setPatternProtected(Boolean.valueOf(properties.get("patternprotected")));
		cepFileEvent.setTrustedAntivirusInstalled(Boolean.valueOf("istrustedantivirusinstalled"));
		cepFileEvent.setRooted(Boolean.valueOf("isrooted"));
		cepFileEvent.setRootPermissionGiven(Boolean.valueOf("isrootpermissiongiven"));
		cepFileEvent.setIpaddress(properties.get("ipaddress"));
		cepFileEvent.setScreenTimeoutInSeconds(Integer.valueOf(properties.get("screentimeoutinseconds")));
		
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
			cepFileEvent.setNetworkId(Integer.valueOf(properties.get("networkid")));
			cepFileEvent.setWifiNeighbors(Integer.valueOf(properties.get("wifineighbors")));
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

		/*resourcePath = getElement(properties.get("properties"), "resourcePath");
		if (resourcePath != null) {
			cepFileEvent.setPath(resourcePath);
		} else {*/
			cepFileEvent.setPath(getElement(properties.get("properties"),
					"path"));
		//}

		resourceType = getElement(properties.get("properties"), "resourceType");
		if (resourceType != null) {
			cepFileEvent.setResourceType(resourceType);
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
