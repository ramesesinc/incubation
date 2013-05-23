/*
 * ListItem.java
 *
 * This class is re-used by GridComponent and SubItemController
 */

package com.rameses.rcp.common;

import java.util.HashMap;

public class ListItem implements Cloneable 
{
    public static int STATE_EMPTY = 0;
    public static int STATE_SYNC  = 1;
    public static int STATE_DRAFT = 2;
    public static int STATE_EDIT  = 3;
    
    private AbstractListModel parent;
    private boolean selected; 
    private Object item;
    
    private int state = STATE_EMPTY;
    private int index;
    private int rownum;
    
    //new addition. this refers to the calling code
    private Object root;
    
    public ListItem() {
    }
    
    public ListItem clone() 
    {
        ListItem item = new ListItem();
        item.item = this.item;
        item.state = this.state;
        item.parent = this.parent;
        item.index = this.index;
        item.rownum = this.rownum;
        item.selected = this.selected;
        item.root = this.root;
        return item;
    }

    public boolean equals(Object obj) {
        return obj != null && this.hashCode() == obj.hashCode();
    }
    
    public int hashCode() {
        return this.getClass().getName().hashCode() + (parent != null? parent.hashCode():0) + rownum;
    }
    
    public final boolean isRangeAllowedForEditing() 
    {
        if (parent.getDataList() == null) return false; 
        
        int size = parent.getDataList().size(); 
        if (size == index) 
            return true;  
        else 
            return (index >= 0 && index < size); 
    }
    
    public final boolean isDraftItem() { 
        return (getState() == STATE_DRAFT); 
    } 
    
    public final Object createDraftItem() 
    {
        item = parent.createItem();
        if (item == null) item = new HashMap();
        
        state = STATE_DRAFT; 
        return item; 
    }
    
    public final void removeDraftItem() 
    {
        if (isDraftItem()) item = null; 
        
        state = STATE_EMPTY;
    }
    
    public final void commitDraftItem() 
    {
        if (!isDraftItem()) return;
        
        parent.commit(this);
        state = STATE_SYNC; 
    }
        
    
    //this method is called only by the AbstractListModel ONLY.
    //during reload
    public final void loadItem(Object item) {
        this.item = item;
    } 
    
    public final Object getItem() {
        return item;
    }
    
    public final void setItem(Object newitem) 
    {
        if (item == null && newitem == null) return;
        if (item != null && item.equals(newitem)) return;
        
        try 
        {
            //fire only replace if the previous item is not null.
            parent.replaceSelectedItem( index, item, newitem );
            this.item = newitem;
            this.state = STATE_SYNC; 
        } 
        catch(Exception e) {
            MsgBox.err(e);
        }
    }
        
    public AbstractListModel getParent() { return parent; }    
    public void setParent(AbstractListModel parent) {
        this.parent = parent;
    }
    
    public int getState() { return state; }    
    public void setState(int state) {
        this.state = state;
    }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getRownum() { return rownum; }    
    public void setRownum(int rowindex) {
        this.rownum = rowindex;
    }
    
    public boolean isSelected() { return selected; }    
    public void setSelected(boolean selected) 
    {
        if ( item != null) 
        {
            this.selected = selected;
            parent.checkItem(this.item, selected);
        }
    }
        
    public final Object getRoot() { return root; }    
    public final void setRoot(Object root) {
        this.root = root;
    }

}
