/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.models;

import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public interface SubItemListener {
    
    List getColumns(String name, Map subSchema);
    List fetchItems(String name, Map subSchema, Object params );
    Map createItem(String name, Map subSchema);
    
    //individual items 
    void addItem (String name, Object item);
    void removeItem( String name, Object item);
    Object openItem(String name,Object item, String colName);
    boolean beforeColumnUpdate(String name, Object item, String colName, Object newValue);
    void afterColumnUpdate(String name, Object item, String colName );
    
}
