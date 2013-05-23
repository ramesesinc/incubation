package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class PageListModel extends AbstractListModel 
{    
    //stores the fetched result list size 
    private int fetchedRows;
    private int preferredRows;
    
    protected int pageIndex = 1;
    protected int pageCount = 1;    
    protected String searchtext;
    
    private Map query = new HashMap(); 

    
    /**
     * forceLoad is used to force the loading without emptying the dataList
     */
    protected boolean forceLoad;
    
    public PageListModel() {}
    
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
        
        Map q = getQuery();
        if (q != null) q.put("searchtext", searchtext); 
    }

    public int getTopRow() { return 0; }    
    public void setTopRow(int row) {}

    public int getMaxRows() { return -1; }
    
    public int getRowCount() {  
        return pageIndex * getRows(); 
    }
    
    // </editor-fold>
        
    protected void fetch() 
    {
        preferredRows = getRows(); 
        if (dataList == null || forceLoad)  
        {
            int _startRow = (pageIndex * preferredRows) - preferredRows;
            if (_startRow < 0) _startRow = 0;
            
            Map m = getQuery(); 
            if (m == null) m = new HashMap();
            if (m.get("searchtext") == null) 
                m.put("searchtext", getSearchtext()); 
            
            m.put("_start", _startRow);
            m.put("_toprow", _startRow);
            
            //add extra row to see if this is last page.
            m.put("_rowsize", preferredRows+1);
            m.put("_limit",   preferredRows+1);
            
            dataList = fetchList(m); 
            if (dataList == null) dataList = new ArrayList();
            
            fetchedRows = dataList.size();
        }

        fillListItems(dataList, 0); 
        
        if (dataList != null && dataList.size() > 0) 
        {
            if (dataList.size() > preferredRows) 
                dataList.remove(dataList.size()-1);
                        
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
    
    
    public void removeItem(Object item) {
        if( item instanceof ListItem )
            throw new IllegalStateException("SubListModel.removeItem error. Item passed must not be a ListItem");
        
        onRemoveItem( item );
        forceLoad = true;
        refresh();
        if(getSelectedItem().getIndex()>0 && getSelectedItem().getState()==0) {
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
    
    public void moveFirstPage() 
    {
        //do not scroll when there are error in validation
        if ( super.hasErrorMessages() ) return;
        
        forceLoad = true;
        pageIndex = 1;
        refresh();
        refreshSelectedItem();
    }    
        
    public void moveBackPage() 
    {
        //do not scroll when there are error in validation
        if ( super.hasErrorMessages() ) return;
        
        if ( pageIndex-1 > 0 ) 
        {
            forceLoad = true;
            pageIndex -= 1;
            refresh();
            refreshSelectedItem();
        }
    }
    
    public void moveNextPage() {
        //do not scroll when there are error in validation
        if ( super.hasErrorMessages() ) return;
        
        if (!isLastPage()) 
        {
            forceLoad = true;
            pageIndex += 1;
            pageCount = Math.max(pageIndex, pageCount); 
            refresh();
            refreshSelectedItem();
        }
    }  
    
    public void moveLastPage() {
        //no implementation yet
    }
    
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
    
    public int getPageIndex() {
        return pageIndex;
    }
    
    public int getPageCount() {
        return pageCount;
    }
    
    public boolean isLastPage() 
    {
        if (pageIndex < pageCount) 
            return false; 
        else if (fetchedRows <= preferredRows)
            return true; 
        else if (pageIndex > pageCount) 
            return true; 
        else 
            return false;
    }   

}
