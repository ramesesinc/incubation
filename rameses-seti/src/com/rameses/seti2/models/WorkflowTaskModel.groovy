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

public class WorkflowTaskModel extends CrudFormModel {
    
    @Service("WorkflowTaskService")
    def workflowTaskSvc;
    
    def task;
    String processName;
    List transitions = [];
    List extActions;
    
    public def getWorkflowTaskService() {
        return workflowTaskSvc;
    }
    
    
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
        def refid = entity.refid;
        def primKey = schema.fields.find{ it.primary == true }?.name;
        
        def k = EntityUtil.getNestedValue(entity, primKey);
        if( !k ) {
            if(!refid)
                throw new Exception("Error opening record. There must be a refid or primary key specified");
            EntityUtil.putNestedValue(entity, primKey, refid);
        }
    
        extActions = super.getExtActions();
        
        //find the task
        if(task) {
            def t = workflowTaskService.findTask( [processname: getSchemaName(), taskid: task.taskid] );
            buildTransitionActions( t );
            task = t;
        }
        return super.open();
    }
    
    public def signal( def transition ) {
        transition.processname = getSchemaName();
        transition.taskid = task.taskid;
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
        return null;
    }
    
    final void buildTransitionActions( def tsk ) {
        def h = { t->
            return signal(t);
        }
        tsk.transitions.each{ 
            transitions << new WorkflowTransitionAction( it, tsk, h ) 
        }
    }
    
    public List getExtActions() {
        def list = new ArrayList();
        list.addAll(transitions); 
        if(extActions!=null) list.addAll( extActions );
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

    
}

