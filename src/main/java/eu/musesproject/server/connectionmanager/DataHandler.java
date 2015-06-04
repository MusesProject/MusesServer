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
