/*
 * AbstractNumberRenderer.java
 *
 * Created on May 21, 2013, 4:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public abstract class AbstractNumberCellRenderer extends AbstractCellRenderer 
{
    private JLabel label;
    
    public AbstractNumberCellRenderer() 
    {
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.RIGHT);
    }
    
    public JComponent getComponent(JTable table, int row, int column) {
        return label;
    }
    
    protected abstract String getFormattedValue(Column c, Object value);
    
    public void refresh(JTable table, Object value, boolean selected, boolean focus, int row, int column) 
    {
        TableControl tc = (TableControl) table;
        Column c = ((TableControlModel) tc.getModel()).getColumn(column);
        String result = getFormattedValue(c, value);
        label.setText((result == null ? "" : result));
        
        String alignment = (c.getAlignment() == null? null: c.getAlignment().toUpperCase());
        if ( alignment != null ) 
        {
            if ("CENTER".equals(alignment))
                label.setHorizontalAlignment(SwingConstants.CENTER);
            else if ("LEFT".equals(alignment))
                label.setHorizontalAlignment(SwingConstants.LEFT);
            else
                label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
    }
    
    protected String formatValue(Number value, String format, String defaultFormat) 
    {
        if (value == null) return null;
        if ("".equals(format)) return value.toString();
        
        DecimalFormat formatter = null;
        if ( format != null)
            formatter = new DecimalFormat(format);
        else
            formatter = new DecimalFormat(defaultFormat);
        
        return formatter.format(value);
    }    
}
