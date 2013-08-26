/*
 * SplitVewLayout.java
 *
 * Created on April 28, 2013, 10:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.layout;


import com.rameses.rcp.constant.UIConstants;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;

/**
 *
 * @author wflores
 */
public class SplitViewLayout implements LayoutManager
{
    private Map<String,LayoutManager> layouts = new HashMap<String,LayoutManager>();
    private LayoutManager layout;
    private String orientation;
    private int dividerSize;
    private int dividerLocation;
    private int locationIndex;
    private Component divider;
    private SplitViewLayout.Provider provider;

    private Component horizontalDivider;
    private Component verticalDivider;
    private Point sourcePoint;
    private Point targetPoint;
    private Rectangle viewRect; 
    
    public SplitViewLayout(SplitViewLayout.Provider provider)
    {
        this.provider = provider;
        layouts.put(UIConstants.HORIZONTAL, new HorizontalLayout()); 
        layouts.put(UIConstants.VERTICAL, new VerticalLayout());        
        setOrientation(UIConstants.HORIZONTAL);
        setDividerLocation(100);
        setDividerSize(5); 
    }
    
    public String getOrientation() { return this.orientation; }    
    public void setOrientation(String orientation) 
    {
        this.orientation = orientation;
        this.layout = layouts.get(orientation); 
        if (this.layout == null) {
            this.orientation = UIConstants.HORIZONTAL;
            this.layout = layouts.get(orientation); 
        }
    }
    
