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

    public void init() {
        if( !getProcessName() ) 
            throw new Exception("Please indicate a processName");
        super.init();    
    }
    
    public def beforeQuery( def m ) {
        m.processname = getProcessName();
    }
    
    public beforeFetchNodes( def m ) {
        m.processname = getProcessName();
    }
}