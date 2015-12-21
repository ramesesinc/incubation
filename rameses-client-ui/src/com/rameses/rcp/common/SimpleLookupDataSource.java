/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author wflores 
 */
public interface SimpleLookupDataSource {
    
    void setSelector(LookupSelector selector); 
    
    boolean show(String searchtext); 
    
}
