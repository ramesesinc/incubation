/*
 * CrudModel.java
 *
 * Created on August 12, 2010, 8:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import com.rameses.osiris3.schema.SchemaField;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for building crud sql statements
 * 
 */
public class CrudModel {
    
    private String tableName;
    private String linkTable;
    private String alias;
    
    private List<CrudField> fields = new ArrayList();
    
    /** Creates a new instance of CrudModel */
    public CrudModel() {
    }
    
    
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getLinkTable() {
        return linkTable;
    }
    
    public void setLinkTable(String linkTable) {
        this.linkTable = linkTable;
    }
    
    public String getAlias() {
        return alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public List<CrudField> getFields() {
        return fields;
    }

    public CrudField addField( String name ) {
        return addField(name, null, false);
    }

    public CrudField addField( String name, String fieldName, boolean primary ) {
        CrudField cf = new CrudField();
        cf.name = name;
        if(fieldName==null) fieldName = name;
        cf.fieldName = fieldName;
        cf.primary = primary;
        fields.add( cf );
        return cf;
    }
    
    public static class CrudField {
        private String name;
        private String fieldName;
        private boolean primary;
        private boolean linked;
        private SchemaField schemaField;
        private String serializer;
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isLinked() {
            return linked;
        }

        public void setLinked(boolean linked) {
            this.linked = linked;
        }

        public SchemaField getSchemaField() {
            return schemaField;
        }

        public void setSchemaField(SchemaField schemaField) {
            this.schemaField = schemaField;
        }

        public String getSerializer() {
            return serializer;
        }

        public void setSerializer(String serializer) {
            this.serializer = serializer;
        }
        
        
    }
    
    
    
    
    
}
