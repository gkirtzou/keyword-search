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
package org.keywordsearch.sparqlgenerator;

import berkeleydbje.BerkeleyDBStorage;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author penny 
 * @author gkirtzou
 */
public class KeywordFunctions  {
        
    /**
     * Class variable: the keywords that the user inserted in the application search.
     */
    String keywords;
    private String[] keywordsArray;
    private boolean at;
    private String[] atValue;
    private boolean before;
    private String[] beforeValue;
    private boolean after;
    private String[] afterValue;
    
    /**
     * Constructor
     */
    public KeywordFunctions() {
        this.keywords = "";
        this.keywordsArray = null;
        this.at = false;
        this.before = false;
        this.after = false;
        this.atValue = new String[2];
        this.beforeValue = new String[2];
        this.afterValue = new String[2];
              
    }
   
    /**
     * Sets the property keywords of this class.
     * @param value A string with the keywords as given by the user that will be used in the search.
     */
    public void setKeywords(String value)
    {
        keywords = value;
    }
    
    /**
     * Gets the keywords that will be used in the search.
     * @return The keywords as given by the user that will be used in the search.
     */
    public String getKeywords() 
    { 
        return keywords; 
    }
    
    
     /**
     * Sets the property keywordsArray of this class.
     * @param value A string array with the keyword after being processed that will be used in the search.
     */
    public void setKeywordsArray(String[] value)
    {
        keywordsArray = value;
    }
    
    /**
     * Gets the keywordsArray that will be used in the search.
     * @return The keywords after being processed that will be used in the search.
     */
    public String[] getKeywordsArray() 
    { 
        return keywordsArray; 
    }
    

