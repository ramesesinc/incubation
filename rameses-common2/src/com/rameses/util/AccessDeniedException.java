/*
 * AccessDeniedException.java
 *
 * Created on April 14, 2014, 6:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

/**
 *
 * @author wflores 
 */
public class AccessDeniedException extends AppException 
{
    private static long serialVersionUID = 1L;
    
    public AccessDeniedException() {
        this("Access denied!");
    }
    
    public AccessDeniedException(String message) {
        super(message); 
    }    
    
    public AccessDeniedException(String message, Throwable t) {
        super(message, t); 
    }    

}
