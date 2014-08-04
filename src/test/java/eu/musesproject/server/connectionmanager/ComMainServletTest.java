package eu.musesproject.server.connectionmanager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class ComMainServletTest {

	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;

	@Mock
	private Helper helper;
	@Mock
	private IConnectionCallbacks iConnectionCallbacks;
	@Mock
	private ConnectionManager connectionManager;
	
	private SessionHandler sessionHandler;
	private Cookie cookie1,cookie2, cookie3;
	private ComMainServlet comMainServlet;

	@Before
	public void setup() {
		cookie1 = new Cookie("JSESSIONID", "00000000000001");
		cookie1.setMaxAge(1);
		cookie2 = new Cookie("JSESSIONID", "00000000000002");
		cookie2.setMaxAge(1);
		
		cookie3 = new Cookie("JSESSIONID", "00000000000003");
		cookie3.setMaxAge(1);

		sessionHandler =new SessionHandler();
		comMainServlet = new ComMainServlet(sessionHandler, helper, connectionManager);
	}

	@Test
	public void testdoPostConnect() {
		try {
			when(httpServletRequest.getHeader("connection-type")).thenReturn(
					"connect");
			when(helper.getRequestData(httpServletRequest)).thenReturn("");
			when(helper.setCookie(httpServletRequest)).thenReturn(0);
			when(helper.getCookie()).thenReturn(cookie1);
			comMainServlet.doPost(httpServletRequest, httpServletResponse);
			for (String id: sessionHandler.getSessionIds()){
				if (id.equalsIgnoreCase(cookie1.getValue())) assertTrue(true); // Cookie in the list
				else assertTrue(false);
			}
			assertEquals(1,new SessionHandler().getSessionIds().size());
	
			when(httpServletRequest.getHeader("connection-type")).thenReturn(
					"connect");
			when(helper.getRequestData(httpServletRequest)).thenReturn("");
			when(helper.setCookie(httpServletRequest)).thenReturn(0);
			when(helper.getCookie()).thenReturn(cookie2);
			comMainServlet.doPost(httpServletRequest, httpServletResponse);
			for (String id: sessionHandler.getSessionIds()){
				if (id.equalsIgnoreCase(cookie1.getValue()) || id.equalsIgnoreCase(cookie2.getValue())) assertTrue(true); // Cookie in the list
			}
			assertEquals(2,new SessionHandler().getSessionIds().size());
			
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	
	@Test
	public void testdoPostData() {
		try {
			when(httpServletRequest.getHeader("connection-type")).thenReturn(
					"data");
			when(helper.getRequestData(httpServletRequest)).thenReturn("{\"password\":\"muses\",\"device_id\":\"a67f130d348d0126\",\"username\":\"muses\",\"requesttype\":\"login\"}");
			when(helper.setCookie(httpServletRequest)).thenReturn(0);
			when(helper.getCookie()).thenReturn(cookie1);
			when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueueDataRequest());

//			doAnswer(new Answer<Void>() {
//				
//				@Override
//				public Void answer(InvocationOnMock invocation) throws Throwable {
//					Object[] arguments = invocation.getArguments();
//					String sessionId = (String) arguments[0];
//					String data = (String) arguments[1];
//					ConnectionManager.addDataHandler(new DataHandler(sessionId, data));
//					return null;
//				}
//			}).when(httpServletResponse).addHeader("data", "Some data for client");
			
			comMainServlet.doPost(httpServletRequest, httpServletResponse);
			for (String id: sessionHandler.getSessionIds()){
				if (id.equalsIgnoreCase(cookie1.getValue())) assertTrue(true); break;  // Cookie in the list
			}
			// No need to test it was sent to functional layer
			
			// assert that the data is available in the queue and attach
			assertEquals("{\"auth-message\":\"Successfully authenticated\",\"auth-result\":\"SUCCESS\",\"requesttype\":\"auth-response\"}", comMainServlet.getResponseData());
//			assertEquals("Some JSON for Client ...", comMainServlet.getResponseData());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
//	@Test
//	public void testdoPostDisconnect() throws Exception {
//		
//		when(httpServletRequest.getHeader("connection-type")).thenReturn(
//				"disconnect");
//		when(helper.getRequestData(httpServletRequest)).thenReturn("");
//		when(helper.setCookie(httpServletRequest)).thenReturn(0);
//		when(helper.getCookie()).thenReturn(cookie1);
//		comMainServlet.doPost(httpServletRequest, httpServletResponse);
//		for (String id: sessionHandler.getSessionIds()){
//			if (id.equalsIgnoreCase(cookie1.getValue())) assertTrue(true); // Cookie in the list
//			else assertTrue(false);
//		}
//		assertEquals(1,new SessionHandler().getSessionIds().size());
//	}
	
	private Queue<DataHandler> getFakeQueueDataRequest(){
		Queue<DataHandler> dataHandlerQueue = new LinkedList<DataHandler>();
		dataHandlerQueue.add(new DataHandler("00000000000001", "{\"auth-message\":\"Successfully authenticated\",\"auth-result\":\"SUCCESS\",\"requesttype\":\"auth-response\"}"));
		return dataHandlerQueue;
	}

	
//	@Test
//	public void testWaitForDataIfAvailable() throws Exception {
//		when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueueDataRequest());
//		comMainServlet.waitForDataIfAvailable(5, "00000000000001");
//		assertEquals("Some JSON for Client ...", comMainServlet.getResponseData());
//	}
	
	
//	@Test
//	public void doPostPoll() {
//		try {
//			when(httpServletRequest.getHeader("connection-type")).thenReturn(
//					"poll");	
//			when(helper.getRequestData(httpServletRequest)).thenReturn("Some JSON for server ...");
//			when(helper.setCookie(httpServletRequest)).thenReturn(0);
//			when(helper.getCookie()).thenReturn(cookie1);
//			when(connectionManager.getDataHandlerQueue()).thenReturn(getFakeQueuePollRequest());
//			comMainServlet.doPost(httpServletRequest, httpServletResponse);
//			assertEquals(1,new SessionHandler().getSessionIds().size());
//		} catch (ServletException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	private Queue<DataHandler> getFakeQueuePollRequest(){
		Queue<DataHandler> dataHandlerQueue = new LinkedList<DataHandler>();
		dataHandlerQueue.add(new DataHandler("00000000000001", "Some JSON for Client ..."));
		return dataHandlerQueue;
	}
	
	
	
	
}
