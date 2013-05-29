/*
 * IntegerRenderer.java
 *
 * Created on May 21, 2013, 4:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.IntegerColumnHandler;

/**
 *
 * @author wflores
 */
public class IntegerCellRenderer extends AbstractNumberCellRenderer 
{
    protected String getFormattedValue(Column c, Object value) 
    {
        Number num = null;
        if (value == null) { 
            /* do nothing */ 
        } 
        else if (value instanceof Integer) {
            num = (Integer) value;
        } 
        else 
        {
            try {
                num = new Integer(value.toString());
            } catch(Exception e) {}
        }
        
        if (num == null) return null;
        
        String format = null; 
        if (c.getTypeHandler() instanceof IntegerColumnHandler) 
            format = ((IntegerColumnHandler) c.getTypeHandler()).getFormat();
        
        if (format == null || format.length() == 0) return num.toString();
        
        return formatValue(num, c.getFormat(), "0");
    }
}