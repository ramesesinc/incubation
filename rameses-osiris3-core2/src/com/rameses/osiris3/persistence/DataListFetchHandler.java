/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.sql.FetchHandler;
import com.rameses.osiris3.sql.FieldToMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public class DataListFetchHandler implements FetchHandler {

    private List columns;
    
    public DataListFetchHandler(List cols) {
        this.columns = cols;
    }
    
    public List start() {
        Map schema = new HashMap();
        schema.put("columns", columns);
        return new ArrayList();
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
        return FieldToMap.convert(data);
    }
    
}
