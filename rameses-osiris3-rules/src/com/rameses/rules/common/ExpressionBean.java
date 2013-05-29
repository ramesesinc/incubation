/*
 * Expression.java
 *
 * Created on May 29, 2013, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ExpressionBean {
    
    private Map params = new HashMap();
    private String expression;
    
    /** Creates a new instance of Expression */
    public ExpressionBean(String expression) {
        this.expression = expression;
    }
    
    public ExpressionBean add(String name, Object value) {
        params.put(name, value);
        return this;
    }
    
    public Map getParams() {
        return params;
    }
    
    public String getExpression() {
        return expression;
    }
}


