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
import java.util.Set;
import java.util.HashSet;

/**
 * This class describes the RDF class
 * as a Berkeley DB entity
 * @author fil
 * @author gkirtzou
 */

@Entity
public class RdfClass {
       
    /*
     * The URI of the RDF Class as whole   
     */
    @PrimaryKey
    String URI;
    
    /* 
     * The RDF class name
     */
    @SecondaryKey(relate=MANY_TO_ONE)
    private String className;
    
    /*
     * The prefix meet with the class name
     */
    private String prefix;
    
    /*
     * Class constructor 
     */ 
    public RdfClass() {
        this.URI = null;
        this.className = null;
        this.prefix = null;
    }
    
    /**
     * Defines the RDF URI
     * @param URI The URI of the RDF class
     */
    public void setURI(String URI) {
        this.URI = URI;
    }
    
    /**
     * Retrieves the RDF class URI
     * @return The URI of the RDF class
     */
    public String getURI() {
        return this.URI;
    }   
        
    /**
     * Defines the className
     * @param data The class name extracted from the URI
     * 
     */
    public void setClassName(String data)
    {
        className=data;
    }
    
    /**
     * Retrieves the className
     * @return The name of the class
     * 
     */
    public String getClassName()
    {
        return className;
    }
    
    /**
     * Defines the prefix
     * @param prefix The prefix extracted from the URI
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Retrieves the prefix of the RDF class
     * @return the prefix of the RDF class
     */
    public String getPrefix() {
        return(this.prefix);
    }
   
    
    @Override
    public String toString() {
        String str = "[ URI:" + this.URI
                + "\nClass:"+ this.className 
                + "\tPrefixes: "+this.prefix 
                + "]\n";
        return(str);
    }
}
