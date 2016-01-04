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

import com.hp.hpl.jena.query.QuerySolution;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import org.keywordsearch.sparqllib.SPARQLQueryLib;
import org.keywordsearch.sparqllib.QueryResponse;

/**
 *
 * @author penny
 */
public class KeywordSearch {

    //String serverEndpoint = "http://snf-541101.vm.okeanos.grnet.gr:2020/sparql";
    String serverEndpoint = "http://localhost:2020/sparql";
    
    String prefixes = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        "PREFIX d2r: <http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#> " +
			//"PREFIX diana: <http://snf-541101.vm.okeanos.grnet.gr:2020/resource/diana/> " +
			"PREFIX diana: <http://localhost:2020/resource/diana/> " +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
			"PREFIX map: <http://snf-541101.vm.okeanos.grnet.gr:2020/resource/#> " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"PREFIX meta: <http://www4.wiwiss.fu-berlin.de/bizer/d2r-server/metadata#> ";
    
    String keywords;
    
       
    public void setKeywords( String value )
    {
        keywords = value;
    }
    
    public String getKeywords() 
    { 
        return keywords; 
    }
    
    public String[] getKeywordsArray()
    {      
        keywords.replaceAll("\\s+","");
        String[] kwords = keywords.split(",");
        
        return kwords;
    }

    public String getLiteralDatatype(String className,String property)
    {
        String literalDatatype="";
        String query = "select distinct (datatype(?o) as ?datatype) where" +
                       "{ "+
                        "?s a diana:"+className+". " +
                        "?s diana:"+property+ " ?o " +
                        "filter isLiteral(?o) " +
                         "} ";
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
    
    public Set<String> getRDFClassNames()
    {  
        String query = "SELECT distinct ?c WHERE {?s a ?c} ";
        Set<String> classnames=new HashSet();
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        //System.out.println(qr.getResultSet());
        //System.out.println("\n");
        
        //For each result, keep only the class name
        for (QuerySolution s : qr.getResultSet()){
          //System.out.println(s.toString());
          //String st=s.toString();
         String str=s.get("?c").toString();
            
            int i=str.lastIndexOf("/")+1;
            //System.out.println(str.substring(i));
            classnames.add(str.substring(i));
            //classnames.add(str);
        
        }
           
        return classnames;
    }
    
    public Map<String, Set<String[]>> getRDFAttributeProperties()
    {  
        //String query = "SELECT DISTINCT ?p ?c WHERE { ?s a ?c. ?s ?p ?o. } ";
        //String query = "SELECT DISTINCT ?c ?p ?d WHERE {{ ?s a ?c. ?s ?p ?o} UNION {?s a ?c. ?s ?p ?o. ?o a ?d} } LIMIT 1"; 
        //String query = "SELECT DISTINCT * WHERE { ?s ?p ?o}  LIMIT 1"; 
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
            if(property.contains("rdf-syntax")||property.contains("sameAs"))
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
    
    
     public Set<String> getRDFPropertiesNames()
    {  
        //String query = "SELECT DISTINCT ?p ?c WHERE { ?s a ?c. ?s ?p ?o. } ";
        //String query = "SELECT DISTINCT ?c ?p ?d WHERE {{ ?s a ?c. ?s ?p ?o} UNION {?s a ?c. ?s ?p ?o. ?o a ?d} } LIMIT 1"; 
        //String query = "SELECT DISTINCT * WHERE { ?s ?p ?o}  LIMIT 1"; 
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
            if(property.contains("rdf-syntax")||property.contains("sameAs"))
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
    
    
    public Map<String, Set<String[]>> getRDFEntityProperties()
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
            if(property.contains("rdf-syntax"))
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
    
    public boolean isRDFEntityProperty(String SubjectClassName,String Property,String ObjectClassName)
    {
        String query = "SELECT DISTINCT * WHERE { ?s a diana:"+SubjectClassName+". ?s diana:"+Property+ " ?o. ?o a diana:"+ObjectClassName+"}";
        
        //String query = "SELECT DISTINCT * WHERE { ?s a diana:Hairpin. ?s diana:producesMature ?o . ?o a diana:Mature }";
        
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
         //System.out.println(qr.getResultSet());
        Map<String, Set<String>> literals = new HashMap<String, Set<String>>();
        //System.out.print(qr);
        return !qr.getResultSet().isEmpty();
    }
    
    public String getPropertyObjectClassName(String property,String className)
    {
        String query = "SELECT DISTINCT ?c WHERE { ?s rdf:type diana:"+className+". ?s diana:"+property+" ?o. ?o rdf:type ?c}";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Map<String, Set<String[]>> properties = new HashMap<String, Set<String[]>>();
        
        
        for (QuerySolution s : qr.getResultSet()){
            String str1=s.get("?c").toString();
            int i=str1.lastIndexOf("/")+1;
            String objectClassName=str1.substring(i);
            //int classes;
            if(property.contains("rdf-syntax"))
                continue;
        }
        return "";
            
    }
    
    public Set<String> getRDFLiterals(String property,String className)
    {  
        String query = "SELECT DISTINCT ?v WHERE { ?s a diana:"+className+". ?s diana:"+property+" ?v . }";
        
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
    
    public boolean getRDFLiterals(String literal,String className,String property)
    {  
        //String query = "SELECT DISTINCT * WHERE { ?s a diana:"+className+". ?s diana:"+property+" ?o FILTER( ?o="+"\""+literal+"\" ) }";
        String query = "SELECT DISTINCT * WHERE { ?s a diana:"+className+". ?s diana:"+property+" \""+literal+"\" . }";
        
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Map<String, Set<String>> literals = new HashMap<String, Set<String>>();
        
        return !qr.getResultSet().isEmpty();
        /*for (QuerySolution s : qr.getResultSet()){
                Set<String> PropertyWithClass = new HashSet<String>();
                PropertyWithClass.add("prop:".concat(property).concat(",class:").concat(className));
                literals.put(literal, PropertyWithClass);
        }*/
           
        
    }
    
    public boolean setContainsString(Set<String[]> set,String str)
    {
        boolean rtn=false;
        for(String[] strArray : set) {
            if(strArray.equals(str))
                rtn=true;
        }
        return rtn;
    }
    
     public void getRDFresult()
    {  
        //String query = "SELECT DISTINCT * WHERE { ?s a diana:"+className+". ?s diana:"+property+" ?o FILTER( ?o="+"\""+literal+"\" ) }";
        String query = "SELECT DISTINCT * WHERE {\n" +
"?K3 diana:name ?v4. " +
"?G1 a diana:Gene. " +
"?K0 diana:hasKegg ?K3. " +
"?M2 diana:name ?v3. " +
"?G1 diana:SNP ?M2. " +
"?M2 a diana:Mature. " +
"?K0 diana:hasGene ?G1. " +
"?K3 a diana:Kegg. " +
"?K0 a diana:KeggGeneConnection. " +
"FILTER (str(?v3) = \"hsa-let-7a-5p\"). " +
"}";
        SPARQLQueryLib queryLib = new SPARQLQueryLib();
        queryLib.connect(serverEndpoint);
        QueryResponse qr = queryLib.sendQuery(prefixes + query);
        Map<String, Set<String>> literals = new HashMap<String, Set<String>>();
        
        System.out.println(qr.getResultSet());
        /*for (QuerySolution s : qr.getResultSet()){
                Set<String> PropertyWithClass = new HashSet<String>();
                PropertyWithClass.add("prop:".concat(property).concat(",class:").concat(className));
                literals.put(literal, PropertyWithClass);
        }*/
           
        
    }
}
