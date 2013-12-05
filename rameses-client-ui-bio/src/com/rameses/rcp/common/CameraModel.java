/*
 * CameraModel.java
 *
 * Created on December 5, 2013, 12:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class CameraModel 
{
    
    public CameraModel() {
    }
    
    public String getTitle() { return "Camera"; } 
    
    public void onselect(byte[] bytes) {
    }
    
    public void onclose() {
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {
        Object getBinding();
        void showDialog(CameraModel model);
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
