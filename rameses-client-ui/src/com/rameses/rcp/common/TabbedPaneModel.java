/*
 * TabbedPaneModel.java
 *
 * Created on September 2, 2013, 11:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class TabbedPaneModel {
    
    public TabbedPaneModel() {
    }
    
    public List<Opener> getOpeners() { return null; } 
    
    public Object getBinding() {
        return (provider == null? null: provider.getBinding());
    }
    
    public List lookupOpeners(String invokerType, Map params) {
        if (provider == null) return new ArrayList(); 
        
        return provider.lookupOpeners(invokerType, params); 
    }
    
    public Map getOpenerParams(Object o) { return null; }
    
    public void reload() {
        if (provider != null) provider.reload(); 
    }
    
    public void refresh() {
        if (provider != null) provider.refresh(); 
    }
    
        
    // <editor-fold defaultstate="collapsed" desc=" Provider interface ">

    private TabbedPaneModel.Provider provider; 

    public final void setProvider(TabbedPaneModel.Provider provider) {
        this.provider = provider; 
    } 
    
    public static interface Provider {
        Object getBinding(); 
        Object lookupOpener(String invokerType, Map params);        
        List lookupOpeners(String invokerType, Map params);
        
        void reload();
        void refresh();
    } 
    
    // </editor-fold>     
}
