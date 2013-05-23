/*
 * LookupColumnHandler.java
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
public class LookupColumnHandler extends ColumnHandler
{   
    public LookupColumnHandler(){
    } 
    
    public LookupColumnHandler(String expression, Object handler) 
    {
        setExpression(expression);
        setHandler(handler);
    }
    
    public String getExpression() { 
        return (String) get("expression"); 
    }
    public void setExpression(String expression) {
        put("expression", expression); 
    }

    public Object getHandler() { 
        return get("handler"); 
    }
    public void setHandler(Object handler) {
        put("handler", handler); 
    }
}
