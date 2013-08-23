/*
 * SchemaCrudBuilder.java
 *
 * Created on August 14, 2010, 6:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.osiris3.sql.AbstractSqlTxn;
import com.rameses.osiris3.sql.CrudModel;
import com.rameses.osiris3.sql.CrudSqlBuilder;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlUnit;

/**
 *
 * This class is used for persistence
 */
public class DeletePersistenceHandler extends AbstractPersistenceHandler {
  
    public DeletePersistenceHandler(SchemaManager schemaManager, SqlContext context, Object rootData) {
        super(schemaManager, context, rootData);
    }
    
    protected String getAction() {
        return "delete";
    }

    protected SqlUnit getSqlUnit(CrudModel model) {
        return getCrudSqlBuilder().getDeleteSqlUnit(model);
    }

    protected AbstractSqlTxn getSqlTransaction(String name) {
        return sqlContext.createNamedExecutor(name);
    }
    
    public void processField(SimpleField sf, String refname, Object value) {
        if(!stack.empty()) {
            DbElementContext dbec = stack.peek();
            String sname = dbec.correctName( sf.getName() );
            SqlExecutor se = (SqlExecutor)dbec.getSqlTxn();
            if( se.getParameterNames().indexOf(sname)>=0 ) {
                se.setParameter( sname, value );
            }
        }
    }
    
    
}
