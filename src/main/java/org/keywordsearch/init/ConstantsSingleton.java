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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class for the constants
 * @author penny
 */
public class ConstantsSingleton {
    
    private static ConstantsSingleton instance = null;
    
    /**
     * a SPARQL endpoint
     */
    public String endpoint = "";
    
    /**
     * Prefixes to be used
     */
    public String prefixes = "";
    
    /**
     * Query prefix to be used
     */
    public String query_prefix = "";
    
    /**
     * Path to Berkeley DB files
     */
    public String bdbfiles_path = "";
    
    /**
     * Mapping URIs to prefix
     */
    public Map<String, String> urlToPrefixMap = new HashMap<String, String>();
    
    
    /**
     * Class constructor
     */
    protected ConstantsSingleton(){
        getPropValues(); 
        parsePrefixes();
    }
    
    /**
     * Gets instance of the singleton class
     * @return 
     */
    public static ConstantsSingleton getInstance(){     
        if(instance == null){
            instance = new ConstantsSingleton();
//            System.out.println("constants singleton created");
        }
//        System.out.println("constants getInstance");
        return instance;
    }
    
    /**
     * Gets property values from property file
     */
    private void getPropValues(){
        Properties configFile = new Properties();
                
        try {
            configFile.load(ConstantsSingleton.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException ex) {
            Logger.getLogger(ConstantsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        endpoint = configFile.getProperty("endpoint");
        prefixes = configFile.getProperty("prefixes");
        query_prefix = configFile.getProperty("query_prefix");
        bdbfiles_path = configFile.getProperty("bdbfiles_path");
    }
    
    /**
     * Maps URIs to prefixes
     */
    private void parsePrefixes(){
        String[] prefixArray = prefixes.split("PREFIX");

        for (String prefix : prefixArray) {
            if(prefix.equals(""))
                continue;
            
//            System.out.println(prefix);
            String[] prefixParts = prefix.trim().split(" ");

            urlToPrefixMap.put(prefixParts[1].substring(1, prefixParts[1].length() - 1), prefixParts[0]);     
        }
    }
      
    /**
     * Gets URIs to prefixes map
     * @return 
     */
   public Map<String, String> getUrlToPrefixMap(){
       return urlToPrefixMap;
   }
    
}
