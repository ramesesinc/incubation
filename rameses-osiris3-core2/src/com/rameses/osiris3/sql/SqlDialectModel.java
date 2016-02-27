/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.LinkedSchemaView;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author dell
 * This is used for the 
 */
public class SqlDialectModel {
    
    private String tablename;
    private String tablealias;
    private String action;
    
    private String selectExpression;
    private List<Field> fields = new ArrayList();
    private List<Field> finderFields;
    
    private List<Field> orderFields;
    private List<Field> groupFields;
    
    //fieldMap is a helper field
    private Map<String, Field> fieldMap = new HashMap();
    private Map<String, String> subqueries = new HashMap();

    //internal fields for finding things
    //for updating and saving
    private int start;
    private int limit;
    
    //consider removing
    private AbstractSchemaView schemaView;
    private List<AbstractSchemaView> joinedViews;
    //private Set<AbstractSchemaView> linkedViews;
    //private LinkedSchemaView linkedView;
    
    private WhereFilter whereFilter;
    
    //private List<WhereFilter> whereList;
    
    /*
     * The id should be unique per call because it will cache the sql units.
     */
     public int getId() {
        StringBuilder sb = new StringBuilder(tablealias+":"+tablename+":"+action+";");
        int i = 0;
        if(!action.equals("create")) {
            if(!ValueUtil.isEmpty(this.selectExpression)) {
                sb.append("select:"+this.selectExpression+";");
            }
            if(fields!=null && fields.size()>0) {
                i = 0;
                sb.append("fields:");
                for( Field f: this.getFields() ) {
                    if(i++>0) sb.append(",");
                    sb.append( f.getExtendedName() );
                    if(! ValueUtil.isEmpty(f.getExpr()) ) {
                        sb.append( "expr:"+f.getExpr());
                    } 
                }
                sb.append(";");
            }
            if( finderFields!=null && finderFields.size() > 0 ) {
                sb.append("finders:");
                i = 0;
                for( Field vf : finderFields ) {
                    if( i++>0 ) sb.append(",");
                    sb.append( vf.getExtendedName() );
                }
                sb.append(";");
            }
            if( getJoinedViews()!=null && getJoinedViews().size()>0 ) {
                sb.append("joinedviews:");
                i = 0;
                for( AbstractSchemaView vw : getJoinedViews() ) {
                    if( i++>0 ) sb.append(",");
                    sb.append( vw.getName()+":"+vw.getTablename() );
                }
                sb.append(";");
            }
            if( whereFilter !=null && !ValueUtil.isEmpty(whereFilter.getExpr()) ) {
                sb.append("where:");
                sb.append( whereFilter.getExpr() );
                sb.append(";");
            }
            if( this.groupFields!=null && this.groupFields.size()>0 ) {
                sb.append( "groupby:");
                i = 0;
                for( Field f: this.getGroupFields() ) {
                    if(i++>0) sb.append(",");
                    sb.append( f.getExtendedName() );
                    if(! ValueUtil.isEmpty(f.getExpr()) ) {
                        sb.append( "expr:"+f.getExpr());
                    } 
                }        
                sb.append(";");
            }
            if( this.orderFields!=null && this.orderFields.size()>0 )  {
                sb.append( "orderby:");
                i = 0;
                for( Field f: this.getOrderFields() ) {
                    if(i++>0) sb.append(",");
                    sb.append( f.getExtendedName() );
                    if(! ValueUtil.isEmpty(f.getExpr()) ) {
                        sb.append( "expr:"+f.getExpr());
                    } 
                }        
                sb.append(";");
            }
            if( start >0 || limit > 0 ) {
                sb.append("start:"+start+";");
                sb.append("limit:"+limit+";");
            }
        }
        return sb.toString().hashCode();
    }
    
    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTablealias() {
        return tablealias;
    }

    public void setTablealias(String tablealias) {
        this.tablealias = tablealias;
    }
    
    public void setFieldMap(Map<String, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }
    
