/*
 * XConnectionProvider.java
 *
 * Created on February 24, 2013, 9:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.AbstractContext;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class XConnectionProvider {
    
    protected AbstractContext context;
    
    public void setContext(AbstractContext c) {
        context = c;
    }
    
    public abstract String getProviderName();
    public abstract XConnection createConnection(String name, Map conf);
}
