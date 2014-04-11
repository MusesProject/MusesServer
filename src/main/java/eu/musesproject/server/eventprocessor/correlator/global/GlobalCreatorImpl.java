package eu.musesproject.server.eventprocessor.correlator.global;

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

public class GlobalCreatorImpl implements GlobalCreator {
	
	public Object[] globals = null;

	public Object[] createGlobalContexts() {
		//return new Object[] { createOwlGlobal(), createStatusGlobal() };
		globals = new Object[] { createStatusGlobal(), createRT2AEGlobal() }; 
		return globals;
	}

	public OwlGlobal createOwlGlobal() {
		return new OwlGlobal();
	}
	
	public StatusGlobal createStatusGlobal() {
		return new StatusGlobal();
	}
	
	public Rt2aeGlobal createRT2AEGlobal() {
		return new Rt2aeGlobal();
	}
	
	public Object[] getGlobals(){
		return globals;
	}

}
