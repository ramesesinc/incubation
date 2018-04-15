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
* This facility only extracts only a portion of the data. 
*/
public class ChangeInfoModel extends DynamicForm {
   
    @Service("ChangeInfoService")
    def changeInfoSvc;
    
    def entity = [:];
    def oldValues = [:];
    
    def reftype;
    def refkeys;
    
    def listener;
    def beforeUpdate;
    
    @PropertyChangeListener
    def fieldListener;
    
    public void init() {
        if(listener!=null) {
            fieldListener = [:];
            listener.each { k,v->
                if( !k.startsWith("data.")) k = "data." + k;
                fieldListener.put( k, { newVal ->  v( data, newVal ); })
            }
        }
        super.init();
    }
    
    public String getSchemaName() {
        return workunit?.info?.workunit_properties?.schemaName;
    }    
    
    def doOk() {
        if(beforeUpdate) beforeUpdate( data );
        entity._schemaname = schemaName;
        entity.data = data;
        entity.reftype = reftype;
        entity.refkeys = refkeys;
        changeInfoSvc.save( entity );
       return "_close";
    }
    
    
}
        