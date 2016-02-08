/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaField;
import com.rameses.osiris3.schema.SchemaRelation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public final class EntityValidator {
    
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
    
    /**
     * Verify applies to field verification, embedded objects. This is a nested operation
     * The element to verify
     * @param elem - the current element to verify
     * @param rawData - the data originally passed. unadulterated
     * @param prefix - prefix node
     * @param errs - holder of error messages
     * @throws Exception 
     */
    public static void verify(SchemaElement elem, Map rawData, String prefix, List<String> errs ) throws Exception {
        String pfx = (prefix==null? "" : prefix+".");
        
        //verfiy first extende d elements
        if( elem.getExtendedElement() !=null ) {
            verify( elem.getExtendedElement(), rawData, prefix,errs );
        }
        List<SchemaField> flds = new ArrayList();
        flds.addAll( elem.getSimpleFields() );
        flds.addAll( elem.getSerializedFields() );
        for( SchemaField sf: flds ) {
            try {
                Object val = getNestedValue( sf.getName(), rawData );
                sf.verify(val);
            }
            catch(Exception e) {
                errs.add( pfx + sf.getCaption() + e.getMessage()  );
            }
        }
        
        //check for embedded objects 
        for( SchemaRelation sr: elem.getOneToOneRelationships() ) {
            Object val = getNestedValue( sr.getName(), rawData );
            if (val == null) {
                if( sr.isRequired() ) {
                    errs.add(  pfx +sr.getName() + " is required" );
                }
            }
            else{
                if( !(val instanceof Map )) {
                    errs.add(  pfx + sr.getName() + " must be a Map object" );
                }
                else {
                    //drill down to the next element
                    verify(sr.getLinkedElement(), (Map)val, sr.getName(), errs);
                }
            }
        }
         //check all coomplex types many to one. The linked value should be a map
        for (SchemaRelation sr : elem.getManyToOneRelationships()) {
            if( !sr.isRequired() ) continue;
            Object val = getNestedValue(sr.getName(),rawData);
            if (val == null ) {
                errs.add(  pfx + sr.getName() + " is required" );
            }
        }
        //check one to many, if required and data type is matched
        for (SchemaRelation sr : elem.getOneToManyRelationships()) {
            Object val = getNestedValue(sr.getName(), rawData);
            if( val == null ) {
                if( sr.isRequired() ) {
                     errs.add(  pfx +" "+ sr.getName() + "is required" );
                }
            }
            else {
                if (!(val instanceof List)) {
                    errs.add(pfx + " " + sr.getName()  + " must be a List");
                }
                else {
                    List dataList = (List)val;
                    SchemaElement elm = sr.getLinkedElement();
                    int i = 0;
                    for(Object m: dataList ) {
                        String np = prefix + "." + sr.getName() + "[" + (i++) + "]";
                        if(! (m instanceof Map)) {
                            errs.add( np + " must be instanceof Map "  );
                        }
                        else {
                            verify(sr.getLinkedElement(), (Map)m, np, errs);
                        }
                    }
                }
            }
        }
    }  
    
    public static void validate( Map mdata, SchemaElement elem ) throws Exception {
        List<String> errs = new ArrayList();
        verify(elem, mdata, null, errs);
        if( errs.size() > 0 ) {
            StringBuilder sb = new StringBuilder();
            for (String e : errs) {
                sb.append(e + "\n");
            }
            throw new Exception(sb.toString());
        }      
    }
    
}
