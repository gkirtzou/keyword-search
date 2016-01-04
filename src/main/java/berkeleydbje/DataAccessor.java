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
package berkeleydbje;

import java.io.File;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

/**
 * This class is used to access the data
 * of the entity store
 * @author fil
 */

public class DataAccessor {
    
     /**
     * Class constructor. It is used to 
     * open the indices in database.
     * @param store The entity store
     * @throws DatabaseException 
     */
    public DataAccessor(EntityStore store)
        throws DatabaseException {
        // Primary key for each class
        classByName = store.getPrimaryIndex(
            String.class, RdfClass.class);
        propertyByName = store.getPrimaryIndex(
            String.class, Property.class);
        literalByName = store.getPrimaryIndex(
            String.class, Literal.class);
        classInvertedIndexByName = store.getPrimaryIndex(
            String.class, ClassInvertedIndex.class);
        propertyInvertedIndexByName = store.getPrimaryIndex(
            String.class, PropertyInvertedIndex.class);
        literalInvertedIndexByName = store.getPrimaryIndex(
            String.class, LiteralInvertedIndex.class);
        }
    
    // Inventory Accessors
    PrimaryIndex<String,RdfClass> classByName;
    PrimaryIndex<String,Property> propertyByName;
    PrimaryIndex<String,Literal> literalByName;
    PrimaryIndex<String,ClassInvertedIndex> classInvertedIndexByName;
    PrimaryIndex<String,PropertyInvertedIndex> propertyInvertedIndexByName;
    PrimaryIndex<String,LiteralInvertedIndex> literalInvertedIndexByName;
}
