<%-- 
    Document   : response
    Created on : Oct 7, 2014, 1:18:16 PM
    Author     : penny
--%>
<%@page import="org.keywordsearch.sparqlgenerator.SPARQL"%>
<%@page import="com.sleepycat.persist.EntityCursor"%>
<%@page import="berkeleydbje.Literal"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hp.hpl.jena.query.QuerySolution"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="org.keywordsearch.sparqllib.QueryResponse"%>
<!--%@page import="org.keywordsearch.sparqllib.QueryLibImpl"%-->
<%@page import="org.keywordsearch.sparqllib.SPARQLQueryLib"%>
<%@page import="org.keywordsearch.sparqllib.ISPARQLQueryLib"%>
<%@page import="org.keywordsearch.init.Initialize"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<jsp:useBean id="ksearch" class="org.keywordsearch.sparqlgenerator.KeywordFunctions" scope="session"/>
<jsp:setProperty name="ksearch" property="*"/> 
<jsp:useBean id="key2sparql" class="org.keywordsearch.sparqlgenerator.KeywordsToSparql" scope="session"/>
<jsp:setProperty name="key2sparql" property="*"/>
<!--jsp:useBean id="queryLib" class="org.keywordsearch.sparqllib.QueryLibImpl" scope="session"/-->
<jsp:useBean id="queryLib" class="org.keywordsearch.sparqllib.SPARQLQueryLib" scope="session"/>
<jsp:setProperty name="queryLib" property="*"/>
<jsp:useBean id="init" class="org.keywordsearch.init.Initialize" scope="session"/>
<jsp:setProperty name="init" property="*"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Explore SPARQL queries</title>
        <link rel="stylesheet" href="./css/main.css">
         <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>

        <script type="text/javascript" type="text/javascript">
            $(document).ready(function(){
                var maxHeight = null;
            $("#query_results_table tr").each(function() {
                 var thisHeight = $(this).height();
                 if(maxHeight == null || thisHeight > maxHeight) maxHeight = thisHeight;
                }).height(maxHeight);
            });
        </script>
        <script>
            function Popup(url,winName,w,h,i)
            {
                var Left = (screen.width/2) - (w/2);
                var Top = (screen.height/2) - (h/2);
                //settings ='height='+h+',width='+w+',top='+TopPosition+',left='+LeftPosition+',scrollbars='+scroll+',resizable';
                //var popupWindow = window.open("popup.jsp", winName, "width=400, height=200");
                var popupWindow = window.open("popup.jsp", winName, 'width='+w+', height='+h+', top='+Top+', left='+Left+',scrollbars=yes');
                //return false;
                //var ii=2*i+1;
                document.forms[0].action = url;
                document.forms[0].submit();
        
            }
        </script>
    </head>
    <body>
    <div id="container">
        <div id="top">
            <a style="visibility: hidden" href="#">About</a>  
            <a style="visibility: hidden" href="#">Examples</a> 
            <a style="visibility: hidden" href="#">Help</a>
        </div>
        
        <% 
            int queryId = Integer.parseInt(request.getParameter("queryId"));
            //List<List<String>> queries = ksearch.getQueries();
            ArrayList<SPARQL> sparqlQueryList = key2sparql.getQueryList();
            String[] curQuery = sparqlQueryList.get(queryId).getSparqlQueryArray();
            String nl_curQuery = (String)((List)session.getAttribute("nl_queries")).get(queryId);
            String query = "";
            
            out.println("<div id=\"results_query_msg\">");
                out.println("Selected SPARQL query #" + (queryId+1) + ":");
            out.println("</div>");
            out.println("<div id=\"results_query_box\">");
                out.println("<div id=\"results_query_backButton\">");
                    out.println("<img src=\"./images/back.png\" alt=\"Back\" title=\"Back\" onClick=history.go(-1);return true;\" onMouseOver=\"this.src='./images/back-omo.png'\" "
                            + "onMouseOut=\"this.src='./images/back.png'\" />");
                    out.println("<span class=\"caption\">Back</span>");
                    //out.println("Back");
                out.println("</div>");
                out.println("<div id=\"results_query\">");
                    for(int i=0; i<curQuery.length; i++){
                        query += curQuery[i] + " ";
                     //   out.println(curQuery[i] + "<br>");
                    } 
                     //String q_nl = request.getParameter("nl_query");
                     out.println(nl_curQuery + "<br>");
                out.println("</div>");
                out.println("<form action=\"\" method=\"GET\" target=\"SPARQL\">"
                            +  "<a href=\"javascript:Popup('popup.jsp','SPARQL',600,350,"+queryId+");\">"
                            +  "<img id=\"myImg\" src=\"./images/show-sparql.png\" "
                            +   "onMouseOver=\"this.src='./images/show-sparql-omo.png'\" onMouseOut=\"this.src='./images/show-sparql.png' \"title=\"SPARQL\">"
                            +  "</a>"
                            +  "<input type=\"hidden\" id=\"query_no\" name=\"query_no\" value=\""+queryId+"\">"
                            +  "</form>");
            out.println("</div>");
            
            query += " LIMIT 100";
            //out.println(query);
            queryLib.connect(init.getConstants().endpoint);
            queryLib.setPrefixes(init.getConstants().prefixes);
            
            QueryResponse qResponse = queryLib.sendQuery(init.getConstants().prefixes + query);
            List<QuerySolution> results = qResponse.getResultSet();
            
            if(results.size()>0){
                out.println("<div id=\"results_query_msg\">");
                    out.println("Results");
                out.println("</div>");
                
                out.println("<table border=\"0\" id=\"query_results_table\" cellpadding=\"3\">");
            

            //find columns in result
            List<String> columnNames = new ArrayList<String>();
            QuerySolution s = results.get(0);
            Iterator<String> it = s.varNames();
            while(it.hasNext()){
                columnNames.add(it.next());
            }
            
            //print header 
            out.println("<tr>");
            out.println("<th></th>");
            for(String column : columnNames){
                
                out.println("<th>" + column + "</th>");
            }
            out.println("<th></th>");
            out.println("</tr>");
            Map<String, String> urlToPrefix = init.getConstants().urlToPrefixMap;
                    
            //print results
            int line = 0;
            for (QuerySolution result : results){
//if(line % 2 == 0){
//                    out.println("<tr class=\"even\">");
//                } else {
//                    out.println("<tr class=\"odd\">");
//                }
                out.println("<tr>");
                out.println("<td></td>");
                for(String column : columnNames){
                    
                    out.println("<td>");
                    
                    //String value = result.get(column).toString();
                    String valueLocalhost = result.get(column).toString();
                    
//                    if(line == 0){
//                        value = value + "asdalksndaklsndaksdnaskfnafafkjahfkiafbkakfjabfa"+line;
//                        
//                    }
                    boolean isLink = (valueLocalhost.startsWith("http"));
                    if(isLink){
                        String value = valueLocalhost.replace("localhost:2020","snf-541101.vm.okeanos.grnet.gr:8080/diana_lod/page");
                        out.println("<a href=\"" + value.trim() + "\">");
                        
                        int index;
                        String prefix = "";
                        if(((index = value.lastIndexOf("/")) != -1) && ((prefix = urlToPrefix.get(value.substring(0, index+1)))!=null)){
                            out.println(prefix + value.substring(index+1));
                        }
                        else if(((index = value.lastIndexOf("#")) != -1) && ((prefix = urlToPrefix.get(value.substring(0, index+1)))!=null)){
                            out.println(prefix + value.substring(index+1));                    
                        }
                        else {
                            out.println(value.trim());
                        }
                        out.println("</a>");
                    }
                    else {
                        String value=valueLocalhost;
                        int httpIndex;
                        if((httpIndex = value.indexOf("^^"))> 0){
                            value = value.substring(0, httpIndex);
                        }
                        
                        out.println(value.trim());
                    }
                    out.println("</td>");
                }
                out.println("<td></td>");
                out.println("</tr>");
                line++;
            }
            }
            else{
                out.println("<h2>No results found</h2>");
            }
            
        %>
        
        </table>
    </div>
    </body>
</html>
