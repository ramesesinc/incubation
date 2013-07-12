/*
 * ErrorException.java
 *
 * Created on July 11, 2013, 2:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

/**
 *
 * @author wflores
 */
public class ErrorException extends AppException 
{
    public static long serialVersionUID = 1L;    
    
    private int code;
    
    public ErrorException(int code, String message) {
        super(message);
        this.code = code; 
    }

    public ErrorException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public int getCode() { return code; } 
    
}
