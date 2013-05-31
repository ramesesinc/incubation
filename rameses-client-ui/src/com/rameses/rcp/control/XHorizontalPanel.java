/*
 * XHorizontalPanel.java
 *
 * Created on April 28, 2013, 10:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.control.layout.HorizontalLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class XHorizontalPanel extends JPanel
{
    private HorizontalLayout layoutMgr;
    private Border borderSeparator;
    
    public XHorizontalPanel() 
    {
        this.layoutMgr = new HorizontalLayout(); 
        this.borderSeparator = this.layoutMgr.getSeparator(); 
        super.setLayout(this.layoutMgr); 
    }

    public LayoutManager getLayout() { return this.layoutMgr; }
    public void setLayout(LayoutManager mgr) {
        //do nothing
    }
    
    public Border getBorderSeparator() { return this.borderSeparator; }
    public void setBorderSeparator(Border borderSeparator) 
    {
        this.borderSeparator = borderSeparator; 
        this.layoutMgr.setSeparator(this.borderSeparator); 
    }
}