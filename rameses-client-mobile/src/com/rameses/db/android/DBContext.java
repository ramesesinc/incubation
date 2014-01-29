/*
 * DBContextImpl.java
 *
 * Created on January 28, 2014, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.db.android;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class DBContext 
{
    private String databaseName;
    private SQLiteDatabase sqldb;
    
    public DBContext(String databaseName) { 
        this.databaseName = databaseName; 
    } 
    
    DBContext(SQLiteDatabase sqldb) { 
        this.sqldb = sqldb; 
    } 

    private SQLiteDatabase getSqldb() {
        if (sqldb == null) { 
            AbstractDB adb = DBManager.get(databaseName); 
            if (adb == null) throw new RuntimeException("'"+databaseName+"' database is not binded"); 
            
            sqldb = adb.getWritableDatabase(); 
        }
        return sqldb;
    }
    
    public void close() {
        if (sqldb != null) {
            sqldb.close();
        } 
        sqldb = null; 
    }
    
    public Map find(String sql, Map params) {
        List<Map> results = getListImpl(sql, params, true); 
        return results.isEmpty()? null: results.remove(0); 
    }
    
    public List<Map> getList(String sql, Map params) {
        return getListImpl(sql, params, false); 
    }
    
    public void execute(String sql, Map params) {
        SQLParser parser = new SQLParser();
        parser.parse(sql, params);
        String sql0 = parser.getSql();
        String[] names = parser.getParameterNames();
        Object[] args = new Object[names.length];
        for (int i=0; i<names.length; i++) {
            args[i] = params.get(names[i]);
        } 
        getSqldb().execSQL(sql0, args);
    }
    
    public long insert(String tableName, Map params) { 
        ContentValues cv = createContentValues(params); 
        return getSqldb().insert(tableName, null, cv);
    }
    
    public int update(String tableName, Map params, String whereClause) { 
        ContentValues cv = createContentValues(params); 
        SQLParser parser = new SQLParser(); 
        parser.parse(whereClause, params); 
        String[] names = parser.getParameterNames();
        String[] args = new String[names.length];
        for (int i=0; i<names.length; i++) {
            Object value = params.get(names[i]);
            args[i] = (value == null? null: value.toString()); 
        }         
        return getSqldb().update(tableName, cv, parser.getSql(), args);
    }

    public int delete(String tableName, Map params, String whereClause) { 
        SQLParser parser = new SQLParser(); 
        parser.parse(whereClause, params); 
        String[] names = parser.getParameterNames();
        String[] args = new String[names.length];
        for (int i=0; i<names.length; i++) {
            Object value = params.get(names[i]);
            args[i] = (value == null? null: value.toString()); 
        } 
        return getSqldb().delete(tableName, parser.getSql(), args); 
    }
    
    private List<Map> getListImpl(String sql, Map params, boolean singleOnly) {
        SQLParser parser = new SQLParser();
        parser.parse(sql, params); 
        String sql0 = parser.getSql();
        String[] names = parser.getParameterNames();
        String[] args = new String[names.length];
        for (int i=0; i<names.length; i++) {
            Object value = params.get(names[i]);
            args[0] = (value == null? null: value.toString()); 
        }
        
        List<Map> results = new ArrayList();
        Cursor cursor = null; 
        try { 
            cursor = getSqldb().rawQuery(sql0, args);
            cursor.moveToFirst(); 
            while (!cursor.isAfterLast()) { 
                results.add(createMap(cursor)); 
                if (singleOnly) break;
                
                cursor.moveToNext(); 
            } 
            return results; 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally { 
            try { cursor.close(); } catch(Throwable t){;} 
        }
    }
    
    private Map createMap(Cursor cursor) {
        Map data = new HashMap(); 
        String[] colnames = cursor.getColumnNames();
        for (int i=0; i<colnames.length; i++) {
            String name = colnames[i];
            Object value = cursor.getExtras().get(name); 
            data.put(name, value); 
        } 
        return data; 
    }
    
    private ContentValues createContentValues(Map params) {
        ContentValues cv = new ContentValues();
        if (params == null) return cv;
        
        Iterator itr = params.keySet().iterator(); 
        while (itr.hasNext()) {
            String key = itr.next().toString();
            Object val = params.get(key);
            if (val instanceof byte[]) {
                cv.put(key, (byte[])val);
            } else if (val instanceof BigDecimal) {
                cv.put(key, ((BigDecimal)val).doubleValue()); 
            } else if (val instanceof Long) {
                cv.put(key, (Long)val); 
            } else if (val instanceof Integer) {
                cv.put(key, (Integer)val);  
            } else if (val instanceof Double) {
                cv.put(key, (Double)val);  
            } else if (val instanceof Boolean) {
                cv.put(key, (Boolean)val);  
            } else {                
                cv.put(key, (String)(val==null? null: val.toString()) );
            }
        }
        return cv;
    }
}
