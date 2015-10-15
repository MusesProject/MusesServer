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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import eu.musesproject.server.continuousrealtimeeventprocessor.EventProcessor;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.ConnectionConfig;
import eu.musesproject.server.entity.DefaultPolicies;
import eu.musesproject.server.entity.MusesConfig;
import eu.musesproject.server.entity.SensorConfiguration;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.entity.Zone;
import eu.musesproject.server.eventprocessor.correlator.engine.DroolsEngineService;
import eu.musesproject.server.eventprocessor.correlator.model.CepFact;
import eu.musesproject.server.eventprocessor.correlator.model.owl.ConfigSyncEvent;
import eu.musesproject.server.eventprocessor.impl.EventProcessorImpl;
import eu.musesproject.server.eventprocessor.impl.MusesCorrelationEngineImpl;
import eu.musesproject.server.eventprocessor.util.EventTypes;
import eu.musesproject.server.scheduler.ModuleType;


public class ConnectionCallbacksImpl implements IConnectionCallbacks {
	
	private static final String MUSES_TAG = "MUSES_TAG";
	private Logger logger = Logger.getLogger(ConnectionCallbacksImpl.class.getName());
	private ConnectionManager connManager;
	private static ConnectionCallbacksImpl ConnectionCallbacksImplSingleton = null;
	//boolean isDataAvailable = false; // not used
	//public static volatile String lastSessionId = null; // several threads will assign to it. one shared variable for several clients to store their sessionid!

	//private static volatile String data = ""; // became local
	//public static volatile String receiveData; // became local
	private static DBManager dbManager = new DBManager(ModuleType.EP);
	private static ExecutorService executor = Executors.newFixedThreadPool(100); 
	

	private ConnectionCallbacksImpl(){
		connManager = ConnectionManager.getInstance();
		connManager.registerReceiveCb(this);
		startConnection();
	}
	
