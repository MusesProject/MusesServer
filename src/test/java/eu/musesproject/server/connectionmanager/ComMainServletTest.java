//package eu.musesproject.server.connectionmanager;
//
///*
// * #%L
// * MUSES Server
// * %%
// * Copyright (C) 2013 - 2015 Sweden Connectivity
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//
//import static org.junit.Assert.*;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.LinkedList;
//import java.util.Queue;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.mockito.stubbing.Answer;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ComMainServletTest {
//
//	@Mock
//	private HttpServletRequest httpServletRequest;
//	@Mock
//	private HttpServletResponse httpServletResponse;
//
//	@Mock
//	private Helper helper;
//	@Mock
//	private IConnectionCallbacks iConnectionCallbacks;
//	@Mock
//	private ConnectionManager connectionManager;
//
//	@Mock
//	private PrintWriter writer;
//	
//	private SessionHandler sessionHandler;
//	private Cookie cookie1,cookie2, cookie3;
//	private ComMainServlet comMainServlet;
//
//	@Before
//	public void setup() {
//		cookie1 = new Cookie("JSESSIONID", "00000000000001");
//		cookie1.setMaxAge(1);
//		cookie2 = new Cookie("JSESSIONID", "00000000000002");
//		cookie2.setMaxAge(1);
//		
//		cookie3 = new Cookie("JSESSIONID", "00000000000003");
//		cookie3.setMaxAge(1);
//
//		sessionHandler =new SessionHandler();
//		comMainServlet = new ComMainServlet(sessionHandler, helper, connectionManager);
//	}
//
//	@Test
//	public void testdoPostConnect() {
//		try {
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletResponse.getWriter()).thenReturn(writer);
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"connect");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie1);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
////			assertEquals("",comMainServlet.getResponseData()); // the getter has been removed
//			
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"connect");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie2);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie2);
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
////			assertEquals("",comMainServlet.getResponseData()); // the getter has been removed
//			
//		} catch (ServletException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//	
//	@Test
//	public void testdoPostData() {
//		try {
//			// Connect request before testing data
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletResponse.getWriter()).thenReturn(writer);
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"connect");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie1);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//			
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"data");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("{\"test\":\"test\"}");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie1);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//			when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueueDataRequest(1));
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
////			for (String id: sessionHandler.getSessionIds()){
////				if (id.equalsIgnoreCase(cookie1.getValue())) assertTrue(true); break;  // Cookie in the list
////			}
//			// No need to test that it was sent to functional layer
//			
//			// assert that the data is available in the queue and attach
////			assertEquals("{\"auth-message\":\"Successfully authenticated\",\"auth-result\":\"SUCCESS\",\"requesttype\":\"auth-response\"}", comMainServlet.getResponseData()); // the getter has been removed
//			
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"data");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("Some event from client");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie1);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//			when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueueDataRequest(2));
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//			
////			assertNotSame("{\"auth-message\":\"Successfully authenticated\",\"auth-result\":\"SUCCESS\",\"requesttype\":\"auth-response\"}", comMainServlet.getResponseData()); // the getter has been removed
//
//			
//		} catch (ServletException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	
//	private Queue<DataHandler> getFakeQueueDataRequest(int i){
//		Queue<DataHandler> dataHandlerQueue = new LinkedList<DataHandler>();
//		switch(i){
//		case 1:
//			dataHandlerQueue.add(new DataHandler("00000000000001", "{\"auth-message\":\"Successfully authenticated\",\"auth-result\":\"SUCCESS\",\"requesttype\":\"auth-response\"}"));
//			break;
//		case 2:
//			dataHandlerQueue.add(new DataHandler("00000000000001", "Some policy for client from server"));
//			break;
//		}
//		return dataHandlerQueue;
//	}
//	
//	
//	@Test
//	public void doPostPoll() {
//		try {
//			
//			// Client 1 connect
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletResponse.getWriter()).thenReturn(writer);
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"connect");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie1);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//
//			// Client 2 connect
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"connect");
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie2);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie2);
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//			
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"poll");	
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie1);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//			when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueuePollRequest());
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
////			assertEquals("Some JSON for Client 1 ...", comMainServlet.getResponseData()); // the getter has been removed
////			assertEquals(2,new SessionHandler().getSessionIds().size());
//			
//			when(httpServletRequest.getMethod()).thenReturn("POST");
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"poll");	
//			when(helper.getRequestData(httpServletRequest)).thenReturn("");
////			when(helper.setCookie(httpServletRequest)).thenReturn(0);
////			when(helper.getCookie()).thenReturn(cookie2);
//			when(helper.extractCookie(httpServletRequest)).thenReturn(cookie2);
//			when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueuePollRequest());
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
////			assertEquals("Some JSON for Client 2 ...", comMainServlet.getResponseData()); // the getter has been removed
////			assertEquals(2,new SessionHandler().getSessionIds().size());
//			
//		} catch (ServletException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private Queue<DataHandler> getFakeQueuePollRequest(){
//		Queue<DataHandler> dataHandlerQueue = new LinkedList<DataHandler>();
//		dataHandlerQueue.add(new DataHandler("00000000000001", "Some JSON for Client 1 ..."));
//		dataHandlerQueue.add(new DataHandler("00000000000002", "Some JSON for Client 2 ..."));
//		return dataHandlerQueue;
//	}
//	
//	@Test
//	public void testdoPostDisconnect() throws Exception {
//		// Client 1 connect
//		when(httpServletRequest.getMethod()).thenReturn("POST");
//		when(httpServletRequest.getHeader("connection-type")).thenReturn(
//				"connect");
//		when(helper.getRequestData(httpServletRequest)).thenReturn("");
////		when(helper.setCookie(httpServletRequest)).thenReturn(0);
////		when(helper.getCookie()).thenReturn(cookie1);
//		when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//		comMainServlet.doPost(httpServletRequest, httpServletResponse);
//
//		// Client 2 connect
//		when(httpServletRequest.getMethod()).thenReturn("POST");
//		when(httpServletRequest.getHeader("connection-type")).thenReturn(
//				"connect");
//		when(helper.getRequestData(httpServletRequest)).thenReturn("");
////		when(helper.setCookie(httpServletRequest)).thenReturn(0);
////		when(helper.getCookie()).thenReturn(cookie2);
//		when(helper.extractCookie(httpServletRequest)).thenReturn(cookie2);
//		comMainServlet.doPost(httpServletRequest, httpServletResponse);
//		
//		// Disconnect should remove cookies from the list
//		when(httpServletRequest.getMethod()).thenReturn("POST");
//		when(httpServletRequest.getHeader("connection-type")).thenReturn(
//				"disconnect");
//		when(helper.getRequestData(httpServletRequest)).thenReturn("");
////		when(helper.setCookie(httpServletRequest)).thenReturn(0);
////		when(helper.getCookie()).thenReturn(cookie1);
//		when(helper.extractCookie(httpServletRequest)).thenReturn(cookie1);
//		comMainServlet.doPost(httpServletRequest, httpServletResponse);
//		boolean found=false;
////		for (String id: sessionHandler.getSessionIds()){
////			if (id.equalsIgnoreCase(cookie1.getValue())) found=true; // Cookie in the list
////			else found=false;
////		}
////		assertEquals(false, found);
////		assertEquals(1,new SessionHandler().getSessionIds().size());
//		
//		when(httpServletRequest.getMethod()).thenReturn("POST");
//		when(httpServletRequest.getHeader("connection-type")).thenReturn(
//				"disconnect");
//		when(helper.getRequestData(httpServletRequest)).thenReturn("");
////		when(helper.setCookie(httpServletRequest)).thenReturn(0);
////		when(helper.getCookie()).thenReturn(cookie2);
//		when(helper.extractCookie(httpServletRequest)).thenReturn(cookie2);
//		comMainServlet.doPost(httpServletRequest, httpServletResponse);
//		found=false;
////		for (String id: sessionHandler.getSessionIds()){
////			if (id.equalsIgnoreCase(cookie2.getValue())) found=true; // Cookie in the list
////			else found=false;
////		}
////		assertEquals(false, found);
////		assertEquals(0,new SessionHandler().getSessionIds().size());
//	}
//	
//
//	
////	@Test
////	public void testWaitForDataIfAvailable() throws Exception {
////		when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueueDataRequest());
////		comMainServlet.waitForDataIfAvailable(5, "00000000000001");
////		assertEquals("Some JSON for Client ...", comMainServlet.getResponseData());
////	}
//	
//}
