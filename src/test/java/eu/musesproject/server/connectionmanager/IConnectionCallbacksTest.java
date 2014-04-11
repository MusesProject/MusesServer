package eu.musesproject.server.connectionmanager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
public class IConnectionCallbacksTest {
	
	@Mock private HttpServletRequest httpServletRequest;
	@Mock private HttpServletResponse httpServletResponse;
	@Mock private ConnectionManager connectionManager;
	@Mock private Helper helper;
	@Mock private SessionHandler sessionHandler;
	@Mock private StubConnectionManager stubManager;
	
	private IConnectionCallbacks iCallBacks;
	private List<DataHandler> dataHandlerList = new CopyOnWriteArrayList<DataHandler>();
	private ComMainServlet comMainServlet;
	private Cookie cookie1, cookie2, cookie3;
	private int counter=1;
	
	@Before
	public void setup(){
		iCallBacks = new StubConnectionManager();
		connectionManager.registerReceiveCb(iCallBacks);
		comMainServlet = new ComMainServlet(sessionHandler,helper,
				connectionManager);
		// Making fake data objects to send
		dataHandlerList.add(new DataHandler("1", "data1tosend"));
		dataHandlerList.add(new DataHandler("2", "data2tosend"));
		dataHandlerList.add(new DataHandler("3", "data3tosend"));
		// Fake cookie for each request
		cookie1 =  new Cookie("JSESSIONID", "1");
		cookie2 =  new Cookie("JSESSIONID", "2");
		cookie3 =  new Cookie("JSESSIONID", "3");

	}
	
	@Test
	public void testSessionCB() throws Exception{
		System.out.println("********* Running Test 'testSessionCB' **********");
		while(counter<=3){
			// Poll request
			when(httpServletRequest.getHeader("connection-type")).thenReturn("poll");
			when(helper.getCookie()).thenAnswer(new Answer<Cookie>() {
				
				@Override
				public Cookie answer(InvocationOnMock invocation) throws Throwable {
					switch(counter){
					case 1:
						return cookie1;
					case 2:
						return cookie2;
					case 3:
						return cookie3;
					default:
						return cookie1;
					}
				}
			});
			when(helper.getRequestData(httpServletRequest)).thenReturn("");
			when(connectionManager.getDataHandlerList()).thenAnswer(new Answer<List<DataHandler>>() {
				
				@Override
				public List<DataHandler> answer(InvocationOnMock invocation)
						throws Throwable {
					return dataHandlerList;
				}
			});
			
			comMainServlet.doPost(httpServletRequest, httpServletResponse);
			
			// Some assertions here
			
			// Ack request
			when(httpServletRequest.getHeader("connection-type")).thenReturn("ack");
			when(helper.getCookie()).thenAnswer(new Answer<Cookie>() {
				
				@Override
				public Cookie answer(InvocationOnMock invocation) throws Throwable {
					switch(counter){
					case 1:
						return cookie1;
					case 2:
						return cookie2;
					case 3:
						return cookie3;
					default:
						return cookie1;
					}
				}
			});
			when(helper.getRequestData(httpServletRequest)).thenReturn("");
			when(connectionManager.getDataHandlerObject(anyString())).thenAnswer(new Answer<DataHandler>() {
				
				@Override
				public DataHandler answer(InvocationOnMock invocation)
						throws Throwable {
					Object[] arguments = invocation.getArguments();
					String currentSessionId = (String) arguments[0];
					for (DataHandler d : dataHandlerList){
						if (d.getSessionId().equalsIgnoreCase(currentSessionId)){
							return d;
						}
					}
					return null;
				}
			});
			
			comMainServlet.doPost(httpServletRequest, httpServletResponse);
			
			// Verifying method invocations
			verify(httpServletRequest, atMost(10)).getHeader("connection-type");
			verify(helper, atMost(10)).getCookie();
			verify(helper, atMost(10)).getRequestData(httpServletRequest);
			
			counter++;
		} // End while
		System.out.println("********* 'testSessionCB' finished **********");
	}
	
	@Test
	public void testReceiveCbIsCalledWhenDataIsSentFromTheClient() throws Exception {
		System.out.println("********* Running Test 'testReceiveCB' **********");
		counter=1;
		while(counter<=3){
			when(httpServletRequest.getHeader("connection-type")).thenReturn("data");
			when(helper.getCookie()).thenAnswer(new Answer<Cookie>() {
				
				@Override
				public Cookie answer(InvocationOnMock invocation) throws Throwable {
					switch(counter){
					case 1:
						return cookie1;
					case 2:
						return cookie2;
					case 3:
						return cookie3;
					default:
						return cookie1;
					}
				}
			});
			when(helper.getRequestData(httpServletRequest)).thenReturn("Sample data from client " + counter);
			comMainServlet.doPost(httpServletRequest, httpServletResponse);
			//assertEquals(StubConnectionManager.receiveData, "Sample data from client " + counter);
			counter++;
		}
		// Verifying method invocations
		verify(httpServletRequest, atMost(10)).getHeader("connection-type");
		verify(helper, atMost(10)).getCookie();
		verify(helper, atMost(10)).getRequestData(httpServletRequest);
		
		System.out.println("********* 'testReceiveCB' finished **********");

	}
}
