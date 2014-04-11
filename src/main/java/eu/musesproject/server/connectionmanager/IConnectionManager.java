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

import java.util.Set;
import javax.servlet.http.HttpSession;

/**
 * Interface 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public interface IConnectionManager {

	void sendData (String sessionId, String data);
	void registerReceiveCb (IConnectionCallbacks callBacks);
	Set<String> getSessionIds();
	HttpSession getSessionDetails (String sessionId);
	
}