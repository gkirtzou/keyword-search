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

import java.io.File;
import java.io.FileNotFoundException;
//import java.nio.file.*;
import java.util.Scanner;
import java.util.*;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import java.io.IOException;

/**
 * This class creates a BerkeleyDB data store for
 * creating, accessing and modifying the Term 
 * index structures.
 * @author fil
 * @author gkirtzou
 */
public class DatabasePut{
    private static File myDbEnvPath;
    private DataAccessor da;
    // Encapsulates the environment and data store.
    private static MyDbEnv myDbEnv;
   
    /**
     * Class constructor 
     * @param bdbfiles_path The path to store the DB environment    
     * @throws DatabaseException 
     */
    public DatabasePut(String bdbfiles_path) throws DatabaseException{
        // Path to the environment home
        myDbEnv= new MyDbEnv();
        myDbEnvPath = new File(bdbfiles_path);
        // Parse the arguments list    
        // Environment read-only?
        // Open the data accessor. This is used to store
        // persistent objects.
        myDbEnv.setup(myDbEnvPath, false);
        da = new DataAccessor(myDbEnv.getEntityStore());
    }
    
    /**
     * Safely closes the data store
     * @throws DatabaseException 
     */
    public void close()
        throws DatabaseException {
        
        myDbEnv.close();
        
    }
    
    /**
     * Inserts into the database the RDF classes. 
     * @param prefixes The specified prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @param namedGraph The named graph to explore. If null, explore the whole dataset
     *                   of the endpoint.
     * @throws DatabaseException 
     */
    public void loadClassDb(String prefixes, String serverEndpoint, String namedGraph) 
        throws DatabaseException {

            KeywordIndex ksearch=new KeywordIndex();
            Set<String> classNames=ksearch.getRDFClassNames(prefixes, serverEndpoint, namedGraph);
            Iterator<String> setIterator = classNames.iterator();
            while (setIterator.hasNext())
            {
                // Split URI to prefix and name
                String str = setIterator.next();
                int i=this.splitURI(str);
                
                // Create RDF class object
                RdfClass rdfClass = new RdfClass();
                rdfClass.setClassName(str.substring(i));
                rdfClass.setPrefix(str.substring(0, i));
                rdfClass.setURI(str);
                //System.out.println(rdfClass);
                da.classIndex.put(rdfClass);
            }
            
    }
    
     /**
     * Inserts into the database the RDF classes. 
     * @param csvFile
     * @throws DatabaseException 
     * @throws java.io.IOException
     * @author gkirtzou
     */
    public void loadClassDb(String csvFile) 
        throws DatabaseException, IOException {

            KeywordIndex ksearch=new KeywordIndex();
            Set<String> classNames=ksearch.getRDFClassNames(csvFile);
            Iterator<String> setIterator = classNames.iterator();
            while (setIterator.hasNext())
            {
                // Split URI to prefix and name
                String str = setIterator.next();
                int i=this.splitURI(str);
                
                // Create RDF class object
                RdfClass rdfClass = new RdfClass();
                rdfClass.setClassName(str.substring(i));
                rdfClass.setPrefix(str.substring(0, i));
                rdfClass.setURI(str);
                //System.out.println(rdfClass);
                da.classIndex.put(rdfClass);
            }
    }
    
    /**
     * Inserts into the database the properties. 
     * @param csvFile
     * @param vocabulary The given RDF vocabulary 
     * @param prefixes The specified prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @param namedGraph The named graph to explore. If null, explore the whole dataset
     *                   of the endpoint.
     * @throws DatabaseException 
     * @throws java.io.IOException 
     */
    //public void loadPropertyDb(String vocabulary,String prefixes,String serverEndpoint, String namedGraph)
    public void loadPropertyDb(String csvFile, String vocabulary,String prefixes,String serverEndpoint, String namedGraph)
        throws DatabaseException, IOException {

        KeywordIndex ksearch=new KeywordIndex();
       // Map<String, Set<String[]>>  properties=ksearch.getRDFPropertiesNames(prefixes, serverEndpoint, namedGraph);
        Map<String, Set<String[]>>  properties=ksearch.getRDFProperties(csvFile, serverEndpoint, prefixes, namedGraph);
        for (Map.Entry<String, Set<String[]>> property : properties.entrySet()) {
        
           // Split URI to prefix and name
           String str = property.getKey().trim();
           int i=this.splitURI(str);
                
           // Create RDF property object
           Property rdfProperty = new Property();
           rdfProperty.setURI(str);
           rdfProperty.setPropertyName(str.substring(i));
           if (!property.getValue().isEmpty()) {
            rdfProperty.setClassName(property.getValue());   
           }
           
           
           // Add RDF property object to term index
           System.out.println(rdfProperty);
           da.propertyIndex.put(rdfProperty);
         }
    }
    
