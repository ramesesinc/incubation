package com.rameses.rulemgmt.action;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleActionVarHandler  {

    @Binding
    def binding;

    @Service("RuleMgmtService")
    def service;

    def action;
    def actionParam;
    def actionParamDef;
    def varList;

    void init() {
        varList = service.findAllVarsByType( [ruleid:action.parentid, datatype:actionParamDef.datatype ] );
        if(actionParam.var) {
            MsgBox.alert( actionParam.var );
            actionParam.var = varList.find{ it.objid == actionParam.var.objid }
        }
    }

}