/*
 * DecimalColumnHandler.java
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
public class DecimalColumnHandler extends Column.TypeHandler implements PropertySupport.DecimalPropertyInfo
{   
    private String format;
    private double minValue;
    private double maxValue;
    private boolean usePrimitiveValue;
    
    public DecimalColumnHandler(){
        this("#,##0.00");
    } 
    
    public DecimalColumnHandler(String format) {
        this(format, -1.0, -1.0, false); 
    }
    
    public DecimalColumnHandler(String format, double minValue, double maxValue, boolean usePrimitiveValue) 
    {
        this.format = format;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.usePrimitiveValue = usePrimitiveValue;
    }    
    
    public String getType() { return "decimal"; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isUsePrimitiveValue() { return usePrimitiveValue; } 
    public void setUsePrimitiveValue(boolean usePrimitiveValue) {
        this.usePrimitiveValue = usePrimitiveValue;
    }
    
    public double getMinValue() { return minValue; }
    public void setMinValue(double minValue) { 
        this.minValue = minValue;
    }
    
    public double getMaxValue() { return maxValue; } 
    public void setMaxValue(double maxValue) { 
        this.maxValue = maxValue;
    } 
    
    public Object put(Object key, Object value) 
    {
        String skey = key+"";
        if ("format".equals(skey)) 
            setFormat((value == null? null: value.toString())); 
        else if ("usePrimitiveValue".equals(skey))
            setUsePrimitiveValue("true".equals(value+""));
        else if ("minValue".equals(skey)) 
        {
            try {
                setMinValue(Double.parseDouble(value+""));
            } catch(Exception ex) {
                setMinValue(-1.0); 
            }
        }
        else if ("maxValue".equals(skey)) 
        {
            try {
                setMaxValue(Double.parseDouble(value+""));
            } catch(Exception ex) {
                setMaxValue(-1.0); 
            }
        } 

        return super.put(key, value); 
    }    
}
