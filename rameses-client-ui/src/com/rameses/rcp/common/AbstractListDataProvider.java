/*
 * AbstractListDataProvider.java
 *
 * Created on May 14, 2013, 2:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author wflores
 */
public abstract class AbstractListDataProvider 
{
    public final static int FETCH_MODE_LOAD     = 0;
    public final static int FETCH_MODE_REFRESH  = 1;
    public final static int FETCH_MODE_RELOAD   = 2;
        
    protected PropertyChangeSupport propertySupport; 
    protected TableModelSupport tableModelSupport; 
    protected MessageSupport messageSupport;
    private ListItemHandler listItemHandler;
    
    private Set checkedItems = new HashSet(); 
    private int fetchMode = FETCH_MODE_LOAD; 
    
    private String selectedColumn;    
    private Column[] columns;
    private List dataList;
    private int fetchedRows;
    private boolean multiSelect;
    private boolean processing;

    private List<ListItem> itemList = new ArrayList<ListItem>(); 
    private ListItem selectedItem; 
        
    public AbstractListDataProvider() 
    {
        propertySupport = new PropertyChangeSupport();
        tableModelSupport = new TableModelSupport(); 
        listItemHandler = new ListItemHandler();        
        messageSupport = new MessageSupport(); 
    }
    
    public abstract List fetchList(Map params);

    public Column[] getColumns() { return columns; } 
    public void setColumns(Column[] columns) { this.columns = columns; }
    
    public boolean isMultiSelect() { return multiSelect; } 
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
    
    public Column getPrimaryColumn() { return null; } 
    
    public void removeHandler(TableModelHandler handler) {
        tableModelSupport.remove(handler); 
    }
    public void addHandler(TableModelHandler handler) {
        tableModelSupport.add(handler); 
    }

    public void removeHandler(PropertyChangeHandler handler) {
        propertySupport.remove(handler); 
    }
    public void addHandler(PropertyChangeHandler handler) {
        propertySupport.add(handler); 
    }
        
    protected void fetch(boolean forceLoad) 
    {
        if (dataList == null || forceLoad) 
        {
            Map params = new HashMap();
            onbeforeFetchList(params);

            List resultList = fetchList(params);
            if (resultList == null) resultList = new ArrayList();

            onafterFetchList(resultList);
            setDataList(resultList); 
            fetchedRows = resultList.size();
        } 
        else {
            fetchedRows = (dataList == null? 0: dataList.size()); 
        }

        fillListItems(dataList, 0); 

        if (selectedItem != null) setSelectedItem(selectedItem.getIndex());
        if (selectedItem == null) setSelectedItem(0);
    } 
    
