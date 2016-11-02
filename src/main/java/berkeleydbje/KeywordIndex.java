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
package berkeleydbje;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import org.keywordsearch.sparqllib.*;


/**
 * This class contains methods used to obtain RDF information
 * by querying to a SPARQL endpoint. Afterwards this set of data 
 * is used to build the Keyword Index.
 * @author fil
 * @author gkirtzou
 */
public class KeywordIndex {
     
    /**
     * Retrieves all the RDF classes from schema
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @param namedGraph The named graph to explore. If null, explore the whole dataset
     *                   of the endpoint.
     * @return The set of the RDF class names
     */
	//Last Modified by Gkirtzou 
    public Set<String> getRDFClasses(String prefixes,String serverEndpoint, String namedGraph)
    {  
        // Set SPARQL query to get data related to class 
        String query = null;
        if (namedGraph != null) {
            query = "SELECT distinct ?c FROM <" + namedGraph + "> WHERE {?s a ?c} limit 10"; 
        }
        else {
            query = "SELECT distinct ?c WHERE {?s a ?c}"; 
        }
              
        // Run SPARQL Query
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        
        // Get RDF class URIs
        Set<String> classnames=new HashSet<String>();
        for (QuerySolution s : qr.getResultSet()){
            String rdfClass=s.get("?c").toString().trim();
            classnames.add(rdfClass);
        }
              
        return classnames;
    }
    
