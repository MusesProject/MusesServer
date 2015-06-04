<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*" %>
<%@ page import="eu.musesproject.server.contextdatareceiver.ConnectionCallbacksImpl" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Muses Server</h1>
    <%!
    {
    //ConnectionCallbacksImpl connCb = new ConnectionCallbacksImpl();
    System.out.println( "Module wants to use connection manager should create object here in this JSP " );
    }
	%>
        
    </body>
</html>
