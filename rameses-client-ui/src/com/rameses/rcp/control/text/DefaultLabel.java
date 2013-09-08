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
import javax.swing.JLabel;

/**
 *
 * @author wflores
 */
public class DefaultLabel extends JLabel 
{
    private Font sourceFont;    
    private String fontStyle; 
    
    public DefaultLabel() {
    }
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } else {
            super.setFont(sourceFont); 
        } 
        new FontSupport().applyStyles(this, fontStyle);
    }     
}
