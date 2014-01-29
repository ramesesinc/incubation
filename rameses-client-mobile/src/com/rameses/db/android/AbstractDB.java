/*
 * AbstractDB.java
 *
 * Created on January 28, 2014, 11:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.db.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author wflores
 */
public abstract class AbstractDB extends SQLiteOpenHelper
{
    private String databaseName;
    private int databaseVersion;
    
    public AbstractDB(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion); 
        this.databaseName = databaseName;
        this.databaseVersion = databaseVersion;
        DBManager.bind(databaseName, this); 
    }
    
    public final String getName() { return databaseName; } 
    public final int getVersion() { return databaseVersion; }
    
    protected DBContext createDBContext(SQLiteDatabase sqldb) {
        return new DBContext(sqldb); 
    } 
    
}