    /**
     * Processing the keywords inserted by the user (e.g. remove special characters, 
     * unnecessary spaces, temporal operators and their values, etc) and end up with a 
     * list of the keywords. 
     * @return A String[] of the keywords that the user inserted in the application.
     */
    public String[] processInputKeywords(String keywords) throws Exception
    {     
        this.keywords = keywords;
        this.at = false;
        this.before = false;
        this.after = false;
        this.atValue = new String[2];
        this.beforeValue = new String[2];
        this.afterValue = new String[2];
        
        List<String> keywordsList = new ArrayList<>();
        String kwords = this.keywords;

        //Remove special characters
        //kwords = kwords.replaceAll("[/#$%^&+=\\[\\];,/{}|:<>?!@~&^]", "");
        kwords = kwords.replaceAll("[/#$%^&+=\\[\\];,/{}|<>?!@~&^]", "");
               
        
       
 
        // Handle temporal operator AT - if present
        Pattern atPattern = Pattern.compile("(\\S+)\\s+AT\\s*:\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher atMatch = atPattern.matcher(kwords);     
        while (atMatch.find()){
            if (this.at) {
                throw new Exception("Cannot handle more than one temporal operators at!");
            }                        
            this.at = true;            
            String[] temp = atMatch.group(0).split("[\\s:]+");
            atValue[0] = temp[0].replace("\"", "").trim(); // temporal property
            atValue[1] = temp[2].replace("\"", "").trim(); // value
            kwords = kwords.replace(atMatch.group(0), "");
        }
       
        
         // Handle temporal operator BEFORE - if present
        Pattern beforePattern = Pattern.compile("(\\S+)\\s+BEFORE\\s*:\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher beforeMatch = beforePattern.matcher(kwords);     
        while (beforeMatch.find()){
            if (this.at) {
                throw new Exception("Cannot handle temporal operator at and before together!");
            }
            if (this.before) {
                throw new Exception("Cannot handle more than one temporal operators before!");
            }
            this.before = true;
            String[] temp = beforeMatch.group(0).split("[\\s:]+");
            beforeValue[0] = temp[0].replace("\"", "").trim(); // temporal property
            beforeValue[1] = temp[2].replace("\"", "").trim(); // value
            kwords = kwords.replace(beforeMatch.group(0), "");
        }
        
        
        // Handle temporal reserved keyword AFTER
        Pattern afterPattern = Pattern.compile("(\\S+)\\s+AFTER\\s*:\\s*(\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher afterMatch = afterPattern.matcher(kwords);     
        while (afterMatch.find()){
            if (this.at) {
                throw new Exception("Cannot handle temporal operator at and after together!");
            }
            if (this.after) {
                throw new Exception("Cannot handle more than one temporal operators after!");
            }
            this.after = true;
            String[] temp = afterMatch.group(0).split("[\\s:]+");
            afterValue[0] = temp[0].replace("\"", "").trim(); // temporal property
            afterValue[1] = temp[2].replace("\"", "").trim(); // value
            kwords = kwords.replace(afterMatch.group(0), "");
        }
               
        //Remove special characters
        kwords = kwords.replaceAll("[:]", "");
        
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher m = pattern.matcher(kwords);                
        while (m.find()){
            keywordsList.add(m.group(1));
            kwords = kwords.replace(m.group(0), "");
        }
        
        // Split keywords with white space delimiters 
        String[] parts = kwords.trim().split("\\s+"); 
               
        int nextPosition = parts.length;
        parts = Arrays.copyOf(parts, parts.length + keywordsList.size());
        
        // Make each key lower case
        int i = nextPosition;
        for(String k : keywordsList){
            parts[i] = k;
            i++;
        }
         
        this.keywordsArray = parts;
        return parts;
    }

    
    /**
     * Returns a HashMap with all the matches found for the keywords that the user inserted in the application.
     * @param kwords The keywords that the user inserted in the application.
     * @param classNames A HashMap with all the class names of the RDF schema. In each (key, value) pair, the key is a class 
     * name and the value is empty.
     * @param propertyNames A HashMap with all the property names of the RDF schema. In each (key, value) pair, the key is a 
     * property name and the value is the class name related to the key property.
     * @param literalNames A HashMap with all the literals in the RDF schema. In each (key, value) pair, the key is a 
     * literal and the value is a Set<property, class> of the related property and class. 
     * @return A HashMap with all the matches found for the keywords that the user inserted in the application.
     */
    public HashMap getKeywordMatches(HashMap classNames, HashMap propertyNames, HashMap literalNames) {
        
        HashMap keywordMatches = new HashMap();
        
        for (String kword : this.keywordsArray) {
            String currentKey = kword.trim();
            int isClass=0;
            int isProperty=0;
            int isLiteral=0;
            //Check if the current keyword is class name
            if(classNames.containsKey(currentKey)){
                isClass=1;
            }
            if(propertyNames.containsKey(currentKey)){
                isProperty=1;
            }
            if(literalNames.containsKey(currentKey)){
                isLiteral=1;
            }
            String kwordProcessed="";
            HashMap currentKeyword = new HashMap();
            if(isClass==0 && isProperty==0 && isLiteral==0){
                kwordProcessed="No matches exist for this keyword";
                out.println(kwordProcessed);
            }    
            else{
                int counter=0;
                if(isClass==1){
                    currentKeyword.put("C:"+currentKey, "");
                }   
                if(isProperty==1){
                    Set<String[]> propsTemp=(Set<String[]>)propertyNames.get(currentKey);
                    for (String[] s : propsTemp) {
                        String propDetails1="";
                        String propDetails2="";
                        if(s.length==1){
                            propDetails1 = s[0];
                            propDetails2 = "P"+counter+":"+currentKey;
                        }
                        else{
                            propDetails1 = s[0]+":"+s[1];
                            propDetails2 = "E"+counter+":"+currentKey;
                        }
                        
                        currentKeyword.put(propDetails2, propDetails1);
                        counter++;
                    }
                } 
                if(isLiteral==1){
                    Set<String> LiteralsTemp=(Set<String>)literalNames.get(currentKey);
                    for (String s : LiteralsTemp) {
                        currentKeyword.put("L"+counter+":"+currentKey, s);
                        counter++;
                    }
                } 
            }
            keywordMatches.put(currentKey, currentKeyword);            
        }
        
        return keywordMatches;
    }
    
    
    
    /**
     * In this function all possible combinations among the keyword matches
     * are computed.
     * @param kwords The keywords that the user inserted.
     * @param keywordMatches The keyword matches of the user keywords.
     * @return A set of all the possible combinations among the keyword matches.
     */
    public Set<HashMap> getKeywordCombinations(String[] kwords, HashMap keywordMatches){
        
        Set<HashMap> keywordCombinations = new HashSet<HashMap>();
                
        //Process the first keyword
        HashMap map=(HashMap)keywordMatches.get(kwords[0].trim());
        HashMap combinations = new HashMap();
        Set set = map.entrySet();
        Iterator i = set.iterator();

        while(i.hasNext()) {
            Map.Entry currentEntry1 = (Map.Entry)i.next();
            combinations.put(currentEntry1.getKey(), currentEntry1.getValue());
            keywordCombinations.add(combinations);
            combinations = new HashMap();
        }

        for(int j=1; j<kwords.length; j++){
            map=(HashMap)keywordMatches.get(kwords[j].trim());
            keywordCombinations=getKeywordSingleCombination(keywordCombinations, map);
        }
        
        return keywordCombinations;
    }
    
    /**
     * This function returns all possible combinations between two keywords.
     * @param map1 The keyword matches of the first keyword 
     * @param map2 The keyword matches of the second keyword
     * @return All possible combinations between two keywords.
     */
    public Set<HashMap> getKeywordSingleCombination(Set<HashMap> map1, HashMap map2){
        
        Set<HashMap> combinationsAll = new HashSet<HashMap>();
        HashMap combinations = new HashMap();
        
        for(HashMap currentEntry : map1) {

            Set set2 = map2.entrySet();
            Iterator i2 = set2.iterator();
            while(i2.hasNext()){
                
                Set set = currentEntry.entrySet();
                Iterator i = set.iterator();
            
                while(i.hasNext()) {
                    Map.Entry currentEntry1 = (Map.Entry)i.next();
                    combinations.put(currentEntry1.getKey(), currentEntry1.getValue());
                }
            
                Map.Entry currentEntry2 = (Map.Entry)i2.next();
                combinations.put(currentEntry2.getKey(), currentEntry2.getValue());
                combinationsAll.add(combinations);
                combinations=new HashMap();
                }
        }
        
        return combinationsAll;
    }

    
    /**
     * This functions gets a singleCombination of keyword matches as input and returns 
     * all the possible pairs of the combination components. Of course, this makes more sense 
     * in case of three user keywords or more. If the user has inserted one or two keywords, this 
     * function returns the singleCombination.
     * The pairs that are returned from this function are needed for the calculation of shortest 
     * paths in later steps of the algorithm.
     * @param kCombination 
     * @return A set of keyword pairs. All the keywords of the pairs belong to the same keyword 
     * combination.
     */
    public Set<String[]> getCombinationPairs(HashMap kCombination){
        
        Set<String[]> combinationsPairs = new HashSet<String[]>();
        
        Set<String> combination = kCombination.keySet(); 
        
        String[] combinationsArray = new String[combination.size()];
        int k=0;
        for(String str : combination) {
           combinationsArray[k]=str;
           k++;
        }
        
        for(int i=0; i<combinationsArray.length; i++){
            if(i+1<combinationsArray.length){
                for(int j=i+1; j<combinationsArray.length; j++){
                    String[] curPair = new String[2];
                    curPair[0]=combinationsArray[i];
                    curPair[1]=combinationsArray[j];
                    combinationsPairs.add(curPair);
                }
            }
        }
        
        return combinationsPairs;
    } 
    
    /**
     * This function finds matches the user keywords to the dbStorage.
     * @param dbStorage A BerkeleyDB structure containing all the necessary information from 
     * the RDF schema.
     * @param temporal_properties A list of the temporal properties that 
     * @return A HashMap with all the matches found for the keywords that the user inserted in 
     * the application. 
     */
    public HashMap getKeywordMatches(BerkeleyDBStorage dbStorage){
        
        HashMap keywordMatches = new HashMap();
        
        // Process keywords
        for (String kword : this.keywordsArray) {
            String currentKey = kword.trim();
            // Ignore empty keyword
            if (currentKey.equals(""))
                continue;
            String currentKeyLowerCase=currentKey.toLowerCase();
            int isClass=0;
            int isProperty=0;
            int isLiteral=0;
            //Check if the current keyword is class name
            if(dbStorage.containsClassInvertedIndex(currentKeyLowerCase)){
                isClass=1;
            }
            if(dbStorage.containsPropertyInvertedIndex(currentKeyLowerCase)){
                isProperty=1;
            }
            if(dbStorage.containsLiteralInvertedIndex(currentKeyLowerCase)){
                isLiteral=1;
            }
            
            if(isClass!=0 || isProperty!=0 || isLiteral!=0){
            
                HashMap currentKeyword = new HashMap();
                int counter=0;
                if(isClass==1){
                    Set<String> refClasses=dbStorage.getRefClasses(currentKeyLowerCase);
                    Iterator setIterator=refClasses.iterator();
                    while(setIterator.hasNext())
                    {
                        String refClassName=setIterator.next().toString();
                        currentKeyword.put("C:"+refClassName, "");
                    }
                }   
                if(isProperty==1){
                    Set<String> refProperties=dbStorage.getRefProperties(currentKeyLowerCase);
                    Iterator setIterator=refProperties.iterator();
                    while(setIterator.hasNext())
                    {
                        String refProperty=setIterator.next().toString();
                        Set<String[]> propsTemp= dbStorage.getProperty(refProperty);
                        for (String[] s : propsTemp) {
                            String propDetails1="";
                            String propDetails2="";
                            if(s.length==1){
                                propDetails1 = s[0];
                                propDetails2 = "P"+counter+":"+refProperty;
                            }
                            else{
                                propDetails1 = s[0]+":"+s[1];
                                propDetails2 = "E"+counter+":"+refProperty;
                            }
                            currentKeyword.put(propDetails2, propDetails1);
                            counter++;
                        }
                    }
                } 
                if(isLiteral==1){
                    Set<String> refLiterals=dbStorage.getRefLiterals(currentKeyLowerCase);
                    Iterator setIterator=refLiterals.iterator();
                    while(setIterator.hasNext())
                    {
                        String refLiteral=setIterator.next().toString();
                        Set<String> LiteralsTemp=(Set<String>)dbStorage.getLiteral(refLiteral);
                        for (String s : LiteralsTemp) {
                            currentKeyword.put("LE"+counter+":"+refLiteral, s);
                            counter++;
                            
                        }
                    }
                } 
                keywordMatches.put(currentKey, currentKeyword);     
            }

        }
        
        //Process keywords bind with temporal operators
        if (this.at) {
            System.out.println("Within processing at operator");
            String currentKeyLowerCase = this.atValue[0].toLowerCase();
            if (dbStorage.containsPropertyInvertedIndex(currentKeyLowerCase)) {
                HashMap currentKeyword = new HashMap();
                int counter = 0;
                Set<String> refProperties = dbStorage.getRefProperties(currentKeyLowerCase);
                Iterator setIterator = refProperties.iterator();                
                while (setIterator.hasNext()) {                    
                    String refProperty = setIterator.next().toString();
                    Set<String[]> propsTemp = dbStorage.getProperty(refProperty);
                    for (String[] s : propsTemp) {                   
                        String propDetails1 = "";
                        String propDetails2 = "";                                             
                        if (s.length == 1) {
                           propDetails2 = "LE" + counter + ":" + this.atValue[1];
                           propDetails1 = "prop:" + refProperty + ",class:" + s[0];
                        }
                        currentKeyword.put(propDetails2, propDetails1);
                        counter++;
                    }
                }
                keywordMatches.put(this.atValue[1], currentKeyword);
            }            
        }
        if (this.before) {
             System.out.println("Within processing before operator");
            String currentKeyLowerCase = this.beforeValue[0].toLowerCase();
            if (dbStorage.containsPropertyInvertedIndex(currentKeyLowerCase)) {
                HashMap currentKeyword = new HashMap();
                int counter = 0;
                Set<String> refProperties = dbStorage.getRefProperties(currentKeyLowerCase);
                Iterator setIterator = refProperties.iterator();                
                while (setIterator.hasNext()) {                    
                    String refProperty = setIterator.next().toString();
                    Set<String[]> propsTemp = dbStorage.getProperty(refProperty);
                    for (String[] s : propsTemp) {                   
                        String propDetails1 = "";
                        String propDetails2 = "";                                             
                        if (s.length == 1) {
                           propDetails2 = "LB" + counter + ":" + this.beforeValue[1];
                           propDetails1 = "prop:" + refProperty + ",class:" + s[0];
                        }
                        currentKeyword.put(propDetails2, propDetails1);
                        counter++;
                    }
                }
                keywordMatches.put(this.beforeValue[1], currentKeyword);
            }            
        }
        if (this.after) {
             System.out.println("Within processing after operator");
            String currentKeyLowerCase = this.afterValue[0].toLowerCase();
            if (dbStorage.containsPropertyInvertedIndex(currentKeyLowerCase)) {
                HashMap currentKeyword = new HashMap();
                int counter = 0;
                Set<String> refProperties = dbStorage.getRefProperties(currentKeyLowerCase);
                Iterator setIterator = refProperties.iterator();                
                while (setIterator.hasNext()) {                    
                    String refProperty = setIterator.next().toString();
                    Set<String[]> propsTemp = dbStorage.getProperty(refProperty);
                    for (String[] s : propsTemp) {                   
                        String propDetails1 = "";
                        String propDetails2 = "";                                             
                        if (s.length == 1) {
                           propDetails2 = "LA" + counter + ":" + this.afterValue[1];
                           propDetails1 = "prop:" + refProperty + ",class:" + s[0];
                        }
                        currentKeyword.put(propDetails2, propDetails1);
                        counter++;
                    }
                }                  
                keywordMatches.put(this.afterValue[1], currentKeyword);
                
            }            
        }
        return keywordMatches;
    }
    
    
}
