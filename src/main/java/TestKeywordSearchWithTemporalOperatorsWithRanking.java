/* 
 * Copyright (C) 2015 "IMIS-Athena R.C.",
 * Institute for the Management of Information Systems, part of the "Athena" 
 * Research and Innovation Centre in Information, Communication and Knowledge Technologies.
 * [http://www.imis.athena-innovation.gr/]
 *
 * This file is part of KeywordSearchLib.
 * KeywordSearchLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KeywordSearchLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KeywordSearchLib.  If not, see <http://www.gnu.org/licenses/>.
 */
import org.keywordsearch.init.*;
import org.keywordsearch.sparqlgenerator.*;
import org.keywordsearch.sparqllib.*;
import java.util.*;
import com.hp.hpl.jena.query.QuerySolution;


/**
 *
 * @author gkirtzou
 */
public class TestKeywordSearchWithTemporalOperatorsWithRanking {
    
    public static void main(String[] args) throws Exception {
        // Initialization Berkeley Files, endpoint
        Initialize init = new Initialize();
        String endpoint = init.getConstants().endpoint;
        String prefixes = init.getConstants().prefixes;
        String query_prefix = init.getConstants().query_prefix;
        String bdbfiles_path = init.getConstants().bdbfiles_path;
        //Map<String, String> temporal_properties = init.getConstants().getTemporalProperiesMap();
        
                
        // Keywords
        String keywordInput = "year after:2000";//"dre-mir-10b-1 SEQUENCE version before:\"10.0\"";
                
                /* ""hairpin version before:10 version after:8"; */
        boolean numTriplets = true;
        boolean averageSP = true;
        boolean longestSP = true;
               
        
        // Keyword to Sparql
        KeywordsToSparql key2sparql = new KeywordsToSparql();
        key2sparql.getSparqlFromKeywords(keywordInput, init.getDB(), endpoint, prefixes, query_prefix);
        ArrayList<SPARQL> sparqlQueryList = key2sparql.getQueryList();
        String keywordsMessage = key2sparql.getKeywordsMessage();
        
        
        // Printing Sparql Queries or Error message
        if(sparqlQueryList.size() == 0) {
            System.out.println("No matches found for the input keywords.");
        }
        else if(sparqlQueryList.size() == 1 && sparqlQueryList.get(0).getSparqlQueryArray().length == 1){
            if(!keywordsMessage.equals("")){                         
                System.out.println(keywordsMessage);
            }
            System.out.println(sparqlQueryList.get(0).getSparqlQueryArray()[0]);
            sparqlQueryList.remove(0);
        }
        else {
            if(!keywordsMessage.equals("")){                         
                System.out.println(keywordsMessage);
            }
            System.out.println(sparqlQueryList.size() + " candidate SPARQL queries");
        }     
        int i = 0;
        Collections.sort(sparqlQueryList, new SparqlComparator(numTriplets, averageSP, longestSP));
        for(SPARQL q : sparqlQueryList) {
            System.out.println(i + ":" + q.getSparqlQuery());
            i++;
        }
       
       // System.out.println(init.getConstants().prefixes);
       // System.out.println(init.getConstants().endpoint);
        // Executing a sparlq query
        /*
        if(sparqlQueryList.size() != 0) {
            // Select query
            int queryId = 0;
            System.out.println("Selected SPARQL query:: " + queryId);

            // Prepare query for execution
            String[] curQuery = sparqlQueryList.get(queryId);
            String query = "";
            for(int i=0; i<curQuery.length; i++) {
                query += curQuery[i] + " ";
            }
            query += " LIMIT 100";
            System.out.println(query);
            
            SPARQLQueryLib queryLib = new SPARQLQueryLib();
            queryLib.connect(init.getConstants().endpoint);
            queryLib.setPrefixes(init.getConstants().prefixes);
            
            QueryResponse qResponse = queryLib.sendQuery(init.getConstants().prefixes + query);
            List<QuerySolution> results = qResponse.getResultSet();
            
            if(results.size()>0) {
                System.out.println("Results");
                //find columns in result
                List<String> columnNames = new ArrayList<String>();
                QuerySolution s = results.get(0);
                Iterator<String> it = s.varNames();
                while(it.hasNext()){
                        columnNames.add(it.next());
                }

                //Print header 
                for(String column : columnNames){
                    System.out.print(column + "\t");
                }
                System.out.println();

                Map<String, String> urlToPrefix = init.getConstants().urlToPrefixMap;
                // Print results
                for(QuerySolution result : results) {
                    for(String column : columnNames) {
                        String valueLocalhost = result.get(column).toString();
                        boolean isLink = (valueLocalhost.startsWith("http"));
                        if(isLink){
                            String value = valueLocalhost.replace("localhost:2020","snf-541101.vm.okeanos.grnet.gr:8080/diana_lod/page");
                            
                            int index;
                            String prefix = "";
                            if(((index = value.lastIndexOf("/")) != -1) && ((prefix = urlToPrefix.get(value.substring(0, index+1)))!=null)){
                                System.out.print(prefix + value.substring(index+1) + "\t");
                            }
                            else if(((index = value.lastIndexOf("#")) != -1) && ((prefix = urlToPrefix.get(value.substring(0, index+1)))!=null)){
                                System.out.print(prefix + value.substring(index+1) + "\t");                    
                            }
                            else {
                                System.out.print(value.trim() + "\t");
                            }   
                        
                        }
                        else {
                            String value=valueLocalhost;
                            int httpIndex;
                            if((httpIndex = value.indexOf("^^"))> 0){
                                value = value.substring(0, httpIndex);
                            }
                        
                            System.out.print(value.trim() +"\t");
                        }
                    }
                    System.out.println();
                }
            }
            else{
                System.out.println("No results found");
            }
        } */
        return;
    }
}
