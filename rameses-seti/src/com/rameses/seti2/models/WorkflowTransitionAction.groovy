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
    WorkflowTaskListener listener;
    
    public WorkflowTransitionAction( def t, def task, def h, def l ) {
        this.task = task;
        this.transition = t;
        this.handler = h;
        this.listener = l;
        caption = t.caption;
        if(!caption) caption = t.properties.caption;
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
        def param = [:];
        param.putAll(transition);
        param.taskstate = task.state;
        
        def addInfo = listener.getInfoBeforeSignal( param );
        if( addInfo ) param.info = addInfo;
        
        def h = { info->
            if(info.assignee) {
                param.assignee = info.remove("assignee");
            }
            param.message = info.remove("message");
            if( info.size()>0 ) {
                if(!param.info) param.info = [:]
                param.info.putAll( info );
            }
            pass = true;
        }
        //transition role here is for the next role. Not the current one.
        Modal.show( "workflow_prompt:view", [role:transition.role, domain:transition.domain, handler: h] );
        if( !pass ) return null; 
        //include also current task state.
        return handler( param );
    }
    
}