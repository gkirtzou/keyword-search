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

import java.util.Set;

/**
 * This interface should be implemented by any class that describes
 * data structures used to form the Term index.
 *
 * @author fil
 * @author serafeim
 * @author gkirtzou
 */
public interface IStorageEngine {
    boolean containsClass(String className); // called to check a RDF class existence in keyword index
    boolean containsProperty(String propertyName); // called to check a property existence in keyword index
    boolean containsLiteral(String literalName); // called to check a literal existence in keyword index
    boolean containsClassInvertedIndex(String className); // called to check a class existence in the corresponding inverted index
    boolean containsPropertyInvertedIndex(String propertyName); // called to check a property existence in the corresponding inverted index
    boolean containsLiteralInvertedIndex(String literalName); // called to check a literal existence in the corresponding inverted index
    
    Set<String[]> getProperty(String propertyName); // called to return a set of relative classes for the given property
    Set<String> getLiteral(String literalName); // called to return a set of relative class-property pairs for the given literal
    Set<String> getRefProperties(String propertyIndex); // return a set of referenced properties for the given property
    Set<String> getRefLiterals(String literalIndex); // return a set of referenced literals for the given literal
    Set<String> getRefClasses(String classNameIndex); // return a set of referenced classes for the given RDF class
}
