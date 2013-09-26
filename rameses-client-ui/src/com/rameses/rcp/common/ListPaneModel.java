/*
 * ListCellModel.java
 *
 * Created on September 9, 2013, 4:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class ListPaneModel 
{
    
    public ListPaneModel() {
    }

    public void afterLoadItems() {}
    
    public boolean beforeSelect(Object item) { return true; }    
    public void onselect(Object item) {}
    
    public Object getItems() { return null; }
    public String getDefaultIcon() { return null; } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" proxying methods ">
    
    public Object getBinding() {
        return (provider == null? null: provider.getBinding()); 
    }
    
    public void refresh() {
        if (provider != null) provider.refresh();
    }
    
    public void reload() {
        if (provider != null) provider.reload();
    }    
    
    public void setSelectedIndex(int index) {
        if (provider != null) provider.setSelectedIndex(index); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    private Provider provider;    
    public final void setProvider(Provider provider) {
        this.provider = provider; 
    }
    
    public static interface Provider 
    {
        Object getBinding();
        
        void refresh();
        void reload();
        void setSelectedIndex(int index); 
    }
    
    // </editor-fold>
}
