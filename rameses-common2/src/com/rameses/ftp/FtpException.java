/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.ftp;

/**
 *
 * @author wflores
 */
public class FtpException extends RuntimeException { 
    
    static final long serialVersionUID = 1L;

    private int code; 
    
    public FtpException(String message) {
	this( message, -1 ); 
    }    

    public FtpException(String message, int code) {
	super(message);
        this.code = code; 
    }    
    
    public FtpException(String message, Throwable cause) {
        this( message, cause, -1); 
    }    

    public FtpException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code; 
    } 
    
    public int getCode() {
        return code; 
    }
}
