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

import eu.musesproject.server.eventprocessor.correlator.model.owl.AppObserverEvent;
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

public class AccessRequestComposer {
	
	
	public static AccessRequest composeAccessRequest(Event event){
		
		AccessRequest composedRequest = new AccessRequest();
		Asset requestedCorporateAsset = new Asset();
		
		if (event.getType().equals(EventTypes.FILEOBSERVER)){			
			FileObserverEvent fileEvent = (FileObserverEvent) event;
			requestedCorporateAsset.setId(fileEvent.getId());//Get the asset identifier		
			requestedCorporateAsset.setLocation(fileEvent.getPath());//Get the asset identifier
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
			requestedCorporateAsset.setId(emailEvent.getId());//Get the asset identifier		
			requestedCorporateAsset.setLocation(emailEvent.getAttachmentName());//Get the asset identifier //FIXME This should be a location, hence we should look into the database for the location in the repository
			requestedCorporateAsset.setValue(15000);//FIXME Set value from the assets' database
			requestedCorporateAsset.setConfidential_level("STRICTLY_CONFIDENTIAL"); //FIXME Set confidential level by means of the domains' database
			composedRequest.setAction(emailEvent.getType());//Get the action over the asset
			composedRequest.setEventId(emailEvent.getTimestamp());
		}
		
		requestedCorporateAsset.setValue(0);
		if (event instanceof OpenFileEvent){
			OpenFileEvent fileEvent = (OpenFileEvent)event;
			requestedCorporateAsset.setTitle(fileEvent.getAssetTypeId());//TODO Asset information should be completed
		}
		User user = new User();//TODO User information should be retrieved
		Device device = new Device();//TODO Device information should be retrieved
		UserTrustValue userTrustValue = new UserTrustValue();
		user.setUsertrustvalue(userTrustValue);
		DeviceTrustValue deviceTrustValue = new DeviceTrustValue();
		device.setDevicetrustvalue(deviceTrustValue);
		composedRequest.setUser(user);
		composedRequest.setDevice(device);
		composedRequest.setRequestedCorporateAsset(requestedCorporateAsset);
		
		
		
		return composedRequest;
	}

}
