package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import java.rmi.server.*;
        
public class CrudFormDebugSubInfoModel {
    
    def data;
    def dataValues;
    
    @FormId
    def formId;
    
    def dataValueHandler = [ 
        fetchList: { o->
            return dataValues;
        },
        onOpenItem: { o, colName->
            if(o.value==null) return null;
            if(o.value instanceof Map ) {
                Modal.show( "debug_subinfo:view", [data: o.value]);
            }
            else if(o.value instanceof List ) {
                Modal.show( "debug_sublist:view", [data: o.value]);
            }
        }
    ]as BasicListModel;
    
    void init() {
        formId = "CFDSM"+new UID();
        dataValues = [];
        if(data) {
            data.each { k,v->
                dataValues << [ key:k, value:v ];
            }
        }
    }
    
    def doClose() {
        return "_close";
    }
    
}    