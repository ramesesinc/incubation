package com.rameses.rcp.support;

import com.rameses.rcp.constant.TextCase;
import java.beans.Beans;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextDocument extends PlainDocument 
{
    private TextCase textCase;
    private int maxlength;
    private boolean dirty;
    
    public TextDocument() 
    {
        this.textCase = TextCase.NONE;
        this.maxlength = -1;
    }
    
    public TextCase getTextCase() { return textCase; }    
    public void setTextCase(TextCase textCase) 
    {
        this.textCase = textCase;
        update(); 
    }
    
    public int getMaxlength() { return maxlength; }    
    public void setMaxlength(int length) { maxlength = length; }
    
    public boolean isDirty() { return dirty; } 
    
    public void reset() 
    { 
        dirty = false; 
    }  
    
    public void loadValue(Object value) 
    {
        try 
        {
            super.remove(0, getLength());
            insertString(0, (value == null? "": value.toString()), null);
        }
        catch(Exception ex) {;}
        
        dirty = false; 
    }
    
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
    {
        if (Beans.isDesignTime()) 
        {
            super.insertString(offs, str, a); 
            return;
        }
        
        if (maxlength > 0) 
        {
            if (getLength() >= maxlength) return;
            
            if (getLength()+str.length() > maxlength) {
                str = str.substring(0, maxlength - getLength());
            }
        }
        
        //convert if textCase is specified
        if (textCase != null) str = textCase.convert(str);
        
        super.insertString(offs, str, a);
        dirty = true; 
    }

    public void remove(int offs, int len) throws BadLocationException 
    {
        super.remove(offs, len); 
        dirty = true; 
    }
    
    private void update() 
    {        
        try 
        {
            String text = getText(0, getLength());
            super.remove(0, getLength());
            insertString(0, text, null); 
        } 
        catch (Exception ex) {;}
    }
}

