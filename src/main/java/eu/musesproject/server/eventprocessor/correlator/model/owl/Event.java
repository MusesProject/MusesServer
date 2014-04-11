package eu.musesproject.server.eventprocessor.correlator.model.owl;

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

import com.hp.hpl.jena.rdf.model.Resource;

import eu.musesproject.server.eventprocessor.correlator.model.CepFact;

public class Event extends CepFact {

	String type;
	String uid;

	public Event() {
		super();
	}

	public Event(String anIdEntity, String aDescription) {
		super(anIdEntity, aDescription);
	}

	public Event(String anIdEntity, String aDescription, String atype) {
		super(anIdEntity, aDescription, atype);
	}

	public Event(Resource aResource) {
		super(aResource);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}