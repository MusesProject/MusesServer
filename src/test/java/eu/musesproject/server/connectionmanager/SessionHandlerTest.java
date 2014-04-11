package eu.musesproject.server.connectionmanager;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SessionHandlerTest {
	@Mock private HttpServletRequest httpServletRequest;
	@Mock private HttpServletResponse httpServletResponse;
	@Mock private HttpSession httpSession;
	@Mock private ServletRequestEvent servletRequestEvent;
	private SessionHandler sessionHandler;
	
	@Before
	public void setup() {
		sessionHandler = new SessionHandler();
	}
	
	@Test
	public void testRequestInitiazed() throws Exception {
		when(servletRequestEvent.getServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getMethod()).thenReturn("POST");
		when(httpServletRequest.getHeader("poll-interval")).thenReturn("10000");
		when(httpServletRequest.getSession()).thenReturn(httpSession);
		sessionHandler.requestInitialized(servletRequestEvent);
		verify(httpServletRequest).getMethod();
		verify(httpServletRequest).getHeader("poll-interval");
		verify(httpServletRequest).getSession();
	}
	
}
