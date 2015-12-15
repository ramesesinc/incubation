package seti2.models;

import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;

public class WorkitemAction extends Action {
    
    private Map workitem;
    def task;    
    def caption;
    
    public WorkitemAction(Map m) {
        this.workitem = m;
    }
    
    public Object execute() {
        Map req = [:]
        req.name = workitem.name;
        req.taskid =  task.objid;
        req.action = workitem.action;
        req.refid = task.refid;
        req.workitemid = workitem.objid;
        Map params = [:];
        params.task = req;
        params.assignees = workitem.assignees;
        Map opt = [:];
        opt.title = caption;
        Modal.show( "taskmessage:create", params, opt );
        if( !req.message ) return null;
        /*
        try {
            getWfService().addWorkItem( req );
            MsgBox.alert(getCaption() + " workitem created");
            return null;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        */
    }
}