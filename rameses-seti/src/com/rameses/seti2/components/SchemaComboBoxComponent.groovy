package com.rameses.seti2.components;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.common.Action
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

/***********************************************
* This is applicable for short lists
************************************************/
public class SchemaComboBoxComponent extends ComponentBean  {
    
    @Service("QueryService")
    def queryService;
    
    String schemaName;
    String customFilter;
    String select;
    def query = [:]; 

    String expression;
    String visibleWhen;
    String disableWhen;

    public def getList() {
        def m = [_schemaname: schemaName]; 
        if(!select) m.select = select;
        if( customFilter ) {
            m.where = [customFilter, query];
        }
        else {
            m.where = ["1=1"];
        }
        return queryService.getList(m);
    }
    
    
}   

