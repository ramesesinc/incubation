package com.rameses.rulemgmt.action;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*

public class RuleActionLookupHandler extends RuleActionHandler {

    @Binding
    def binding;

    @Service("RuleMgmtService")
    def service;

    def action;
    def actionParam;
    def actionParamDef;

    void init() {
    }

    def showLookup() {
        if( !actionParamDef.lookupkey || !actionParamDef.lookupvalue )
            throw new Exception( "Please specify a lookup key and lookup value in the definition" )
        return InvokerUtil.lookupOpener( actionParamDef.datatype + ":lookup", [
            onselect: { o->
                actionParam.value = [ key: o[actionParamDef.lookupkey], value: o[actionParamDef.lookupvalue] ] ;
                binding.refresh("selection");
            }
        ]);
    }
    
    String getSelection() {
        return actionParam.value?.value;
    }

}