 /*
 * LookupCellRenderer.java
 *
 * Created on May 21, 2013, 4:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.OpenerColumnHandler;
import com.rameses.rcp.util.UIControlUtil;

/**
 *
 * @author wflores
 */
public class OpenerCellRenderer extends TextCellRenderer
{
    
    protected Object resolveValue(Object value) 
    {
        Object cellValue = value; 
        if (column.getTypeHandler() instanceof OpenerColumnHandler) 
        {
            OpenerColumnHandler handler = (OpenerColumnHandler) column.getTypeHandler(); 
            String expression = handler.getExpression();
            if (expression != null) 
            {
                DataTableModel dtm = (DataTableModel) table.getModel(); 
                Object exprBean = dtm.createExpressionBean(rowIndex); 
                try { 
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expression); 
                } catch(Exception ex) {
                    ;
                }
            }
        }
        return cellValue; 
    }
    
}
