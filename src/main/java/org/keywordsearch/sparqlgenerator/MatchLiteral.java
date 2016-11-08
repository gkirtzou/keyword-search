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
* A match of a given keyword to a Literal entry of the Term index expanded for use to combination. 
* @author gkirtzou
*/
public class MatchLiteral extends KeywordMatch {
	
	//<language, datatype, property, class of subject>
	private String language;
	private String datatype;
	private String property;
	private String subjClass;
	
	// Constructors 
	public MatchLiteral() {
		super();
		this.language = null;
		this.datatype = null;
		this.property = null;
		this.subjClass = null;
	}

	public MatchLiteral(String referenceMatch, String language, String datatype, String property, String subjClass) {
		super(referenceMatch);
		this.language = language;
		this.datatype = datatype;
		this.property = property;
		this.subjClass = subjClass;		
	}
	
	// Getters and Setters
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSubClass() {
		return this.subjClass;
	}

	public void setSubClass(String subjClass) {
		this.subjClass = subjClass;
	}

	@Override
    public String toString() {
		String str = "[Match reference :: " + this.getReferenceMatch() 
					+ "\nLang::" + this.language + "\tDatatype::" + this.datatype 
					+ "\tProperty::" + this.property + "\tSubjClass::" + this.subjClass +"]\n";    
		return(str);
	}
	
	
	
}
