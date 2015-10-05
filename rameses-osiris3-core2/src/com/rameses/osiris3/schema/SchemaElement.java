/*
 * Schema.java
 *
 * Created on August 12, 2010, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SchemaElement implements Serializable {
    
    private Schema schema;
    private String name;
    private List<SchemaField> fields = new ArrayList();
    private Map<String,SchemaField> fieldMap = new Hashtable();
    private Map properties = new HashMap();
    
    private List<SimpleField> simpleFields;
    private List<ComplexField> complexFields;
    private List<LinkField> linkFields;
    
    /** Creates a new instance of Schema */
    public SchemaElement(Schema schema) {
        this.schema = schema;
    }
    
    /**
     * method accessed by the parser.
     */
    public void addSchemaField(SchemaField fld) {
        fields.add( fld );
        if( fld.getName()!=null ) {
            fieldMap.put(fld.getName(), fld);
        }  
        fld.setElement(this);
    }
    
    public Map getProperties() {
        return properties;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Schema getSchema() {
        return schema;
    }
    
    public List<SchemaField> getFields() {
        return fields;
    }
    
    
    public SchemaField getField(String name) {
        return fieldMap.get(name);
    }
    
    public List<SimpleField> getSimpleFields() {
        if(simpleFields==null) {
            simpleFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof SimpleField) simpleFields.add((SimpleField) sf);
            }
        }
        return simpleFields;
    }
    
    public List<ComplexField> getComplexFields() {
        if(complexFields==null) {
            complexFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof ComplexField) complexFields.add((ComplexField) sf);
            }
        }
        return complexFields;
    }
    
    public List<LinkField> getLinkFields() {
        if(linkFields==null) {
            linkFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof LinkField) linkFields.add((LinkField) sf);
            }
        }
        return linkFields;
    }
    
    public Object getProperty(String name) {
        return this.properties.get( name );
    }
    
    public String getExtends() {
        String ext = (String) this.properties.get("extends");
        if( ext == null ) return null;
        if( ext.indexOf(":")<=0) return ext + ":" + ext;
        return ext;
    }
    
    public String getTablename() {
        return (String) this.properties.get("tablename");
    }
    
    public Map toMap() {
        Map mh = new HashMap();
        mh.putAll( properties );
        mh.put("name", this.getName());
        if( this.getTablename()!=null) {
            mh.put("tablename", this.getTablename());
        }
        List fields = new ArrayList();
        for( SchemaField sf: this.getFields()) {
            fields.add( sf.toMap() );
        }
        return mh;
    }
    
}
