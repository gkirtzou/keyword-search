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
 * This class describes the RDF literal
 * as a Berkeley DB entity
 * @author fil
 */
@Entity
public class Literal {
    
    @PrimaryKey
    private String literalName;
    private Set<String> propertyWithClass;
    
   /**
    * Defines the literalName
    * @param data The name of the literal
    */
    public void setLiteralName(String data)
    {
        literalName=data;
    }
    
   /**
    * Defines the propertyWithClass set
    * @param data The set of property-class pairs
    */
    public void setPropertyWithClass(Set<String> data)
    {
        propertyWithClass=data;
    }

   /**
    * Retrieves the propertyWithClass set
    * @return The set of property-class pairs
    */
    public Set<String> getPropertyWihClass()
    {
        return propertyWithClass;
    }
    
   /**
    * Retrieves the literalName
    * @return The name of the literal
    */
    public String getLiteralName()
    {
        return literalName;
    }
}
