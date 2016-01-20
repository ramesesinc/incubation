/*
 * MySqlDialect.java
 *
 * Created on April 30, 2012, 10:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.sql.dialect;


import com.rameses.osiris3.sql.AbstractSqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlUnit;


/**
 *
 * @author Elmo
 */
public class MySqlDialect extends AbstractSqlDialect {
    
    public String getName() {
        return "mysql";
    }
    
    public String[] getDelimiters() {
        return new String[]{"`","`"};
    }
    
    public String getPagingStatement(String sql, int start, int limit, String[] pagingKeys) {
        return sql + " LIMIT " + start + "," + limit;
    }

    public String buildBasicSelectStatement(SqlDialectModel model) throws Exception {
        String s = super.buildBasicSelectStatement(model);
        if(model.getStart() >= 0 && model.getLimit()>0 ) {
            s += " LIMIT $P{_start}, $P{_limit}" ;
        }
        return s;
    }


}
