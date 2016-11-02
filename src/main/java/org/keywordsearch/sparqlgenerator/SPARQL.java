/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.keywordsearch.sparqlgenerator;

/**
 *
 * @author gkirtzou
 */
public class SPARQL  {
    private String[] sparqlQueryArray;
    private String sparqlQuery;
    private double weightNumTriplets;
    private double weightAverageSP;
    private double weightLongestSP;

    
    public SPARQL() {
        this.sparqlQueryArray = null;
        this.sparqlQuery = null;
        this.weightNumTriplets = 0.0;
        this.weightAverageSP = 0.0;
        this.weightLongestSP = 0.0; 
    }    
    
    public SPARQL(String[] query) {
        this.sparqlQueryArray = query;
        this.sparqlQuery = "";
        for(int i=0; i< this.sparqlQueryArray.length; i++) {
            this.sparqlQuery += this.sparqlQueryArray[i] + " ";
        }
        this.weightNumTriplets = 0.0;
        this.weightAverageSP = 0.0;
        this.weightLongestSP = 0.0;
    }
    
    public SPARQL(String[] query, int weightNumTriplets) {
        this.sparqlQueryArray = query;
        this.sparqlQuery = "";
        for(int i=0; i< this.sparqlQueryArray.length; i++) {
            this.sparqlQuery += this.sparqlQueryArray[i] + " ";
        }
        this.weightNumTriplets = weightNumTriplets;
        this.weightAverageSP = 0.0;
        this.weightLongestSP = 0.0;
     }
    
    
    public String[] getSparqlQueryArray () {
        return this.sparqlQueryArray;
    }
    
    public void setSparqlQueryArray (String[] value) {
        this.sparqlQueryArray = value;
    }
    
    public String getSparqlQuery () {
        return this.sparqlQuery;
    }
    
    public void setSparqlQuery (String value) {
        this.sparqlQuery = value;
    }
    
    
    public double getWeightNumTriplets () {
        return this.weightNumTriplets;
    }
    
    public void setWeightNumTriplets (double value) {
        this.weightNumTriplets = value;
    }
    
    public double getWeightAverageSP () {
        return this.weightAverageSP;
    }
    
    public void setWeightAverageSP (double value) {
        this.weightAverageSP = value;
    }
    
    public double getWeightLongestSP () {
        return this.weightLongestSP;
    }
    
    public void setWeightLongestSP (double value) {
        this.weightLongestSP = value;
    }
}
    

