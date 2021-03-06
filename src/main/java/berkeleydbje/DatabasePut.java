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

/**
 * This class creates a BerkeleyDB data store for
 * creating,accessing and modifying the keyword
 * index structures.
 * @author fil
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
     * @throws DatabaseException 
     */
    public void loadClassDb(String prefixes,String serverEndpoint) 
        throws DatabaseException {

            KeywordIndex ksearch=new KeywordIndex();
            Set<String> classNames=ksearch.getRDFClassNames(prefixes,serverEndpoint);
            RdfClass theClass = new RdfClass();
            Iterator setIterator = classNames.iterator();
            while (setIterator.hasNext())
            {
                theClass.setClassName(setIterator.next().toString());
                da.classByName.put(theClass);
            }
            
    }
    
    /**
     * Inserts into the database the properties. 
     * @param vocabulary The given RDF vocabulary 
     * @param prefixes The specified prefixes
     * @param serverEndpoint The SPARQL endpoint
     * @throws DatabaseException 
     */
    public void loadPropertyDb(String vocabulary,String prefixes,String serverEndpoint)
        throws DatabaseException {

        KeywordIndex ksearch=new KeywordIndex();
        Set<String> allPropertiesNames=ksearch.getRDFPropertiesNames(prefixes,serverEndpoint);
        Iterator setIterator = allPropertiesNames.iterator();
        while (setIterator.hasNext())
        {
            EntityCursor<RdfClass> SubjectClass =
                da.classByName.entities();
            String Property=setIterator.next().toString();
            //System.out.println("Property:"+property);
            // For each pair of subject-object className find the entity-to-entity properties
                      for(RdfClass subject:SubjectClass)
                      {
                          EntityCursor<RdfClass> ObjectClass =
                          da.classByName.entities();
                          for(RdfClass object:ObjectClass)
                          {
                          String Subject=subject.getClassName();
                          String Object=object.getClassName();
                          System.out.println("Object:"+Object);
                          if(ksearch.isRDFEntityProperty(Subject,Property, Object,vocabulary,prefixes,serverEndpoint))
                          {
                              Property relationProperty=new Property();
                              //System.out.println("!!!!!Subject:"+Subject+" Property:"+Property+" Object:"+Object);
                              if(!da.propertyByName.contains(Property))
                              {
                                   Set<String[]> newClassNames = new HashSet<String[]>();
                                   String[] classNames=new String[2];
                                   classNames[0]=Subject;
                                   classNames[1]=Object;
                                   newClassNames.add(classNames);
                                   Property newProperty=new Property();
                                   newProperty.setPropertyName(Property);
                                   newProperty.setClassName(newClassNames);
                                   da.propertyByName.put(newProperty);
                              }    
                              else
                              {
                                  Set<String[]> newClassNames = new HashSet<String[]>();
                                  newClassNames=da.propertyByName.get(Property).getClassName();
                                  String[] classNames=new String[2];
                                  classNames[0]=Subject;
                                  classNames[1]=Object;
                                  newClassNames.add(classNames);
                                  Property newProperty=new Property();
                                  newProperty.setPropertyName(Property);
                                  newProperty.setClassName(newClassNames);
                                  da.propertyByName.put(newProperty);
                              }
                          }
                      }
                      ObjectClass.close();
            }
            SubjectClass.close();
         }
         Map<String, Set<String[]>> propertiesWithSubject=ksearch.getRDFPropertiesWithClasses(prefixes,serverEndpoint);
         for (Map.Entry<String, Set<String[]>> entry : propertiesWithSubject.entrySet())
         {
             // Load the missing entity-to-attribute properties
             if(!da.propertyByName.contains(entry.getKey().toString()))
             {
                  Property newProperty=new Property();
                  newProperty.setPropertyName(entry.getKey().toString());
                  newProperty.setClassName(entry.getValue());
                  da.propertyByName.put(newProperty);
             }    
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
            while (scanner.hasNextLine()) {

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
                for (RdfClass curClassName : classes) {
                    Set<String> newClasses = new HashSet<String>();
                    ClassInvertedIndex classIndex= new ClassInvertedIndex();
                    String className=curClassName.getClassName();
                    String classLowerCaseName=curClassName.getClassName().toLowerCase();
                    if(!da.classInvertedIndexByName.contains(classLowerCaseName))
                    {
                        newClasses.add(className);
                        classIndex.setClassNameIndex(className);
                        classIndex.setClassNames(newClasses);
                        da.classInvertedIndexByName.put(classIndex);
                    }
                    else
                    {
                        classIndex=da.classInvertedIndexByName.get(classLowerCaseName);
                        newClasses=classIndex.getClassNames();
                        newClasses.add(className);
                        classIndex.setClassNames(newClasses);
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
                    Set<String> newProperties = new HashSet<String>();
                    PropertyInvertedIndex propertyIndex= new PropertyInvertedIndex();
                    String propertyName=property.getPropertyName();
                    String propertyLowerCaseName=property.getPropertyName().toLowerCase();
                    if(!da.propertyInvertedIndexByName.contains(propertyLowerCaseName))
                    {
                        newProperties.add(propertyName);
                        propertyIndex.setPropertyIndex(propertyLowerCaseName);
                        propertyIndex.setProperties(newProperties);
                        da.propertyInvertedIndexByName.put(propertyIndex);
                    }
                    else
                    {
                        propertyIndex=da.propertyInvertedIndexByName.get(propertyLowerCaseName);
                        newProperties=propertyIndex.getProperties();
                        newProperties.add(propertyName);
                        propertyIndex.setProperties(newProperties);
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
}