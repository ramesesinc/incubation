/*
 * TreeTableModel.java
 *
 * Created on February 18, 2011, 10:17 AM
 * @author jaycverg
 */

package com.rameses.rcp.common;

import com.rameses.rcp.control.treetable.ItemStatus;
import com.rameses.common.PropertyResolver;
import com.rameses.util.CacheMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class TreeTableModel extends SubListModel 
{    
    private PropertyResolver resolver = PropertyResolver.getInstance(); 
    private Map<Object, ItemStatus> statusIndex = new WeakHashMap();
    private int indentWidth = 10;
    private int selectedColumnIndex = 0;
        
    private CacheMap cache = new CacheMap() {
        public Object fetch(Object key) {
            List items = fetchChildren(key);
            if( items == null ) return null;
            
            int level = getStatus(key).level;
            for(Object item : items) {
                ItemStatus status = getStatus(item);
                status.level = level + 1;
            }
            return items;
        }
    };
    
    public void load() {
        statusIndex.clear();
        cache.clear();
        super.load();
    }
    
    public void reload(Object obj) {
        Object id = getRowId(obj);
        cache.remove(id);
        
        ArrayList arrayList = (ArrayList) getDataList();
        int idx = arrayList.indexOf(obj);
        ItemStatus status = getStatus(obj);
        int level = status.level;
        
        if( status.expanded ) {
            //remove first
            List removed = new ArrayList();
            for( int j=idx+1; j<arrayList.size(); j++ ) {
                Object x = arrayList.get(j);
                ItemStatus xStatus = getStatus(x);
                if( xStatus.level <= level ) break;
                removed.add(x);
            }
            arrayList.removeAll(removed);
            
            //add the sublist again
            List subList = (List) cache.getData( id, obj );
            if( subList == null ) return;
            
            arrayList.addAll(idx+1, subList);
            for(Object o : subList) {
                ItemStatus is = getStatus(o);
                if( is.expanded ) loadExpandedItems(o);
            }
        }
    }
    
    protected void fetch(boolean forceLoad) 
    {
        List dataList = getDataList();
        if (dataList == null) 
        {
            dataList = new ArrayList();
            
            Map params = new HashMap();
            onbeforeFetchList(params);
            
            List result = fetchList(params);
            if (result != null) 
            {
                onafterFetchList(result);
                dataList.addAll(result);
            }
        }
        
        maxRows = dataList.size()-1;
        
        //reset the force load.
        List subList = new ArrayList();
        if (dataList.size() > 0) 
        {
            int tail = toprow + getRows();
            if (tail > dataList.size()) tail = dataList.size();
            
            subList = dataList.subList(toprow, tail);
        }
        
        fillListItems(subList, toprow);
        
        if (getSelectedItem() != null) 
            pageIndex = (getSelectedItem().getRownum()/ getRows())+1;
        
        else 
        {
            pageIndex = 1;
            setSelectedItem(0);
        }
        
        pageCount = ((maxRows+1) / getRows()) + (((maxRows+1) % getRows()>0)?1:0);
    }
    
    public abstract List fetchChildren(Object parent);
    
    /** non-overridable since report sheet is for viewing only **/
    public final Object createItem() {
        return null;
    }
    
    private Object getRowId(Object obj) 
    {
        Column col = getPrimaryColumn();
        if ( col != null ) 
        {
            return resolver.getProperty(obj, col.getName());
        } 
        else {
            return obj;
        }
    }
    
    public final ItemStatus getStatus(Object obj) 
    {
        Object id = getRowId(obj);
        ItemStatus status = statusIndex.get(id);
        if ( status == null ) 
        {
            status = new ItemStatus();
            status.level = 0;
            statusIndex.put(id, status);
        }
        return status;
    }
    
    public final Object onOpenItem(Object obj, String columnName) {
        if( selectedColumnIndex != 0 ) return null;
        
        try {
            processToggle(obj);
        } catch(Exception e){;}
        
        refresh();
        return null;
    }
    
    private void processToggle(Object obj) {
        Object id = getRowId(obj);
        
        ArrayList arrayList = (ArrayList) getDataList();
        
        int idx = arrayList.indexOf(obj);
        ItemStatus status = getStatus(obj);
        int level = status.level;
        
        if ( !status.expanded ) 
        {
            List subList = (List) cache.getData( id, obj );
            if( subList == null ) return;
            
            arrayList.addAll(idx+1, subList);
            for (Object o : subList) 
            {
                ItemStatus is = getStatus(o);
                if ( is.expanded ) loadExpandedItems(o);
            }
            status.expanded = true;
        } 
        else 
        {
            List removed = new ArrayList();
            for ( int j=idx+1; j<arrayList.size(); j++ ) 
            {
                Object x = arrayList.get(j);
                ItemStatus xStatus = getStatus(x);
                if( xStatus.level <= level ) break;
                
                removed.add(x);
            }
            arrayList.removeAll(removed);
            status.expanded = false;
        }
    }
    
    private void loadExpandedItems(Object obj) {
        ArrayList arrayList = (ArrayList) getDataList();
        int idx = arrayList.indexOf(obj);
        
        Object id = getRowId(obj);
        List subList = (List) cache.getData( id, obj );
        if( subList == null ) return;
        
        arrayList.addAll(idx+1, subList);
        for (Object o : subList) 
        {
            ItemStatus is = getStatus(o);
            if( is.expanded ) loadExpandedItems(o);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="  getters/setters  ">
    
    public int getIndentWidth() {
        return indentWidth;
    }
    
    public void setIndentWidth(int indentWidth) {
        this.indentWidth = indentWidth;
    }
    
    public final int getSelectedColumnIndex() {
        return selectedColumnIndex;
    }
    
    public final void setSelectedColumnIndex(int selectedColumnIndex) {
        this.selectedColumnIndex = selectedColumnIndex;
    }
    
    // </editor-fold>
    
}
