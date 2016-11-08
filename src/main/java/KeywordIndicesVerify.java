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

import org.keywordsearch.init.ConstantsSingleton;

import com.sleepycat.persist.EntityCursor;
import berkeleydbje.BerkeleyDBStorage;
import berkeleydbje.Literal;
import berkeleydbje.Property;
import berkeleydbje.RdfClass;

/**
 * This example class shows the elements of the Term index
 * @author gkirtzou
 */
public class KeywordIndicesVerify {

	public static void main(String[] args) {
		
		ConstantsSingleton constants = ConstantsSingleton.getInstance();
        BerkeleyDBStorage db = new BerkeleyDBStorage(constants.bdbfiles_path);
        // Show all RDF classes order by their Name
        System.out.println("RDF Class in Term Index::");
        EntityCursor<RdfClass> itemsC = db.getClassCursor();
        for (RdfClass itemC : itemsC) {
            System.out.println(itemC);
        }
       
        // Show all RDF properties order by their Name
        System.out.println("\n\n\nRDF properties in Term Index::");
        EntityCursor<Property> itemsP = db.getPropertyCursor();
        for (Property itemP : itemsP) {
            System.out.println(itemP);
        }
        
        // Show all literal values order by their Name
        System.out.println("\n\n\nLiteral Values in Term Index::");
        EntityCursor<Literal> itemsL = db.getLiteralCursor();
        for (Literal itemL : itemsL) {
            System.out.println(itemL);
        }
        
    /*   // Test inverted index
       System.out.println("B " + db.containsLiteralInvertedIndex("B"));
       System.out.println("Switch " + db.containsLiteralInvertedIndex("switch"));
       System.out.println("Valerie Whittingham " + db.containsLiteralInvertedIndex("ValeRie Whittingham"));
       System.out.println("Person class "+ db.containsClassInvertedIndex("person"));
      */ 

	}

}
