package com.rameses.seti2.models;
 
import com.rameses.common.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;

/****
* This displays selected fields for editing. 
* The fields, handler and info is initially passed
*    fields = field names for regex match
*    handler = handler when info is updated
*    entity = the original bean
*    schema = the schema map to check field properties
*****/
public class DynamicInputForm  {
    
    def handler;
    
    public void init() {
        if(!handler) throw new Exception("handler is required in dynamic input");
        if(!handler.getFields) throw new Exception("handler must have getFields in dynamic input");
        if(!handler.onUpdate) throw new Exception("handler must have onUpdate in dynamic input");
        if(!handler.onComplete) throw new Exception("handler must have onComplete in dynamic input");
    }

    def formControls = [
        updateBean: {name,value,item->
            handler.onUpdate( name, value, item );
        },
        getControlList: {
            return handler.getFields();
        }
    ] as FormPanelModel;
    
    def doOk() {
        handler.onComplete();
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        