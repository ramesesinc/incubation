/*
 * AbstractDateField.java
 *
 * Created on August 28, 2013, 11:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *
 * @author wflores
 */
public abstract class AbstractDateField extends DefaultTextField 
{
    private IDateDocument document;
    private String outputFormat;
    
    protected final void initDefaults() 
    {
        setPreferredSize(new Dimension(100, getPreferredSize().height));  
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                actionPerformedImpl(e);
            } 
        });

        if (!Beans.isDesignTime()) 
            setDocumentImpl(new BasicDateDocument()); 
        
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getOutputFormat() { return outputFormat; } 
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat; 
    }
    
    public Object getValue() {
        return (document == null? null: document.getValue()); 
    }
    
    public void setValue(Object value) {
        if (document == null) 
            super.setText(""); 
        else 
            document.setValue(value); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper and supporting methods "> 
    
    private void setDocumentImpl(Document document) {
        super.setDocument(document); 
        if (document instanceof IDateDocument) {
            this.document = (IDateDocument)document;
        } else {
            this.document = null; 
        }
    }
    
    private void actionPerformedImpl(ActionEvent e) {         
        transferFocus(); 
        if (document != null) document.showFormattedValue(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" IDateDocument interface "> 
    
    private interface IDateDocument {
        Object getValue(); 
        void setValue(Object value); 
        void showFormattedValue(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BasicDateDocument class ">
    
    private class BasicDateDocument extends PlainDocument implements IDateDocument  
    {        
        AbstractDateField root = AbstractDateField.this; 
        private SimpleDateFormat outputFormatter; 
        private boolean dirty;

        public Object getValue() { 
            String sval = getText();
            if (sval == null || sval.length() == 0) return null;
            
            Date dt = new DateParser().parse(sval); 
            if (dt == null) return null; 
            
            return getOutputFormatter().format(dt); 
        } 

        public void setValue(Object value) {
            String sval = null;
            if (value == null) {
                //do nothing 
            } else if (value instanceof Date) {
                sval = getOutputFormatter().format((Date) value); 
            } else {
                Date dt = new DateParser().parse(value.toString()); 
                if (dt != null) sval = getOutputFormatter().format(dt); 
            } 
            
            try {
                super.remove(0, getLength());
                if (sval != null) insertString(0, sval, null); 
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } finally {
                dirty = false; 
            }
        }    

        public void showFormattedValue() { 

        }
        
        private String getText() {
            try {
                return new StringBuffer(getText(0, getLength())).toString();
            } catch (BadLocationException ex) {
                ex.printStackTrace(); 
                return null; 
            }
        }
        
        private SimpleDateFormat getOutputFormatter() {
            if (outputFormatter == null) {
                String format = root.getOutputFormat(); 
                if (format == null || format.length() == 0) format = "yyyy-MM-dd"; 
                
                outputFormatter = new SimpleDateFormat(format); 
            } 
            return outputFormatter; 
        } 
    }
    
    // </editor-fold>
}
