package com.rameses.wf.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;

public abstract class WorkflowModel {
    
    @Service("WorkflowService")
    def wfService;

    @Binding
    def binding;
    
    abstract String getProcessname();
    abstract String getRefid();
    
    def prevTask;
    def prevTransition;
    def messagelist = [];
    
    def task;
    
    def afterOpen() {
        return "default";
    }
    
    void showMessage() {
        if( task?.message ) {
            messagelist << task.message;
        }    
    }
    
    def loadTask() {
        if(task)
            task = wfService.findOpenTask( [processname: getProcessname(), taskid: task.taskid] );
        else
            task = wfService.findOpenTask( [processname: getProcessname(), refid: getRefid()] );
        messagelist.clear();    
        showMessage();
        return afterOpen();    
    }
    
    void beforeSignal(def p) {
        //do nothing
    }

    void afterSignal() {
        if(task.state == 'end') {
            binding.fireNavigation("_close");
        }
        else {
            messagelist.clear();
            showMessage();
            //do nothing. p is the previous task prior to submit
            binding.refresh();
        }
    }
    
    void checkBeforeSignal(def p ) {
        wfService.checkBeforeSignal( p );
    }
    
    def getFormActions() {
        if(!task) return [];
        def list =  [];
        def h = { o->
            //store the previous task and action in case we need it.
            def _prevTask = task;    
            def _prevTransition = o.transition;
            
            def p = [processname:getProcessname(), taskid: task.taskid];
            p.state = task.state;
            p.action = o.transition.action;
            p.to = o.transition.to;
            p.refid = getRefid(); 
            
            //check first before attempting to signal            
            checkBeforeSignal( p );
            
            if( o.transition.to != 'end' ) {
                def result = [:];
                String promptname = "default";
                //if( o.transition.messagehandler ) promptname = o.transition.messagehandler.toLowerCase();

                boolean pass = false;
                def hh = { r->
                    pass = true;
                    p.putAll(r);
                }

                String m = "workflow_prompt:"+promptname;
                Modal.show(m, [handler: hh, task: p]);
                if( !pass ) return;
            }
            beforeSignal( p );        
            
            task = wfService.signal( p );
            
            //commit the previous task and transition;
            prevTask = _prevTask;
            prevTransition = _prevTransition;
            
            afterSignal();
        }    
        for( t in task.transitions ) {
            def a = new WorkflowTransitionAction();
            a.caption = t.caption;
            a.transition = t;
            a.handler = h;
            a.domain = task.domain;
            a.role = task.role;
            a.permission = t.permission;
            list << a;
        }
        return list;
    }
    
    /*
    def getExtActions() {
        return Inv.lookupActions( getProcessname()+":form:extActions", [entity: entity] );
    }
    */
    
    def viewHistory() {
        def p= [ processname: getProcessname(), refid:getRefid() ];
        return Inv.lookupOpener("workflow_history:view", [param:p] );
    }
    
    def viewOpenSubTasks() {
        def p= [ processname: getProcessname(), taskid:task.taskid ];
        return Inv.lookupOpener("workflow_open_subtask:view", [param: p] );
    }

}
