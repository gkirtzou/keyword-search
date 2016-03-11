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
    private String classNameIndex;
    private Set<String> classURIs;
    
    /**
     * Defines the classNameIndex.
     * @param data The class name in the inverted index 
     */
    public void setClassNameIndex(String data)
    {
        classNameIndex=data;
    }
    
    /**
     * Defines the classNames.
     * @param data The set of referencing RDF classes 
     * in the keyword index. 
     */
    public void setClassNames(Set<String> data)
    {
        classURIs=data;
    }
    
     /**
     * Defines the classNames.
     * @param data An reference RDF classes 
     * to be added in the Term index. 
     * @author gkirtzou
     */
    public void addClassNames(String data)
    {
        classURIs.add(data);
    }
    /**
     * Retrieves the classNames.
     * @return The set of referencing RDF classes
     * in the keyword index.
     */
    public Set<String> getClassNames()
    {
        return classURIs;
    }
    
    /**
     * Retrieves the classNameIndex.
     * @return The class name in the inverted index.
     */
    public String getClassNameIndex()
    {
        return classNameIndex;
    }
    
    @Override
    public String toString() {
        String str = "[ Class name:" + this.classNameIndex
                + "\n Class References:\n"+ this.classURIs.toString()
                + "]\n";
        return(str);
    }
}
