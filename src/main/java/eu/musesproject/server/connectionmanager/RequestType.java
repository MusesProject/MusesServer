/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

/**
 * This Class holds the constants for connection-type header
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class RequestType {
	public static final String CONNECT = "connect";
	public static final String DATA = "data";
	public static final String POLL = "poll";
	public static final String ACK = "ack";
	public static final String DISCONNECT = "disconnect";
}