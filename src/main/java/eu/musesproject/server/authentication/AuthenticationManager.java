package eu.musesproject.server.authentication;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.server.contextdatareceiver.JSONManager;
import eu.musesproject.server.db.handler.DBManager;
import eu.musesproject.server.entity.Users;
import eu.musesproject.server.eventprocessor.util.Constants;
import eu.musesproject.server.scheduler.ModuleType;

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

public class AuthenticationManager {
private static AuthenticationManager authenticationManagerSingleton = null;
	public Logger logger = Logger.getLogger(AuthenticationManager.class.getName());
	//private List<String> authSessionIdList = new ArrayList<String>(); // several threads will add and remove items from list. the list must be thread safe. 
	// the best solution is to let tomcat handle the authentication entirely by having security constraints defined. Even if you want to do it manually then 
	// you have to clean up all the sessions that have timed out in order to not get memory leaks. Beside that a set is more appropriate in this case instead 
	// of a list. you do not want duplicates of the same session id stored several times. 
	private Set<String> authSessionIdList = Collections.synchronizedSet(new HashSet<String>());
	private static DBManager dbManager = new DBManager(ModuleType.EP);
	
	/**
	 * Creare authentication manager singleton if not created
	 * @return singleton object
	 */
	public static AuthenticationManager getInstance(){
		// this is the old non thread-safe code. assume the singleton has not yet been assigned and then two threads at the same time check to see if the 
		// singleton is null. both will create their own instance which will defeat the purpose of a singleton. the correct thread-safe version 
		// is available below.
//		if (authenticationManagerSingleton == null) {
//			authenticationManagerSingleton = new AuthenticationManager();
//		}
//		return authenticationManagerSingleton;
		
		if (authenticationManagerSingleton == null) {
			synchronized(AuthenticationManager.class) {
				if (authenticationManagerSingleton == null) {
					authenticationManagerSingleton = new AuthenticationManager();
				}
			}
		}
		return authenticationManagerSingleton;
	}
	
	public JSONObject authenticate(JSONObject root, String sessionId){
		String username = null;
		String password = null;
		String deviceId = null;
		JSONObject response = null;
		try {
			// retrieveCredentials
			username = root.getString(JSONIdentifiers.AUTH_USERNAME);
			password = root.getString(JSONIdentifiers.AUTH_PASSWORD);
			deviceId = root.getString(JSONIdentifiers.AUTH_DEVICE_ID);

			logger.log(Level.INFO,"Login attempt with credentials: " + username
					+ "-" + password + "-" + deviceId);
			// Authentication
			// with
			// database
			
			Users user = dbManager.getUserByUsername(username);
			
			if ((user != null)&&(user.getPassword().equalsIgnoreCase(password)&&(user.getEnabled()==1))){
			
																			
				logger.log(Level.INFO, "Authentication successful");
				authSessionIdList.add(sessionId);
				// Send authentication response with success message
				response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, Constants.SUCCESS,	"Successfully authenticated");
				logger.log(Level.INFO, response.toString());

			} else if (user == null) {
				logger.log(Level.INFO, "Authentication failed: user not found");
				// Send authentication response with failure message
				response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, Constants.FAIL, "User not found");
				logger.log(Level.INFO, response.toString());

			} else if (user.getEnabled()!=1) {
				logger.log(Level.INFO, "Authentication failed: user not enabled");
				// Send authentication response with failure message
				response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, Constants.FAIL, "User not enabled");
				logger.log(Level.INFO, response.toString());

			}else {
				logger.log(Level.INFO, "Authentication failed");
				// Send authentication response with failure message
				response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, Constants.FAIL, "Incorrect password");
				logger.log(Level.INFO, response.toString());

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public JSONObject logout(JSONObject root, String sessionId){
		String username = null;
		String deviceId = null;
		JSONObject response = null;
		try {
			// retrieveCredentials
			username = root.getString(JSONIdentifiers.AUTH_USERNAME);
			deviceId = root.getString(JSONIdentifiers.AUTH_DEVICE_ID);

			System.out.println("Logout attempt : " + username
					+ "-" + deviceId);
			// Authentication
			if (isAuthenticated(sessionId)){
				logger.log(Level.INFO, "Logout successful");
				authSessionIdList.remove(sessionId);
				// Send authentication response with success message
				response = JSONManager.createJSON(JSONIdentifiers.LOGOUT_RESPONSE, Constants.SUCCESS,	"Successfully logged out");
				logger.log(Level.INFO, response.toString());

			} else {
				logger.log(Level.INFO, "Logout failed");
				// Send authentication response with failure message
				response = JSONManager.createJSON(JSONIdentifiers.LOGOUT_RESPONSE, Constants.FAIL, "No session found");
				logger.log(Level.INFO, response.toString());

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return response;
	}

//	public List<String> getAuthSessionIdList() {
//	return authSessionIdList;
//}


// we can not allow any external call replace the implementation. what if they replace it with a non thread-safe variant
//public void setAuthSessionIdList(List<String> authSessionIdList) {
//	this.authSessionIdList = authSessionIdList;
//}

	public boolean isAuthenticated(String sessionId) {
		return authSessionIdList.contains(sessionId);
	}

}
