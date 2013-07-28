/*
 * ExplorerViewContentController.java
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ExplorerViewContentController extends BasicListModel
{
    private ExplorerViewModel treeHandler;
    private Object selectedNode;
    
    public void init() { 
        ExplorerViewModel handler = getTreeHandler();
        if (handler != null) {
            handler.setListHandler(this); 
        }
    }

    public BasicListModel getListHandler() { return this; } 
    
    public ExplorerViewModel getTreeHandler() { return treeHandler; } 
    public void setTreeHandler(ExplorerViewModel treeHandler) {
        this.treeHandler = treeHandler; 
    }
    
    public Object getSelectedNode() { return selectedNode; } 
    public void setSelectedNode(Object selectedNode) {
        this.selectedNode = selectedNode;
    }
    
    public Column[] getColumns() {
        List<Map> list = getColumnList();
        if (list == null || list.isEmpty()) return null;
        
        Column[] columns = new Column[list.size()];
        for (int i=0; i<list.size(); i++) {
            columns[i] = new Column(list.get(i)); 
        }
        return columns;
    }   
    
    public List<Map> getColumnList() {
        List<Map> list = new ArrayList();
        Map map = new HashMap();
        map.put("name", "caption");
        map.put("caption", "Folder");
        list.add(map); 
        return list; 
    }

    public List fetchList(Map params) {
        ExplorerViewModel handler = getTreeHandler();
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
