/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

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
        sb.append("DATEADD( ");
        if ( params.size() == 2 ) {
            String arg0 = params.get(0); 
            String arg1 = params.get(1); 
            String[] arr = (arg1==null? "" : arg1).replaceAll("\\s{1,}", " ").trim().split(" ");
            sb.append( arr.length>2 ? arr[2] : " ").append(",");
            sb.append( arr.length>1 ? arr[1] : " ").append(",");
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
