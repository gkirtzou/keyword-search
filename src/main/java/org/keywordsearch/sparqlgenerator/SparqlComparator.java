/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.keywordsearch.sparqlgenerator;

import java.util.Comparator;

/**
 *
 * @author gkirtzou
 */
public class SparqlComparator implements Comparator<SPARQL> {
    
    private boolean numTriplets = false;
    private boolean averageSP = false;
    private boolean longestSP = false;
    
    
    public SparqlComparator(boolean numTripletsValue, boolean averageSPValue, boolean longestSPValue) {
        this.numTriplets = numTripletsValue;
        this.averageSP = averageSPValue;
        this.longestSP = longestSPValue;
    }
    
    
    public boolean getNumTriplets () {
        return this.numTriplets;
    }
    
    public void setNumTriplets (boolean value) {
        this.numTriplets = value;
    }
    
    public boolean getAverageSP () {
        return this.averageSP;
    }
    
    public void setAverageSP (boolean value) {
        this.averageSP = value;
    }
    
    public boolean getLongestSP () {
        return this.longestSP;
    }
    
    public void setLongestSP (boolean value) {
        this.longestSP = value;
    }
    
    @Override
    public int compare(SPARQL o1, SPARQL o2) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;     
        double obj1Weight = 0.0;        
        double obj2Weight = 0.0;
        
        if (this.numTriplets ) {
            obj1Weight += o1.getWeightNumTriplets();
            obj2Weight += o2.getWeightNumTriplets();
        }
        else if (this.averageSP ) {
            obj1Weight += o1.getWeightAverageSP();
            obj2Weight += o2.getWeightAverageSP();
        }
        else if (this.longestSP ) {
            obj1Weight += o1.getWeightLongestSP();
            obj2Weight += o2.getWeightLongestSP();
        }
          
        if (obj1Weight > obj2Weight) {
            return AFTER;
        }
        else if (obj1Weight < obj2Weight) {
            return BEFORE;
        }
        else {
            return EQUAL;
        }
    }
}
    

