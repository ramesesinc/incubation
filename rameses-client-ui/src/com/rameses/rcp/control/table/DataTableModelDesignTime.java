/*
 * DataTableModelDesignTime.java
 *
 * Created on May 28, 2013, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wflores
 */
class DataTableModelDesignTime extends AbstractTableModel
{
    private Column[] columns;
    
    public DataTableModelDesignTime(Column[] columns) {
        this.columns = columns; 
    }

    public int getRowCount() { return 1; }

    public int getColumnCount() { 
        return (columns == null? 0: columns.length); 
    }

    public String getColumnName(int index) 
    {
        int colCount = getColumnCount();
        if (index >= 0 && index < colCount) 
            return columns[index].getCaption(); 
        else
            return "";
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return "";
    }
    
}