	// we don't want to register a new callback everytime tomcat creates a new instance of the ComMainServlet (or everytime a person refreshes the /server )
	public static ConnectionCallbacksImpl getInstance(){
		if (ConnectionCallbacksImplSingleton == null) {
			synchronized(ConnectionCallbacksImpl.class){
				if (ConnectionCallbacksImplSingleton == null) {
					ConnectionCallbacksImplSingleton = new ConnectionCallbacksImpl();
				}
			}
		}
		return ConnectionCallbacksImplSingleton;
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

		//ConnectionCallbacksImpl.lastSessionId = sessionId;
		//ConnectionCallbacksImpl.receiveData = rData;
		
		// became local variable from being class variable for thread safety
		String data = "";
		String receiveData = rData;		
		
		
		logger.log(Level.INFO, MUSES_TAG + "  Info SS, received callback from CM with data:"+rData);
		
		try {
			// First, get the request type, in order to differentiate between login
			// and data exchange
			//root = new JSONObject(ConnectionCallbacksImpl.receiveData);
			root = new JSONObject(receiveData);
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
					username = root
							.getString(JSONIdentifiers.AUTH_USERNAME);
					deviceId = root
							.getString(JSONIdentifiers.AUTH_DEVICE_ID);
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
						
						//Retrieve default policies
						String defaultPoliciesXML = null;
						username = root
								.getString(JSONIdentifiers.AUTH_USERNAME);
						Users user = dbManager.getUserByUsername(username);
						if (user != null) {

							String language = user.getLanguage();
							List<DefaultPolicies> defaultPolicies = dbManager
									.getDefaultPolicies(language);

							if ((defaultPolicies != null)
									&& (defaultPolicies.size() > 0)) {
								
								defaultPoliciesXML = JSONManager
										.createDefaultPoliciesJSON(defaultPolicies);
								logger.log(Level.INFO,
										defaultPoliciesXML.toString());
								//connManager.sendData(sessionId, defaultPoliciesJSON.toString());
							}
						}
						
						

						JSONObject response = JSONManager
								.createConfigUpdateJSON(
										RequestType.CONFIG_UPDATE, config,
										sensorConfig, connectionConfig,
										zoneConfig, defaultPoliciesXML);
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
						// According to issue #11, we should not send data when not logged in, so this line is commented:connManager.sendData(sessionId, response.toString());
					}
				}else if ((os != null) && (os.contains(eu.musesproject.server.eventprocessor.util.Constants.OS_WINDOWS))) {
					//TODO Windows config-sync to be done
					logger.log(Level.INFO, "Windows config-sync...");
					
					String response = "";
					logger.log(Level.INFO, MUSES_TAG + " Response to send:"
							+ response);
					logger.log(Level.INFO, MUSES_TAG + " sessionID: "
							+ sessionId);
					connManager.sendData(sessionId, response);
					
					//Insert config sync
					
					//eu.musesproject.server.eventprocessor.correlator.model.owl.Event formattedEvent = null;
					//CepFact formattedEvent = null;
							
					ConfigSyncEvent csEvent= new ConfigSyncEvent();
					
					csEvent.setOs(os);
					csEvent.setSessionId(sessionId);
					
					//formattedEvent = (eu.musesproject.server.eventprocessor.correlator.model.owl.Event)csEvent;
					//formattedEvent = csEvent;
					
					EventProcessor processor = null;
					MusesCorrelationEngineImpl engine = null;
					DroolsEngineService des = EventProcessorImpl.getMusesEngineService();
				
					if (des==null){
						processor = new EventProcessorImpl();
						engine = (MusesCorrelationEngineImpl)processor.startTemporalCorrelation("drl");
						des = EventProcessorImpl.getMusesEngineService();
					}else{
						logger.info("DroolsEngine Service already available");
					}
					if (csEvent != null){
						csEvent.setSessionId(sessionId);
						csEvent.setUsername(username);
						csEvent.setDeviceId(deviceId);
						//if (requestId != 0){
						csEvent.setHashId(requestId);
						//}
						logger.info("Inserting event into the WM:"+csEvent);
						try{
							des.insertFact(csEvent);
						}catch(NullPointerException e){
							logger.info("formatter Event not inserted due to NullPointerException");
						}
					}
					
				}
			}else {
				//Data exchange: We should check if sessionId is correctly authenticated
				if (AuthenticationManager.getInstance().isAuthenticated(sessionId)) {

					//List<ContextEvent> list = JSONManager
					//		.processJSONMessage(ConnectionCallbacksImpl.receiveData, null, sessionId);
					List<ContextEvent> list = JSONManager
							.processJSONMessage(receiveData, null, sessionId);
					logger.log(Level.INFO, "Starting ProcessThread...");
					
					
					try {
						username = root
								.getString(JSONIdentifiers.AUTH_USERNAME);
						deviceId = root
								.getString(JSONIdentifiers.AUTH_DEVICE_ID);
						if (requestType.equals(RequestType.ONLINE_DECISION)){
							requestId = root.getInt(JSONIdentifiers.REQUEST_IDENTIFIER);
							logger.log(Level.INFO, "requestId"+requestId);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						logger.log(Level.INFO, "JSON identifier not found:"+e.getMessage());
						logger.log(Level.INFO, "Original JSON message:" + receiveData);
					}
					
					// Thread t = new Thread(new ProcessThread(list, sessionId, username, deviceId, requestId));
					// t.start(); 
					// the executor is a pool of threads/workers. runnables are work units that we
					// assign to the workers to execute. this way the number of threads are fixed and under 
					// control. this is important because cpu time is a limited resource. if the running machine
					// has more capacity then increase the pool size of executor above in class variable section.
					Runnable runnable = new ProcessThread(list, sessionId, username, deviceId, requestId);
					executor.execute(runnable);

					logger.log(Level.INFO,
							"Resuming receiveCb after calling ProcessThread...");

					logger.info("*************Receive Callback called: "
							//+ ConnectionCallbacksImpl.receiveData
							+ receiveData
							+ "from client ID " + sessionId);
					if (data != null) {
						//connManager.sendData(sessionId,
						//		ConnectionCallbacksImpl.data);
						connManager.sendData(sessionId, data);
					}

				} else {//Current sessionId has not been authenticated
					// According to issue #11, we should not send data when not logged in, so this section is commented:
					logger.log(Level.INFO, "Authentication has failed, any data coming after this authentication failed, should not be replied. According to issue #11, we don't reply with any auth-message ");
				}
			}
		} catch (JSONException je) {
			logger.log(Level.ERROR, MUSES_TAG+ je.getMessage() + je.getCause());
			je.printStackTrace();
		}

		//return ConnectionCallbacksImpl.data;
		return data;
	}
	@Override
	public void sessionCb(String sessionId, int status) {
		logger.info("*************Session Callback called Id: " + sessionId + " Status: " + ((status == Statuses.DISCONNECTED) ? "Disconnected" : "Data sent"));
		
	}

	public static ExecutorService getExecutor() {
		return executor;
	}
}
