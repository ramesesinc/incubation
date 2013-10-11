/*
 * CrudService.java
 *
 * Created on August 5, 2013, 9:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import com.rameses.annotations.ProxyMethod;
import groovy.lang.GroovyObject;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ActiveListService  {
    
    protected abstract Object getEm();
    
    private GroovyObject getObj() {
        return (GroovyObject)getEm();
    }
    
    public void beforeList(Object data){;}
    public void afterList(Object data, Object list){;}
    
    public String getListMethod() {
        return "getList";
    }
    
    public String getPagingKeys() {
        return null;
    }
    
    @ProxyMethod
    public Object getList(Object params) throws Exception {
        if(getPagingKeys()!=null) {
            ((Map)params).put("_pagingKeys", getPagingKeys());
        }
        beforeList(params);
        List list = (List) getObj().invokeMethod(getListMethod(), new Object[]{params});
        afterList(params, list);
        return list;
    }
    
    
}
