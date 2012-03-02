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
    script = "print(datahamstern.getEntityStore().getOrganizations().count());\n//se.datahamstern.services.naringslivsregistret.HarvestNaringslivsregistret.main(null);";
  }
%>
<html>
<head><title>bsh</title></head>
<body>
<form action="bsh.jsp" method="post">
  <%--<div><label>description: <input type="text" name="description" value=""/></label></div>--%>
  <div><textarea rows="20" cols="160" name="script"><%=script%></textarea></div>
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
<h1>System.out</h1>
<pre>
  <%=bshOut%>
</pre>
<h1>System.err</h1>
<pre>
  <%=bshErr%>
</pre>
</body>
</html>