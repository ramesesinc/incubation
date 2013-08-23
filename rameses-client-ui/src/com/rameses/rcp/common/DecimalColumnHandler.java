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
    private static final long serialVersionUID = 1L;    
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
    
    public String getFormat() 
    {
        Object value = super.get("format");
        if (value == null) value = this.format;
        
        return (value == null? null: value.toString());
    }
    
    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isUsePrimitiveValue() 
    {
        Object value = super.get("usePrimitiveValue");
        if (value == null) value = this.usePrimitiveValue;
        
        Boolean bool = convertBoolean(value); 
        return (bool == null? false: bool.booleanValue());
    }
    
    public void setUsePrimitiveValue(boolean usePrimitiveValue) {
        this.usePrimitiveValue = usePrimitiveValue;
    }
    
    public double getMinValue() 
    {
        Object value = super.get("minValue");
        if (value == null) value = this.minValue;
        
        Number num = convertDouble(value);
        return (num == null? -1.0: num.doubleValue()); 
    }
    
    public void setMinValue(double minValue) { 
        this.minValue = minValue;
    }
    
    public double getMaxValue() 
    {
        Object value = super.get("maxValue");
        if (value == null) value = this.maxValue;
        
        Number num = convertDouble(value);
        return (num == null? -1.0: num.doubleValue()); 
    }
    
    public void setMaxValue(double maxValue) { 
        this.maxValue = maxValue;
    } 
    
    protected Number convertDouble(Object value) 
    {
        if (value instanceof Number)
            return (Number) value;

        try {
            return Double.parseDouble(value.toString()); 
        } catch(Exception ex) {
            return null; 
        }
    }

    private Boolean convertBoolean(Object value) 
    {
        if (value instanceof Boolean)
            return (Boolean) value;

        try {
            return Boolean.parseBoolean(value.toString()); 
        } catch(Exception ex) {
            return null; 
        }
    }
}
