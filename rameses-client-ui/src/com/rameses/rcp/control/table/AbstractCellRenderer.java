/*
 * AbstractRenderer.java
 *
 * Created on May 21, 2013, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
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
    protected Column column;
    
    private Insets CELL_MARGIN = TableUtil.CELL_MARGIN; 
    private Color FOCUS_BG = TableUtil.FOCUS_BG;
    
    public abstract JComponent getComponent(JTable table, int rowIndex, int columnIndex);
    
    public abstract void refresh(JTable table, Object value, boolean selected, boolean hasFocus, int rowIndex, int columnIndex);
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) 
    {
        TableControl xtable = (TableControl) table;
        TableControlModel xmodel = (TableControlModel) xtable.getModel();
        JComponent comp = getComponent(table, rowIndex, colIndex);
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
            
            if ( (rowIndex % 2 == 0) ) 
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
        
        AbstractListDataProvider ldp = xtable.getDataProvider();
        column = xmodel.getColumn(colIndex);
        
        String errmsg = ldp.getMessageSupport().getErrorMessage(rowIndex);
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
        if (hasFocus) 
        {
            if (isSelected) border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            if (border == null) border = UIManager.getBorder("Table.focusCellHighlightBorder");
        }
        comp.setBorder(BorderFactory.createCompoundBorder(border, inner));        
        refresh(table, value, isSelected, hasFocus, rowIndex, colIndex);
        return comp;        
    }
}
