/*
 * StringRenderer.java
 *
 * Created on May 21, 2013, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.framework.ClientContext;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class TextCellRenderer extends AbstractCellRenderer 
{    
    protected TableControl tableControl;
    protected JTable table;    
    protected Column column;
    protected int rowIndex;
    protected int colIndex;
    
    private JLabel label;
    
    public TextCellRenderer() 
    {
        label = new JLabel();
        label.setVerticalAlignment(SwingConstants.CENTER);
    }
    
    public JComponent getComponent(JTable table, int row, int column) {
        return label;
    }
    
    protected Object resolveValue(Object value) { 
        return value; 
    }
    
    public void refresh(JTable table, Object value, boolean selected, boolean focus, int rowIndex, int colIndex) 
    {
        tableControl = (TableControl) table;
        column = ((TableControlModel) tableControl.getModel()).getColumn(colIndex);
        this.rowIndex = rowIndex;
        this.colIndex = colIndex; 
        this.table = table; 
        
        Object columnValue = resolveValue(value);        
        /*
        if ( "date".equals(type) || columnValue instanceof Date || columnValue instanceof Time || columnValue instanceof Timestamp ) 
        {            
            label.setHorizontalAlignment( SwingConstants.CENTER );
            SimpleDateFormat formatter = null;
            if ( format != null )
                formatter = new SimpleDateFormat(format);
            else
                formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            label.setText((columnValue == null ? "" : formatter.format(columnValue)));
            
        } 
        else 
        {*/
            label.setHorizontalAlignment( SwingConstants.LEFT );
            if ( columnValue != null && column.isHtmlDisplay() )
                columnValue = "<html>" + columnValue + "</html>";
            
            label.setText((columnValue == null ? "" : columnValue.toString()));
        //}
        
        //set alignment if it is specified in the Column model
        if ( column.getAlignment() != null ) 
        {
            if ( "right".equals(column.getAlignment().toLowerCase()) )
                label.setHorizontalAlignment(SwingConstants.RIGHT);
            else if ( "center".equals(column.getAlignment().toLowerCase()))
                label.setHorizontalAlignment(SwingConstants.CENTER);
            else if ( "left".equals(column.getAlignment().toLowerCase()) )
                label.setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        //set vertical alignment if it is specified in the Column model
        if ( column.getVAlignment() != null ) 
        {
            if ( "top".equals(column.getVAlignment().toLowerCase()) )
                label.setVerticalAlignment(SwingConstants.TOP);
            else if ( "center".equals(column.getVAlignment().toLowerCase()) )
                label.setVerticalAlignment(SwingConstants.CENTER);
            else if ( "bottom".equals(column.getVAlignment().toLowerCase()) )
                label.setVerticalAlignment(SwingConstants.BOTTOM);
        }
    }
    
    private String format(Object value, String format, String defaultFormat) 
    {
        DecimalFormat formatter = null;
        if ( format != null)
            formatter = new DecimalFormat(format);
        else
            formatter = new DecimalFormat(defaultFormat);
        
        return formatter.format(value);
    }    
}
