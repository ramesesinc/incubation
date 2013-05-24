/*
 * ScriptTemplate.java
 *
 * Created on May 23, 2013, 3:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.custom.impl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 *
 * @author Elmo
 */
public class ScriptTemplate {
    
    private Script script;
    
    public ScriptTemplate(String text) 
    {
        GroovyShell shell = new GroovyShell();
        if ((text.startsWith("#{") && text.endsWith("}")) || (text.startsWith("${") && text.endsWith("}"))) 
            text = text.substring(2, text.length()-1); 
        
        script = shell.parse(text);
    }
    
    public Object execute(Object data) 
    {
        Binding b = null;
        if (data instanceof ExprBean) 
            b = new CustomBinding((ExprBean) data);
        else 
            b = new CustomBinding(new ExprBean(data)); 

        Script s = InvokerHelper.createScript( script.getClass(), b);
        return s.run();
    }
}
