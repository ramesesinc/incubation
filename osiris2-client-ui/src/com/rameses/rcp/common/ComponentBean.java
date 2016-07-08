/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.framework.Binding;

public abstract class ComponentBean {
 
    private String bindingName; 
    private Object caller;
    private Binding binding; 
    
    public String getBindingName() {
        return bindingName; 
    }
    public void setBindingName( String name ) {
        this.bindingName = name; 
    }
    
    public Binding getBinding() { return binding; } 
    public void setBinding( Binding binding ) {
        this.binding = binding; 
    }
    
    public Object getCaller() { return caller; } 
    public void setCaller( Object caller ) {
        this.caller = caller; 
    }
    
    public Object getValue() {
        return PropertyResolver.getInstance().getProperty(caller, bindingName); 
    }
    public void setValue( Object value ) {
        PropertyResolver.getInstance().setProperty( caller, bindingName, value ); 
    } 
}
