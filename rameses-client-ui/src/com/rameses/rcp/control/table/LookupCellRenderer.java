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
import java.util.Map;

/**
 *
 * @author wflores
 */
public class LookupCellRenderer extends StringCellRenderer
{
    
    protected Object resolveValue(Object value) 
    {
        Object cellValue = value;        
        if (column.getExpression() != null) 
        {
            ExpressionResolver er = ExpressionResolver.getInstance();
            try 
            {
                Object bean = cellValue;
                if (table.getModel() instanceof DataTableModel)
                    bean = ((DataTableModel) table.getModel()).getItem(rowIndex);
                
                cellValue = er.eval(column.getExpression(), (Map) bean);
            } 
            catch(Exception e) {
                //e.printStackTrace();
            }
        } 
        return cellValue; 
    }

}
