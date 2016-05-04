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

//This is used in conjunction with an OKCancelModel. Used for popping up data
//a handler must be passed. This can be extended or just plainly used as is 
public class PromptDataModel  {
    
    @Script("ListTypes")
    def listTypes;
    
    @Caller
    def caller;
    
    String title;
    def entity = [:];
    def handler;
    
    def doOk() {
        if(!handler) throw new Exception("A handler must be specified");
        handler(entity);
        return "_close";
    }
    
    def doCancel() {
        return "_close";
    }
}
        