/*
 * SimpleDataSource.java
 *
 * Created on July 22, 2010, 10:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author elmo
 */
public class SimpleDataSource implements DataSource {
    
    private String driverClass;
    private String user;
    private String pwd;
    private String url;
    
    public SimpleDataSource(Map map) {
        this( (String)map.get("driverClass"),
                (String)map.get("url"),
                (String)map.get("user"),
                (String)map.get("pwd"));
    }
    
    public SimpleDataSource(String driverClass,String url, String user,String pwd) {
        this.driverClass = driverClass;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        try { 
            Class.forName(driverClass); 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,pwd);
    }
    
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }
    
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }
    
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }
    
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }
    
    
    public <T extends Object> T unwrap(Class<T> T) throws SQLException {
        throw new SQLException("unwrap not supported");
    }
    
    public boolean isWrapperFor(Class<?> T) throws SQLException {
        throw new SQLException("isWrapperFor not supported");
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
