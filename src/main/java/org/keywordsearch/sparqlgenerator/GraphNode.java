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

/**
 *
 * @author penny
 * @author gkirtzou
 */
public class GraphNode{
    
    /**
     * The type of the node. 
     * "C" if the node represents a class
     * "P" if the node represents an inter-entity property
     * "E" if the node represents an entity-to-attribute property
     * "L" if the node represents a literal
     */
    private String isType;
    /**
     * The node name.
     * This can be the class name, property name or literal depending on the node type.
     */
    private String nodeName;
    /**
     * The subject of the node. 
     * For example, if the node type is a property, this attribute is the subject
     * related to the property node.
     */
    private String subject;
    /**
     * The property to which a literal is related. 
     * 
     */
    private String property;
    /**
     * The object to which a property is related.
     *
     */
    private String object;
    
    /**
     * 
     */
    private String object_filter;
    
    // Information in case the node is of literal type.
    // datatype
    private String datatype;
    // language
    private String language;
    
    private String variable;
       
    public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
     * Constructor of a class node.
     * @param nodeName The class name.
     */
    public GraphNode(String nodeName){
        this.isType = "C";
        this.nodeName = nodeName;
        this.subject = null;
        this.property = null;
        this.object = null;
        this.object_filter = null;
        this.datatype = null;
        this.language = null;
        this.variable = null;
    }
    
    /**
     * Constructor of an inter-entities property node.
     * @param nodeName The property name.
     * @param subject The subject of the property. 
     * @param object In case of an inter-entity property node, this is the object class of 
     * the property node. In case of an entity-to-attribute property node, this field is empty. 
     */
    public GraphNode(String nodeName, String subject, String object){
        this.isType = "P";
        this.nodeName = nodeName;
        this.subject = subject;
        this.property = null;
        this.object = object;
        this.object_filter = null;
        this.datatype = null;
        this.language = null;
        this.variable = null;
    }
    
    
    /**
     * Constructor of a literal node
     * @param nodeName The node name.
     * @param subject The subject of the property to which the literal is related.
     * @param property The property to which the literal is related.
     * @param datatype The datatype information of the literal value
     * @param language The language information of the literal value
     */
    public GraphNode(String nodeName, String subject, String property, String datatype, String language){
        this.isType = "L";
        this.nodeName = nodeName;
        this.subject = subject;
        this.property = property;
        this.object = null; 
        this.object_filter = null;
        this.datatype = datatype;
        this.language = language;
        this.variable = null;
    }
    


    @Override
    /**
     * This function overrides equals functions and is used in order to compare Node 
     * objects in Graphs.
     */
    public boolean equals(Object obj) {
    
        boolean comparison = false;
    
        final GraphNode other = (GraphNode) obj;
        // Compare RDF class nodes
        if (this.isType != null && this.isType.equals("C") 
        	&& this.isType.equals(other.isType) &&
        	this.nodeName.equals(other.nodeName)) {
        	comparison = true;
        }
        // Compare Property nodes
        else if (this.isType != null && this.isType.equals("P") 
        		&& this.isType.equals(other.isType) 
        		&& this.nodeName != null && this.nodeName.equals(other.nodeName) 
        		&& this.subject != null && this.subject.equals(other.subject)) {
       		// Compare inter-entities properties
        	if (this.object != null && this.object.equals(other.object)) {
        		comparison = true;
        	}
        	// Compare entity-to-literal properties
        	else if (this.object == null && other.object == null) {
        		comparison = true;
        	}
        }	
        // Compare Literal nodes
        else if (this.isType != null && this.isType.equals("L") 
        		&& this.isType.equals(other.isType)
        		&& this.nodeName.equals(other.nodeName)) {
        	if (this.datatype != null && this.datatype.equals(other.datatype)) {
        		comparison = true;
        	}
        	else if (this.language != null && this.language.equals(other.language)) {
        		comparison = true;
        	}
        	else if (this.datatype == null && this.language == null) {
        		comparison = true;
        	}
        }

        return comparison;
    }

