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
    private String statement;
    
    /** Creates a new instance of Expression */
    public ExpressionBean(String stmt) {
        this.statement = stmt;
    }
    
    public ExpressionBean() {
    }
    
    public ExpressionBean add(String name, Object value) {
        params.put(name, value);
        return this;
    }
    
    public Map getParams() {
        return params;
    }
    
    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}


