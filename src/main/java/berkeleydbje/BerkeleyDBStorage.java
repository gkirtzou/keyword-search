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

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import java.io.File;
import java.util.Set;



/**
 * This class encapsulates Berkeley DB operations used 
 * for the mapping of keywords to data elements
 * @author serafeim
 * @author fil
 * @author gkirtzou
 */
public class BerkeleyDBStorage implements IStorageEngine{
    
    private static File myDbEnvPath = null;
    private final DataAccessor da;
    private static final MyDbEnv myDbEnv = new MyDbEnv();
    
    /**
     * Class constructor
     * @param bdbfiles_path path to Berkeley DB files
     * @throws DatabaseException 
     */
    public BerkeleyDBStorage(String bdbfiles_path) throws DatabaseException{
        myDbEnvPath = new File(bdbfiles_path);
        myDbEnv.setup(myDbEnvPath, true);
        da = new DataAccessor(myDbEnv.getEntityStore());
    }
    
    
    /******************
     * Contains
     *****************/
    
    /**
     * Finds if a class is present in the keyword index
     * @param className name of the class
     * @return true if class is present, false otherwise
     * @throws DatabaseException 
     */
    @Override
    public boolean containsClass(String className) 
            throws DatabaseException{
        return da.classByName.contains(className);
    }
    
    
    /**
     * Finds if a property is present in the keyword index
     * @param propertyName name of the property
     * @return true if class is present, false otherwise
     * @throws DatabaseException 
     */
    @Override
    public boolean containsProperty(String propertyName) 
            throws DatabaseException{
        return da.propertyByName.contains(propertyName);
    }
    
    /**
     * Finds if a literal is present in the keyword index
     * @param literalName name of the literal
     * @return true if class is present, false otherwise
     * @throws DatabaseException 
     */
    @Override
    public boolean containsLiteral(String literalName)
            throws DatabaseException{
        return da.literalByName.contains(literalName);
    }
    
    
    /**
     * Finds if a class is present in the inverted index
     * @param className name of the class
     * @return true if class is present, false otherwise
     * @throws DatabaseException 
     */
    @Override
    public boolean containsClassInvertedIndex(String className) 
            throws DatabaseException{
        return da.classInvertedIndexByName.contains(className.toLowerCase());
    }
    
    /**
     * Finds if a property is present in the inverted index
     * @param property name of the property
     * @return true if class is present, false otherwise
     * @throws DatabaseException 
     */
    @Override
    public boolean containsPropertyInvertedIndex(String property) 
            throws DatabaseException{
        return da.propertyInvertedIndexByName.contains(property.toLowerCase());
    }
    
    /**
     * Finds if a literal is present in the inverted index
     * @param literal name of the literal
     * @return true if class is present, false otherwise
     * @throws DatabaseException 
     */
    @Override
    public boolean containsLiteralInvertedIndex(String literal) 
            throws DatabaseException{
        return da.literalInvertedIndexByName.contains(literal.toLowerCase());
    }
    
    
    /******************
     * Get details
     *****************/
    
    /**
     * Gets property information from the term index
     * @param propertyName the property name
     * @return The set of pairs <subjectClass,objectClass> related to the propertyName
     * 	if null the property is not inter-entities property
     * @throws DatabaseException 
     */
    @Override
    public Set<String[]> getDetailsEntityPropertyByName(String propertyName) 
            throws DatabaseException{
        return da.propertyByName.get(propertyName).getClassName();
    }
    
    /**
     * Gets property information from the term index
     * @param propertyName the property name
     * @return The set of pairs <subjectClass,objectClass> related to the propertyName
     * 	if null the property is not inter-entities property
     * @throws DatabaseException 
     */
    @Override
    public Set<String[]> getDetailsEntityPropertyByURI(String propertyURI) 
            throws DatabaseException{
        return da.propertyIndex.get(propertyURI).getClassName();
    }
    
    
    /**
     * Gets property information from the term index using property name
     * @param propertyName the property name
     * @return The set of pairs <subjectClass,objectType> related to the property name
     * 	if null the property is not literal property
     * @throws DatabaseException 
     */
    @Override
    public Set<String[]> getDetailsLiteralPropertyByName(String propertyName) 
            throws DatabaseException{
        return da.propertyByName.get(propertyName).getLiteralDatatype();
    }
    
    
    /**
     * Gets property information from the term index using property URI
     * @param propertyName the property name
     * @return The set of pairs <subjectClass,objectType> related to the propertyName
     * 	if null the property is not literal property
     * @throws DatabaseException 
     */
    @Override
    public Set<String[]> getDetailsLiteralPropertyByURI(String propertyURI) 
            throws DatabaseException{
        return da.propertyIndex.get(propertyURI).getLiteralDatatype();
    }
    
    /**
     * Gets literal information from the keyword index
     * @param literalName the literal name
     * @return The set of class-property pairs related to the literalName
     * @throws DatabaseException 
     */
    @Override
    public Set<String[]> getDetailsLiteral(String literalName) 
            throws DatabaseException{
        return da.literalByName.get(literalName).getPropertyWihClass();
    }
    
    /**
     * Gets all RDF classes referenced to the given keyword 
     * @param className the class name to search for
     * @return The set of matching classes
     * @throws DatabaseException 
     */
    @Override
    public Set<String> getRefClasses(String className) 
            throws DatabaseException{
        return da.classInvertedIndexByName.get(className.toLowerCase()).getClassURIs();
    }
    
    /**
     * Gets all properties referenced to the given keyword 
     * @param propertyIndex the property name to search for
     * @return The set of matching properties
     * @throws DatabaseException 
     */
    @Override
    public Set<String> getRefProperties(String propertyIndex) 
            throws DatabaseException{
        return da.propertyInvertedIndexByName.get(propertyIndex.toLowerCase()).getPropertiesURIs();
    }
    
    /**
     * Gets all literals referenced to the given keyword 
     * @param literalIndex the literal name to search for
     * @return The set of matching literals
     * @throws DatabaseException 
     */
    @Override
    public Set<String> getRefLiterals(String literalIndex) 
            throws DatabaseException{
        return da.literalInvertedIndexByName.get(literalIndex.toLowerCase()).getLiterals();
    }
    
    
    /**
     * Gets a cursor to the classes of the keyword index
     * @return cursor to classes
     * @throws DatabaseException 
     */
    public EntityCursor<RdfClass> getClassCursor() 
            throws DatabaseException{
        return da.classByName.entities();
    }
    
    /**
     * Gets a cursor to the properties of the keyword index
     * @return cursor to the properties
     * @throws DatabaseException 
     */
    public EntityCursor<Property> getPropertyCursor() 
            throws DatabaseException{
        return da.propertyByName.entities();
    }
    
    /**
     * Gets a cursor to the literals of the keyword index
     * @return cursor to the literals
     * @throws DatabaseException 
     */
    public EntityCursor<Literal> getLiteralCursor() 
            throws DatabaseException{
        return da.literalByName.entities();
    }
    

    /**
     * Closes connection with the indices
     */
    public void close(){
        myDbEnv.close();
    }
}
