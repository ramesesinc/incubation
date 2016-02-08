/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.LinkedSchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
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
    private List<SchemaViewField> fields = new ArrayList();
    private List<SchemaViewField> finderFields;
    
    //fieldMap is a helper field
    private Map<String, SchemaViewField> fieldMap = new HashMap();
    private Map<String, String> subqueries = new HashMap();

    //internal fields for finding things
    //for updating and saving
    
    private String orderExpr;

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
     public String getId() {
        StringBuilder sb = new StringBuilder(tablealias+":"+tablename+":"+action+";");
        int i = 0;
        if(!action.equals("create")) {
            if(!ValueUtil.isEmpty(this.selectExpression)) {
                sb.append("select:"+this.selectExpression+";");
            }
            if(fields.size()>0) {
                i = 0;
                sb.append("fields:");
                for( SchemaViewField f: this.getFields() ) {
                    if(i++>0) sb.append(",");
                    sb.append( f.getExtendedName() );
                }
                sb.append(";");
            }
            if( finderFields!=null && finderFields.size() > 0 ) {
                sb.append("finders:");
                i = 0;
                for( SchemaViewField vf : finderFields ) {
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
            if(!ValueUtil.isEmpty(orderExpr)) {
                sb.append("orderby:"+this.orderExpr+";");
            }
            if( start >0 || limit > 0 ) {
                sb.append("start:"+start+";");
                sb.append("limit:"+limit+";");
            }
        }
        return sb.toString();
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
    
    public void setFieldMap(Map<String, SchemaViewField> fieldMap) {
        this.fieldMap = fieldMap;
    }
    
    public SchemaViewField findField(String name) {
        return fieldMap.get(name);
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /*
    public LinkedSchemaView getLinkedView() {
        return linkedView;
    }

    public void setLinkedView(LinkedSchemaView linkedView) {
        this.linkedView = linkedView;
    }
    */ 
    
    /*
    public Set<AbstractSchemaView> getLinkedViews() {
        return linkedViews;
    }

    public void setLinkedViews(Set<AbstractSchemaView> linkedViews) {
        this.linkedViews = linkedViews;
    }
    */ 

    public List<SchemaViewField> getFinderFields() {
        return finderFields;
    }

    public void setFinderFields(List<SchemaViewField> finderFields) {
        this.finderFields = finderFields;
        for( SchemaViewField vf: finderFields ) {
            fieldMap.put(vf.getExtendedName(), vf);
        }
    }

    /*
    public List<WhereFilter> getWhereList() {
        return whereList;
    }

    public void setWhereList(List<WhereFilter> whereList) {
        this.whereList = whereList;
    }
    */ 

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

    public void addField(SchemaViewField vf) {
        this.fields.add(vf);
    }
    
    public List<SchemaViewField> getFields() {
        return fields;
    }
    
    public Map<String, SchemaViewField> getFieldMap() {
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

    public String getOrderExpr() {
        return orderExpr;
    }

    public void setOrderExpr(String orderExpr) {
        this.orderExpr = orderExpr;
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
    
}
