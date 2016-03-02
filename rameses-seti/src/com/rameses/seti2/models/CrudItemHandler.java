/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.models;

import java.util.List;

/**
 *
 * @author dell
 */
public interface CrudItemHandler {
    List fetchItems(String name, Object params );
    void addItem (String name, Object item);
    void removeItem( String name, Object item);
    Object openItem(String name,Object item, String colName);
    boolean beforeColumnUpdate(String name, Object item, String colName, Object newValue);
    void afterColumnUpdate(String name, Object item, String colName );
}
