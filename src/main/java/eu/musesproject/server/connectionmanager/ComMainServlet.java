/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
	
	/**
	 * Class ComMainServlet
	 * 
	 * @author Yasir Ali
	 * @version Jan 27, 2014
	 */

public class ComMainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ComMainServlet.class.getName());
	private Helper helper;
	private SessionHandler sessionHandler;
	private ConnectionManager connectionManager;
	private String dataAttachedInCurrentReuqest;
	private String dataToSendBackInResponse="";
	private static final String DATA = "data";
	private static final int INTERVAL_TO_WAIT = 5;
	private static final long SLEEP_INTERVAL = 1000;
	private static final String MUSES_TAG = "MUSES_TAG";
	private static final String MUSES_TAG_LEVEL_2 = "MUSES_TAG_LEVEL_2";
	/**
	 * 
	 * @param sessionHandler
	 * @param helper
	 * @param communicationManager
	 */
	
	public ComMainServlet(SessionHandler sessionHandler, Helper helper, ConnectionManager communicationManager){
		this.sessionHandler=sessionHandler;
		this.helper=helper;
		this.connectionManager=communicationManager;
	}
	

	public ComMainServlet() {
	}
	
	/**
	 * Initialize servlet
	 * 
	 * @throws Servlet exception
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.log(Level.INFO, MUSES_TAG + " init");
		helper = new Helper();
		connectionManager = ConnectionManager.getInstance();
		sessionHandler = SessionHandler.getInstance(getServletContext());
	}
	
	/**
	 * Handle POST http/https requests
	 * @param HttpServletRequest request 
	 * @param com.swedenconnectivity.comserverHttpServletResponse response
	 * @throws ServletException, IOException
	 */
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve value from request header
		String connectionType = request.getHeader("connection-type");

		// create cookie if not in the request
		helper.setCookie(request);
		Cookie cookie = helper.getCookie();
		String currentJSessionID = cookie.getValue();
		
		// Retrieve data in the request
		dataAttachedInCurrentReuqest = helper.getRequestData(request);

		// if "connect" request
		if (connectionType!=null && connectionType.equalsIgnoreCase(RequestType.CONNECT)) {
			logger.log(Level.INFO, MUSES_TAG + " Request type:"+connectionType+" with *ID*: "+currentJSessionID+ " with **dataInRequest**: "+dataAttachedInCurrentReuqest);
		}
		
		// if "send-data" request
		if (connectionType!=null && connectionType.equalsIgnoreCase(RequestType.DATA)) {
			// Callback the FL to receive data from the client and get the response data back into string
			dataToSendBackInResponse="";
			if (dataAttachedInCurrentReuqest != null){
				dataToSendBackInResponse = ConnectionManager.toReceive(currentJSessionID, dataAttachedInCurrentReuqest); // FIXME needs to be tested properly
				if (dataToSendBackInResponse == null) {
					dataToSendBackInResponse = "";
				}
			}
			if (dataToSendBackInResponse.equals("")) {
				dataToSendBackInResponse = waitForDataIfAvailable(INTERVAL_TO_WAIT, currentJSessionID);
			}
			
			response.setHeader("Content-Type", "text/plain");
			PrintWriter writer = response.getWriter();
			writer.write(dataToSendBackInResponse);
			
			//response.addHeader(DATA,dataToSendBackInResponse); // Now data is added in the body instead
			logger.log(Level.INFO, MUSES_TAG + " Data avaialble Request type:"+connectionType+" with *ID*: "+currentJSessionID+ " with **dataInResponse**: "+dataToSendBackInResponse);
		}
				
		// if "poll" request
		if (connectionType!= null && connectionType.equalsIgnoreCase(RequestType.POLL)) {
			for (DataHandler dataHandler : connectionManager.getDataHandlerQueue()){ // FIXME concurrent thread
				if (dataHandler.getSessionId().equalsIgnoreCase(currentJSessionID)){
					dataToSendBackInResponse = dataHandler.getData();
					
					response.setHeader("Content-Type", "text/plain");
					PrintWriter writer = response.getWriter();
					writer.write(dataToSendBackInResponse);
					
					//response.addHeader(DATA,dataToSendBackInResponse); // Now data is added in the body instead
					connectionManager.removeDataHandler(dataHandler);
					Queue<DataHandler> dQueue = connectionManager.getDataHandlerQueue();
					if (dQueue.size() > 1) {
						response.addHeader("more-packets", "YES");
					}else {
						response.addHeader("more-packets", "NO");	
					}
					logger.log(Level.INFO, "Data avaialble Request type:"+connectionType+" with *ID*: "+currentJSessionID+ " with **dataInResponse**: "+dataToSendBackInResponse);
					break; // FIXME temporary as multiple same session ids are in the list right now
				}
			}
		}

		// if "ack" request
		if (connectionType!=null && connectionType.equalsIgnoreCase(RequestType.ACK)) {
			logger.log(Level.INFO, "Request type:"+connectionType+" with *ID*: "+currentJSessionID);
			// Clean up the data handler object from the list 
			connectionManager.removeDataHandler(connectionManager.getDataHandlerObject(currentJSessionID));
			ConnectionManager.toSessionCb(currentJSessionID, Statuses.DATA_SENT_SUCCESFULLY);
		}
		
		// if disconnect request 
		// invalidate session from Servlet
		// remove it from the session id list
		// Callback the Functional layer about the disconnect
		if (connectionType!= null && connectionType.equalsIgnoreCase(RequestType.DISCONNECT)) {
			logger.log(Level.INFO, "Request type:"+connectionType+" with *ID*: "+currentJSessionID);
			helper.disconnect(request);
			sessionHandler.removeCookieToList(cookie);
			ConnectionManager.toSessionCb(currentJSessionID, Statuses.DISCONNECTED);
		} 
		
		// Add session id to the List
		if (currentJSessionID != null && !connectionType.equalsIgnoreCase(RequestType.DISCONNECT) ) {
			sessionHandler.addCookieToList(cookie);
		}

		// Setup response to send back
		response.setContentType("text/html");
		response.addCookie(cookie);
	
	}
	public String getResponseData(){
		return dataToSendBackInResponse;
	}
	
	
	public String waitForDataIfAvailable(int timeout, String currentJSessionID){
		int i=1;
		while(i<=timeout){
			Queue<DataHandler> dQueue = connectionManager.getDataHandlerQueue();
			logger.log(Level.INFO, MUSES_TAG_LEVEL_2 + " Current Data queue size is: " +dQueue.size());
			logger.log(Level.INFO, MUSES_TAG_LEVEL_2 + " Current JSessionID: " + currentJSessionID);
			if (dQueue.size()>=1) {
				logger.log(Level.INFO, MUSES_TAG_LEVEL_2 + " Looping the queue..");
				for (DataHandler dataHandler : connectionManager.getDataHandlerQueue()){ // FIXME concurrent thread
					logger.log(Level.INFO, MUSES_TAG_LEVEL_2+ " SessionId in queue:"+ dataHandler.getSessionId());
					if (dataHandler.getSessionId().equalsIgnoreCase(currentJSessionID)){
						logger.log(Level.INFO, MUSES_TAG_LEVEL_2 + " SessionIds matched, sending response back");
						connectionManager.removeDataHandler(dataHandler);
						dataToSendBackInResponse = dataHandler.getData();
						return dataHandler.getData();
					}
				}
			}
			sleep(SLEEP_INTERVAL);
			i++;
		}
		return "";
	}
	
	private void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.log(Level.INFO, e);
		}
	}

	/**
	 * Handle GET http/https requests // Muses will not use this method
	 * @param HttpServletRequest request 
	 * @param HttpServletResponse response
	 * @throws ServletException, IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}

