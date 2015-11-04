<%@ page import="java.util.Date" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>ditaa - DIagrams Through Ascii Art</title>
<link rel="stylesheet" href="org.ditaa.web.Ditaa/ditaa.css">
<link rel="stylesheet" href="org.ditaa.web.Ditaa/gwt.css">
</head>
<%  System.out.println("Request for " + request.getRequestURI() + " referred from " + request.getHeader("referer") + "  from " + request.getRemoteHost() + " - " + new Date() + ":"); %>
<frameset cols="45%,55%">
  <frame src="entry.jsp?<%=request.getQueryString()==null ? "E" : request.getQueryString()%>">
  <frameset rows="70%,30%">
    <frame src="blank.html" name="chart">
    <frame src="http://ditaa.sourceforge.net/">
  </frameset>
  <noframes>
    <p>Frames disabled.  Try <a href="entry.html">this form</a></p>
  </noframes>
</frameset>
</html>