    /**
     * Retrieves all the RDF classes from schema
     * @param csvFile
     * @return The set of the RDF class names
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @author gkirtzou
     */
    // Last Modified by Gkirtzou 
    public Set<String> getRDFClasses(String csvFile)
            throws FileNotFoundException, IOException
    {
        Set<String> classnames=new HashSet<String>();
        BufferedReader br = null;
        String line = "";
        
        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            classnames.add(line.replace("\"", ""));
        }
        br.close();
        return (classnames);
    }

    /**
     * Retrieves all the RDF properties from schema and
     * range/domain if available.
     * @param csvFile the path to csvFile
     * @return The set of the RDF properties and their description <type, domain, range>
     */
    // Last Modified by Gkirtzou 
    public Map<String, Set<String[]>> getRDFProperties(String csvFile)
            throws FileNotFoundException, IOException, Exception {
        
        Map<String, Set<String[]>> properties = new HashMap<>(); 
        String line = "";
       
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            ///////////// 
        	// Extract RDF property description, ie name, type, and range/domain if available
        	//System.out.println(line);
        	String[] splits = line.split(",");
        	assert splits.length != 6;
            
            String property = splits[0].replace("\"", "");
            String domain = "null";
            String range = "null";
            String type = "null";
            if (!splits[1].equals("")) {
                domain = splits[1].replace("\"", "");
            }
            if(!splits[2].equals("")) {
                range = splits[2].replace("\"", "");
            }
            if (splits[4].equals("1") && splits[5].equals("0")) {
                type = "L"; // Literal type property or Attribute-To-Entity Property 
            }             
            else  if (splits[4].equals("0") && splits[5].equals("1")) {
                type = "O"; // Object type property or Inter-Entities Property 
            }
            else { 
            	throw new Exception("Line contains a property that is attribute and relation simultaneously!");
            	
            }
            if (splits[4].equals("1")) {
            	if (!splits[3].equals("")) {
                range = splits[3].replace("\"", "");
            	}
        	}
            ////////
            // Add the RDF property description to the result set
            boolean containsProperty = properties.containsKey(property);
            Set<String[]> propertyDefinitions = null;
            if(containsProperty) {
                propertyDefinitions = properties.get(property);
            }
            else if(!containsProperty) {
                propertyDefinitions =  new HashSet<>();                       
            }
            String[] propDescription=new String[3];
            propDescription[0] = type;
            propDescription[1] = domain;
            propDescription[2] = range;
            propertyDefinitions.add(propDescription);                                      
            properties.put(property, propertyDefinitions);
        }
        br.close();
        return properties;
    }
     
    /**
     * Retrieves all the RDF properties from SPARQL endpoint
     * their description, ie. name, type, and range/domain if available.
     * @param serverEndpoint : The SPARQL endpoint to retrieve information
     * @param prefixes : prefixes to provide in the SPARQL query
     * @param namedGraph : the named graph to retrieve from. Null doesn't
     * 			restrict the query.
     * @return The set of the RDF properties and their description <type, domain, range>
     */
    // Last Modified by Gkirtzou 
    public  Map<String, Set<String[]>> getRDFProperties(String prefixes, String serverEndpoint,  String namedGraph) {

        Map<String, Set<String[]>> properties = new HashMap<>(); 
        ///////        
        // Run query to retrieve properties and their description 
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        // connect to SPARQL endpoint
        queryLib.connect(serverEndpoint);  
        // Form query 
        String query = "SELECT distinct ?p ?cS ?cR (datatype(?o) AS ?datatype) (isLiteral(?o) AS ?literalType) (isIRI(?o) AS ?objectType) ";
        if (namedGraph != null && !namedGraph.equals("")) {
        	//System.out.println(namedGraph);
        	query = query + " FROM <" + namedGraph + "> ";
        }
        query = query + "WHERE { ?s ?p ?o. optional {?s rdf:type ?cS} optional {?o rdf:type ?cR} } LIMIT 5";
       // System.out.println(query);
        // Send query and get response
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        ///////
        // Extract results
        for (QuerySolution s : qr.getResultSet()){
        	///////////// 
        	// Extract RDF property description, ie name, type, and range/domain if available
            //System.out.println("QuerySolution " + s);
            String property = "";
            if (s.get("?p") != null) {
            	property = s.get("?p").toString().replace("\"", "");;
            }
            String domain = "";
            if (s.get("?cS") != null) {
            	domain = s.get("?cS").toString().replace("\"", "");;
            }
            String type = "";
            String range = "";
            if (s.get("?literalType") != null && s.get("?literalType").asLiteral().getInt() == 1){
            	type = "L";
            	if (s.get("?datatype") != null) {
            		range = s.get("?datatype").toString().replace("\"", "");;
            	}
            }
            else if (s.get("?objectType") != null &&  s.get("?objectType").asLiteral().getInt() == 1 ) {
            	type = "O";
            	if (s.get("?cO") != null) {
            		range = s.get("?cO").toString().replace("\"", "");;
            	}
            }
            ////////
            // Add the RDF property description to the result set
            boolean containsProperty = properties.containsKey(property);
            Set<String[]> propertyDefinitions = null;
            if(containsProperty) {
                propertyDefinitions = properties.get(property);
            }
            else if(!containsProperty) {
                propertyDefinitions =  new HashSet<>();                       
            }
            String[] propDescription=new String[3];
            propDescription[0] = type;
            propDescription[1] = domain;
            propDescription[2] = range;
            propertyDefinitions.add(propDescription);                                      
            properties.put(property, propertyDefinitions); 
        }
        return properties;
    }
    
    /**
     * Retrieves the RDF literals for a particular class-property pair
     * and a specified vocabulary.
     * @param property The given property name
     * @param className The given RDF class name
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @param namedGraph : the named graph to retrieve from. Null doesn't
     * 			restrict the query.
     * @return The set of the literals found <literal value, language, datatype>
     */
    // Last Modified by Gkirtzou
    public Set<String[]> getRDFLiterals(String property,String className, String prefixes,String serverEndpoint, String namedGraph)  {
    	
    	///////        
        // Run query to retrieve properties and their description 
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        // connect to SPARQL endpoint
        queryLib.connect(serverEndpoint);  
        // Form query 
        String query = "SELECT distinct ?v ";
        if (namedGraph != null && !namedGraph.equals("")) {
        	//System.out.println(namedGraph);
        	query = query + " FROM <" + namedGraph + "> ";
        }
        query = query + "WHERE { ?s a <"+className+">. ?s <"+property+"> ?v . } ";
        query = query + " LIMIT 10";
      //  System.out.println(query);
        
        //////////
        // Send query and get response
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Set<String[]> literals = new HashSet<>();      
        for (QuerySolution s : qr.getResultSet()){
            if(s.get("?v")!= null && s.get("?v").isLiteral()) {
            	String[] litDescription = new String[3];
            	//System.out.println("Answer::" + s.get("?v"));
            	String ss = s.get("?v").asLiteral().getString();
            	ss = ss.replace("\"", "");
            	ss = ss.replace("\\", "");
            	//System.out.println("Value::" + ss);
            	//System.out.println("Language::" + s.get("?v").asLiteral().getLanguage());
            	//System.out.println("Datatype::" + s.get("?v").asLiteral().getDatatypeURI());
            
            	litDescription[0] = ss; //literal value, strip of quotes
            	litDescription[1] = s.get("?v").asLiteral().getLanguage(); // language
            	litDescription[2] = s.get("?v").asLiteral().getDatatypeURI(); // datatype
                literals.add(litDescription);     
            }
        }
           
        return literals;
    }
    
    
    
    
    
    /**
     * Retrieves the RDF properties connecting two entities.
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return A hash map where the key is the property name and
     * the value is a set of referencing class names. The string array
     * inside the set of each map element contains two items. The first
     * item is the class name of the subject and the second one is the 
     * class name of the object entity.
     */
    public Map<String, Set<String[]>> getRDFEntityProperties(String prefixes,String serverEndpoint)
    {  
        //String query = "SELECT DISTINCT ?p ?c WHERE { ?s a ?c. ?s ?p ?o. } ";
        //String query = "SELECT DISTINCT ?c ?p ?d WHERE {{ ?s a ?c. ?s ?p ?o} UNION {?s a ?c. ?s ?p ?o. ?o a ?d} } LIMIT 1"; 
        //String query = "SELECT DISTINCT * WHERE { ?s ?p ?o}  LIMIT 1"; 
        String query = "SELECT DISTINCT ?p ?c ?d WHERE { ?s a ?c. ?s ?p ?o. ?o a ?d}";
        //String query = "SELECT DISTINCT ?p ?c ?o WHERE { ?s a ?c. ?s ?p ?o. }";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Map<String, Set<String[]>> properties = new HashMap<String, Set<String[]>>();
        
        
        for (QuerySolution s : qr.getResultSet()){
            String str1=s.get("?p").toString();
            String str2=s.get("?c").toString();
            String str3=s.get("?d").toString();
            int i=str1.lastIndexOf("/")+1;
            int j=str2.lastIndexOf("/")+1;
            int k=str3.lastIndexOf("/")+1;
            //String object=s.get("?o").toString();
            //boolean isLiteral=s.get("?o").isLiteral();
            String property=str1.substring(i);
            String className=str2.substring(j);
            String object=str3.substring(k);
            System.out.print(s);
            //int classes;
            if(property.contains("rdf-syntax")||property.contains("sameAs")||property.contains("rdf-schema"))
                continue;
            //if(!isLiteral)
           // {
            if(properties.containsKey(property))
            {
                Set<String[]> newClassNames = new HashSet<String[]>();
                newClassNames=properties.get(property);
                String[] classNames=new String[2];
                
                    //String[] classNames=new String[1];
                    classNames[0]=className;
                    classNames[1]=object;
                //System.out.print(classNames);
                newClassNames.add(classNames);
                properties.put(property, newClassNames);
            }
            else
            {
                Set<String[]> newClassNames = new HashSet<String[]>();
                String[] classNames=new String[2];
                //System.out.print(className);
               
                    //String[] classNames=new String[1];
                    classNames[0]=className;
                    classNames[1]=object;
                    newClassNames.add(classNames);
                //System.out.print(Arrays.toString(classNames));
                //newClassNames.add(classNames);
                //System.out.print(newClassNames);
                properties.put(property, newClassNames);
            }
            
            
        }
           
        return properties;
    }

   
    
    
    
    /**
     * Checks if there is a RDF property connecting two entities for a
     * particular vocabulary.
     * @param SubjectClassName The class name for subject checking
     * @param Property The testing property name
     * @param ObjectClassName The class name for object checking
     * @param vocabulary The specified schema vocabulary
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return True if the property is entity-to-entity
     */
    public boolean isRDFEntityProperty(String SubjectClassName,String Property,String ObjectClassName,String vocabulary,String prefixes,String serverEndpoint)
    {
        String query = "SELECT DISTINCT * WHERE { ?s a vocabulary:"+SubjectClassName+". ?s vocabulary:"+Property+ " ?o. ?o a vocabulary:"+ObjectClassName+"}";
        
        //String query = "SELECT DISTINCT * WHERE { ?s a vocabulary:Hairpin. ?s vocabulary:producesMature ?o . ?o a vocabulary:Mature }";
        
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
         //System.out.println(qr.getResultSet());
        Map<String, Set<String>> literals = new HashMap<String, Set<String>>();
        //System.out.print(qr);
        return !qr.getResultSet().isEmpty();
    }
    
   
    
    /**
     * Checks if a string corresponds to a given RDF literal for
     * a particular class-property pair and a specified vocabulary.
     * @param literal The participant string  
     * @param className The given RDF class 
     * @param property The given property name
     * @param vocabulary The specified schema vocabulary
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return true if the RDF literal exists
     */
    public boolean isRDFLiteral(String literal,String className,String property,String vocabulary,String prefixes,String serverEndpoint)
    {  
        //String query = "SELECT DISTINCT * WHERE { ?s a vocabulary:"+className+". ?s vocabulary:"+property+" ?o FILTER( ?o="+"\""+literal+"\" ) }";
        String query = "SELECT DISTINCT * WHERE { ?s a vocabulary:"+className+". ?s vocabulary:"+property+" \""+literal+"\" . }";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        return !qr.getResultSet().isEmpty();          
    }
    
    /**
     * Retrieves the datatype of a RDF literal object given a class-property
     * pair and a specified vocabulary.
     * @param className The name of the RDF class in which the object belongs
     * @param property The property of the object
     * @param vocabulary The given schema vocabulary
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return The datatype of the literal 
     */
    public String getLiteralDatatype(String className,String property,String vocabulary,String prefixes,String serverEndpoint)
    {
        String literalDatatype="";
        String query = "select distinct (datatype(?o) as ?datatype) where" +
                       "{ "+"?s a vocabulary:"+className+". " +
                        "?s vocabulary:"+property+ " ?o " +
                        "filter isLiteral(?o) " + "} ";
        Set<String> classnames=new HashSet();
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        for (QuerySolution s : qr.getResultSet()){
            String str1=s.get("?datatype").toString();
            literalDatatype=str1;
        }
        return literalDatatype;
    }
    
    /**
     * Retrieves all the RDF properties along with the RDF classes
     * of their subjects.
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return A hash map where the key is the property name and
     * the value is a set of referencing class names. The string array
     * inside the set of each map element contains only one item, the 
     * subject class name.
     */
    public Map<String, Set<String[]>> getRDFPropertiesWithClasses(String prefixes,String serverEndpoint)
    {  
        String query = "SELECT DISTINCT ?p ?c WHERE { ?s a ?c. ?s ?p ?o.}";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Map<String, Set<String[]>> properties = new HashMap<String, Set<String[]>>();
        
        //System.out.print(qr.getResultSet());
        for (QuerySolution s : qr.getResultSet()){
            String str1=s.get("?p").toString();
            String str2=s.get("?c").toString();
            int i=str1.lastIndexOf("/")+1;
            int j=str2.lastIndexOf("/")+1;
            //String object=s.get("?o").toString();
            //boolean isLiteral=s.get("?o").isLiteral();
            String property=str1.substring(i);
            String className=str2.substring(j);
            //int classes;
            if(property.contains("rdf-syntax")||property.contains("sameAs")||property.contains("rdf-schema"))
                continue;
            //if(isLiteral)
            //{
                //System.out.println("Object:"+object);
            //if(properties.containsKey(property)&&!setContainsString(properties.get(property),className))
            if(properties.containsKey(property))
            {
                Set<String[]> newClassNames = new HashSet<String[]>();
                newClassNames=properties.get(property);
                //String[] classNames=new String[2];
                
                    String[] classNames=new String[1];
                    classNames[0]=className;
                    newClassNames.add(classNames);
                
                //System.out.print(classNames);
                //newClassNames.add(classNames);
                properties.put(property, newClassNames);
            }
            else
            {
                Set<String[]> newClassNames = new HashSet<String[]>();
                //String[] classNames=new String[2];
                //System.out.print(className);
                    String[] classNames=new String[1];
                    //System.out.print("hello");
                    classNames[0]=className;
                    newClassNames.add(classNames);
                    //System.out.print(Arrays.toString(classNames));
                    //System.out.print(classNames[0]);
                
                //System.out.print(Arrays.toString(classNames));
                //newClassNames.add(classNames);
                //System.out.print(newClassNames);
                properties.put(property, newClassNames);
            }
            //System.out.print(classNames);
            //String str=s.get("?o").toString();
            //boolean str1=s.get("?o").isLiteral();
            //}
            
        }
           
        return properties;
    }
    
    /**
     * Retrieves all the RDF properties from schema and
     * range/domain if available.
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @param namedGraph The named graph to explore. If null, explore the whole dataset
     *                   of the endpoint.
     * @return The set of the RDF properties names	
     */
    public Map<String, Set<String[]>> getRDFPropertiesNames(String prefixes,String serverEndpoint, String namedGraph)
    {  
        // Set SPARQL query to retrieve RDF properties, as well as range/domain if available
        String query = null;
        if (namedGraph != null) {
            query = "SELECT distinct ?p ?domain ?range FROM <" + namedGraph +
                     "> WHERE {?s ?p ?p "
                     + "OPTIONAL { ?p rdfs:domain ?domain} " + "OPTIONAL {?p rdfs:range ?range}} LIMIT 20";
        }
        else {
                query = "SELECT distinct ?p ?domain ?range " + 
                     "WHERE {?s ?p ?o"
                     + "OPTIONAL { ?p rdfs:domain ?domain} " + "OPTIONAL {?p rdfs:range ?range}} LIMIT 20";
        }
         
        // Run SPARQL query
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        
        // Extract RDF property and range/domain
        Map<String, Set<String[]>> properties = new HashMap<String, Set<String[]>>(); 
        for (QuerySolution s : qr.getResultSet()){
            String property=s.get("?p").toString().trim();
            RDFNode r = s.get("?domain");
            String domain=null;
            if( r != null) {
                domain = r.toString().trim();
            }
            String range=null;
            r = s.get("?range");
            if(r != null) {
                range = r.toString().trim();
            }            
            
            // Add the property to the result set
            if(properties.containsKey(property))
            {
                Set<String[]> newClassNames = properties.get(property);
                String[] classNames=new String[2];
                classNames[0]=domain;
                classNames[1]=range;
                //System.out.print(classNames);
                newClassNames.add(classNames);
                properties.put(property, newClassNames);
            }
            else
            {
                Set<String[]> newClassNames = new HashSet<>();
                String[] classNames=new String[2];
                classNames[0]=domain;
                classNames[1]=range;
                //System.out.println("ClassNames::" + classNames[0] + "\t" + classNames[1] + "\n");
                newClassNames.add(classNames);
                properties.put(property, newClassNames);
            }
        }
        return properties;
    }
    

            
}
