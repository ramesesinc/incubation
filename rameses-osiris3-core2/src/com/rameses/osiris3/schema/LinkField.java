/*
 * LinkedField.java
 *
 * Created on August 16, 2010, 7:42 AM
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
public class LinkField extends SchemaField implements IRelationalField {
    
    private String name;
    private boolean required;
    private String ref;
    private String exclude;
    private boolean prefixed = true;
    private List<RelationKey> relationKeys = new ArrayList();
    
    /** Creates a new instance of LinkedField */
    public LinkField() {
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
    
    public String getRef() {
        return ref;
    }
    
    public void setRef(String ref) {
        this.ref = ref;
    }
    
    public String getExclude() {
        return exclude;
    }
    
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
    
    public boolean isPrefixed() {
        if(name==null || name.trim().length()==0)
            return false;
        else
            return true;
    }
    
    public boolean isExcludeField( SchemaField sf ) {
        boolean exclude = false;
        String excludeNames = getExclude();
        if(excludeNames!=null && sf.getName().matches(excludeNames)) exclude = true;
        return exclude;
    }
    
    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }
    
    
}
