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
            throws IOException {
        ConstantsSingleton constants = ConstantsSingleton.getInstance();
        DatabasePut edp = new DatabasePut(constants.bdbfiles_path);
                
        String ClassCsvFile = "/home/gkirtzou/Work/Projects/KeywordSearch/UseCasesData/DBpedia/TestDataForBerkeyleyDB/RDFClasses.csv";
        String PropertiesCsvFile = "/home/gkirtzou/Work/Projects/KeywordSearch/UseCasesData/DBpedia/TestDataForBerkeyleyDB/RDFProperties.csv";
        
        System.out.println("Named Graph <" + constants.named_graph +">");
        System.out.println("loading class db....");
        edp.loadClassDb(ClassCsvFile);
        System.out.println("loading property db....");
       // edp.loadPropertyDb(constants.query_prefix, constants.prefixes, constants.endpoint, constants.named_graph);
         edp.loadPropertyDb(PropertiesCsvFile);
       /* edp.loadLiteralDb(constants.query_prefix, constants.prefixes, constants.endpoint, "/home/fil/Class-Aedges2.org");     
        edp.loadLiteralCaseInsensitiveIndexes();
        */
        System.out.println("loading caseinsensitive db....");
        edp.loadClassCaseInsensitiveIndexes();
        edp.loadPropertyCaseInsensitiveIndexes();
        edp.close();
        
        /*
        System.out.println("reading class db....");
        
        BerkeleyDBStorage db = null;
        db = new BerkeleyDBStorage(constants.bdbfiles_path);
        // Show all RDF classes order by their Name
        EntityCursor<RdfClass> items = db.getClassCursor();
        for (RdfClass item : items) {
            System.out.println(item);
        }
        items.close();
       // Show inverted Index for RDF classes
       System.out.println(db.containsClassInvertedIndex("name"));
       */
     
    }
}
