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
import java.util.regex.Matcher;
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
        
        StringBuilder buff = new StringBuilder();
        buff.append( selectBuilder );

        boolean hasSelectTop = false; 
        String sqlSelectTop = null;      
        String sqlSelectCols = null; 
        Pattern p = Pattern.compile("(TOP[\\s]{1,}[0-9]{1,}[\\s]{1,}PERCENT).*?", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE); 
        Matcher m = p.matcher( columnBuilder );
        if ( m.find() ) {
            sqlSelectTop = m.group();
            sqlSelectCols = columnBuilder.substring( m.end() ); 
            hasSelectTop = true; 

        } else {
            p = Pattern.compile("(TOP[\\s]{1,}[0-9]{1,}[\\s]{1,}).*?", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE); 
            m = p.matcher( columnBuilder );
            if ( m.find() ) {
                sqlSelectTop = m.group();
                sqlSelectCols = columnBuilder.substring( m.end() ); 
                hasSelectTop = true; 
            } 
        } 
        
        buff.append(" ");
        if ( hasSelectTop ) { 
            buff.append( sqlSelectTop ); 
        } else if ( limit > 0 ) { 
            buff.append(" TOP "+ (limit + start)); 
        } else { 
            buff.append(" TOP 1000 "); 
        } 
        
        buff.append(" ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS _rownum_, ");
        
        if ( hasSelectTop ) {
            buff.append( sqlSelectCols ); 
        } else {
            buff.append( columnBuilder ); 
        }
        
        buff.append(" ");
        buff.append( fromBuilder ); 
        buff.append( whereBuilder ); 
        buff.append( groupBuilder ); 
        buff.append( havingBuilder ); 
        buff.append( orderBuilder ); 

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ");
        if ( limit > 0 ) {
            sb.append(" TOP " + limit); 
        } 
        sb.append(" * FROM ( ").append( buff ).append(" )xx1 "); 
        if ( start >= 0 ) { 
            sb.append(" WHERE _rownum_ > "+ start ); 
        } 
        return sb.toString();        
    }

   
    public String getSelectStatement( SqlDialectModel model )  {
        if ( model.getStart()>0 || model.getLimit()>0 ) { 
            
            StringBuilder buff = new StringBuilder();
            buff.append(" SELECT ");      
            if ( model.getLimit() > 0) { 
                buff.append("TOP ($P{_limit}+$P{_start}+1) "); 
            } else { 
                buff.append("TOP 1000 "); 
            } 
            buff.append("ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS _rownum_, ");
                        
            if ( model.getOrWhereList() == null || model.getOrWhereList().isEmpty()) {
                buff.append( buildSelectFields( model )).append(" FROM ")
                    .append( buildTablesForSelect( model ))
                    .append( buildWhereForSelect( model, null))
                    .append( buildGroupByStatement( model ))
                    .append( buildOrderStatement( model ));

            } else {
                int i = 0;
                StringBuilder union = new StringBuilder();                 
                for( SqlDialectModel.WhereFilter wf : model.getOrWhereList() ) {
                    if (i++ > 0) union.append( " UNION ");

                    union.append(" SELECT ")
                         .append( buildSelectFields( model ))
                         .append(" FROM ")
                         .append( buildTablesForSelect( model ))
                         .append( buildWhereForSelect( model, wf))
                         .append( buildGroupByStatement( model ));
                } 
                buff.append(" * FROM ( ").append( union ).append(" )t1 ")
                    .append( buildOrderStatement( model, "" ));
            } 
            
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT "); 
            if ( model.getLimit() > 0 ) {
                sb.append(" TOP " + model.getLimit() );
            }
            sb.append(" * FROM ( ").append( buff ).append(" )t2  "); 
            sb.append(" WHERE _rownum_ > $P{_start} "); 
            return sb.toString(); 
            
        } else {
             return super.getSelectStatement(model, true);
        }
    }

    public String getUpdateStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder whereBuff = new StringBuilder();
        sb.append( " UPDATE ");
        sb.append( getDelimiters()[0]+model.getTablealias()+getDelimiters()[1] );
        sb.append( " ");
        sb.append( buildUpdateFieldsStatement(model, true) );
        
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
        sb.append( getDelimiters()[0]+ model.getTablename()+getDelimiters()[1] );
        sb.append( " WHERE ");
        List<String> list = new ArrayList();
        buildFinderStatement(model, list, false);
        buildSingleWhereStatement(model, list,false);
        sb.append( concatFilterStatement(list));        
        
        return sb.toString();
    }    
}
