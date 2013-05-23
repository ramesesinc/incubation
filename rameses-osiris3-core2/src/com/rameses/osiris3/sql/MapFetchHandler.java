/*
 * MapResultHandler.java
 *
 * Created on July 21, 2010, 8:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elmo
 */
public class MapFetchHandler implements FetchHandler {
    
    private String excludeFields;
    private boolean fieldToMap = true;
    
    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    public void setFieldToMap(boolean fieldToMap) {
        this.fieldToMap = fieldToMap;
    }
    
    public void start() {
    }

    public void end() {
    }
    
    public Object getObject(ResultSet rs) throws Exception {
        Map data = new HashMap();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        for (int i=0; i<columnCount; i++) {
            String name = meta.getColumnName(i+1);
            data.put(name, rs.getObject(name));
        }
        if( fieldToMap == true ) {
            return FieldToMap.convert(data, excludeFields);
        }
        else {
           return data; 
        }
    }

    

    
    
}
