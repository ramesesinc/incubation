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
    
    @ProxyMethod
    public Object getList(Object params) throws Exception {
        beforeList(params);
        List list = (List) getObj().invokeMethod("getList", new Object[]{params});
        afterList(params, list);
        return list;
    }
    
    
}
