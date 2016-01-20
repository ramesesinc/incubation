/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SimpleField;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author dell
 */
public class SelectFields {

    private HashSet<String> simpleFields = new HashSet();
    private HashMap<String, SelectFields> complexFields = new HashMap();

    public void addFields(String fieldNames) {
        if(fieldNames == null ) fieldNames = ".*";
        for (String s : fieldNames.split(",")) {
            s = s.trim();
            if (s.indexOf(".") <= 0) {
                if( s.equals("*")) s = ".*";
                simpleFields.add(s);
            } else {
                String prefix = s.substring(0, s.indexOf("."));
                String suffix = s.substring(s.indexOf(".") + 1);
                if (!complexFields.containsKey(prefix)) {
                    complexFields.put(prefix, new SelectFields());
                }
                SelectFields sf = complexFields.get(prefix);
                sf.addFields(suffix);
            }
        }
    }

    //after asking, consume immediately so it won't be available the next time
    public boolean hasSimpleField(String name) {
        if( simpleFields.contains(".*") ) return true;
        return simpleFields.remove(name);
    }

    //same as hasSimpleField but returns a SelectFields object instead of boolean
    public SelectFields getComplexField(String name) {
        return complexFields.remove(name);
    }
    
    //check element if field exists from the remianing unconsumed fields 
    //any one match, break immediately
    public boolean checkExistField(SchemaElement elem) {
        for(SimpleField sf: elem.getSimpleFields()) {
            for(String s: simpleFields) {
                if( sf.getName().matches(s) ) return true;
            }
        }
        for(ComplexField cf: elem.getComplexFields() ) {
            for( String s: complexFields.keySet() ) {
                if( cf.getName().matches(s) ) return true;
            }
        }
        return false;
    }
    
    public void clearAll() {
        simpleFields.clear();
        complexFields.clear();
    }
    
}
