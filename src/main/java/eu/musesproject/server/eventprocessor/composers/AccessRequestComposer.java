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
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ChangeSecurityPropertyEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.EmailEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.OpenFileEvent;
import eu.musesproject.server.eventprocessor.util.Constants;
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
		Assets entityAsset = null;
		
		if (event.getType()!=null){
			if (event.getType().equals(EventTypes.FILEOBSERVER)){
				if (event instanceof FileObserverEvent) {
					FileObserverEvent fileEvent = (FileObserverEvent) event;
					requestedCorporateAsset.setId(fileEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(fileEvent.getPath());//Get the asset identifier
				
//					if ((fileEvent.getResourceType()!=null)&&(fileEvent.getResourceType().equals("sensitive"))){
//						requestedCorporateAsset.setConfidential_level("CONFIDENTIAL");//TODO This is temporary. Fix this with the use of the domain confidentiality selector
//					}else{
//						requestedCorporateAsset.setConfidential_level("PUBLIC");//TODO This is temporary. Fix this with the use of the domain confidentiality selector
//					}
					if (fileEvent.getResourceType()!=null){
						requestedCorporateAsset.setConfidential_level(fileEvent.getResourceType());
					}else{
						requestedCorporateAsset.setConfidential_level(Constants.PUBLIC);
					}
					composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
					composedRequest.setEventId(fileEvent.getTimestamp());
					
					//Store asset
					requestedCorporateAsset.setDescription(EventTypes.FILEOBSERVER);
					entityAsset = convertAsset(requestedCorporateAsset);
					dbManager.setAsset(entityAsset);
				}
			}else if (event.getType().equals(EventTypes.APPOBSERVER)){
				if (event instanceof AppObserverEvent){
					AppObserverEvent appEvent = (AppObserverEvent) event;
					requestedCorporateAsset.setId(appEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(appEvent.getName());//Get the asset identifier
					composedRequest.setAction(appEvent.getEvent());//Get the action over the asset
					composedRequest.setEventId(appEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.APPOBSERVER);
					
				}
			}else if (event.getType().equals(EventTypes.SEND_MAIL)){
				if (event instanceof EmailEvent) {
					EmailEvent emailEvent = (EmailEvent) event;
					requestedCorporateAsset = testGetRequestedAsset(emailEvent
							.getAttachmentName());
					composedRequest.setAction(emailEvent.getType());// Get the action over the asset
					composedRequest.setEventId(emailEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.SEND_MAIL);
				}
			}else if (event.getType().equals(EventTypes.CHANGE_SECURITY_PROPERTY)){
				if (event instanceof ChangeSecurityPropertyEvent) {
					ChangeSecurityPropertyEvent changeSecurityPropertyEvent = (ChangeSecurityPropertyEvent) event;
					requestedCorporateAsset = new Asset();//TODO It is not clear what is the asset when a device setting is changed
					requestedCorporateAsset.setId(0);
					requestedCorporateAsset.setLocation("device");
					//requestedCorporateAsset.setValue(400);
					logger.log(Level.INFO, "ACTION TYPE:"+changeSecurityPropertyEvent.getType());
					composedRequest.setAction(changeSecurityPropertyEvent.getType());//Get the action over the asset
					composedRequest.setEventId(changeSecurityPropertyEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.CHANGE_SECURITY_PROPERTY);
				}
			}else if (event.getType().equals(EventTypes.SAVE_ASSET)){
				if (event instanceof FileObserverEvent) {
					FileObserverEvent fileEvent = (FileObserverEvent) event;
					requestedCorporateAsset.setId(fileEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(fileEvent.getPath());//Get the asset identifier
					composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
					composedRequest.setEventId(fileEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.SAVE_ASSET);
				}
			}else {
				logger.log(Level.INFO, "Unsupported Event type:"+event.getType());
				requestedCorporateAsset.setDescription(event.getType());
			}
		}else{
			logger.log(Level.INFO, "Null type for event instantiated as:" + event.getClass().getName());
		}
		
		//Set value according to sensitivity level
		if (requestedCorporateAsset.getConfidential_level()!=null){
			if (requestedCorporateAsset.getConfidential_level().equals(Constants.PUBLIC)){
				requestedCorporateAsset.setValue(0);
			}else if (requestedCorporateAsset.getConfidential_level().equals(Constants.INTERNAL)){
				requestedCorporateAsset.setValue(100);
			}else if (requestedCorporateAsset.getConfidential_level().equals(Constants.CONFIDENTIAL)){
				requestedCorporateAsset.setValue(10000);
			}else if (requestedCorporateAsset.getConfidential_level().equals(Constants.STRICTLY_CONFIDENTIAL)){
				requestedCorporateAsset.setValue(1000000);
			}
		}else{
			requestedCorporateAsset.setValue(0);
		}
		
		if (event instanceof OpenFileEvent){
			OpenFileEvent fileEvent = (OpenFileEvent)event;
			requestedCorporateAsset.setTitle(fileEvent.getAssetTypeId());//TODO Asset information should be completed
		}
		
		//Store asset
		entityAsset = convertAsset(requestedCorporateAsset);
		//Associate event_id in the asset table
		//EventType type = dbManager.getEventTypeByKey(event.getType());
		//SimpleEvents simpleEvent = dbManager.findLastEventByEventType(type.getEventTypeId());
		//entityAsset.setEvent(simpleEvent);

		String assetId = dbManager.setAsset(entityAsset);
		
		//Assign correct asset to simple event
		
		SimpleEvents associatedEvent = dbManager.updateSimpleEvent(event.getType(), assetId );
		
		if (associatedEvent!=null){
			composedRequest.setEventId(Long.valueOf(associatedEvent.getEventId()));
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
	
	private static Assets convertAsset(Asset asset){
		Assets assets = new Assets();
		if (asset.getConfidential_level()==null){
			assets.setConfidentialLevel(Constants.PUBLIC);
		}else{
			assets.setConfidentialLevel(asset.getConfidential_level());
		}	
		if (asset.getLocation()==null){
			assets.setLocation("");
		}else{
			assets.setLocation(asset.getLocation());
		}
		if (asset.getTitle()==null){
			assets.setTitle("");
		}else{
			assets.setTitle(asset.getTitle());
		}
		if (asset.getDescription()==null){
			assets.setDescription("");
		}else{
			assets.setDescription(asset.getDescription());
		}
		assets.setValue(asset.getValue());
		return assets;
	}
	
	

}
