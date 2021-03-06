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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Assets;
import eu.musesproject.server.entity.EventType;
import eu.musesproject.server.entity.SimpleEvents;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AddNoteEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ChangeSecurityPropertyEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.EmailEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.FileObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.OpenFileEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Opportunity;
import eu.musesproject.server.eventprocessor.correlator.model.owl.PackageObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.PasswordEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.USBDeviceConnectedEvent;
import eu.musesproject.server.eventprocessor.util.Constants;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.risktrust.AccessRequest;
import eu.musesproject.server.risktrust.Asset;
import eu.musesproject.server.risktrust.Device;
import eu.musesproject.server.risktrust.DeviceTrustValue;
import eu.musesproject.server.risktrust.OpportunityDescriptor;
import eu.musesproject.server.risktrust.Outcome;
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
			if ((event.getType().equals(EventTypes.FILEOBSERVER))||(event.getType().equals(EventTypes.FILEOBSERVER_SENSOR))||(event.getType().equals(EventTypes.SAVE_ASSET))){
				if (event instanceof FileObserverEvent) {
					FileObserverEvent fileEvent = (FileObserverEvent) event;
					requestedCorporateAsset.setId(fileEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(fileEvent.getPath());//Get the asset identifier
				
					if (fileEvent.getResourceType()!=null){
						requestedCorporateAsset.setConfidential_level(fileEvent.getResourceType());
					}else{
						if((fileEvent.getPath()!=null)&&(fileEvent.getPath().toUpperCase().contains(Constants.CONFIDENTIAL))){
							requestedCorporateAsset.setConfidential_level(Constants.CONFIDENTIAL);
						}else{
							requestedCorporateAsset.setConfidential_level(Constants.PUBLIC);
						}	
					}
					
					composedRequest.setAction(fileEvent.getType());//Get the action over the asset
					composedRequest.setEventId(fileEvent.getTimestamp());
					
					
					requestedCorporateAsset.setTitle(fileEvent.getResourceName());
					
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
					//composedRequest.setAction(appEvent.getEvent());//Get the action over the asset
					composedRequest.setAction(appEvent.getType());//Get the action over the asset
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
					//composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
					composedRequest.setAction(fileEvent.getType());//Get the action over the asset
					composedRequest.setEventId(fileEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.SAVE_ASSET);
				}
			}else if (event.getType().equals(EventTypes.USER_ENTERED_PASSWORD_FIELD)){
				if (event instanceof PasswordEvent) {
					PasswordEvent pwdEvent = (PasswordEvent) event;
					requestedCorporateAsset.setId(pwdEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(pwdEvent.getPackageName());//Get the asset identifier
					composedRequest.setAction(pwdEvent.getType());//Get the action over the asset
					composedRequest.setEventId(pwdEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.USER_ENTERED_PASSWORD_FIELD);
				}
			}else if (event.getType().equals(EventTypes.USB_DEVICE_CONNECTED)){
				if (event instanceof USBDeviceConnectedEvent) {
					USBDeviceConnectedEvent usbEvent = (USBDeviceConnectedEvent) event;
					requestedCorporateAsset.setId(usbEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(usbEvent.getDescription());//Get the asset identifier
					composedRequest.setAction(usbEvent.getType());//Get the action over the asset
					composedRequest.setEventId(usbEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.USB_DEVICE_CONNECTED);
				}

			}else if (event.getType().equals(EventTypes.ADD_NOTE)){
				if (event instanceof AddNoteEvent) {
					AddNoteEvent addNoteEvent = (AddNoteEvent) event;
					requestedCorporateAsset.setId(addNoteEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(addNoteEvent.getDescription());//Get the asset identifier
					composedRequest.setAction(addNoteEvent.getType());//Get the action over the asset
					composedRequest.setEventId(addNoteEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.ADD_NOTE);
				}

			}else if (event.getType().equals(EventTypes.PACKAGE)){
				if (event instanceof PackageObserverEvent) {
					PackageObserverEvent packageEvent = (PackageObserverEvent) event;
					requestedCorporateAsset.setId(packageEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(packageEvent.getDescription());//Get the asset identifier
					composedRequest.setAction(packageEvent.getType());//Get the action over the asset
					composedRequest.setEventId(packageEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.PACKAGE);
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
			if ((requestedCorporateAsset.getLocation()!=null)&&(requestedCorporateAsset.getLocation().contains("onfidential"))){
				requestedCorporateAsset.setConfidential_level(Constants.CONFIDENTIAL);
				requestedCorporateAsset.setValue(12000);
				logger.info("confidential asset: value 12000");
			}else{
				requestedCorporateAsset.setValue(0);
				logger.info("normal asset: value 0");
			}	
		}
		
		logger.info("title:"+requestedCorporateAsset.getTitle());
		if ((requestedCorporateAsset.getTitle()!=null)&&(requestedCorporateAsset.getTitle().contains("maybe"))){
			logger.info("set value to 16000:"+ requestedCorporateAsset.getTitle());
			requestedCorporateAsset.setValue(16000);
		}else if ((requestedCorporateAsset.getTitle()!=null)&&(requestedCorporateAsset.getTitle().contains("opportunity"))){
			logger.info("set value to 32000:"+ requestedCorporateAsset.getTitle());
			requestedCorporateAsset.setValue(32000);
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
		entityAsset.setAvailable(new Date());
		String assetId = dbManager.setAsset(entityAsset);
		
		//Assign correct asset to simple event
		
		SimpleEvents associatedEvent = dbManager.updateSimpleEvent(event.getType(), assetId );
		
		if (associatedEvent!=null){
			composedRequest.setEventId(Long.valueOf(associatedEvent.getEventId()));
		}else{
			if (isInteger(event.getType(),10)){
				associatedEvent = dbManager.findLastEventByEventType(Integer.valueOf(event.getType()));
			}else{
				EventType eventType = dbManager.getEventTypeByKey(event.getType());
				int eventTypeIndex = eventType.getEventTypeId();
				associatedEvent = dbManager.findLastEventByEventType(eventTypeIndex);
			}
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
	
	
	public static AccessRequest composeAccessRequestOpportunity(Event event, Opportunity opportunity){
		
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
						if((fileEvent.getPath()!=null)&&(fileEvent.getPath().toUpperCase().contains(Constants.CONFIDENTIAL))){
							requestedCorporateAsset.setConfidential_level(Constants.CONFIDENTIAL);
						}else{
							requestedCorporateAsset.setConfidential_level(Constants.PUBLIC);
						}	
					}
					//composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
					composedRequest.setAction(fileEvent.getType());//Get the action over the asset
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
					//composedRequest.setAction(appEvent.getEvent());//Get the action over the asset
					composedRequest.setAction(appEvent.getType());//Get the action over the asset
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
					//composedRequest.setAction(fileEvent.getEvent());//Get the action over the asset
					composedRequest.setAction(fileEvent.getType());//Get the action over the asset
					composedRequest.setEventId(fileEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.SAVE_ASSET);
				}
			}else if (event.getType().equals(EventTypes.USER_ENTERED_PASSWORD_FIELD)){
				if (event instanceof PasswordEvent) {
					PasswordEvent pwdEvent = (PasswordEvent) event;
					requestedCorporateAsset.setId(pwdEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(pwdEvent.getPackageName());//Get the asset identifier
					composedRequest.setAction(pwdEvent.getType());//Get the action over the asset
					composedRequest.setEventId(pwdEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.USER_ENTERED_PASSWORD_FIELD);
				}
			}else if (event.getType().equals(EventTypes.USB_DEVICE_CONNECTED)){
				if (event instanceof USBDeviceConnectedEvent) {
					USBDeviceConnectedEvent usbEvent = (USBDeviceConnectedEvent) event;
					requestedCorporateAsset.setId(usbEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(usbEvent.getDescription());//Get the asset identifier
					composedRequest.setAction(usbEvent.getType());//Get the action over the asset
					composedRequest.setEventId(usbEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.USB_DEVICE_CONNECTED);
				}

			}else if (event.getType().equals(EventTypes.ADD_NOTE)){
				if (event instanceof AddNoteEvent) {
					AddNoteEvent addNoteEvent = (AddNoteEvent) event;
					requestedCorporateAsset.setId(addNoteEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(addNoteEvent.getDescription());//Get the asset identifier
					composedRequest.setAction(addNoteEvent.getType());//Get the action over the asset
					composedRequest.setEventId(addNoteEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.ADD_NOTE);
				}

			}else if (event.getType().equals(EventTypes.PACKAGE)){
				if (event instanceof PackageObserverEvent) {
					PackageObserverEvent packageEvent = (PackageObserverEvent) event;
					requestedCorporateAsset.setId(packageEvent.getId());//Get the asset identifier		
					requestedCorporateAsset.setLocation(packageEvent.getDescription());//Get the asset identifier
					composedRequest.setAction(packageEvent.getType());//Get the action over the asset
					composedRequest.setEventId(packageEvent.getTimestamp());
					requestedCorporateAsset.setDescription(EventTypes.PACKAGE);
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
		entityAsset.setAvailable(new Date());
		String assetId = dbManager.setAsset(entityAsset);
		
		//Assign correct asset to simple event
		
		SimpleEvents associatedEvent = dbManager.updateSimpleEvent(event.getType(), assetId );
		
		if (associatedEvent!=null){
			composedRequest.setEventId(Long.valueOf(associatedEvent.getEventId()));
		}else{
			if (isInteger(event.getType(),10)){
				associatedEvent = dbManager.findLastEventByEventType(Integer.valueOf(event.getType()));
			}else{
				EventType eventType = dbManager.getEventTypeByKey(event.getType());
				int eventTypeIndex = eventType.getEventTypeId();
				associatedEvent = dbManager.findLastEventByEventType(eventTypeIndex);
			}
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
		
		//Compose opportunity part
		OpportunityDescriptor opDescriptor = new OpportunityDescriptor();
		List<Outcome> outcomes = new ArrayList<Outcome>();
		double costBenefit = Integer.valueOf(opportunity.getTime())*200;
		Outcome outcome1 = new Outcome(Constants.WORKING_HOURS_OPP, costBenefit);
		outcomes.add(outcome1);
		Outcome outcome2 = new Outcome(opportunity.getLossDescription(), Double.valueOf(opportunity.getLossCost()));
		opDescriptor.setOutcomes(outcomes);
		outcomes.add(outcome2);
		composedRequest.setOpportunityDescriptor(opDescriptor);
		
		return composedRequest;
	}
	
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
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
		assets.setValue(asset.getValue());
		if (asset.getConfidential_level()==null){
			assets.setConfidentialLevel(Constants.PUBLIC);
		}else{
			assets.setConfidentialLevel(asset.getConfidential_level());
			assets.setValue(1000);
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
		
		return assets;
	}
	
	

}
