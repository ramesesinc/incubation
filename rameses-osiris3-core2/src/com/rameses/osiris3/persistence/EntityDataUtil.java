/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaRelation;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.util.ObjectSerializer;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This verifies the data types and required fields. the data passed here
 * assumes it is already falttened.
 * @param vw - the schema view
 * @param data the flattened out map consists of names with undersocres
 * @throws Exception 
 */
public class EntityDataUtil {

    public static Object getNestedValue( String name, Map data ) {
        if( name.indexOf("_") <= 0 ) {
            return data.get(name);
        }
        else {
            String arr[] = name.split("_");
            Map d = data;
            for(int i =0; i<arr.length-1; i++) {
                d = (Map)d.get(arr[i]);
                if( d == null || (!(d instanceof Map )) ) {
                    return null;
                }
            }
            return d.get(arr[arr.length-1]);
        }
     }
    
    
    public final static Map flatten( Map data, String ch) {
        Map newHashMap = new HashMap();
        scanFlatten( data, null, newHashMap, ch  );
        return newHashMap;
    }
    
    private static void scanFlatten( Map data, String prefix, Map result, String ch ) {
        if(ch==null) ch = ".";
        for( Object o: data.entrySet() ) {
            Map.Entry me = (Map.Entry)o;
            Object val = me.getValue();
            String keyName = ((prefix==null) ? "" : prefix+ch) + me.getKey();
            if( !(val instanceof Map )) {
                result.put(keyName, val);
            }
            else {
                scanFlatten( (Map)val, keyName, result, ch );
            }
        }
    }
    
    public static String stringify(Map map) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for( Object s: map.keySet() ) {
            if(i++>0) sb.append("|");
            sb.append(s.toString());
        }
        return sb.toString();
    }
    
    /**
     * Fills the raw data with default values like id's and dates
     * and other default values in sequence
     * base fields, serialized fields, extende d fields, one to one, one to many
     * 
     * @param svw
     * @param data 
     */
    public static void fillInitialData( SchemaElement elem, Map rawData ) {
        for( SimpleField sf: elem.getSimpleFields() ) {
            //insert primary key ids.
            Object val = rawData.get(sf.getName());
            if( val == null ) {
                if( sf.isPrimary() ) {
                    String prefix = (String) sf.getProperty("prefix");
                    String id = ((prefix==null)?"":prefix)+new UID();
                    rawData.put( sf.getName(), id );
                }
                else if( sf.getDefaultValue()!=null ) {
                    rawData.put( sf.getName(), sf.getDefaultValue() );
                }
            }
        }
        //fill in the extende d items
        if( elem.getExtendedElement()!=null ) {
            fillInitialData( elem.getExtendedElement(), rawData );
        }
        
        //for one to one, specify the objid 
        for(SchemaRelation sr: elem.getOneToOneRelationships()) {
            Map m = (Map)rawData.get(sr.getName());
            if(m!=null) {
                //check also many to one on the other side. From the many 
                //to one relationships, check the one that matches the 
                //ref. 
                /*
                 * On hold ---- until I can figure out what to do with this.
                for( SchemaRelation mo: tgt.getInverseRelationships() ) {
                    if( mo.getRef().equals(elem.getName() ) ) {
                        for(RelationKey rk: mo.getRelationKeys()  ) {
                            m.put( rk.getField(), rawData.get(rk.getTarget()) );
                        }
                    }
                }
                */ 
                SchemaElement tgt = sr.getLinkedElement();                
                fillInitialData( tgt, m );
            }
        }
        for(SchemaRelation sr: elem.getOneToManyRelationships()) {
            List list = (List)rawData.get(sr.getName());
            if(list!=null) {
                for(  Object o : list) {
                    if(o instanceof Map) {
                        Map e = (Map)o;
                        
                        //ordinary load items. inverse relationships are not specified
                        for( RelationKey rk: sr.getRelationKeys() ) {
                            e.put( rk.getTarget(), getNestedValue( rk.getField(), rawData )  );
                        }
                        fillInitialData( sr.getLinkedElement(), e );
                    }
                }
            }
        }
    }
    
    
    /**
     * This results a new map that contains the flattened data. This is for easier 
     * data entry in the insert. It also converts the necessary fields to its proper 
     * data types readay for persisting.
     * @param svw
     * @param sourceData
     * @param targetData 
     */
    public static Map prepareDataForInsert(SchemaView svw, Map sourceData ) {
        Map targetData = new LinkedHashMap();
        for( SchemaViewField vf: svw.findAllFields(".*")) {
            if(! (vf instanceof SchemaViewRelationField )) {
                if(!vf.isInsertable()) continue;
                Object val = getNestedValue(vf.getExtendedName(), sourceData );
                
                if( vf.isSerialized() && val !=null) {
                    //get the default serializer
                    String ser = (String)vf.getProperty("serializer");
                    if(ser==null) ser = "default";
                    val = ObjectSerializer.getInstance().toString(val);
                }
                targetData.put( vf.getExtendedName(), val);
            }
            else  {
                //do not replace if there is already a value entered. 
                //This is to protect one to many relationships being cascaded
                //do not include one to many
                SchemaViewRelationField svr = (SchemaViewRelationField)vf;
                Object val = getNestedValue(svr.getExtendedName(), sourceData);
                if( val == null ) {
                    //this is usually for many to one.
                    val = getNestedValue(svr.getTargetFieldExtendedName(),  sourceData  );
                    targetData.put( svr.getFieldname(), val);
                }
                else {
                    targetData.put( svr.getExtendedName(), val);
                }
            }
        }
        return targetData;
    }
    
    public static void printMap( Map baseData ) {
        for( Object o: baseData.entrySet()) {
            Map.Entry m= (Map.Entry)o;
            System.out.println(m.getKey()+"="+m.getValue());
        }    
    }
    
    public static Map buildFinderFromPrimaryKeys( SchemaElement elem, Map data ){
        if(data==null) return null;
        Map mapKey = new HashMap();
        for(SimpleField sf: elem.getPrimaryKeys()) {
            Object val = data.get(sf.getName());
            if( val!=null) {
                mapKey.put( sf.getName(), val );
            }
        }
        if( mapKey.size() == 0 )
            return null;
        return mapKey;
    }
    
    //This will create a new map of flattened, not nested data. This will also
    //only update fields that exist in the data source
    public static Map prepareDataForUpdate(SchemaView svw, Map rawData ) {
        
        Map sourceData = flatten(rawData, "_");
        
        Map targetData = new LinkedHashMap();
        for( SchemaViewField vf: svw.findAllFields(".*")) {
            if(! (vf instanceof SchemaViewRelationField )) {
                if(!vf.isUpdatable()) continue;
                if( sourceData.containsKey(vf.getExtendedName()) ) {
                    Object val = sourceData.get(vf.getExtendedName());  
                    if( val!=null && vf.isSerialized()) {
                        //get the default serializer
                        String ser = (String)vf.getProperty("serializer");
                        if(ser==null) ser = "default";
                        val = ObjectSerializer.getInstance().toString(val);
                    }
                    targetData.put( vf.getExtendedName(), val);
                }
            }
            else  {
                SchemaViewRelationField svr = (SchemaViewRelationField)vf;
                if( svr.getTargetJoinType().equals( JoinTypes.MANY_TO_ONE) ) {
                    String tgt = svr.getTargetFieldExtendedName();
                    String src = svr.getFieldname();
                    if( sourceData.containsKey(tgt )) {
                        Object val =sourceData.get(tgt);
                        targetData.put( src, val );        
                    }
                    
                }
            }
        }
        return targetData;
    }
    
    
    
}
