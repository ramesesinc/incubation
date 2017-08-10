/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.models;

import java.util.List;

/**
 *
 * @author Elmo Nazareno
 */
public interface FilterCriteriaHandler {
    
    List getFieldList();
    void add( Object field );
    void remove( Object field );
    void clear();
    
}
