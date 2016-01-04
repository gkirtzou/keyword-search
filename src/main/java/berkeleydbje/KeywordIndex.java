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
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import org.keywordsearch.sparqllib.*;


/**
 * This class contains methods used to obtain RDF information
 * by querying to a SPARQL endpoint. Afterwards this set of data 
 * is used to build the Keyword Index.
 * @author fil
 */
public class KeywordIndex {
    
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
     * Retrieves all the RDF classes from schema
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return The set of the RDF class names
     */
    public Set<String> getRDFClassNames(String prefixes,String serverEndpoint)
    {  
        String query = "SELECT distinct ?c WHERE {?s a ?c} ";
        Set<String> classnames=new HashSet();
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        
        //For each result, keep only the class name
        for (QuerySolution s : qr.getResultSet()){
         String str=s.get("?c").toString();
            
            int i=str.lastIndexOf("/")+1;
            String className=str.substring(i);
            if(className.contains("rdf-syntax")||className.contains("sameAs")||className.contains("rdf-schema"))
                continue;
            classnames.add(className);
        }
           
        return classnames;
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
     * Retrieves all the RDF properties from schema
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return The set of the RDF properties names
     */
    public Set<String> getRDFPropertiesNames(String prefixes,String serverEndpoint)
    {  
        //String query = "SELECT DISTINCT ?c ?p ?d WHERE {{ ?s a ?c. ?s ?p ?o} UNION {?s a ?c. ?s ?p ?o. ?o a ?d} } LIMIT 1"; 
        String query = "SELECT DISTINCT ?p WHERE { ?s ?p ?o.}";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Set<String> properties = new HashSet();
        
        
        for (QuerySolution s : qr.getResultSet()){
            String str1=s.get("?p").toString();
            int i=str1.lastIndexOf("/")+1;
            //String object=s.get("?o").toString();
            //boolean isLiteral=s.get("?o").isLiteral();
            String property=str1.substring(i);
            //int classes;
            if(property.contains("rdf-syntax")||property.contains("sameAs")||property.contains("rdf-schema"))
                continue;
                    //System.out.print(Arrays.toString(classNames));
                    //System.out.print(classNames[0]);
                
                //System.out.print(Arrays.toString(classNames));
                //newClassNames.add(classNames);
                //System.out.print(newClassNames);
            properties.add(property);
            
            //System.out.print(classNames);
            //String str=s.get("?o").toString();
            //boolean str1=s.get("?o").isLiteral();
            
            
        }
           
        return properties;
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
     * Retrieves the RDF literals for a particular class-property pair
     * and a specified vocabulary.
     * @param property The given property name
     * @param className The given RDF class name
     * @param vocabulary The specified schema vocabulary
     * @param prefixes The RDF prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @return The set of the literals found 
     */
    public Set<String> getRDFLiterals(String property,String className,String vocabulary,String prefixes,String serverEndpoint)
    {  
        String query = "SELECT DISTINCT ?v WHERE { ?s a vocabulary:"+className+". ?s vocabulary:"+property+" ?v . }";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Set<String> literals = new HashSet();
        
        
        for (QuerySolution s : qr.getResultSet()){
            String str1=s.get("?v").toString();
            if(s.get("?v").isLiteral())
                literals.add(str1);
            /*if(literals.contains(curLiteral))
            {
                Set<String> newPropertyWithClass = new HashSet<String>();
                newPropertyWithClass=literals.get(literal);
                newPropertyWithClass.add("prop:".concat(curProperty).concat(",class:").concat(className));
                literals.replace(literal, newPropertyWithClass);
            }
            else
            {
                Set<String> PropertyWithClass = new HashSet<String>();
                PropertyWithClass.add("prop:".concat(curProperty).concat(",class:").concat(className));
                literals.put(literal, PropertyWithClass);
            }*/
        }
           
        return literals;
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
    
}
