/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.sparql2nl.naturallanguagegeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dllearner.algorithms.isle.WordNet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import org.apache.xmlbeans.ResourceLoader;

import edu.smu.tspell.wordnet.NounSynset;
import java.sql.Timestamp;

/**
 *
 * @author ngonga
 */
public class PropertyProcessor {
	
    private static final Logger logger = Logger.getLogger(PropertyProcessor.class);
    
    private double threshold = 2.0;
    private Preposition preposition;
    WordNetDatabase database;
    
    private final String VERB_PATTERN = "^((VP)|(have NP)|(be NP P)|(be VP P)|(VP NP)).*";
	private StanfordCoreNLP pipeline;
	private boolean useLinguistics = true;

    public PropertyProcessor(String dict) {
        // ------ ------
       // String dictionary = "C:/Users/Kostas/Dropbox/OPA/DIDAKT+DIPL_PMS/IPSY - SPARQL/SPARQL2NL-master-Kostis/SPARQL2NL-master/resources/wordnetWindows/dict"; //desktop @ lab <-------- -------- --------
        ClassLoader loader = PropertyProcessor.class.getClassLoader();
        System.out.println("We have to find the path for the wordnet dictionary.");
        System.out.println("Pou einai to adj: "+loader.getResource("resources/wordnet/windows/data.adj"));
        //String path="";
    /*    try {
            System.out.println("I am running in directory: " + getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            try {
                path = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                java.util.logging.Logger.getLogger(PropertyProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(PropertyProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }  */
        //String path = getClass().getProtectionDomain().getCodeSource().getLocation().toString().replace("/target/classes/", "") + dict;
        
        //path = path.replace("file:/", "");
      //  path = path + dict;
     //   System.out.println("path = " + path);
     /*   File f = new File(path);
        if (!f.exists())
        {
            path = path.replace("resources", "");
            path = path.replace("wordnetWindows/dict", "wordnet/windows/");
            path = path.replace("sparql2nl-1.jar/", "");
        }  */
        // ------ ------
    //    String dictionary = path;
        //String in = ResourceLoader.class.getResource("/wordnetWindows/dict/").getPath();//to vriskei...
        String in1 = PropertyProcessor.class.getResource("/wordnet/windows/").getPath();
        System.out.println("in1 = " + in1);
       //-------------
        //ClassLoader classloader = getClass().getClassLoader();
        //URL dataFile = classloader.getResource("/wordnet/windows/index.sense");
       //------------- 
        //String inn = in.toString().replace("file:", "");
        String inn = in1.replace("index.sense", "");
       // if (inn.contains("jar!"))
       //     inn = "jar:" + inn;
        System.out.println("path inn ----> " + inn);
        
        //System.setProperty("wordnet.database.dir", dictionary);
        System.setProperty("wordnet.database.dir", inn);
        System.out.println("### MOLIS EKANA SetProperty(wordnet.database.dir, ###) ###");
        database = WordNetDatabase.getFileInstance();
        System.out.println("### MOLIS EKANA KAI getFileInstance() ###");
        //----DOKIMES------------
        NounSynset nounSynset;
        NounSynset[] hyponyms;
       // System.out.println("### Pame na kanoume ma dokimh apo edw...");
        Synset[] synsets = database.getSynsets("fly", SynsetType.NOUN);
        //System.out.println("### MOLIS EKANA getSynsets()");
        
        String t = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("#1#TIMESTAMP: " + t);
        
        for (int i = 0; i < synsets.length; i++) {
            nounSynset = (NounSynset)(synsets[i]);
            hyponyms = nounSynset.getHyponyms();
            System.err.println(nounSynset.getWordForms()[0] +
            ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
        } 
        //----      -------------
        preposition = new Preposition(this.getClass().getClassLoader().getResourceAsStream("preposition_list.txt"));
        
        Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
		props.put("ssplit.isOneSentence","true");
		pipeline = new StanfordCoreNLP(props);
                
        String t1 = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("#2#TIMESTAMP: " + t1);
    }

    public enum Type {
        VERB, NOUN, UNKNOWN;
    }

    public Type getType(String property) {
    	property = property.trim();
        System.out.println("-- -- -- -- -- -- -- -- // -- -- -- -- -- -- -- --");
    	logger.info("Getting lexicalization type for \"" + property + "\"...");
    	
    	Type type = getLinguisticalType(property);
    	if(type == Type.UNKNOWN){
    		type = getTypeByWordnet(property);
    	}
    	logger.info("Type("+ property + ")=" + type.name());
        return type;
    }
    
    public Type getTypeByWordnet(String property){
    	logger.info("...using WordNet based analysis...");
    	
    	 //length is > 1
        if (property.contains(" ")) {
            String split[] = property.split(" ");
            String lastToken = split[split.length - 1];
            //first check if the ending is a preposition
            //if yes, then the type is that of the first word
            if (preposition.isPreposition(lastToken)) {
            	String firstToken = split[0];
                if (getTypeByWordnet(firstToken) == Type.NOUN) {
                    return Type.NOUN;
                } else if (getTypeByWordnet(firstToken) == Type.VERB) {
                    return Type.VERB;
                }
            }
            if (getTypeByWordnet(lastToken) == Type.NOUN) {
                return Type.NOUN;
            } else if (getTypeByWordnet(split[0]) == Type.VERB) {
                return Type.VERB;
            } else {
                return Type.NOUN;
            }
        } else {
            double score = getScore(property);
			if (score < 0) {// some count did not work
				return Type.UNKNOWN;
			}
			if (score >= threshold) {
				return Type.NOUN;
			} else if (score < 1 / threshold) {
				return Type.VERB;
			} else {
				return Type.NOUN;
			}
        }
    }
    
    public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

    /**
     * Returns log(nounCount/verbCount), i.e., positive for noun, negative for
     * verb
     *
     * @param word Input token
     * @return "Typicity"
     */
    public double getScore(String word) {
        double nounCount = 0;
        double verbCount = 0;
        logger.debug("Checking " + word);
        Synset[] synsets = database.getSynsets(word, SynsetType.NOUN);
        for (int i = 0; i < synsets.length; i++) {
            String[] s = synsets[i].getWordForms();
            for (int j = 0; j < s.length; j++) {//System.out.println(s[j] + ":" + synsets[i].getTagCount(s[j]));
                nounCount = nounCount + Math.log(synsets[i].getTagCount(s[j]) + 1.0);
            }
        }

        synsets = database.getSynsets(word, SynsetType.VERB);
        for (int i = 0; i < synsets.length; i++) {

            String[] s = synsets[i].getWordForms();
            for (int j = 0; j < s.length; j++) {//System.out.println(s[j] + ":" + synsets[i].getTagCount(s[j]));
                verbCount = verbCount + Math.log(synsets[i].getTagCount(s[j]) + 1.0);
            }
        }
//        System.out.println("Noun count = "+nounCount);
//        System.out.println("Verb count = "+verbCount);
//        //verbCount = synsets.length;
        if (word.equals("name"))
            return 1.0;     //to name synh8ws einai NOUN...omws ta synsets sta opoia vrisketai mesa, ta perissotera einai VERB..giauto kai paremvainoume
            
        if (verbCount == 0 && nounCount == 0) {
            return 1.0;
        }
        if (verbCount == 0) {
            return Double.MAX_VALUE;
        }
        if (nounCount == 0) {
            return 0.0;
        } else {
            return nounCount / verbCount;
        }
    }

    public ArrayList<String> getAllSynsets(String word) {
        ArrayList<String> synset = new ArrayList<String>();

        WordNetDatabase database = WordNetDatabase.getFileInstance();
        Synset[] synsets = database.getSynsets(word, SynsetType.NOUN, true);
        for (int i = 0; i < synsets.length; i++) {
            synset.add("NOUN " + synsets[i].getWordForms()[0]);
        }
        synsets = database.getSynsets(word, SynsetType.VERB, true);
        for (int i = 0; i < synsets.length; i++) {
            synset.add("VERB " + synsets[i].getWordForms()[0]);
        }

        System.out.println(synset);
        return synset;
    }

    public String getInfinitiveForm(String word) {

        String[] split = word.split(" ");
        String verb = split[0];

        //check for past construction that simply need an auxilliary
        if (verb.endsWith("ed") || verb.endsWith("un") || verb.endsWith("wn") || verb.endsWith("en")) {
            return "be " + word;
        }

        ArrayList<String> synset = new ArrayList<String>();
        WordNetDatabase database = WordNetDatabase.getFileInstance();
        Synset[] synsets = database.getSynsets(verb, SynsetType.VERB, true);
        double min = verb.length();
        String result = verb;
        for (int i = 0; i < synsets.length; i++) {
            String[] wordForms = synsets[i].getWordForms();
            for (int j = 0; j < wordForms.length; j++) {
                if (verb.contains(wordForms[j])) {
                    result = wordForms[j];
                    if (split.length > 1) {
                        for (int k = 1; k < split.length; k++) {
                            result = result + " " + split[k];
                        }
                    }
                    return result;
                }
            }
        }
        return word;
    }
    
	private Type getLinguisticalType(String text) {
		logger.info("...using linguistical analysis...");
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		String pattern = "";
		for (CoreMap sentence : sentences) {
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			//get the first word and check if it's 'is' or 'has'
			CoreLabel token = tokens.get(0);
			String word = token.get(TextAnnotation.class);
			String pos = token.get(PartOfSpeechAnnotation.class);
			String lemma = token.getString(LemmaAnnotation.class);
			
			//if first token is form for 'be' or 'has' we can return verb
			if(lemma.equals("be") || word.equals("has")){
				return Type.VERB;
			}
			
			if(lemma.equals("be") || word.equals("have")){
				pattern += lemma;
			} else {
				if(pos.startsWith("N")){
					pattern += "NP";
				} else if(pos.startsWith("V")){
					pattern += "VP";
				} else {
					pattern += pos;
				}
			}
			if(tokens.size() > 1){
				pattern += " ";
				for (int i = 1; i < tokens.size(); i++) {
					token = tokens.get(i);
					pos = token.get(PartOfSpeechAnnotation.class);
					if(pos.startsWith("N")){
						pattern += "NP";
					} else if(pos.startsWith("V")){
						pattern += "VP";
					} else {
						pattern += pos;
					}
					pattern += " ";
				}
			}
			//get parse tree
			// this is the parse tree of the current sentence
		      Tree tree = sentence.get(TreeAnnotation.class);
		      logger.debug("Parse tree:" + tree.pennString());
		}
		pattern = pattern.trim();
		
		//check if pattern matches
		if(pattern.matches(VERB_PATTERN)){
			logger.info("...successfully determined type.");
			return Type.VERB;
		} else {
			logger.info("...could not determine type.");
			return Type.UNKNOWN;
		}
	}


    public static void main(String args[]) {
        PropertyProcessor pp = new PropertyProcessor("resources/wordnet/dict");
        
      //  PropertyProcessor pp = new PropertyProcessor("C:/Users/Kostas/Dropbox/OPA/DIDAKT+DIPL_PMS/IPSY - SPARQL/SPARQL2NL-master/resources/wordnet/dict");
      //  PropertyProcessor pp = new PropertyProcessor("C:/Users/Kostas/Dropbox/OPA/DIDAKT+DIPL_PMS/IPSY - SPARQL/SPARQL2NL-master/resources/wordnet");
        String token = "birth place";
       // System.out.println(pp.getScore(token));   //<--------
       // System.out.println(pp.getType(token));    //<--------
       // System.out.println(pp.getAllSynsets(token));  //<--------
       // System.out.println(pp.getInfinitiveForm(token));  //<--------
        token = "has color";
     //   System.out.println(pp.getType(token));    //<--------
        
        token = "was hard working";
      //  System.out.println(pp.getType(token));    //<--------
    }
}
