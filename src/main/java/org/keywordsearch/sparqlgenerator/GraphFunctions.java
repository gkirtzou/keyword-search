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
import berkeleydbje.Property;
import berkeleydbje.RdfClass;
import com.sleepycat.persist.EntityCursor;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JFrame;

/**
 *
 * @author penny
 * @author gkirtzou
 */
public class GraphFunctions {
    
    int counter=100;
    
        
    /**
     * Given the class names and the property names of the RDF schema, this function
     * returns the Summary Graph structure. Within the summary graph, every class of the RDF schema as 
     * well as every inter-entity node are represented by graph nodes. 
     * An inter-entity node is a node of which both the subject and the object are classes. 
     * @param classNames HashMap with all the class names of the RDF schema. The keys of the (key, value) pairs 
     * are the class names while the values are empty. 
     * @param propertyNames HashMap with all the property names of the RDF schema.The key
     * in each (key, value) pair of the HashMap, is a property name and the value is the class name 
     * related with the key property. 
     * @return the summary graph structure of the RDF schema
     */
    public UndirectedSparseGraph getSummaryGraph(HashMap<String,String> classNames, HashMap<String,String> propertyNames){
        
        UndirectedSparseGraph summaryGraph = new UndirectedSparseGraph();
        
        //Insert all the class nodes in the summary graph
        Set set1 = classNames.entrySet();
        Iterator i1 = set1.iterator();
        while(i1.hasNext()) {
            Map.Entry currentClass = (Map.Entry)i1.next();
            summaryGraph.addVertex(currentClass.getKey());
        }
        
        //Insert the property nodes in the summary graph. 
        //Only the properties that have entity objects are applicable.
        Set set2 = propertyNames.entrySet();
        Iterator i2 = set2.iterator();
        while(i2.hasNext()) {   
            Map.Entry currentClass = (Map.Entry)i2.next();
            String property = (String)currentClass.getKey();
            Set<String[]> set = (Set)currentClass.getValue();
            for(String[] strArray : set) {
                //check that the property has an object that is a class
                if(strArray.length==2){
                    String subject=strArray[0];
                    String object=strArray[1];
                    
                    //Add the property node if it doesn't already exist
                    if(!summaryGraph.containsVertex(property))
                        summaryGraph.addVertex(property);
                    
                    //Add the related edges, if they doesn't already exist
                    if(!summaryGraph.containsEdge(subject+":"+property))
                        summaryGraph.addEdge(subject+":"+property, subject, property);
                    
                    if(!summaryGraph.containsEdge(property+":"+object))
                        summaryGraph.addEdge(property+":"+object, property, object);
                }
            }
        }     
        
        
        return summaryGraph;
    }
    
    /**
     * Given the the BerkeleyDBStorage object of the RDF schema, this function
     * returns the Summary Graph structure.  
     * @param dbStorage A BerkeleyDB structure containing all the necessary information from the RDF schema. 
     * @return the summary graph structure of the RDF schema
     */
    public UndirectedSparseGraph getSummaryGraph(BerkeleyDBStorage dbStorage){
        
        UndirectedSparseGraph summaryGraph = new UndirectedSparseGraph();
        
        //Insert all the class nodes in the summary graph
        EntityCursor<RdfClass> classNames = dbStorage.getClassCursor();
        for(RdfClass currentClass : classNames){
            //Create a class node and add to the graph
            GraphNode n = new GraphNode("C", currentClass.getClassName());
            summaryGraph.addVertex(n);
        }

        int counter=0;
        //Insert the property nodes in the summary graph. 
        //Only the properties that have entity objects are applicable.        
        EntityCursor<Property> propertyNames = dbStorage.getPropertyCursor();
        for(Property currentProperty : propertyNames){
            String property = (String)currentProperty.getPropertyName();
            Set<String[]> set = (Set)dbStorage.getProperty(property);
            for(String[] strArray : set) {
                //check that the property has an object that is a class
                if(strArray.length==2){
                    String subject=strArray[0];
                    String object=strArray[1];
                    
                    GraphNode n = new GraphNode("E", property, subject, property, object, "");
                    
                    //Add the property node if it doesn't already exist
                    if(!summaryGraph.containsVertex(n)){
                        summaryGraph.addVertex(n);
                        
                        //Add the related edges, if they doesn't already exist
                        //create the subject node
                        GraphNode ns = new GraphNode("C", subject);
                        if(summaryGraph.findEdge(ns, n)==null){
                            summaryGraph.addEdge("edge"+counter, ns, n);
                            counter++;
                        }
                    
                        //create the object node
                        GraphNode no = new GraphNode("C", object);
                        if(summaryGraph.findEdge(n, no)==null){
                            summaryGraph.addEdge("edge"+counter, n, no);
                            counter++;
                        } 
                    }
                }
            }
        }     

        return summaryGraph;
    }
    
