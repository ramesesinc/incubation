package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ScrollListModel extends AbstractListDataProvider implements ListPageModel 
{
    public final static int PAGE_FIRST         = 0;
    public final static int PAGE_BACK          = 1;
    public final static int PAGE_NEXT          = 2;
    public final static int PAGE_LAST          = 3;
    public final static int PAGE_BACK_RECORD   = 4;
    public final static int PAGE_NEXT_RECORD   = 5;    
    
    protected int pageIndex = 1;
    protected int pageCount = 1;
    
    //this indicates the absolute row pos;
    protected int toprow;
    protected int maxRows = -1;
    
    //-1 means it has not been initialized yet
    protected int minlimit = 0;
    protected int maxlimit = 0;
    protected String searchtext;
    
    private Map query = new HashMap(); 
    private boolean hasMoreRecords;
    private int preferredRows;
    private int fetchedRows;
    private int pageMode = PAGE_FIRST;
    
    public Map getQuery() { return query; } 
    
    public String getSearchtext() {  return searchtext; }     
    public void setSearchtext(String searchtext) 
    { 
        this.searchtext = searchtext; 
        getQuery().put("searchtext", searchtext); 
    }    
    
    public Object createItem() { 
        return new HashMap(); 
    }     
    
    public void load() 
    {
        toprow = 0;
        minlimit = 0;
        maxlimit = 0;
        maxRows = -1;
        pageIndex = 1;
        pageCount = 1;
        super.load();
    }
    
    public int getRows() { return 10; }    
    
    protected void onbeforeFetchList(Map params) 
    {
        Map qry = getQuery();
        if (qry != null) params.putAll(qry); 
    }
        
    protected void fetch(boolean forceLoad) 
    {
        ListItem selItem = getSelectedItem();
        int selIndex = (selItem == null? 0: selItem.getIndex());
        
        if (getDataList() == null || forceLoad) 
        {
            preferredRows = getRows() * 3;            
            minlimit = toprow - getRows();
            if (minlimit < 0) minlimit = 0;
            
            Map params = new HashMap();
            onbeforeFetchList(params);
            
            params.put("_toprow", toprow);
            params.put("_start", minlimit);
            params.put("_rowsize", preferredRows+1);
            params.put("_limit", preferredRows+1);
            
            List resultList = fetchList(params);
            if (resultList == null) resultList = new ArrayList();
            
            onafterFetchList(resultList); 
            fetchedRows = resultList.size(); 
            hasMoreRecords = false; 
            if (resultList.size() > preferredRows) 
            {
                hasMoreRecords = true;                
                resultList.remove(resultList.size()-1); 
            } 
            
            setDataList(resultList); 
            
            // calculate the maximum number of rows first.
            int tmpMaxRows = minlimit + resultList.size()-1;
            if (isAllocNewRow()) tmpMaxRows = tmpMaxRows + 1;
            if (tmpMaxRows > maxRows) maxRows = tmpMaxRows;
            
            //calculate the maximum limit to trigger next fetch.
            maxlimit = toprow + (getRows()*2)-1;
            if (maxlimit > maxRows) maxlimit = maxRows;
            
            //determine total page count. add extra page if not yet last page.
            pageCount = ((maxRows+1)/getRows()) + ( ((maxRows+1)%getRows())>0?1:0 ); 
        }

        pageIndex = (toprow/getRows())+1; 
        if (toprow==0 && minlimit==0) {
            fillListItems(getDataList(), minlimit);  
        } 
        else //if (isLastItem(selItem))
        {
            int baseIndex = ((toprow-minlimit)-getRows())+1; 
            if (selIndex == 0) baseIndex = (toprow-minlimit);
            
            List sublist = subList(getDataList(), baseIndex, getRows()); 
            fillListItems(sublist, minlimit+baseIndex);  
        }
        
        if (selItem != null) setSelectedItem(selIndex);
        if (selItem == null) setSelectedItem(0); 
    } 
    
    private List subList(List source, int start, int length) 
    {
        List list = new ArrayList();
        if (source == null || source.isEmpty()) return list;
        
        for (int i=start, len=start+length; i<len; i++) 
        {
            try { 
                list.add(source.get(i));
            } catch(Exception ex) {;} 
        } 
        return list;
    }

    
    /**
     * for moveNextPage,moveBackPage we need to force the loading.
     * for moveNextRecord and moveBackRecord, we shouls not force the load.
     * if maxRows < 0 meaning the maxRows was not determined.
     */
    public void moveNextRecord() {
        moveNextRecord(true); 
    }
    
    public void moveNextRecord(boolean includesEmptyItem) 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 
                
        int idx = getListItems().indexOf(getSelectedItem()); 
        if (idx >= 0 && idx+1 < getListItems().size()) 
        {
            setSelectedItem(idx+1);
            refreshSelectedItem(); 
        }  
        else 
        {
            ListItem selItem = getSelectedItem();
            int newToprow = selItem.getRownum()+1;
            if (newToprow < (minlimit+preferredRows) && newToprow <= (minlimit+fetchedRows)) 
            {
                toprow = newToprow; 
                refresh();
            } 
            else if (hasMoreRecords) 
            {
                toprow = newToprow; 
                refresh(true); 
            }
        }
    }
    
    public void moveBackRecord() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 
        
        int idx = getListItems().indexOf(getSelectedItem())-1; 
        if (idx >= 0 && idx < getListItems().size()) 
        {
            setSelectedItem(idx);
            refreshSelectedItem(); 
        }  
        else 
        {
            ListItem selItem = getSelectedItem();
            int newToprow = selItem.getRownum()-1;
            if (newToprow < 0) return;
            
            toprow = newToprow;
            if (newToprow >= minlimit) 
                refresh();
            else 
                refresh(true); 
        } 
    } 
    
    public void moveNextPage() 
    { 
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 
        
        int newToprow = toprow + getRows();
        if (newToprow < (minlimit+preferredRows)) 
        {
            refresh();
        }
        else 
        {
            
        }
        
        if (!isLastPage()) 
        {
            toprow = toprow+getRows();
            refresh();
            //refreshSelectedItem();
        }
    }
    
    public void moveBackPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        if (toprow-getRows() >= 0) 
        {
            toprow = toprow-getRows();
            refresh();
            //refreshSelectedItem();
        }
    }
    
    public void moveFirstPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        toprow = 0;
        refresh();
    }
    
    /**
     * this method sets the top row.
     * check first if the top row is possible.
     * if the toprow value is not possible,
     * do nothing. toprow is possible only if
     * it it does not exceed getRowCount() - getRows()
     */
    public int getTopRow() { return toprow; }    
    public void setTopRow( int toprow ) 
    {
        System.out.println("setTopRow: " + toprow);
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        //if the toprow is current do not proceed.
        if (this.toprow == toprow) return;
        if (toprow <= getMaxRows()) 
        {
            this.toprow = toprow; 
            refresh(); 
        }
    }

    protected void onselectedItemChanged(ListItem li) { 
        this.toprow = (li == null? 0: li.getRownum());  
    } 
      
    public final void doSearch() {
        load();
    }
    
    public int getPageIndex() { return pageIndex; }
    
    public int getRowCount() 
    {
        if (isAllocNewRow()) return maxRows - 1;

        return maxRows;
    }
    
    public boolean isLastPage() {
        return pageIndex >= pageCount;
    }
    
    /**
     * This function is used internally to check if we need to allocate
     * a new row for new item. If true, this will add an extra row in the
     * list.
     */
    private Boolean allocNewRow;
    protected boolean isAllocNewRow() 
    {
        if (allocNewRow == null) 
        {
            if (createItem() != null) 
                allocNewRow = new Boolean(true); 
            else 
                allocNewRow = new Boolean(false); 
        } 
        return allocNewRow.booleanValue();
    }
    
    public int getMaxRows() { return maxRows; }    
    public int getPageCount() { return pageCount; }
}
