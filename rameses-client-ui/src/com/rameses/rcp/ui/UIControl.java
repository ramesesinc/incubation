package com.rameses.rcp.ui;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.*;

/**
 *
 * @author jaycverg
 */
public interface UIControl extends Comparable 
{
    static final long serialVersionUID = 1L;
    
    
    String getName();
    
    //void setValue(Object value);    
    
    /**
     * specify the names or pattern of names of the controls when updated
     * can affect the value of this control
     */
    String[] getDepends();
    int getIndex();
    
    /**
     * happens when the control is added to a UIViewPanel
     * occurs one time only per page
     */
    void setBinding(Binding binding);
    Binding getBinding();
    
    /**
     * fires when binding.refesh is invoked
     */
    void refresh();
    
    /**
     * fires after Binding's code bean has been set
     */
    void load();
    
    
    
    /*
     *  added the following methods below to be part of the standard property 
     *  of the UIControl object
     */
    Object getClientProperty(Object name); 
    void putClientProperty(Object name, Object value);
    
    void setPropertyInfo(PropertySupport.PropertyInfo info); 
}
