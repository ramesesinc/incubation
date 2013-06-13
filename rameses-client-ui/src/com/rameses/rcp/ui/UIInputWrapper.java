/*
 * UIInputWrapper.java
 *
 * Created on June 11, 2013, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.ui;

import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public interface UIInputWrapper extends UIInput 
{
    JComponent getEditorComponent(); 
}
