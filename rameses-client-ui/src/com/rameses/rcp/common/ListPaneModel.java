/*
 * ListCellModel.java
 *
 * Created on September 9, 2013, 4:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * @author wflores
 */
public class ListPaneModel {
    
    public ListPaneModel() {
    }

    @Deprecated
    public void afterLoadItems() {}
    
    @Deprecated
    public Object getItems() { return null; }
    
    public boolean beforeSelect(Object item) { return true; }    
    public void onselect(Object item) {}
        
    public String getDefaultIcon() { return null; } 

    public List fetchList( Map params ) {
        Object items = getItems(); 
        if (items instanceof Object[]) {
            Object[] values = (Object[])items; 
            return Arrays.asList(values); 
        } else {
            return (List)items;
        }
    }
        
    public void afterFetchList() {
        afterLoadItems(); 
    }    
    
    public long getRefreshInterval() {
        return 0; 
    }
    
    public void removeItem( Object value ) {
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" proxying methods ">
    
    public Object getBinding() {
        return (provider == null? null: provider.getBinding()); 
    }
    public void repaint() {
        if (provider != null) provider.repaint(); 
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
        
        void repaint();        
        void refresh();
        void reload();
        void setSelectedIndex(int index);         
    }
    
    // </editor-fold>
}
