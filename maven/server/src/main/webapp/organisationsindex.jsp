<%@ page import="org.apache.solr.client.solrj.response.QueryResponse" %>
<%@ page import="se.datahamstern.domain.OrganisationIndex" %>
<%@ page import="se.datahamstern.domain.naringslivsregistret.Organisation" %>
<%@ page import="se.datahamstern.domain.DomainStore" %>
<%--
  User: kalle
  Date: 4/6/12
  Time: 12:16 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>datahamstern - sök organisation</title></head>
<body>

<%
  String query = request.getParameter("query") ;
  if (query == null) {
    query = "";
  }
  query = query.trim();
%>

<form method="post" action="organisationsindex.jsp">
  <input name="query" value="<%=query%>">
  <button onclick="form.submit();">sök</button>
</form>





<%
  if ("post".equalsIgnoreCase(request.getMethod()) && !query.isEmpty()) {
    QueryResponse results = OrganisationIndex.getInstance().search(query);


%>
<%=results.getResults().getNumFound()%> träffar totalt.
<table>
<%
    for (int i=0; i<results.getResults().size(); i++) {
      Organisation organisation = DomainStore.getInstance().getOrganisationer().get((String)results.getResults().get(i).getFirstValue("identity"));
      float score = (Float)results.getResults().get(i).getFirstValue("score");
%>
  <tr>
    <td><%=i%></td>
    <td><%=score%></td>
    <td><%=organisation.getFirmaform()%></td>
    <td><%=organisation.getNummer()%></td>
    <td><%=organisation.getNamn()%></td>
    <td><%=DomainStore.getInstance().getLän().get(organisation.getLänIdentity().get()).getNamn()%></td>
  </tr>
<%
    }
  }
%>
</table>

</body>
</html>