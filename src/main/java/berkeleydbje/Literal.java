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
 * @author gkirtzou
 */
@Entity
public class Literal {
    
    @PrimaryKey
    private String literalValue;
    // The quad is <language, datatype, property, class of subject>
    // When language is equal to empty string "", means that no 
    // language is available. 
    // When datatype is equal to "null" string, means that no 
    // datatype information is available. 
    private Set<String[]> propertyWithClass;
    
    /*
     * Class constructor 
     */ 
    public Literal() {
        this.literalValue = null;
        this.propertyWithClass = null;
    }
    
    
   /**
    * Defines the literalName
    * @param data The name of the literal
    */
    public void setLiteralName(String data)
    {
        literalValue=data;
    }
    
   /**
    * Defines the propertyWithClass set
    * @param data The set of property-class pairs
    */
    public void setPropertyWithClass(Set<String[]> data)
    {
        propertyWithClass=data;
    }
    
    /**
     * Adds a new property-class pair to the propertyWithClass set
     * @param data A pair of property-class 
     */
     public void addPropertyWithClass(String[] data)
     {
    	  if(this.propertyWithClass == null) {
              this.propertyWithClass = new HashSet<String[]>();
          }
    	  propertyWithClass.add(data);
     }
     
   /**
    * Retrieves the propertyWithClass set
    * @return The set of property-class pairs
    */
    public Set<String[]> getPropertyWihClass()
    {
        return propertyWithClass;
    }
    
   /**
    * Retrieves the literalName
    * @return The name of the literal
    */
    public String getLiteralName()
    {
        return literalValue;
    }
    
    
    @Override
    public String toString() {
        String str = "[Value: " + this.literalValue
                + "\nProperty-Class pair: \n";
        if (this.propertyWithClass  != null) {
        	for (String[] c: this.propertyWithClass) {
            	for (String cc :c) {
            		str = str + cc + "\t";
            	}
                str = str + "\n";
            }   
        }
        else {
            str =  str + "null\n";
        }
        return(str);
    }
}
