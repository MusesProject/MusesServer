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
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.atMost;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import java.util.LinkedList;
//import java.util.Queue;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.mockito.stubbing.Answer;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class IConnectionCallbacksTest {
//	
//	@Mock private HttpServletRequest httpServletRequest;
//	@Mock private HttpServletResponse httpServletResponse;
//	@Mock private ConnectionManager connectionManager;
//	@Mock private Helper helper;
//	@Mock private SessionHandler sessionHandler;
//	@Mock private StubConnectionManager stubManager;
//	
//	private IConnectionCallbacks iCallBacks;
//	private Queue<DataHandler> dataHandlerQueue = new LinkedList<DataHandler>();
//	private ComMainServlet comMainServlet;
//	private Cookie cookie1, cookie2, cookie3;
//	private int counter=1;
//	
//	@Before
//	public void setup(){
//		iCallBacks = new StubConnectionManager();
//		connectionManager.registerReceiveCb(iCallBacks);
//		comMainServlet = new ComMainServlet(sessionHandler,helper,
//				connectionManager);
//		// Making fake data objects to send
//		dataHandlerQueue.add(new DataHandler("1", "data1tosend"));
//		dataHandlerQueue.add(new DataHandler("2", "data2tosend"));
//		dataHandlerQueue.add(new DataHandler("3", "data3tosend"));
//		// Fake cookie for each request
//		cookie1 =  new Cookie("JSESSIONID", "1");
//		cookie2 =  new Cookie("JSESSIONID", "2");
//		cookie3 =  new Cookie("JSESSIONID", "3");
//
//	}
//	
//	@Test
//	public void testSessionCB() throws Exception{
//		System.out.println("********* Running Test 'testSessionCB' **********");
//		while(counter<=3){
//			// Poll request
//			when(httpServletRequest.getHeader("connection-type")).thenReturn("poll");
//			when(helper.getCookie()).thenAnswer(new Answer<Cookie>() {
//				
//				@Override
//				public Cookie answer(InvocationOnMock invocation) throws Throwable {
//					switch(counter){
//					case 1:
//						return cookie1;
//					case 2:
//						return cookie2;
//					case 3:
//						return cookie3;
//					default:
//						return cookie1;
//					}
//				}
//			});
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
//			when(connectionManager.getDataHandlerQueue()).thenAnswer(new Answer<Queue<DataHandler>>() {
//				
//				@Override
//				public Queue<DataHandler> answer(InvocationOnMock invocation)
//						throws Throwable {
//					return dataHandlerQueue;
//				}
//			});
//			
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//			
//			// Some assertions here
//			
//			// Ack request
//			when(httpServletRequest.getHeader("connection-type")).thenReturn("ack");
//			when(helper.getCookie()).thenAnswer(new Answer<Cookie>() {
//				
//				@Override
//				public Cookie answer(InvocationOnMock invocation) throws Throwable {
//					switch(counter){
//					case 1:
//						return cookie1;
//					case 2:
//						return cookie2;
//					case 3:
//						return cookie3;
//					default:
//						return cookie1;
//					}
//				}
//			});
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
//			when(connectionManager.getDataHandlerObject(anyString())).thenAnswer(new Answer<DataHandler>() {
//				
//				@Override
//				public DataHandler answer(InvocationOnMock invocation)
//						throws Throwable {
//					Object[] arguments = invocation.getArguments();
//					String currentSessionId = (String) arguments[0];
//					for (DataHandler d : dataHandlerQueue){
//						if (d.getSessionId().equalsIgnoreCase(currentSessionId)){
//							return d;
//						}
//					}
//					return null;
//				}
//			});
//			
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//			
//			// Verifying method invocations
//			verify(httpServletRequest, atMost(10)).getHeader("connection-type");
//			verify(helper, atMost(10)).getCookie();
//			verify(helper, atMost(10)).getRequestData(httpServletRequest);
//			
//			counter++;
//		} // End while
//		System.out.println("********* 'testSessionCB' finished **********");
//	}
//	
//	@Test
//	public void testReceiveCbIsCalledWhenDataIsSentFromTheClient() throws Exception {
//		System.out.println("********* Running Test 'testReceiveCB' **********");
//		counter=1;
//		while(counter<=3){
//			when(httpServletRequest.getHeader("connection-type")).thenReturn("data");
//			when(helper.getCookie()).thenAnswer(new Answer<Cookie>() {
//				
//				@Override
//				public Cookie answer(InvocationOnMock invocation) throws Throwable {
//					switch(counter){
//					case 1:
//						return cookie1;
//					case 2:
//						return cookie2;
//					case 3:
//						return cookie3;
//					default:
//						return cookie1;
//					}
//				}
//			});
//			when(helper.getRequestData(httpServletRequest)).thenReturn("Sample data from client " + counter);
//			// comMainServlet.doPost(httpServletRequest, httpServletResponse); FIXME commented for time being
//			//assertEquals(StubConnectionManager.receiveData, "Sample data from client " + counter);
//			counter++;
//		}
//		// Verifying method invocations
//		verify(httpServletRequest, atMost(10)).getHeader("connection-type");
//		verify(helper, atMost(10)).getCookie();
//		verify(helper, atMost(10)).getRequestData(httpServletRequest);
//		
//		System.out.println("********* 'testReceiveCB' finished **********");
//
//	}
//}
