package com.rameses.seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.*;
        
/**
* used for lists under sections
**/
public abstract class CrudSubListModel extends CrudListModel {
        
    def getMasterEntity() {
        return caller.entity;
    }

    void beforeQuery( def qry ) {
        qry.findBy = getListFilter();
    }
    
    public abstract def getListFilter();
    
}
