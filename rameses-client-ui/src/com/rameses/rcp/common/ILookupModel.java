/*
 * ILookupModel.java
 *
 * Created on September 3, 2013, 1:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public interface ILookupModel {

    Object getValue();
    
    LookupSelector getSelector(); 
    void setSelector(LookupSelector selector);
    
    String getReturnItemKey();
    void setReturnItemKey(String returnItemKey);

    String getReturnItemValue();
    void setReturnItemValue(String returnItemValue);
    
    String getReturnFields();
    void setReturnFields(String returnFields);
    
    boolean show(String searchtext);
}
