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

import com.hp.hpl.jena.query.QuerySolution;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The query response
 * @author serafeim
 */
public class QueryResponse {
    List<QuerySolution> resultSet;
    long queryTime;
    
    /**
     * Class constructor
     */
    public QueryResponse(){
        resultSet = new ArrayList<>();
        queryTime = 0;
    }
    
    /**
     * Gets query result set
     * @return a list with the query results
     */
    public List<QuerySolution> getResultSet(){
        return resultSet;
    }
    
    /**
     * Adds a result to the result set
     * @param result the new result to be added
     */
    protected void addResult(QuerySolution result){
        resultSet.add(result);
    }
    
    /**
     * Gets query execution time
     * @return the query execution time in millisec
     */
    public long getQueryTime(){
        return queryTime;
    }
    
    /**
     * Sets query execution time
     * @param time query execution time to set in millisec
     */
    protected void setQueryTime(long time){
        queryTime = time;
    }
    
    /**
     * Prints query execution time
     */
    public void printQueryTime(){
        System.out.println("Query took " + queryTime + " millis");
    }
    
    /**
     * Prints query result set
     */
    public void printResultSet(){
        if(this.resultSet.isEmpty()){
            System.out.println("No results found!");
            return;
        }
        for (QuerySolution result : resultSet){
            System.out.println(result.toString());
        }
    }
    
    /**
     * Gets column names of response
     * @return a list with the column names, null if no results
     */
    public List<String> getColumnNames(){
        
        if(this.resultSet.isEmpty()){
            return null;
        }
        
        List<String> columnNames = new ArrayList<>();
        QuerySolution s = this.resultSet.get(0);
        Iterator<String> it = s.varNames();
        while(it.hasNext()){
            columnNames.add(it.next());
        }
        
        return columnNames;
    }
}
