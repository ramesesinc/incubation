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
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class AbstractCrudService {
    
    protected abstract Object getEm();
    protected abstract String getSchemaName();
    
    public boolean isValidate() {
        return true;
    }
    
    public void beforeCreate(Object data) {;}
    public void afterCreate(Object data) {;}
    public void beforeUpdate(Object data) {;}
    public void afterUpdate(Object data) {;}
    
    public void beforeOpen(Object data) {;}
    public void afterOpen(Object data) {;}
     
    public void beforeRemoveEntity(Object data) {;}
    public void afterRemoveEntity(Object data) {;}
    
    @ProxyMethod
    public Object create(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.create parameter must be map");
        Map map = (Map)data;
        beforeCreate(map);
        EntityManager em = (EntityManager)getEm();
        if(isValidate()) em.validate(getSchemaName(), map);
        afterCreate(map);
        return em.create(getSchemaName(), map);
    }
    
    @ProxyMethod
    public Object update(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.create parameter must be map");
        Map map = (Map)data;
        EntityManager em = (EntityManager)getEm();
        beforeUpdate(map);
        if(isValidate()) em.validate(getSchemaName(), map);
        afterUpdate(map);
        return em.create(getSchemaName(), map);
    }
    
    @ProxyMethod
     public Object open(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.create parameter must be map");
        Map map = (Map)data;
        EntityManager em = (EntityManager)getEm();
        beforeOpen(map);
        map = (Map)em.read(getSchemaName(), map);
        afterOpen(map);
        return map;
    }
     
     @ProxyMethod
     public void removeEntity(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.create parameter must be map");
        Map map = (Map)data;
        EntityManager em = (EntityManager)getEm();
        beforeRemoveEntity(map);
        em.delete(getSchemaName(), map);
        afterRemoveEntity(map);
    } 
    
}
