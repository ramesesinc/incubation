package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.sigid.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;

public class WorkflowTaskWithSignaturePromptModel extends WorkflowTaskPromptModel {

    void showSignature() {
        SigIdViewer.open([
            onselect: { b->
                info.signature = b;
            }
        ] as SigIdModel)
    }
    
}