/*
 * DataTableModel.java
 *
 * Created on January 31, 2011
 * @author jaycverg
 */
package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ListItem;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.TableModelHandler;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel implements TableControlModel, TableModelHandler 
{
    private List<Column> columnList = new ArrayList();    
    private AbstractListDataProvider dataProvider; 
    private DataTableBinding binding;
    private String multiSelectName;    
    private String varName = "item";
    private String varStatus;
    
    void setBinding(DataTableBinding binding) { this.binding = binding; }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public String getVarStatus() { return varStatus; }    
    public void setVarStatus(String varStatus) { this.varStatus = varStatus; }     
        
    public String getMultiSelectName() { return multiSelectName; } 
    public void setMultiSelectName(String multiSelectName) {
        this.multiSelectName = multiSelectName; 
    }    
    
    public AbstractListDataProvider getDataProvider() { return dataProvider; }    
    public void setDataProvider(AbstractListDataProvider dataProvider) 
    {
        if (this.dataProvider != null) {
            this.dataProvider.removeHandler(this);
        } 
        
        this.dataProvider = dataProvider;
        if (this.dataProvider != null) {
            this.dataProvider.addHandler(this);
        } 
        
        reIndexColumns(); 
    } 
    
    public void reIndexColumns() 
    {
        columnList.clear(); 
        
        if (dataProvider == null) return;        
        if (dataProvider.isMultiSelect()) 
        {
            String multiName = getMultiSelectName();
            if (multiName == null) multiName = "dataListProvider.checkedItems";
            
            Column col = new Column(multiName, " ");
            col.setTypeHandler(new SelectionColumnHandler()); 
            columnList.add(col);
        } 
        
        for (Column col : dataProvider.getColumns()) 
        {
            if (col.isVisible()) columnList.add(col);
        } 
    }
        
    public int getRowCount() {
        return dataProvider.getListItems().size();
    } 
    
    public Column getColumn(int index) 
    {
        if (index >= 0 && index < columnList.size())
            return columnList.get(index);
        
        return null;
    }
    
    public int getColumnIndex(String name) 
    {
        for (int i=0; i<columnList.size(); i++) 
        {
            String cname = columnList.get(i).getName();
            if (cname != null && cname.equals(name)) return i;
        } 
        return -1;
    }
    
    public String getColumnName(int column) {
        return columnList.get(column).getCaption();
    }    
    
    public int getColumnCount() {
        return columnList.size();
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }    
    
    public Object getItem(int rowIndex) 
    {
        ListItem item = getListItem(rowIndex);
        return (item == null? null: item.getItem()); 
    }

    public ListItem getListItem(int rowIndex) 
    {
        try {
            return dataProvider.getListItem(rowIndex); 
        } catch(Exception e) {
            return null;
        } 
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) 
    {
        PropertyResolver resolver = PropertyResolver.getInstance();
        ListItem item = getListItem(rowIndex); 
        if (item == null) return null; 
        
        String name = columnList.get(columnIndex).getName();
        if (ValueUtil.isEmpty(name)) return null; 
        
        if (varStatus == null) {
            //do nothing
        }
        else if (name.equals(varStatus)) {
            return item;
        } 
        else if (name.startsWith(varStatus + ".")) { 
            return resolver.getProperty(item, name.substring(name.indexOf(".")+1));
        }

        if (item.getItem() != null) 
            return resolver.getProperty(item.getItem(), name);
        else
            return null; 
    }  

    public void setValueAt(Object value, int rowIndex, int columnIndex) 
    {
        Column column = getColumn(columnIndex);
        if (column == null) return;
        if (ValueUtil.isEmpty(column.getName())) return;
        
        Object item = getItem(rowIndex); 
        if (item == null) 
            throw new NullPointerException("The item object in row " + rowIndex + " column " + columnIndex + " is not initialized");
        
        PropertyResolver resolver = PropertyResolver.getInstance();
        if (column.getTypeHandler() instanceof SelectionColumnHandler)
        {
            Object exprBean = createExpressionBean(rowIndex);             
            Collection checkedItems = null; 
            try 
            { 
                checkedItems = (Collection) resolver.getProperty(exprBean, column.getName()); 
                if (checkedItems == null) return;
            } 
            catch(Exception ex) {;} 
            
            synchronized (checkedItems) 
            {
                if ("true".equals(value+"")) 
                    checkedItems.add(item);
                else 
                    checkedItems.remove(item);
            }
            
            fireTableRowsUpdated(rowIndex, rowIndex); 
        } 
        else 
        {
            Object oldValue = null; 
            try { 
                oldValue = resolver.getProperty(item, column.getName()); 
            } catch(Exception ex) {;} 

            //exit if no changes made
            if (!hasValueChanged(oldValue, value)) return;

            resolver.setProperty(item, column.getName(), value); 

            ListItem li = getListItem(rowIndex);
            if (li.getState() == ListItem.STATE_SYNC)
                li.setState(ListItem.STATE_EDIT); 

            fireTableRowsUpdated(rowIndex, rowIndex); 

            try { 
                binding.getChangeLog().addEntry(item, column.getName(), oldValue, value);
            } catch(Exception ex) {;} 
        }
    }

    public void fireTableRowSelected(int row, boolean focusOnItemDataOnly) {
    }
    
    private boolean hasValueChanged(Object oldValue, Object newValue) 
    {
        if (oldValue == null && newValue == null) return false; 
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) return false; 
        
        return true;
    }
   
    public Object createExpressionBean(int rowIndex) 
    {
        Object item = dataProvider.getListItemData(rowIndex); 
        return createExpressionBean(item);
    } 
    
    public Object createExpressionBean(Object itemBean) 
    {
        Object rootBean = binding.getRoot().getBean();
        ExprBeanSupport support = new ExprBeanSupport(rootBean);
        support.setItem("dataListProvider", dataProvider); 
        
        if (getVarName() != null)
        {
            if (itemBean == null) itemBean = new HashMap(); 
            
            support.setItem(getVarName(), itemBean); 
        } 
        return support.createProxy(); 
    }     
}
