package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.common.*;
import com.rameses.util.*;

public class WorkflowTransitionAction extends com.rameses.rcp.common.Action {
    
    def task;
    def transition;
    def handler;
    
    public WorkflowTransitionAction( def t, def task, def h ) {
        this.task = task;
        this.transition = t;
        this.handler = h;
        caption = t.properties.caption;
        if(!caption) caption = t.action;
        if(!caption) caption = t.to;
        immediate = true;
        if(task.domain) domain = task.domain;
        if(task.role) role = task.role;
        if(t.permission) permission = t.permission;
        tooltip = caption;
        visibleWhen = "#{task?.assignee?.objid==null || task.assignee.objid==user.objid }";
        /*
        visibleWhen
        mnemonic
        */
    }
    
    public def execute() {
        def props = transition.properties;
        if(!props ) props = [:];
        boolean pass = false;
        def h = { info->
            if(info.assignee) {
                transition.assignee = info.assignee;
            }
            transition.message = info.message;
            pass = true;
        }
        Modal.show( "workflow_prompt:view", [role:transition.role, domain:transition.domain, handler: h] );
        if( !pass ) return null; 
        return handler( transition );
    }
    
}