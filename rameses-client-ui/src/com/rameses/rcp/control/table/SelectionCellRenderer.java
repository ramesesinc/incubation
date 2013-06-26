/*
 * SelectionCellRenderer.java
 *
 * Created on June 5, 2013, 1:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.util.UIControlUtil;
import java.lang.reflect.Method;
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
public class SelectionCellRenderer extends CellRenderers.AbstractRenderer
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
        Object itemData = getTableControl().getDataProvider().getListItemData(rowIndex);
        return (itemData == null? label: component);
    }

    public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int columnIndex) 
    {                
        component.setSelected(false);
        
        Column oColumn = getTableControlModel().getColumn(columnIndex); 
        Object itemData = getTableControl().getDataProvider().getListItemData(rowIndex); 
        Collection checkedItems = getSourceItems(itemData, oColumn.getName());

        boolean matched = false;
        Object checkHandler = getTableControl().getDataProvider().getMultiSelectHandler(); 
        if (checkHandler != null) 
            matched = isItemCheckedFromHandler(checkHandler, itemData); 
        
        if (!matched)
            matched = (checkedItems == null? false: checkedItems.contains(itemData));
        
        if (checkedItems != null) 
        {
            checkedItems.remove(itemData);
            if (matched) checkedItems.add(itemData);
            
            component.setSelected(matched); 
        }
    }  
    
    private Collection getSourceItems(Object itemData, String name) 
    {
        Object exprBean = getTableControl().createExpressionBean(itemData);

        Collection checkedItems = null; 
        try {
            checkedItems = (Collection) UIControlUtil.getBeanValue(exprBean, name); 
        } catch(Exception ex) {;} 
        
        return checkedItems; 
    }
    
    private boolean isItemCheckedFromHandler(Object handler, Object itemData) 
    {
        try 
        {
            Class handlerClass = handler.getClass();
            Method m = handlerClass.getMethod("call", new Class[]{Object.class}); 
            Object res = m.invoke(handler, new Object[]{ itemData }); 
            if (res instanceof Boolean) 
                return ((Boolean) res).booleanValue(); 
            else 
                return "true".equals(res+""); 
        }
        catch(Throwable ex) 
        {
            if (ClientContext.getCurrentContext().isDebugMode()) 
                ex.printStackTrace(); 
            
            return false; 
        }
    }
    
}
