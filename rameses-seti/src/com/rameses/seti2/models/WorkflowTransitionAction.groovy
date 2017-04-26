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
    
    private boolean showMessagePrompt(def param) {
        boolean pass = false;
        def h = { info-> pass = true; }
        //transition role here is for the next role. Not the current one.
        Modal.show( "workflow_prompt:view", [role:param.role, domain:param.domain, handler: h, param:param] );
        if( !pass ) return false; 
        return true;
    }
    
    /**************************************************************************
    * This is the general behavior of showing the confirm message: 
    * 1. [default]: message prompt will be displayed. If role specified, list of
    *    people having the role must be selected
    * 2. [props.showPrompt==false] The basic confirm message will be displayed.
    *    if props.showConfirm is specified it will be displayed.
    * 3. [props.showPrompt==false, props.showConfirm==false] No confirm message
    *    and after clicking signal it will just pass thru        
    ***************************************************************************/
    public def execute() {
        def props = transition.properties;
        if(!props ) props = [:];
        
        def param = [:];
        param.putAll(transition);
        param.taskstate = task.state;
        
        //try to check if the extending client has something to do before signal
        boolean t = listener.beforeSignal( param );
        if( !t) return null;
        //by default confirm message is displayed. 
        //Instead the prompt message will be displayed
        if( props.showPrompt == null || props.showPrompt.toBoolean() == true ) {
            t = showMessagePrompt( param );
            if(!t) return null;
        }
        else if( props.showConfirm == null || props.showConfirm.toBoolean() == true ) {
            String s = props.confirmMessage;
            if( !s ) s = "You are about to process this action. Proceed?";
            t = MsgBox.confirm( s );
            if(!t) return null;
        }
        
        //include also current task state.
        return handler( param );
    }
    
}