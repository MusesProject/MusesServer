package eu.musesproject.server.eventprocessor.correlator.engine.changeset.notifficator;

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

import org.apache.log4j.Logger;
import org.drools.ChangeSet;
import org.drools.event.io.ResourceChangeListener;

import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;

public class DroolsEngineResourceNotifier implements ResourceChangeListener {

	private Logger log = Logger.getLogger(DroolsEngineResourceNotifier.class);

	private DroolsEngineService droolsEngine;

	public DroolsEngineResourceNotifier(DroolsEngineService droolsEngine) {
		this.droolsEngine = droolsEngine;
	}

	public void resourcesChanged(ChangeSet changeSet) {
		try {
			Thread.sleep(10000);
			droolsEngine.updateKSession();
		} catch (InterruptedException e) {
			log.error(e);
		}
	}
}
