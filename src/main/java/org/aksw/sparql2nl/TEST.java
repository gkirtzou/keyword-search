/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.sparql2nl;


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



public class TEST {
	
	@Test
	public void testSPARQL2NL() throws Exception {
		Lexicon lexicon = new NIHDBLexicon("/home/me/tools/lexAccess2013lite/data/HSqlDb/lexAccess2013.data");
                SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();
		//QueryExecutionFactory qef = new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs());
		//SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();
		//SimpleSPARQL2NLConverter sparql2nlConverter = new SimpleSPARQL2NLConverter(endpoint, "cache/sparql2nl", lexicon);
		
		SimpleNLGwithPostprocessing2 snlg = new SimpleNLGwithPostprocessing2(endpoint);
		
		//for (Query query : QALDBenchmark.getQueries(9,10,12)) {
                    String query3 = "PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX res: <http://dbpedia.org/resource/> "
                + "SELECT COUNT(DISTINCT ?uri) "
                + "WHERE { "
                + "?uri rdf:type dbo:Mountain . "
                + "?uri dbo:locatedInArea res:Nepal . "
                + "?uri dbo:elevation ?elevation . "
                + "res:Mansiri_Himal dbo:border ?uri . "
                + "FILTER (?elevation > 8000) . "
                //+ "FILTER (!BOUND(?date))"
                + "}";
			System.out.println(query3);		
                        QueryFactory qf = new QueryFactory();
                        Query q = qf.create(query3);
			
//			String nlr = sparql2nlConverter.getNLR(query);
//			System.out.println(nlr);
                        
			String nlr = snlg.getNLR(q);
			System.out.println(nlr);
		//}
	}
	
    public static void main(String[] args) {
       
       
        
      //  String[] queries = {query,query2,query2b,query2c,query3,query3b,query4,query5,query6,query7,query8,query9,query10,query11,query14};
        //String[] queries = {query,query2,query2b,query2c,query3,query3b,query4,query5,query6,query7,query8,query9,query10,query11,query14};
       // String[] queries = {query3};
        String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                    "PREFIX d2r: <http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#> " +
                    "PREFIX diana: <http://snf-541101.vm.okeanos.grnet.gr:8080/diana_lod/resource/diana/> " +
                    "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                    "PREFIX map: <http://snf-541101.vm.okeanos.grnet.gr:2020/resource/#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                    "PREFIX meta: <http://www4.wiwiss.fu-berlin.de/bizer/d2r-server/metadata#>";
        String q1 = PREFIX
                + "SELECT distinct ?hairpin ?name where {"
                + "?hairpin rdf:type diana:Hairpin."
                + "?hairpin diana:name ?name."
                + "FILTER(?name = \"let7\")}";
        
        String q2 = PREFIX + "Select distinct ?hairpin ?mature where {" 
                + "?hairpin diana:producesMature ?mature."
                + "?hairpin rdf:type diana:Hairpin."
                + "?mature rdf:type diana:Mature.}";
                
        String q3 = PREFIX + "Select distinct ?hairpin ?gene where { "
                + "?hairpin rdf:type diana:Hairpin."
                + "?gene rdf:type diana:Gene."
                + "?gene diana:producesHairpin ?hairpin.}";
        
        String q4 = PREFIX + "Select distinct ?hairpin ?name ?pm ?paper where {"
            + "?hairpin rdf:type diana:Hairpin."
            + "?hairpin diana:name ?name." 
            + "?pm diana:hasMirna ?hairpin."
                + "?pm rdf:type diana:PaperMirna."
                + "?pm diana:hasPaper ?paper."
                + "?paper rdf:type diana:Paper.}";
       
        String q5 = PREFIX + "Select distinct ?m ?s ?seq where {"
                + "?m rdf:type diana:Mature."
                + "?m diana:sequence ?seq."
                + "?m diana:species ?s."
                + "?s rdf:type diana:Species}";
       
        String q6 = PREFIX + "Select distinct ?i ?score where {"
                + "?i rdf:type diana:Interaction."
                + "?i diana:score ?score}";
        
        String q7 = PREFIX + "Select distinct ?i ?score where {"
                + "?i rdf:type diana:Interaction."
                + "?i diana:score 1.5}";
        String q8 = PREFIX + "Select distinct ?i ?score where {"
                + "?i rdf:type diana:Interaction."
                + "?i diana:score ?score."
                + "FILTER(?score = 1.5)}";
        String[] queries = { q1, q2, q3, q4, q5, q6, q7, q8};
        try {  //---sxoliase apo edw....
            SparqlEndpoint ep = new SparqlEndpoint(new URL("http://leonardo.imis.athena-innovation.gr:8891/diana/sparql"));
         //   SparqlEndpoint ep = SparqlEndpoint.getEndpointDBpedia();
            Lexicon lexicon = Lexicon.getDefaultLexicon();
            SimpleNLGwithPostprocessing2 snlg = new SimpleNLGwithPostprocessing2(ep);
            int count = 0;
            for (String q : queries) {
                System.out.println("\n------------  ------------------  -------------------  ---------------");
                System.out.println("QUERY_#" + ++count + " " + q);
                Query sparqlQuery = QueryFactory.create(q, Syntax.syntaxARQ);
                
                //try{
                    String ss = snlg.getNLR(sparqlQuery);
                  //  System.out.println("H getNLR epistrefei: " + ss);
                //}catch (Exception ee)
                //{   System.out.println();
                //    System.err.println("####### " + ee + " Pali eskase h getNLR()... #######");}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }   //---mexri edw...
       /*  for (String q : queries) {
            String res[] = new String[2];
            res = getQueryNL(q, "http://leonardo.imis.athena-innovation.gr:8891/diana/sparql");
            System.out.println(res[0]);
            System.out.println(res[1]);
         }  */
    }
    
    public/* static*/ String[] getQueryNL (String query, String url)
    {
        String queryNL[] = new String[2];
        queryNL[0] = query; //initialize with the SPARQL query..if something goes wrong, it will return that unaffected
        queryNL[1] = query;
        try {
            SparqlEndpoint ep = new SparqlEndpoint(new URL(url));
         //   SparqlEndpoint ep = SparqlEndpoint.getEndpointDBpedia();
            Lexicon lexicon = Lexicon.getDefaultLexicon();
            SimpleNLGwithPostprocessing2 snlg = new SimpleNLGwithPostprocessing2(ep);
            //int count = 0;
            //for (String q : queries) {
                System.out.println("\n------------  ------------------  -------------------  ---------------");
              //  System.out.println("QUERY_#" + ++count + " " + q);
                Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
                
                //try{
                    queryNL = snlg.getNLRRR(sparqlQuery);
                //}catch (Exception ee)
                //{   System.out.println();
                //    System.err.println("####### " + ee + " Pali eskase h getNLR()... #######");}
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryNL;
    }
}
