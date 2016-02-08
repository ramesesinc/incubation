/*
 * MsSqlDialect.java
 *
 * Created on April 30, 2012, 8:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.sql.dialect;



import com.rameses.osiris3.sql.AbstractSqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import java.util.ArrayList;
import java.util.List;



import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 *
 * @author Elmo
 * implementing paging routine for mssql server
 */
public class MsSqlDialect extends AbstractSqlDialect  {
    
    private static final Pattern FN_PATTERN = Pattern.compile("[a-zA-Z]\\w+\\(.*?\\)");
    
    public String getName() {
        return "mssql";
    }
    
    public String[] getDelimiters() {
        return new String[]{"[","]"};
    }
    
    public String getPagingStatement(String sql, int start, int limit, String[] pagingKeys) {
        try {
            return doParse(sql, start, limit, pagingKeys);
        }
        catch(Exception e) {
            System.out.println("=== error parsing statement ===\n" + sql + "===========");
            throw new RuntimeException(e);
        }
    }

    private String doParse(String sql, int start, int limit, String[] pagingKeys) {
        
        String ids = "objid";
        if( pagingKeys !=null && pagingKeys.length>0) {
            boolean firstTime = true;
            StringBuilder keys = new StringBuilder();
            for( String s: pagingKeys) {
                if(!firstTime) 
                    keys.append("+");
                else 
                    firstTime = false;
                keys.append( s );
            }
            ids = keys.toString();
        }
         

        int STATE_SELECT = 0;
        int STATE_COLUMNS = 1;
        int STATE_FROM = 2;
        int STATE_WHERE = 3;
        int STATE_GROUP = 4;
        int STATE_HAVING = 5;
        int STATE_ORDER = 6;

        StringBuilder selectBuilder = new StringBuilder();
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder fromBuilder = new StringBuilder();
        StringBuilder whereBuilder = new StringBuilder();
        StringBuilder groupBuilder = new StringBuilder();
        StringBuilder havingBuilder = new StringBuilder();
        StringBuilder orderBuilder = new StringBuilder();

        StringBuilder currentBuilder = null;
        Stack stack = new Stack();
        int currentState = STATE_SELECT;
        boolean hasDistinct = false;
        
        StringTokenizer st = new StringTokenizer(sql.trim());
        while(st.hasMoreElements()) {
            String s = (String)st.nextElement(); 
            if( s.equalsIgnoreCase("select") && currentState <= STATE_SELECT  ) 
            {
                selectBuilder.append( s  );
                currentBuilder = columnBuilder;
                currentState = STATE_COLUMNS;
            }
            else if( s.equalsIgnoreCase("distinct")) {
               selectBuilder.append( " DISTINCT " );
            }
            else if( s.equalsIgnoreCase("from") && currentState == STATE_COLUMNS && stack.empty()  ) 
            {
                currentBuilder = fromBuilder;
                currentBuilder.append( " " + s );
                currentState = STATE_FROM;
            } 
            else if( s.equalsIgnoreCase("where") && currentState == STATE_FROM && stack.empty()) 
            {
                currentBuilder = whereBuilder;
                currentBuilder.append( " " + s );
                currentState = STATE_WHERE;
            }
            else if( s.equalsIgnoreCase("group") && currentState <= STATE_WHERE && currentState != STATE_COLUMNS && stack.empty() ) 
            {
                currentBuilder = groupBuilder;
                currentBuilder.append( " " + s );
                currentState = STATE_GROUP;
            }
            else if( s.equalsIgnoreCase("having") && currentState <= STATE_GROUP && currentState != STATE_COLUMNS && stack.empty() ) 
            {
                currentBuilder = havingBuilder;
                currentBuilder.append( " " + s );
                currentState = STATE_HAVING;
            }
            //else if( s.equalsIgnoreCase("order") && currentState <= STATE_HAVING && currentState != STATE_COLUMNS && stack.empty() ) 
            else if( s.equalsIgnoreCase("order") ) 
            {
                currentBuilder = orderBuilder;
                currentBuilder.append( " " + s );
                currentState = STATE_ORDER;
            }
            else if(s.equals("(") || s.trim().startsWith("(") || s.trim().endsWith("(")) 
            {
                if( currentState != STATE_WHERE ) {
                    stack.push(true);
                }
                currentBuilder.append( " " + s );
            }
            else if(s.equals(")") || s.trim().startsWith(")") || s.trim().endsWith(")")) 
            {
                if( currentState != STATE_WHERE && !FN_PATTERN.matcher(s).matches() ) {
                    stack.pop();
                }
                currentBuilder.append( " " + s );
            }
            else {
                currentBuilder.append( " " + s );
            }
        }

        /*
        String orderBy = "ORDER BY (SELECT NULL)";
        if(orderBuilder.length()>0) {
            orderBy = orderBuilder.toString();
        }
        //System.out.println("start "+(start+1)+" to "+(start+limit));
        StringBuilder sresult = new StringBuilder();
        sresult.append("SELECT * FROM (");
        sresult.append(selectBuilder);
        sresult.append(" ROW_NUMBER() OVER (" + orderBy + ") AS _rownum_,"  );
        sresult.append(columnBuilder);
        sresult.append(" " + fromBuilder);
        sresult.append(" " + whereBuilder);
        sresult.append(" " + groupBuilder);
        sresult.append(" " + havingBuilder);
        sresult.append(") AS ConstrainedResult ");
        sresult.append(" WHERE _rownum_ BETWEEN ");
        sresult.append( (start+1) + " AND " + (start+limit));
        */
        
        StringBuilder sresult = new StringBuilder();
        sresult.append(selectBuilder);
        sresult.append( " top " + limit + " ");
        sresult.append(columnBuilder);
        sresult.append(fromBuilder);
        if( whereBuilder.length() == 0 ) {
            sresult.append(" where ");
        }
        else {
            sresult.append(" " + whereBuilder);
            sresult.append(" and ");
        }
        sresult.append( " " + ids + " not in ");

        sresult.append("( select top " +  start + " " + ids + " ");
        sresult.append( " " + fromBuilder);
        sresult.append( " " + whereBuilder);
        sresult.append( " " + groupBuilder);
        sresult.append( " " + orderBuilder);
        sresult.append( " )" );
        sresult.append( " " + groupBuilder);
        sresult.append( " " + orderBuilder);
        
        if( "true".equals((System.getProperty("app.debugMode")+"").toLowerCase()) ) {
            System.out.println("mssql dialect debug: ");
            System.out.println( sresult );
        }
        return sresult.toString();
    }

   
    public String getSelectStatement( SqlDialectModel model )  {
        if(model.getStart() > 0 || model.getLimit()>0 ) {
            String orderBy = super.buildOrderStatement(model);
            if( orderBy == null || orderBy.trim().length() == 0 ) {
                orderBy = " ORDER BY (SELECT NULL) "; 
            }
            StringBuilder sb = new StringBuilder();
            sb.append( "SELECT * FROM (");
            sb.append("SELECT ROW_NUMBER() OVER ("+ orderBy + ") AS _rownum_," );
            sb.append( super.buildSelectFields(model) );
            sb.append( " FROM " );
            sb.append(buildTablesForSelect(model));
            sb.append(buildWhereForSelect(model));
            sb.append(") AS ConstrainedResult ");
            sb.append(" WHERE _rownum_ BETWEEN ($P{_start}+1) AND ($P{_start}+$P{_limit}) ORDER BY _rownum_" );
            return sb.toString();
        }
        else {
             return super.getSelectStatement(model);
        }
    }

    public String getUpdateStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder whereBuff = new StringBuilder();
        sb.append( " UPDATE ");
        sb.append( model.getTablealias() );
        sb.append( " ");
        sb.append( buildUpdateFieldsStatement(model, false) );
        
        sb.append( " FROM " );
        sb.append( buildListTablesForUpdate(model));
        
        sb.append( " WHERE " );
        List<String> list = new ArrayList();
        buildJoinTablesForUpdate(model, list);
        buildFinderStatement(model, list, true);
        buildSingleWhereStatement(model, list, true);
        sb.append( concatFilterStatement(list));        

        return sb.toString();
    }

    public String getDeleteStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        sb.append( " DELETE FROM ");
        sb.append( model.getTablename() );
        sb.append( " WHERE ");
        List<String> list = new ArrayList();
        buildFinderStatement(model, list, false);
        buildSingleWhereStatement(model, list,false);
        sb.append( concatFilterStatement(list));        
        
        return sb.toString();
    }
    
    
}
