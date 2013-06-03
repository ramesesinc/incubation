/*
 * TableModelSupport.java
 *
 * Created on May 15, 2013, 10:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class TableModelSupport implements TableModelHandler, Cloneable
{
    private List<TableModelHandler> handlers = new ArrayList<TableModelHandler>(); 
    
    public TableModelSupport() {
    }
    
    public void removeAll() { handlers.clear(); }

    public void remove(TableModelHandler handler) {
        if (handler != null) handlers.remove(handler); 
    }

    public void add(TableModelHandler handler) 
    {
        if (handler != null && !handlers.contains(handler)) 
            handlers.add(handler); 
    } 
    
    public TableModelSupport clone() 
    {
        TableModelSupport tms = new TableModelSupport();
        for (TableModelHandler handler : handlers) {
            tms.handlers.add(handler); 
        } 
        return tms; 
    }
    
    public void fireTableCellUpdated(int row, int column) {
        for (TableModelHandler handler : handlers) {
            handler.fireTableCellUpdated(row, column); 
        }
    }

    public void fireTableDataChanged() {
        for (TableModelHandler handler : handlers) {
            handler.fireTableDataChanged();
        }        
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        for (TableModelHandler handler : handlers) {
            handler.fireTableRowsDeleted(firstRow, lastRow);
        }          
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        for (TableModelHandler handler : handlers) {
            handler.fireTableRowsInserted(firstRow, lastRow);
        }           
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        for (TableModelHandler handler : handlers) {
            handler.fireTableRowsUpdated(firstRow, lastRow);
        } 
    }

    public void fireTableStructureChanged() {
        for (TableModelHandler handler : handlers) {
            handler.fireTableStructureChanged();
        }         
    }

    public void fireTableRowSelected(int row, boolean focusOnItemDataOnly) {
        for (TableModelHandler handler : handlers) {
            handler.fireTableRowSelected(row, focusOnItemDataOnly);
        }          
    }    
}
