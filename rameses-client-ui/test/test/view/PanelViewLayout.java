/*
 * PanelViewLayout.java
 *
 * Created on April 28, 2013, 10:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test.view;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
class PanelViewLayout implements LayoutManager {
        
    public final static String TOP      = "top";
    public final static String LEFT     = "left";
    public final static String BOTTOM   = "bottom";
    public final static String RIGHT    = "right";
    public final static String CENTER   = "center";
    
    private PanelViewLayout.Provider provider;
    private int dividerSize;

    private Component compTop;
    private Component compLeft;
    private Component compBottom;
    private Component compRight;
    private Component compCenter;
    
    public PanelViewLayout(PanelViewLayout.Provider provider) { 
        this.provider = provider;
        setDividerSize(5); 
    }
    
    public int getDividerSize() { return dividerSize; } 
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize; 
    }

    public void addLayoutComponent(String name, Component comp) {
        if ( comp == null ) return; 
        else if ( TOP.equals( name )) compTop = comp;
        else if ( LEFT.equals( name )) compLeft = comp;
        else if ( BOTTOM.equals( name )) compBottom = comp;
        else if ( RIGHT.equals( name )) compRight = comp;
        else if ( CENTER.equals( name )) compCenter = comp;
    }
    
    public void removeLayoutComponent(Component comp) {
        if ( comp == null ) return; 
        else if ( compTop != null && compTop.equals(comp)) compTop = null; 
        else if ( compLeft != null && compLeft.equals(comp)) compLeft = null; 
        else if ( compBottom != null && compBottom.equals(comp)) compBottom = null; 
        else if ( compRight != null && compRight.equals(comp)) compRight = null; 
        else if ( compCenter != null && compCenter.equals(comp)) compCenter = null; 
    }

    public Dimension preferredLayoutSize(Container parent) { 
        return getLayoutSize( parent );  
    } 

    public Dimension minimumLayoutSize(Container parent) {
        return getLayoutSize( parent ); 
    }
    
    private Dimension getLayoutSize(Container parent) {
        synchronized ( parent.getTreeLock()) { 
            int wv=0, hv=0, wh=0, hh=0;
            int dsize = getDividerSize(); 

            boolean hasTop = (compTop != null && compTop.isVisible()); 
            boolean hasLeft = (compLeft != null && compLeft.isVisible()); 
            boolean hasBottom = (compBottom != null && compBottom.isVisible()); 
            boolean hasRight = (compRight != null && compRight.isVisible()); 
            boolean hasCenter = (compCenter != null && compCenter.isVisible()); 
            
            if ( hasTop ) {
                Dimension dim = compTop.getPreferredSize(); 
                wv = Math.max(wv, dim.width); 
                hv += dim.height; 
            }
            if ( hasBottom ) {
                Dimension dim = compBottom.getPreferredSize(); 
                wv = Math.max(wv, dim.width); 
                hv += dim.height; 
            }
            if ( hasLeft ) {
                Dimension dim = compLeft.getPreferredSize(); 
                hh = Math.max(hh, dim.height); 
                wh += dim.width;
            }
            if ( hasRight ) {
                Dimension dim = compRight.getPreferredSize(); 
                hh = Math.max(hh, dim.height); 
                wh += dim.width;
            }
            
            Insets margin = parent.getInsets(); 
            w += (margin.left + margin.top);
            h += (margin.top + margin.bottom);
            return new Dimension( w, h ); 
        }
    }
    
    public void layoutContainer(Container parent) {
        synchronized ( parent.getTreeLock()) { 
            Insets margin = parent.getInsets(); 
            int pw = parent.getWidth();
            int ph = parent.getHeight();
            int x = margin.left, y = margin.top;
            int w = pw - (margin.left + margin.right);
            int h = ph - (margin.top + margin.bottom);
            
            
        } 
    }    
    
    private Component[] getVisibleComponents(Component[] comps) {
        if (comps == null) return new Component[]{};
        
        List<Component> list = new ArrayList();
        if ( compTop != null && compTop.isVisible()) {
            list.add( compTop ); 
        }
        if ( compLeft != null && compLeft.isVisible()) {
            list.add( compLeft ); 
        }        
        if ( compBottom != null && compBottom.isVisible()) {
            list.add( compBottom ); 
        }        
        if ( compRight != null && compRight.isVisible()) {
            list.add( compRight ); 
        }        
        if ( compCenter != null && compCenter.isVisible()) {
            list.add( compCenter ); 
        } 
        return list.toArray(new Component[]{}); 
    }

    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider {        
        void revalidate();
        void repaint();
        void paintDividerHandle(Rectangle viewRect, Rectangle divRect, Point targetPoint);         
    }
    
    // </editor-fold>
}
