package eu.musesproject.server.authentication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.server.connectionmanager.ConnectionManager;
import eu.musesproject.server.contextdatareceiver.JSONManager;

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
	private List<String> authSessionIdList = new ArrayList<String>();
	
	/**
	 * Creare authentication manager singleton if not created
	 * @return singleton object
	 */
	public static AuthenticationManager getInstance(){
		if (authenticationManagerSingleton == null) {
			authenticationManagerSingleton = new AuthenticationManager();
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

			System.out.println("Login attempt with credentials: " + username
					+ "-" + password + "-" + deviceId);
			// Authentication
			if (username.equals("muses") && (password.equals("muses"))) {// TODO
																			// Authentication
																			// with
																			// database
				logger.log(Level.INFO, "Authentication successful");
				authSessionIdList.add(sessionId);
				// Send authentication response with success message
				response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, "SUCCESS",	"Successfully authenticated");
				logger.log(Level.INFO, response.toString());

			} else {
				logger.log(Level.INFO, "Authentication failed");
				// Send authentication response with failure message
				response = JSONManager.createJSON(JSONIdentifiers.AUTH_RESPONSE, "FAIL", "Incorrect password");
				logger.log(Level.INFO, response.toString());

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	public List<String> getAuthSessionIdList() {
		return authSessionIdList;
	}

	public void setAuthSessionIdList(List<String> authSessionIdList) {
		this.authSessionIdList = authSessionIdList;
	}

	public boolean isAuthenticated(String sessionId) {
		
		return authSessionIdList.contains(sessionId);
	}

}
