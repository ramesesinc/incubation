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
public class IntegerColumnHandler extends Column.TypeHandler implements PropertySupport.IntegerPropertyInfo
{   
    private String format;
    private int minValue;
    private int maxValue;
    
    public IntegerColumnHandler(){
        this(null);
    } 
    
    public IntegerColumnHandler(String format) {
        this(format, -1, -1); 
    }
    
    public IntegerColumnHandler(String format, int minValue, int maxValue) {
        this.format = format;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public String getType() { return "integer"; }
        
    public String getFormat() { return format; }
    public void setFormat(String format) {
        this.format = format;
    }

    public int getMinValue() { return minValue; }
    public void setMinValue(int minValue) { 
        this.minValue = minValue;
    }
    
    public int getMaxValue() { return maxValue; } 
    public void setMaxValue(int maxValue) { 
        this.maxValue = maxValue;
    } 
}
