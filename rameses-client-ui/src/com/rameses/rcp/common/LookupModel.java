package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The lookup list model extends paging model
 * - returnSingleResult is when you want the lookup to
 *    immediately return a result without popping the lookup
 *    dialog. null null is the default
 */
public class LookupModel extends ScrollListModel 
{    
    private List DEFAULT_LIST = new ArrayList(); 
    private Map properties = new HashMap(); 
    private LookupSelector selector;
        
    public Map getProperties() { return properties; } 
    
    public LookupSelector getSelector() { return selector; }    
    public void setSelector(LookupSelector s) { this.selector = s; }

    public Object getSelectedValue() 
    {
        if (getSelectedItem() == null) 
            return null;
        else
            return getSelectedItem().getItem(); 
    }    
        
    public boolean selectSingleResult() { return false; }    
    public boolean errorOnEmpty() { return false; }    
    
    public List fetchList(Map params) { return DEFAULT_LIST; } 
        
    //default implementation for select and cancel
    public String select() 
    {
        if (selector != null) selector.select( getSelectedValue() );
        
        return "_close";
    }
    
    public String cancel() 
    {
        if (selector != null) selector.cancelSelection();
        
        return "_close";
    }
    
    public String emptySelection() 
    {
        if (selector != null) selector.select(null);
        
        return "_close";
    }
    
    
    //invoked when the lookup screen is shown
    public boolean show(String t) 
    {
        setSearchtext(t);
        load();
        
        if (errorOnEmpty() && getDataList().size() == 0) 
            throw new IllegalStateException("There are no records found");
        
        if (selectSingleResult() && getDataList().size() == 1) 
        {
            Object retVal = getDataList().get(0);
            if (selector != null) selector.select(retVal);
            
            return false;
        } 
        else {
            return true;
        }
    }
    
    protected void onfinalize() throws Throwable {
        selector = null;
    }

    public void moveLastPage() {
    }   
}
