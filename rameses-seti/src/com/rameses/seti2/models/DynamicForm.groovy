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
    
    @Binding
    def binding;

    @Controller
    def workunit;
    
    @Invoker
    def invoker;
    
    @Caller
    def caller;
    
    def data;
    def fields;
    def handler;
    def formInfos = [];
    def formTitle;
    
    public void init() {
        if(!fields) throw new Exception("handler must have getFields in dynamic input");
        if(!data) data = [:];
        buildFormInfos();
    }

    def formControls = [
        getControlList: {
            return formInfos;
        }
    ] as FormPanelModel;
    
    def buildFormInfos() {
        formInfos.clear();
        fields.each {x->
            if(!x.datatype) x.datatype = "string";
            def nme = x.name;
            if(!nme) throw new Exception("Error in DynamicForm. name is required");
            if(!nme.startsWith("data.")) nme = "data."+nme;
            def i = [
                type:x.datatype, 
                caption: (x.caption ? x.caption : x.title), 
                categoryid: x.category,
                name: nme, 
                required: ((x.required!=null) ? x.required : false),
                properties: [:],
            ];
            if( x.enabled!=null ) i.enabled = x.enabled;
            if( x.depends!=null ) {
                if(!x.depends.startsWith("data.")) x.depends = "data."+x.depends;
                i.depends = x.depends;
            }
            
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
            
            x.each { kk,vv->
                if( !kk.matches("name|datatype|caption|title|category|required|enabled|depends|arrayvalues|sortorder,showCaption,preferredSize") ) {
                    i.put(kk,vv);
                }
            }
            formInfos << i;
        }
     }
    
    def doOk() {
        if(handler) handler( data );
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
    
}
        