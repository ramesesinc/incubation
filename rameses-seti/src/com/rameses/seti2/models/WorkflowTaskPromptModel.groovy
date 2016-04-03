package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;

public class WorkflowTaskPromptModel {

    def role;
    def domain;
    def assigneeList;
    def handler;
    def info;

    boolean showAssigneeList = false;

    public def getLookupAssignees() {
        def h = { o->
            info.assignee = o;
        }
        return Inv.lookupOpener( "workflow_user:lookup", [domain:domain, role:role, onselect:h] );
    }

    void init() {
        info = [:];
        if( role )  showAssigneeList = true;
    }

    def doCancel() {
        return "_close";
    }

    def doOk() {
        if(handler) handler(info);
        return "_close";
    }
}