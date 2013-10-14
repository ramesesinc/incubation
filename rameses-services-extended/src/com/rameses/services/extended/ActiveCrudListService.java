/*
 * AbstractCrudListService.java
 *
 * Created on August 5, 2013, 12:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import com.rameses.annotations.ProxyMethod;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ActiveCrudListService extends ActiveCrudService {
    
    public void beforeList(Object data){;}
    public void afterList(Object data, Object list){;}
    public String getPagingKeys() {
        return null;
    }
    
    public String getListMethod() {
        return "getList";
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
