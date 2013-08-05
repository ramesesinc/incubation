/*
 * CrudHelper.java
 *
 * Created on August 5, 2013, 12:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import com.rameses.osiris3.persistence.EntityManager;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class CrudHelper {
    
    private EntityManager em;
    private String schemaName;
    private ICrudListener listener;
    private boolean validate = true;
    
    /** Creates a new instance of CrudHelper */
    public CrudHelper(String schemaName, String subSchemaName, EntityManager em, ICrudListener listener, boolean validate) {
        this.schemaName = schemaName;
        if(subSchemaName!=null && subSchemaName.trim().length()>0) {
            this.schemaName = this.schemaName+":"+subSchemaName;
        }
        this.em = em;
        this.listener = listener;
        this.validate = validate;
    }
    
    public Object create(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.create parameter must be map");
        Map map = (Map)data;
        listener.beforeCreate(map);
        if(validate) em.validate(schemaName, map);
        listener.afterCreate(map);
        return em.create(schemaName, map);
    }
    
    public Object update(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.update parameter must be map");
        Map map = (Map)data;
        listener.beforeUpdate(map);
        if(validate) em.validate(schemaName, map);
        listener.afterUpdate(map);
        return em.create(schemaName, map);
    }
    
     public Object open(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.open parameter must be map");
        Map map = (Map)data;
        listener.beforeOpen(map);
        map = (Map)em.read(schemaName, map);
        listener.afterOpen(map);
        return map;
    }
     
     public void removeEntity(Object data) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.removeEntity parameter must be map");
        Map map = (Map)data;
        listener.beforeRemoveEntity(map);
        em.delete(schemaName, map);
        listener.afterRemoveEntity(map);
    } 
}
