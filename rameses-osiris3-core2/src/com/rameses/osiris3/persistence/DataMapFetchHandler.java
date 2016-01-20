/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaUtil;
import com.rameses.osiris3.sql.FetchHandler;
import com.rameses.osiris3.sql.FieldToMap;
import com.rameses.util.ObjectDeserializer;

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
public class DataMapFetchHandler implements FetchHandler {

    private List columns;
    private Map<String, ObjectDeserializer> serializers = new HashMap();
    
    public DataMapFetchHandler(List cols) {
        this.columns = cols;
    }
    
    public List start() {
        Map schema = new HashMap();
        schema.put("columns", columns);
        for( Object c: columns ) {
            Map m = (Map)c;
            if( m.containsKey("serializer")) {
                serializers.put(m.get("name").toString(), new ObjectDeserializer());
            }
        };      
        return new ArrayList();
    }

    public void end() {;}
    
    public Object getObject(ResultSet rs) throws Exception {
        Map data = new HashMap();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        for (int i=0; i<columnCount; i++) {
            String name = meta.getColumnName(i+1);
            Object val = rs.getObject(name);
            if( val!=null && (val instanceof String) && serializers.containsKey(name)) {
                val = serializers.get(name).read(val.toString());
            }
            DataUtil.putData(data, name, val);
            //data.put(name, val);
        };
        return data;
        //return FieldToMap.convert(data);
    }
    
}
