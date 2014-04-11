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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.common.InternalRuleBase;
import org.drools.definition.type.FactType;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.Resource;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;

import eu.musesproject.server.eventprocessor.correlator.engine.changeset.Add;
import eu.musesproject.server.eventprocessor.correlator.engine.changeset.ChangeSet;
import eu.musesproject.server.eventprocessor.correlator.engine.changeset.ObjectFactory;
import eu.musesproject.server.eventprocessor.correlator.engine.changeset.Remove;

public abstract class AbstractDroolsEngine implements DroolsEngineService {

	StatefulKnowledgeSession ksession;
	KnowledgeAgent kagent;
	static Logger log = Logger.getLogger(AbstractDroolsEngine.class);

	static String changesetPath;
	static Resource engineResource;

	private ReteooStatefulSession wm;

	public AbstractDroolsEngine(FileInputStream input) throws IOException,
			ClassNotFoundException {
		loadCorrelator(input);
	}

	public AbstractDroolsEngine(KnowledgeBuilder kbuilder, Object[] globals,
			Environment env, KnowledgeBaseConfiguration config,
			SessionConfiguration sessionConf) {

		this(kbuilder, globals, env, config, sessionConf, null, false);
	}

	public AbstractDroolsEngine(KnowledgeBuilder kbuilder, Object[] globals,
			Environment env, KnowledgeBaseConfiguration config,
			SessionConfiguration sessionConf, String changesetPath) {

		this(kbuilder, globals, env, config, sessionConf, changesetPath, true);
	}

	public AbstractDroolsEngine(KnowledgeBuilder kbuilder, Object[] globals,
			Environment env, KnowledgeBaseConfiguration config,
			SessionConfiguration sessionConf, String changesetPath,
			Boolean hasKnowledgeAgent) {

		KnowledgeBase kbase = createKBase(kbuilder, config);

		if (hasKnowledgeAgent) {
			createKnowledgeAgent(kbase, "MyKAgent");
			wm = (ReteooStatefulSession) ((KnowledgeBaseImpl) kagent
					.getKnowledgeBase()).ruleBase.newStatefulSession(
					(SessionConfiguration) sessionConf, env);
			ksession = new StatefulKnowledgeSessionImpl(wm, kagent
					.getKnowledgeBase());
		} else {
			ksession = kbase.newStatefulKnowledgeSession(sessionConf, env);
		}

		for (Object global : globals) {
			ksession.setGlobal(global.getClass().getSimpleName(), global);
		}
	}

	/**
	 * Creates KnowledgeBase from KBuilder and KBaseConfig
	 * 
	 * @param kbuilder
	 * @param config
	 * @return
	 */
	protected KnowledgeBase createKBase(KnowledgeBuilder kbuilder,
			KnowledgeBaseConfiguration config) {

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

		return kbase;
	}

	/**
	 * Creates a KnowledgeAgent for loading the RuleBase changes dynamically
	 * 
	 * @param kbase
	 * @param correlatorId
	 */
	protected void createKnowledgeAgent(KnowledgeBase kbase, String engineName) {
		Properties agentProps = new Properties();
		agentProps.setProperty("drools.agent.scanResources", "true");
		agentProps.setProperty("drools.agent.monitorChangeSetEvents", "true");
		agentProps.setProperty("drools.agent.scanDirectories", "true");

		kagent = KnowledgeAgentFactory.newKnowledgeAgent(engineName, kbase,
				KnowledgeAgentFactory
						.newKnowledgeAgentConfiguration(agentProps));
	}

	/**
	 * Updates the KnowledgeSession with the KnowledgeAgent RuleBase info
	 */
	public void updateKSession() {
		if (kagent != null) {
			for (org.drools.runtime.rule.FactHandle fc : ksession
					.getFactHandles()) {
				log.info("Before ********************"
						+ fc.toExternalForm());
			}

			wm.setRuleBase((InternalRuleBase) ((KnowledgeBaseImpl) kagent.getKnowledgeBase()).ruleBase);
			ksession = new StatefulKnowledgeSessionImpl(wm, kagent.getKnowledgeBase());

			for (org.drools.runtime.rule.FactHandle fc : ksession.getFactHandles()) {
				log.info("After ********************"
						+ fc.toExternalForm());
			}

			log.info("*************** #Rule:"	+ ksession.getKnowledgeBase().getKnowledgePackages().iterator().next().getRules().size());
		}
	}

	public void saveCorrelator(FileOutputStream out) throws IOException {

		DroolsObjectOutputStream droolsOut = new DroolsObjectOutputStream(out);
		droolsOut.writeObject(ksession.getKnowledgeBase());
		Marshaller mas = createMarshaller(ksession.getKnowledgeBase());
		mas.marshall(droolsOut, ksession);
	}

	public final void loadCorrelator(FileInputStream input) throws IOException,
			ClassNotFoundException {

		DroolsObjectInputStream droolsIn = new DroolsObjectInputStream(input);
		KnowledgeBase kbase = (KnowledgeBase) droolsIn.readObject();
		Marshaller mas = createMarshaller(kbase);
		ksession = mas.unmarshall(droolsIn);
	}

	private Marshaller createMarshaller(KnowledgeBase kbase) {
		ObjectMarshallingStrategyAcceptor acceptor = MarshallerFactory
				.newClassFilterAcceptor(new String[] { "*.*" });
		ObjectMarshallingStrategy strategy = MarshallerFactory
				.newSerializeMarshallingStrategy(acceptor);
		return MarshallerFactory.newMarshaller(kbase,
				new ObjectMarshallingStrategy[] { strategy });
	}

	protected static void createChangeSetInfo(String rulePackagePath,
			String droolsEngineName, Boolean add) {

		ObjectFactory of = new ObjectFactory();

		ChangeSet cs = of.createChangeSet();
		eu.musesproject.server.eventprocessor.correlator.engine.changeset.Resource rs = of
				.createResource();
		rs.setSource(rulePackagePath);
		rs.setType("DRL");

		if (add) {
			Add ad = of.createAdd();
			ad.getContent().add(rs);
			cs.setAdd(ad);
		} else {
			Remove rm = of.createRemove();
			rm.getContent().add(rs);
			cs.setRemove(rm);
		}

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(ChangeSet.class.getPackage()
					.getName());
			javax.xml.bind.Marshaller marshaller = jaxbContext
					.createMarshaller();

			File file = new File(changesetPath + droolsEngineName + ".xml");
			if (file.exists()){
				file.delete();
			}	
			marshaller.marshal(cs, new FileWriter(changesetPath
					+ droolsEngineName + ".xml"));
		} catch (JAXBException e) {
			log.error(e);
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * 
	 */
	public FactType getFactType(String factName) {

		return ksession.getKnowledgeBase().getFactType("MusesCorrelator",
				factName);

	}

	private RuleBase getRuleBase() {

		return ((KnowledgeBaseImpl) ksession.getKnowledgeBase()).ruleBase;
	}

	public abstract void insertFact(Object fact);
}
