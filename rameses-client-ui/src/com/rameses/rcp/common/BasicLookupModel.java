/*
 * BasicLookupModel.java
 *
 * Created on September 3, 2013, 11:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class BasicLookupModel implements ILookupModel 
{
    private LookupSelector selector; 
    private String returnItemKey;
    private String returnItemValue;
    private String returnFields; 
    
    public BasicLookupModel() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">  
    
    public Object getValue() { 
        //This method needs to be overidden by the implementors 
        return null; 
    } 
    
    public LookupSelector getSelector() { return selector; }    
    public void setSelector(LookupSelector selector) { 
        this.selector = selector; 
    }
    
    public String getReturnItemKey() { return returnItemKey; }    
    public void setReturnItemKey(String returnItemKey) { 
        this.returnItemKey = returnItemKey; 
    }

    public String getReturnItemValue() { return returnItemValue; }    
    public void setReturnItemValue(String returnItemValue) { 
        this.returnItemValue = returnItemValue; 
    } 
    
    public String getReturnFields() { return returnFields; }    
    public void setReturnFields(String returnFields) { 
        this.returnFields = returnFields; 
    }   
        
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" helper/owner methods ">
    
    public boolean show(String searchtext) { 
        //This method is invoked by the XLookupField control before showing the popup form. 
        //return true will open the popup dialog 
        return true; 
    } 
        
    protected void onselect(Object item) {} 
    
    public Object doSelect() { 
        Object value = getValue();
        onselect(value); 
        
        LookupSelector selector = getSelector();
        if (selector != null) selector.select(value); 
        
        return "_close";
    } 
    
    public Object doCancel() { 
        LookupSelector selector = getSelector();
        if (selector != null) selector.cancelSelection(); 
        
        return "_close"; 
    } 
    
    protected void onfinalize() throws Throwable {
        selector = null;
    }    
    
    // </editor-fold>
}
