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
    def refkey = "objid";
    def refid;
    
    def listener;
    
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
        MsgBox.alert( "data is " + data);    
        
        entity._schemaname = schemaName;
        entity.data = data;
        
        changeInfoSvc.save( entity );
        /*
        def changeInfo = [:];
        changeInfo.reftype;
        changeInfo.refid;
        changeInfo.oldvalue = oldValues;
        entity._changeinfo = changeInfo;
        persistenceService.update( m );
        */
       return doOk();
    }
    
    
}
        