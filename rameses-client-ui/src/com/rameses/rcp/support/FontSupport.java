/*
 * FontSupport.java
 *
 * Created on August 8, 2013, 12:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public class FontSupport 
{
    // <editor-fold defaultstate="collapsed" desc=" static support ">
    
    private static FontSupport instance = null;
    
    public static FontSupport getInstance() {
        if (instance == null) 
            instance = new FontSupport(); 
        
        return instance; 
    }
    
    // </editor-fold>
        
    public void applyStyles(JComponent component, String styles) {
        if (component == null || styles == null || styles.trim().length() == 0) {
            return;
        }
        
        String[] values = styles.trim().split(";");
        for (String str: values) { 
            int idx = str.indexOf(':');
            if (idx <= 0) continue;
            
            String key = str.substring(0, idx).trim(); 
            if (key.length() == 0) continue;
            
            String val = str.substring(idx+1).trim();
            if (val.length() == 0) continue;
            
            if ("font".equals(key)) { 
                Font oldFont = component.getFont(); 
                component.setFont(oldFont.decode(val)); 
            }
            else if ("font-family".equals(key)) {
                Map attrs = new HashMap(); 
                attrs.put(TextAttribute.FAMILY, val);
                Font oldFont = component.getFont();
                component.setFont(oldFont.deriveFont(attrs)); 
            } 
            else if ("font-style".equals(key)) {
                Map attrs = new HashMap(); 
                if ("normal".equalsIgnoreCase(val) || "regular".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
                else if ("italic".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
                else if ("oblique".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);

                if (!attrs.isEmpty()) component.setFont(component.getFont().deriveFont(attrs)); 
            } 
            else if ("font-weight".equals(key)) {
                Map attrs = new HashMap(); 
                if (val.toLowerCase().matches("normal|regular"))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);                
                else if ("bold".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                else if ("demibold".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD);
                else if ("demilight".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT);
                else if (val.toLowerCase().matches("extrabold|bolder"))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
                else if ("extralight".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT);
                else if ("heavy".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY);
                else if ("light".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);
                else if ("medium".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
                else if ("semibold".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
                else if ("ultrabold".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
                else if ("ultrabold".equalsIgnoreCase(val))
                    attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
                
                else {  
                    try { 
                        attrs.put(TextAttribute.WEIGHT, Float.parseFloat(val));
                    } catch(Throwable t) {;} 
                }
                
                if (!attrs.isEmpty()) component.setFont(component.getFont().deriveFont(attrs)); 
            } 
            else if ("font-size".equals(key)) {
                try { 
                    float size = Float.parseFloat(val); 
                    Map attrs = new HashMap(); 
                    attrs.put(TextAttribute.SIZE, size);
                    component.setFont(component.getFont().deriveFont(attrs)); 
                } catch(Throwable t) {;} 
            } 
            else if ("text-decoration".equals(key)) {
                Map attrs = new HashMap(); 
                if ("underline".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                else if ("underline-dashed".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED);
                else if ("underline-dotted".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
                else if ("underline-gray".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
                else if ("underline-one-pixel".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                else if ("underline-two-pixel".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
                else if ("strikethrough".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                else if ("superscript".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
                else if ("subscript".equalsIgnoreCase(val)) 
                    attrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);

                if (!attrs.isEmpty()) component.setFont(component.getFont().deriveFont(attrs)); 
            }   
        }
    }
    
}
