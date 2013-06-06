/*
 * SelectionBox.java
 *
 * Created on June 5, 2013, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class SelectionCellEditor extends JCheckBox implements UIInput
{
    private ItemHandler itemHandler; 
    private Binding binding;
    
    public SelectionCellEditor() 
    {
        setHorizontalAlignment(SwingConstants.CENTER); 
        setBorderPainted(true);    
        addItemListener((itemHandler = new ItemHandler())); 
    }

    // <editor-fold defaultstate="collapsed" desc="  UIInput implementation  ">
    
    public Object getValue() { return null; }    
    public void setValue(Object value) 
    {
        if (value instanceof KeyEvent) 
        {
            KeyEvent ke = (KeyEvent) value;
            if (ke.getKeyCode() != KeyEvent.VK_SPACE) return;
        }
        refresh();
        setSelected(!isSelected()); 
    }

    public boolean isNullWhenEmpty() { return false; }
    public boolean isImmediate() { return true; }

    public boolean isReadonly() { return false; }    
    public void setReadonly(boolean readonly) {}

    public void setRequestFocus(boolean focus) {}

    public String[] getDepends() { return null; }
    public int getIndex() { return 0; }

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public void load() {
    }
    
    public void refresh() 
    {
        try 
        {
            itemHandler.enabled = false;
            setSelected(itemHandler.isItemMatches()); 
        } 
        catch(Exception e) 
        {
            if (ClientContext.getCurrentContext().isDebugMode())
                e.printStackTrace();
        } 
        finally 
        {
            itemHandler.enabled = true;
        }
    } 

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  ItemHandler (class)  ">
    
    private class ItemHandler implements ItemListener
    {
        private SelectionCellUtil cellUtil = SelectionCellUtil.newInstance();
        private boolean enabled = true;
        
        public void itemStateChanged(ItemEvent e) 
        {
            if (!enabled) return;
            
            try 
            {
                Object oResult = null;
                Object checkedItems = getCheckedItems(); 
                
                boolean result = (e.getStateChange() == ItemEvent.SELECTED); 
                if (result) 
                    oResult = cellUtil.attachItem(checkedItems, binding.getBean());
                else 
                    oResult = cellUtil.detachItem(checkedItems, binding.getBean()); 
                    
                updateBean(oResult); 
            }
            catch(Exception ex) {
                MsgBox.err(ex);
            }
        }      
        
        boolean isItemMatches() 
        {
            Object checkedItems = getCheckedItems();
            return cellUtil.match(checkedItems, binding.getBean());             
        }
        
        Object getCheckedItems() 
        {
            Binding oBinding = (Binding) getClientProperty(Binding.class); 
            return UIControlUtil.getBeanValue(oBinding, getName()); 
        }
        
        void updateBean(Object value) 
        {
            Binding oBinding = (Binding) getClientProperty(Binding.class); 
            UIControlUtil.setBeanValue(oBinding, getName(), value); 
        }
    }
    
    // </editor-fold>
}
