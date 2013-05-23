/*
 * AbstractListModel.java
 *
 * Created on January 14, 2010, 8:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author elmo
 */
public abstract class AbstractListModel implements Runnable 
{    
    protected ListItem selectedItem;
    protected String selectedColumn;
    protected ListModelListener listener;
    protected ListModelSupport listModelSupport = new ListModelSupport();     

    //This is the real list needed by the grid. Not overridable
    protected List<ListItem> items = new ArrayList<ListItem>();    
    protected List dataList = new ArrayList();
    
    //used to asynchronously fetch items
    private ExecutorService fetchExecSvc;
    //processing flag
    private boolean processing;
    
    private int rows = 10;        
    private Column primaryColumn;
    private boolean primaryColChecked;
    
    //used by XTable to register error messages
    private Map<ListItem, String> errorMessages = new HashMap();
    
    /**
     * this contains items that have been selected
     */
    protected Set checkedItems = new HashSet();
    
    
    /**
     * this method is called only by the direct subclasses
     * like ListModel, PageListModel.
     */
    protected abstract void fetch();
    
    
    /**
     * This method is only called once. when initiating.
     * most do not have an implementation. Most notably used
     * by AsyncListModel.
     */
    public void init() {        
    }    
    
    /**
     * This method is called to rebuild the
     * entire list and resets everything to 0.
     */
    public void load() 
    {
        dataList = null;
        selectedItem = null;
        refresh();
    }
    
    /**
     * This method is contrary to the load method, does not reset the list
     */
    public void refresh() 
    {        
        //refresh(false); 
        if ( fetchExecSvc == null ) 
        {
            fetchExecSvc = Executors.newFixedThreadPool(1);
            ClientContext.getCurrentContext().registerExecutor(fetchExecSvc);
            fillListItems(new ArrayList(), getRows());
        } 
        fetchExecSvc.submit(this);
    }
    
