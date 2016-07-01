package com.rameses.client.notification.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.NotificationManager;
import com.rameses.rcp.framework.NotificationHandler; 

class GroupNotificationHandlerModel implements NotificationHandler {   

    @Invoker 
    def invoker; 

    @Service('QueryService') 
    def querySvc;  
    
    @Service('NotificationService') 
    def notificationSvc; 
    
    def ctx = ClientContext.getCurrentContext(); 
    def nodes = [];
    def groups = [];
    
    void init() { 
        nodes = [];  
        groups = [];

        def secProvider = ctx.getSecurityProvider();        
        def params = [ _schemaname: 'sys_wf_node' ]; 
        params.findBy = [ nodetype: 'state' ]; 
        params.orderBy = 'processname,idx';
        params.select = 'name,domain,role';
        querySvc.getList( params ).findAll{( it.domain && it.role )}.each{ 

            if ( secProvider.checkPermission( it.domain, it.role, null)) {
                nodes << it; 
            } 
        } 

        if ( nodes ) { 
            NotificationManager.addHandler( this ); 
            groups = nodes.collect{ it.name.toString().toUpperCase() }.unique(); 
            notificationSvc.getNotified([ groups: groups ]); 
        } 
    } 

    public void onMessage( data ) { 
        def np = ctx.getNotificationProvider(); 
        if ( np == null ) return; 

        def env = ctx.getHeaders(); 
        if ( data.recipienttype == 'user' && data.recipientid == env?.USERID) {
            np.sendMessage( data ); 
            
        } else if ( data.recipienttype == 'group' && groups.contains(data.recipientid.toString().toUpperCase()) ) {  
            np.sendMessage( data ); 
        }
    } 
    
    public void onRead( data ) { 
        //
    } 
} 