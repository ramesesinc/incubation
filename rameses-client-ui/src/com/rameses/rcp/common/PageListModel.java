package com.rameses.rcp.common;

import com.rameses.rcp.common.EditorListSupport.TableEditorHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PageListModel extends AbstractListDataProvider 
    implements ListPageModel, EditorListSupport.TableEditor  
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

    
    // <editor-fold defaultstate="collapsed" desc=" editor options and callback events ">
        
    public boolean isAllowColumnEditing() { return false; } 
    public boolean isAllowAdd() { return true; } 
    
    public Object createItem() { 
        return new HashMap(); 
    }     
    
    protected void validateItem(Object item) {}    
    protected void validate(ListItem li) {
        if (li != null) validateItem(li.getItem()); 
    }
    
    protected void addItem(Object item) {}
    protected void onAddItem(Object item) {
        addItem(item);
    }
    
    protected void onUpdateItem(Object item) {
    }
    
    protected boolean onRemoveItem(Object item) { 
        return false;
    } 
    
    protected void commitItem(Object item) {} 
    protected void onCommitItem(Object item) {
        commitItem(item); 
    }

    public boolean isColumnEditable(Object item, String columnName) { 
        return true;
    }
    
    protected boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
        return true; 
    } 
    
    protected void afterColumnUpdate(Object item, String columnName) {}
    protected void onColumnUpdate(Object item, String columnName) {
        afterColumnUpdate(item, columnName); 
    }    
    
    public final void refreshEditedCell() {
        refreshCurrentEditor(); 
    }
    public final void refreshCurrentEditor() {
        if (editorSupport != null) editorSupport.refreshCurrentEditor(); 
    }    
    
    public final boolean hasUncommittedData() {
        return (editorSupport == null? false: editorSupport.hasUncommittedData()); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TableEditor implementation ">

    private TableEditorHandlerImpl editorHandler;
    private EditorListSupport editorSupport;
    
    public TableEditorHandler getTableEditorHandler() { 
        if (!isAllowColumnEditing()) return null; 
        
        if (editorHandler == null) { 
            editorHandler = new TableEditorHandlerImpl(); 
        } 
        return editorHandler; 
    } 

    public TableModelSupport getTableModelSupport() {
        return tableModelSupport; 
    }
    
    public void setEditorListSupport(EditorListSupport editorSupport) {
        this.editorSupport = editorSupport; 
    }    
        
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" TableEditorHandler implementation ">
    
    private class TableEditorHandlerImpl implements EditorListSupport.TableEditorHandler 
    {
        PageListModel root = PageListModel.this;

        public Object createItem() {
            return root.createItem(); 
        }
        
        public boolean isAllowAdd() {
            return root.isAllowAdd(); 
        }

        public void validate(ListItem li) { 
            root.validate(li);
        }

        public void onAddItem(Object item) {
            root.onAddItem(item);
        }
        
        public void onUpdateItem(Object item) {
            root.onUpdateItem(item);
        }        

        public void onCommitItem(Object item) { 
            root.onCommitItem(item);
        } 

        public boolean onRemoveItem(Object item) {
            return root.onRemoveItem(item);
        }

        public boolean isColumnEditable(Object item, String columnName) {
            return root.isColumnEditable(item, columnName); 
        }

        public boolean beforeColumnUpdate(Object item, String columnName, Object newValue) {
            return root.beforeColumnUpdate(item, columnName, newValue);
        }

        public void onColumnUpdate(Object item, String columnName) {
            root.onColumnUpdate(item, columnName); 
        }
    } 
    
    // </editor-fold>    
}
