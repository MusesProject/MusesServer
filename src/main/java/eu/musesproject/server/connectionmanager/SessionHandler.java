/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
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
	
	public Map<String,HttpSession> activeSessions = new ConcurrentHashMap<String,HttpSession>();
	public Map<String,List<HttpSession>> eachClientSessions = new ConcurrentHashMap<String,List<HttpSession>>(); // TBD
	private Set<String> sessionIDs = new HashSet<String>();
	private static final String ATTRIBUTE_NAME = "com.swedenconnectivity.comserver.SessionHandler";
	private static final boolean D = true;
	private Logger logger = Logger.getLogger(SessionHandler.class.getName());

	
	public SessionHandler() {
		logger = Logger.getRootLogger();
		BasicConfigurator.configure();	
	}
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
		int interval=0;
		HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
		if (request.getMethod().equalsIgnoreCase("POST")){
			String pollIntervalTimeout = request.getHeader("poll-interval");
			HttpSession session = request.getSession();
			if (pollIntervalTimeout != null) {
				interval = Integer.parseInt(pollIntervalTimeout) * 2;
				session.setMaxInactiveInterval(interval);
			}
			// Set timeout for the session
			if (session !=null){
				if (session.isNew()) {
					addSessionToTable(request);
				}
			}
		}
		
	}
	
	/**
	 * Add session to the table
	 * @param httpServletRequest
	 * @return void
	 */
	
	private void addSessionToTable(HttpServletRequest httpServletRequest){
		if (!activeSessions.isEmpty()){ // FIXME why is if condition we are adding things here not retrieving
			for (Map.Entry<String, HttpSession> entry : activeSessions.entrySet()) {
				if (httpServletRequest.getSession() != entry.getValue()){
					activeSessions.put(httpServletRequest.getSession().getId(), httpServletRequest.getSession());
					if (D) logger.log(Level.INFO,"Session added to the table: " + httpServletRequest.getSession().getId());
				}
			}
		}else {
			activeSessions.put(httpServletRequest.getSession().getId(), httpServletRequest.getSession());
			if (D) logger.log(Level.INFO, "Session added to the table: " + httpServletRequest.getSession().getId());
		}
	}
	
	/**
	 * Remove session from the table
	 * @param httpSession
	 * @return void
	 */
	
	private void removeSessionFromTable(HttpSession httpSession){
		if (!activeSessions.isEmpty()){
			for (Map.Entry<String, HttpSession> entry : activeSessions.entrySet()) {
				if (httpSession == entry.getValue()){
					activeSessions.remove(entry.getKey());
					if (D) logger.log(Level.INFO, "Session removed from the table: " + httpSession.getId());
				}
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
		//HttpSession session = event.getSession().get;
		//session.setMaxInactiveInterval(30); //in seconds
	}

	/** 
	 * Called every time a session is destroyed, if the sesion
	 * is in the table remove it from the list and call the session callback method
	 * to inform other users
	 * @param HttpSessionEvent event
	 * @return void
	 */
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		removeSessionFromTable(event.getSession());
		if (sessionIDs.contains(event.getSession().getId())){
			logger.log(Level.INFO, "*** Session Destroyed *** " + event.getSession().getId()); // FIXME this id is wrong 
			removeSessionIdFromList(event.getSession().getId());
			ConnectionManager.toSessionCb(event.getSession().getId(), Statuses.DISCONNECTED);
		}
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
	 * Get numbers of sessions from the list
	 * @return count of sessions
	 */
	
	public int getSessionCount(){
		return activeSessions.size();
	}
	
	/**
	 * Get session id list
	 * @return sessionIDs
	 */
	public Set<String> getSessionIds(){
		return sessionIDs;
	}

	/**
	 * add session id to list
	 * @param sessionId
	 * @return void
	 */
	public void addSessionIdToList(String sessionId){
		if (D) logger.log(Level.INFO,"About to add : " + sessionId);
		sessionIDs.add(sessionId);
	}
	
	
	/**
	 * remove sesion ids
	 * @param sessionId
	 */
	public void removeSessionIdFromList(String sessionId){
		sessionIDs.remove(sessionId);
	}
	
	public boolean isSessionTableEmpty(){
		return activeSessions.isEmpty();
	}
	
	public Set<Entry<String, HttpSession>> getEntrySet(){
		return activeSessions.entrySet();
	}
	
	public HttpSession getSessionForId(String sessionId){
		return activeSessions.get(sessionId);		
	}
	
	public void removeSessionFromTable(String sessionId){
		activeSessions.remove(sessionId);
	}
	
	public void clearSessionTableEntries(){
		activeSessions.clear();
	}
}
