package org.keywordsearch.sparqllib;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * SPARQL Query Library
 * @author serafeim
 */
public class SPARQLQueryLib implements ISPARQLQueryLib{

    private String serverEndpoint = null;
    private String prefixes = "";
    
    /**
     * Connects to SPARQL endpoint
     * @param serverEndpoint URL of the SPARQL Endpoint
     */
    @Override
    public void connect(String serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }

    /**
     * Sets SPARQL prefixes
     * @param prefixes prefixes to be used
     */
    @Override
    public void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }
    
    /**
     * Sends a query to the server and gets results
     * @param queryString The query to be executed
     * @return query results
     * @throws QueryParseException when query parse error occurs
     * @throws QueryException when server is unreachable
     */
    @Override
    public QueryResponse sendQuery(String queryString) throws QueryParseException, QueryException{
        
        //check if server endpoint is given
        if(this.serverEndpoint == null){
            throw new QueryException("No server endoint specified");
        }
        
        queryString = prefixes + " " + queryString;
        //org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        //...sorry 4 commentin the logger stuff...
        QueryResponse response = new QueryResponse();
        Query query = QueryFactory.create(queryString);

        long startTime = System.currentTimeMillis();
        
        //Executing SPARQL Query
        QueryExecution qexec = QueryExecutionFactory.sparqlService(serverEndpoint, query);
        
        //Retrieving the SPARQL Query results
        ResultSet results = qexec.execSelect();

        //set query execution time
        long endTime = System.currentTimeMillis();
        response.setQueryTime(endTime - startTime);

        //Iterating SPARQL Query results
        while (results.hasNext()) {
            QuerySolution s = results.nextSolution();
            response.addResult(s);
        }

        return response;
    }

    /**
     * Sends a query to the server and gets results
     * @param serverEndpoint URL of the SPARQL Endpoint
     * @param prefixes Prefixes to be used
     * @param queryString The query to be executed
     * @throws QueryParseException when query parse error occurs
     * @throws QueryException when server is unreachable
     * @return The results
     */
    @Override
    public QueryResponse sendQuery(String serverEndpoint, String prefixes, String queryString) 
                                    throws QueryParseException, QueryException {
        connect(serverEndpoint);
        setPrefixes(prefixes);
        QueryResponse response = sendQuery(queryString);
        return response;
    }

}