    public void refresh(boolean fetchNew) 
    {
        
    }
    
    
    
    
    /**
     * Implementation of Runnable's run method
     * This method do the actual fetching
     */
    public void run() 
    {
        try 
        {
            processing = true;
            if ( listener != null ) listener.fetchStart(); 
            
            onFetchStart();        
            fetch();
        }
        catch (RuntimeException re) {
            throw re; 
        } 
        catch (Exception e) { 
            throw new RuntimeException(e.getMessage(), e);
        } 
        finally 
        {
            try 
            {
                if (listener != null) 
                {
                    listener.fetchEnd();
                    listener.refreshList();
                    listener.refreshSelectedItem();
                }
            }
            catch (RuntimeException re) {
                throw re; 
            }
            catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } 
            finally 
            {
                processing = false;
                onFetchEnd();
            }
        }
    }
    
    public boolean isProcessing() {
        return processing;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters "> 
        
    public final ListItem getListItem(int index) 
    {
        if (index >= 0 && index < items.size()) 
            return items.get(index); 
        else
            return null; 
    }
    
    public final Object getData(int index) 
    {
        List list = getDataList(); 
        if (list == null) 
            return null;
        else if (index >= 0 && index < list.size()) 
            return list.get(index); 
        else
            return null; 
    }
    
    public Column[] getColumns() { return columns; }    
    public void setColumns(Column[] columns) {
        this.columns = columns;
    }
    
    public final String getSelectedColumn() { return selectedColumn; }    
    public final void setSelectedColumn(String selectedColumn) {
        this.selectedColumn = selectedColumn;
    }
    
    public ListModelListener getListener() { return listener; }    
    public void setListener(ListModelListener listener) {
        this.listener = listener;
    }    
    
    public int getRows() { return rows; }    
    public void setRows(int rows) {
        this.rows = rows;
    }    
    
    public final ListItem getSelectedItem() { return this.selectedItem; }    
    public final void setSelectedItem(ListItem o) 
    {
        this.selectedItem = o;
        refreshSelectedItem();
    }
    
    public final void refreshSelectedItem() {
        if(listener!=null) {
            listener.refreshSelectedItem();
        }
    }
    
    public final void setSelectedItem(int i) 
    {
        if ( items.size() > 0 && i>=0 ) 
        {
            if ( i > items.size()-1 ) 
            {
                setTopRow( i - items.size() - 1);
                setSelectedItem( items.get(items.size() - 1) );
            } 
            else {
                setSelectedItem( items.get(i) );
            }
        }
    }
    
    public final void setSelectedItem(Object obj) {
        if( obj == null ) return;
        
        if( obj instanceof Number ) {
            setSelectedItem( new Integer( obj+"" ) );
        } else if ( obj instanceof ListItem ) {
            setSelectedItem( (ListItem) obj );
        } else {
            Column col = getPrimaryColumn();
            String id = (col != null)? col.getName() : null;
            PropertyResolver resolver = PropertyResolver.getInstance();
            
            int idx = -1;
            for(Object o : getDataList()) {
                idx++;
                if( !ValueUtil.isEqual(o, obj, id, resolver) ) continue;
                
                setSelectedItem( idx );
                break;
            }
        }
    }
    
    // </editor-fold>
    
    
    //-- free resources when garbage collected
    public void finalize() 
    {
        boolean debugMode = ClientContext.getCurrentContext().isDebugMode();
        if ( debugMode ) System.out.println("Finalizing abstract list model " + this);
        
        try 
        {
            ClientContext.getCurrentContext().unregisterExecutor(fetchExecSvc);
            if ( fetchExecSvc != null ) fetchExecSvc.shutdownNow();
        }
        catch(Exception e) 
        {
            if ( debugMode ) e.printStackTrace();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Called by the DataTableModel "> 
 
    public final void commit(ListItem item) 
    {
        if (!(item.getState() == ListItem.STATE_DRAFT)) 
            throw new IllegalStateException("Unable to commit item. The ListItem state must be STATE_DRAFT");
        
        if (dataList == null) 
            throw new NullPointerException("dataList is not initialized"); 

        Object o = item.getItem(); 
        validate(item); 
        onAddItem(o); 

        int index = item.getIndex();        
        int size = dataList.size();
        if (size == index) { 
            dataList.add(o); 
        } 
        else if (index >= 0 && index < size) {
            dataList.set(index, o); 
        } 
        else { 
            return; 
        } 
        
        item.setState(ListItem.STATE_SYNC);         
    } 
    
    // </editor-fold> 
    
    /**
     * Override this if you want a more customized ListItem.
     * One may need to customize if it contains ListItem additional
     * codes.
     */
    public ListItem createListItem() {
        return new ListItem();
    }
    
    public ListItem createListItemAt(int rowIndex) 
    {
        ListItem li = getListItem(rowIndex);
        if (li != null) return li;
        
        li = createListItem();
        li.setParent(this);
        li.setIndex(rowIndex); 
        li.setRownum(rowIndex);
        li.loadItem(null);
        items.add(li);
        
        if (listModelSupport.getTableModel() != null) 
            listModelSupport.getTableModel().fireTableRowsInserted(rowIndex, rowIndex);
        
        return li; 
    }
    
    public ListItem replaceListItem(ListItem item) 
    {
        int rowIndex = item.getIndex(); 
        ListItem newItem = createListItem();
        newItem.setParent(this);
        newItem.setIndex(rowIndex); 
        newItem.setRownum(rowIndex);
        newItem.loadItem(null);
        items.set(rowIndex, newItem);
        return newItem; 
    }
    
    public boolean removeListItemAt(int rowIndex) {
        return false; 
    }
    
    
    
    /**
     * This method is called to build the ListItem objects based on
     * the getRows(). If the items size is already the same as getRows(),
     * there will be no need to rebuild the items.
     */
    public final void buildListItems() 
    {
        if (items.size() == getRows()) return;
        
        items.clear();
        int _rows = getRows();
        for (int i=0; i < _rows; i++) 
        {
            ListItem gi = createListItem();
            gi.loadItem(null);
            gi.setParent(this);
            gi.setIndex(i);
            gi.setRownum(i);
            items.add(gi);
        }
    }
    
    /**
     * This method is called by the class implementing the fetch method
     */
    protected final void fillListItems(List list, int toprow) 
    {
        buildListItems();
        int sz = list.size();
        for (int i=0; i< getRows();i++) 
        {
            ListItem li = items.get(i);
            li.setIndex(i);
            li.setRownum(toprow+i);
            if ( i < sz ) 
            {
                Object item = list.get(i);
                li.loadItem( item );
                li.setState(ListItem.STATE_SYNC);
                li.setSelected(checkSelected(item));
            } 
            else 
            {
                li.loadItem(null);
                li.setState(ListItem.STATE_EMPTY);
                li.setSelected(false);
            }
        }
    }
    
    
    /**
     * default columns will display item's toString() with a blank header
     */
    private Column[] columns = new Column[] {
        new Column("item", null)
    };
    
    
    /**
     * this method is called when the selectedItem has a state of 0 which means new
     * and the item associated with it is not null. Take note, item becomes null if
     * createItem method is not implemented.
     */
    public final Object addCreatedItem() 
    {
        if (selectedItem == null)  return null;
        
        if (selectedItem.getState() == ListItem.STATE_EMPTY) 
        {
            Object item = selectedItem.getItem();
            if (item == null)
                throw new IllegalStateException("addCreateItem error. Selected new object is null. createItem must be implemented");
            
            addItem( item );
        }
        return null;
    }
    
    /**
     * This method is called as a result of changing the underlying object of the ListItem.
     * This is package level and should not be called except by the ListItem.
     */
    final void replaceSelectedItem(int index, Object oldItem, Object newItem) 
    {
        onReplaceItem( oldItem, newItem ); 
        getDataList().set(index, newItem); 
        if (getListener() != null) getListener().refreshItemUpdated(index); 
    }
    
    /**
     * this method is called when double clicking an item in a grid or force called by the
     * developer. When invoked, it calls the onOpenItem method which needs to be implemented
     * by the developer.
     */
    public final Object openSelectedItem() 
    {
        if(selectedItem.getItem()==null) return null;
        return onOpenItem( selectedItem.getItem(), selectedColumn );
    }
    
    /**
     * this method is called when there are changes in the row made
     *
     */
    public final void updateSelectedItem() {
        if(selectedItem.getItem()!=null) {
            onUpdateItem( selectedItem.getItem() );
            onColumnUpdate( selectedItem.getItem(), selectedColumn );
        }
    }
    
    /**
     * this method removes the item based on the selected item.
     * It can sucessfully call remove if the selected ListItem has a state==1.
     * which means it's already exists. If the method is successfully called
     * it triggers the onRemoveItem method which must be implemented by the developer.
     */
    public final void removeSelectedItem() 
    {
        ListItem item = getSelectedItem();
        if ( item == null || item.getItem() == null ) {
            //do nothing
        } 
        else if ( item.getState() != ListItem.STATE_SYNC ) 
        {
            item.setItem( createItem() );
            errorMessages.remove(item);
            refreshSelectedItem();
        } 
        else if ( item.getState() == ListItem.STATE_SYNC ) 
        {
            errorMessages.remove(item);
            removeItem( item.getItem() );
        }
    }
    /**
     * this is called to get the list items
     */
    public final List<ListItem> getItemList() {
        return items;
    }
    
    protected void clearList() {
        items.clear();
    }
    
    //call back methods need to be supplied by the client.
    public Object onOpenItem( Object o, String columnName ) {
        //do nothing
        return null;
    }
    
    public void onAddItem(Object o) {
        //do nothing
    }
    
    /**
     * this method had been deprecated in favor of #onColumnUpdate
     */
    @Deprecated
    public void onUpdateItem(Object o) {
        //do nothing.
    }
    
    public void onColumnUpdate(Object o, String colName) {
        //do nothing.
    }
    
    public void onRemoveItem(Object o) {
        //throw new IllegalStateException("Error remove item. onRemoveItem(Object item) must be implemented.");
    }
    
    public void onReplaceItem( Object oldValue, Object o ) {
        //throw new IllegalStateException("Error ListItem.setItem. onReplaceItem(Object oldItem,Object newItem) must be implemented.");
    }
    
    /**
     * called on before the fetch is executed
     */
    public void onFetchStart() {}
    
    /**
     * called on after the fetch is executed
     */
    public void onFetchEnd() {}
    
    /**
     * this must be implemented by the developers.
     * The Map parameter contains the parameter values like
     * start and rowsize. Parameter values coming from the model
     * are identified by _ (underscored) prefix. This is to
     * differentiate from other parameters.
     */
    public abstract List fetchList(Map o);
    
    /**
     * This method must be overridden if the developer wants allows
     * the control to allow adding on new items.
     */
    public Object createItem() {
        return new HashMap();
    }
    
    /**
     *    this is used when you want explicitly set the selected items
     */
    public boolean checkSelected(Object obj) {
        return checkedItems.contains(obj);
    }
    
    public abstract void addItem( Object item );
    public abstract void removeItem( Object item );
    
    
    //key related functions: up and down beyond the list.
    //These are overridable methods
    public abstract void moveNextRecord();
    public abstract void moveBackRecord();
    public abstract void moveFirstPage();
    public abstract void moveNextPage();
    public abstract void moveBackPage();
    public abstract void setTopRow(int row);
    public abstract int getTopRow();
    public abstract int getMaxRows();
    
    /**
     * The implementing model determines how to calculate this value.
     * -1 means row count is not determined.
     */
    public abstract int getRowCount();
    
    
    //overridable. throw exception if there is validation error
    public void validate(ListItem item) {
        
    }
    
    public List getDataList() {
        return dataList;
    }
    
    
    /**
     * this is called during unbinding
     */
    public void destroy() {
        listener = null;
    }
    
    public void checkItem( Object item, boolean checked ) {
        if( checked ) {
            checkedItems.add( item );
        } else {
            checkedItems.remove(item);
        }
    }
    
    public StyleRule[] getStyleRules() {
        return null;
    }
    
    public Set getCheckedItems() {
        return checkedItems;
    }
    
    /**
     * This is called when the list of columns changed.
     * This method will notify the listener to re-index the list of columns.
     */
    public void rebuildColumns() {
        primaryColumn = null;
        primaryColChecked = false;
        if ( listener != null ) listener.rebuildColumns();
    }
    
    /**
     * these methods below are used by the XTable to register/retrieve/remove error messages
     */
    public void addErrorMessage(int liIndex, String message) {
        ListItem li = getItemList().get(liIndex);
        if ( li.getItem() == null ) return;
        
        errorMessages.put(li.clone(), message);
    }
    
    public String getErrorMessage(int liIndex) {
        ListItem li = getItemList().get(liIndex);
        if ( li.getItem() == null ) return null;
        
        return errorMessages.get( li );
    }
    
    public void removeErrorMessage(int liIndex) {
        ListItem li = getItemList().get(liIndex);
        if ( li.getItem() == null ) return;
        
        errorMessages.remove( li );
    }
    
    public String getErrorMessages() {
        if ( errorMessages.size() == 0 ) return null;
        
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Map.Entry<ListItem, String> me: errorMessages.entrySet()) {
            if ( !first ) sb.append("\n");
            else first = false;
            sb.append("Row " + (me.getKey().getRownum()+1) + ": " + me.getValue());
        }
        
        return sb.toString();
    }
    
    public boolean hasErrorMessages() {
        return !errorMessages.isEmpty();
    }
    
    public Column getPrimaryColumn() {
        if ( primaryColumn == null && !primaryColChecked ) {
            primaryColChecked = true;
            Column[] cols = getColumns();
            if ( cols != null && cols.length > 0 ) {
                //primaryColumn = cols[0];
                for ( Column c : cols ) {
                    if ( c.isPrimary() ) {
                        primaryColumn = c;
                        break;
                    }
                }
            }
        }
        return primaryColumn;
    }
 
    
    public static enum FetchMode
    {
        START(0, "Fetch from the start"),
        REFRESH(1, "Refresh the current model from cache"),
        RELOAD(2, "Reload the current model");
        
        private int value;
        private String description;
        
        FetchMode(int value, String description) {
            this.value = value;
            this.description = description; 
        }
        
        public int getValue() { return value; }
        public String getDescription() { return description; } 
        
        public String toString() {
            return "FetchMode [value="+value+", description="+description+"]";
        } 
    }
    
    public static class Support 
    {
        public static synchronized ListModelSupport link(AbstractTableModel tableModel, AbstractListModel listModel) { 
            ListModelSupport listModelSupport = new ListModelSupport(listModel, tableModel);
            listModel.listModelSupport = listModelSupport;
            return listModelSupport; 
        } 
    }    
}
