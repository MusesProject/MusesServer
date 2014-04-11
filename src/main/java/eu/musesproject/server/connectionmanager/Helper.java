/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

/**
 * This is helper class used by the servlet
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Helper {
	private static boolean D = false;
	private static final int COOKIE_MAX_AGE =  60*60*24;
	Cookie retreivedCookie = null;

	/**
	 * Set the cookie from the http request, if cookie is null then create the cookie from the session id
	 * @param HttpServletRequest req
	 * @return void
	 */
	public void setCookie(HttpServletRequest req) {
		Cookie [] cookies = req.getCookies();
		if (cookies != null ){
			for (Cookie ck : cookies){
				if (ck.getName().equals("JSESSIONID")) {
					retreivedCookie = ck;
					if (D) System.out.println("Rereived Cookie: Name " + ck.getName() + "   Value- " + retreivedCookie.getValue());
				}
			}
		} else {
			retreivedCookie = new Cookie("JSESSIONID", req.getSession().getId());
			retreivedCookie.setMaxAge(COOKIE_MAX_AGE);
			retreivedCookie.setPath(req.getContextPath());
			if (D) System.out.println("Cookie created .. new request ..");	
		}
		/**
		 * 
		 * @author yasir
		 * @version 2.1
		 */
	}
	/**
	 * Retrieves current cookie
	 * @return Cookie
	 */
	public Cookie getCookie(){
		return retreivedCookie;
	}

	/**
	 * Get data attached in http request
	 * @param request
	 * @return Data attached in Http request body 
	 * @throws IOException
	 */
	public String getRequestData(HttpServletRequest request) throws IOException {
		
		// This code is copy pasted from http://stackoverflow.com/questions/14525982/getting-request-payload-from-post-request-in-java-servlet
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        ServletInputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	        	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            bufferedReader = new BufferedReader(inputStreamReader);
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	            	throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	    
	}
	
	/**
	 * Destroys the http session
	 * @param request
	 * @return void
	 */
	public void disconnect(HttpServletRequest request){
		request.getSession().invalidate();
	}
	
	/**
	 * Check if the request connection-type header is "disconnect"
	 * @param dataAttachedInCurrentReuqest
	 * @return 
	 */
	public boolean isDisconnectRequest(String dataAttachedInCurrentReuqest){
		if (dataAttachedInCurrentReuqest.equalsIgnoreCase("disconnect")){
			return true; 
		} 
		return false;
	}
	
	/**
	 * Check if the request connection-type header is "poll"
	 * @param dataAttachedInCurrentReuqest
	 * @return
	 */
	
	public boolean isPollRequest(String dataAttachedInCurrentReuqest){
		if (dataAttachedInCurrentReuqest.equalsIgnoreCase("poll")){
			return true; 
		} 
		return false;
	}
	/**
	 * Check if the request connection-type header is "connect"
	 * @param dataAttachedInCurrentReuqest
	 * @return
	 */
	public boolean isConnectRequest(String dataAttachedInCurrentReuqest){
		if (dataAttachedInCurrentReuqest.equalsIgnoreCase("connect")){
			return true; 
		} 
		return false;
	}
	
}
