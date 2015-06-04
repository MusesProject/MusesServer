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

import javax.servlet.*;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.connectionmanager.ComMainServlet;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.eventprocessor.util.EventTypes;

public class CorrelationStartupServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1991438642299144762L;
	
	private static Logger logger = Logger.getLogger(CorrelationStartupServlet.class.getName());
	private static final String MUSES_TAG = "MUSES_TAG";

	public void init() throws ServletException{
		logger.log(Level.INFO, MUSES_TAG + "CorrelationStartupServlet");
		EventProcessor processor = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
	
		if (des==null){
			processor = new EventProcessorImpl();
			processor.startTemporalCorrelation("drl");
			EventProcessorImpl.getMusesEngineService();
			UserContextEventDataReceiver.storeEvent(EventTypes.RESTART, "muses", "MUSES-Server", "server", "Valencia", "Server restart: correlation startup init");
		}else{
			logger.info("DroolsEngine Service already available");
		}
		logger.info("Correlation platform initialized successfully");
		
    }
}