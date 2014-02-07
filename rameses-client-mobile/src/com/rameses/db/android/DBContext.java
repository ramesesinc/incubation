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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author wflores 
 */
public class DBContext 
{
    private final static int QUERY_SINGLE_RESULT    = 1;
    private final static int QUERY_LIST_RESULT      = 2;
    private final static int QUERY_COUNT            = 4;
    
    private String databaseName;
    private SQLiteDatabase sqldb;
    private boolean hasClosed;
    
    public DBContext(String databaseName) { 
        this.databaseName = databaseName; 
    } 
    
    DBContext(SQLiteDatabase sqldb) { 
        this.sqldb = sqldb; 
    } 

    private SQLiteDatabase getSqldb() {
        if (sqldb == null && !hasClosed) { 
            AbstractDB adb = DBManager.get(databaseName); 
            if (adb == null) throw new RuntimeException("'"+databaseName+"' database is not binded"); 
            
            sqldb = adb.getWritableDatabase(); 
        }
        return sqldb;
    }
    
    public boolean isClosed() { return hasClosed; } 
    
    public void close() {
        try { 
            if (sqldb != null) { 
                if (sqldb.inTransaction()) {
                    //we will let the one who invoke the begintransaction to do the closing
                    return;
                } 
                
                sqldb.close(); 
            } 
            sqldb = null; 
            hasClosed = true; 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    public String showCreateTable(String name) {
        String sql = "SELECT * FROM sqlite_master WHERE type='table' and name=?";
        Map data = find(sql, new String[]{name});
        Object ov = (data == null? null: data.get("sql"));
        return (ov == null? null: ov.toString()); 
    } 
    
    public Map find(String sql, Map params) {
        List<Map> results = getListImpl(sql, params, QUERY_SINGLE_RESULT); 
        return results.isEmpty()? null: results.remove(0); 
    }
    
    public Map find(String sql, Object[] params) {
        List<Map> results = getListImpl(sql, params, QUERY_SINGLE_RESULT); 
        return results.isEmpty()? null: results.remove(0); 
    } 
    
    public List<Map> getList(String sql, Map params) {
        return getListImpl(sql, params, QUERY_LIST_RESULT); 
    }
    
    public List<Map> getList(String sql, Object[] params) {
        return getListImpl(sql, params, QUERY_LIST_RESULT); 
    }    
    
    public int getCount(String sql, Object[] params) {
        List<Map> results = getListImpl(sql, params, QUERY_COUNT); 
        Map map = (results.isEmpty()? null: results.remove(0));
        try { 
            return Integer.parseInt(map.get("COUNT").toString()); 
        } catch(Throwable t) {
            return 0;
        }
    }
    
    public int getCount(String sql, Map params) {
        List<Map> results = getListImpl(sql, params, QUERY_COUNT); 
        Map map = (results.isEmpty()? null: results.remove(0));
        try { 
            return Integer.parseInt(map.get("COUNT").toString()); 
        } catch(Throwable t) {
            return 0;
        }
    }    
    
    public void execute(String sql) {
        getSqldb().execSQL(sql); 
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
    
    public int update(String tableName, Object[] args, String whereClause) { 
        String[] values = new String[(args == null? 0: args.length)];
        for (int i=0; i<values.length; i++) {
            values[i] = (args[i] == null? null: args[i].toString()); 
        } 
        return getSqldb().update(tableName, null, whereClause, values);
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
    
    public int delete(String tableName, Object[] args, String whereClause) { 
        String[] values = new String[(args == null? 0: args.length)];
        for (int i=0; i<values.length; i++) {
            values[i] = (args[i] == null? null: args[i].toString()); 
        } 
        return getSqldb().delete(tableName, whereClause, values);
    }
    
    private List<Map> getListImpl(String sql, Object params, int queryOption) {
        SQLParser parser = new SQLParser();
        if (params instanceof Map) {
            parser.parse(sql, (Map) params); 
        }

        String[] args = new String[0];
        if (params instanceof String[]) {
            args = (String[]) params;
            
        } else if (params instanceof Object[]) {
            Object[] arrays = (Object[]) params;
            args = new String[arrays.length];
            for (int i=0; i<arrays.length; i++) {
                args[i] = (arrays[i]==null? null: arrays[i].toString()); 
            }
            
        } else if (params instanceof Map) {
            Map map = (Map) params;
            String[] names = parser.getParameterNames(); 
            args = new String[names.length];
            sql = parser.getSql();
            for (int i=0; i<names.length; i++) {
                Object value = map.get(names[i]);
                args[i] = (value == null? null: value.toString()); 
            }
        }
        
        List<Map> results = new ArrayList();
        Cursor cursor = null; 
        try { 
            cursor = getSqldb().rawQuery(sql, args);
            if (queryOption == QUERY_COUNT) {
                Map m = new Properties();
                m.put("COUNT", cursor.getCount()); 
                results.add(m);
                return results;
            }
            
            cursor.moveToFirst(); 
            int counter = 0;
            while (!cursor.isAfterLast()) { 
                results.add(createMap(cursor)); 
                if (queryOption == QUERY_SINGLE_RESULT) break;
                
                cursor.moveToNext(); 
                counter++;
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
    
    private Properties createMap(Cursor cursor) {
        Properties data = new Properties(); 
        String[] colnames = cursor.getColumnNames();
        for (int i=0; i<colnames.length; i++) {
            int type = cursor.getType(i);            
            String name = colnames[i];
            Object value = null;
            if (type == Cursor.FIELD_TYPE_STRING) {
                value = cursor.getString(i); 
            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                value = cursor.getInt(i);
            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                value = cursor.getDouble(i); 
            } else if (type == Cursor.FIELD_TYPE_BLOB) {
                value = cursor.getBlob(i); 
            } else if (type == Cursor.FIELD_TYPE_NULL) {
                value = null;
            } else {
                value = cursor.getString(i); 
            }
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
