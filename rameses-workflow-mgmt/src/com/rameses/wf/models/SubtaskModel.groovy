package com.rameses.wf.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;

public abstract class SubtaskModel {
    
    @Service("WorkflowService")
    def wfService;

    def subtask;
    
    def loadSubtask() {
        
    }
    
}

