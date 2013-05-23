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
public class DecimalColumnHandler extends ColumnHandler
{   
    private String format;
    private boolean usePrimitiveValue;
    private double minValue;
    private double maxValue;
            
    public DecimalColumnHandler(){
    } 
    
    public DecimalColumnHandler(String format) {
        this.format = format;
    }
    
    public String getFormat() { return format; }
    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isUsePrimitiveValue() { return usePrimitiveValue; }
    public void setUsePrimitiveValue(boolean usePrimitiveValue) {
        this.usePrimitiveValue = usePrimitiveValue; 
    }
    
    public double getMinValue() { return minValue; }
    public void setMinValue(double minValue) { this.minValue = minValue; }
    
    public double getMaxValue() { return maxValue; }    
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }    
}
