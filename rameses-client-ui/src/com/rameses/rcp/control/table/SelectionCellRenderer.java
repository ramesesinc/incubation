/*
 * SelectionCellRenderer.java
 *
 * Created on June 5, 2013, 1:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.util.UIControlUtil;
import java.util.Collection;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class SelectionCellRenderer extends AbstractCellRenderer
{
    private JLabel label;
    private JCheckBox component;
    
    public SelectionCellRenderer() 
    {
        label = new JLabel("");    
        component = new JCheckBox();
        component.setHorizontalAlignment(SwingConstants.CENTER);
        component.setBorderPainted(true);        
    }

    public JComponent getComponent(JTable table, int rowIndex, int columnIndex) 
    {
        AbstractListDataProvider ldp = ((TableControl) table).getDataProvider();
        if (ldp.getListItemData(rowIndex) == null) return label;
        
        return component;        
    }

    public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) 
    {                
        component.setSelected(false);
        
        if (table.getModel() instanceof DataTableModel) 
        {
            DataTableModel tableModel = (DataTableModel) table.getModel(); 
            Column oColumn = tableModel.getColumn(columnIndex); 
            Object oItem = tableModel.getDataProvider().getListItemData(rowIndex);
            Object exprBean = tableModel.createExpressionBean(oItem); 
            
            Collection checkedItems = null; 
            try {
                checkedItems = (Collection) UIControlUtil.getBeanValue(exprBean, oColumn.getName()); 
            } catch(Exception ex) {;} 

            boolean matched = (checkedItems == null? false: checkedItems.contains(oItem));
            component.setSelected(matched);             
        }
    }  
}
