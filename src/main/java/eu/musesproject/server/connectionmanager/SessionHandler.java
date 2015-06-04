/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import eu.musesproject.server.contextdatareceiver.ConnectionCallbacksImpl;
import eu.musesproject.server.db.handler.DBManager;

/**
 * This class handles all http session, called every time a 
 * request come from the client for both POST/GET. 
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class SessionHandler implements ServletContextListener { //, HttpSessionListener, ServletRequestListener{
	
//	private static Set<String> sessionIDs = new HashSet<String>();
//	private static Map<Date,Cookie> cookieSet = new ConcurrentHashMap<Date,Cookie>();
//	private static final String ATTRIBUTE_NAME = "com.swedenconnectivity.comserver.SessionHandler";
	private Logger logger = Logger.getLogger(SessionHandler.class.getName());

	
//	public SessionHandler() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	@Override
//	public void requestDestroyed(ServletRequestEvent sre) {
//		
//	}
//	
//	/**
//	 * This methods check for request method POST/GET, retrieve and set
//	 * poll-interval value from the header 
//	 * @param ServletRequestEvent
//	 * @return void
//	 */
//	
//	@Override
//	public void requestInitialized(ServletRequestEvent sre) {
//		
//		int interval=0;
//		HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
//		if (request.getMethod().equalsIgnoreCase("POST")){
//			String pollIntervalTimeout = request.getHeader("poll-interval");
//			HttpSession session = request.getSession();
//			if (pollIntervalTimeout != null) {
//				interval = Integer.parseInt(pollIntervalTimeout) * 10;
//				session.setMaxInactiveInterval(interval);
//			}
//			session.setMaxInactiveInterval(60*60); // 0=> never times out// 60 minutes => 60*60  
//		} else if (request.getMethod().equalsIgnoreCase("GET")) {
//			String pollIntervalTimeout = request.getParameter("poll-interval");
//			HttpSession session = request.getSession();
//			if (pollIntervalTimeout != null) {
//				interval = Integer.parseInt(pollIntervalTimeout) * 10;
//				session.setMaxInactiveInterval(interval);
//			}
//			session.setMaxInactiveInterval(60*60); // 0=> never times out// 60 minutes => 60*60  
//		}
//		
//	}
//	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//ce.getServletContext().setAttribute(ATTRIBUTE_NAME, this);
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// all the runnables/work units inside the queue will be canceled but the currently running 
		// task will be finished and not interrupted.
		ConnectionCallbacksImpl.getExecutor().shutdownNow();
		if (DBManager.sessionFactory != null) {
			DBManager.sessionFactory.close();
		}
	}
//
//	@Override
//	public void sessionCreated(HttpSessionEvent event) {
//		removeExpiredCookies();
//	}
//
//	/** 
//	 * Called every time a session is destroyed, if the session
//	 * is in the table remove it from the list and call the session callback method
//	 * to inform other users
//	 * @param HttpSessionEvent event
//	 * @return void
//	 */
//	
//	@Override
//	public void sessionDestroyed(HttpSessionEvent event) {
//		removeExpiredCookies();
//	}
//
//	/**
//	 * Get session handler singleTon object
//	 * @param context
//	 * @return
//	 */
//	public static SessionHandler getInstance(ServletContext context) {
//	    return (SessionHandler) context.getAttribute(ATTRIBUTE_NAME);
//	}
//	
//	/**
//	 * Get session id list
//	 * @return sessionIDs
//	 */
//	public Set<String> getSessionIds(){
//		return sessionIDs;
//	}
//
//	public void addCookieToList(Cookie cookie){
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.SECOND, cookie.getMaxAge());
//		Date d = calendar.getTime();
//		boolean found = false;
//		if(cookieSet.isEmpty()){
//			found=false; 
//		} 
//		for (Map.Entry<Date, Cookie> entry : cookieSet.entrySet()) {
//			if (cookie.getValue().equalsIgnoreCase(entry.getValue().getValue())) {
//				found = true;
//			}
//		}
//		if (!found) {
//			cookieSet.put(d,cookie);
//			addSessionIdToList(cookie.getValue());
//		}
//	}
//		
//	public void removeCookieToList(Cookie cookie){
//		if (!cookieSet.isEmpty()){
//			cookieSet.remove(cookie);
//			removeSessionIdFromList(cookie.getValue());
//		} 
//	}
//	
//	private void removeExpiredCookies(){
//		if (!cookieSet.isEmpty()) {
//			for (Map.Entry<Date, Cookie> entry : cookieSet.entrySet()){
//				if(isExpired(entry.getKey())){
//					removeCookieToList(entry.getValue());
//					removeSessionIdFromList(entry.getValue().getValue());
//				}
//			}
//		}
//	}
//	
//    private boolean isExpired(Date cookieExpires) {
//		if (cookieExpires == null) {
//				return true;
//		}
//		Date now = new Date();
//		if (now.compareTo(cookieExpires) <= 0){
//			return false;
//		}
//		return true; 
//    }
//	
//    /**
//     * add session id to list
//     * @param sessionId
//     * @return void
//     */
//    public void addSessionIdToList(String sessionId){
//    	sessionIDs.add(sessionId);
//    }
//    
//	/**
//	 * remove session ids
//	 * @param sessionId
//	 */
//	public void removeSessionIdFromList(String sessionId){
//		sessionIDs.remove(sessionId);
//	}
//	
//	public void printCurrentList(){
//		logger.log(Level.INFO, "Active session:");
//		for (String id : sessionIDs){
//			logger.log(Level.INFO,id);
//		}
//	}
//	
//
}
