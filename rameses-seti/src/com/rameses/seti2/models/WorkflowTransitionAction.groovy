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
        
        //only visible false property will matter
        if( t.properties?.visible!=null && t.properties?.visible?.booleanValue() == false) {
            visibleWhen = "#{false}";
        } 
        else {
            visibleWhen = "#{task?.assignee?.objid==null || task.assignee.objid==user.objid }";
        }
        
        /*
        visibleWhen
        mnemonic
        */
    }
    
    public def execute() {
        def props = transition.properties;
        if(!props ) props = [:];
        
        def param = [:];
        param.putAll(transition);
        param.taskstate = task.state;
        
        boolean t = listener.beforeSignal( param );
        if( !t) return null;
        //include also current task state.
        return handler( param );
    }
    
}