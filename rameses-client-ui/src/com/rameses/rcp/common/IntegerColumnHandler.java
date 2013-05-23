/*
 * IntegerColumnHandler.java
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
public class IntegerColumnHandler extends ColumnHandler
{   
    public IntegerColumnHandler(){
    } 
    
    public IntegerColumnHandler(String format) 
    {
        setFormat(format);
    }
    
    public String getFormat() { 
        return (String) get("format"); 
    }
    public void setFormat(String format) {
        put("format", format); 
    }

    public boolean isUsePrimitiveValue() { 
        return "true".equals(get("usePrimitiveValue")+""); 
    }
    public void setUsePrimitiveValue(boolean usePrimitiveValue) {
        put("usePrimitiveValue", usePrimitiveValue); 
    }
    
    public int getMinValue() {
        try 
        {
            Object value = get("minValue");
            if (value instanceof Integer)
                return ((Integer) value).intValue(); 
            else 
                return Integer.parseInt(get("minValue")+"");
        } 
        catch(Exception ex) {
            return 0;
        }
    }
    public void setMinValue(int minValue) {
        put("minValue", minValue);
    }
    
    public int getMaxValue() {
        try 
        {
            Object value = get("maxValue");
            if (value instanceof Integer)
                return ((Integer) value).intValue(); 
            else 
                return Integer.parseInt(get("maxValue")+"");
        } 
        catch(Exception ex) {
            return 0;
        }
    }    
    public void setMaxValue(int maxValue) {
        put("maxValue", maxValue);
    }    
}
