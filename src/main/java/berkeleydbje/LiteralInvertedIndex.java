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
 * This class describes the literal inverted 
 * index as a Berkeley DB entity. The inverted 
 * index refers to similar RDF literals of the
 * keyword index(e.g. lower case letters, keywords
 * excluding special characters etc).
 * @author fil
 */
@Entity
public class LiteralInvertedIndex {
    @PrimaryKey
    private String literalIndex;
    private Set<String> literals;
    
    /**
     * Defines the literalIndex.
     * @param data The literal name in the inverted index.
     */
    public void setLiteralIndex(String data)
    {
        literalIndex=data;
    }
    
    /**
     * Defines the literals. 
     * @param data The set of referencing RDF literals
     * in the keyword index
     */
    public void setLiterals(Set<String> data)
    {
        literals=data;
    }
    
    /**
     * Retrieves the literals.
     * @return The set of referencing RDF literals
     * in the keyword index
     */
    public Set<String> getLiterals()
    {
        return literals;
    }
     
    /**
     * Retrieves the literalIndex.
     * @return The literal name in the keyword index
     */
    public String getLiteralIndex()
    {
        return literalIndex;
    }
}
