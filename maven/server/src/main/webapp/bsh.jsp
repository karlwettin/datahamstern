<%@ page import="bsh.Interpreter" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.util.Date" %>
<%@ page import="se.datahamstern.Datahamstern" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!

  public static class Script {
    private String code;
    private Date executed = new Date();
    private String description;
    private String out;
    private String err;
  }

%>

<%
  String script = request.getParameter("script");
  if (script == null) {
    script = "";
  }
%>
<html>
<head><title>bsh</title></head>
<body>

<pre>
h = new se.datahamstern.external.naringslivsregistret.HarvestNaringslivsregistret();
datahamstern.glue.put(&quot;h&quot;, h);

h = datahamstern.glue.get(&quot;h&quot;);
h.harvest(&quot;5562999622&quot;, &quot;5600000000&quot;);

print(h.found());
</pre>

<form action="bsh.jsp#output" method="post">
  <%--<div><label>description: <input type="text" name="description" value=""/></label></div>--%>
  <div><textarea rows="8" cols="60" name="script"><%=script%></textarea></div>
  <a name="output"></a>
  <div><input type="submit"/></div>
</form>

<hr/>

<%
  ByteArrayOutputStream bshOutBaos = new ByteArrayOutputStream();
  PrintStream bshOutPw = new PrintStream(bshOutBaos);

  ByteArrayOutputStream bshErrBaos = new ByteArrayOutputStream();
  PrintStream bshErrPw = new PrintStream(bshErrBaos);

  Interpreter interpreter = new Interpreter();
  interpreter.setOut(bshOutPw);
  interpreter.setErr(bshErrPw);
  interpreter.set("datahamstern", Datahamstern.getInstance());

  long started = System.currentTimeMillis();
  interpreter.eval(script);
  long evalMilliseconds = System.currentTimeMillis() - started;

  bshErrPw.close();
  bshOutPw.close();

  String bshOut = IOUtils.toString(new ByteArrayInputStream(bshOutBaos.toByteArray()));
  String bshErr = IOUtils.toString(new ByteArrayInputStream(bshErrBaos.toByteArray()));

%>
<%=evalMilliseconds%> milliseconds to evalute script.
<br/>
<strong>System.out</strong>
<pre>
  <%=bshOut%>
</pre>
<strong>System.err</strong>
<pre>
  <%=bshErr%>
</pre>
</body>
</html>