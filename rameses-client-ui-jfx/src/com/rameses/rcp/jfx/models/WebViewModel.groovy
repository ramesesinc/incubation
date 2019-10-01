package com.rameses.rcp.jfx.models; 

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;

class WebViewModel {
    
    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Caller
    def caller;
    
    @Binding
    def binding;

    def _url;
    
    def getUrl() { 
        if ( !_url ) _url = workunit?.info?.workunit_properties?.url; 
        if ( !_url ) _url = invoker?.properties?.url; 
        return _url; 
    }
    
    def getValue() { 
        return getUrl(); 
    } 
}