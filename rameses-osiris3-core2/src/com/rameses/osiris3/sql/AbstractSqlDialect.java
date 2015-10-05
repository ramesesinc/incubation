/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import com.rameses.osiris3.sql.SqlDialectModel.Criteria;
import com.rameses.osiris3.sql.SqlDialectModel.Field;
import com.rameses.osiris3.sql.SqlDialectModel.Relationship;
import com.rameses.osiris3.sql.SqlDialectModel.RelationshipKey;
import java.util.Map;

/**
 * @author elmo
 */
public abstract class AbstractSqlDialect implements SqlDialect {
    
    public abstract String[] getDelimiters();
    
    //Important called by PersistenceUtil methods
    public SqlUnit getCreateSqlUnit( SqlDialectModel model ) throws Exception {
        String[] delims = getDelimiters();
        StringBuilder sb = new StringBuilder();
        StringBuilder sbv = new StringBuilder();
        sb.append("INSERT INTO");
        sb.append( " " + delims[0]+ model.getBaseTable().getName() + delims[1]+ " " );
        sb.append("(");
        boolean _first = true;
        for( Field f: model.getFields() ) {
            if( !_first ) {
                sb.append(",");
                sbv.append(",");
            }
            else {
                _first = false;
            }
            sb.append( delims[0]+ f.getFieldname() + delims[1] );
            sbv.append( "$P{" + f.getName() + "}" );
        }
        sb.append( ")");
        sb.append(" VALUES ");
        sb.append("(").append( sbv.toString() ).append(")");
        return new SqlUnit(sb.toString());
    }   
    
    public SqlUnit getUpdateSqlUnit( SqlDialectModel model ) throws Exception {
        String[] delims = getDelimiters();
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append( " " + delims[0]+ model.getBaseTable().getName() + delims[1]+ " " );
        sb.append( " SET ");
        boolean _first = true;
        for( Field f: model.getFields() ) {
            if(!_first) sb.append( ",");
            else _first = false;
            sb.append( delims[0]+ f.getFieldname() + delims[1] );
            sb.append( "=" );
            sb.append( "$P{" + f.getName() + "}" );
        }
        //there must be a where statement. it is a MUST.
        sb.append( " WHERE ");
        String whereExpr = parseCriteria( model, true );
        if( whereExpr.trim().length() == 0 ) 
            throw new Exception("error getUpdateSqlUnit. where expr is expired");
        sb.append( whereExpr );
        return new SqlUnit( sb.toString() );
    }
    
    /**
     * One of the differences from getSelectSqlUnit is the  
     * @param elem - the schema element
     * @param options - options for retrieving the data
     * @return 
     * @throws Exception 
     */
    public SqlUnit getReadSqlUnit( SqlDialectModel model ) throws Exception {
        return getSelectSqlUnit(model);
    }
    
    public SqlUnit getDeleteSqlUnit( SqlDialectModel model ) throws Exception {
        StringBuilder sb = new StringBuilder();
        String[] delims = getDelimiters();
        sb.append("DELETE FROM ");
        sb.append( " " +delims[0]+ model.getBaseTable().getName() + delims[1]+" ");
        //there must be a where statement. it is a MUST.
        sb.append( " WHERE ");
        String whereExpr = parseCriteria( model, true );
        if( whereExpr.trim().length() == 0 ) 
            throw new Exception("error getDeleteSqlUnit. where expr is expired");
        sb.append( whereExpr );
        return new SqlUnit( sb.toString() );
    }
    
    public String parseCriteria( Criteria c, boolean forUpdate ) {
        String[] delims = getDelimiters();
        String expr = c.getExpr();
        for( Object mv: c.getFields().entrySet() ) {
            Map.Entry me = (Map.Entry)mv;
            String key = "@@["+me.getKey().toString()+"]";
            StringBuilder sb = new StringBuilder();
            Field f = (Field)me.getValue();
            
            //include alias only if not used for update
            if(!forUpdate) {
                sb.append( delims[0] + f.getTable().getAlias() + delims[1] );
                sb.append( "."  );
            }
            sb.append( delims[0] + f.getName() + delims[1] );
            expr = expr.replace(key, sb.toString());
        }
        return expr;
    }
    
