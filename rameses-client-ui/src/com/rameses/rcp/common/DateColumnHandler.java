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
    
    public String getInputFormat() { return inputFormat; }
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat; 
    }

    public String getValueFormat() { return valueFormat; }
    public void setValueFormat(String valueFormat) { 
        this.valueFormat = valueFormat;
    }
    
    public Object put(Object key, Object value) 
    {
        String skey = key+"";
        if ("inputFormat".equals(skey)) 
            setInputFormat((value == null? null: value.toString())); 
        else if ("outputFormat".equals(skey)) 
            setOutputFormat((value == null? null: value.toString())); 
        else if ("valueFormat".equals(skey)) 
            setValueFormat((value == null? null: value.toString())); 

        return super.put(key, value); 
    }     
}
