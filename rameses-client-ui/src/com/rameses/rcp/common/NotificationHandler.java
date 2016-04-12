/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores 
 */
public interface NotificationHandler {
    
    boolean accept( Object data );
    
    void handle( Object event );
    
}
