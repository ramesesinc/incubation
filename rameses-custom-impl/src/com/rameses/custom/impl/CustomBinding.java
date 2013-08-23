/*
 * CustomBinding.java
 *
 * Created on May 23, 2013, 3:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.custom.impl;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class CustomBinding extends Binding{
    
    public CustomBinding(Map map){
        super(map);
    }
    
    public Object getVariable(String name) {
        try {
            return super.getVariable(name);
        } catch (MissingPropertyException e) {
            return null;
        }
    }
    
}
