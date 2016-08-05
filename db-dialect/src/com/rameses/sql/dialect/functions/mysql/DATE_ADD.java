/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;

import com.rameses.sql.dialect.functions.mssql.*;
import com.rameses.osiris3.sql.SqlDialectFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dell
 */
public class DATE_ADD implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "DATE_ADD";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append("DATE_ADD( ");
        if ( params.size() == 3 ) {
            String arg0 = params.get(0); 
            String arg1 = params.get(1); 
            String arg2 = params.get(2); 
            sb.append( arg2 == null? " " : arg2 ).append(", INTERVAL ");
            sb.append( arg1 == null? " " : arg1 ).append(" ");
            sb.append( arg0 == null? " " : arg0 );
        } else {
            for (int i=0; i<params.size(); i++) {
                if ( i > 0 ) sb.append(","); 
                
                sb.append( params.get(i) ); 
            }
        } 
        sb.append(")"); 
        return sb.toString();
    }
}
