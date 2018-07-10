package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
import com.rameses.rcp.constant.*;
import java.rmi.server.*;
import com.rameses.util.*;

public class MdiChildModel  {

    @Caller
    def caller;

    boolean hasCallerMethod( property ) {
        if ( caller == null ) return false; 
        return caller.metaClass.respondsTo(caller, property ); 
    }
    
    public def getEntity() { 
        if( hasCallerMethod('entity')) {
            return caller.entity;
        }
        return null; 
    }
    
}