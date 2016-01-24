package com.rameses.wf.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;


public class WorkflowPromptModel {

    @Service("WorkflowService")
    def wfService;
    
    def assigneeList = [];
    def entity = [:];
    def task;
    def handler;

    void init() {
        assigneeList = wfService.getAssigneeList(task); 
    }
    
    def doOk() {
        if(!handler) throw new Exception("Please provide handler");
        handler( entity );
        return "_close";
    }

    def doCancel() {
        return "_close";
    }

}