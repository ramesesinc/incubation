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
    
    public List fetchList(Map params) { 
        return DEFAULT_LIST; 
    }
    
    public Object createItem() { 
        return new HashMap(); 
    } 
    
    protected void validate(ListItem li) {}
    
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
        getDataList().add(li.getItem()); 
        li.setState(ListItem.STATE_SYNC); 
    }
    
    public boolean isLastItem(ListItem li) 
    {
        int index = li.getIndex();
        return (index+1 == getListItems().size()); 
    }     
}