/*
 * PersistenceContextDependencyHandler.java
 *
 * Created on January 10, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.ActiveDB;
import com.rameses.osiris3.activedb.ActiveDBTransactionManager;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.persistence.EntityManager;

import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class ActiveDBDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return ActiveDB.class;
    }
    
    public Object getResource(Annotation c, ExecutionInfo einfo) {
        ActiveDB adb = (ActiveDB)c;
        TransactionContext txn = TransactionContext.getCurrentContext();
        DataService dataSvc = txn.getContext().getService(DataService.class);
        EntityManager em = dataSvc.getEntityManager( adb.em() );
        ActiveDBTransactionManager dbm = txn.getManager( ActiveDBTransactionManager.class );
        return dbm.create( adb.value(), em );
    }
    
}
