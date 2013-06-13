/*
 * ColumnPropertyEditor.java
 *
 * Created on May 28, 2013, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor;

import com.rameses.beaninfo.editor.table.ColumnEditorPage;
import com.rameses.rcp.common.CheckBoxColumnHandler;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ComboBoxColumnHandler;
import com.rameses.rcp.common.DateColumnHandler;
import com.rameses.rcp.common.DecimalColumnHandler;
import com.rameses.rcp.common.DoubleColumnHandler;
import com.rameses.rcp.common.IntegerColumnHandler;
import com.rameses.rcp.common.LookupColumnHandler;
import com.rameses.rcp.common.OpenerColumnHandler;
import com.rameses.rcp.common.TextColumnHandler;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

/**
 *
 * @author wflores
 */
public class ColumnPropertyEditor implements PropertyEditor 
{
    private PropertyChangeSupport support; 
    private ColumnEditorPage page;
    private Column[] columns;
    
    public ColumnPropertyEditor() 
    {
        support = new PropertyChangeSupport(this); 
        page = new ColumnEditorPage();
        page.setPropertyEditor(this);          
    }

    public Object getValue() { return columns; }    
    public void setValue(Object value) 
    {
        Column[] oldColumns = (Column[]) value; 
        if (oldColumns != null) 
        {
            this.columns = new Column[oldColumns.length];
            for (int i=0; i<oldColumns.length; i++) { 
                this.columns[i] = createColumn(oldColumns[i]);
            }
        }
        else {
            this.columns = null; 
        }
        support.firePropertyChange("", null, null); 
    }

    public void paintValue(Graphics gfx, Rectangle box) {}

    public boolean isPaintable() { return false; }

    public String getAsText() { return null; }    
    public void setAsText(String text) throws IllegalArgumentException {}

    public String[] getTags() { return null; }
    public boolean supportsCustomEditor() { return true; }

    public Component getCustomEditor() {
        return page; 
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener); 
    }
    
    public String getJavaInitializationString() 
    {
        if (columns == null || columns.length == 0) return null;
        
        StringBuffer sb = new StringBuffer("new " + Column.class.getName() + "[]{");
        for (int i=0; i<columns.length; i++) 
        {
            Column c = columns[i]; 
            if (i > 0) sb.append(", ");
            
            sb.append("\n");
            sb.append("new " + Column.class.getName() + "("+ convertString(c.getName()) +", "+ convertString(c.getCaption()) +"");
            sb.append(", " + c.getWidth() + ", " + c.getMinWidth() + ", " + c.getMaxWidth());
            sb.append(", " + c.isRequired() + ", " + c.isResizable() + ", " + c.isNullWhenEmpty() + ", " + c.isEditable());
            sb.append(", " + convertString(c.getEditableWhen())); 
            sb.append(", " + getInitString(c.getTypeHandler())); 
            sb.append(")");
        }
        sb.append("\n}");
        return sb.toString(); 
    }

    private String getInitString(Column.TypeHandler typeHandler) 
    {
        StringBuffer sb = new StringBuffer();
        if (typeHandler instanceof CheckBoxColumnHandler) 
        {
            CheckBoxColumnHandler chk = (CheckBoxColumnHandler) typeHandler;
            sb.append("new " + chk.getClass().getName() + "(");
            if (chk.getValueType() == Integer.class) 
                sb.append(Integer.class.getName()+".class, "+ chk.getCheckValue() + ", " + chk.getUncheckValue());
            else if (chk.getValueType() == String.class) 
                sb.append(String.class.getName()+".class, "+ convertString(chk.getCheckValue()) + ", " + convertString(chk.getUncheckValue()));
            else 
                sb.append(Boolean.class.getName()+".class, "+ chk.getCheckValue() + ", " + chk.getUncheckValue());

            sb.append(")");
        }
        else if (typeHandler instanceof ComboBoxColumnHandler) 
        {
            ComboBoxColumnHandler combo = (ComboBoxColumnHandler) typeHandler;
            sb.append("new " + combo.getClass().getName() + "(");
            sb.append(convertString(combo.getItems()) + ", " + convertString(combo.getItemKey()) + ", " + convertString(combo.getExpression()));
            sb.append(")");
        }
        else if (typeHandler instanceof DateColumnHandler) 
        {
            DateColumnHandler date = (DateColumnHandler) typeHandler;
            sb.append("new " + date.getClass().getName() + "(");
            sb.append(convertString(date.getInputFormat()) + ", " + convertString(date.getOutputFormat()) + ", " + convertString(date.getValueFormat())); 
            sb.append(")");
        }    
        else if (typeHandler instanceof DecimalColumnHandler) 
        {
            DecimalColumnHandler dec = (DecimalColumnHandler) typeHandler;
            sb.append("new " + dec.getClass().getName() + "(");
            sb.append(convertString(dec.getFormat()) + ", " + dec.getMinValue() + ", " + dec.getMaxValue() + ", " + dec.isUsePrimitiveValue()); 
            sb.append(")");
        }           
        else if (typeHandler instanceof DoubleColumnHandler) 
        {
            DoubleColumnHandler num = (DoubleColumnHandler) typeHandler;
            sb.append("new " + num.getClass().getName() + "(");
            sb.append(convertString(num.getFormat()) + ", " + num.getMinValue() + ", " + num.getMaxValue()); 
            sb.append(")");
        }                 
        else if (typeHandler instanceof IntegerColumnHandler) 
        {
            IntegerColumnHandler num = (IntegerColumnHandler) typeHandler;
            sb.append("new " + num.getClass().getName() + "(");
            sb.append(convertString(num.getFormat()) + ", " + num.getMinValue() + ", " + num.getMaxValue()); 
            sb.append(")");
        }  
        else if (typeHandler instanceof LookupColumnHandler) 
        {
            LookupColumnHandler lkp = (LookupColumnHandler) typeHandler;
            sb.append("new " + lkp.getClass().getName() + "(");
            sb.append(convertString(lkp.getExpression()) + ", " + convertString(lkp.getHandler())); 
            sb.append(")");
        } 
        else if (typeHandler instanceof OpenerColumnHandler) 
        {
            OpenerColumnHandler handler = (OpenerColumnHandler) typeHandler;
            sb.append("new " + handler.getClass().getName() + "(");
            sb.append(convertString(handler.getExpression()) + ", " + convertString(handler.getHandler())); 
            sb.append(")");
        }         
        else {
            sb.append("new " + TextColumnHandler.class.getName() + "()");
        }
        return sb.toString(); 
    }
    
    private String convertString(Object value) 
    {
        if (value == null) return null; 
        
        return "\"" + value + "\""; 
    }
    
    private Column createColumn(Column oldColumn) 
    {
        Column newColumn = new Column(
            oldColumn.getName(), oldColumn.getCaption(), oldColumn.getWidth(), oldColumn.getMinWidth(), oldColumn.getMaxWidth(), 
            oldColumn.isRequired(), oldColumn.isResizable(), oldColumn.isNullWhenEmpty(), oldColumn.isEditable(), 
            oldColumn.getEditableWhen(), oldColumn.getTypeHandler() 
        );
        return newColumn; 
    }
    
}
