package eu.musesproject.server.eventprocessor.correlator.engine;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.Environment;

public class DroolsEngineServiceImpl extends AbstractDroolsEngine {

	private static KnowledgeBuilder getKnowledgeBuilder(String rulePackagePath,
			String droolsEngineName) {

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();

		changesetPath = "var/";

		createChangeSetInfo(rulePackagePath, droolsEngineName, true);
		engineResource = ResourceFactory.newFileResource(changesetPath
				+ droolsEngineName + ".xml");
		kbuilder.add(engineResource, ResourceType.CHANGE_SET);

		if (kbuilder.hasErrors()) {
			Iterator<KnowledgeBuilderError> ite = kbuilder.getErrors()
					.iterator();
			while (ite.hasNext()) {
				log.error(ite.next().getMessage());
			}
			return null;
		} else {
			return kbuilder;
		}
	}

	public DroolsEngineServiceImpl(FileInputStream input,
			String droolsEngineName) throws IOException, ClassNotFoundException {
		super(input);

		createKnowledgeAgent(ksession.getKnowledgeBase(), droolsEngineName);
		try {
			kagent.applyChangeSet(ResourceFactory.newFileResource(changesetPath
					+ droolsEngineName + ".xml"));
		} catch (Exception e) {
			log.warn(e);
		}
	}

	public DroolsEngineServiceImpl(String rulePackagePath,
			String droolsEngineName, Object[] globals, Environment env,
			String changesetPath) {

		super(getKnowledgeBuilder(rulePackagePath, droolsEngineName), globals,
				env, null, null, changesetPath);

		kagent.applyChangeSet(engineResource);

	}

	public void insertFact(Object fact) {
		ksession.insert(fact);
		ksession.getWorkingMemoryEntryPoint("ArgosCorrelator").insert(fact);
		ksession.fireAllRules();
	}

}