    public Field findField(String name) {
        return fieldMap.get(name);
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public List<Field> getFinderFields() {
        return finderFields;
    }

    public void setFinderFields(List<Field> finderFields) {
        this.finderFields = finderFields;
        for( Field vf: finderFields ) {
            fieldMap.put(vf.getExtendedName(), vf);
        }
    }

    public Map<String, String> getSubqueries() {
        return subqueries;
    }
    
    public void setSubqueries(Map<String, String> subqueries) {
        this.subqueries = subqueries;
    }

    public WhereFilter getWhereFilter() {
        return whereFilter;
    }

    public void setWhereFilter(WhereFilter wf) {
        //this should also add whatever fields are there in the where filter
        this.whereFilter = wf;
    }

    public String getSelectExpression() {
        return selectExpression;
    }

    public void setSelectExpression(String selectExpression) {
        this.selectExpression = selectExpression;
    }

    public void addField(Field vf) {
        this.fields.add(vf);
    }
    
    public List<Field> getFields() {
        return fields;
    }
    
    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<Field> getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(List<Field> groupFields) {
        this.groupFields = groupFields;
    }

    public List<Field> getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(List<Field> orderFields) {
        this.orderFields = orderFields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public static class WhereFilter {
        
        private String expr;
        
        public WhereFilter(String expr) {
            this.expr = expr;
        }
        public String getExpr() {
            return expr;
        }
    }

    public void setJoinedViews(  List<AbstractSchemaView> vws ) {
        this.joinedViews = vws;
    }
    
    public List<AbstractSchemaView> getJoinedViews() {
        return joinedViews;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "table name : " + this.getTablename() + ";");
        sb.append( "table alias : " + this.getTablealias() + ";");
        sb.append( "fields: " + this.selectExpression );
        sb.append( "joined views \n");
        if( getJoinedViews()!=null) {
            for( AbstractSchemaView vw: getJoinedViews() ) {
                sb.append( vw.getTablename() + " " + vw.getName());
                if( vw instanceof LinkedSchemaView ) {
                    LinkedSchemaView lv = ((LinkedSchemaView)vw);
                    sb.append( " " + lv.getJointype() );

                }
                sb.append(";\n");
            }
        }
        return sb.toString();
    }
    
    
    public static class Subquery {
        private String expr;
        public String getExpr() {
            return expr;
        }
        public void setExpr(String expr) {
            this.expr = expr;
        }
    }
    
    public static class Field {
        
        private String name;
        private String tablename;
        private String tablealias;
        private String extendedName;
        private String fieldname;
        private boolean primary;
        private boolean insertable;
        private boolean updatable;
        private boolean serialized;
        private boolean basefield;
        private String sortDirection;
        
        private String expr;
        
        public String getTablename() {
            return tablename;
        }

        public void setTablename(String tablename) {
            this.tablename = tablename;
        }

        public String getTablealias() {
            return tablealias;
        }

        public void setTablealias(String tablealias) {
            this.tablealias = tablealias;
        }

        public String getFieldname() {
            return fieldname;
        }

        public void setFieldname(String fieldname) {
            this.fieldname = fieldname;
        }

        public String getExtendedName() {
            return extendedName;
        }

        public void setExtendedName(String extendedName) {
            this.extendedName = extendedName;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isInsertable() {
            return insertable;
        }

        public void setInsertable(boolean insertable) {
            this.insertable = insertable;
        }

        public boolean isUpdatable() {
            return updatable;
        }

        public void setUpdatable(boolean updatable) {
            this.updatable = updatable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSerialized() {
            return serialized;
        }

        public void setSerialized(boolean serialized) {
            this.serialized = serialized;
        }

        public boolean isBasefield() {
            return basefield;
        }

        public void setBasefield(boolean basefield) {
            this.basefield = basefield;
        }

        
        public String getExpr() {
            return expr;
        }

        public void setExpr(String expr) {
            this.expr = expr;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }
        
        
        public int hashCode() {
            if( this.extendedName == null && this.expr!=null ) {
                return this.expr.hashCode();
            }
            return extendedName.hashCode();
        }

        public boolean equals(Object obj) {
            return hashCode() == obj.hashCode();
        }

    }
    
}
