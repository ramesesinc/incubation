package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import com.rameses.util.*;

public class WorkflowAssignToMeAction extends com.rameses.rcp.common.Action {
    
    def task;
    def handler;
    
    public WorkflowAssignToMeAction( def task, def h ) {
        this.task = task;
        this.handler = h;
        caption = 'Assign To Me';
        immediate = true;
        if(task.domain) domain = task.domain;
        if(task.role) role = task.role;
        tooltip = caption;
        visibleWhen = "#{task?.assignee?.objid==null || task.assignee.objid==user.objid }";
        /*
        mnemonic
        */
    }
    
    public def execute() {
        if( MsgBox.confirm("You are about to assign this task to you. Proceed?")) {
            handler();
        }
        return null;
    }
    
}