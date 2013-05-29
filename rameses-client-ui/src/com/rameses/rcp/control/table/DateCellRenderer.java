 /*
 * DateCellRenderer.java
 *
 * Created on May 21, 2013, 4:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.DateColumnHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author wflores
 */
public class DateCellRenderer extends TextCellRenderer
{
    private SimpleDateFormat outputFormatter;
    
    protected Object resolveValue(Object value) 
    {
        Object cellValue = value; 
        if (column.getTypeHandler() instanceof DateColumnHandler) 
        {
            DateColumnHandler h = (DateColumnHandler) column.getTypeHandler(); 
            if (h.getOutputFormat() != null && value instanceof Date) 
            {
                try 
                {
                    if (outputFormatter == null) outputFormatter = new SimpleDateFormat(h.getOutputFormat()); 
                    
                    outputFormatter.format((Date) value); 
                }
                catch(Exception ex) {
                    
                }
            }
        }
        return cellValue; 
    }     
}
