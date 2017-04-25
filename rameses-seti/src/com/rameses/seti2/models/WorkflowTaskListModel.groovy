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
import com.rameses.seti2.models.*;

public class WorkflowTaskListModel extends com.rameses.seti2.models.CrudListModel {
    
    @Service("WorkflowTaskListService")
    def wfTaskListService;
    
    @Script('TaskNotifier')
    def _taskNotifier;
    
    public def getTaskNotifier() {
        if( workunit.info.workunit_properties.allowNotify == 'false' ) return null;
        return _taskNotifier;
    }
    
    public def getQueryService() {
        return wfTaskListService;
    }

    public String getProcessName() {
        return workunit.info.workunit_properties.processName;
    }

    @Close
    void onclose() { 
        if(taskNotifier) taskNotifier.deactivate();
    } 
    
    public void init() {
        if( !getProcessName() ) 
            throw new Exception("Please indicate a processName");

        super.init();  
        if(taskNotifier) {
            taskNotifier.activate(getProcessName(), {
                nodeListHandler.repaint(); 
                listHandler.reload();
            });
        }
    }
    
    public void beforeQuery( def m ) {
        m.processname = getProcessName();
    }
    
    public beforeFetchNodes( def m ) {
        m.processname = getProcessName();
    }
    
    def nodeListHandler = [
        fetchList: { 
            if( taskNotifier ) {
                return taskNotifier.getNodeList();    
            }
            else {
                return WorkflowCache.getNodeList( getProcessName() );
            }
        },
        onselect: { 
            selectedNode = it; 
        }
    ] as ListPaneModel;    

    
}