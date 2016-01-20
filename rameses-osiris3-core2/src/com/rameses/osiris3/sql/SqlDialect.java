/*
 * SqlDialect.java
 *
 * Created on April 30, 2012, 9:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

/**
 *
 * @author Elmo.
 * added new methods ->
 */
public interface SqlDialect {
    String getName();
    String getPagingStatement( String sql, int start, int limit, String [] pagingKeys  );
    
    SqlUnit getCreateSqlUnit( SqlDialectModel model ) throws Exception;
    SqlUnit getUpdateSqlUnit( SqlDialectModel model ) throws Exception;
    SqlUnit getReadSqlUnit( SqlDialectModel model ) throws Exception;
    SqlUnit getDeleteSqlUnit( SqlDialectModel model ) throws Exception;
    SqlUnit getSelectSqlUnit( SqlDialectModel model ) throws Exception;
    
}
