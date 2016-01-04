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
package org.keywordsearch.sparqllib;

/**
 * SPARQL Query Library Interface
 * @author serafeim
 */
public interface ISPARQLQueryLib {
    /**
     * Connects to SPARQL endpoint
     * @param serverEndpoint URL of the SPARQL Endpoint
     */
    void connect(String serverEndpoint);
    
    /**
    * Sets SPARQL prefixes
    * @param prefixes Prefixes to be used
    */
    void setPrefixes(String prefixes);
    
    /**
     * Sends a query to server and gets results
     * @param queryString The query to be executed
     * @return The results
     */
    QueryResponse sendQuery(String queryString);
    
    /**
     * Sends a query to the server and gets results
     * @param serverEndpoint URL of the SPARQL Endpoint
     * @param prefixes The prefixes to be used
     * @param queryString The query to be executed
     * @return The results
     */
    QueryResponse sendQuery(String serverEndpoint, String prefixes, String queryString);
}