    @Override
    /**
     * This function is needed in order not to have duplicate nodes in the Graphs.
     */
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.isType != null ? this.isType.hashCode() : 0);
        hash = 53 * hash + (this.nodeName != null ? this.nodeName.hashCode() : 0);
        hash = 53 * hash + (this.subject != null ? this.subject.hashCode() : 0);
        hash = 53 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 53 * hash + (this.object != null ? this.object.hashCode() : 0);
        hash = 53 * hash + (this.object_filter != null ? this.object_filter.hashCode() : 0);
        hash = 53 * hash + (this.datatype != null ? this.datatype.hashCode() : 0);
        hash = 53 * hash + (this.language != null ? this.language.hashCode() : 0);
   //     hash = 53 * hash + (this.variable != null ? this.variable.hashCode() : 0);
        return hash;
    }
    
    /**
     * Returns isType field of a Node object.
     * @return isType field of a Node object
     */
    public String getIsType() {
        return isType;
    }

    /**
     * Sets isType field of a Node object.
     * @param isType The type of a Node object.
     */
    public void setIsType(String isType) {
        this.isType = isType;
    }

    /**
     * Returns the name of a Node object.
     * @return the name of a Node object.
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Sets the name of a Node object.
     * @param nodeName the name of a node object.
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Returns the subject of a Node object.
     * @return the subject of a node object.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of a Node object.
     * @param subject the subject of a Node object.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Returns the property of a Node object.
     * @return the property of a Node object.
     */
    public String getProperty() {
        return property;
    }

    /**
     * Sets the property of a Node object.
     * @param property the property of a Node object.
     */
    public void setProperty(String property) {
        this.property = property;
    }
    
    /**
     * Returns the object of a Node object
     * @return the object of a Node object
     */
    public String getObject() {
        return object;
    }

    /**
     * Sets the object of a Node object
     * @param object the object of a Node object
     */
    public void setObject(String object) {
        this.object = object;
    }
    
     /**
     * Returns the object filter option of a Node object
     * @return the object of a Node object
     */
    public String getObjectFilter() {
        return this.object_filter;
    }

    /**
     * Sets the object filter option of a Node object
     * @param object_filter the object filter of a Node object
     */
    public void setObjectFilter(String object_filter) {
        this.object_filter = object_filter;
    }
    
   /**
    * Returns the datatype of a Node Literal
    * @return the datatype of a Node Literal
    */
   public String getDatatype() {
       return this.datatype;
   }

   /**
    * Sets the datatype of a Node Literal
    * @param datatype the datatype of a Node Literal
    */
   public void setDatatype(String datatype) {
       this.datatype = datatype;
   }

 
   /**
    * Returns the language of a Node Literal
    * @return the language of a Node Literal
    */
   public String getLanguage() {
       return this.language;
   }

   /**
    * Sets the language of a Node Literal
    * @param language the language of a Node Literal
    */
   public void setLanguage(String language) {
       this.language = language;
   }
    
    /**
     * @override
     */
    public String toString() {
    	String str = "Type = " + this.isType +
    				 "\tNode Name = " + this.nodeName +
    				 "\tSubject = " + this.subject + 
    				 "\tPredicate = " + this.property +
    				 "\tObject = " + this.object +
    				 "\tObject Filter Option = " + this.object_filter +
    				 "\tDatatype = " + this.datatype +
    				 "\tLanguage = " + this.language +
    				 "\nVariable = " + this.variable +
    				 "\n";
    	return(str);
    	
    }
   
    /**
     * @override used for graph visualization
     */
    /*public String toString() {
    	 int i = this.nodeName.lastIndexOf("/")+1;
         int j = this.nodeName.lastIndexOf("#")+1;
         String str = this.variable + " ";
         if (i < j) {
        	 str = str + this.nodeName.substring(j);
         }
         else {
        	 str = str + this.nodeName.substring(i);	        
         }    	
         
         return str;
    }*/
}
