/*
 * LookupDataSource.java
 *
 * Created on September 15, 2013, 9:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface LookupDataSource extends SimpleLookupDataSource 
{
    Object getOnselect();
    Object getOnempty();
    
    LookupSelector getSelector(); 
        
    String getReturnItemKey();
    void setReturnItemKey(String returnItemKey);

    String getReturnItemValue();
    void setReturnItemValue(String returnItemValue);
    
    String getReturnFields();
    void setReturnFields(String returnFields);    
} 
