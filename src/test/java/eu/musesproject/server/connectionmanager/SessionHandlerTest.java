//package eu.musesproject.server.connectionmanager;

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

//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import javax.servlet.ServletRequestEvent;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SessionHandlerTest {
//	@Mock private HttpServletRequest httpServletRequest;
//	@Mock private HttpServletResponse httpServletResponse;
//	@Mock private HttpSession httpSession;
//	@Mock private ServletRequestEvent servletRequestEvent;
//	private SessionHandler sessionHandler;
//	
//	@Before
//	public void setup() {
//		sessionHandler = new SessionHandler();
//	}
//	
//	@Test
//	public void testRequestInitiazed() throws Exception {
//		when(servletRequestEvent.getServletRequest()).thenReturn(httpServletRequest);
//		when(httpServletRequest.getMethod()).thenReturn("POST");
//		when(httpServletRequest.getHeader("poll-interval")).thenReturn("10000");
//		when(httpServletRequest.getSession()).thenReturn(httpSession);
//		sessionHandler.requestInitialized(servletRequestEvent);
//		verify(httpServletRequest).getMethod();
//		verify(httpServletRequest).getHeader("poll-interval");
//		verify(httpServletRequest).getSession();
//	}
//	
//}
