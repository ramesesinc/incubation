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
        
    /*
     *  Needs to be implemented. This method is invoked when: 
     *  1) The ListItem is in a DRAFT state and about to be added in the list 
     *  2) The ListItem is in an EDIT state and about to changed item selection 
     */
    protected void validate(ListItem li) {}
    
    /*
     *  Method is invoked before adding it to the data list
     */
    protected void onAddItem(Object item) {}
    
    protected boolean onRemoveItem(Object item) { return false; }    
    
    /*
     *  Method is invoked before item selection changed
     */
    protected void onCommitItem(Object item) {}
    
    /*
     *  Method is invoked after the value has set to data bean
     */
    protected void onColumnUpdate(Object item, String columnName) {}
    
    
    
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
        
        int dataIndex = getDataList().indexOf(li.getItem());
        if (dataIndex < 0)
            getDataList().add(li.getItem()); 
        else    
            getDataList().set(dataIndex, li.getItem()); 
        
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
    public void fireColumnUpdate(ListItem li) 
    {    
        if (li == null) return;
        
        onColumnUpdate(li.getItem(), getSelectedColumn()); 
    }

    public void fireCommitItem(ListItem li) 
    {
        if (li == null) return;
        
        onCommitItem(li.getItem());         
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
}
