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
 * Class for initialization
 * @author serafeim
 */
public class Initialize {
    ConnectionSingleton con = null;
    ConstantsSingleton constants = null;
    
    /**
     * Class constructor
     */
    public Initialize(){
        con = ConnectionSingleton.getInstance();
        constants = ConstantsSingleton.getInstance();
    }
    
    /**
     * Gets a connection to Berkeley DB
     * @return a connection to Berkeley DB
     */
    public BerkeleyDBStorage getDB(){
        return con.getDB();
    }
    
    /**
     * Gets constants used
     * @return constants
     */
    public ConstantsSingleton getConstants(){
        return constants;
    }
}
