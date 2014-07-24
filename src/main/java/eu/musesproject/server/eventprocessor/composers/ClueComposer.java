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
import eu.musesproject.server.eventprocessor.correlator.model.owl.PackageObserverEvent;
import eu.musesproject.server.eventprocessor.correlator.model.owl.VirusFoundEvent;
import eu.musesproject.server.risktrust.Clue;


public class ClueComposer {
	
	
	/*public static Clue composeClue(int requestId, Event event){
		
		Clue composedClue = new Clue();
		//composedClue.setRequestId(requestId);
		if (event instanceof FileObserverEvent){
			FileObserverEvent fileEvent = (FileObserverEvent)event;
			composedClue.setId((int)fileEvent.getTimestamp());
		}else if (event instanceof AppObserverEvent){
			AppObserverEvent appEvent = (AppObserverEvent)event;
			composedClue.setId((int)appEvent.getTimestamp());
		}
		//AccessRequest accessRequest = Rt2aeGlobal.getRequestById(requestId);
		//composedClue.setAssetId(accessRequest.getRequestedCorporateAsset().getId());
		
		return composedClue;
	}*/
	
public static Clue composeClue(Event event, String name, String type){
		
		Clue composedClue = new Clue();
		
		if (event instanceof FileObserverEvent){
			FileObserverEvent fileEvent = (FileObserverEvent)event;
			composedClue.setId((int)fileEvent.getTimestamp());
			composedClue.setTimestamp(fileEvent.getTimestamp());
		}else if (event instanceof AppObserverEvent){
			AppObserverEvent appEvent = (AppObserverEvent)event;
			composedClue.setId((int)appEvent.getTimestamp());
			composedClue.setTimestamp(appEvent.getTimestamp());
		}else if (event instanceof PackageObserverEvent){
			PackageObserverEvent pkgEvent = (PackageObserverEvent)event;
			composedClue.setId((int)pkgEvent.getTimestamp());
			composedClue.setTimestamp(pkgEvent.getTimestamp());
		}else if (event instanceof EmailEvent){
			EmailEvent emailEvent = (EmailEvent)event;
			composedClue.setId((int)emailEvent.getTimestamp());
			composedClue.setTimestamp(emailEvent.getTimestamp());
		}else if (event instanceof VirusFoundEvent){
			VirusFoundEvent virusFoundEvent = (VirusFoundEvent)event;
			composedClue.setId((int)virusFoundEvent.getTimestamp());
			composedClue.setTimestamp(virusFoundEvent.getTimestamp());
		}
		composedClue.setName(name);
		composedClue.setType(type);
		return composedClue;
	}

}
