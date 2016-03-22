package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import java.rmi.server.*;
        
public class CrudFormDebugSubListModel {
    
    def data;
    def schema;
    
    def selecteditem;
    
    @FormId
    def formId;
    
    def listHandler = [ 
        getColumnList : {
            def cols = [];
            def o = data.iterator().next();
            o.each { k,v->
                cols << [name: k, caption: k];
            }
            return cols;
        },
        fetchList: { o->
            return data;
        },
        onOpenItem: { o, colName->
            Modal.show( "debug_subinfo:view", [data: o]);
        }
    ]as BasicListModel;
    
    void init() {
        formId = "CFDSLM"+new UID();
    }
    
    def doClose() {
        return "_close";
    }
    
}    