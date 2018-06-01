/*
 * PanelView.java
 *
 * Created on August 25, 2013, 9:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test.view;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class PanelView extends JPanel implements PanelViewLayout.Provider {
    
    public final static String TOP      = PanelViewLayout.TOP;
    public final static String LEFT     = PanelViewLayout.LEFT;
    public final static String BOTTOM   = PanelViewLayout.BOTTOM;
    public final static String RIGHT    = PanelViewLayout.RIGHT;
    public final static String CENTER   = PanelViewLayout.CENTER;
    
    private static final long serialVersionUID = 1L;
    
    private PanelViewLayout layout; 
    
    public PanelView() {
        layout = new PanelViewLayout(this); 
        super.setLayout(layout); 
    } 
    
    public LayoutManager getLayout() { return layout; } 
    public void setLayout(LayoutManager mgr) {}
    
    public int getDividerSize() { 
        return layout.getDividerSize(); 
    } 
    public void setDividerSize(int dividerSize) {
        layout.setDividerSize(dividerSize); 
    }
    
    protected void addImpl(Component comp, Object constraints, int index) {
        String vwname = getConstraint( constraints ); 
        super.addImpl(comp, vwname, index);
    }
    
    private String getConstraint( Object constraint ) {
        if ( constraint == null ) return PanelView.CENTER; 
        
        String s = constraint.toString(); 
        if ( PanelView.TOP.equals( s )) return PanelView.TOP;
        else if ( PanelView.LEFT.equals( s )) return PanelView.LEFT;
        else if ( PanelView.BOTTOM.equals( s )) return PanelView.BOTTOM;
        else if ( PanelView.RIGHT.equals( s )) return PanelView.RIGHT;
        else return PanelView.CENTER; 
    }

    public void paintDividerHandle(Rectangle viewRect, Rectangle dividerRect, Point targetPoint) {
        repaint();
    } 
}
