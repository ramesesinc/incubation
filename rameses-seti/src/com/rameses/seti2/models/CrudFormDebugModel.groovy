package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
        
public class CrudFormDebugModel {
    
    def schema;
    def data;
    
    def selectedName;
    
    def fieldNames;
    def dataValues;

    def fieldHandler = [
        fetchList: { o->
            if( !selectedName ) return [];
            def m = (schema.fields+schema.links).find{ it.name == selectedName };
            def list = [];
            m.each {k,v->
                list << [key:k, value:v];
            }
            return list;
        }
    ] as BasicListModel;

    def dataValueHandler = [ 
        fetchList: { o->
            return dataValues;
        },
        onOpenItem: { o, colName->
            if(o.value==null) return null;
            if(o.value instanceof Map ) {
                Modal.show( "debug_subinfo:view", [data: o.value]);
            }
            else if( o.value instanceof List ) {
                Modal.show( "debug_sublist:view", [data: o.value, schema: schema]);
            }
        }
    ]as BasicListModel;
    
    void init() {
        fieldNames = (schema.fields+schema.links)*.name;
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