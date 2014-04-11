package eu.musesproject.server.connectionmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class IConnectionManagerTest {

	@Mock private IConnectionManager iConnectionManager;
	@Mock private IConnectionCallbacks iCallBacks;
	
	private List<DataHandler> dataHandlerList	 = new CopyOnWriteArrayList<DataHandler>();
	private IConnectionCallbacks iCallBacks2;
	private int counter=1;
	
	@Before
	public void setup() {
		// TBD
	}
	
	@Test
	public void testSendDataIsStoredInTheDataQueue() throws Exception {
		while (counter <=3){
			doAnswer(new Answer<Void>() {
				
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					Object[] arguments = invocation.getArguments();
					String sessionId = (String) arguments[0];
					String data = (String) arguments[1];
					dataHandlerList.add(new DataHandler(sessionId, data));	
					return null;
					
				}
			}).when(iConnectionManager).sendData(anyString(), anyString());
			
			iConnectionManager.sendData("1", "data1tosend");
			
			doAnswer(new Answer<Void>() {
				
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					Object[] arguments = invocation.getArguments();
					String sessionId = (String) arguments[0];
					String data = (String) arguments[1];
					dataHandlerList.add(new DataHandler(sessionId, data));	
					return null;
					
				}
			}).when(iConnectionManager).sendData(anyString(), anyString());
			
			iConnectionManager.sendData("2", "data2tosend");
			
			doAnswer(new Answer<Void>() {
				
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					Object[] arguments = invocation.getArguments();
					String sessionId = (String) arguments[0];
					String data = (String) arguments[1];
					dataHandlerList.add(new DataHandler(sessionId, data));	
					return null;
					
				}
			}).when(iConnectionManager).sendData(anyString(), anyString());
			
			iConnectionManager.sendData("3", "data3tosend");
			counter++;
		}

		verify(iConnectionManager, atMost(10)).sendData(anyString(), anyString());
		assertEquals("data1tosend", dataHandlerList.get(0).getData());
		assertEquals("data2tosend", dataHandlerList.get(1).getData());
		assertEquals("data3tosend", dataHandlerList.get(2).getData());
	}
	
	@Test
	public void testRegisterReceiveCb() throws Exception {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				iCallBacks2 = (IConnectionCallbacks)arguments[0];
				return null;
			}
		}).when(iConnectionManager).registerReceiveCb(iCallBacks);
		iConnectionManager.registerReceiveCb(iCallBacks);
		assertNotNull(iCallBacks2);
	}
	
}
