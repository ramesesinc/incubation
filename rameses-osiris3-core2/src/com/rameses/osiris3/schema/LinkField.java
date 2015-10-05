/*
 * LinkedField.java
 *
 * Created on August 16, 2010, 7:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

/**
 *
 * @author elmo
 */
public class LinkField extends SchemaField {
    
    private String name;
    private boolean required;
    private String ref;
    private String target;
    
    /** Creates a new instance of LinkedField */
    public LinkField() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRef() {
        return ref;
    }
    
    public void setRef(String ref) {
        this.ref = ref;
    }
    
    public String getFieldname() {
        return (String)super.getProperty("fieldname");
    }
    
    public void setFieldname(String name) {
        super.getProperties().put("fieldname", name);  
    }

    public boolean isRequired() {
        return true;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
