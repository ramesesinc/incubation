/*
 * UIKeyBinding.java
 *
 * Created on September 4, 2013, 4:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.ui;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 *
 * @author wflores
 */
public class UIKeyBinding 
{
    private KeyStroke ks;
    private KeyEvent ke;
    private int condition;
    private boolean pressed;
    
    public UIKeyBinding(KeyStroke ks, KeyEvent ke, int condition, boolean pressed) {
        this.ks = ks; 
        this.ke = ke; 
        this.condition = condition;
        this.pressed = pressed;
    }
    
    public KeyStroke getKeyStroke() { return ks; } 
    public KeyEvent getKeyEvent() { return ke; }
    public int getCondition() { return condition; } 
    public boolean isPressed() { return pressed; } 
}