    /**
     * This functions gets a keyword combination and processes the Summary Graph as
     * described below:
     * For each keyword combination component, it checks the type of match to the HashMaps.
     * If the keyword matches a class or inter-entity property, it is ignored because it is
     * already added in the summary graph. If the keyword matches an entity-to-attribute property, 
     * then an entity-to-attribute property node is created and added to the graph. If the keyword 
     * matches a literal, then a literal node and property node for the related property are created 
     * and added in the graph.
     * The graph that occurs after all the above additions is the Augmented Graph of the Keyword Search 
     * Algorithm.
     * @param keyCombination A combination of keyword matches.
     * @param summaryGraph The summary Graph
     * @param classNames The class names HashMap
     * @param propertyNames The property names HashMap
     * @return The Augmented Graph of the Keyword Search Algorithm for this keyword match combination.
     */
    public UndirectedSparseGraph getSingleAugmentedGraph(HashMap keyCombination, UndirectedSparseGraph summaryGraph, 
            HashMap classNames, HashMap propertyNames){
        
        UndirectedSparseGraph augmentedGraph = new UndirectedSparseGraph();
        
        //First of all, initialize the current augmentet graph with the summary graph\
        augmentedGraph=getSummaryGraph(classNames, propertyNames);
               
        //Process each keyword match from the current match combination
        Set set = keyCombination.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry currentCombi = (Map.Entry)i.next();
            String cKey = (String)currentCombi.getKey();
            String cType=cKey.substring(0, 1);
            cKey=cKey.substring(cKey.indexOf(":")+1);
            cKey=cKey.trim();
            
            
            switch (cType) {
                case "P":
                    //Get the related class of this property
                    String curClass=(String)currentCombi.getValue();
                    //Check if this node already has a neighbour that represents the current property.
                    //If not, add the node to the graph and also add an empty attribute node to this property.
                    if(!(augmentedGraph.containsVertex(cKey) && augmentedGraph.isNeighbor(curClass, cKey))){
                        augmentedGraph.addVertex(cKey);
                        augmentedGraph.addEdge(curClass+":"+cKey, curClass, cKey);
                        counter++;
                    } 
                    
                    break;
                case "L":
                    //Get the related property and class of this literal
                    String curData=(String)currentCombi.getValue();
                    String curProp=curData.substring(0, curData.indexOf(","));
                    curProp=curProp.substring(curProp.indexOf(":")+1);
                    curClass=curData.substring(curData.indexOf(",")+1);
                    curClass=curClass.substring(curClass.indexOf(":")+1);
                    
                    //Check if the class node and property node are already neighbours
                    //If not, add the property node to the graph 
                    if(!(augmentedGraph.containsVertex(curProp) && augmentedGraph.isNeighbor(curClass, curProp))){
                        augmentedGraph.addVertex(curProp);
                        augmentedGraph.addEdge(curClass+":"+curProp, curClass, curProp);
                        counter++;
                    }
                    //If the literal does not already exist in the graph, add it
                    if(!augmentedGraph.containsVertex(cKey)){
                        augmentedGraph.addVertex(cKey);
                        augmentedGraph.addEdge(curProp+":"+cKey, curProp, cKey);
                        counter++;
                    }
 
                    break;
            }
        }
        
