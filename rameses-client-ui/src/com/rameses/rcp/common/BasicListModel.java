package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicListModel extends AbstractListDataProvider 
{    
    private List DEFAULT_LIST = new ArrayList();    
    private Map query = new HashMap();

    public Map getQuery() { return query; } 
    
    public List fetchList(Map params) { return DEFAULT_LIST; }
    
    public AbstractListDataProvider getListHandler() { return this; }
    
    protected void onbeforeFetchList(Map params) 
    {
        Map qry = getQuery(); 
        if (qry != null) params.putAll(qry); 
    }
       
    public int getRows() 
    { 
        // to indicate that the rows are dynamic 
        // and will use the getRowCount instead
        return -1; 
    }

    public void search() {
        load();
    }

    public void reload() {
        super.refresh(true);
    }
}
