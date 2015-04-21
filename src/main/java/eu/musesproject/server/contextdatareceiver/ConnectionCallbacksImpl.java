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
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.ConnectionConfig;
import eu.musesproject.server.entity.MusesConfig;
import eu.musesproject.server.entity.SensorConfiguration;
import eu.musesproject.server.entity.Zone;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.scheduler.ModuleType;


public class ConnectionCallbacksImpl implements IConnectionCallbacks {
	
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(ConnectionCallbacksImpl.class.getName());
	private ConnectionManager connManager;
	boolean isDataAvailable = false;
	public static volatile String lastSessionId = null;
	private static volatile String data = "";
	public static volatile String receiveData;
	private static DBManager dbManager = new DBManager(ModuleType.EP);

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
		String os = null;
		String osVersion = null;
		int requestId = 0;

		ConnectionCallbacksImpl.lastSessionId = sessionId;
		ConnectionCallbacksImpl.receiveData = rData;
		
		logger.log(Level.INFO, MUSES_TAG + "  Info SS, received callback from CM with data:"+rData);
		
		try {
			// First, get the request type, in order to differentiate between login
			// and data exchange
			root = new JSONObject(ConnectionCallbacksImpl.receiveData);
			requestType = root.getString(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER);

			if (requestType.equals(RequestType.LOGIN)) {
				logger.log(Level.INFO, "Login request");
				// Delegate authentication to AuthenticationManager
				JSONObject authResponse = AuthenticationManager.getInstance().authenticate(root, sessionId);
				logger.log(Level.INFO, MUSES_TAG + " Info SS, Login request=> authentication response is:"+authResponse.toString());
				if (authResponse != null) {
					try {
						username = root
								.getString(JSONIdentifiers.AUTH_USERNAME);
						deviceId = root
								.getString(JSONIdentifiers.AUTH_DEVICE_ID);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logger.log(Level.INFO, "JSON identifier not found:"+e.getMessage());
					}
					UserContextEventDataReceiver.storeEvent(EventTypes.LOG_IN, username, "musesawaew", deviceId, "Geneva", authResponse.toString());
					return authResponse.toString();
				}
			}else if (requestType.equals(RequestType.LOGOUT)) {
				logger.log(Level.INFO, "Logout request");
				JSONObject authResponse = AuthenticationManager.getInstance().logout(root, sessionId);
				logger.log(Level.INFO, MUSES_TAG + "Info SS, Logout request");
				try {
						username = root
								.getString(JSONIdentifiers.AUTH_USERNAME);
						deviceId = root
								.getString(JSONIdentifiers.AUTH_DEVICE_ID);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logger.log(Level.INFO, "JSON identifier not found:"+e.getMessage());
					}
					UserContextEventDataReceiver.storeEvent(EventTypes.LOG_OUT, username, "musesawaew", deviceId, "Geneva", authResponse.toString());
					return authResponse.toString();
				
			}else if (requestType.equals(RequestType.CONFIG_SYNC)) {
				try{
					os = root.getString(JSONIdentifiers.OPERATING_SYSTEM);
					//osVersion = root.getString(JSONIdentifiers.OPERATING_SYSTEM_VERSION);
				} catch (JSONException je) {
					logger.log(Level.ERROR, MUSES_TAG+ je.getMessage() + je.getCause());					
				}
				logger.log(Level.INFO, MUSES_TAG + " Sending config sync for operating system:" + os);
				if ((os == null) || (os.contains(eu.musesproject.server.eventprocessor.util.Constants.OS_ANDROID))) {//Android by default, if no operating system specified					
					if (AuthenticationManager.getInstance().isAuthenticated(
							sessionId)) {
						logger.log(Level.INFO, MUSES_TAG
								+ "Config sync requested");
						// MUSES Configuration
						MusesConfig config = dbManager.getMusesConfig();
						logger.log(
								Level.INFO,
								MUSES_TAG + config.getConfigName()
										+ " silent mode:"
										+ config.getSilentMode());
						// Connection Configuration
						ConnectionConfig connectionConfig = dbManager
								.getConnectionConfig();
						logger.log(Level.INFO,
								MUSES_TAG + " Connection config id:"
										+ connectionConfig.getConfigId());
						// Sensor Configuration
						List<SensorConfiguration> sensorConfig = dbManager
								.getSensorConfiguration();

						// Retrieve zone info
						List<Zone> zoneConfig = dbManager.getZones();
						logger.log(Level.INFO, MUSES_TAG
								+ " Zones information for " + zoneConfig.size()
								+ " zones.");

						JSONObject response = JSONManager
								.createConfigUpdateJSON(
										RequestType.CONFIG_UPDATE, config,
										sensorConfig, connectionConfig,
										zoneConfig);
						logger.log(Level.INFO, response.toString());
						logger.log(Level.INFO, MUSES_TAG + " Response to send:"
								+ response.toString());
						logger.log(Level.INFO, MUSES_TAG + " sessionID: "
								+ sessionId);
						connManager.sendData(sessionId, response.toString());
					} else {// Current sessionId has not been authenticated
						JSONObject response = JSONManager
								.createJSON(JSONIdentifiers.AUTH_RESPONSE,
										"FAIL",
										"Data cannot be processed: Failed authentication");
						logger.log(Level.INFO, response.toString());
						connManager.sendData(sessionId, response.toString());
					}
				}else if ((os != null) && (os.contains(eu.musesproject.server.eventprocessor.util.Constants.OS_WINDOWS))) {
					//TODO Windows config-sync to be done
					logger.log(Level.INFO, "Windows config-sync to be done");
				}
			}else {
				//Data exchange: We should check if sessionId is correctly authenticated
				if (AuthenticationManager.getInstance().isAuthenticated(sessionId)) {

					List<ContextEvent> list = JSONManager
							.processJSONMessage(ConnectionCallbacksImpl.receiveData, null, sessionId);
					logger.log(Level.INFO, "Starting ProcessThread...");
					
					
					try {
						username = root
								.getString(JSONIdentifiers.AUTH_USERNAME);
						deviceId = root
								.getString(JSONIdentifiers.AUTH_DEVICE_ID);
						//if (requestType.equals(RequestType.ONLINE_DECISION)){
							requestId = root.getInt(JSONIdentifiers.REQUEST_IDENTIFIER);
							logger.log(Level.INFO, "requestId"+requestId);
						//}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logger.log(Level.INFO, "JSON identifier not found:"+e.getMessage());
						logger.log(Level.INFO, "Original JSON message:" + receiveData);
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
			logger.log(Level.ERROR, MUSES_TAG+ je.getMessage() + je.getCause());
			je.printStackTrace();
		}

		return ConnectionCallbacksImpl.data;
	}
	@Override
	public void sessionCb(String sessionId, int status) {
		logger.info("*************Session Callback called Id: " + sessionId + " Status: " + ((status == Statuses.DISCONNECTED) ? "Disconnected" : "Data sent"));
		
	}

}
