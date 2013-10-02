/*
 * DefaultLabel.java
 *
 * Created on September 8, 2013, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.support.FontSupport;
import java.awt.Font;
import java.util.Map;
import javax.swing.JLabel;

/**
 *
 * @author wflores 
 */
public class DefaultLabel extends JLabel 
{
    private FontSupport fontSupport;    
    private Font sourceFont;    
    private String fontStyle; 
    
    public DefaultLabel() {
    }
    
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }
    
    public void setFont(Font font) { 
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            if (!attrs.isEmpty()) sourceFont = sourceFont.deriveFont(attrs);
        }
        
        super.setFont(sourceFont); 
    } 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        
        if (sourceFont == null) sourceFont = super.getFont(); 
        
        Font font = sourceFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        if (!attrs.isEmpty()) font = font.deriveFont(attrs); 
        
        super.setFont(font); 
    } 
}