    public int getDividerSize() { return dividerSize; } 
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize; 
    }
    
    public int getDividerLocation() { return dividerLocation; } 
    public void setDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;
        this.locationIndex = dividerLocation;         
    }
        
    void setLocationIndex(int x) {
        this.locationIndex = x; 
    }
    
    private Component getDivider() {
        if ("vertical".equalsIgnoreCase(getOrientation()+"")) { 
            if (verticalDivider == null) {
                JLabel lbl = new JLabel();
                lbl.setName("splitview.divider"); 
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)); 
                
                new VerticalMouseSupport().setDivider(lbl); 
                verticalDivider = lbl;
            }
            return verticalDivider; 
        } else {
            if (horizontalDivider == null) {
                JLabel lbl = new JLabel();
                lbl.setName("splitview.divider"); 
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)); 
                
                new HorizontalMouseSupport().setDivider(lbl);                
                horizontalDivider = lbl;
            } 
            return horizontalDivider;
        }
    }
        
    public void addLayoutComponent(String name, Component comp) {}    
    public void removeLayoutComponent(Component comp) {}

    public Dimension preferredLayoutSize(Container parent) {
        if (layout == null) 
            return new Dimension(getDividerSize(), getDividerSize());
        else 
            return layout.preferredLayoutSize(parent);
    }

    public Dimension minimumLayoutSize(Container parent) {
        if (layout == null) 
            return new Dimension(getDividerSize(), getDividerSize());
        else 
            return layout.minimumLayoutSize(parent); 
    }
    
    public void layoutContainer(Container parent) {
        layout.layoutContainer(parent);
    }    
    
    private Component[] getLayoutComponents(Component[] comps) {
        if (comps == null) return new Component[]{};
        
        List<Component> list = new ArrayList();
        for (int i=0; i<comps.length; i++) {
            Component c = comps[i];            
            if (horizontalDivider != null && horizontalDivider.equals(c)) continue;
            if (verticalDivider != null && verticalDivider.equals(c)) continue;
            if (list.size() >= 2) break;
                
            list.add(c); 
        }
        return list.toArray(new Component[]{}); 
    }
    
    private Component getLayoutComponent(Component[] comps, String name) {
        if (comps == null || name == null) return null;
        
        for (int i=0; i<comps.length; i++) {
            String cname = comps[i].getName();
            if (name.equals(cname)) return comps[i]; 
        } 
        return null; 
    } 
    
    private Component lookupComponent(Component[] comps) {
        if (comps == null || comps.length == 0) return null;
        
        for (int i=0; i<comps.length; i++) {
            Component c = comps[i];
            if (c.getName() == null) return c;
        }
        return null; 
    }

    // <editor-fold defaultstate="collapsed" desc=" Provider ">
    
    public static interface Provider 
    {        
        void revalidate();
        void repaint();
        void paintDividerHandle(Rectangle viewRect, Rectangle divRect, Point targetPoint);         
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" HorizontalMouseSupport ">
    
    private class HorizontalMouseSupport implements MouseListener, MouseMotionListener
    {
        SplitViewLayout root = SplitViewLayout.this;  
        Component divider;
        
        void setDivider(Component divider) {
            this.divider = divider;
            divider.addMouseListener(this);
            divider.addMouseMotionListener(this); 
        }
        
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            sourcePoint = e.getPoint(); 
        }
        
        public void mouseReleased(MouseEvent e) {
            if (targetPoint != null) {
                Rectangle rect = divider.getBounds();
                int nx = rect.x + targetPoint.x; 
                if (nx < 0) targetPoint.x = locationIndex * -1;
                
                locationIndex = nx;
            }
            sourcePoint = null;
            targetPoint = null;
            provider.revalidate();
            provider.repaint(); 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}        
        public void mouseDragged(MouseEvent e) {
            targetPoint = e.getPoint(); 
            Rectangle divRect = divider.getBounds();
            int nx = divRect.x + targetPoint.x; 
            if (nx < 10) { 
                targetPoint.x = (divRect.x * -1)+10; 
            } else {
                int w = viewRect.width-divRect.x;
                int limit = w - getDividerSize() - 10;
                if (targetPoint.x > limit) targetPoint.x = limit;
            }
            
            provider.paintDividerHandle(viewRect, divRect, targetPoint); 
        }        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" VerticalMouseSupport ">
    
    private class VerticalMouseSupport implements MouseListener, MouseMotionListener
    {
        SplitViewLayout root = SplitViewLayout.this;  
        Component divider;
        
        void setDivider(Component divider) {
            this.divider = divider;
            divider.addMouseListener(this);
            divider.addMouseMotionListener(this); 
        }
        
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
            sourcePoint = e.getPoint(); 
        }
        
        public void mouseReleased(MouseEvent e) {
            if (targetPoint != null) {
                Rectangle rect = divider.getBounds();
                int ny = rect.y + targetPoint.y; 
                if (ny < 0) targetPoint.y = locationIndex * -1;
                
                locationIndex = ny;
            }
            sourcePoint = null;
            targetPoint = null;
            provider.revalidate();
            provider.repaint(); 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}        
        public void mouseDragged(MouseEvent e) {
            targetPoint = e.getPoint(); 
            Rectangle divRect = divider.getBounds();
            int ny = divRect.y + targetPoint.y; 
            if (ny < 10) { 
                targetPoint.y = (divRect.y * -1)+10; 
            } else {
                int h = viewRect.height-divRect.y;
                int limit = h - getDividerSize() - 10;
                if (targetPoint.y > limit) targetPoint.y = limit;
            }             
            provider.paintDividerHandle(viewRect, divRect, targetPoint); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HorizontalLayout (Class) ">

    private class HorizontalLayout implements LayoutManager 
    {
        SplitViewLayout root = SplitViewLayout.this; 
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;

                w += getDividerSize();
                h += getDividerSize();

                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w,h); 
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component divider = getDivider();
                parent.remove(divider);
                parent.add(divider); 

                Insets margin = parent.getInsets();
                int pw = parent.getWidth(), ph = parent.getHeight();
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                viewRect = new Rectangle(x, y, w, h); 

                if (locationIndex < 0) 
                    locationIndex = 0; 
                else if (locationIndex >= (pw-margin.right)) 
                    locationIndex = (pw-margin.right)-getDividerSize();

                Component[] comps = getLayoutComponents(parent.getComponents());
                if (comps.length >= 1) {
                    comps[0].setBounds(x, y, locationIndex, h); 
                }
                
                x += locationIndex;
                divider.setBounds(x, y, getDividerSize(), h); 
                x += getDividerSize();

                int rw = (pw-margin.right)-x;
                if (rw < 0) rw = 0;
                if (comps.length >= 2) {
                    comps[1].setBounds(x, y, rw, h); 
                }                
            }
        }
    }

    //</editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" VerticalLayout (Class) ">

    private class VerticalLayout implements LayoutManager 
    {
        SplitViewLayout root = SplitViewLayout.this; 
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;

                w += getDividerSize();
                h += getDividerSize();

                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w,h); 
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component divider = getDivider();
                parent.remove(divider);
                parent.add(divider); 

                Insets margin = parent.getInsets();
                int pw = parent.getWidth(), ph = parent.getHeight();
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                viewRect = new Rectangle(x, y, w, h); 

                if (locationIndex < 0) 
                    locationIndex = 0; 
                else if (locationIndex >= (ph-margin.bottom)) 
                    locationIndex = (ph-margin.bottom)-getDividerSize();

                Component[] comps = getLayoutComponents(parent.getComponents());
                if (comps.length >= 1) {
                    comps[0].setBounds(x, y, w, locationIndex); 
                }

                y += locationIndex;
                divider.setBounds(x, y, w, getDividerSize()); 
                y += getDividerSize();

                int nh = (ph-margin.bottom)-y;
                if (nh < 0) nh = 0;
                if (comps.length >= 2) {
                    comps[1].setBounds(x, y, w, nh); 
                }
            }
        }
    }

    //</editor-fold>        
}
