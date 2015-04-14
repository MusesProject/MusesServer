package eu.musesproject.server.eventprocessor.composers;

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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ChangeSecurityPropertyEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.EmailEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.OpenFileEvent;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.User;
import eu.musesproject.server.risktrust.UserTrustValue;
import eu.musesproject.server.scheduler.ModuleType;

public class AccessRequestComposer {
	

	private static Logger logger = Logger.getLogger(AccessRequestComposer.class.getName());
	private static DBManager dbManager = new DBManager(ModuleType.EP);

	public static AccessRequest composeAccessRequest(Event event){
		
		AccessRequest composedRequest = new AccessRequest();
		Asset requestedCorporateAsset = new Asset();
		
		if (event.getType()!=null){
			if (event.getType().equals(EventTypes.FILEOBSERVER)){			
				FileObserverEvent fileEvent = (FileObserverEvent) event;
				requestedCorporateAsset.setId(fileEvent.getId());//Get the asset identifier		
				requestedCorporateAsset.setLocation(fileEvent.getPath());//Get the asset identifier
				
				if ((fileEvent.getResourceType()!=null)&&(fileEvent.getResourceType().equals("sensitive"))){
					requestedCorporateAsset.setConfidential_level("CONFIDENTIAL");//TODO This is temporary. Fix this with the use of the domain confidentiality selector
				}else{
					requestedCorporateAsset.setConfidential_level("PUBLIC");//TODO This is temporary. Fix this with the use of the domain confidentiality selector
				}
				
				composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
				composedRequest.setEventId(fileEvent.getTimestamp());
			}else if (event.getType().equals(EventTypes.APPOBSERVER)){
				AppObserverEvent appEvent = (AppObserverEvent) event;
				requestedCorporateAsset.setId(appEvent.getId());//Get the asset identifier		
				requestedCorporateAsset.setLocation(appEvent.getName());//Get the asset identifier
				composedRequest.setAction(appEvent.getEvent());//Get the action over the asset
				composedRequest.setEventId(appEvent.getTimestamp());
			}else if (event.getType().equals(EventTypes.SEND_MAIL)){
				EmailEvent emailEvent = (EmailEvent) event;
				requestedCorporateAsset = testGetRequestedAsset(emailEvent.getAttachmentName());
				composedRequest.setAction(emailEvent.getType());//Get the action over the asset
				composedRequest.setEventId(emailEvent.getTimestamp());
			}else if (event.getType().equals(EventTypes.CHANGE_SECURITY_PROPERTY)){
				ChangeSecurityPropertyEvent changeSecurityPropertyEvent = (ChangeSecurityPropertyEvent) event;
				requestedCorporateAsset = new Asset();//TODO It is not clear what is the asset when a device setting is changed
				requestedCorporateAsset.setId(0);
				requestedCorporateAsset.setLocation("device");
				requestedCorporateAsset.setValue(400);
				composedRequest.setAction(changeSecurityPropertyEvent.getType());//Get the action over the asset
				composedRequest.setEventId(changeSecurityPropertyEvent.getTimestamp());
			}else if (event.getType().equals(EventTypes.SAVE_ASSET)){
				FileObserverEvent fileEvent = (FileObserverEvent) event;
				requestedCorporateAsset.setId(fileEvent.getId());//Get the asset identifier		
				requestedCorporateAsset.setLocation(fileEvent.getPath());//Get the asset identifier
				composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
				composedRequest.setEventId(fileEvent.getTimestamp());
			}else {
				logger.log(Level.INFO, "Unsupported Event type:"+event.getType());
			}
		}else{
			logger.log(Level.INFO, "Null type for event instantiated as:" + event.getClass().getName());
		}
		
		requestedCorporateAsset.setValue(0);
		if (event instanceof OpenFileEvent){
			OpenFileEvent fileEvent = (OpenFileEvent)event;
			requestedCorporateAsset.setTitle(fileEvent.getAssetTypeId());//TODO Asset information should be completed
		}
		

		eu.musesproject.server.entity.Users musesUser = dbManager.getUserByUsername(event.getUsername());
		
		eu.musesproject.server.entity.Devices musesDevice = dbManager.getDeviceByIMEI(event.getDeviceId());


		User user = new User();
		Device device = new Device();
		dbManager.convertUsertoCommonUser(user, musesUser);
				
		dbManager.convertDevicetoCommonDevice(device, musesDevice);
		//testGetUserFromDatabase(event.getUsername());//TODO User information should be retrieved from the database

		//Device device = testGetDeviceFromDatabase(event.getDeviceId());//TODO Device information should be retrieved

		composedRequest.setUser(user);
		composedRequest.setDevice(device);
		composedRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		logger.log(Level.INFO, "AccessRequest event id:"+composedRequest.getEventId());
		
		return composedRequest;
	}

	private static Asset testGetRequestedAsset(String attachmentName) {//TODO This method will be replaced by the info in the database
		Asset asset = new Asset();
		asset.setId(1);//Get the asset identifier		
		asset.setLocation("//repository/projects/sandproject/offer/"+attachmentName);//Get the asset identifier //FIXME This should be a location, hence we should look into the database for the location in the repository
		asset.setValue(15000);//FIXME Set value from the assets' database
		asset.setConfidential_level("STRICTLY_CONFIDENTIAL"); //FIXME Set confidential level by means of the domains' database		
		return asset;
	}

	private static Device testGetDeviceFromDatabase(String deviceId) {//TODO This method will be replaced by the info in the database
		Device device = new Device();
		DeviceTrustValue deviceTrustValue = new DeviceTrustValue();
		deviceTrustValue.setValue(500);
		device.setDevicetrustvalue(deviceTrustValue);
		return device;
	}

	private static User testGetUserFromDatabase(String username) {//TODO This method will be replaced by the info in the database
		User user = new User();
		UserTrustValue userTrustValue = new UserTrustValue();
		userTrustValue.setValue(2000);
		user.setUsertrustvalue(userTrustValue);
		return user;
	}
	
	

}
