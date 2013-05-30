/*
 * TableModelHandler.java
 *
 * Created on May 15, 2013, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores 
 */
public interface TableModelHandler 
{
    void fireTableCellUpdated(int row, int column);
    void fireTableDataChanged();
    void fireTableRowsDeleted(int firstRow, int lastRow);
    void fireTableRowsInserted(int firstRow, int lastRow);
    void fireTableRowsUpdated(int firstRow, int lastRow);
    void fireTableStructureChanged();     
       
    void fireTableRowSelected(int row, boolean focusOnItemDataOnly); 
}
