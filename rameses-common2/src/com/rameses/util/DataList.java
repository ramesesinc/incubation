/*
 * DataList.java
 *
 * Created on August 7, 2013, 4:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 * A list that also stores data definition 
 */
public class DataList extends LinkedList implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //where you can add more info
    private Map properties = new HashMap();
    private List<Map> columns; 
    
    /** Creates a new instance of DataList */
    public DataList(List list, List<Map> columns) {
        super.addAll( list );
        this.columns = columns;
    }

    public List<Map> getColumns() {
        return columns;
    }

    public Map getProperties() {
        return properties;
    }
    
    public Object getFirstResult() {
        if( size()==0 ) return new HashMap();
        return iterator().next();
    }
    
    
    
}
