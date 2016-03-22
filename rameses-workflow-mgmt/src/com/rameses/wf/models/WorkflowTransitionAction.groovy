package com.rameses.wf.models;

import com.rameses.rcp.common.Action;

public class WorkflowTransitionAction extends Action {
    
    def transition;
    def handler;
    
    def execute() {
        if(!handler) 
            throw new Exception("Handler not specified");
        handler(this);
    }
    
}
    
