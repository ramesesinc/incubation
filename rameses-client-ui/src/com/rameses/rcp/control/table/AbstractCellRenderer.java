/*
 * AbstractRenderer.java
 *
 * Created on May 21, 2013, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.AbstractListModel;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.support.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author wflores
 */
public abstract class AbstractCellRenderer implements TableCellRenderer 
{
    private Insets CELL_MARGIN = TableUtil.CELL_MARGIN; 
    private Color FOCUS_BG = TableUtil.FOCUS_BG;
    
    public abstract JComponent getComponent(JTable table, int row, int column);
    
    public abstract void refresh(JTable table, Object value, boolean selected, boolean focus, int row, int column);
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
    {
        TableControl xtable = (TableControl) table;
        TableControlModel xmodel = (TableControlModel) xtable.getModel();
        JComponent comp = getComponent(table, row, column);
        comp.setBorder(BorderFactory.createEmptyBorder(CELL_MARGIN.top, CELL_MARGIN.left, CELL_MARGIN.bottom, CELL_MARGIN.right));
        comp.setFont(table.getFont());
        
        if (isSelected) 
        {
            comp.setBackground(table.getSelectionBackground());
            comp.setForeground(table.getSelectionForeground());
            comp.setOpaque(true);
            if (hasFocus) 
            {
                comp.setBackground(FOCUS_BG);
                comp.setForeground(table.getForeground());
            }
        } 
        else 
        {
            comp.setForeground(table.getForeground());
            comp.setOpaque(false);
            
            if ( (row % 2 == 0) ) 
            {
                if ( xtable.getEvenBackground() != null ) 
                {
                    comp.setBackground( xtable.getEvenBackground() );
                    comp.setOpaque(true);
                }
                if ( xtable.getEvenForeground() != null ) {
                    comp.setForeground( xtable.getEvenForeground() );
                }
                
            } 
            else 
            {
                if ( xtable.getOddBackground() != null ) 
                {
                    comp.setBackground( xtable.getOddBackground() );
                    comp.setOpaque(true);
                }
                if ( xtable.getOddForeground() != null ) {
                    comp.setForeground( xtable.getOddForeground() );
                }
            }
        }
        
        AbstractListModel lm = xtable.getListModel();
        ExpressionResolver exprRes = ExpressionResolver.getInstance();
        Column colModel = xmodel.getColumn(column);
        
//            try {
//                StyleRule[] styles = xtable.getBinding().getStyleRules();
//                if( styles != null && styles.length > 0) {
//                    comp.setOpaque(true);
//
//                    ListItem listItem = lm.getSelectedItem();
//                    if( listItem == null ) {
//                        listItem = lm.getItemList().get(0);
//                    }
//
//                    Map bean = new HashMap();
//                    bean.put("row", listItem.getRownum());
//                    bean.put("column", column);
//                    bean.put("columnName", colModel.getName());
//                    bean.put("root", listItem.getRoot());
//                    bean.put("selected", isSelected);
//                    bean.put("hasFocus", hasFocus);
//                    bean.put("item", listItem.getItem());
//                    applyStyle( xtable.getName(), bean, comp, styles, exprRes );
//                }
//            } catch(Exception e){;}
        
        
        String errmsg = lm.getErrorMessage(row);
        if (errmsg != null) 
        {
            if (!hasFocus) 
            {
                comp.setBackground( xtable.getErrorBackground() );
                comp.setForeground( xtable.getErrorForeground() );
                comp.setOpaque(true);
            }
        }
        
        if ( !table.isEnabled() ) 
        {
            Color c = comp.getBackground();
            comp.setBackground(ColorUtil.brighter(c, 5));
            c = comp.getForeground();
            comp.setForeground(ColorUtil.brighter(c, 5));
        }
        
        //border support
        Border inner = BorderFactory.createEmptyBorder(CELL_MARGIN.top, CELL_MARGIN.left, CELL_MARGIN.bottom, CELL_MARGIN.right);
        Border border = BorderFactory.createEmptyBorder(1,1,1,1);
        if (hasFocus) {
            if (isSelected)
                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            if (border == null)
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
        }
        comp.setBorder(BorderFactory.createCompoundBorder(border, inner));
        
        refresh(table, value, isSelected, hasFocus, row, column);
        return comp;        
    }
}
