<%@page import="org.keywordsearch.sparqlgenerator.SparqlComparator"%>
<%@page import="org.keywordsearch.sparqlgenerator.SPARQL"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.sleepycat.persist.EntityCursor"%>
<%@page import="berkeleydbje.Literal"%>
<%@page import="org.keywordsearch.init.Initialize"%>
<%@page import="org.keywordsearch.sparqlgenerator.KeywordFunctions" %>
<%@page import="org.keywordsearch.sparqllib.QueryResponse"%>
<%@page import="org.keywordsearch.sparqllib.ISPARQLQueryLib"%>
<%@page import="org.aksw.sparql2nl.Sparql2nl"%>
<%@page import="org.aksw.sparql2nl.naturallanguagegeneration.SimpleNLGwithPostprocessing2"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<jsp:useBean id="ksearch" class="org.keywordsearch.sparqlgenerator.KeywordFunctions" scope="session"/>
<jsp:setProperty name="ksearch" property="*"/> 
<jsp:useBean id="fgraph" class="org.keywordsearch.sparqlgenerator.GraphFunctions" scope="session"/>
<jsp:setProperty name="fgraph" property="*"/> 
<jsp:useBean id="key2sparql" class="org.keywordsearch.sparqlgenerator.KeywordsToSparql" scope="session"/>
<jsp:setProperty name="key2sparql" property="*"/>
<jsp:useBean id="init" class="org.keywordsearch.init.Initialize" scope="session"/>
<jsp:setProperty name="init" property="*"/>
<jsp:useBean id="sparql2nl" class="org.aksw.sparql2nl.Sparql2nl" scope="session"/>
<jsp:setProperty name="sparql2nl" property="*"/> 

<script language="javascript" type="text/javascript">
    function Popup(url,winName,w,h,i)
    {
        var Left = (screen.width/2) - (w/2);
        var Top = (screen.height/2) - (h/2);
        //settings ='height='+h+',width='+w+',top='+TopPosition+',left='+LeftPosition+',scrollbars='+scroll+',resizable';
        //var popupWindow = window.open("popup.jsp", winName, "width=400, height=200");
        var popupWindow = window.open("popup.jsp", winName, 'width='+w+', height='+h+', top='+Top+', left='+Left+',scrollbars=yes');
        //return false;
        var ii=2*i+1;
        document.forms[ii].action = url;
        document.forms[ii].submit();
        
    }
</script>
<!--<p><a href="http://www.quackit.com/common/link_builder.cfm" onclick="centeredPopup(this.href,'myWindow','700','300','yes');return false">Centered Popup</a></p>
<p><a href="http://www.quackit.com/common/link_builder.cfm" onclick="centeredPopup(this.href,'myWindow','700','300','yes');return false">Centered Popup</a></p>
-->
<html>
    <head>
        <title>Keyword search</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="./js/functions.js"></script>
        <link rel="stylesheet" href="./css/main.css">
    </head>
    <body>
    <div id="container">
        <div id="top">
            <a style="visibility: hidden" href="#">About</a> 
            <a style="visibility: hidden" href="#">Examples</a>
            <a style="visibility: hidden" href="#">Help</a>
        </div>
        <h1>Keyword Search and Explore</h1>

        <form id="search_form" method="get" action="#">
            <!--<table border="0" id="search_box_table">
                <tr>
                    <td>-->
            <div id="search_box" >
                <div id="search_box_front_img" >
                    <img src="./images/search.png" alt="search" height="25" width="25">
                </div>
                    <!--</td>-->
                    <!--<td>-->
                <div id="search_box_textarea" >

                        <input type="text" name="keywords" placeholder="" 
                                value="<% 
                        
                                    if(request.getParameter("keywords") != null)
                                                out.println(ksearch.getKeywords().replaceAll("\"", "&quot;"));
                                         
                                         %>"/>
                                                
                </div>
<!--                    </td>
                    <td>-->
                <div id="search_box_clear" >
