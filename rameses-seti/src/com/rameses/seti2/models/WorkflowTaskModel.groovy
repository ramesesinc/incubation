package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;
import com.rameses.seti2.models.CrudFormModel;

public class WorkflowTaskModel extends CrudFormModel implements WorkflowTaskListener {
    
    @Service("WorkflowTaskService")
    def workflowTaskSvc;
    
    def task;
    String processName;
    List transitions = [];
    
    def messagelist = [];
    
    public def getWorkflowTaskService() {
        return workflowTaskSvc;
    }
    
    public void afterSignal() {;}
    
    //the default behavior is it displays the workflow prompt.
    public boolean beforeSignal( def param  ) {
        /*
        def addInfo = 
        if( addInfo ) param.info = addInfo;
        */
       boolean pass = false;
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
        Modal.show( "workflow_prompt:view", [role:param.role, domain:param.domain, handler: h] );
        if( !pass ) return false; 
        return true;
    }
    
    protected void buildMessage() {
        //message is only viewed by the owner.
        messagelist.clear();
        if( task?.assignee?.objid ==  user.objid) {
            if(task?.message) messagelist.add( task.message );
        }
    }
    
    public def open() {
        if( entity.refid ) {
            refid = entity.refid;
        }
        def v = super.open();
        task = entity.remove("task");
        if(!task) 
            throw new Exception("There is no task attached to this entity. Please check read interceptor");
        buildMessage();
        if( pageExists(task.state)) {
            return task.state;
        }
        else {
            return v;
        }
    }
    
    /*
    public def open() {
        //do not use entity because it is the item from the passed list.
        def p = entity;
        entity = [:];
        entity.putAll(p);
        
        super.init();
        
        //find primary keys
        if(entity.taskid) {
            task = [taskid: entity.taskid, refid: entity.refid];
        }
        refid = entity.refid;
        def primKey = schema.fields.find{ it.primary == true }?.name;
        
        def k = EntityUtil.getNestedValue(entity, primKey);
        if( !k ) {
            if(!refid)
                throw new Exception("Error opening record. There must be a refid or primary key specified");
            EntityUtil.putNestedValue(entity, primKey, refid);
        }
            
        //find the task
        if(task) {
            def t = workflowTaskService.findTask( [processname: getSchemaName(), taskid: task.taskid] );
            buildTransitionActions( t );
            task = t;
        }
        
        def r = super.open();
        buildMessage();
        if( pageExists(task.state)) {
            return task.state;
        }
        else {
            return r;
        }
    }
    */
   
    
    public def signal( def transition ) {
        transition.processname = getSchemaName();
        transition.taskid = task.taskid;
        transition.refid = refid;
        def newTask = workflowTaskService.signal( transition );
        if( newTask?.taskid ) {
            task = newTask;
            transitions.clear();
            if( task.transitions ) {
                buildTransitionActions(task);
            }
        }
        else {
            task = [:];
        }
        //refresh the list
        if( caller.listHandler !=null ) {
            caller.listHandler.reload();
        }
        binding.refresh();
        buildMessage();
        afterSignal();
        return null;
    }
    
    final void buildTransitionActions( def tsk ) {
         if( tsk?.state == 'end' ) return;
         if( !tsk.assignee?.objid ) {
            def h = {
                def m = [:];
                m.processname = getSchemaName();
                m.taskid = task.taskid;
                def res = workflowTaskService.assignToMe(m);
                task.assignee = res.assignee;
                task.startdate = res.startdate;
                transitions.clear();
                buildTransitionActions(task);
            }
            transitions << new WorkflowAssignToMeAction( tsk, h );
        }
        else {
            def h = { t->
                return signal(t);
            }
            tsk.transitions.each{ 
                transitions << new WorkflowTransitionAction( it, tsk, h, this ) 
            }
        }
    }
    
    public List getFormActions() {
        def list = new ArrayList();
        list.addAll(transitions); 
        return list;
    }
    
    public boolean getShowNavigation() {
        return false;
    }
    
    public boolean isCreateAllowed() {
        return false;
    }
    
    public boolean isEditAllowed() {
        return false;
    }

    
    //This is to display the standard workflow actions
    public List getNavActions() {
        def actions2 = [];
        try { 
            def actionProvider = ClientContext.currentContext.actionProvider; 
            actions2 = actionProvider.lookupActions( "workflowtask:navActions" );
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup invokers caused by " + t.message);
        }
        return actions2.sort{ (it.index==null? 0: it.index) };
    }     
    
    def showTaskInfo() {
        return Inv.lookupOpener("workflowtask:showinfo");
    }
    
    
}