    /**
     * Inserts into the database the literals for a given
     * set of class-properties pairs in a file. The file 
     * type is text-plain where each line is a pair of class 
     * name-property name separated by comma e.g. Mature,name. 
     * 
     * @param vocabulary The given RDF vocabulary 
     * @param prefixes The specified prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @param classPropertiesFilepath The file path with the class-properties pairs 
     * @throws DatabaseException 
     */
    /*
    public void loadLiteralDb(String vocabulary,String prefixes,String serverEndpoint,String classPropertiesFilepath)
        throws DatabaseException {

        try {
        Scanner scanner = new Scanner(new File(classPropertiesFilepath));
        Scanner dataScanner = null;
        KeywordIndex ksearch=new KeywordIndex();
        //EntityCursor<Property> properties =
          //      da.propertyByName.entities();
        //EntityCursor<Literal> literals=
          //      da.literalByName.entities();
       // for(Literal literal : literals)
       // {
            //count++;
            /*System.out.print(count+" ");
            EntityCursor<Property> properties =
                da.propertyByName.entities();*/
            //String currentFileName="/tmp/literals.csv";
     /*       while (scanner.hasNextLine()) {

                dataScanner = new Scanner(scanner.nextLine());
                dataScanner.useDelimiter(",");
                Set<String> curLiterals= new HashSet();
                //Set<String> newPropertyWithClass = new HashSet<String>();
                //Literal theLiteral= new Literal();
                //Employee emp = new Employee();
                String className = dataScanner.next();
                String property = dataScanner.next();
                //System.out.println("Class:"+className+" Property:"+property);
                curLiterals=ksearch.getRDFLiterals(property, className,vocabulary,prefixes,serverEndpoint);
                Iterator setIterator = curLiterals.iterator();
                while (setIterator.hasNext())
                {
                    Set<String> newPropertyWithClass = new HashSet<String>();
                    Literal theLiteral= new Literal();
                    String next=setIterator.next().toString();
                    if(!da.literalByName.contains(next))
                    {
                        newPropertyWithClass.add("prop:".concat(property).concat(",class:").concat(className));
                        theLiteral.setLiteralName(next);
                        theLiteral.setPropertyWithClass(newPropertyWithClass);
                        da.literalByName.put(theLiteral);
                    }
                    else
                    {
                        theLiteral=da.literalByName.get(next);
                        newPropertyWithClass=theLiteral.getPropertyWihClass();
                        newPropertyWithClass.add("prop:".concat(property).concat(",class:").concat(className));
                        theLiteral.setPropertyWithClass(newPropertyWithClass);
                        da.literalByName.put(theLiteral);
                    }
                }
           }
 
        scanner.close();
        } catch (FileNotFoundException e) {
                    e.printStackTrace();
             }

    }
    */
    
    
    /**
     * Inserts into the database the RDF class names
     * for the inverted keyword index. The inverted 
     * index refers to similar RDF classes of the
     * keyword index(e.g. lower case letters, keywords
     * excluding special characters etc).
     * @throws DatabaseException 
     */
    public void loadClassCaseInsensitiveIndexes()
        throws DatabaseException {
              
         EntityCursor<RdfClass> classes =
                da.classByName.entities();
         try {
                for (RdfClass curClass : classes) {
                    //System.out.println("Working on class " + curClass);
                    String classLowerCaseName=curClass.getClassName().toLowerCase();
                    
                    if(!da.classInvertedIndexByName.contains(classLowerCaseName))  {
                        // Create ClassInvertedIndex
                        ClassInvertedIndex classIndex= new ClassInvertedIndex();
                        // The lower case class name
                        classIndex.setClassName(classLowerCaseName);
                        // The reference to the current RDF Class
                        Set<String> newClasses = new HashSet<>();
                        newClasses.add(curClass.getURI());
                        classIndex.setClassURIs(newClasses);
                        //System.out.println("Insert\n" + classIndex);
                        // Add the newly ClassInvertedIndex to the index
                        da.classInvertedIndexByName.put(classIndex);
                    }
                    else {
                        ClassInvertedIndex classIndex=da.classInvertedIndexByName.get(classLowerCaseName);
                        classIndex.addClassURIs(curClass.getURI());
                        //System.out.println("In the index\n " + classIndex);
                        da.classInvertedIndexByName.put(classIndex);
                    }
                     
                }
            } finally {
                classes.close();
            }
        
     }
    
     
    /**
     * Inserts into the database the RDF properties
     * for the inverted keyword index. The inverted 
     * index refers to similar RDF properties of the
     * keyword index(e.g. lower case letters, keywords
     * excluding special characters etc).
     * @throws DatabaseException 
     */
    public void loadPropertyCaseInsensitiveIndexes()
        throws DatabaseException {
         
        EntityCursor<Property> properties =
                da.propertyByName.entities();
        try {
            for (Property property : properties) {
                    //System.out.println("Working on property" + property);
                    String propertyLowerCaseName= property.getPropertyName().toLowerCase();
                    
                    if(!da.propertyInvertedIndexByName.contains(propertyLowerCaseName))
                    {
                        // Create PropertyInvertedIndex
                        PropertyInvertedIndex propertyIndex= new PropertyInvertedIndex();
                        // The lower case property name
                        propertyIndex.setPropertyName(propertyLowerCaseName);
                        // The reference to the current RDF Class
                        Set<String> newProperties = new HashSet<>();
                        newProperties.add(property.getURI());
                        propertyIndex.setPropertiesURIs(newProperties);
                        //System.out.println("Insert\n" + propertyIndex);
                        // Add the newly ProperptyInvertedIndex to the index
                        da.propertyInvertedIndexByName.put(propertyIndex);
                    }
                    else {
                        PropertyInvertedIndex propertyIndex=da.propertyInvertedIndexByName.get(propertyLowerCaseName);
                        propertyIndex.addPropertiesURIs(property.getURI());
                        //System.out.println("In the index\n " + propertyIndex);
                        da.propertyInvertedIndexByName.put(propertyIndex);
                    }
            }
        } finally {
                properties.close();
        }
         
     }
     
