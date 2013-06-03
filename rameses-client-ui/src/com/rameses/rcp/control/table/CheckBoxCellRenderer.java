/*
 * CheckBoxRenderer.java
 *
 * Created on May 21, 2013, 4:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.AbstractListModel;
import com.rameses.rcp.common.CheckBoxColumnHandler;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class CheckBoxCellRenderer extends AbstractCellRenderer 
{
    private JLabel empty = new JLabel("");    
    private JCheckBox component;
    
    public CheckBoxCellRenderer() 
    {
        component = new JCheckBox();
        component.setHorizontalAlignment(SwingConstants.CENTER);
        component.setBorderPainted(true);
    }
    
    public JComponent getComponent(JTable table, int rowIndex, int colIndex) 
    {
        AbstractListDataProvider ldp = ((TableControl) table).getDataProvider();
        if (ldp.getListItemData(rowIndex) == null) return empty;
        
        return component;
    }
    
    public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int colIndex) 
    {
        AbstractListDataProvider ldp = ((TableControl) table).getDataProvider();
        if (ldp.getListItemData(rowIndex) == null) return;        
        
        component.setSelected(resolveValue(value));
    }
    
    private boolean resolveValue(Object value) 
    {
        boolean selected = false;        
        Object checkValue = null;
        if (column.getTypeHandler() instanceof CheckBoxColumnHandler)
            checkValue = ((CheckBoxColumnHandler) column.getTypeHandler()).getCheckValue();
        
        if (value == null) selected = false;
        else if (value.equals(checkValue+"")) selected = true;
        else if ("true".equals(value+"")) selected = true;
        else if ("yes".equals(value+"")) selected = true;
        else if ("t".equals(value+"")) selected = true;
        else if ("y".equals(value+"")) selected = true;
        else if ("1".equals(value+"")) selected = true;
        
        return selected;
    }    
}
