package com.rameses.seti2.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.common.Action;
import java.util.*;
import com.rameses.osiris2.client.*;
import com.rameses.rcp.framework.*;

public final class WorkflowCache {
    
    private static def svc;
    private static Map<String, List> nodes = [:]; 
    
    private static def getService() {
        if(svc==null) svc = InvokerProxy.getInstance().create("WorkflowTaskListService");
        return svc;
    }
    
    public static getNodeList(String processName) {
        if( !nodes.containsKey(processName) ) {
            def list =  getService().getNodeList( [processname: processName ] );
            nodes.put( processName, list );
        }
        return nodes.get(processName);
    }
    
    public static void clear( String processName ) {
        nodes.remove( processName );
    }
    
    public static void reload( String processName ) {
        nodes.remove( processName );
        getNodeList( processName );
    }
    
    
}
