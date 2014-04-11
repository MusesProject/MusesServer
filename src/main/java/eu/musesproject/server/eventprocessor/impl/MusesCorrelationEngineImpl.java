/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */

/**
 * The Class MusesCorrelationEngineImpl.
 * 
 * @author Sergio Zamarripa (S2)
 * @version 26 sep 2013
 */

package eu.musesproject.server.eventprocessor.impl;

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

import eu.musesproject.server.continuousrealtimeeventprocessor.model.KnowledgeBase;
import eu.musesproject.server.continuousrealtimeeventprocessor.model.KnowledgeBaseConfiguration;
import eu.musesproject.server.continuousrealtimeeventprocessor.IMusesCorrelationEngine;
import eu.musesproject.server.continuousrealtimeeventprocessor.model.KnowledgeBuilder;

/**
 * The Class MusesCorrelationEngineImpl
 * 
 * @author Sergio Zamarripa (S2)
 * @version Sep 24, 2013
 */
public class MusesCorrelationEngineImpl implements IMusesCorrelationEngine{

	@Override
	public KnowledgeBase createKBase(KnowledgeBuilder kbuilder,
			KnowledgeBaseConfiguration config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadSecurityRules() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifySecurityRulesUpdate() {
		// TODO Auto-generated method stub
		
	}
	
}