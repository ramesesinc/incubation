/*
 * AbstractListModel.java
 *
 * Created on January 14, 2010, 8:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.HashMap;

/**
 *
 * @author elmo
 */
public abstract class AbstractListModel extends AbstractListDataProvider 
{
    private Column primaryColumn;
    private boolean primaryColChecked;
    
    /*
     *  abstract methods
     */
    public abstract void moveNextRecord();
    public abstract void moveBackRecord();
    public abstract void moveFirstPage();
    public abstract void moveNextPage();
    public abstract void moveBackPage();
    
    public abstract void setTopRow(int row);
    public abstract int getTopRow();
    public abstract int getMaxRows();
    
    /**
     * The implementing model determines how to calculate this value.
     * -1 means row count is not determined.
     */
    public abstract int getRowCount();    
    
    
    /**
     * This method is only called once. when initiating.
     * most do not have an implementation. Most notably used
     * by AsyncListModel.
     */
    public void init() {}    
    
    /**
     * This method must be overridden if the developer wants allows
     * the control to allow adding on new items.
     */
    public Object createItem() {
        return new HashMap();
    }    
    
    //overridable. throw exception if there is validation error
    public void validate(ListItem item) {}    
    
    /**
     * this is called during unbinding
     */
    public void destroy() {}    
 
    /**
     * this method is called when there are changes in the row made
     */
    public final void updateSelectedItem() 
    {
//        if(selectedItem.getItem()!=null) {
//            onUpdateItem( selectedItem.getItem() );
//            onColumnUpdate( selectedItem.getItem(), selectedColumn );
//        }
    }
        
    public void onAddItem(Object o) {}    
    public void onRemoveItem(Object o) {}    
    public void onReplaceItem( Object oldValue, Object o ) {}                  
    
    public Column getPrimaryColumn() 
    {
        if ( primaryColumn == null && !primaryColChecked ) 
        {
            primaryColChecked = true;
            Column[] cols = getColumns();
            if ( cols != null && cols.length > 0 ) 
            {
                for ( Column c : cols ) {
                    if ( c.isPrimary() ) 
                    {
                        primaryColumn = c;
                        break;
                    }
                }
            }
        }
        return primaryColumn;
    } 
} 
