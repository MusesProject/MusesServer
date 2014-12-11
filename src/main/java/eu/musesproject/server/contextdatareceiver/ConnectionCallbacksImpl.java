package eu.musesproject.server.contextdatareceiver;

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

import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.client.model.RequestType;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.authentication.AuthenticationManager;
import eu.musesproject.server.connectionmanager.ConnectionManager;
import eu.musesproject.server.connectionmanager.IConnectionCallbacks;
import eu.musesproject.server.connectionmanager.Statuses;


public class ConnectionCallbacksImpl implements IConnectionCallbacks {
	
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(ConnectionCallbacksImpl.class.getName());
	private ConnectionManager connManager;
	boolean isDataAvailable = false;
	public static volatile String lastSessionId = null;
	private static volatile String data = "";
	public static volatile String receiveData;

	public ConnectionCallbacksImpl(){
		connManager = ConnectionManager.getInstance();
		connManager.registerReceiveCb(this);
		startConnection();
	}
	
	
	private static class ProcessThread implements Runnable {
		List<ContextEvent> list = null;
		String sessionId = null;
		String username = null;
		String deviceId = null;
		int requestId;

		public ProcessThread(List<ContextEvent> contextList, String id, String user, String device, int request) {
			list = contextList;
			sessionId = id;
			username = user;
			deviceId = device;
			requestId = request;
		}

		public void run() {
			UserContextEventDataReceiver.getInstance().processContextEventList(
					list, sessionId, username, deviceId, requestId);
		}
	}
	private void startConnection() {
				
		logger.info("Start Server Connection");
 
	}
	

	@Override
	public String receiveCb(String sessionId, String rData) {
		JSONObject root;
		String requestType = null;
		String username = null;
		String deviceId = null;
		int requestId = 0;

		ConnectionCallbacksImpl.lastSessionId = sessionId;
		ConnectionCallbacksImpl.receiveData = rData;
		
		logger.log(Level.INFO, MUSES_TAG + "Info SS, received callback from CM with data:"+rData);
		
		try {
			// First, get the request type, in order to differentiate between login
			// and data exchange
			root = new JSONObject(ConnectionCallbacksImpl.receiveData);
			requestType = root.getString(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER);

			if (requestType.equals(RequestType.LOGIN)) {
				logger.log(Level.INFO, "Login request");
				// Delegate authentication to AuthenticationManager
				JSONObject authResponse = AuthenticationManager.getInstance().authenticate(root, sessionId);
				logger.log(Level.INFO, MUSES_TAG + "Info SS, Login request=>authenticating ..... authentication response is:"+authResponse.toString()	);
				if (authResponse != null) {
					return authResponse.toString();
				}
			} else {
				//Data exchange: We should check if sessionId is correctly authenticated
				if (AuthenticationManager.getInstance().isAuthenticated(sessionId)) {

					List<ContextEvent> list = JSONManager
							.processJSONMessage(ConnectionCallbacksImpl.receiveData);
					logger.log(Level.INFO, "Starting ProcessThread...");
					
					
					try {
						username = root
								.getString(JSONIdentifiers.AUTH_USERNAME);
						deviceId = root
								.getString(JSONIdentifiers.AUTH_DEVICE_ID);
						if (requestType.equals(RequestType.ONLINE_DECISION)){
							requestId = root.getInt(JSONIdentifiers.REQUEST_IDENTIFIER);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Thread t = new Thread(new ProcessThread(list, sessionId, username, deviceId, requestId));
					t.start();

					logger.log(Level.INFO,
							"Resuming receiveCb after calling ProcessThread...");

					logger.info("*************Receive Callback called: "
							+ ConnectionCallbacksImpl.receiveData
							+ "from client ID " + sessionId);
					if (data != null) {
						connManager.sendData(sessionId,
								ConnectionCallbacksImpl.data);
					}

				} else {//Current sessionId has not been authenticated
					JSONObject response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, "FAIL", "Data cannot be processed: Failed authentication");
					logger.log(Level.INFO, response.toString());
					connManager.sendData(sessionId, response.toString());
				}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}

		return ConnectionCallbacksImpl.data;
	}
	@Override
	public void sessionCb(String sessionId, int status) {
		logger.info("*************Session Callback called Id: " + sessionId + " Status: " + ((status == Statuses.DISCONNECTED) ? "Disconnected" : "Data sent"));
		
	}

}
