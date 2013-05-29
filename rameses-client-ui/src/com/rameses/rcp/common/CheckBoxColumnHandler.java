/*
 * CheckBoxColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class CheckBoxColumnHandler extends Column.TypeHandler implements PropertySupport.CheckBoxPropertyInfo 
{
    private Class valueType;
    private Object checkValue;
    private Object uncheckValue;
    
    public CheckBoxColumnHandler(){
        this(Boolean.class, true, false);
    } 
    
    public CheckBoxColumnHandler(Class valueType, Object checkValue, Object uncheckValue) 
    {
        this.valueType = valueType;
        this.checkValue = checkValue;
        this.uncheckValue = uncheckValue;
    }

    public String getType() { return "checkbox"; }
    
    public Class getValueType() { return valueType; }    
    public void setValueType(Class valueType) { 
        this.valueType = valueType; 
    }
    
    public Object getCheckValue() { return checkValue; }
    public void setCheckValue(Object checkValue) {
        this.checkValue = checkValue;
    }

    public Object getUncheckValue() { return uncheckValue; }
    public void setUncheckValue(Object uncheckValue) {
        this.uncheckValue = uncheckValue; 
    }
}
