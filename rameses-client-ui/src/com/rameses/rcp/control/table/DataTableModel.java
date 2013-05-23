/*
 * DataTableModel.java
 *
 * Created on January 31, 2011
 * @author jaycverg
 */
package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListModel;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.common.ListModelSupport;
import com.rameses.common.PropertyResolver;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel implements TableControlModel 
{
    private List<Column> columnList = new ArrayList();    
    private ListModelSupport listModelSupport; 
    private AbstractListModel listModel;
    private String varStatus;
    
    public String getVarStatus() { return varStatus; }    
    public void setVarStatus(String varStatus) { this.varStatus = varStatus; }     
    
    public final boolean hasListModelSupport() {
        return (listModelSupport != null); 
    }
    
    public ListModelSupport getListModelSupport() { return listModelSupport; } 
    public AbstractListModel getListModel() { return listModel; }    
    public void setListModel(AbstractListModel model) 
    {
        columnList.clear();        
        listModel = model;
        
        if (listModel == null) { 
            listModelSupport = null; 
        } 
        else 
        {
            listModelSupport = AbstractListModel.Support.link(this, listModel); 
            indexColumns();
        } 
    }
    
    private void indexColumns() {
        for ( Column col : listModel.getColumns() ) {
            if ( col.isVisible() ) {
                columnList.add(col);
            }
        }
    }
    
    public void reIndexColumns() {
        columnList.clear();
        indexColumns();
    }
    
    
    public int getRowCount() {
        return listModel.getItemList().size();
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
            return listModel.getItemList().get(rowIndex); 
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
        resolver.setProperty(item, column.getName(), value); 
        
        ListItem li = getListItem(rowIndex);
        if (li.getState() == ListItem.STATE_SYNC)
            li.setState(ListItem.STATE_EDIT); 
        
        fireTableRowsUpdated(rowIndex, rowIndex); 
    }

    public void commit(ListItem item) 
    {
        getListModel().commit(item);
        fireTableRowsUpdated(item.getIndex(), item.getIndex()); 
    }

    void ensureRowVisibility(int rowIndex) 
    {
        ListItem li = getListItem(rowIndex);
        if (li != null) return;
        
        if (hasListModelSupport()) 
            listModelSupport.createListItemAt(rowIndex);
            
        listModel.createListItemAt(rowIndex); 
    }

    void removeDraftItem(ListItem li) 
    {
        if (!li.isDraftItem()) return;
        
        int rowIndex = li.getIndex(); 
        listModel.removeErrorMessage(rowIndex); 

        
        
        if (listModel.removeListItemAt(rowIndex)) 
        {
            if (getListItem(rowIndex) != null) 
                fireTableRowsUpdated(rowIndex, rowIndex); 
        }
        else 
        {
            listModel.replaceListItem(li); 
            fireTableRowsUpdated(rowIndex, rowIndex); 
        }
    }
}
