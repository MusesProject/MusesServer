package eu.musesproject.server.connectionmanager;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.server.contextdatareceiver.ConnectionCallbacksImpl;
	
	/**
	 * Class ComMainServlet
	 * 
	 * @author Yasir Ali
	 * @version Jan 27, 2014
	 */

public class ComMainServlet extends HttpServlet {

	// a servlet has to be thread safe so no instance variables are allowed (this is not optional, it is a rule)
	// read this for more info http://www.javaworld.com/article/2072798/java-web-development/write-thread-safe-servlets.html
	// there is no issue with those that are final int, final long or final string because they are read-only
	// the rest needs to fulfill one of these options: become local variables or be thread safe or be stateless
	private static final long serialVersionUID = 1L; 
	private static Logger logger = Logger.getLogger(ComMainServlet.class.getName());
	private Helper helper; // became stateless
	//private SessionHandler sessionHandler; // tomcat handles sessions for us
	private ConnectionManager connectionManager; // removing this requires going from pull to push
	//private String dataAttachedInCurrentReuqest; // became local variable
	//private String dataToSendBackInResponse="";  // became local variable
	private static final String CONNECTION_TYPE = "connection-type";
	private static final String DATA = "data";
	private static final int INTERVAL_TO_WAIT = 5;
	private static final long SLEEP_INTERVAL = 1000;
	private static final String MUSES_TAG = "MUSES_TAG";
	//private static String connectionType = "connect"; // became local variable
	
	/**
	 * 
	 * @param sessionHandler
	 * @param helper
	 * @param communicationManager
	 */
	
	public ComMainServlet(SessionHandler sessionHandler, Helper helper, ConnectionManager communicationManager){
		//this.sessionHandler=sessionHandler;
		this.helper=helper;
		this.connectionManager=communicationManager;
	}
	

	public ComMainServlet() {
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.log(Level.INFO, MUSES_TAG + " init");
		helper = new Helper();
		connectionManager = ConnectionManager.getInstance(); 
		//sessionHandler = SessionHandler.getInstance(getServletContext());
		ConnectionCallbacksImpl.getInstance(); // register callback
		
		// the connection manager should be moved to ApplicationScope like this
		//ConnectionManager connectionManager = ConnectionManager.getInstance(); 
		//config.getServletContext().setAttribute("ConnectionManager.instance", connectionManager);
		
		// the retrieval in doPost() would look like this. bear in mind that objects in application 
		// scope like ConnectionManager will be accessed by many threads so they must be thread-safe
		//connectionManager = (ConnectionManager) this.getServletContext().getAttribute("ConnectionManager.instance");

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

		// by calling getSession() we send a signal to tomcat that we want a session created if there
		// isn't any already. bear in mind that tomcat will put the JSESSIONID as a cookie in the 
		// response for us so we don't have to do it ourselves. btw every jsp file already contains this 
		// method call.
		HttpSession session = request.getSession();
		
		String dataAttachedInCurrentReuqest;
		String dataToSendBackInResponse="";
		String connectionType = "connect";
				
		// create cookie if not in the request
		//Cookie cookie = helper.extractCookie(request);
		//String currentJSessionID = cookie.getValue();
		String currentJSessionID = session.getId();
		
		// Retrieve data in the request
		if (request.getMethod().equalsIgnoreCase("POST")) {
			// Retrieve connection-type from request header
			connectionType = request.getHeader(CONNECTION_TYPE);
			dataAttachedInCurrentReuqest = Helper.getRequestData(request);
		}else  {
			// Retrieve connection-type from request parameter
			connectionType = DATA;
			dataAttachedInCurrentReuqest = request.getParameter(DATA);
		} 

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
			
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "nocache");
	        response.setCharacterEncoding("utf-8");
			
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
			Helper.disconnect(request);
			//sessionHandler.removeCookieToList(cookie);
			ConnectionManager.toSessionCb(currentJSessionID, Statuses.DISCONNECTED);
		} 
		
		// Add session id to the List
//		if (currentJSessionID != null && !connectionType.equalsIgnoreCase(RequestType.DISCONNECT) ) {
//			//sessionHandler.addCookieToList(cookie);
//		}

		// the cookie should only be set by tomcat once when the session has been created. 
		// after that the client will include the cookie inside every request until the cookie
		// expires. btw this is the correct mime type for json (Source: RFC 4627)
		// should already be in response because we called the getSession above
		//response.addCookie(cookie);
		response.setContentType("application/json");
	}
	
	// we don't have any instance variable so we can not have a getter either. this method is called
	// by some unit test assert methods. those methods should instead extract the payload from httpsresponse
	//public String getResponseData(){
	//	return dataToSendBackInResponse;
	//}
	
	
	public String waitForDataIfAvailable(int timeout, String currentJSessionID){
		int i=1;
		while(i<=timeout){
			Queue<DataHandler> dQueue = connectionManager.getDataHandlerQueue();
			if (dQueue.size()>=1) {
				for (DataHandler dataHandler : connectionManager.getDataHandlerQueue()){ // FIXME concurrent thread
					if (dataHandler.getSessionId().equalsIgnoreCase(currentJSessionID)){
						connectionManager.removeDataHandler(dataHandler);
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
		doPost(request, response);
	}
	
}

