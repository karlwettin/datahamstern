<%@ page import="org.apache.solr.client.solrj.response.QueryResponse" %>
<%@ page import="se.datahamstern.domain.OrganisationIndex" %>
<%@ page import="se.datahamstern.domain.Organisation" %>
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
    if (results.getResults().size() > 0) {
      boolean thresholdHit = false;
      float topScore = (Float)results.getResults().get(0).getFirstValue("score");
      float threshold = topScore *  0.25f;
      for (int i=0; i<results.getResults().size(); i++) {
        Organisation organisation = DomainStore.getInstance().getOrganisationer().get((String)results.getResults().get(i).getFirstValue("identity"));
        float score = (Float)results.getResults().get(i).getFirstValue("score");
        if (score < threshold && !thresholdHit) {
          thresholdHit = true;
          %><tr><td colspan="6"><hr/></td></tr><%
        }
%>
  <tr>
    <td><%=i%></td>
    <td><%=score%></td>
    <td><%=organisation.getTyp()%></td>
    <td><%=organisation.getNummer()%></td>
    <td><%=organisation.getNamn()%></td>
    <td><%=DomainStore.getInstance().getLän().get(organisation.getLänIdentity().get()).getNamn()%></td>
  </tr>
<%
      }
    }
  }
%>
</table>

</body>
</html>