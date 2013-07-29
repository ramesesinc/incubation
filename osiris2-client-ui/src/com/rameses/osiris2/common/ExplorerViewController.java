/*
 * ExplorerViewController.java
 *
 * Created on July 28, 2013, 10:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.BasicListModel;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.Node;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ExplorerViewController extends BasicListModel
{
    private ExplorerListViewModel treeHandler;
    private Node selectedEntity;
    
    public void init() { 
        ExplorerListViewModel handler = getTreeHandler();
        if (handler != null) {
            handler.setListHandler(this); 
        }
    }

    public BasicListModel getListHandler() { return this; } 
    
    public ExplorerListViewModel getTreeHandler() { return treeHandler; } 
    public void setTreeHandler(ExplorerListViewModel treeHandler) {
        this.treeHandler = treeHandler; 
    }
    
    public Node getSelectedEntity() { return selectedEntity; } 
    public void getSelectedEntity(Node selectedEntity) {
        this.selectedEntity = selectedEntity;
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
        return (node == null? null: node.getItems()); 
    }

    protected Object onOpenItem(Object item, String columnName) {
        if (item instanceof Node) {
            Node node = (Node)item;
            node.open();
        }
        return null;
    }
}
