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
import java.util.Set;
/**
 * This class describes the property inverted 
 * index as a Berkeley DB entity. The inverted 
 * index refers to similar RDF properties of the
 * keyword index(e.g. lower case letters, keywords
 * excluding special characters etc).
 * @author fil
 */
@Entity
public class PropertyInvertedIndex {
    @PrimaryKey
    private String propertyIndex;
    private Set<String> properties;
    
    /**
     * Defines the propertyIndex
     * @param data The property name in the inverted index
     */
    public void setPropertyIndex(String data)
    {
        propertyIndex=data;
    }
    
    /**
     * Defines the properties
     * @param data The set of referencing RDF properties
     * in the keyword index
     */
    public void setProperties(Set<String> data)
    {
        properties=data;
    }
    
    /**
     * Retrieves the properties
     * @return The set of referencing RDF properties
     * in the keyword index
     */
    public Set<String> getProperties()
    {
        return properties;
    }
    
    /**
     * Retrieves the propertyIndex
     * @return The property name in the inverted index
     */
    public String getPropertyIndex()
    {
        return propertyIndex;
    }
}