        return augmentedGraph;
    }
    
    /**
     * This functions gets a keyword combination and processes the Summary Graph as
     * described below:
     * For each keyword combination component, it checks the type of match to the HashMaps.
     * If the keyword matches a class or inter-entity property, it is ignored because it is
     * already added in the summary graph. If the keyword matches an entity-to-attribute property, 
     * then an entity-to-attribute property node is created and added to the graph. If the keyword 
     * matches a literal, then a literal node and property node for the related property are created 
     * and added in the graph.
     * The graph that occurs after all the above additions is the Augmented Graph of the Keyword Search 
     * Algorithm.
     * @param keyCombination A combination of keyword matches.
     * @param summaryGraph The summary Graph
     * @param dbStorage A BerkeleyDB structure containing all the necessary information from 
     * the RDF schema.
     * @return The Augmented Graph of the Keyword Search Algorithm for this keyword match combination.
     */
    public UndirectedSparseGraph getSingleAugmentedGraph(HashMap keyCombination, UndirectedSparseGraph summaryGraph, 
            BerkeleyDBStorage dbStorage){
        
        //First of all, initialize the current augmented graph with the summary graph
        UndirectedSparseGraph augmentedGraph=getSummaryGraph(dbStorage);
        int counter=augmentedGraph.getEdgeCount(EdgeType.UNDIRECTED);
        counter++;
               
        //Process each keyword match from the current match combination
        Set set = keyCombination.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry currentCombi = (Map.Entry)i.next();
            String Key = (String)currentCombi.getKey();
            String cType=Key.substring(0, 1);
            String cKey=Key.substring(Key.indexOf(":")+1);
            cKey=cKey.trim();
            
            switch (cType) {
                case "P":
                    //Get the related class of this property
                    String curClass=(String)currentCombi.getValue();

                    //Create the node 
                    GraphNode n = new GraphNode("P", cKey, curClass, cKey, "?", "" );
                    
                    //If the property does not already exist in the graph, add it
                    if(!augmentedGraph.containsVertex(n)){
                        augmentedGraph.addVertex(n);
                        //create the subject node
                        GraphNode ns = new GraphNode("C", curClass);
                        if(augmentedGraph.findEdge(ns, n)==null){
                            augmentedGraph.addEdge("edge"+counter, ns, n);
                            counter++;
                        }
                    } 
                    break;
                case "L":
                    //Get the related property and class of this literal
                    String curData=(String)currentCombi.getValue();
                    String curProp=curData.substring(0, curData.indexOf(","));
                    curProp=curProp.substring(curProp.indexOf(":")+1);
                    curClass=curData.substring(curData.indexOf(",")+1);
                    curClass=curClass.substring(curClass.indexOf(":")+1);
                    
                    //Create the literal node and the related property node
                    GraphNode np = new GraphNode("P", curProp, curClass, curProp, cKey, Key.substring(1, 2)) ;
                    GraphNode nl = new GraphNode("L", cKey, curClass, curProp, cKey, Key.substring(1, 2));
                        
                    
                    //If the property node does not already exist in the graph, add it
                    if(!augmentedGraph.containsVertex(np)){
                        augmentedGraph.addVertex(np);
                        
                        //Add also the related adge
                        //create the subject node
                        GraphNode ns = new GraphNode("C", curClass);
                        //if(!augmentedGraph.containsEdge(curClass+":"+curProp)){
                        if(augmentedGraph.findEdge(ns, np)==null){
                            augmentedGraph.addEdge("edge"+counter, ns, np);
                            counter++;
                        }
                    }
                    //If the literal node does not already exist in the graph, add it
                    if(!augmentedGraph.containsVertex(nl)){
                        augmentedGraph.addVertex(nl);
                        
                        //Add also the related adge
                        if(augmentedGraph.findEdge(np, nl)==null){
                            augmentedGraph.addEdge("edge"+counter, np, nl);
                            counter++;
                        }
                    }
                    break;
            }
        }
        
        return augmentedGraph;
    }
    
    
    /**
     * This function returns the shortest path between two keywords on the augmented graph.
     * The two keywords are represent by graph nodes.
     * @param augmGraph The augmented graph for these keywords 
     * @param key1 The first keyword
     * @param key2 The second keyword
     * @return The shortest path
     */
    public Map getShortestPath(UndirectedSparseGraph augmGraph, String key1, String key2){
        
        Set<Map> shortestPathSet = new HashSet<>();
        
        UnweightedShortestPath shrtPathObject = new UnweightedShortestPath(augmGraph);
        Map shortestPaths = shrtPathObject.getIncomingEdgeMap(key1);
        Map currentShortestPath = new HashMap();
        
        int distance=shrtPathObject.getDistance(key1, key2).intValue();
        //Start from the second keyword and get the edge of its shortest path
        String curNode=key2;
        String prevEdge="";
        while(distance>=0){
            String curEdge=(String)shortestPaths.get(curNode);
            currentShortestPath.put(curNode, curEdge);

            //Get the next node from the augmented graph. The next node is the
            //other end of curEdge. 
            try{
                Object str1=augmGraph.getOpposite(curNode, curEdge);
                curNode=str1.toString();
            }
            catch(NullPointerException e){
                currentShortestPath.put(key1, prevEdge);
            }

            prevEdge=curEdge;
            distance--;                  
        }

        return currentShortestPath;
    }
       
    /**
     * This function returns the shortest path between two keyword nodes of the augmented graph.
     * The two nodes represent user keywords.
     * @param augmGraph The augmented graph for these keywords
     * @param n1 The keyword node 1
     * @param n2 The keyword node 2 
     * @return The shortest path
     */
    public Map getShortestPath(UndirectedSparseGraph augmGraph, GraphNode n1, GraphNode n2){
        
        Set<Map> shortestPathSet = new HashSet<>();
        
        UnweightedShortestPath shrtPathObject = new UnweightedShortestPath(augmGraph);
        Map shortestPaths = shrtPathObject.getIncomingEdgeMap(n1);
        Map currentShortestPath = new HashMap();
        
        
        Map m = shrtPathObject.getDistanceMap(n1);
        int distance=shrtPathObject.getDistance(n1, n2).intValue();
        //Start from the second keyword and get the edge of its shortest path
        GraphNode curNode=n2;
        String prevEdge="";
        while(distance>=0){
            String curEdge=(String)shortestPaths.get(curNode);
            currentShortestPath.put(curNode, curEdge);

            //Get the next node from the augmented graph. The next node is the
            //other end of curEdge. 
            try{
                Object node=augmGraph.getOpposite(curNode, curEdge);
                curNode=(GraphNode) node;
            }
            catch(NullPointerException e){
                currentShortestPath.put(n1, prevEdge);
            }

            prevEdge=curEdge;
            distance--;                  
        }

        return currentShortestPath;
    }
        
    /**
     * This function merges the shortest paths of all keyword pairs of a keyword combination
     * into one new graph the queryPatternGraph. This graph will be used in order to form the SPARQL 
     * query for this keyword combination.
     * @param shortestPathSet The set with all the shortest paths for this keyword combination.
     * @param augmentedGraph The augmented graph for this keyword combination.
     * @param propertyNames The property names HashMap.
     * @return The query pattern graph.
     */
    public UndirectedSparseGraph getQueryPatternGraph(Set<Map> shortestPathSet, UndirectedSparseGraph augmentedGraph, 
            HashMap propertyNames){
        
        UndirectedSparseGraph queryPatternGraph = new UndirectedSparseGraph();
        int counter=0;
        
        for(Map shortestPath : shortestPathSet) {
            
            //Get the keys of the Map. The keys represent the nodes of the current shortest path.
            //Insert these nodes to the query pattern graph.
            Set<String> nodes = shortestPath.keySet();
            for(String node : nodes) {
                if(!queryPatternGraph.containsVertex(node)){
                    queryPatternGraph.addVertex(node);
                }
            }
           
            //Iterate the keys again. For each key, get the corresponding value. 
            //The values represent the edges of the current shortest path. 
            //They will be added in the queryPatternGraph.
            for(String node : nodes) {
                String edge = (String) shortestPath.get(node);
                if(!queryPatternGraph.containsEdge(edge)){
                    Collection vertices = augmentedGraph.getIncidentVertices(edge);
                    queryPatternGraph.addEdge(edge, vertices);
                }
            }
                     
        }
        
        //At the end, check the leaf nodes. Leaf nodes are the nodes with only one neighbour.
        //For each leaf node that is a property, add a variable node and a connecting edge.
        Collection<String> vertices = queryPatternGraph.getVertices();
        List l = new ArrayList(vertices);
        for (Iterator<String> it = l.iterator(); it.hasNext();) {
            String node = it.next();
            int cnt = queryPatternGraph.getNeighborCount(node);
            if(cnt==1 && propertyNames.containsKey(node)){
                queryPatternGraph.addVertex("?"+counter);
                queryPatternGraph.addEdge(node+":?"+counter, node, "?"+counter);
            }
        }
                
        return queryPatternGraph;
    }   
    
    /**
     * 
     * This function merges the shortest paths of all keyword pairs of a keyword combination
     * into one new graph the queryPatternGraph. This graph will be used in order to form the SPARQL 
     * query for this keyword combination.
     * @param shortestPathSet The set with all the shortest paths for this keyword combination.
     * @param augmentedGraph The augmented graph for this keyword combination.
     * @param dbStorage A BerkeleyDB structure containing all the necessary information from 
     * the RDF schema.
     * @return The query pattern graph.
     */
    public UndirectedSparseGraph getQueryPatternGraph(Set<Map> shortestPathSet, UndirectedSparseGraph augmentedGraph, 
            BerkeleyDBStorage dbStorage){
        
        UndirectedSparseGraph queryPatternGraph = new UndirectedSparseGraph();
        Set<GraphNode> variableNodes = new HashSet();
        int counter=0;
        
        for(Map shortestPath : shortestPathSet) {
            
            //Get the keys of the Map. The keys represent the nodes of the current shortest path.
            //Insert these nodes to the query pattern graph.
            Set<GraphNode> nodes = shortestPath.keySet();
            for(GraphNode node : nodes) {
                if(node.getIsType().equals("P") && !queryPatternGraph.containsVertex(node) && !node.getObject().equals("?")){
                    queryPatternGraph.addVertex(node);
                    
                    //create the subject node and the related edge
                    GraphNode sn = new GraphNode("C", node.getSubject());
                    GraphNode on = new GraphNode("L", node.getObject(), node.getSubject(), node.getProperty(), node.getObject(), node.getObjectFilter());
                    
                    
                    if(!queryPatternGraph.containsVertex(sn)){
                        queryPatternGraph.addVertex(sn);
                    }
                    if(queryPatternGraph.findEdge(sn, node)==null){
                        queryPatternGraph.addEdge("edge"+counter, sn, node);
                        counter++;
                    }
                    
                    if(!queryPatternGraph.containsVertex(on)){
                        queryPatternGraph.addVertex(on);
                    }
                    if(queryPatternGraph.findEdge(node, on)==null){
                        queryPatternGraph.addEdge("edge"+counter, node, on);
                        counter++;
                    }
                    
                    //create the object node and the related edge
                }
                else if(node.getIsType().equals("P") && node.getObject().equals("?")){
                    variableNodes.add(node);                    
                }
                else if(node.getIsType().equals("E") && !queryPatternGraph.containsVertex(node)){
                    queryPatternGraph.addVertex(node);
                    
                    //create the subject node and the related edge
                    GraphNode sn = new GraphNode("C", node.getSubject());
                    GraphNode on = new GraphNode("C", node.getObject());
                    
                    if(!queryPatternGraph.containsVertex(sn)){
                        queryPatternGraph.addVertex(sn);
                    }
                    if(queryPatternGraph.findEdge(sn, node)==null){
                        queryPatternGraph.addEdge("edge"+counter, sn, node);
                        counter++;
                    }
                    
                    if(!queryPatternGraph.containsVertex(on)){
                        queryPatternGraph.addVertex(on);
                    }
                    if(queryPatternGraph.findEdge(node, on)==null){
                        queryPatternGraph.addEdge("edge"+counter, node, on);
                        counter++;
                    }
                    
                }
            }
            
            //At the end, process the property nodes that have a variable object.
            //For each of them, check if there is already such a variable node with a literal object.
            for(GraphNode gn : variableNodes){
                //indicator of whether the variable node will be added or not
                int found=0;
                
                Collection<GraphNode> nodeCollection = queryPatternGraph.getVertices();
                for(GraphNode queryNode : nodeCollection){
                    if(queryNode.getIsType().equals("P") && 
                            queryNode.getNodeName().equals(gn.getNodeName()) &&
                            queryNode.getSubject().equals(gn.getSubject()) &&
                            queryNode.getProperty().equals(gn.getProperty()) &&
                            !queryNode.getObject().equals("?")){
                        found=1;
                    }                    
                }
                
                //Add the variable node if necessary
                if(found==0){
                    //create the subject node
                    GraphNode sn = new GraphNode("C", gn.getSubject());
                    if(!queryPatternGraph.containsVertex(sn)){
                        queryPatternGraph.addVertex(sn);
                    }
                    //add the property node
                    queryPatternGraph.addVertex(gn);
                    
                    //add the related edge
                    if(queryPatternGraph.findEdge(sn, gn)==null){
                        queryPatternGraph.addEdge("edge"+counter, sn, gn);
                        counter++;
                    }
                    
                }
            }
                     
        }
                        
        return queryPatternGraph;
    }   
    
    /**
     * This function transforms the query pattern graph into a SPARQL query.
     * @param queryPatternGraph The query pattern graph.
     * @param classNames The class names HashMap.
     * @param propertyNames The property names HashMap.
     * @param query_prefix The RDF schema vocabulary.
     * @return A SPARQL query in the form of a String[].
     */
    public String[] getSparqlQuery(UndirectedSparseGraph queryPatternGraph, HashMap classNames, HashMap propertyNames, 
            String query_prefix){
        
        int counter=0;
        
        Set<String> triplets = new HashSet();
        Set<String> filters = new HashSet();
        HashMap variables = new HashMap();
        
        //Get all the nodes from the query pattern graph
        Collection<String> vertices = queryPatternGraph.getVertices();
        List l = new ArrayList(vertices);
        for (Iterator<String> it = l.iterator(); it.hasNext();) {
            String node = it.next();
            
            String subject="";
            String object="";
            String property=query_prefix+":"+node;
            
            //Process only if the current node is a property node
            if(propertyNames.containsKey(node)){
                
                //Get all the edges for this node
                Collection<String> edges = queryPatternGraph.getInEdges(node);
                List l2 = new ArrayList(edges);
                for (Iterator<String> it2 = l2.iterator(); it2.hasNext();) {
                    String edge = it2.next();
                    
                    //Process the subject of the triplet
                    if(!edge.substring(0, edge.indexOf(":")).equals(node)){
                        String className=edge.substring(0, edge.indexOf(":"));
                        //Assign a variable to the subject of the triplet.
                        //keep the name of the variable in a HashTable for reference.
                        if(!variables.containsKey(className)){
                            subject="?"+edge.substring(0,1)+counter;
                            variables.put(className, subject);
                            counter++;
                            
                            //Add an extra triplet explaining that the subject refers to this class
                            if(classNames.containsKey(className)){
                                triplets.add(subject+" a "+query_prefix+":"+className+".");
                            }
                        }
                        else{
                            subject=(String) variables.get(className);
                        }

                    }
                    //Process the object of the triplet
                    else{
                        
                        String objectName=edge.substring(edge.indexOf(":")+1);
                        
                        object=edge.substring(edge.indexOf(":")+1);
                        //If the object is already a variable
                        if(objectName.subSequence(0, 1)=="?"){
                            object=objectName;
                        }
                        //If the object is a class or a literal
                        else{
                            //Object is a class
                            if(classNames.containsKey(objectName)){
                                //Assign a variable to the object and keep a reference in Hashtable
                                //Also, add a triplet explaining that the object refers to this class
                                if(!variables.containsKey(objectName)){
                                    object="?"+objectName.substring(0,1)+counter;
                                    variables.put(objectName, object);
                                    counter++;
                                    
                                    triplets.add(object+" a "+query_prefix+":"+objectName+".");
                                }
                                else{
                                    object=(String) variables.get(objectName);
                                }
                                
                            }
                            //Object is a literal
                            else{
                                //Assign a variable to the literal object
                                object="?l"+counter;
                                counter++;
                                
                                //Create a FILTER condition for the SPARQL query
                                if(!objectName.substring(0,1).equals("?"))
                                    filters.add("FILTER (str("+object+") = \""+objectName+"\").");
                            }
                        
                        }                      
                        
                    }
                
                }
                
                //Now that the subject, property and object are known, create the triplet.
                triplets.add(subject+" "+property+" "+object+".");
                               
            }
            
        }
        
        //Create a String[] with the lines of the query.
        String[] sparqlQuery = new String[triplets.size()+filters.size()+2];
        sparqlQuery[0]="SELECT DISTINCT * WHERE {";
        
        //Add the triplets to the query array
        int i=1;
        for(String triplet : triplets){
            sparqlQuery[i]=triplet;
            i++;
        }
        for(String filter : filters){
            sparqlQuery[i]=filter;
            i++;
        }
        //At the end, add the closing }
        sparqlQuery[i]="}";
        
        return sparqlQuery;
    }
    
    /**
     * This function transforms a query pattern graph into a SPARQL query.
     * @param queryPatternGraph The query pattern graph.
     * @param dbStorage A BerkeleyDB structure containing all the necessary information from 
     * the RDF schema.
     * @param query_prefix The RDF schema vocabulary.
     * @return A SPARQL query in the form of a String[].
     */
    public SPARQL getSparqlQuery(UndirectedSparseGraph queryPatternGraph, BerkeleyDBStorage dbStorage, 
            String query_prefix){
        
        int counter=0;
        
        Set<String> triplets = new HashSet();
        Set<String> filters = new HashSet();
        HashMap variables = new HashMap();

        
        //Get all the nodes from the query pattern graph
        Collection<GraphNode> vertices = queryPatternGraph.getVertices();
        List l = new ArrayList(vertices);
        for (Iterator<GraphNode> it = l.iterator(); it.hasNext();) {
            GraphNode node = it.next();           
            
            //Process only if the current node is a property node
            String nodeType = node.getIsType();
            if(nodeType.equals("P") || nodeType.equals("E")){
                
                String subject="";
                String object="";
                String subjectClass = node.getSubject();
                String property= query_prefix+":"+node.getProperty(); 
                String objectClass="";
                       
                //PROCESS THE SUBJECT
                //Assign a variable to the subject of the triplet.
                //keep the name of the variable in a HashTable for reference.
                if(!variables.containsKey(subjectClass)){
                    subject="?"+subjectClass.substring(0,1)+counter;
                    variables.put(subjectClass, subject);
                    counter++;
                            
                    //Add an extra triplet explaining that the subject refers to this class
                    if(dbStorage.containsClass(subjectClass)){
                        triplets.add(subject+" a "+query_prefix+":"+subjectClass+".");
                    }
                }
                else{
                    subject=(String) variables.get(subjectClass);
                }
            
                //PROCESS THE OBJECT
                if(nodeType.equals("E")){
                    objectClass = node.getObject();
                    if(!variables.containsKey(objectClass)){
                        object="?"+objectClass.substring(0,1)+counter;
                        variables.put(objectClass, object);
                        counter++;
                        
                        //Add an extra triplet explaining that the subject refers to this class
                        if(dbStorage.containsClass(objectClass)){
                            triplets.add(object+" a "+query_prefix+":"+objectClass+".");
                        }
                    }
                    else{
                        object=(String) variables.get(objectClass);
                    }
                }
                else{
                    String objectName = node.getObject();
                    //process a variable node
                    if(objectName.equals("?")){
                        object = "?v"+counter;
                        counter++;
                    }
                    //process a literal node
                    else{
                        object = "?v"+counter;
                        counter++;
                        //Add a filter for this literal
                        if (node.getObjectFilter().equals("A")) {
                            //filters.add("FILTER (str("+object+") >= \""+objectName+"\").");
                            filters.add("FILTER (xsd:integer("+object+") >= "+objectName+").");
                        }
                        else if (node.getObjectFilter().equals("B")) {
                            //filters.add("FILTER (str("+object+") <= \""+objectName+"\").");
                            filters.add("FILTER (xsd:integer("+object+") <= "+objectName+").");
                        }
                        else { 
                            filters.add("FILTER (str("+object+") = \""+objectName+"\").");
                            
                        }
                    }

                }
                
                //Now that the subject, property and object are known, create the triplet.
                triplets.add(subject+" "+property+" "+object+".");
            }
            
        }

        //Create a String[] with the lines of the query.
        String[] sparqlQuery = new String[triplets.size()+filters.size()+2];
        sparqlQuery[0]="SELECT DISTINCT * WHERE {";
        
        //Add the triplets to the query array
        int i=1;
        for(String triplet : triplets){
            sparqlQuery[i]=triplet;
            i++;
        }
        for(String filter : filters){
            sparqlQuery[i]=filter;
            i++;
        }
        //At the end, add the closing }
        sparqlQuery[i]="}";
        
        
        return new SPARQL(sparqlQuery, triplets.size());
    }

    /**
     * This function returns a SPARQL query from a single match. This applies to the
     * Keyword Match Algorithm when the user has searched with only one keyword.
     * @param currentMatch The keyword match that is currently processed.
     * @param query_prefix The RDF schema vocabulary.
     * @return A SPARQL query in the form of String[].
     */
    // Last modified by @gkirtzou
    public SPARQL getSparqlQuery(KeywordMatch  currentMatch, String namedGraph, String query_prefix){
         
        Set<String> triplets = new HashSet();
        Set<String> filters = new HashSet();
    
        if (currentMatch instanceof MatchRdfClass) { 
        	// Match to RDF Class
        	MatchRdfClass match = (MatchRdfClass) currentMatch;
        	triplets.add("?s a <" + match.getReferenceMatch()  + ">.");
        }
        else if (currentMatch instanceof MatchPropertyLiteral) { 
        	// Match to Property-to-Literal 
        	MatchPropertyLiteral match = (MatchPropertyLiteral) currentMatch;
        	// Add triplets
           	triplets.add("?s <" + match.getReferenceMatch() + "> ?o.");
           	if (!match.getSubjClass().equals("null")) {
           		triplets.add("?s a <" + match.getSubjClass()  + ">.");
           	}           	
       	}
        else if (currentMatch instanceof MatchPropertyClass) { 
        	// Match to Property-to-Class
        	MatchPropertyClass match = (MatchPropertyClass) currentMatch;
        	// Add triplets
           	if (!match.getSubjClass().equals("null") || match.getObjClass().equals("null")) {
           		triplets.add("?s <" + match.getReferenceMatch() + "> ?o.");
           		if (! match.getSubjClass().equals("null")) {
           			triplets.add("?s a <" + match.getSubjClass()  + ">.");
           		}
           		if (! match.getObjClass().equals("null")) {
           			triplets.add("?o a <" + match.getObjClass()  + ">.");
           		}
           	}       
        }
        else if (currentMatch instanceof MatchLiteral) { // Match to Literal
        	MatchLiteral match = (MatchLiteral) currentMatch;
        	// Add triplets
        	triplets.add("?s a <" + match.getSubClass() + ">.");
        	if(! match.getLanguage().equals("null")) { 
        		triplets.add("?s <" + match.getProperty() + "> \""+ match.getReferenceMatch() + "\"@"+ match.getLanguage() +  ".");  
        	}
        	if(! match.getDatatype().equals("null")) { 
        		triplets.add("?s <" + match.getProperty() + "> \""+ match.getReferenceMatch() + "\"^^<"+ match.getDatatype() +  ">.");  
        	}
        }
       
       
        	
/*      /// TEMPORAL OPERATORs !!!!
    	String propertyName = litDetails.substring(0, litDetails.indexOf(","));
        	propertyName = propertyName.substring(propertyName.indexOf(":") + 1);
        	String className = litDetails.substring(litDetails.indexOf(",") + 1);
        	className = className.substring(className.indexOf(":") + 1);
        	
        	triplets.add("?s " + query_prefix + ":" + propertyName + " ?o.");
        	triplets.add("?s a " + query_prefix + ":" + className + ".");
        	//System.out.println(k.substring(1, 2));
        	switch (k.substring(1, 2)) {
        	case "E":
        		filters.add("FILTER (str(?o) = \"" + k.substring(k.indexOf(":") + 1) + "\").");
                        break;
        	case "B":
        		//filters.add("FILTER (str(?o) <= \"" + k.substring(k.indexOf(":") + 1) + "\").");
        		filters.add("FILTER (xsd:integer(?o) <= " + k.substring(k.indexOf(":") + 1) + ").");
        		break;
        	case "A":
        		//filters.add("FILTER (str(?o) >= \"" + k.substring(k.indexOf(":") + 1) + "\").");
        		filters.add("FILTER (xsd:integer(?o) >= " + k.substring(k.indexOf(":") + 1) + ").");
        		break;
             }*/
       // }
               
        
                   
        // Form query
        String[] sparqlQuery = null;
        SPARQL querySp = null;
        if (triplets.size() > 0) {
	        sparqlQuery = new String[triplets.size() + filters.size() + 2];
	        sparqlQuery[0]="SELECT DISTINCT * ";
	        if (namedGraph != null && !namedGraph.equals("")) {
	        	//System.out.println(namedGraph);
	        	sparqlQuery[0] = sparqlQuery[0] + " FROM <" + namedGraph + "> ";
	        }
	        sparqlQuery[0] = sparqlQuery[0] + "WHERE { ";
	        int counter = 1;
	        for(String t : triplets){
	            sparqlQuery[counter] = t;
	            counter++;
	        }
	        
	        for(String f : filters){
	            sparqlQuery[counter] = f;
	            counter++;
	        }
	        sparqlQuery[counter] = "} LIMIT 10";
	        querySp = new SPARQL(sparqlQuery, triplets.size());
        }
                        
        return querySp;
    }
    

    /**
     * This function displays a graph.
     * @param graph The graph that will be displayed.
     * @param width The width of the graph window (e.g. 1100).
     * @param height The height of the graph window (e.g. 700).
     */
    public void graphDisplay(UndirectedSparseGraph graph, int width, int height){
        
        VisualizationImageServer vs =
            new VisualizationImageServer(
                new CircleLayout(graph), new Dimension(width, height));
        vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
 
        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }

    
    /**
     * This function returns a specific graph node based on specific information.
     * This is necessary in order to define the shortest paths between two nodes of the 
     * graphs.
     * @param key First part of the node information that refers to the keyword.
     * @param info Second part of the node information that refers to the node type and
     * other details.
     * @return A graph node object.
     */
    public GraphNode getShortestPathEndNode(String key, String info) {
        
        String nodeName = key.substring(key.indexOf(":")+1);
        GraphNode endNode = new GraphNode(nodeName);
        
        //Define the type of the node
        String isType = key.substring(0, 1);
        endNode.setIsType(isType);
        
        switch(isType){
            case "C":
                break;
            case "E":
                endNode.setSubject(info.substring(0, info.indexOf(":")));
                endNode.setProperty(key.substring(key.indexOf(":")+1));
                endNode.setObject(info.substring(info.indexOf(":")+1));
                break;
            case "P":
                endNode.setSubject(info);
                endNode.setProperty(nodeName);
                endNode.setObject("?");
                break;
            case "L":
                String[] split = info.split(",");
                endNode.setProperty(split[0].substring(split[0].indexOf(":")+1));
                endNode.setSubject(split[1].substring(split[1].indexOf(":")+1));
                endNode.setObject(nodeName);
                endNode.setObjectFilter(key.substring(1, 2));
                break;
        }
       
        return endNode;
    }
                
}