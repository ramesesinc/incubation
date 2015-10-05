/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.common;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author dell
 * This is used for loading lists from the database.
 * The schema contains list of columns and their values
 */
public class DataList extends ArrayList {
    
    public Map _schema;

    public DataList( Map schema ) {
        this._schema = schema;
    }
    
}
