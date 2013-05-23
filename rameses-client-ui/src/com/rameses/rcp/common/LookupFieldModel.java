/*
 * LookupFieldModel.java
 *
 * Created on April 29, 2013, 5:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class LookupFieldModel 
{
    private Column[] columns = new Column[] { new Column("item", null) };   
    
    public LookupFieldModel() {
    }
    
    public String getDisplayName() { return null; }  
    
    public int getRows() { return 10; }
    
    public Column[] getColumns() { return this.columns; }    

    public Map getParameters() { return null; } 
    
    public Object getOpener() { return null; } 
    
    public String getTitle() { return "Lookup"; } 
    
    public void onselect(Object item) {} 
    
    public List fetchList(Map params) { 
        return new ArrayList(); 
    }     
}
