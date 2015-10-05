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

/**
 *
 * @author Elmo
 */
public class PostgreSqlDialect extends AbstractSqlDialect {
    
    public String getName() {
        return "pgsql";
    }
    
    public String[] getDelimiters() {
        return new String[]{"[","]"};
    }
    
    public String getPagingStatement(String sql, int start, int limit, String[] pagingKeys) {
        return sql + " LIMIT " + limit + " OFFSET " + start;
    }


    
    
}
