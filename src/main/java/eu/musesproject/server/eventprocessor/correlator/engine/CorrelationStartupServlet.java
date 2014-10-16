package eu.musesproject.server.eventprocessor.correlator.engine;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.connectionmanager.ComMainServlet;
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;

public class CorrelationStartupServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(CorrelationStartupServlet.class.getName());
	private static final String MUSES_TAG = "MUSES_TAG";

	public void init() throws ServletException{
		logger.log(Level.INFO, MUSES_TAG + "CorrelationStartupServlet");
		EventProcessor processor = null;
		MusesCorrelationEngineImpl engine = null;
		DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
	
		if (des==null){
			processor = new EventProcessorImpl();
			engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("/drl");
			des = EventProcessorImpl.getMusesEngineService();
		}else{
			logger.info("DroolsEngine Service already available");
		}
		logger.info("Correlation platform initialized successfully");
		
    }
}