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

public class TextInput extends CepFact {

	String hasText;

	public String getHasText() {
		return hasText;
	}

	public void setHasText(String hasText) {
		this.hasText = hasText;
	}

	public TextInput() {
		super();
	}

	public TextInput(String anIdEntity, String aDescription) {
		super(anIdEntity, aDescription);
	}

	public TextInput(String anIdEntity, String aDescription,
			String anObjectClass) {
		super(anIdEntity, aDescription, anObjectClass);
	}

	public TextInput(String anIdEntity, String aDescription, String aText,
			String anObjectClass) {
		super(anIdEntity, aDescription, anObjectClass);
		setHasText(aText);
	}

	public TextInput(Resource aResource) {
		super(aResource);
	}

}