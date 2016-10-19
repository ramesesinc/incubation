/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;

/**
 *
 * @author Elmo Nazareno
 */
public class DATE {
    
    public String getName() {
        return "DATE";
    }

    public void addParam(String s) {
        //do nothing....
    }

    public String toString() {
        return "DATE_FORMAT(NOW(),'%Y-%m-%d' )";
    }
    
}
