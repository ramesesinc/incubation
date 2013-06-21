/*
 * TextDocumentModel.java
 *
 * Created on June 21, 2013, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class TextDocumentModel 
{
    private Provider provider;
    
    public TextDocumentModel() {
    }
    
    public boolean hasProvider() { 
        return (provider != null); 
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider; 
    }
        
    public String getText() { 
        return provider.getText(); 
    } 
    
    public void setText(String text) {
        provider.setText(text); 
    }
    
    public void insertText(String text) {
        provider.insertText(text); 
    }

    public void requestFocus() { 
        provider.requestFocus();
    }
    
    
    
    public static interface Provider 
    {
        void requestFocus();
        
        String getText(); 
        void setText(String text);
        
        void insertText(String text);         
    }
}
