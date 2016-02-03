package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
        
public class CrudNodeListModel extends CrudListModel {

    private _nodeList;
    private def _selectedNode;
    
    def getNodeList() {
        if(!_nodeList) {
            def m = [:];
            m.schemaname = schema.name;
            m.adapter = schema.adapter;            
            _nodeList = queryService.getNodeList( m );
        }
        return _nodeList;
    }
    
    void setSelectedNode(def n) {
        _selectedNode = n;
        query.put("node", n);
        listHandler.reload();
    }
    
    def getSelectedNode() {
        return _selectedNode;
    }
    
}