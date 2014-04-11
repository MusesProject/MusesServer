package eu.musesproject.server.eventprocessor.simulation;

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



import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.correlator.model.owl.OpenFileEvent;

public class AccessRequestFactory {
	public static Event createAccessRequestEvent(){
		
		OpenFileEvent arequest = new OpenFileEvent();
		arequest.setAssetTypeId("urn:oasis:names:tc:xspa:1.0:resource:hl7:type:medical-record");		
		arequest.setUserId("urn:oasis:names:tc:xacml:1.0:subject:subject-id:jdoe");
		arequest.setActionId("urn:oasis:names:tc:xacml:1.0:action:action-id:read");	
		arequest.setType("AccessRequest");
		arequest.setUid("14537453");
		return arequest;
	}
}