 /*
 * LookupCellRenderer.java
 *
 * Created on May 21, 2013, 4:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.LookupColumnHandler;

/**
 *
 * @author wflores
 */
public class LookupCellRenderer extends TextCellRenderer
{
    
    protected Object resolveValue(Object value) 
    {
        Object cellValue = value; 
        if (column.getTypeHandler() instanceof LookupColumnHandler) 
        {
            LookupColumnHandler lkp = (LookupColumnHandler) column.getTypeHandler(); 
            if (lkp.getExpression() != null) 
            {
                ExpressionResolver er = ExpressionResolver.getInstance();
                try 
                {
                    Object bean = cellValue;
                    if (table.getModel() instanceof DataTableModel)
                        bean = ((DataTableModel) table.getModel()).getItem(rowIndex);
                    
                    if (table instanceof DataTableComponent) 
                        bean = ((DataTableComponent) table).createExpressionBean(bean); 

                    cellValue = er.evalString(lkp.getExpression(), bean);
                } 
                catch(Exception e) {}
            }
        }
        return cellValue; 
    }

}