<!--                        <input type="button" onclick="clear_form()" value="Click Me!">-->
                
                    <!--</td>-->
                    <!--<td>-->
                    <a href="<%out.println(request.getRequestURL());%>"><img src="./images/remove-kwds-basic.png" alt="clear" onMouseOver="this.src='./images/remove-kwds-omo.png'" 
                             onMouseOut="this.src='./images/remove-kwds-basic.png'" title="Clear">
                    </a>
                 </div>   
                </div>
                <div id="ranking_checkbox" >
                   <input type="checkbox" name="numTriplets" checked"<%

                   %>"/> Number of Triplets
                   <input type="checkbox" name="averageSP"/> Average Shortest Paths
                   <input type="checkbox" name="longestSP" checked="checked"/> Longest Shortest Path
                </div> 
                    <!--</td>-->
                <!--</tr>-->
            <!--</table>-->
           
            
            
        </form>
        <%
        if(request.getParameter("keywords") == null || request.getParameter("keywords").equals("")){
            out.println("<div id=\"example_msg\">" + "Type your keywords here (e.g. MI0001364 NAME) </div>");
            if (session.getAttribute("ready") == null)
            {
                sparql2nl.init_url();
                session.setAttribute("ready", "yes");
                //pame na steiloume ena dummy query sthn getQueryNL()
                //gia na ginoun oi xronovOres arxikopoihseis sto QueryFactory...
                String dummy = "Select ?h where { ?h rdf:type diana:Hairpin.}";
                dummy = sparql2nl.getQueryNL(dummy)[1];
            }
        }
        %>


        
        <%                              
            if(request.getParameter("keywords") != null && !request.getParameter("keywords").equals("")){
                
                try {
                    //Get all the necessary constants
                    String endpoint = init.getConstants().endpoint;
                    String prefixes = init.getConstants().prefixes;
                    String query_prefix = init.getConstants().query_prefix;                  
                    String bdbfiles_path = init.getConstants().bdbfiles_path;
                    boolean numTriplets = false;
                    boolean averageSP = false;
                    boolean longestSP = false;
                        
                    if (request.getParameter("numTriplets") != null) {
                        numTriplets = true;
                    }
                    else if (request.getParameter("averageSP") != null) {
                        averageSP = true;
                    }
                    else if (request.getParameter("longestSP") != null) {
                       longestSP = true;
                    }
                   
                    // Keyword to Sparql
                    String keywordInput = ksearch.getKeywords();
                    key2sparql.getSparqlFromKeywords(keywordInput, init.getDB(), endpoint, prefixes, query_prefix);
                    ArrayList<SPARQL> sparqlQueryList = key2sparql.getQueryList();
                    String keywordsMessage = key2sparql.getKeywordsMessage();
                    
                    // Sparql2nl
                    ArrayList<String> nl_sparqlQueryList = new ArrayList();
                    sparql2nl.setPrefixes(prefixes);
                    sparql2nl.setEndpoint(endpoint);
                    

                    out.println("<table border=\"0\" id=\"query_table\">");

                    if (sparqlQueryList.size() == 0){
                        out.println("<div id=\"queries_msg\">" + "No matches found for these keywords </div>");
                    }
                    else if (sparqlQueryList.size() == 1 && sparqlQueryList.get(0).getSparqlQueryArray().length == 1){
                        if (!keywordsMessage.equals("")){                         
                            out.println("<div id=\"keywords_msg\"><i>" + keywordsMessage + "</i></div>");
                        }
                        out.println("<div id=\"queries_msg\">" +  sparqlQueryList.get(0).getSparqlQueryArray()[0] + "</div>");
                        sparqlQueryList.remove(0);
                    }
                    else {
                        if (!keywordsMessage.equals("")){                         
                            out.println("<div id=\"keywords_msg\"><i>" + keywordsMessage + "</i></div>");
                        }
                        int numQueries = (sparqlQueryList.size() > 10 ? 10:sparqlQueryList.size()) ;
                        out.println("<div id=\"queries_msg\">" + numQueries + " candidate SPARQL queries</div>");
                    }
                    Collections.sort(sparqlQueryList, new SparqlComparator(numTriplets, averageSP, longestSP));
                    for (int i = 0;  i < sparqlQueryList.size() && i < 10; i++) {
                        if (i % 3 == 0){
                            out.println("<tr>");
                        }
                        out.println("<td>");
                        out.println("<table border=\"0\" id=\"inside_table\">");
                        out.println("<tr>");

                        out.println("<td>");
                        //out.println(i + ")");
                        out.println("</td>");
                        out.println("<td>");
                        String query_sparql = sparqlQueryList.get(i).getSparqlQuery();
                        query_sparql += " LIMIT 100";
                        String query_nl = "";
                                               
                        query_nl = sparql2nl.getQueryNL(query_sparql)[1];//result before postprocessing:[0] 
                        out.println("<i id=\"queries_msg\">#"+(i+1)+"</i>" + " " + query_nl + "<br>");//- after postprocessing:[1] -
                        nl_sparqlQueryList.add(query_nl);
                        
                        out.println("</td>");
                        out.println("<td>");
                        
                        out.println("<form action=\"\" method=\"GET\" target=\"SPARQL\">"
                                   +  "<a href=\"javascript:Popup('popup.jsp','SPARQL',600,350,"+i+");\">"
                                   +  "<img id=\"myImg\" src=\"./images/show-sparql.png\" "
                                   +   "onMouseOver=\"this.src='./images/show-sparql-omo.png'\" onMouseOut=\"this.src='./images/show-sparql.png' \"title=\"SPARQL\">"
                                   +  "</a>"
                               //  +  "<input type=\"hidden\" id=\"query_s\" name=\"query_s\" value=\""+query_sparql+"\">"
                                   +  "<input type=\"hidden\" id=\"query_no\" name=\"query_no\" value=\""+i+"\">"
                                   +  "</form>");
                        out.println("<form action=\"results.jsp\" method=\"GET\">");
                        out.println("<input type=\"hidden\" id=\"queryId\" name=\"queryId\" value=\"" + i + "\"/>");
                        out.println("<input type=\"image\" src=\"./images/execute-query-basic.png\" alt=\"Run\" "
                                   + "onMouseOver=\"this.src='./images/execute-query-omo.png'\" onMouseOut=\"this.src='./images/execute-query-basic.png'\""
                                   + "title=\"Execute\"/>"
                          //       + "<input type=\"hidden\" id=\"nl_query\" name=\"nl_query\" value=\"" + query_nl +"\"/>"
                                   + "</form>");
                          //  out.println("</tr>");
                        out.println("</td>");
                        out.println("</tr>");
                        out.println("</table>");
                        out.println("</td>");  
                        if((i+1) % 3 == 0){ //anA 3 queries pame apo katw...
                            out.println("</tr>");
                        }
                        
                    }
                    out.println("</table>");
                    session.setAttribute("nl_queries", nl_sparqlQueryList);
            }         
            catch (Exception e) {
                    out.println("<div id=\"queries_msg\">" + e.getMessage() + "</div>");
            }
         }    
        %>
    </div>
    </body>
</html>
