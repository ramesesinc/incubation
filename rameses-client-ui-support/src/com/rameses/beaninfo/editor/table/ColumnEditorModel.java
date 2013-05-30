/*
 * ColumnEditorModel.java
 *
 * Created on May 20, 2013, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.Column;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wflores
 */
public class ColumnEditorModel extends AbstractTableModel
{
    private List<Column> rows = new ArrayList<Column>(); 
    private String[] columns = { "Column" };
    
    public ColumnEditorModel() {
    }
    
    
    public Column[] getColumns() {
        return rows.toArray(new Column[]{}); 
    }
    
    public void setColumns(Column[] columns) 
    {
        rows.clear();        
        if (columns == null) return;
        
        for (int i=0; i<columns.length; i++) rows.add(columns[i]); 
    }

    public int getRowCount() { return rows.size(); }
    public int getColumnCount() { return columns.length; }
    public String getColumnName(int index) { return columns[index]; }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; 
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) 
    {
        try 
        {
            Column col = rows.get(rowIndex); 
            if (col.getCaption() != null) 
                return col.getCaption(); 
            else 
                return col.getName(); 
        } 
        catch(Exception ex) {
            return null; 
        }
    }
    
    Column getItem(int index) 
    {
        try {
            return rows.get(index); 
        } catch(Exception ex) {
            return null; 
        }
    }
    
    Column addRow() 
    {
        Column col = new Column();
        int index = rows.size(); 
        rows.add(col); 
        fireTableRowsInserted(index, index); 
        return col;
    }
    
    boolean removeItem(int index) 
    {
        if (index >= 0 && index < rows.size()) 
        {
            rows.remove(index); 
            fireTableRowsDeleted(index, index); 
            return true; 
        }
        else {
            return false; 
        }
    }
    
    boolean moveItemUp(int index) 
    {
        if (index-1 >= 0 && index-1 < getRowCount()) 
        {
            Column popItem = rows.remove(index-1); 
            rows.add(index, popItem);
            fireTableDataChanged(); 
            return true;
        }
        else {
            return false; 
        }
    }
    
    boolean moveItemDown(int index) 
    {
        int moveIndex = index+1;
        if (moveIndex >= 0 && moveIndex < getRowCount()) 
        {
            Column moveItem = rows.get(index); 
            Column affectedItem = rows.get(moveIndex); 
            rows.set(moveIndex, moveItem);
            rows.set(index, affectedItem); 
            fireTableRowsUpdated(index, moveIndex); 
            return true;
        }
        else {
            return false; 
        }
    }    
    
    int indexOf(Column c) {
        for (int i=0; i<rows.size(); i++) {
            if (c.equals(rows.get(i))) return i; 
        }
        return -1;
    }
}
