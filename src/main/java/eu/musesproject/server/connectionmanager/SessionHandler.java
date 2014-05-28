/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class handles all http session, called every time a 
 * request come from the client for both POST/GET. 
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class SessionHandler implements ServletContextListener , HttpSessionListener, ServletRequestListener{
	
	private Set<String> sessionIDs = new HashSet<String>();
	public Map<Date,Cookie> cookieSet = new ConcurrentHashMap<Date,Cookie>();
	private static final String ATTRIBUTE_NAME = "com.swedenconnectivity.comserver.SessionHandler";
	private static final boolean D = true;
	private Logger logger = Logger.getLogger(SessionHandler.class.getName());

	
	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		
	}
	
	/**
	 * This methods check for request method POST/GET, retrieve and set
	 * poll-interval value from the header 
	 * @param ServletRequestEvent
	 * @return void
	 */
	
	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		logger = Logger.getRootLogger();
		BasicConfigurator.configure();
		logger.setLevel(Level.INFO);
		
		int interval=0;
		HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
		if (request.getMethod().equalsIgnoreCase("POST")){
			String pollIntervalTimeout = request.getHeader("poll-interval");
			HttpSession session = request.getSession();
			if (pollIntervalTimeout != null) {
				interval = Integer.parseInt(pollIntervalTimeout) * 2;
				session.setMaxInactiveInterval(interval);
			}
		}
		
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		sce.getServletContext().setAttribute(ATTRIBUTE_NAME, this);
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		removeExpiredCookies();
	}

	/** 
	 * Called every time a session is destroyed, if the session
	 * is in the table remove it from the list and call the session callback method
	 * to inform other users
	 * @param HttpSessionEvent event
	 * @return void
	 */
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		removeExpiredCookies();
	}

	/**
	 * Get session handler singleTon object
	 * @param context
	 * @return
	 */
	public static SessionHandler getInstance(ServletContext context) {
	    return (SessionHandler) context.getAttribute(ATTRIBUTE_NAME);
	}
	
	/**
	 * Get session id list
	 * @return sessionIDs
	 */
	public Set<String> getSessionIds(){
		return sessionIDs;
	}

	public void addCookieToList(Cookie cookie){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, cookie.getMaxAge());
		Date d = calendar.getTime();
		boolean found = true;
		if(cookieSet.isEmpty()) 
			found=false; 
		for (Map.Entry<Date, Cookie> entry : cookieSet.entrySet()) {
			if (cookie.getValue().equalsIgnoreCase(entry.getValue().getValue())) {
				found = true;
			}
		}
		if (!found) {
			cookieSet.put(d,cookie);
			addSessionIdToList(cookie.getValue());
		}
	}
		
	public void removeCookieToList(Cookie cookie){
		if (cookieSet.isEmpty()){
			cookieSet.remove(cookie);
			removeSessionIdFromList(cookie.getValue());
		} 
	}
	
	private void removeExpiredCookies(){
		if (!cookieSet.isEmpty()) {
			for (Map.Entry<Date, Cookie> entry : cookieSet.entrySet()){
				if(isExpired(entry.getKey())){
					removeCookieToList(entry.getValue());
					removeSessionIdFromList(entry.getValue().getValue());
				}
			}
		}
	}
	
    private boolean isExpired(Date cookieExpires) {
		if (cookieExpires == null) return true;
		Date now = new Date();
		if (now.compareTo(cookieExpires) <= 0){
			return false;
		}
		return true; 
    }
	
    /**
     * add session id to list
     * @param sessionId
     * @return void
     */
    public void addSessionIdToList(String sessionId){
    	sessionIDs.add(sessionId);
    }
    
	/**
	 * remove session ids
	 * @param sessionId
	 */
	public void removeSessionIdFromList(String sessionId){
		sessionIDs.remove(sessionId);
	}
	
	public void printCurrentList(){
		logger.log(Level.INFO, "Active session:");
		for (String id : sessionIDs)
			logger.log(Level.INFO,id);
	}
	

}
