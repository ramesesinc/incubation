/*
 * ImageGalleryModel.java
 *
 * Created on April 21, 2014, 3:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ImageGalleryModel 
{
    public int getCols() { return 5; } 
    
    public List fetchList(Map params) {
        return null; 
    }
    
    public Object onselect(Object item) {
        return null; 
    }
    
    public Object onopen(Object item) {
        return null; 
    }
    
    public void reload() {
        if (provider != null) provider.reload(); 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider interface ">
    
    public static interface Provider 
    {
        void reload(); 
    } 
    
    
    private Provider provider;
    public void setProvider(Provider provider) {
        this.provider = provider; 
    }
    
    // </editor-fold>    
}
