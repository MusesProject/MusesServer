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

import org.apache.log4j.Logger;
import org.drools.ClockType;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.Environment;

public class TemporalDroolsEngineServiceImpl extends AbstractDroolsEngine {

	private String rulePackagePath;
	private static boolean startUpError = false;
	private static Logger logger = Logger.getLogger(TemporalDroolsEngineServiceImpl.class);

	private static KnowledgeBuilder getKnowledgeBuilder(String rulePackagePath,
			String droolsEngineName) {

		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();

		changesetPath = "src/main/resources/";

		createChangeSetInfo(rulePackagePath, droolsEngineName, true);
		engineResource = ResourceFactory.newFileResource(changesetPath
				+ droolsEngineName + ".xml");
		kbuilder.add(engineResource, ResourceType.CHANGE_SET);

		if (kbuilder.hasErrors()) {
			startUpError = true;
			Iterator<KnowledgeBuilderError> ite = kbuilder.getErrors()
					.iterator();
			while (ite.hasNext()) {
				logger.error(ite.next().getMessage());
			}
			return null;
		} else {
			return kbuilder;
		}
	}

	private static KnowledgeBaseConfiguration getKBaseConfig() {
		KnowledgeBaseConfiguration config = KnowledgeBaseFactory
				.newKnowledgeBaseConfiguration();

		config.setOption(EventProcessingOption.STREAM);
		return config;
	}

	private static SessionConfiguration getSessionConf() {
		SessionConfiguration sessionConf = new SessionConfiguration();
		sessionConf.setClockType(ClockType.REALTIME_CLOCK);
		return sessionConf;
	}

	public TemporalDroolsEngineServiceImpl(FileInputStream input,
			String engineName) throws IOException, ClassNotFoundException {

		super(input);

		createKnowledgeAgent(ksession.getKnowledgeBase(), engineName);
		kagent.applyChangeSet(ResourceFactory.newFileResource(changesetPath
				+ engineName + ".xml"));
	}

	public TemporalDroolsEngineServiceImpl(String rulePackagePath,
			String engineName, Object[] globals, Environment env,
			String changesetPath) {

		super(getKnowledgeBuilder(rulePackagePath, engineName), globals, env,
				getKBaseConfig(), getSessionConf(), changesetPath);

		kagent.applyChangeSet(engineResource);
		this.rulePackagePath = rulePackagePath;
	}

	public void insertFact(Object fact) {

		ksession.insert(fact);
		ksession.fireAllRules();
	}

	public void removeCorrelator() {
	}
	
	public boolean engineInError(){
		return startUpError;
	}

}
