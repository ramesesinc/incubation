/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.util.ObjectSerializer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author dell
 * This utility will handle map transformations to prepare it for saving.
 */
public class DataTransposer {
    
    
    /**
     * This results a new map that contains the flattened data. This is for easier 
     * data entry in the insert. It also converts the necessary fields to its proper 
     * data types readay for persisting.
     * @param svw
     * @param sourceData
     * @param targetData 
     */
    public static Map prepareDataForInsert(SchemaView svw, Map sourceData ) throws Exception {
        Map targetData = new LinkedHashMap();
        for( SchemaViewField vf: svw.findAllFields(".*")) {
            if(! (vf instanceof SchemaViewRelationField )) {
                if(!vf.isInsertable()) continue;
                Object val = DataUtil.getNestedValue(sourceData, vf.getExtendedName() );
                
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
                Object val = DataUtil.getNestedValue(sourceData, svr.getExtendedName());
                if( val == null ) {
                    //this is usually for many to one.
                    val = DataUtil.getNestedValue(sourceData, svr.getTargetFieldExtendedName()  );
                    targetData.put( svr.getFieldname(), val);
                }
                else {
                    targetData.put( svr.getExtendedName(), val);
                }
            }
        }
        return targetData;
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
    
    
}
