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
 * This class describes the RDF class inverted 
 * index as a Berkeley DB entity. The inverted 
 * index refers to similar RDF classes of the
 * keyword index(e.g. lower case letters, keywords
 * excluding special characters etc).
 * @author fil
 * @author gkirtzou
 */
@Entity
public class ClassInvertedIndex {
    @PrimaryKey
    private String className;
    private Set<String> classURIs;
    
    /**
     * Defines the className.
     * @param data The class name in the inverted index 
     */
    public void setClassName(String data)
    {
        className=data;
    }
    
    /**
     * Defines the classURIs.
     * @param data The set of referencing RDF classes 
     * in the keyword index. 
     */
    public void setClassURIs(Set<String> data)
    {
        classURIs=data;
    }
    
     /**
     * Expands the classURIs.
     * @param data An reference RDF classes 
     * to be added in the Term index. 
     * @author gkirtzou
     */
    public void addClassURIs(String data)
    {
        classURIs.add(data);
    }
    /**
     * Retrieves the classURIs.
     * @return The set of referencing RDF classes
     * in the keyword index.
     */
    public Set<String> getClassURIs()
    {
        return classURIs;
    }
    
    /**
     * Retrieves the className.
     * @return The class name in the inverted index.
     */
    public String getClassName()
    {
        return className;
    }
    
    @Override
    public String toString() {
        String str = "[ Class name:" + this.className
                + "\n Class References:\n"+ this.classURIs.toString()
                + "]\n";
        return(str);
    }
}
