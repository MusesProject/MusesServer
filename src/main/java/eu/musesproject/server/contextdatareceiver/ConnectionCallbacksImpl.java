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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.connectionmanager.ConnectionManager;
import eu.musesproject.server.connectionmanager.IConnectionCallbacks;
import eu.musesproject.server.connectionmanager.Statuses;


public class ConnectionCallbacksImpl implements IConnectionCallbacks {
	
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
	private void startConnection() {
				
		logger.info("Start Server Connection");
 
	}
	

	@Override
	public String receiveCb(String sessionId, String rData) {
		ConnectionCallbacksImpl.lastSessionId = sessionId;
		ConnectionCallbacksImpl.receiveData = rData;
		List<ContextEvent> list = JSONManager.processJSONMessage(ConnectionCallbacksImpl.receiveData);
		UserContextEventDataReceiver.getInstance().processContextEventList(list);
		
		logger.info("*************Receive Callback called: "
				+ ConnectionCallbacksImpl.receiveData + "from client ID " + sessionId);
		if (isDataAvailable) {
			connManager.sendData(sessionId, ConnectionCallbacksImpl.data);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ConnectionCallbacksImpl.receiveData;
	}
	@Override
	public void sessionCb(String sessionId, int status) {
		logger.info("*************Session Callback called Id: " + sessionId + " Status: " + ((status == Statuses.DISCONNECTED) ? "Disconnected" : "Data sent"));
		
	}

}
