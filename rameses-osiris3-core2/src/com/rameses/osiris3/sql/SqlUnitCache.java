/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 */
public class SqlUnitCache {
    
    private static Map<String, SqlUnit> sqlUnits = new HashMap();
    
    public static SqlUnit getSqlUnit( SqlDialectModel model, SqlDialect dialect ) throws Exception {
        if(! sqlUnits.containsKey(model.getId()) ) {
            String action = model.getAction();
            String statement = null;
            if(action.equals("create")) {
                statement = dialect.getCreateStatement(model);
            }
            else if( action.equals("update")) {
                statement = dialect.getUpdateStatement(model);
            }
            else if( action.equals("select")) {
                statement = dialect.getSelectStatement(model);
            }
            else if( action.equals("delete")) {
                statement = dialect.getDeleteStatement(model);
            }
            SqlUnit sqlUnit = new SqlUnit( statement );
            sqlUnits.put(model.getId(), sqlUnit);
        }
        return sqlUnits.get(model.getId());
    }
    
}
