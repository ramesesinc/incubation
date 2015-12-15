package seti2.models;

import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;

public class WorkflowController {

    @Service("WorkflowService")
    def wfService;

    @Binding
    def binding;
    
    @Controller 
    def controller;
    
    def entity = [:];
    def task;
    def tasks = [];
    def messagelist = [];
    def fomrActions = [];
    def extActions = [];
    def workActions = [];
    
    def title;
    def message;
    
    //overridable actions
    public void beforeOpen(def o){;}
    public void afterOpen(def o){;}
    public void beforeSignal(def task){;}
    public void afterSignal(def result){;}
    public Object handleWarning(Warning w){ 
        throw w; 
    } 
    
    public Object findPage(def task) {
        return null;
    }
    
    public void buildExtActions() {
        extActions.clear();
        //build workitem types if any.
        if( task?.workitemtypes ) {
            extActions.add( new Action("addWorkitem", "Add Work Item", null)  );
        }
        List list = InvokerUtil.lookupActions( "extActions", { 
                invoker.workunitid == controller.workunit.workunit.id; 
        } as InvokerFilter );
        for(Object a: list) {
            extActions.add( a.clone() ); 
        }
    }
    
    private void buildFormActions(Map task) {
        if(!task.owner) return;
        if(! task.transitions) return;

        workActions.clear();
        for(Map t: transitions) {
            def props = t.properties;
            WorkflowAction wa = new WorkflowAction(t.action,task, t);
            if(props.visible) formActions << wa;
            workActions.add(wa);
        }
        
        buildExtActions();
        
        //set the message also
        message = null;
        if(task.message ) {
            message = task.message;
            messagelist.clear();
            messagelist.add(message);
        }
    }

    
    public Object addWorkitem() {
        PopupMenuOpener opener = new PopupMenuOpener();
        for(Object m: task.workitemtypes) {
            Action a = new WorkitemAction(m);
            a.setCaption( m.title );
            opener.add( a );
        }
        return  opener;
    }
    
    
    //called when opening a task
    public Object open() throws Exception {
        beforeOpen(entity);
        if(!entity.taskid ) {
            throw new Exception("Please indicate a taskid in the selectedItem");
        }
        Map map = [:];
        map.taskid = entity.taskid;
        task = wfService.openTask( map );
        
        formActions.clear();
        formActions.add(new Action("_close", "Close", null));
        buildFormActions(task);
        if( !task.data) {
            MsgBox.warn("Warning. Please add a data in task info. Entity will be empty");
        }
        
        entity = task.data;
        afterOpen( entity );
        Object nextPage = findPage(task);
        if(nextPage==null) return "default";
        return nextPage;
    }
    
    public Object signal() throws Exception {
        if(workActions.size) {
            WorkflowAction wfa = workActions.iterator().next();
            return wfa.execute();
        } else {
            throw new Exception("No action to signal found");
        }
    }
    
    public void onEnd() {
        //need to override here.
    }
    
    public Object signal(  String action ) throws Exception {
        if(!action) throw new Exception("Please provide an action in signal");
        def wfa =workactions.find{ it.name == action }
        if(!wfa) throw new Exception("Action " + action + " not found");
        return wfa.execute();
    }
    
    private Object invokeSignal(  Map req ) throws Exception {
        formActions.clear();
        formActions.add(new Action("_close", "Close", null));
        def result = wfService.signal( req );
        
        if( !result.task ) return null;
        this.task = result.task;
        this.tasks = result.tasks;
        buildFormActions( this.task );
        return result;
    }
    
    
}