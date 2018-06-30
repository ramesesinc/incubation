/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import java.util.Iterator;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author wflores
 */
public final class LookAndFeelCustomizer {
    
    public static void install() { 
        try { 
            LookAndFeelCustomizer laf = new LookAndFeelCustomizer(); 
            laf.install0();
        } catch(Throwable t) {
            t.printStackTrace(); 
        }
    }
    
    private void install0() {
        int fontsize = 0; 
        try {
            fontsize = Integer.parseInt(System.getProperty("fontsize"));  
        } catch(Throwable t){;} 
        
        String fontname = System.getProperty("fontname","").trim(); 
        if ( fontname.trim().length() == 0 ) fontname = null; 
        
        boolean debug = ("true".equals(System.getProperty("laf.debug",""))); 
        
        UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
        Iterator itr = uidefs.keySet().iterator();
        while (itr.hasNext()) {
            Object key = itr.next(); 
            Object val = uidefs.get( key ); 
            if ( val instanceof FontUIResource ) {
                FontUIResource old = (FontUIResource) val; 
                int fsize = old.getSize(); 
                if ( fontsize > 0 ) { 
                    fsize = fontsize; 
                    String kname = key.toString().split("\\.")[0];
                    if ( kname.matches(SPECIAL_KEYS)) {
                        fsize += 1;
                    }
                }
                
                String fname = ( fontname == null ? old.getFontName() : fontname );
                uidefs.put(key, new FontUIResource(fname, old.getStyle(), fsize)); 
                if ( debug ) System.out.println(key + " = "+ uidefs.get(key));
            } 
        }  
    }
    
    private final String SPECIAL_KEYS = "ColorChooser|InternalFrame|Menu|MenuBar|MenuItem|OptionPane|RadioButtonMenuItem|TextArea|ToolTip";
}
