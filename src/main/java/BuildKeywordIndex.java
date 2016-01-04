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
import java.io.File;
import java.io.FileNotFoundException;
//import java.nio.file.*;
import java.util.Scanner;
import java.util.*;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
/**
 * This example class demonstrates the creation of the
 * Keyword Index using Oracle Berkeley DB Java Edition.
 * @author fil
 */
public class BuildKeywordIndex {
    public static void main(String args[]) {
        String serverEndpoint = "http://snf-629975.vm.okeanos.grnet.gr:8190/ai4b/sparql";
        String prefixes =   "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                            "PREFIX d2r: <http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#> " +
                            "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                            "PREFIX map: <http://localhost:2020/resource/#> " +
                            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                            "PREFIX sym: <http://snf-629975.vm.okeanos.grnet.gr:8890/resource/sym/> ";
        
        String vocabulary="sym";
        DatabasePut edp = new DatabasePut("/home/fil/dbfilesAI4B4");
        System.out.println("loading class db....");
        edp.loadClassDb(prefixes, serverEndpoint);
        System.out.println("loading property db....");
        edp.loadPropertyDb(vocabulary, prefixes, serverEndpoint);
        System.out.println("loading literal db....");
        edp.loadLiteralDb(vocabulary, prefixes, serverEndpoint, "/home/fil/Class-Aedges2.org");
        System.out.println("loading caseinsensitive db....");
        edp.loadClassCaseInsensitiveIndexes();
        edp.loadPropertyCaseInsensitiveIndexes();
        edp.loadLiteralCaseInsensitiveIndexes();
        edp.close();
        /*System.out.println("reading class db....");
        
        BerkeleyDBStorage db = null;
        try{
            db = new BerkeleyDBStorage("/home/fil/dbfilesAI4B");
            EntityCursor<RdfClass> items = db.getClassCursor();

            try {
                for (RdfClass item : items) {
                    System.out.println(item.getClassName());
                    
                }
            } finally {
                items.close();
            }
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    db.close();
     */    
    }
}
