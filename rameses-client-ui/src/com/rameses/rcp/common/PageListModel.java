package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PageListModel extends AbstractListDataProvider implements ListPageModel 
{    
    //stores the fetched result list size 
    private int fetchedRows;
    private int preferredRows;
    private int totalMaxRows;
    private int totalRowCount;
    private int pageIndex = 1;
    private int pageCount = 1;    
    private String searchtext;
    
    private Map query = new HashMap(); 
    
    public ListItemStatus createListItemStatus(ListItem oListItem) 
    {
        ListItemStatus stat = super.createListItemStatus(oListItem); 
        stat.setPageIndex(pageIndex); 
        stat.setPageCount(pageCount);
        stat.setTotalRows(totalRowCount); 
        stat.setIsLastPage(isLastPage()); 
        stat.setHasNextPage(fetchedRows > preferredRows); 
        return stat;
    }      
    
    protected void beforeLoad() {
        fetchedRows = 0;
        preferredRows = 0;
        totalMaxRows = 0;
        totalRowCount = 0;
        pageIndex = 1;
        pageCount = 1;
    }
    
    public String getSearchtext() {  return searchtext; }     
    public void setSearchtext(String searchtext) 
    { 
        this.searchtext = searchtext; 
        getQuery().put("searchtext", searchtext); 
    }

    public int getRows() { return 10; }
    public int getRowCount() { return getRows(); }
    public int getMaxRows() { return getRows(); }    
    public Map getQuery() { return query; }     
        
    protected void onbeforeFetchList(Map params) 
    {
        Map qry = getQuery();
        if (qry != null) params.putAll(qry); 
    }
        
    protected void fetch(boolean forceLoad) 
    { 
        preferredRows = getRows();         
        List dataList = getDataList();
        boolean fetchNewRecords = (dataList == null || forceLoad);
        if (fetchNewRecords) 
        {
            int _startRow = (pageIndex * preferredRows) - preferredRows;
            if (_startRow < 0) _startRow = 0;
            
            Map params = new HashMap(); 
            onbeforeFetchList(params);
                        
            params.put("_start", _startRow);
            params.put("_toprow", _startRow);
            //add extra row to see if this is last page.
            params.put("_rowsize", preferredRows+1);
            params.put("_limit",   preferredRows+1);
            
            dataList = fetchList(params); 
            if (dataList == null) dataList = new ArrayList();
            
            onafterFetchList(dataList); 
            fetchedRows = dataList.size();
            if (dataList.size() > preferredRows)
                dataList.remove(dataList.size()-1); 
            
            setDataList(dataList); 
        }

        if (pageIndex >= pageCount) {
            int nrows = Math.max(pageCount-1, 0) * preferredRows;
            totalRowCount = nrows + dataList.size(); 
        }
        
        totalMaxRows = Math.max(totalMaxRows, (pageIndex*getRows())+Math.min(fetchedRows, preferredRows));
        
        fillListItems(dataList, 0);         
        setSelectedItem((getSelectedItem() == null? 0: getSelectedItem().getIndex())); 
        if (fetchNewRecords && !dataList.isEmpty() && getSelectedItem() != null) 
        {
            if (getSelectedItem().getItem() == null) setSelectedItem(0);
        }
    }
        
    public void moveFirstPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        pageIndex = 1;
        refresh(true);
        refreshSelectedItem();
    } 
        
    public void moveBackPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        if (pageIndex-1 > 0) 
        {
            pageIndex -= 1;
            refresh(true);
            refreshSelectedItem();
        }
    }
    
    public void moveNextPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        if (!isLastPage()) 
        {
            pageIndex += 1;
            pageCount = Math.max(pageIndex, pageCount); 
            refresh(true);
            refreshSelectedItem();
        }
    }  
    
    public void moveLastPage() {
        //no implementation yet
    }
                
    public final void doSearch() {
        load();
    }
        
    public int getPageIndex() { return pageIndex; }
    
    public int getPageCount() { return pageCount; }
    
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
