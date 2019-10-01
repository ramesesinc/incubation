/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.framework;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
        
        double fontscale = 1.0; 
        DecimalFormat numformat = new DecimalFormat("0.00"); 
        try {
            Number num = Math.max( 100, Integer.parseInt(System.getProperty("fontscale", "100")));
            fontscale = numformat.parse( numformat.format((num.doubleValue()/100.0))).doubleValue(); 
        } catch(Throwable t){;} 
        
        System.getProperties().put("laf.fontscale", fontscale); 
        
        String fontname = System.getProperty("fontname","").trim(); 
        if ( fontname.trim().length() == 0 ) fontname = null; 
        
        boolean debug = ("true".equals(System.getProperty("laf.debug",""))); 
        
        numformat = new DecimalFormat("0"); 
        
        HashMap<Object,FontUIResource> umap = new HashMap();
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

                try { 
                    Number num = ((Number) fsize).doubleValue() * fontscale;
                    fsize = numformat.parse( numformat.format( num )).intValue(); 
                } catch(Throwable t) {;} 
                    
                String fname = ( fontname == null ? old.getFontName() : fontname );
                umap.put(key, new FontUIResource(fname, old.getStyle(), fsize)); 
            } 
        }  
        
        Set<Map.Entry<Object,FontUIResource>> entries = umap.entrySet();
        for (Map.Entry e : entries) { 
            uidefs.put(e.getKey(), e.getValue()); 
            if ( debug ) System.out.println(e.getKey() + " = "+ uidefs.get(e.getKey()));
        }
    }
    
    private final String SPECIAL_KEYS = "ColorChooser|InternalFrame|Menu|MenuBar|MenuItem|OptionPane|RadioButtonMenuItem|TextArea|ToolTip";
}
