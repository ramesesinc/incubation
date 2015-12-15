package seti2.models;

import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;

public class WorkflowAction extends Action {
    
    def Map task;
    def String confirm;
    def String messageHandler;
    def List assignees;
    boolean closeOnEnd = false;
        
    public WorkflowAction(String n, Map task, Map t) {
        super(n);
        this.task = task;
        this.domain = task.domain;
        this.role = task.role;
        this.permission = task.permission;
        this.assignees = t.assignees;
        
        String caption = getName();
        messageHandler = null;
        Map props = t.properties;
        this.caption = props?.caption;
        if(this.caption==null) this.caption = "Submit";
        confirm = props?.confirm;
        messageHandler = props.messageHandler;
        if( messageHandler == "default") messageHandler = "taskmessage:create";
        if(props.closeOnEnd) closeOnEnd = props.closeonend;
    }
        
    public Object execute() {
        try {
            if(!task.state)
                throw new Exception("WorkflowController.execute error. There is no state in task.");
            Map req = new HashMap();
            req.taskid = task.objid;
            req.refid = task.refid;
            req.data = entity;
            req.state = task.state;
            req.extended = task.extended;
            if(name) req.action = name;
            beforeSignal(req);
            if( confirm ) {
                boolean pass = MsgBox.confirm(confirm);
                if(!pass) return null;
            }
            if( messageHandler != null ) {
                final Map resmap = new HashMap();
                def c = { 
                    resmap.pass = true;
                }
                try {
                    Map params = [:];
                    params.task = req;
                    params.assignees = assignees;
                    params.handler = c;
                    Map opt = [:];
                    opt.title = this.caption;
                    Modal.show( messageHandler, params, opt );
                    if ( !resmap.pass ) throw new BreakException();
                } catch(BreakException be) { 
                    throw be; 
                } catch(Exception e) {
                    e.printStackTrace();
                    throw new Exception("Error on displaying "+messageHandler, e);
                }
            }
                
            Object result = invokeSignal(req);
            if(result==null) {
                //if owner is not set, we must close the task to avoid loading errors.
                onEnd();
                if(closeOnEnd) return "_close";
                else return null;
            }
            afterSignal(result);
            return findPage(task);
        } 
        catch(Warning w) {
            return handleWarning(w); 
        } 
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}