package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicListModel extends AbstractListModel 
{    
    //stores the fetched result list size 
    private int fetchedRows;
    
    protected int pageIndex = 1;
    protected int pageCount = 1;
    protected String searchtext;

    private Map query = new HashMap();
    
    /**
     * forceLoad is used to force the loading without emptying the dataList
     */
    protected boolean forceLoad;
    
    public BasicListModel() {}
    
    public void load() 
    {
        pageIndex = 1;
        pageCount = 1;
        super.load();
    }

    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
    
    public Map getQuery() { return query; } 
    
    public String getSearchtext() {  return searchtext; }     
    public void setSearchtext(String searchtext) { 
        this.searchtext = searchtext; 
        
        Map m = getQuery();
        if (m != null) m.put("searchtext", searchtext); 
    }

    public int getTopRow() { return 0; }    
    public void setTopRow(int row) {}

    public int getMaxRows() { return -1; }
    
    public int getRows() { 
        return fetchedRows+1;
    }
    
    public int getRowCount() {  
        return getRows(); 
    }
    
    // </editor-fold>
        
    protected void fetch() 
    {
        if (dataList == null || forceLoad)  
        {
            Map qry = getQuery(); 
            if (qry == null) qry = new HashMap();
            if (qry.get("searchtext") == null)
                qry.put("searchtext", getSearchtext()); 

            List resultlist = fetchList(qry); 
            if (resultlist == null) resultlist = new ArrayList();             
            if (dataList != null) dataList.clear(); 
            
            dataList = resultlist;
        }

        fetchedRows = (dataList == null? 0: dataList.size());         
        fillListItems(dataList, 0); 
        if (dataList != null && dataList.size() > 0) 
        {
            if (selectedItem == null) 
                setSelectedItem(dataList.get(0)); 
        } 
        else {
            setSelectedItem(null);
        } 
        forceLoad = false;
    }
    
    public void addItem(Object item) 
    {
        if ( item instanceof ListItem )
            throw new IllegalStateException("Unable to add item. The item object passed must not be an instanceof ListItem ");
        
        onAddItem(item);
        forceLoad = false;
        refresh();
    }
    
    public void removeItem(Object item) 
    {
        if ( item instanceof ListItem )
            throw new IllegalStateException("Unable to remove item. The item object passed must not be an instanceof ListItem");
        
        onRemoveItem( item );
        forceLoad = true;
        refresh(); 
        
        if (getSelectedItem().getIndex()>0 && getSelectedItem().getState()==0) {
            setSelectedItem( items.get(getSelectedItem().getIndex()-1) );
        }
    }
        
    public void reload() { 
        reload(true); 
    } 
    
    protected void reload(boolean forceLoad)
    {
        //do not scroll when there are error in validation
        if ( super.hasErrorMessages() ) return;
        
        this.forceLoad = forceLoad;
        refresh();
        refreshSelectedItem();        
    }
    
    public void moveFirstPage() {}    
    public void moveBackPage() {}     
    public void moveNextPage() {}  
    public void moveLastPage() {}
    
    public void moveNextRecord() 
    {
        //do not scroll when there are error in validation
        if ( super.hasErrorMessages() ) return;
    
        if (getSelectedItem() == null) return; 
        if (getSelectedItem().getItem() == null) return;
        
        int idx = items.indexOf(getSelectedItem()); 
        if (idx >= 0 && idx+1 < items.size()) 
        {
            if (items.get(idx+1) != null && items.get(idx+1).getItem() != null)
                setSelectedItem(idx+1);
        } 
            
    }
    
    public void moveBackRecord() 
    {
        //do not scroll when there are error in validation
        if ( super.hasErrorMessages() ) return;        
        if (getSelectedItem() == null) return; 
        if (getSelectedItem().getItem() == null) return;
        
        int idx = items.indexOf(getSelectedItem()); 
        if (idx-1 >= 0) 
        {
            if (items.get(idx-1) != null && items.get(idx-1).getItem() != null)
                setSelectedItem(idx-1);
        } 
    }    
            
    public final void doSearch() {
        load();
    }
    
    public int getPageIndex() { return pageIndex; }    
    public int getPageCount() { return pageCount; }    
    public boolean isLastPage() { return true; }   

    public boolean removeListItemAt(int rowIndex) 
    {
        ListItem liA = getListItem(rowIndex); 
        if (liA == null) return false;
        
        ListItem liB = getListItem(rowIndex+1); 
        if (liB == null) return false; 
        
        items.remove(liA); 
        //if (tableModel != null) 
            //tableModel.fireTableRowsDeleted(rowIndex, rowIndex);
        
        return true; 
    }
    
}
