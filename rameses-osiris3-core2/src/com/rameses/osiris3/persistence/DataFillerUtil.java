/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaUtil;
import com.rameses.osiris3.schema.SimpleField;
import java.util.List;
import java.util.Map;

/**
 * @author dell
 * This utility fills the data prior to saving..
 */
public class DataFillerUtil {
    
    private static UIDKeyGenerator uidGenerator = new UIDKeyGenerator();
    
    private static void populateOneToManyParentIds( Map item, Map parent, List<RelationKey> relationKeys ) throws Exception {
        for(RelationKey rk: relationKeys) {
            item.put(rk.getTarget(), DataUtil.getData(parent, rk.getField()));
        }
    }
    
    private static void populateManyToOneKeys( Map item, Map parent, List<RelationKey> relationKeys ) throws Exception {
        for(RelationKey rk: relationKeys) {
            item.put(rk.getTarget(), DataUtil.getData(parent, rk.getField()));
        }
    }
    
     //this method fixes the data to correct. example, populating the objids
    public static void convertData( SimpleField sf, Map data, Object val ) throws Exception {
        if(val!=null) {
            String stype = sf.getType();
            if(stype==null) stype = "string";
            val = SchemaUtil.convertData( val, stype );
        }
        //correct also the data type
        DataUtil.putData( data, sf.getName(), val );
    }
    
    private static void setPrimaryKey( SimpleField sf, Map data  ) throws Exception {
        Object val = DataUtil.getData(data, sf.getName());
        //do not proceed id there is already a primary key
        if( val !=null ) return;    
        String keyGen = (String) sf.getProperty("keygen");
        
        //if there is a key generator specified in keygen use that instead.
        if( keyGen == null || keyGen.trim().length()==0 || keyGen.equalsIgnoreCase("default") ) {
            //use the basic keygen
            String prefix = (String) sf.getProperty("prefix");
            val = uidGenerator.getNewKey(prefix, 0);
            DataUtil.putData(data, sf.getName(), val);
        }
    }
    
    public static void fill( SchemaElement element, Map data ) throws Exception {
        fill( element, data, null, null, null );
    }
    
    public static void fill( SchemaElement element, Map data, Map parent, String includeFields, String excludeFields ) throws Exception {
        SchemaManager sm = element.getSchema().getSchemaManager();
        //populate the simple fields
        for( SimpleField sf: element.getSimpleFields() ) {
            if( sf.isPrimary() ) {
                setPrimaryKey( sf, data );
            }
            Object val = DataUtil.getData( data, sf.getName() );
            convertData(  sf, data, val );
        }   
        for( ComplexField cf: element.getComplexFields() ) {
            if( cf.getSerializer() !=null ) continue;
            if( cf.getJoinType() == null ) continue;
            if( cf.getRef()==null) {
                throw new Exception("PersistenceUtil.create error. Ref not found for complex field "+cf.getName());
            }
            String joinType = cf.getJoinType();

            //get the schema reference for the list
            SchemaElement itemElem = sm.getElement(cf.getRef());
            if(joinType.matches("one-to-one")) {
                //feed the parent's objid into the item
                Map item =(Map)DataUtil.getData( data, cf.getName() );
                if( item == null ) 
                    throw new Exception("DataFillerUtil.error. " + "one-to-one element reference for " + cf.getName()+" must not be null");
                fill( itemElem, item, data, includeFields, excludeFields );
            }
            else if(joinType.matches(JoinTypes.ONE_TO_MANY)) {
                //feed the parent's objid into the item
                List<Map> items = DataUtil.getDataList( data, cf.getName() );
                for( Map item : items ) {
                    populateOneToManyParentIds( item, data, cf.getRelationKeys() );
                    fill( itemElem,item, data, includeFields, excludeFields );
                }
            }
        }
        //fill data if there are extends
        if( element.getExtends()!=null ) {
            SchemaElement ext = element.getSchema().getSchemaManager().getElement(element.getExtends());
            fill( ext, data, null, includeFields, excludeFields );
        }
        
    }
    
}
