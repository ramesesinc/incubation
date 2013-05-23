/*
 * CheckBoxColumnHandler.java
 *
 * Created on May 21, 2013, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.math.BigDecimal;

/**
 *
 * @author wflores
 */
public class CheckBoxColumnHandler extends ColumnHandler
{   
    private Class valueType = Boolean.class;
    private Object checkValue;
    private Object uncheckValue;
    
    public CheckBoxColumnHandler(){
    } 
    
    public CheckBoxColumnHandler(String format) 
    {
        setCheckValue(true);
        setUncheckValue(false);
    }
    
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
