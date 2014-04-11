/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

/**
 * Class DataHandler handles data sent from the other module from the server
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class DataHandler {

	private String sessionId;
	private String data;
	/**
	 * Constructor to initialize object
	 * @param sessionId
	 * @param data
	 */
	public DataHandler(String sessionId, String data) {
		this.sessionId=sessionId;
		this.data=data;
	}

	public DataHandler() {
	}
	
	/**
	 * Get session id
	 * @return
	 */
	
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * Get data
	 * @return
	 */
	public String getData() {
		return data;
	}
	
	
}
