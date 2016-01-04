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

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
//import static com.sleepycat.persist.model.Relationship.*;
//import com.sleepycat.persist.model.SecondaryKey;
import java.util.*;

/**
 * This class describes the RDF property
 * as a Berkeley DB entity
 * @author fil
 */
@Entity
public class Property {
    
    @PrimaryKey
    private String propertyName;
    
    //@SecondaryKey(relate=MANY_TO_MANY)
    private Set<String[]> className;
    
    private String literalDatatype;
  
   /**
    * Defines the propertyName
    * @param data The name of the property
    */
    public void setPropertyName(String data)
    {
        propertyName=data;
    }
    
   /**
    * Defines the literalDatatype
    * @param data The RDF datatype of the property 's subject 
    */
    public void SetLiteralDatatype(String data)
    {
        literalDatatype=data;
    }
    
    /**
    * Retrieves the literalDatatype
    * @return The RDF datatype of the property 's subject
    */
    public String getLiteralDatatype()
    {
        return literalDatatype;
    }
    
   /**
    * Defines the className
    * @param data The set of the RDF classes related to the property
    */        
    public void setClassName(Set<String[]> data)
    {
        className=data;
    }
    
    /**
    * Retrieves the propertyName
    * @return The name of the property
    */
    public String getPropertyName()
    {
        return propertyName;
    }
     
    /**
    * Retrieves the className
    * @return The set of the RDF classes related to the property
    */
    public Set<String[]> getClassName()
    {
        return className;
    }
    
}
