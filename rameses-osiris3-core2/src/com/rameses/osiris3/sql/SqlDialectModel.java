/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 *
 * @author dell
 * This is used for the 
 */
public class SqlDialectModel {
    
    private Table baseTable;
    private List<SqlDialectModel.Field> fields = new ArrayList();
    private Set<SqlDialectModel.Relationship> relationships = new LinkedHashSet();
    private List<Criteria> criteria = new ArrayList();
    private List<SqlDialectModel.OrderKey> orderKeys = new ArrayList();

    private long start;
    private long limit;
    
    public Table getBaseTable() {
        return baseTable;
    }

    public void setBaseTable(Table bastTable) {
        this.baseTable = bastTable;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
    
    public static class Table {
        private String name;
        private String alias;

        public Table(String name, String alias) {
            if(name==null) {
                this.name = alias;
                this.alias = alias;
            }
            else if( alias == null ) {
                this.name = name;
                this.alias = name;
            }
            else {
                this.name = name;
                this.alias = alias;
            }
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public boolean equals(Object obj) {
            return hashCode()  == obj.hashCode();
        }

        public int hashCode() {
            String alias = (this.alias==null) ? this.name : this.alias;
            return (this.name + ":" + alias).hashCode();
        }
        
        //if the name and alias is the same then this value should be true
        public boolean isNameAndAliasEqual() {
            if( alias == null ) return false;
            return name.equals(alias);
        }
    }
    
    public static class RelationshipKey {
        private Table fromTable;
        private Field fromKey;
        
        private Table toTable;
        private Field toKey;

        public Table getFromTable() {
            return fromTable;
        }

        public void setFromTable(Table fromTable) {
            this.fromTable = fromTable;
        }

        public Field getFromKey() {
            return fromKey;
        }

        public void setFromKey(Field fromKey) {
            this.fromKey = fromKey;
        }

        public Table getToTable() {
            return toTable;
        }

        public void setToTable(Table toTable) {
            this.toTable = toTable;
        }

        public Field getToKey() {
            return toKey;
        }

        public void setToKey(Field toKey) {
            this.toKey = toKey;
        }

    }
    
    public static class Field {
        private String name;        //used to target the variable
        private String fieldname;   //used to target the fieldname;
        private String alias;
        private Table table;
        private boolean primary;
        private String embeddedPrefix;
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            if(this.fieldname==null) this.fieldname = name;
        }

        public String getFieldname() {
            return fieldname;
        }

        public void setFieldname(String fieldname) {
            this.fieldname = fieldname;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Table getTable() {
            return table;
        }

        public void setTable(Table table) {
            this.table = table;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }
        
        public boolean isNameAndAliasEqual() {
            if( alias == null ) return false;
            return fieldname.equals(alias);
        }


        public String getEmbeddedPrefix() {
            return embeddedPrefix;
        }

        public void setEmbeddedPrefix(String embeddedPrefix) {
            this.embeddedPrefix = embeddedPrefix;
        }
    }

    public static class Criteria {
        
        private Map<String, Field> fields = new HashMap();
        private String expr;

        public void addField(String name, Field field) {
            this.fields.put(name, field);
        }
        
        public Map<String, Field> getFields() {
            return fields;
        }

        public String getExpr() {
            return expr;
        }

        public void setExpr(String expr) {
            this.expr = expr;
        }
        
    }
    
    public static class Relationship  {
        private String joinType;
        private Table joinTable;
        private List<RelationshipKey> keys = new ArrayList();

        public List<RelationshipKey> getKeys() {
            return keys;
        }
        
        public void addKey(RelationshipKey rk) {
            this.keys.add(rk);
        }

        public String getJoinType() {
            return joinType;
        }

        public void setJoinType(String joinType) {
            this.joinType = joinType;
        }

        public Table getJoinTable() {
            return joinTable;
        }

        public void setJoinTable(Table joinTable) {
            this.joinTable = joinTable;
        }

        public boolean equals(Object obj) {
            return this.hashCode() == obj.hashCode();
        }

        public int hashCode() {
            StringBuilder sb = new StringBuilder();
            for( RelationshipKey rk : keys) {
                sb.append( rk.getFromTable().getAlias()+"."+rk.getFromKey().getName());
                sb.append( "=");
                sb.append( rk.getToTable().getAlias()+"."+rk.getToKey().getName());
            }
            return sb.toString().hashCode();
        }
    }
    
    public static class OrderKey {
        private Field field;
        private String direction = "ASC";

        public Field getField() {
            return field;
        }
        public void setField(Field field) {
            this.field = field;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }
    
    public List<SqlDialectModel.Field> getFields() {
        return fields;
    }
    
    public Set<SqlDialectModel.Relationship> getRelationships() {
        return relationships;
    }
    
    public List<OrderKey> getOrderKeys() {
        return orderKeys;
    }
    
    public void addField(Field f) {
        fields.add(f);
    }
    
    public void addRelationship(Relationship r) {
        relationships.add(r);
    }
    
    public void addCriteria( Criteria criteria ) {
        this.criteria.add(criteria);
    }
    
    public List<Criteria> getCriteria() {
        return criteria;
    }
    
    public void addOrderKey( OrderKey key ) {
        this.orderKeys.add(key);
    }
    
}
