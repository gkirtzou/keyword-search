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
package org.keywordsearch.sparqlgenerator;

import berkeleydbje.BerkeleyDBStorage;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author penny
 * @author gkirtzou
 */
public class KeywordsToSparql {
    
    /**
     * Instance of the class KeywordFunctions.
     * This class contains all the necessary functions in order to process
     * the user keywords.
     */
    KeywordFunctions fkeywords;
    /**
     * Instance of the class GraphFunctions. 
     * This class contains all the necessary functions in order to process 
     * the graphs of the Keyword Search Algorithm.
     */
    GraphFunctions fgraph;
    
    /**
     * The SPARQL query list.
     * This list is returned as a result after the algorithm has run.
     */
    private ArrayList<SPARQL> sparqlQueryList;  //static?
    /**
     * Messages for the user.
     */
    public String message; //static?

    
    /**
     * 
     */
    private boolean isRankByNumTriples; 
 
    
    
    public KeywordsToSparql() {
        this.sparqlQueryList = null; 
        this.message = "";
        this.fkeywords = new KeywordFunctions();
        this.fgraph = new GraphFunctions();
        this.isRankByNumTriples = false;
 
    }
    /**
     * Returns the SPARQL query list that occurs after the Keyword Search
     * Algorithm has run.
     * @return The SPARQL query list.
     */
    public ArrayList<SPARQL> getQueryList() 
    { 
        return sparqlQueryList; 
    }
    
    /**
     * Returns the user messages.
     * @return The user messages.
     */
    public String getKeywordsMessage() 
    { 
        return message; 
    }
    
    public boolean getRankByNumTriples () {
        return this.isRankByNumTriples;
    }
    
    public void setRankByNumTriples (boolean value) {
        this.isRankByNumTriples = value;
    }
    
    /**
     * This function gets the the user keywords as input, runs the Keyword Search
     * Algorithm and returns a list of SPARQL queries.
     * @param kwords The user keywords
     * @param classNames A HashMap with all the class names of the RDF schema. In each (key, value) 
     * pair, the key is a class name and the value is empty.
     * @param propertyNames A HashMap with all the property names of the RDF schema. In each (key, 
     * value) pair, the key is a property name and the value is the class name related to the key property.
     * @param literalNames A HashMap with all the literals in the RDF schema. In each (key, value) pair, 
     * the key is a literal and the value is a Set<property, class> of the related property and class. 
     * @param endpoint The endpoint where the RDF store is located.
     * @param prefixes All the prefixes in one string.
     * @param query_prefix The RDF schema vocabulary.
     * @return A list of SPARQL queries. Each SPARQL query is stored in a String[].
     */
    /*
    public List<String[]> getSparqlFromKeywords(HashMap classNames, HashMap propertyNames, 
            HashMap literalNames, String endpoint, String prefixes, String query_prefix) {
        
           
        String [] kwords = this.fkeywords.getKeywords();
        
        //Find the keyword matches for all the given keywords
        HashMap keywordMatches=fkeywords.getKeywordMatches(classNames, propertyNames, literalNames);
        
        //If only one match was found:
        if(kwords.length==1){
            
            //Iterate the keyword matches
            Set set = keywordMatches.entrySet();
            Iterator i = set.iterator();

            while(i.hasNext()) {
                Map.Entry currentCombination = (Map.Entry)i.next();
                
                String keyword= (String) currentCombination.getKey();
                HashMap matches = (HashMap) currentCombination.getValue();
            
                Set set1 = matches.entrySet();
                Iterator i1 = set1.iterator();

                while(i1.hasNext()) {
                    Map.Entry currentMatch = (Map.Entry)i1.next();
                    String[] sparqlQuery = fgraph.getSparqlQuery(currentMatch, query_prefix);
                    sparqlQueryList.add(sparqlQuery);
                }

            }
  
        }
        //If more than one matches were found:
        else{
            //Create the summary grpaph
            UndirectedSparseGraph summaryGraph = fgraph.getSummaryGraph(classNames, propertyNames);
            
        
            //Create all the possible combinations from the above matches
            Set<HashMap> keywordCombinations=fkeywords.getKeywordCombinations(kwords, keywordMatches);
         
            //Process each combination separately:
            for(HashMap kCombination : keywordCombinations) {
            
                //Initialize the shortest paths data structure
                Set<Map> shortestPathSet = new HashSet<>();
                //Create the augmented graph
                UndirectedSparseGraph augmentedGraph=fgraph.getSingleAugmentedGraph(kCombination, summaryGraph, classNames, propertyNames);
                //get all pairs
                Set<String[]> combinationsPairs=fkeywords.getCombinationPairs(kCombination);
            
                //For each pair:
                for(String[] str : combinationsPairs){
                    //Find the shortest path in the augmented graph
                    String key1=str[0].substring(str[0].indexOf(":")+1);
                    String key2=str[1].substring(str[1].indexOf(":")+1);
                
                    Map sPath =fgraph.getShortestPath(augmentedGraph, key1, key2); 
                    shortestPathSet.add(sPath); 
                }
            
                UndirectedSparseGraph queryPatternGraph = fgraph.getQueryPatternGraph(shortestPathSet, augmentedGraph, propertyNames);

                String[] sparqlQuery = fgraph.getSparqlQuery(queryPatternGraph, classNames, propertyNames, query_prefix);
            
                sparqlQueryList.add(sparqlQuery);
            
            }
            
        }
        
        return sparqlQueryList;
    } 
    */
    /**
     * This function gets the the user keywords as input, runs the Keyword Search
     * Algorithm and returns a list of SPARQL queries.
     * @param userKwords The user keywords
     * @param dbStorage A BerkeleyDB structure containing all the necessary information from 
     * the RDF schema.
     * @param endpoint The endpoint where the RDF store is located.
     * @param prefixes All the prefixes in one string.
     * @param query_prefix The RDF schema vocabulary.
     * @return A list of SPARQL queries. Each SPARQL query is stored in a String[].
     */
    
