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
import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;
import com.sleepycat.persist.model.SecondaryKey;
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
    
    /*
     * The URI of the RDF Property
     */
    @PrimaryKey
    private String URI;
    
    /*
     * The name of the RDF property
     */
    @SecondaryKey(relate=MANY_TO_ONE)
    private String propertyName;
    
    /*
     * The pairs of URIs of all RDF class <subjects, objects>
     * met with this RDF property. 
     * The URI of the RDF class is a unique key
     * that can be use to find it the within collection.
     */
    private Set<String[]> classURI;
      
    /*
     * The Datatype of the object, if property
     * is entity-to-attribute property. Otherwise
     * is null.
     */
    private String literalDatatype;
    
    /*
     * Class constructor 
     */ 
    public Property() {
        this.URI = null;
        this.propertyName = null;
        this.classURI = null;
        this.literalDatatype = null;
    }
    
    
    /**
     * Defines the RDF URI
     * @param URI The URI of the RDF property
     */
    public void setURI(String URI) {
        this.URI = URI;
    }
    
    /**
     * Retrieves the URI of the RDF property
     * @return The URI of the RDF property
     */
    public String getURI() {
        return this.URI;
    }   
    
    
    /**
    * Defines the propertyName
    * @param data The name of the property
    */
    public void setPropertyName(String data)
    {
        this.propertyName=data;
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
    * Defines the literalDatatype
    * @param data The RDF datatype of the property 's subject 
    */
    public void setLiteralDatatype(String data)
    {
        this.literalDatatype=data;
    }
    
    /**
    * Retrieves the literalDatatype
    * @return The RDF datatype of the property 's subject
    */
    public String getLiteralDatatype()
    {
        return this.literalDatatype;
    }
    
   /**
    * Defines the className
    * @param data The set of the RDF classes related to the property
    * The strings is the URIs of the RDF classes (keys).
    */        
    public void setClassName(Set<String[]> data)
    {
        this.classURI=data;
    }
        
    /**
    * Retrieves the className
    * @return The set of the RDF classes related to the property
    */
    public Set<String[]> getClassName()
    {
        return this.classURI;
    }
    
    public void addClassName(String[] data) {
        if(this.classURI == null) {
            this.classURI = new HashSet();
        }
        this.classURI.add(data);    
    }   
        
    @Override
    public String toString() {
        String str = "[URI: " + this.URI
                + "\nProperty: "+ this.propertyName
                + "\nDomain-Range: ";
        if (this.classURI  != null) {
            for (String[] c: this.classURI) {
                for (String s : c) {
                    str = str + s + "\t";
                }
                str = str + "\n";
            }   
        }
        else {
            str =  str + "null\n";
        }
        str = str + "LiteralDatatype: " + this.literalDatatype + "\n]";
        return(str);
    }
}
