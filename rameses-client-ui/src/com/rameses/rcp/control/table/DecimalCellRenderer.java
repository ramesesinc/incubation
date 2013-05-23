/*
 * DecimalRenderer.java
 *
 * Created on May 21, 2013, 4:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import java.math.BigDecimal;

/**
 *
 * @author wflores
 */
public class DecimalCellRenderer extends AbstractNumberCellRenderer 
{
    protected String getFormattedValue(Column c, Object value) 
    {
        Number num = null;
        if (value == null) { 
            /* do nothing */ 
        } 
        else if (value instanceof BigDecimal) {
            num = (BigDecimal) value;
        } 
        else {
            try {
                num = new BigDecimal(value.toString());
            } catch(Exception e) {}
        }
        
        if (num == null) return null;
        
        return formatValue(num, c.getFormat(), "#,##0.00");
    }
}
