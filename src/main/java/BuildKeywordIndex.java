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

import berkeleydbje.*;
import com.sleepycat.persist.EntityCursor;
import java.io.IOException;
import org.keywordsearch.init.ConstantsSingleton;

/**
 * This example class demonstrates the creation of the
 * Keyword Index using Oracle Berkeley DB Java Edition.
 * @author fil
 * @author gkirtzou
 */
public class BuildKeywordIndex {
    public static void main(String args[]) 
            throws IOException, Exception {
        ConstantsSingleton constants = ConstantsSingleton.getInstance();
        DatabasePut edp = new DatabasePut(constants.bdbfiles_path);
                
        String ClassCsvFile = "/home/gkirtzou/Dropbox/Work/Projects/LODGOV/KeywordSearch/UseCasesData/DBpedia/TestDataForBerkeyleyDB/RDFClasses.csv";
        String PropertiesCsvFile = "/home/gkirtzou/Dropbox/Work/Projects/LODGOV/KeywordSearch/UseCasesData/DBpedia/TestDataForBerkeyleyDB/RDFProperties.csv";
               
        System.out.println("Named Graph <" + constants.named_graph +">");
        System.out.println("loading class db....");
        //edp.loadClassDb(constants.prefixes, constants.endpoint, constants.named_graph);
        edp.loadClassDb(ClassCsvFile);
        System.out.println("loading property db....");
        //edp.loadPropertyDb(constants.prefixes, constants.endpoint, constants.named_graph);
        edp.loadPropertyDb(PropertiesCsvFile);
        edp.loadLiteralDb(constants.prefixes, constants.endpoint, constants.named_graph);     
             
       
        System.out.println("loading caseinsensitive db....");
        edp.loadClassCaseInsensitiveIndexes();
        edp.loadPropertyCaseInsensitiveIndexes();        
        edp.loadLiteralCaseInsensitiveIndexes();
        edp.close();
    
        
        System.out.println("reading literal db....");        
        BerkeleyDBStorage db = new BerkeleyDBStorage(constants.bdbfiles_path);
   /*     // Show all RDF classes order by their Name
        EntityCursor<RdfClass> itemsC = db.getClassCursor();
        for (RdfClass itemC : itemsC) {
            System.out.println(itemC.toString());
        }
       
        // Show all RDF properties order by their Name
        EntityCursor<Property> itemsP = db.getPropertyCursor();
        for (Property itemP : itemsP) {
            System.out.println(itemP.toString());
        }
        
        // Show all literal values order by their Name
        EntityCursor<Literal> itemsL = db.getLiteralCursor();
        for (Literal itemL : itemsL) {
            System.out.println(itemL.toString());
        }
        */
       // Test inverted index
       System.out.println("B " + db.containsLiteralInvertedIndex("B"));
       System.out.println("Switch " + db.containsLiteralInvertedIndex("switch"));
       System.out.println("Valerie Whittingham " + db.containsLiteralInvertedIndex("ValeRie Whittingham"));
       System.out.println("Person class "+ db.containsClassInvertedIndex("person"));
       
    }
}
