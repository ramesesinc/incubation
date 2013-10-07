/*
 * EditorListModel.java
 *
 * Created on May 15, 2013, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class EditorListModel extends AbstractListDataProvider 
{
    private List DEFAULT_LIST = new ArrayList();
    
    public List fetchList(Map params) { return DEFAULT_LIST; }    
    public Object createItem() { return new HashMap(); } 
    
    public boolean isAllowAdd() { return true; } 
        
    /*
     *  Needs to be implemented. This method is invoked when: 
     *  1) The ListItem is in a DRAFT state and about to be added in the list 
     *  2) The ListItem is in an EDIT state and about to changed item selection 
     */
    protected void validateItem(Object item) {}
    
    protected void validate(ListItem li) {
        if (li != null) validateItem(li.getItem()); 
    }
    
    
    /*
     *  Method is invoked before adding it to the data list
     */
    protected void addItem(Object item) {}
    protected void onAddItem(Object item) {
        addItem(item); 
    }
    
    protected boolean onRemoveItem(Object item) { return false; }    
    
    /*
     *  Method is invoked before item selection changed
     */
    protected void commitItem(Object item) {} 
    protected void onCommitItem(Object item) {
        commitItem(item); 
    }
    
    /*
     *  Method is invoked before the value is set to data bean
     */
    protected boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
        return true; 
    } 
    
    /*
     *  Method is invoked after the value has set to data bean
     */
    protected void afterColumnUpdate(Object item, String columnName) {}
    protected void onColumnUpdate(Object item, String columnName) {
        afterColumnUpdate(item, columnName); 
    }
    
    public boolean isColumnEditable(Object item, String columnName) { 
        return true;
    }
    
    public boolean isAllowedForEditing(ListItem li) 
    {
        if (li == null) return false; 

        int index = li.getIndex();        
        int size = getListItems().size();
        if (index == size) return true; 
        
        return (index >= 0 && index < size);
    }
    
    public boolean isTemporaryItem(ListItem li) { 
        return (li.getState() == ListItem.STATE_DRAFT); 
    } 
    
    public Object loadTemporaryItem(ListItem li) 
    {
        Object item = createItem();
        if (item == null) item = new HashMap();
        
        li.loadItem(item, ListItem.STATE_DRAFT); 
        return item; 
    }
    
    public void removeTemporaryItem(ListItem li) 
    {
        if (isTemporaryItem(li)) 
        {
            li.loadItem(null, ListItem.STATE_EMPTY);
            //remove last row only 
            //let us assume that temporary item is second to the last row
            removeListItem(li.getIndex()+1);
        } 
    }
    
    public void flushTemporaryItem(ListItem li) 
    {
        if (!isTemporaryItem(li)) return;
        
        validate(li);
        onAddItem(li.getItem()); 
        li.setState(ListItem.STATE_SYNC); 
    }
    
    public boolean isLastItem(ListItem li) 
    {
        int index = li.getIndex();
        return (index+1 == getListItems().size()); 
    }     

    
    /*
     *  events
     */
    public final void fireBeforeColumnUpdate(ListItem li, Object newValue) { 
        if (li == null) return;
        
        try { 
            boolean success = beforeColumnUpdate(li.getItem(), getSelectedColumn(), newValue); 
            if (success == true) return; 
        } catch(Throwable t) {
            throw new BeforeColumnUpdateException(t); 
        } 
        
        throw new BeforeColumnUpdateException(); 
    }
    
    public final void fireColumnUpdate(ListItem li) {    
        if (li == null) return;
        
        try { 
            onColumnUpdate(li.getItem(), getSelectedColumn()); 
        } catch(Throwable t) {
            throw new AfterColumnUpdateException(t); 
        }
    }

    public void fireCommitItem(ListItem li) 
    {
        if (li == null) return;
        
        onCommitItem(li.getItem()); 
        li.setState(ListItem.STATE_SYNC); 
    }

    public void fireValidateItem(ListItem li) 
    {
        if (li == null) return;
        
        validate(li); 
    }

    public void fireRemoveItem(ListItem li) 
    {
        if (li == null) return;
        if (li.getState() == ListItem.STATE_EMPTY) return;
                
        int index = li.getIndex();
        getMessageSupport().removeErrorMessage(index); 
                
        if (li.getState() == ListItem.STATE_DRAFT) 
        {
            if (getListItem(index+1) == null)
            {
                li.loadItem(null, ListItem.STATE_EMPTY); 
                tableModelSupport.fireTableRowsUpdated(index, index); 
            }
            else 
            {
                getDataList().remove(index); 
                getListItems().remove(index); 
                tableModelSupport.fireTableRowsDeleted(index, index); 
            }
        }
        else 
        {
            if (!onRemoveItem(li.getItem())) return;

            int itemIndex = getListItems().indexOf(li); 
            int dataIndex = getDataList().indexOf(li.getItem());             
            if (dataIndex < 0 && itemIndex == index) refresh(false); 
        }
    }        

    // <editor-fold defaultstate="collapsed" desc=" Custom Exceptions ">  
    
    public static class BeforeColumnUpdateException extends RuntimeException { 
        
        BeforeColumnUpdateException() {
            super(); 
        }         
        
        BeforeColumnUpdateException(Throwable caused) {
            super(caused); 
        } 
    }
    
    public static class AfterColumnUpdateException extends RuntimeException { 
        
        AfterColumnUpdateException() {
            super(); 
        }         
        
        AfterColumnUpdateException(Throwable caused) {
            super(caused); 
        } 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIEditorProvider interface and proxy methods ">        

    public static interface UIEditorProvider {
        void refreshCurrentEditor(ListItem li);        
    } 
    
    
    private UIEditorProvider _uiEditorProvider; 
    
    public void setUIEditorProvider(UIEditorProvider uiEditorProvider) { 
        this._uiEditorProvider = uiEditorProvider; 
    }
    
    public final void refreshEditedCell() {
        refreshCurrentEditor(); 
    }
    
    public final void refreshCurrentEditor() {
        if (_uiEditorProvider == null) return;
        
        ListItem li = getSelectedItem(); 
        if (li != null) _uiEditorProvider.refreshCurrentEditor(li); 
    }    
    
    // </editor-fold>    
}
