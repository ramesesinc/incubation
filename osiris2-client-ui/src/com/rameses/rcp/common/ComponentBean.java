/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.framework.Binding;

public abstract class ComponentBean {
 
    private String bindingName; 
    private Binding innerBinding;
    private Binding callerBinding;
    
    public String getBindingName() {
        return bindingName; 
    }
    public void setBindingName( String name ) {
        this.bindingName = name; 
    }
    
    public Binding getBinding() { 
        return innerBinding; 
    } 
    public void setBinding( Binding innerBinding ) {
        this.innerBinding = innerBinding; 
    }
    
    public Binding getCallerBinding() {
        return callerBinding; 
    }
    public void setCallerBinding( Binding callerBinding ) { 
        this.callerBinding = callerBinding; 
    } 
    
    public Object getCaller() { 
        Binding bi = getCallerBinding(); 
        return ( bi == null? null : bi.getBean()); 
    } 
    
    public Object getValue() {
        return getValue( bindingName ); 
    }
    public Object getValue( String name ) {
        return PropertyResolver.getInstance().getProperty( getCaller(), name ); 
    } 
    public void setValue( Object value ) { 
        setValue( bindingName, value ); 
    } 
    public void setValue( String name, Object value ) { 
        Binding bi = getCallerBinding(); 
        if ( bi == null ) return; 
        
        PropertyResolver.getInstance().setProperty( getCaller(), name, value ); 
        bi.notifyDepends( name ); 
    }
    
    public Object getProperty( String name ) {
        return PropertyResolver.getInstance().getProperty( this, name ); 
    }
    public void setProperty( String name, Object value ) {
        PropertyResolver.getInstance().setProperty( this, name, value ); 
    }
}
