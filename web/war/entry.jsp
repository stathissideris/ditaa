<%@ page import="org.ditaa.web.ImageServlet" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>ditaa - DIagrams Through Ascii Art</title>
<link rel="stylesheet" href="ditaa.css">
<script language="javascript">
    function autosub() { document.getElementById("goForm").submit(); }
</script>
</head>
<%-- response.setDateHeader("Expires", 0L) --%>
<body onload="autosub()">

<h2>Create a diagram from ASCII using
<a target="_parent" href="http://ditaa.sourceforge.net/">ditaa</a></h2>

<blockquote style="margin-left: 1em">
<a target="_parent" href="http://ditaa.sourceforge.net/">ditaa library</a> by
<a target="_parent" href="http://www.stathis.co.uk/">Stathis Sideris</a>.  Web service by
<a target="_parent" href="http://billbaker.net/">Bill Baker</a>.
</blockquote>

<hr>

<form style="margin:0" action="render" target="chart" id="goForm">

<p>
<strong>ditaa</strong> transforms <strong>ASCII art</strong> into graphical diagrams.<br>
To see it in action, make changes to the text below and click
<input style="white-space:nowrap" type="submit" value="update the diagram">.
</p>

<label><textarea name="grid" cols="60" rows="30" style="background:#fff;width:100%"><% if (request.getParameter("grid") == null) {%>
     +--------+   +-------+    +-------+
     | c897   +---+ ditaa +--->|       |
     |  Text  |   +-------+    |diagram|
     |Document|   |!magic!|    |       |
     |     {d}|   |  c978 |    | c789  |
     +---+----+   +-------+    +-------+
         :                         ^
         |       Lots of work      |
         \-------------------------/

          ^
          |                   +-----------------+
 /--------+   /----*<-+----\  | Things to do    |
 |   cRED |   |cBLU|  |cEEE|  | cFB9            |
 |   /----+   +----+--+----/  | * Cut the grass |
 |   |cYEL|        |  |       | * Buy jam       |
 +---+----+        \--+--=-->*+ * Make website  |
                      |       +-----------------+
/--\/--\/--\          v
|  ||  ||  |
|  ++  ++  | +----+---+----+----+---+---+---+
\\  cDB6  // |    |{o}|    |    |{c}|{s}|   |
 | /----\ |  |{tr}|   |{mo}|{io}|   |   |{d}|
 | |c733| |  +----+---+----+----+---+---+---+
 +-+----+-+
            +-------+
+-------+   |       |    +---------+
|cFDA   |   |{c}    |    |eat cFF8 +
|wake up+-->+hungry?+--->|breakfast|
| {o}   |   | cDBF  |Y   +----+----+
+-------+   |       |         |
            +---+---+         v
                |N       +-----------+
                +------->| save c9FB |
                         | planet{mo}|
                         +-----------+

      /-----------------------\
      |         ditaa         |
      |  /- /\ +\/+ +\ |  +-  |
      |  \\ ++ |\/| +/ |  +-  |
      |  -/ || |  | |  +- +-  |
      |     cDEF {o}          |
      \-----------------------/
<% } else { %><%=request.getParameter("grid")%><% } %></textarea></label>

<br>
&nbsp;<input title="update the diagram with your changes" value="Update the diagram" type="submit">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label title="Magnification factor">Scale
<input type="text" name="scale" value="<%=request.getParameter("scale") == null ? "1" : request.getParameter("scale")%>" style="width:3em">
</label>

<label title="background color, hex-encoded (for example, FF0000 for red); 8 digits to include transparency.">Background
<input type="text" name="background" value="<%=request.getParameter("background") == null ? "FFFFFF" : request.getParameter("background")%>" style="width:5em">
</label>

<label title="make the background transparent (overrides background color)">
<input type="checkbox" name="T" <%=request.getParameter("T") == null ? "" : "checked"%>>Transparent
</label>

<br>
<label title="turn off antialiasing">
<input type="checkbox" name="A" <%=request.getParameter("A") == null ? "" : "checked"%>>No Antialiasing
</label>

<label title="turn off shadows">
<input type="checkbox" name="S" <%=request.getParameter("S") == null ? "" : "checked"%>>No Shadows
</label>

<label title="make all corners rounded, instead of square">
<input type="checkbox" name="r" <%=request.getParameter("r") == null ? "" : "checked"%>>Round all corners
</label>

<label title="don't leave any space between neighboring elements">
<input type="checkbox" name="E" <%=request.getParameter("E") == null ? "" : "checked"%>>Don't separate
</label>

<label title="give the sides of trapezoids and parallelogram fixed slope instead of fixed width">
<input type="checkbox" name="W" <%=request.getParameter("W") == null ? "" : "checked"%>>Fixed slope
</label>

<p>
<label title="how long should ditaa spend attempting to render before giving up?">Rendering time limit <input type="text" name="timeout" value="<%=ImageServlet.getTimeout(request)%>" style="width:3em"> seconds</label>
</p>

</form>

<hr>
<p><strong>Tips for embedding and revising diagrams:</strong></p>
<ul>
  <li>To embed a diagram in a webpage, just use the image's URL -- for example, if you see a diagram on the right side of this web page, in some browsers you can right-click on it and select "Copy image location" or "Copy image URL" to get the image's URL, which you can then paste into a link.</li>
  <li>
    To edit an image later, just remove the <samp>render</samp> part of its URL to get back to this editing page.  For example, for a simple diagram:
    <ul>
      <li>Image URL: <a target="_parent" href="http://ditaa.org/ditaa/render?grid=%2F--%5C%0D%0A%5C--%2F"><samp>http://ditaa.org/ditaa/<strong>render</strong>?grid=%2F--%5C%0D%0A%5C--%2F</samp></a></li>
      <li>URL for editing: <a target="_parent" href="http://ditaa.org/ditaa/?grid=%2F--%5C%0D%0A%5C--%2F"><samp>http://ditaa.org/ditaa/?grid=%2F--%5C%0D%0A%5C--%2F</samp></a></li>
    </ul>
  </li>
</ul>

<hr>
<p>For more on the ditaa rendering library, see <a target="_parent" href="http://ditaa.sourceforge.net/">the main ditaa webpage</a>.</p>

<!--
<form style="margin:0" action="restart" target="chart" id="restartForm">
<p>Apologies if the site is down.  If instead of a diagram you see an exception report
<i>(500 blah blah blah <b>Out of Memory</b> whine whine)</i>,
<input type="submit" value="reset the service">.</p>
</form>
-->

</body>
</html>