      /**
     * Inserts into the database the RDF literals
     * for the inverted keyword index. The inverted 
     * index refers to similar RDF literals of the
     * keyword index(e.g. lower case letters, keywords
     * excluding special characters etc).
     * @throws DatabaseException 
     */
    /*
     public void loadLiteralCaseInsensitiveIndexes()
        throws DatabaseException {
         
         EntityCursor<Literal> literals =
                da.literalByName.entities();
         try {
                for (Literal literal : literals) {
                    Set<String> newLiterals = new HashSet<String>();
                    LiteralInvertedIndex literalIndex= new LiteralInvertedIndex();
                    String literalName=literal.getLiteralName();
                    String literalLowerCaseName=literal.getLiteralName().toLowerCase();
                    if(!da.literalInvertedIndexByName.contains(literalLowerCaseName))
                    {
                        newLiterals.add(literalName);
                        literalIndex.setLiteralIndex(literalLowerCaseName);
                        literalIndex.setLiterals(newLiterals);
                        da.literalInvertedIndexByName.put(literalIndex);
                    }
                    else
                    {
                        literalIndex=da.literalInvertedIndexByName.get(literalLowerCaseName);
                        newLiterals=literalIndex.getLiterals();
                        newLiterals.add(literalName);
                        literalIndex.setLiterals(newLiterals);
                        da.literalInvertedIndexByName.put(literalIndex);
                    }
                    
                }
            } finally {
                literals.close();
            }
     }
    */
    /**
     * Find the splitting point of a URI. Before the splitting 
     * point lies the prefix, while after the splitting point
     * lies the name (suffix).
     * @param str The URI to split
     * @return The splitting point. 
     */
    private int splitURI(String str) {
        int i = str.lastIndexOf("/")+1;
        int j = str.lastIndexOf("#")+1;
        if (i < j)
            return j;
        return(i);
    }
}