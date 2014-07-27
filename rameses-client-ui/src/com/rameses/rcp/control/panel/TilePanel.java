/*
 * TilePanel.java
 *
 * Created on July 26, 2014, 6:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.panel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class TilePanel extends JPanel  
{
    private Object selectedItem;    
    private Dimension cellSize;
    private Insets padding;
    private int cellSpacing;
    private String textAlignment;
    private String textPosition; 
    private boolean showCaptions;
    
    public TilePanel() {
        super.setLayout(new ContainerLayout()); 
        cellSize = new Dimension(120, 80);
        padding = new Insets(5,5,5,5); 
        cellSpacing = 5;
        showCaptions = true;
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                try {
                    clearSelection(); 
                } catch(Throwable t) {
                    t.printStackTrace(); 
                }
            }
        });
    }
    
    public void setLayout(LayoutManager mgr) {;}
    
    public String getTextAlignment() { return this.textAlignment; }
    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }
    
    public String getTextPosition() { return this.textPosition; }
    public void setTextPosition(String textPosition) {
        this.textPosition = textPosition;
    }
    
    public boolean isShowCaptions() { return showCaptions; }
    public void setShowCaptions(boolean showCaptions) { 
        this.showCaptions = showCaptions; 
    } 
    
    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing = cellSpacing; 
    }
    
    public Dimension getCellSize() { return cellSize; } 
    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize; 
    }
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) {
        this.padding = padding;
    }
    
    public void addItem(String caption, Object userObject) {
        addItem(caption , userObject); 
    }
    
    public void addItem(String caption, Object userObject, ImageIcon icon) {
        TileItem ti = new TileItem(caption, userObject, icon);
        super.add(ti); 
    }    
    
    public Object getSelectedItem() { 
        return selectedItem; 
    } 

    public TileItem getItem(Object userObject) {
        int idx = indexOf(userObject); 
        return getItem(idx); 
    }
    
    public TileItem getItem(int index) {
        try {
            return (TileItem) getComponent(index); 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    public void removeItem(Object userObject) {
        int idx = indexOf(userObject); 
        removeItem(idx); 
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < getComponentCount()) {
            remove(index); 
        }
    }
    
    public int indexOf(Object userObject) {
        Component[] comps = getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if (!(comps[i] instanceof TileItem)) continue; 
            
            TileItem item = (TileItem)comps[i]; 
            Object itemobj = item.getUserObject(); 
            if (userObject == null && itemobj == null) {
                return i; 
            } else if (userObject != null && userObject.equals(itemobj)) {
                return i; 
            } else if (itemobj != null && itemobj.equals(userObject)) {
                return i;
            }
        }
        return -1; 
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof TileItem) {
            super.addImpl(comp, constraints, index); 
        } else {
            throw new IllegalStateException("This container only supports TileItem component. Please use addItem to correct this.");
        }
    }
    
    protected void onselect(Object item) {
    }
    
    private void clearSelection() {
        Component[] comps = getComponents(); 
        for (int i=0; i<comps.length; i++) {
            if (comps[i] instanceof TileItem) {
                TileItem ti = (TileItem)comps[i]; 
                ti.setSelected(false); 
                ti.repaint();
            } 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" TileItem ">
    
    class TileItem extends JLabel 
    {
        private Color selBackground;
        private Color selBorderBackground;
        private String text;
        private Object userObject;
        private ImageIcon icon;
        
        private boolean mouse_entered;
        private boolean selected;
        
        public TileItem(String text, Object userObject) {
            this(text, userObject, null); 
        }
        
        public TileItem(String text, Object userObject, ImageIcon icon) {
            super();
            this.text = text; 
            this.icon = icon; 
            this.userObject = userObject;  
            
            if (isShowCaptions()) { 
                setText("<html>"+ text +"</html>"); 
            }
            selBackground = Color.decode("#c1dcfc");
            selBorderBackground = Color.decode("#7da2ce"); 
            setBorder(BorderFactory.createEmptyBorder(0,0,0,3));
            new TileItemMouseAdapter(this); 
        } 
        
        public Object getUserObject() { return userObject; } 
        public void setUserObject(Object userObject) {
            this.userObject = userObject; 
        }
        
        public Icon getIcon() { return icon; }
        public void setIcon(ImageIcon icon) {
            this.icon = icon; 
        }
        
        boolean isMouseEntered() { return mouse_entered; } 
        void setMouseEntered(boolean mouse_entered) {
            this.mouse_entered = mouse_entered; 
        }
        
        boolean isSelected() { return selected; } 
        void setSelected(boolean selected) { 
            this.selected = selected; 
        } 

        protected void paintComponent(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(selBackground);
                g2.fillRoundRect(0, 0, width-1, height-1, 3, 3); 
            } else if (isMouseEntered()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f)); 
                g2.setColor(selBackground);
                g2.fillRoundRect(0, 0, width-1, height-1, 3, 3); 
            }
            g2.dispose(); 
            
            super.paintComponent(g); 
        }

        public void paint(Graphics g) {
            super.paint(g); 
            
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(selBorderBackground);
                g2.drawRoundRect(0, 0, width-1, height-1, 3, 3); 
            } else if (isMouseEntered()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f)); 
                g2.setColor(selBorderBackground);
                g2.drawRoundRect(0, 0, width-1, height-1, 3, 3);
            } 
            g2.dispose(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TileItemMouseAdapter ">
    
    private class TileItemMouseAdapter implements MouseListener
    {
        private TileItem source; 
        private boolean pressed;
        private boolean processing;
        
        TileItemMouseAdapter(TileItem source) {
            this.source = source;
            source.addMouseListener(this); 
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            pressed = true; 
            clearSelection();
            source.setSelected(true);
            source.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            if (pressed) {
                pressed = false; 
                clearSelection();
                source.setSelected(true);
                source.repaint();
                
                try {
                    Thread.currentThread().sleep(68);
                } catch(InterruptedException ie) {;}
                
                if (e.getClickCount() == 2) {
                    //do not process when double-click 
                    return; 
                }
                
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        doClick();
                    }
                });
            }
        }

        public void mouseEntered(MouseEvent e) {
            Component[] comps = getComponents(); 
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof TileItem) {
                    TileItem ti = (TileItem)comps[i]; 
                    ti.setMouseEntered(false); 
                    ti.repaint();
                }
            }
            source.setMouseEntered(true); 
            source.repaint(); 
        }

        public void mouseExited(MouseEvent e) {
            if (pressed) {
                pressed = false; 
                source.setSelected(false); 
            }
            source.setMouseEntered(false); 
            source.repaint(); 
        }
        
        void doClick() {
            selectedItem = source.getUserObject(); 
            onselect(selectedItem); 
        }
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout (Class) ">
    
    private class ContainerLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension newdim = new Dimension(0, 0);
                Dimension celldim = getCellSize();
                Insets margin = parent.getInsets();
                boolean has_visible_components = false; 
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    if (!(comps[i] instanceof TileItem)) continue; 
                    
                    TileItem c = (TileItem) comps[i];
                    if (!c.isVisible()) continue;
                    if (has_visible_components) {
                        newdim.width += Math.max(getCellSpacing(), 0); 
                    }
                    
                    newdim.width += celldim.width;
                    newdim.height = celldim.height; 
                    has_visible_components = true;
                }

                Insets pads = getPadding();
                if (pads != null) {
                    newdim.width += (margin.left + margin.right + pads.left + pads.right);
                    newdim.height += (margin.top + margin.bottom + pads.top + pads.bottom);
                }
                return newdim; 
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets pads = getPadding();
                if (pads == null) pads = new Insets(0,0,0,0); 
                
                Dimension celldim = getCellSize();
                Insets margin = parent.getInsets();
                int x = margin.left + pads.left;
                int y = margin.top + pads.top;
                int w = parent.getWidth() - (margin.left + margin.right + pads.left + pads.right);
                int h = parent.getHeight() - (margin.top + margin.bottom + pads.top + pads.bottom);
                int rb = parent.getWidth() - (margin.right + pads.right);
                
                boolean firstItemInRow = true; 
                boolean has_visible_components = false; 
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    if (!(comps[i] instanceof TileItem)) continue; 
                    
                    TileItem c = (TileItem) comps[i];
                    if (!c.isVisible()) continue;
                    if (has_visible_components) {
                        x += Math.max(getCellSpacing(), 0); 
                    }
                    
                    if (firstItemInRow) {
                        firstItemInRow = false;                         
                        c.setSize(celldim.width, celldim.height); 
                        c.setLocation(x, y); 
                    } else if (x + celldim.width > rb) {
                        y += (celldim.height + Math.max(getCellSpacing(), 0));
                        x = margin.left + pads.left;
                        firstItemInRow = false;    
                        c.setSize(celldim.width, celldim.height); 
                        c.setLocation(x, y); 
                    } else {
                        c.setSize(celldim.width, celldim.height); 
                        c.setLocation(x, y); 
                    }
                    x += celldim.width; 
                    has_visible_components = true;
                }
            }
        }
    }
    
    //</editor-fold>        
}
