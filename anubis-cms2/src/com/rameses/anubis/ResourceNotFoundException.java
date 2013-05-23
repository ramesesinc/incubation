/*
 * PageNotFoundException.java
 *
 * Created on July 19, 2012, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

/**
 *
 * @author Elmo
 */
public class ResourceNotFoundException extends Exception {
    
    /** Creates a new instance of PageNotFoundException */
    public ResourceNotFoundException(String pageName) {
        super(pageName);
    }
    
     /** Creates a new instance of PageNotFoundException */
    public ResourceNotFoundException() {
        super();
    }
    
}
