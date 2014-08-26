package eu.musesproject.server.connectionmanager;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 Sweden Connectivity
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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Class Connection Manager
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */


public class ConnectionManager implements IConnectionManager{

	private static Logger logger = Logger.getLogger(ConnectionManager.class.getName());
	private static IConnectionCallbacks callBacks;
	private DataHandler dataHandler;
	private SessionHandler sessionCounter;
	private static ConnectionManager connectionManagerSingleton = null;
	private static Queue<DataHandler> dataHandlerQueue = new LinkedList<DataHandler>();
	/**
	 * Constructor initialises callback
	 * @param calBacks
	 */
	public ConnectionManager(IConnectionCallbacks iCallbacks){ // FIXME this constrctor is used by Unit test only
		callBacks = iCallbacks;
	}
	
	private ConnectionManager() {
		sessionCounter = new SessionHandler();
	}
	
	/**
	 * Creare Communication manager singleton if not created
	 * @return singleton object
	 */
	public static ConnectionManager getInstance(){
		if (connectionManagerSingleton == null) {
			connectionManagerSingleton = new ConnectionManager();
		}
		return connectionManagerSingleton;
	}
	
	/**
	 * Sends data back to client with associate session ID
	 * @param String sessionId 
	 * @param String dta
	 * @return void
	 */
	
	@Override
	public void sendData(String sessionId, String dta) { // FIXME if several packets are sent with same session ID there is no way to find out which one was sent
		if (!sessionId.equals(null) && !dta.equals(null) && !dta.equals("")) {
			dataHandler = new DataHandler(sessionId, dta);	
			addDataHandler(dataHandler);
		}
	}

	/**
	 * Get all sessionIds
	 * @return Set<String>
	 */
	@Override
	public Set<String> getSessionIds() {
		return sessionCounter.getSessionIds();
	}
	
	/**
	 * Registers for callbacks
	 * @param ICallBacks iCallBacks
	 * @return void
	 */
	
	@Override
	public void registerReceiveCb(IConnectionCallbacks iCallBacks) {
		// FIXME how to handle the return status 
		if (iCallBacks != null){
			callBacks = iCallBacks;
			logger.log(Level.INFO, "callback registered");
		} else {
			logger.log(Level.INFO, "Passed callback is null");		
		}
	}
	
	/**
	 * Called by connection manager to send data received from the client
	 * @param sessionID
	 * @param dataAttachedInCurrentReuqest
	 * @return data received from the client
	 */
	
	public static String toReceive(String sessionID, String dataAttachedInCurrentReuqest){
		if (callBacks != null){
			return callBacks.receiveCb(sessionID, dataAttachedInCurrentReuqest);
		} else {
			logger.log(Level.INFO, "Callback object is null");// logg
		}
		return null;
	} 

	/**
	 * This method is used by connection manager to add data handler object
	 * @param dataHandler
	 * @return void
	 */
	
	public static  synchronized void addDataHandler(DataHandler dataHandler){
		dataHandlerQueue.add(dataHandler);
	}
	
	/**
	 * This method is used by connection manager to remove data handler object
	 * @param dataHandler
	 * @return void
	 */
	
	public synchronized void removeDataHandler(DataHandler dataHandler) {
		dataHandlerQueue.remove(dataHandler);
	}
	
	/**
	 * This method is used by connection manager to get data handler object with session id
	 * @param session
	 * @return void
	 */
	
	public synchronized DataHandler getDataHandlerObject(String sessionId){
		if (!dataHandlerQueue.isEmpty()) {
			for (DataHandler d : dataHandlerQueue){
				if(d.getSessionId().equalsIgnoreCase(sessionId)){
					return d;
				}
			}
		}
		return null;
	}
	
	/**
	 * Get data handler objects list
	 * @return
	 */
	public synchronized Queue<DataHandler> getDataHandlerQueue() {
		return dataHandlerQueue;
	}
	
	/**
	 * This method is called to session status
	 * @param sessionId
	 * @param status
	 * @return void
	 */
	
	public static void toSessionCb(String sessionId, int status){
		if (callBacks!=null){
			callBacks.sessionCb(sessionId, status);
		} else {
			logger.log(Level.INFO, "Callback object is null");
		}
	}

	
	
}