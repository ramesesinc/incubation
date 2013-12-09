/*
 * FingerPrintModel.java
 *
 * Created on December 8, 2013, 11:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class FingerPrintModel 
{
    
    public FingerPrintModel() {
    }
    
    public String getTitle() { return "FingerPrint Capture"; }     
    
    public void onselect(byte[] bytes) {
    }
    
    public void onclose() {
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {
        Object getBinding();
        void showDialog(FingerPrintModel model);
    }

    
    private Provider provider;
    
    public void setProvider(Provider provider) { 
        this.provider = provider;  
    } 
    
    public Object getBinding() { 
        return (provider == null? null: provider.getBinding()); 
    } 
    
    public void showDialog() { 
        if (provider != null) provider.showDialog(this); 
    } 
    
    // </editor-fold>
}
