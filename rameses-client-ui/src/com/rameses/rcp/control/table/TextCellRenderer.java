/*
 * StringRenderer.java
 *
 * Created on May 21, 2013, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.support.ComponentSupport;
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

    private ComponentSupport componentSupport;    
    private JLabel label;
    
    public TextCellRenderer() 
    {
        label = new JLabel();
        label.setVerticalAlignment(SwingConstants.CENTER);
    }
    
    protected ComponentSupport getComponentSupport() 
    {
        if (componentSupport == null) 
            componentSupport = new ComponentSupport();
        
        return componentSupport;
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
        
        TextCase oTextCase = TextCase.UPPER;
        try {
            oTextCase = TextCase.valueOf(column.getTextCase().toUpperCase()); 
        } catch(Exception ex) {;}         
        
        Object columnValue = resolveValue(value); 
        if (oTextCase != null && columnValue != null) 
            label.setText(oTextCase.convert(columnValue.toString())); 
        
        label.setHorizontalAlignment( SwingConstants.LEFT );
        if ( columnValue != null && column.isHtmlDisplay() )
            columnValue = "<html>" + columnValue + "</html>";

        label.setText((columnValue == null ? "" : columnValue.toString()));
        
        //set alignment if it is specified in the Column model
        if ( column.getAlignment() != null ) 
            getComponentSupport().alignText(label, column.getAlignment()); 
    }  
}
