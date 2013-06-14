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
public class LookupColumnHandler extends Column.TypeHandler implements PropertySupport.LookupPropertyInfo
{   
    private Object handler;
    private String expression;
    
    public LookupColumnHandler(){
    } 
    
    public LookupColumnHandler(String expression, Object handler) 
    {
        this.expression = expression;
        this.handler = handler;
    }
    
    public String getType() { return "lookup"; }
    
    public String getExpression() { return expression; }
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object getHandler() { return handler; }
    public void setHandler(Object handler) {
        this.handler = handler;
    }
    
    public Object put(Object key, Object value) 
    {
        String skey = key+"";
        if ("expression".equals(skey)) 
            setExpression((value == null? null: value.toString())); 
        else if ("handler".equals(skey)) 
            setHandler(value); 

        return super.put(key, value); 
    }    
}
