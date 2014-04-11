package eu.musesproject.server.eventprocessor.correlator.model;

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

import java.util.Date;

import com.hp.hpl.jena.rdf.model.Resource;
import eu.musesproject.server.eventprocessor.correlator.model.owl.properties.DataProperties;

public class CepFact extends AbstractCepFact {

	public String idEntity;

	private String description;

	private String objectClass;

	public String getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(String idEntity) {
		this.idEntity = idEntity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	public CepFact() {
		this.setEvent_date(new Date(System.currentTimeMillis()));
	}

	public CepFact(String idEntidad, String description) {
		this.setIdEntity(idEntidad);
		this.setDescription(description);
		this.setEvent_date(new Date(System.currentTimeMillis()));
	}

	public CepFact(String idEntidad, String description, String objectClass) {
		this.setIdEntity(idEntidad);
		this.setDescription(description);
		this.setEvent_date(new Date(System.currentTimeMillis()));
		this.setObjectClass(objectClass);
	}

	public CepFact(Resource aResource) {
		this.setEvent_date(new Date(System.currentTimeMillis()));
		this.setDescription(aResource.getLocalName());
	}

}
