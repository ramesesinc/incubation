/*
 * CheckBoxRenderer.java
 *
 * Created on May 21, 2013, 4:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListModel;
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
    
    public JComponent getComponent(JTable table, int row, int column) 
    {
        AbstractListModel alm = ((TableControl) table).getListModel();
        if (alm.getItemList().get(row).getItem() == null) return empty;
        
        return component;
    }
    
    public void refresh(JTable table, Object value, boolean selected, boolean focus, int row, int column) 
    {
        AbstractListModel alm = ((TableControl) table).getListModel();
        if (alm.getItemList().get(row).getItem() == null) return;
        
        component.setSelected(resolveValue(value));
    }
    
    private boolean resolveValue(Object value) 
    {
        boolean selected = false;
        if (value == null) { /* do nothing */ } else if ("true".equals(value+"")) selected = true;
        else if ("yes".equals(value+"")) selected = true;
        else if ("t".equals(value+"")) selected = true;
        else if ("y".equals(value+"")) selected = true;
        else if ("1".equals(value+"")) selected = true;
        
        return selected;
    }    
}
