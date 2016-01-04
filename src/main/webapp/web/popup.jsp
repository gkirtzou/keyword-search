<%-- 
    Document   : popup
    Created on : Mar 16, 2015, 2:06:08 AM
    Author     : Tukei
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.keywordsearch.sparqlgenerator.SPARQL"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.io.IOException"%> 
<%@page import="javax.servlet.ServletException"%>
<%@page import="javax.servlet.http.HttpServlet"%>
<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@page import="javax.servlet.http.HttpServletResponse"%>
<%@page import="java.io.PrintWriter"%>
<!DOCTYPE html>

<jsp:useBean id="key2sparql" class="org.keywordsearch.sparqlgenerator.KeywordsToSparql" scope="session"/>
<jsp:setProperty name="key2sparql" property="*"/>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="./js/functions.js"></script>
        <link rel="stylesheet" href="./css/main.css">
        <title>SPARQL query</title>
    </head>
    <body>
        <%  int i = Integer.parseInt(request.getParameter("query_no"));  
            ArrayList<SPARQL> sparqlQueryList = key2sparql.getQueryList();
            String[] curQuery = sparqlQueryList.get(i).getSparqlQueryArray();
            
        %>
        <div id="container">
            <div id="queries_msg">The query #<%=i+1%> in SPARQL:</div>
                <table border="0" id="query_table">
                       <table border="0" id="inside_table">
                           <div id="results_query_box">
                           <%
                                for (int j=0; j<curQuery.length; j++)
                                {
                                    out.println(curQuery[j] + "<br>");
                                }
                           %>
                           </div>
                       </table>
                </table>
        </div>
    </body>
</html>
