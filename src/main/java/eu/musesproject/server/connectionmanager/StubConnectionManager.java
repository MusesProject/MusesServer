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


import eu.musesproject.client.model.contextmonitoring.Event;
import eu.musesproject.server.contextdatareceiver.UserContextEventDataReceiver;

public class StubConnectionManager implements IConnectionManager, IConnectionCallbacks{


	public boolean isActive(){
		return true;
	}
	
	public void notifyEvent(){
		UserContextEventDataReceiver receiver = UserContextEventDataReceiver.getInstance();
		Event event =  new Event();
		receiver.storeEvent(event);
	}


	
	/*
	 * These methods needs to be implemented in order to use connection manager
	 */
	@Override
	public void sendData(String sessionId, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerReceiveCb(IConnectionCallbacks callBacks) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * These methods are called from the connection manager, handle these call here 
	 */
	
	@Override
	public String receiveCb(String sessionId, String receiveData) {
		// Handle callbacks here
		return null;
	}

	@Override
	public void sessionCb(String sessionId, int status) {
		// Handle callbacks here
	}
	
	
	
}