    public String parseCriteria( SqlDialectModel model, boolean forUpdate ) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for( Criteria c: model.getCriteria() ) {
            if(  first ) first = false;
            else sb.append( " AND ");
            sb.append( parseCriteria( c, forUpdate ) );
        };    
        return sb.toString();
    }
    
    public String getOrderByStatement(SqlDialectModel model) {
        return null;
        /*
        StringBuilder sb = new StringBuilder();
        boolean _first = true;
        for(Field f: model.getFields()) {
            if(!f.isPrimary()) continue;
            if(!_first) sb.append(",");
            else _first = false;
            sb.append(f.getFieldname());
        }
        return " ORDER BY " + sb.toString();
        */ 
    }
    
    public String getSelectColumnStatement( SqlDialectModel model ) {
        String[] delims = getDelimiters();
        StringBuilder sb = new StringBuilder();
        boolean _first = true;
        for( Field f: model.getFields() ) {
            if(!_first) sb.append( ",");
            else _first = false;
            sb.append( delims[0] + f.getTable().getAlias() + delims[1] );
            sb.append( "."  );
            sb.append( delims[0] + f.getName() + delims[1] );
            if( !f.isNameAndAliasEqual() || f.getEmbeddedPrefix()!=null ) {
                sb.append( " AS ");
                sb.append( delims[0] );
                if( f.getEmbeddedPrefix()!=null ) {
                    sb.append( f.getEmbeddedPrefix() + "_" );
                }
                sb.append( f.getAlias() );
                sb.append( delims[1] );
            }
        }
        return sb.toString();
    }
    public String getSelectTableStatement( SqlDialectModel model ) {
        String[] delims = getDelimiters();
        StringBuilder sb = new StringBuilder();
        sb.append( delims[0] + model.getBaseTable().getName() + delims[1] );
        if( !model.getBaseTable().getName().equals(model.getBaseTable().getAlias()) ) {
            sb.append( " " + delims[0] + model.getBaseTable().getAlias() + delims[1] + " ");
        }
        for( Relationship r: model.getRelationships() ) {
            sb.append( " " + r.getJoinType().toUpperCase() + " JOIN " );
            sb.append(  delims[0] + r.getJoinTable().getName() +  delims[1] );
            if( !r.getJoinTable().isNameAndAliasEqual() ) {
                sb.append( " "+delims[0] +  r.getJoinTable().getAlias() + delims[1] + " ");
            }
            sb.append( " ON ");
            for(RelationshipKey rk: r.getKeys()) {
                sb.append( delims[0]+rk.getFromTable().getAlias()+delims[1]);
                sb.append( "." );
                sb.append( delims[0]+rk.getFromKey().getFieldname()+delims[1]);
                sb.append( "=" );
                sb.append( delims[0]+rk.getToTable().getAlias()+delims[1]);
                sb.append( "." );
                sb.append( delims[0]+rk.getToKey().getFieldname()+delims[1]);
            }
        }
        return sb.toString();
    }
    
    public String getWhereStatement( SqlDialectModel model ) {
        StringBuilder sb = new StringBuilder();
        String whereExpr = parseCriteria( model, false ) ;
        if( whereExpr.trim().length() > 0 ) {
            sb.append( whereExpr );
        }
        return sb.toString();
    }
    
    public String buildBasicSelectStatement( SqlDialectModel model ) throws Exception {
        String[] delims = getDelimiters();
        StringBuilder sb = new StringBuilder();
        sb.append( "SELECT " + getSelectColumnStatement(model));
        sb.append( " FROM " + getSelectTableStatement(model) );
        String whereExpr = getWhereStatement( model ) ;
        if( whereExpr.trim().length() > 0 ) {
            sb.append( " WHERE ");
            sb.append( whereExpr );
        }
        return sb.toString();
    }
    
    public SqlUnit getSelectSqlUnit( SqlDialectModel model ) throws Exception {
        String fsql = buildBasicSelectStatement(model);
        return new SqlUnit( fsql );
    }
    
}
