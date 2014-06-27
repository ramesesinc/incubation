/*
 * WrokflowServiceProxy.java
 *
 * Created on June 27, 2014, 5:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended.proxy;

import groovy.lang.GroovyObject;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class NotificationServiceProxy {
    
    private GroovyObject svc;
    
    /** Creates a new instance of WrokflowServiceProxy */
    public NotificationServiceProxy(Object w) {
        this.svc = (GroovyObject)w;
    }

    public void addMessage(Map msg) {
        svc.invokeMethod( "addMessage", new Object[]{msg} );   
    }
     
    
}
