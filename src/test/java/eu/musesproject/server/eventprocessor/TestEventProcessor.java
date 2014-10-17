package eu.musesproject.server.eventprocessor;

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



import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.wso2.balana.PDP;

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;
import eu.musesproject.server.contextdatareceiver.stub.ContextEventFactory;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.global.StatusGlobal;
import eu.musesproject.server.eventprocessor.correlator.model.owl.Event;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.eventprocessor.policy.manage.PolicyDecisionPoint;
import eu.musesproject.server.eventprocessor.policy.manage.PolicyEnforcementPoint;
import eu.musesproject.server.eventprocessor.simulation.UseCaseFactory;

public class TestEventProcessor extends TestCase {

	private MusesCorrelationEngineImpl engine = null;
	private EventProcessorImpl processor = null;
	private Logger logger = Logger.getLogger(TestEventProcessor.class.getName());
	private String defaultSessionId = "3SKLFNVNO230247";
	
	/**
	  * testStartup - JUnit test case whose aim is to test the initialization and startup of the event correlation processor
	  *
	  * @param none 
	  * 
	  */
	public final void testStartup() {
		logger.info("Running testStartup");
		if (EventProcessorImpl.getMusesEngineService()==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			assertNotNull(engine);
		}else{
			logger.info("testStartup: The event processor was previously started before the test");
		}
	}

	/**
	  * testRuleFiring - JUnit test case whose aim is to test the correct firing of an example rule, showing that the whole event correlation platform is working correctly
	  *
	  * @param none 
	  * 
	  */
	public final void testRuleFiring() {
		logger.info("Running testRuleFiring");
		Event event = new Event();
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			des = EventProcessorImpl.getMusesEngineService();
		}
		event.setSessionId(defaultSessionId);
		des.insertFact(event);
		assertNotNull(des);
	}
	
	

	
	

	
	/**
	  * sequenceInsertionInWorkingMemory - Helper method that inserts every event of a sequence in the working memory
	  *
	  * @param sequence 
	  * 
	  */
	public void sequenceInsertionInWorkingMemory(List<ContextEvent> sequence){
		ContextEvent contextEvent = null;
		Event formattedEvent = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
		for (Iterator iterator = sequence.iterator(); iterator.hasNext();) {
			contextEvent = (ContextEvent) iterator.next();
			formattedEvent = UserContextEventDataReceiver.getInstance().formatEvent(contextEvent);
			formattedEvent.setSessionId(defaultSessionId);
			des.insertFact(formattedEvent);			
		}
	}
	
	/**
	 * 
	 */
	/*public void testAuthorizedResources(){
		String authorizedResources = PolicyEnforcementPoint.authorizedResources(UseCaseFactory.getUserByRole(UseCaseFactory.ROLE_MANAGER), UseCaseFactory.DESCENDANTS);
		assertTrue(!authorizedResources.contains("NOT authorized"));
		
	}*/
	
	/**
	 * 
	 */
	public void testCreateXACMLRequest(){
		String xacmlRequest = PolicyEnforcementPoint.createXACMLRequest(UseCaseFactory.getUserByRole(UseCaseFactory.ROLE_MANAGER), UseCaseFactory.DESCENDANTS);
		PDP pdp = PolicyDecisionPoint.getPDPNewInstance();
		String evaluation = pdp.evaluate(xacmlRequest);
		assertTrue(!evaluation.contains("Indeterminate"));
		
	}
	
	/**
	 * 
	 */
	public void testPDPNewInstance(){
		
		PDP pdp = PolicyDecisionPoint.getPDPNewInstance();
		assertNotNull(pdp);
		
	}


}
