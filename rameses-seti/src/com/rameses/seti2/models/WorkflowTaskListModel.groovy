package com.rameses.seti2.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;


public class WorkflowTaskListModel extends com.rameses.seti2.models.CrudListModel {
    
    @Service("WorkflowTaskListService")
    def wfTaskListService;
    
    @Service('NotificationService')
    def notificationSvc; 
    
    @Script('Notification')
    def noteScript;
    
    public def getQueryService() {
        return wfTaskListService;
    }

    public String getProcessName() {
        return workunit.info.workunit_properties.processName;
    }
    
    public String getEntitySchemaName() {
        return getProcessName();
    }
    
    public String getSchemaName() {
        return getEntitySchemaName() + "_task";
    }

    @Close
    void onclose() { 
        noteScript.remove();
    } 
    
    public void init() {
        if( !getProcessName() ) 
            throw new Exception("Please indicate a processName");

        super.init();  
        
        noteScript.onMessage = {
            fetchNodeCount();
            nodeListHandler.repaint(); 
        }
    }
    
    public def beforeQuery( def m ) {
        m.processname = getProcessName();
    }
    
    public beforeFetchNodes( def m ) {
        m.processname = getProcessName();
    }

    
    boolean _nodes_loaded = false;
    
    def nodes = [];
    def nodeListHandler = [
        fetchList: { 
            nodes = super.getNodeList(); 
            if( !_nodes_loaded ) {
                fetchNodeCount(); 
                _nodes_loaded = true; 
            }
            return nodes; 
        },
        onselect: { 
            selectedNode = it; 
        }
    ] as ListPaneModel;    
    
    void fetchNodeCount() {
        if ( !notificationSvc ) return; 
        
        nodes.each { 
            if ( it.origtitle==null ) { 
                it.origtitle = it.title; 
            } 
            
            if( it.domain && it.role ) {
                boolean pass = false;
                try {
                    pass = secProvider.checkPermission( it.domain, it.role, null );
                }catch(e){;}
                
                if( pass) {
                    def icount = notificationSvc.getCount([ tag: getProcessName()+':'+ it.name ]);
                    if ( icount > 0 ) {
                        it.title = "<html>"+it.origtitle+"&nbsp;<font color=red><b>("+ icount +")</b></font></html>";
                    }
                }
            }
        }
    }
}