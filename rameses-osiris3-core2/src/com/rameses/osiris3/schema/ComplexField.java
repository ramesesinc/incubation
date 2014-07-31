/*
 * ComplexField.java
 *
 * Created on August 12, 2010, 10:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elmo
 */
public class ComplexField extends SchemaField implements IRelationalField {
    
    private String name;
    private boolean required = true;
    private String type;
    private String ref;
    private int min;
    private int max;
    private String serializer;
    private List<RelationKey> relationKeys = new ArrayList();
    
    public String getType() {
        return type;
    }
    
    public void setType(String t) {
        this.type = t;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public int getMin() {
        return min;
    }
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public String getRef() {
        return ref;
    }
    
    public void setRef(String ref) {
        this.ref = ref;
    }
    
    public String getSerializer() {
        return serializer;
    }
    
    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }

    public void addKey(RelationKey rk) {
        relationKeys.add( rk );
    }

    public String getRelation() {
        return (String)super.getProperty("relation");
    }
    
    public String getTarget() {
        return (String)super.getProperty("target");
    }
    
    public String getJoinType() {
        return (String)super.getProperty("jointype");
    }
}
