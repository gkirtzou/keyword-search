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
package org.keywordsearch.init;

import berkeleydbje.BerkeleyDBStorage;

/**
 * Class for connecting to Berkeley DB
 * @author serafeim
 */
public class ConnectionSingleton {
    
    private static ConnectionSingleton instance = null;
    private BerkeleyDBStorage db = new BerkeleyDBStorage(ConstantsSingleton.getInstance().bdbfiles_path);

    
    protected ConnectionSingleton(){

    }
    
    /**
     * Gets instance of the singleton class
     * @return Instance of the class
     */
    public static ConnectionSingleton getInstance(){     
        if(instance == null){
            instance = new ConnectionSingleton();
//            System.out.println("singleton created");
        }

        return instance;
    }
    
    /**
     * Gets connection to Berkeley DB
     * @return Berkeley DB connection
     */
    public BerkeleyDBStorage getDB(){
        return db;
    }

    
}
