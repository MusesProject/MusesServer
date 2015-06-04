/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
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


/**
 * This Class holds the constants for connection-type header
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public final class RequestType {
	private RequestType(){};
	public static final String CONNECT = "connect";
	public static final String DATA = "data";
	public static final String POLL = "poll";
	public static final String ACK = "ack";
	public static final String DISCONNECT = "disconnect";
}