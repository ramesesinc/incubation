/*
 * ActionButtonSupport.java
 *
 * Created on August 13, 2013, 11:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author wflores
 */
public class ActionButtonSupport {
    
    // <editor-fold defaultstate="collapsed" desc=" static methods support ">
    
    private static ActionButtonSupport instance = null; 
    
    public static ActionButtonSupport getInstance() { 
        if (instance == null) instance = new ActionButtonSupport(); 
        
        return instance;
    } 
    
    private static ActionThemeConfig actionThemeConfig;
    
    // </editor-fold>
    
    public ActionButtonSupport() {
    }
    
    public void loadDefaults(JButton button, String actionName, ActionListener listener) {
        String resname = "button." + actionName;
        Properties props = getConfig().find(resname);
        if (props.isEmpty()) return;
        
        String caption = props.getProperty(resname + ".caption");
        if (caption != null && caption.trim().length() > 0) {
            button.setText(caption); 
        }        
        String sicon = props.getProperty(resname + ".icon");
        if (sicon != null && sicon.trim().length() > 0) {
            button.setIcon(ImageIconSupport.getInstance().getIcon(sicon)); 
        }
        String mnemonic = props.getProperty(resname + ".mnemonic");
        if (mnemonic != null && mnemonic.trim().length() > 0) {
            button.setMnemonic(mnemonic.trim().charAt(0)); 
        }        
        String accelerator = props.getProperty(resname + ".accelerator");
        if (accelerator != null && accelerator.trim().length() > 0 && listener != null) {
            try {
                KeyStroke ks = KeyStroke.getKeyStroke(accelerator.trim()); 
                InputMap inputMap = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);                 
                inputMap.remove(ks);
                inputMap.put(ks, "fireActionPerformed");
                
                button.getActionMap().remove("fireActionPerformed");
                button.getActionMap().put("fireActionPerformed", new ActionProxy(listener)); 
            } catch(Throwable t){;} 
        }
    }

    private synchronized ActionThemeConfig getConfig() {
        if (actionThemeConfig == null) {
            actionThemeConfig = new ActionThemeConfig();
            actionThemeConfig.parse(); 
        }
        return actionThemeConfig; 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" ActionThemeConfig (class) ">
    
    private class ActionThemeConfig {

        private Properties properties = new Properties(); 
        
        void parse() {
            try { 
                Enumeration en = ClassLoader.getSystemResources("META-INF/action-theme.properties"); 
                while (en.hasMoreElements()) {
                    try {
                        URL url = (URL) en.nextElement();
                        properties.load(url.openStream()); 
                    } catch(Throwable t0){;}
                } 
            } catch(Throwable t) {;} 
        }
        
        Properties find(String name) {
            Properties results = new Properties(); 
            Set entries = properties.entrySet(); 
            for (Object o: entries) {
                Map.Entry me = (Map.Entry)o;
                String key = me.getKey().toString(); 
                if (key.startsWith(name)) {
                    results.put(key, me.getValue()); 
                }
            }
            return results; 
        }         
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionProxy (class) ">
    
    private class ActionProxy extends AbstractAction {
        
        private ActionListener listener;
        
        ActionProxy(ActionListener listener) {
            this.listener = listener; 
        }
        
        public void actionPerformed(ActionEvent e) {
            if (listener != null) listener.actionPerformed(e); 
        } 
    }
    
    // </editor-fold>
}