    public ArrayList<SPARQL> getSparqlFromKeywords(String keywords, BerkeleyDBStorage dbStorage, String endpoint, 
            String prefixes, String query_prefix) throws Exception {
             
        // Initialize list and message
        this.sparqlQueryList = new ArrayList<>(); 
        this.message = "";
        // Extract keywords from user input
        String [] userKwords = this.fkeywords.processInputKeywords(keywords);
        //for (String str: userKwords) {
        //    System.out.println(str);
        //}
        //Find the keyword matches for all the given keywords
        HashMap keywordMatches=fkeywords.getKeywordMatches(dbStorage);    
        //System.out.println(keywordMatches.toString());
        
               
        String kwords[] = new String[keywordMatches.size()];
        Set<String> kwordSet = keywordMatches.keySet();
        int cnt = 0;
        for(String str : kwordSet){
            kwords[cnt] =  str;
            cnt++;
        }
        
        //If only one keyword was matched:
        if(keywordMatches.size()==1){
            
            //Iterate the keyword matches
            Set set = keywordMatches.entrySet();
            Iterator i = set.iterator();
            
            while(i.hasNext()) {
                Map.Entry currentCombination = (Map.Entry)i.next();
                
                String keyword= (String) currentCombination.getKey();
                HashMap matches = (HashMap) currentCombination.getValue();
            
                Set set1 = matches.entrySet();
                Iterator i1 = set1.iterator();

                while(i1.hasNext()) {
                    Map.Entry currentMatch = (Map.Entry)i1.next();
                    SPARQL q = fgraph.getSparqlQuery(currentMatch, query_prefix);
                    this.sparqlQueryList.add(q);
                   
                }

            }
  
        }
        //If more than one keywords were matched:
        else if(keywordMatches.size()>1){
            
            //If no matches are found for one or more keywords, display a message to the user with the ignored keywords
            if(userKwords.length > keywordMatches.size()){
                message = "Showing results for: ";
                Set<String> keySet = keywordMatches.keySet();
                for(String key : keySet){
                    message = message + key + " ";
                }
            }
            
            //Create the summary graph
            UndirectedSparseGraph summaryGraph = fgraph.getSummaryGraph(dbStorage);
        
            //Create all the possible combinations from the above matches
            Set<HashMap> keywordCombinations=fkeywords.getKeywordCombinations(kwords, keywordMatches);
         
            //Process each combination separately:
            for(HashMap kCombination : keywordCombinations) {
            
                //Initialize the shortest paths data structure
                Set<Map> shortestPathSet = new HashSet<>();
                //Create the augmented graph
                UndirectedSparseGraph augmentedGraph=fgraph.getSingleAugmentedGraph(kCombination, summaryGraph, dbStorage);
                
                
                //get all pairs
                Set<String[]> combinationsPairs=fkeywords.getCombinationPairs(kCombination);
            
                //For each pair:
                double averageSP = 0.0;
                double longestSP = 0.0;
                for(String[] str : combinationsPairs){
                    //create the end nodes of the shortest path
                    
                    GraphNode n1 = fgraph.getShortestPathEndNode(str[0], (String) kCombination.get(str[0]));
                    GraphNode n2 = fgraph.getShortestPathEndNode(str[1], (String) kCombination.get(str[1]));
                                       
                    Map sPath =fgraph.getShortestPath(augmentedGraph, n1, n2); 
                    averageSP = averageSP + sPath.size();
                    if (longestSP < sPath.size())
                        longestSP = sPath.size();
                    shortestPathSet.add(sPath);                    
                }
            
                UndirectedSparseGraph queryPatternGraph = fgraph.getQueryPatternGraph(shortestPathSet, augmentedGraph, dbStorage);               
                
                SPARQL q = fgraph.getSparqlQuery(queryPatternGraph, dbStorage, query_prefix);
                
                
                q.setWeightAverageSP(averageSP/combinationsPairs.size());
                q.setWeightLongestSP(longestSP);
                this.sparqlQueryList.add(q);
                
            }
        }
        
        return this.sparqlQueryList;
    }
            
}
