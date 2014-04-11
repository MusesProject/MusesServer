package eu.musesproject.server.eventprocessor.correlator.model.owl.properties;

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

public final class ObjectProperties {

	public static final String MAGENT_SYMPTOM = "isRelatedWith";
	public static final String SYMPTOM_FAULT = "isSymptomOf";
	public static final String FAULT_ROOTCAUSE = "causedBy";
	public static final String ROOTCAUSE_SOLUTION = "hasSolution";

	public static final String CAUSED_BY = "causedBy";

}
