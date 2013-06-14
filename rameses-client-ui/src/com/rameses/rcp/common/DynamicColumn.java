/*
 * DynamicColumn.java
 *
 * Created on June 13, 2013, 11:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class DynamicColumn extends Column 
{
    private List<Column> items = new ArrayList();
    
    public DynamicColumn() {
        super();
    }

    public DynamicColumn(String name, String caption) {
        super(name, caption);
    }

    public String getType() { return "dynamic"; }
    
    public List<Column> getItems() { return items; } 
    public void setItems(List<Column> items) { 
        this.items = (items == null? new ArrayList(): items); 
    } 
}
