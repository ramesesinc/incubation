/*
 * DateColumnHandler.java
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
public class DateColumnHandler extends Column.TypeHandler implements PropertySupport.DatePropertyInfo 
{   
    private static final long serialVersionUID = 1L;
    private String inputFormat;
    private String outputFormat;
    private String valueFormat;
    
    public DateColumnHandler(){
    } 
    
    public DateColumnHandler(String inputFormat, String outputFormat, String valueFormat) 
    {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.valueFormat = valueFormat;
    }
    
    public String getType() { return "date"; }
    
    public String getInputFormat() 
    {
        Object value = super.get("inputFormat");
        if (value == null) value = this.inputFormat;
        
        return (value == null? null: value.toString());
    }
    
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() 
    {
        Object value = super.get("outputFormat");
        if (value == null) value = this.outputFormat;
        
        return (value == null? null: value.toString()); 
    }
    
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat; 
    }

    public String getValueFormat() 
    {
        Object value = super.get("valueFormat");
        if (value == null) value = this.valueFormat;
        
        return (value == null? null: value.toString()); 
    }
    
    public void setValueFormat(String valueFormat) { 
        this.valueFormat = valueFormat;
    }
}
