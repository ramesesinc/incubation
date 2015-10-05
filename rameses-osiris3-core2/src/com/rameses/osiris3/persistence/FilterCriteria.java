/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dell
 */
public class FilterCriteria {
    
    private Set<String> fields = new HashSet();
    private String expr;
    private Map data = new HashMap();
    
    public void addField(String field) {
        this.fields.add( field );
    }
    
    public Set<String> getFields() {
        return this.fields;
    }
    
    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }
    
    public String toString() {
        return this.expr;
    }

    public SelectFields buildFields() {
        SelectFields sf  = new SelectFields();
        //loop thru the finders
        for( String s: fields ) {
            sf.addFields(s);
        }
        return sf;
    }
    
    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
    
}
