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
import com.rameses.osiris3.persistence.EntityManager;

/**
 *
 * @author Elmo
 */
public abstract class AbstractCrudListService extends AbstractCrudService  implements IListListener{
    
    public void beforeList(Object data){;}
    public void afterList(Object data, Object list){;}
    
    private ListHelper getListHelper() {
        return new ListHelper(getSchemaName(), (EntityManager) getEm(), this);
    }
    
    @ProxyMethod
    public Object getList(Object params) {
        try {
            return getListHelper().getList( params );
        } catch(Exception ign){
            throw new RuntimeException(ign);
        }
    }
    
}
