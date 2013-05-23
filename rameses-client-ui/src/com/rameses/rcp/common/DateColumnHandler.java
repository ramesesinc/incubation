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
public class DateColumnHandler extends ColumnHandler
{   
    public DateColumnHandler(){
    } 
    
    public DateColumnHandler(String inputFormat, String outputFormat, String valueFormat) 
    {
        setInputFormat(inputFormat);
        setOutputFormat(outputFormat);
        setValueFormat(valueFormat); 
    }
    
    public String getInputFormat() { 
        return (String) get("inputFormat"); 
    }
    public void setInputFormat(String inputFormat) {
        put("inputFormat", inputFormat); 
    }

    public String getOutputFormat() { 
        return (String) get("outputFormat"); 
    }
    public void setOutputFormat(String outputFormat) {
        put("outputFormat", outputFormat); 
    }

    public String getValueFormat() { 
        return (String) get("valueFormat"); 
    }
    public void setValueFormat(String valueFormat) { 
        put("valueFormat", valueFormat); 
    }
    
}
