 /*
 * ComboBoxCellRenderer.java
 *
 * Created on May 21, 2013, 4:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.ComboBoxColumnHandler;

/**
 *
 * @author wflores
 */
public class ComboBoxCellRenderer extends TextCellRenderer
{
    protected Object resolveValue(Object value) 
    {
        Object cellValue = value; 
        if (column.getTypeHandler() instanceof ComboBoxColumnHandler) 
        {
            ComboBoxColumnHandler cbo = (ComboBoxColumnHandler) column.getTypeHandler(); 
            if (cbo.getExpression() != null) 
            {
                ExpressionResolver er = ExpressionResolver.getInstance();
                try 
                {
                    Object bean = cellValue;
                    if (table.getModel() instanceof DataTableModel)
                        bean = ((DataTableModel) table.getModel()).getItem(rowIndex);

                    cellValue = er.evalString(cbo.getExpression(), bean);
                } 
                catch(Exception e) {}
            }
        }
        return cellValue; 
    }
}
