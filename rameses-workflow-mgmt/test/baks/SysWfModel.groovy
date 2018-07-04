package com.rameses.wf.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;
import com.rameses.seti2.models.*;

public class SysWfModel extends CrudFormModel {
    
    def listHandler;    //this is a dummy so it wont produce an error.
    
    public def openItem(String n, def item, String colName) {
        return Inv.lookupOpener( "sys_wf_node:open", [entity: item] );
    }

    public void afterSave() {
        com.rameses.seti2.models.WorkflowCache.reload( entity.name );
    }
}