/*
 * DBTransaction.java
 *
 * Created on February 3, 2014, 11:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.db.android;

import android.database.sqlite.SQLiteDatabase;

/**
 *
 * @author wflores
 */
public class DBTransaction 
{
    private final static Object LOCKED = new Object();
    private DBContext dbContext; 
    private SQLiteDatabase sqldb; 
    private String dbname; 
    
    public DBTransaction(String dbname) {
        this.dbname = dbname;
    }
    
    protected SQLiteDatabase getSqldb() { 
        getContext();
        return sqldb;
    }
    
    protected DBContext getContext() {
        synchronized (LOCKED) { 
            if (dbContext == null) { 
                AbstractDB adb = DBManager.get(dbname);
                if (adb == null) throw new RuntimeException("'"+dbname+"' database is not registered");

                sqldb = adb.getWritableDatabase();
                sqldb.beginTransaction();
                dbContext = new DBContext(sqldb); 
            } 
            return dbContext; 
        }
    } 

    protected void onExecute() throws Exception {
    }
    
    protected void onError(Exception e) {
    }
    
    public final void execute() {
        try {
            getContext();
            onExecute();
            commitTransaction();
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            endTransaction();
        }
    } 
    
    private void commitTransaction() {
        getSqldb().setTransactionSuccessful(); 
    }
    
    private void endTransaction() {
        SQLiteDatabase sqldb = null; 
        try { 
            sqldb = getSqldb();
            sqldb.endTransaction(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally { 
            try { sqldb.close(); } catch(Throwable t){;} 

            dbContext = null;
            sqldb = null;            
        }
    }
}
