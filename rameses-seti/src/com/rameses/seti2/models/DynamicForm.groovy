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
public class DynamicForm  {
    
    def data;
    def fields;
    def handler;
    def formInfos = [];
    def formTitle;
    
    public void init() {
        if(!handler) throw new Exception("handler is required in dynamic input");
        if(!fields) throw new Exception("handler must have getFields in dynamic input");
        if(!data) data = [:];
        buildFormInfos();
    }

    def formControls = [
        updateBean: {name,value,item->
            data[(name)] = value;
        },
        getControlList: {
            return formInfos;
        }
    ] as FormPanelModel;
    
    def buildFormInfos() {
        formInfos.clear();
        fields.each {x->
            if(!x.datatype) x.datatype = "string";
            x.value = data[ (x.name) ];
            def i = [
                type:x.datatype, 
                caption: (x.caption ? x.caption : x.title), 
                categoryid: x.category,
                name:x.name, 
                required: ((x.required!=null) ? x.required : false),
                properties: [:],
                value: x.value
            ];
            //fix the datatype
            if(x.datatype.indexOf("_")>0) {
                x.datatype = x.datatype.substring(0, x.datatype.indexOf("_"));
            }
            if(i.type == "boolean") {
                i.type = "yesno";
            }
            else if(i.type == "string_array") {
                i.type = "combo";
                i.preferredSize = '150,20';
                i.itemsObject = x.arrayvalues;
            }
            else if( i.type == 'decimal' ) {
                i.preferredSize = '150,20';
            }
            else if( i.type == 'integer' ) {
                i.preferredSize = '150,20';
            }
            else if( i.type == "string" ) {
                i.type = "text";
            }
            if(!i.sortorder) i.sortorder = 0;
            formInfos << i;
        }
     }
    
    def doOk() {
        handler( data );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        