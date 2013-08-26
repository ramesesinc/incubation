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
import com.rameses.osiris3.persistence.EntityManager;

/**
 *
 * @author Elmo
 */
public abstract class AbstractListService implements IListListener {
    
    protected abstract Object getEm();
    protected abstract String getSchemaName();
    
    public void beforeList(Object data){;}
    public void afterList(Object data, Object list){;}
    
    public String getListMethod() {
        return "getList";
    }
    
    private ListHelper getListHelper() {
        return new ListHelper(getSchemaName(), (EntityManager) getEm(), this, this.getListMethod());
    }
    
    @ProxyMethod
    public Object getList(Object params) throws Exception {
        return getListHelper().getList( params );
    }
    
    
}
