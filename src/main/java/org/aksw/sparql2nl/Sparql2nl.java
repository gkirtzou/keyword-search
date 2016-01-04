/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.sparql2nl;

/**
 *
 * @author Tukei
 */
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import java.net.URL;
import org.aksw.sparql2nl.naturallanguagegeneration.SimpleNLGwithPostprocessing2;
//import org.aksw.sparql2nl.naturallanguagegeneration.SimpleSPARQL2NLConverter;
import org.junit.Test;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.NIHDBLexicon;
import org.dllearner.kb.sparql.SparqlEndpoint;
import com.hp.hpl.jena.query.QueryFactory;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

public class Sparql2nl {
    
    String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                    "PREFIX d2r: <http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#> " +
                    "PREFIX diana: <http://snf-541101.vm.okeanos.grnet.gr:8080/diana_lod/resource/diana/> " +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                    "PREFIX map: <http://snf-541101.vm.okeanos.grnet.gr:2020/resource/#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                    "PREFIX meta: <http://www4.wiwiss.fu-berlin.de/bizer/d2r-server/metadata#>";
    
    String URL = "";
    
    SparqlEndpoint ep = null;
    
    Lexicon lexicon = null;
    
    SimpleNLGwithPostprocessing2 snlg = null;
    
    
    public void setPrefixes(String Prefixes) {
        this.PREFIX = Prefixes;
    }
            
    public void setEndpoint(String endpoint_url) {
        this.URL = endpoint_url;
        try {
            ep = new SparqlEndpoint(new URL(endpoint_url));
            lexicon = Lexicon.getDefaultLexicon();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Sparql2nl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        snlg = new SimpleNLGwithPostprocessing2(ep);
    }
    
    public void init_url()
    {
        try {
            ep = new SparqlEndpoint(new URL("http://leonardo.imis.athena-innovation.gr:8891/diana/sparql"));
            lexicon = Lexicon.getDefaultLexicon();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Sparql2nl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        snlg = new SimpleNLGwithPostprocessing2(ep);
    }
    
   
    
    public  String[] getQueryNL (String query)
    {
        query = this.PREFIX + query;
        String queryNL[] = new String[2];
        queryNL[0] = query; //initialize with the SPARQL query..if something goes wrong, it will return that unaffected
        queryNL[1] = query;
        try {
            //SparqlEndpoint ep = new SparqlEndpoint(new URL(url));
            //Lexicon lexicon = Lexicon.getDefaultLexicon();
           // SimpleNLGwithPostprocessing2 snlg = new SimpleNLGwithPostprocessing2(ep);
            
           // System.out.println("\n------------  ------------------  -------------------  ---------------");
             
           // String t = new Timestamp(System.currentTimeMillis()).toString(); 
           // System.out.println("#EEMAAAA_Before#TIMESTAMP: " + t);
            
            Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);//auto edw mesa thn prwth-prwth fora ka8ysterei poly...
            
           // String tt = new Timestamp(System.currentTimeMillis()).toString(); 
           // System.out.println("#EEMAAAA_After#TIMESTAMP: " + tt);
                
            queryNL = snlg.getNLRRR(sparqlQuery);
                
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryNL;
    }
    
   
    
}
