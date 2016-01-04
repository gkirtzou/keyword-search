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
    private String isType = "";
    /**
     * The node name.
     * This can be the class name, property name or literal depending on the node type.
     */
    private String nodeName = "";
    /**
     * The subject of the node. 
     * For example, if the node type is a property, this attribute is the subject
     * related to the property node.
     */
    private String subject = "";
    /**
     * The property to which a literal is related. 
     * 
     */
    private String property = "";
    /**
     * The object to which a property is related.
     *
     */
    private String object = "";
    
    /**
     * 
     */
    private String object_filter = "";
    /**
     * Constructor of a general node. A general node is a node with unknown type
     * at the time of creation.
     * @param The name of the node 
     */
    public GraphNode(String nodeName){
        this.nodeName = nodeName;
    }
    
    /**
     * Constructor of a class node.
     * @param isType The type of the node. In this case isType="C".
     * @param nodeName The class name.
     */
    public GraphNode(String isType, String nodeName){
        this.isType = isType;
        this.nodeName = nodeName;
    }
    
    /**
     * Constructor of a property or literal node
     * @param isType The node type.
     * @param nodeName The node name.
     * @param subject The subject of the node. Either the subject of the property (in case 
     * of property node) or the subject of the property to which the literal is related (in 
     * case of literal node).     
     * @param property In case of a property node, this is the same as the nodeName. In case 
     * of a literal node, this is the property to which the literal is related.
     * @param object In case of an inter-entity property node, this is the object class of 
     * the entity node. In case of an entity-to-attribute property node, this field is empty. 
     * In case of a literal node, this is the same as the nodeName.
     */
    public GraphNode(String isType, String nodeName, String subject, String property, String object, String object_filter){
        this.isType = isType;
        this.nodeName = nodeName;
        this.subject = subject;
        this.property = property;
        this.object = object;
        this.object_filter = object_filter;               
    }
    


    @Override
    /**
     * This function overrides equals functions and is used in order to compare Node 
     * objects in Graphs.
     */
    public boolean equals(Object obj) {
    
        boolean comparison = false;
    
        final GraphNode other = (GraphNode) obj;
        if (this.isType.equals(other.isType) && this.nodeName.equals(other.nodeName) && this.subject.equals(other.subject) 
                && this.property.equals(other.property) && this.object.equals(other.object)&&
                this.object_filter.equals(other.object_filter)) {
            comparison = true;
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
     * @override
     */
    public String toString() {
        System.out.print("Type = " + this.isType);
        System.out.print("\tNode Name = " + this.nodeName);
        System.out.print("\tSubject = " + this.subject);
        System.out.print("\tPredicate = " + this.property);
        System.out.print("\tObject = " + this.object);
        System.out.println("\tObject Filter Option = " + this.object_filter);
        return "";
    }
}