    private void fetchImpl() 
    {
        try
        {
            propertySupport.firePropertyChange("loading", true);
            processing = true;
            boolean forceLoad = (fetchMode == FETCH_MODE_LOAD || fetchMode == FETCH_MODE_RELOAD); 
            fetch(forceLoad); 
            tableModelSupport.fireTableDataChanged(); 
            
            int index = (selectedItem == null? 0: selectedItem.getIndex());
            tableModelSupport.fireTableRowSelected(index, false);
            //fireFocusSelectedItem(); 
        }
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } 
        finally 
        {
            processing = false;
            propertySupport.firePropertyChange("loading", false);        
        }
    }

    public final boolean isProcessing() { return processing; }
    
    protected void onbeforeFetchList(Map params) {}
    protected void onafterFetchList(List resultList) {}

    public int getTopRow() { return 0; }
    public void setTopRow(int topRow) {}  
            
    public int getMaxRows() { return getRowCount(); }     
    public int getRowCount() { return fetchedRows; }
    public int getRows() { return -1; }
    
    public boolean validRange(int index) { 
        return (index >= 0 && index < itemList.size()); 
    } 
    
    public int getListItemCount() { return itemList.size(); }    
    public List<ListItem> getListItems() { return itemList; }
    
    public Object getListItemData(int index) 
    {
        ListItem li = getListItem(index); 
        return (li == null? null: li.getItem()); 
    }
    
    public ListItem getListItem(int index) 
    {
        if (index >= 0 && index < itemList.size()) 
            return itemList.get(index); 
        else
            return null; 
    }
    
    public ListItem getListItemByRownum(int rownum) 
    {
        for (int i=0; i<itemList.size(); i++) 
        {
            if (itemList.get(i).getRownum() == rownum) 
                return itemList.get(i); 
        } 
        return null; 
    }
    
    public ListItem getSelectedItem() { return selectedItem; } 
    public void setSelectedItem(ListItem li) 
    {
        int index = (li == null? -1: li.getIndex()); 
        setSelectedItem(index); 
    }

    protected void onselectedItemChanged(ListItem li) {}        
    public void setSelectedItem(int index) 
    {  
        if (index >= 0 && index < itemList.size()) 
            selectedItem = itemList.get(index); 
        else 
            selectedItem = null; 
        
        onselectedItemChanged(selectedItem); 
        fireSelectedItemChanged();  
    } 
    
    protected Object onOpenItem(Object item, String columnName) { return null; }
    
    public final Object openSelectedItem() 
    { 
        if (selectedItem == null || selectedItem.getItem() == null) return null;
        
        return onOpenItem(selectedItem.getItem(), selectedColumn); 
    } 
    
    public final void removeSelectedItem() 
    {
        if (selectedItem == null) return;        
        if (selectedItem.getState() == ListItem.STATE_EMPTY) return;
        
        int index = selectedItem.getIndex();
        messageSupport.removeErrorMessage(index);        
        
        if (selectedItem.getState() == ListItem.STATE_DRAFT) 
        {
            selectedItem.loadItem(null, ListItem.STATE_EMPTY); 
            tableModelSupport.fireTableRowsUpdated(index, index); 
        }
        else 
        {
            ListItem firstLI = getListItem(0); 
            int rownum = selectedItem.getRownum(); 
            dataList.remove(rownum); 
            itemList.remove(index); 
            
            rownum = (firstLI == null? 0: firstLI.getRownum()); 
            fillListItems(dataList, rownum); 
            
            selectedItem = null;
            tableModelSupport.fireTableDataChanged(); 
            tableModelSupport.fireTableRowSelected(index, true); 
        } 
    } 
    
    public final void removeListItem(int index) 
    {
        if (index >= 0 && index < itemList.size()) 
        {
            itemList.remove(index); 
            rebuildIndexes();
            tableModelSupport.fireTableRowsDeleted(index, index); 
        }
    }
    
    public final String getSelectedColumn() { return selectedColumn; }    
    public final void setSelectedColumn(String selectedColumn) {
        this.selectedColumn = selectedColumn;
    }    
    
    
    
    
    public void load() 
    {
        checkedItems.clear();        
        fetchMode = FETCH_MODE_LOAD; 
        fetchImpl();
    }
    
    public void refresh() { 
        refresh(false); 
    } 

    public void refresh(boolean forceLoad) 
    {
        fetchMode = (forceLoad? FETCH_MODE_RELOAD: FETCH_MODE_REFRESH); 
        fetchImpl(); 
    } 
    
    public final int getDataIndexByRownum(int rownum) 
    {
        ListItem li = getListItemByRownum(rownum); 
        if (li == null || li.getItem() == null) return -1;
        
        return dataList.indexOf(li.getItem()); 
    }
    
    public final List getDataList() { return dataList; }
    public final Object getData(int index) 
    {
        if (dataList != null && index >= 0 && index < dataList.size()) 
            return dataList.get(index); 
        else 
            return null; 
    } 
    
    protected final void setDataList(List dataList) {
        this.dataList = dataList; 
    }
    
    protected final void initDataListWhenNull() 
    {
        if (dataList == null) dataList = new ArrayList(); 
    }
    
    public void moveBackRecord()
    {
        //do not scroll when there are error in validation
        if (messageSupport.hasErrorMessages()) return;
        if (selectedItem == null) return; 
        
        int idx = itemList.indexOf(selectedItem);
        if (idx-1 >= 0) 
        {
            setSelectedItem(idx-1);
            refreshSelectedItem();                 
        }         
    }

    public void moveNextRecord() { 
        moveNextRecord(false);  
    } 
    
    public void moveNextRecord(boolean includesEmptyItem) 
    {
        //do not scroll when there are error in validation
        if (messageSupport.hasErrorMessages()) return;
        if (selectedItem == null) return; 
        
        int idx = itemList.indexOf(selectedItem); 
        if (idx >= 0 && idx+1 < itemList.size()) 
        {
            if (getListItemData(idx+1) != null || includesEmptyItem)
            {
                setSelectedItem(idx+1);
                refreshSelectedItem(); 
            }
        }        
    }
    
    
    protected void finalize() throws Throwable 
    {
        propertySupport.removeAll();
        tableModelSupport.removeAll();
        checkedItems.clear(); 
        itemList.clear(); 
        dataList = null; 
        onfinalize();
    }
    
    protected void onfinalize() throws Throwable {}
    
    public MessageSupport getMessageSupport() { return messageSupport; } 
    
    protected void onreplaceSelectedItem(Object oldItem, Object newItem) {}
    
    
    /*
     *  notify events
     */
    public void fireSelectedItemChanged() { 
        propertySupport.firePropertyChange("selectedItemChanged", selectedItem); 
    }
    
    public void fireFocusSelectedItem() {
        propertySupport.firePropertyChange("focusSelectedItem", selectedItem); 
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" ListItem helper methods "> 
    
    public ListItemStatus createListItemStatus(ListItem oListItem) 
    { 
        if (oListItem == null) return null;
        
        ListItemStatus stat = new ListItemStatus(oListItem); 
        stat.setPageIndex(1);
        stat.setPageCount(1);
        stat.setIsLastPage(true);
        
        if (dataList != null) 
            stat.setRecordCount(dataList.size());
        
        return stat; 
    } 
    
    protected ListItem createCustomListItem() {
        return new ListItem();
    }
    
    public final ListItem createListItem() 
    { 
        ListItem li = createCustomListItem();
        li.addHandler(listItemHandler);
        return li;
    } 
    
    protected ListItem getFirstItem() 
    {
        try { 
            return itemList.get(0); 
        } catch(Exception ex) {
            return null; 
        }        
    }
    
    public boolean isFirstItem(ListItem li) 
    {
        if (li == null) return false; 
        
        return isFirstItem(li.getIndex()); 
    }
    
    public boolean isFirstItem(int index) { 
        return (index == 0); 
    } 
    
    protected ListItem getLastItem() 
    {
        try { 
            return itemList.get(itemList.size()-1); 
        } catch(Exception ex) {
            return null; 
        }
    }
    
    public boolean isLastItem(ListItem li) 
    {
        if (li == null) return false; 
        
        return isLastItem(li.getIndex()); 
    }
    
    public boolean isLastItem(int index) { 
        return (index >= 0 && index==itemList.size()-1); 
    } 
    
    public final void addEmptyItem() 
    {
        ListItem lastLI = getLastItem();
        int index = (lastLI == null? 0: lastLI.getIndex()+1);
        int rownum = (lastLI == null? 0: lastLI.getRownum()+1);
        ListItem li = createListItem(); 
        li.setIndex(index);
        li.setRownum(rownum);
        li.loadItem(null, ListItem.STATE_EMPTY); 
        itemList.add(li); 
        tableModelSupport.fireTableRowsInserted(index, index); 
    }
    
    protected final void buildListItems() 
    {
        boolean dynamic = (getRows() == -1);
        if (!dynamic && itemList.size() == getRows()) return;
        
        itemList.clear(); 
        int rowSize = getRows(); 
        if (dynamic) rowSize = getRowCount()+1;
        
        rowSize = Math.max(rowSize, 1); 
        for (int i=0; i<rowSize; i++) 
        {
            ListItem li = createListItem(); 
            li.setIndex(i); 
            li.setRownum(i);
            li.loadItem(null);             
            itemList.add(li);
        }
    }
    
    protected final void fillListItems(List list, int toprow) 
    {
        buildListItems();
        int dataSize = list.size();        
        int itemSize = itemList.size();
        for (int i=0; i<itemSize; i++) 
        {
            ListItem li = itemList.get(i);
            li.setIndex(i);
            li.setRownum(toprow+i);
            if ( i < dataSize ) 
            {
                Object item = list.get(i);
                li.loadItem( item );
                li.setState(ListItem.STATE_SYNC);
                li.setSelected(isSelected(item));
            } 
            else 
            {
                li.loadItem(null);
                li.setState(ListItem.STATE_EMPTY);
                li.setSelected(false);
            }
        }
    } 
    
    public final void rebuildIndexes() 
    {
        for (int i=0; i<itemList.size(); i++) {
            itemList.get(i).setIndex(i); 
        }
    } 
    
    public boolean isSelected(Object item) {
        return checkedItems.contains(item);
    }    
    
    public Set getCheckedItems() { return checkedItems; }    
    public void checkItem( Object item, boolean checked ) 
    {   
        if (item == null) return;
        
        if (checked)
            checkedItems.add(item);
        else 
            checkedItems.remove(item);
    }    
    
    protected void refreshSelectedItem() { 
        fireFocusSelectedItem(); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MessageSupport (Class) ">
    
    public class MessageSupport
    {
        private Map<ListItem, String> errors = new HashMap();  
        
        public boolean hasErrorMessages() {
            return !errors.isEmpty();
        }        
        
        public void addErrorMessage(int index, String message) 
        {
            ListItem li = getListItem(index);
            if (li == null || li.getItem() == null) return;

            errors.put(li.clone(), message);
        }

        public String getErrorMessage(int index) 
        {
            ListItem li = getListItem(index);
            if (li == null || li.getItem() == null) return null;

            return errors.get( li );
        }

        public void removeErrorMessage(int index) 
        {
            ListItem li = getListItem(index);
            if (li == null || li.getItem() == null) return;

            errors.remove( li );
        }

        public String getErrorMessages() 
        {
            if ( errors.isEmpty() ) return null;

            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Map.Entry<ListItem, String> me: errors.entrySet()) 
            {
                if ( !first ) sb.append("\n");
                else first = false; 
                
                sb.append("Row " + (me.getKey().getRownum()+1) + ": " + me.getValue());
            }
            return sb.toString();
        }         
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" ListItemHandler (Class) ">    
    
    private class ListItemHandler implements ListItem.Handler
    {
        public void setSelected(ListItem li, boolean selected) {
        }

        public void replaceSelectedItem(ListItem li, Object newData) {
        }

        public void refreshItemUpdated(ListItem li) {
        }        
    }
    
    // </editor-fold>     
}
