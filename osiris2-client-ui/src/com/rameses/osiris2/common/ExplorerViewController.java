/*
 * ExplorerViewController.java
 *
 * Created on July 28, 2013, 10:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ExplorerViewController extends BasicListController
{
    private List formActions;
    private ExplorerListViewModel treeHandler;
    
    public void init() { 
        ExplorerListViewModel handler = getTreeHandler();
        if (handler != null) {
            handler.setListHandler(this); 
        }
    }

    public ExplorerListViewModel getTreeHandler() { return treeHandler; } 
    public void setTreeHandler(ExplorerListViewModel treeHandler) {
        this.treeHandler = treeHandler; 
    }
    
    public String getTitle() {
        ExplorerListViewModel handler = getTreeHandler();
        Node node = (Node) handler.getSelectedNode();
        return (node == null? null: node.getCaption()); 
    }    
    
    public Column[] getColumns() {
        List<Map> list = getColumnList();
        if (list == null || list.isEmpty()) 
            return getDefaultColumns();
        
        Column[] columns = new Column[list.size()];
        for (int i=0; i<list.size(); i++) {
            columns[i] = new Column(list.get(i)); 
        }
        return columns;
    }   
    
    public List<Map> getColumnList() {
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null;
        
        Node node = handler.getSelectedNode();
        return handler.getColumnList(handler.createParam(node));
    }
    
    private Column[] getDefaultColumns() {
        return new Column[]{ 
            new Column("caption", "Folder")
        };
    }

    public List fetchList(Map params) {
        ExplorerListViewModel handler = getTreeHandler();
        if (handler == null) return null; 

        Node node = handler.getSelectedNode();
        return handler.getList(handler.createParam(node));
    }

    public Object open() throws Exception { 
        Object item = getSelectedEntity(); 
        if (item instanceof Map) {
            Map map = (Map) item;
            String type = (String) map.get("type");
            if (type != null) {
                Map params = new HashMap();
                params.put("entity", map);
                return InvokerUtil.lookupOpener("explorer-"+type+":open", params); 
            }
        }
        return null; 
    }
        
    // <editor-fold defaultstate="collapsed" desc=" Form and Navigation Actions ">  

    public List getFormActions() {
        if (formActions == null) {
            ExplorerListViewModel handler = getTreeHandler();
            formActions = (handler == null? null: handler.lookupActions("formActions")); 
        } 
        return formActions; 
    }
    
    // </editor-fold>    

}
