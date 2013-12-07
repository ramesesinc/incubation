/*
 * GroovyExpressionResolver.java
 *
 * Created on May 21, 2013, 1:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.custom.impl;

import com.rameses.common.ExpressionResolver;
import groovy.lang.MissingPropertyException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Elmo
 */
public class GroovyExpressionResolver extends ExpressionResolver {
    
    private static Map<String,ScriptTemplate> expressions = new Hashtable();
    private Pattern pattern = Pattern.compile( "(\\$|#)?\\{.*?\\}" );
    
    private Object evalObject(String expr, ExprBean bean) throws MissingPropertyException {
        try {
            //expr = expr.replaceAll("(\\$|#)\\{","").replaceAll("\\}","");
            expr = replaceFunctions(expr);
            ScriptTemplate t = null;
            
            if (!expressions.containsKey(expr)) {
                t = new ScriptTemplate(expr);
                expressions.put(expr, t );
            } else {
                t = expressions.get(expr);
            }
            return t.execute( bean );
        } catch(MissingPropertyException me) {
            throw me;
        } catch(Throwable e) {
            if (e instanceof InvocationTargetException ||
                    e instanceof NullPointerException)
                return null;
            throw new RuntimeException("error in expression " + expr + ":" + e.getMessage(), e);
        }
    }
    
    
    
    public String evalString(String expr, Object data) 
    {
        ExprBean bean = new ExprBean(data);
        Matcher matcher = pattern.matcher(expr);
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while(matcher.find()) {
            int end = matcher.start();
            sb.append(expr.substring(start, end) );
            start = expr.indexOf("}", end)+1;
            String _expr = expr.substring(end+2, start-1);
            String result  = null;
            try 
            {
                Object oResult = evalObject(_expr, bean);
                result = (oResult == null? "": oResult.toString()); 
            } catch(Exception e) {;}
            
            sb.append( result );
        }
        if(expr.length()>start) {
            sb.append( expr.substring(start) );
        }
        return sb.toString();
    }
    
    public Object eval(String expr,Object data) {
        try {
            ExprBean bean = new ExprBean(data);
            return evalObject(expr,bean);
        } catch(Exception e) {
            throw new RuntimeException( "error in expression " + expr + " " + e.getMessage(), e);
        }
    }
    
    public boolean evalBoolean(String expr, Object o) {
        try {
            ExprBean bean = new ExprBean(o);
            Object result = evalObject(expr,bean);
            if( result instanceof Boolean)
                return ((Boolean) result).booleanValue(); 
            
            if (!(result+"").matches("true|false"))
                throw new Exception("Expression must be a condition");
            
            return Boolean.parseBoolean(result.toString());
        } 
        catch(Exception e) {
            throw new RuntimeException( "error in expression " + expr + " " + e.getMessage(), e);
        }
    }
    
    public double evalDouble(String expr, Object o) 
    {
        try 
        {
            ExprBean bean = new ExprBean(o);
             Object result = evalObject(expr, bean);
            if (result instanceof Number)
                return ((Number) result).doubleValue(); 
            
            return new BigDecimal(result+"").doubleValue();
        } 
        catch(Exception e) {
            throw new RuntimeException( "error in expression " + expr + " " + e.getMessage(), e);
        }
        
    }
    
    public int evalInt(String expr, Object o) {
        try 
        {
            ExprBean bean = new ExprBean(o);
            Object result = evalObject(expr,bean);
            if (result instanceof Number) 
                return ((Number) result).intValue(); 
            
            return new BigDecimal(result+"").intValue();
        } 
        catch(Exception e) {
            throw new RuntimeException( "error in expression " + expr + " " + e.getMessage(), e);
        }
    }
    
    public BigDecimal evalDecimal(String expr, Object o) {
        try {
            ExprBean bean = new ExprBean(o);
            
            Object result = evalObject(expr,bean);
            if (result == null) 
                return null; 
            else if (result instanceof BigDecimal)
                return (BigDecimal) result; 
            else 
                return new BigDecimal(result+"");
        } 
        catch(Exception e) {
            throw new RuntimeException( "error in expression " + expr + " " + e.getMessage(), e);
        }
    }
    
}
