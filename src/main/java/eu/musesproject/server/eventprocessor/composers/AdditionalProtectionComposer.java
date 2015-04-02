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

import eu.musesproject.server.eventprocessor.correlator.global.Rt2aeGlobal;
import eu.musesproject.server.eventprocessor.correlator.model.owl.AdditionalProtection;
import eu.musesproject.server.eventprocessor.correlator.model.owl.DeviceProtectionEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.risktrust.AccessRequest;


public class AdditionalProtectionComposer {
	
	
	public static AdditionalProtection composeAdditionalProtection(int requestId, Event event){
		
		AdditionalProtection composedProtection = new AdditionalProtection();
		composedProtection.setRequestId(requestId);
		
		AccessRequest accessRequest = Rt2aeGlobal.getRequestById(requestId);
		composedProtection.setAssetId(accessRequest.getRequestedCorporateAsset().getId());
		
		DeviceProtectionEvent protectionEvent = (DeviceProtectionEvent)event;
		
		composedProtection.setType(EventTypes.DEVICE_PROTECTION);
		composedProtection.setPasswordProtected(protectionEvent.getIsPasswordProtected());
		composedProtection.setPatternProtected(protectionEvent.getIsPatternProtected());
		composedProtection.setRooted(protectionEvent.getIsRooted());
		composedProtection.setTrustedAVInstalled(protectionEvent.isTrustedAntivirusInstalled());
		
		return composedProtection;
	}

}
