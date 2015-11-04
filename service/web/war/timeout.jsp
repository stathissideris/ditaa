<%@ page import="org.ditaa.web.ImageServlet" %>
<%@ page import="org.ditaa.web.HttpKit" %>
<%@ page import="org.ditaa.web.Config" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    int timeout = ImageServlet.getTimeout(request);
    int newTimeout = Math.min((timeout < 0) ? 60 : timeout * 2, ImageServlet.MAX_TIMEOUT);
    String newParams = HttpKit.adjustParameters(request, "timeout", "" + newTimeout);
%>
<html>
  <head><title>ditaa -- Time limit expired</title></head>
  <body>
  <h1>Operation Timed Out</h1>
  <p>It's taking <%=(timeout > 0) ? "more than " + timeout + " seconds" : "a while"%> to render your diagram.  That may be because:</p>
  <ul>
  <li>It's a large diagram</li>
  <li>The server is busy</li>
  <li>You've found a bug in Ditaa</li>
  </ul>
  <p>Would you like to <a href="render?<%=newParams%>">try again with a time limit of <%=newTimeout%> seconds</a>?</p>

<% if (!Config.isRenderExternal(getServletContext())) { %>
  <form style="margin:0" action="restart" target="chart" id="restartForm">
    <p>
      If this is happening a lot, and you think something is wrong, you could press this button:
      <input type="submit" value="reset the service">
    </p>
  </form>
<% } %>

  </body>
</html>