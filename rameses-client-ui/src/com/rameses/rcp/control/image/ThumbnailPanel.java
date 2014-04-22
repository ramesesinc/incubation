/*
 * ThumbnailPanel.java
 *
 * Created on April 21, 2014, 11:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
public class ThumbnailPanel extends JPanel 
{
    private Dimension cellSize;
    private int cellSpacing;
    private int columnCount;
    
    public ThumbnailPanel() {
        super.setLayout(new DefaultLayout()); 
        setBackground(Color.decode("#808080"));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));         
        cellSize = getDefaultCellSize();
        cellSpacing = 2;
        columnCount = 5;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public void setLayout(LayoutManager layout) {}
    
    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing = cellSpacing;
    }
    
    public int getColumnCount() { return columnCount; } 
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount; 
    }

    protected Dimension getDefaultCellSize() {
        return new Dimension(35, 35);
    }
    
    public Dimension getCellSize() { return cellSize; } 
    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize; 
    }
    
    private Dimension getPreferredCellSize() {
        Dimension size = getCellSize();
        if (size == null) size = getDefaultCellSize();
        
        return new Dimension(size.width, size.height); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    protected void onselect(Object item) {
    }
    
    protected void onopen(Object item) {
        
    }
    
    public void add(Map map) {
        if (map == null) return;
        
        Object ocaption = map.get("caption");
        Object oimage = map.get("image");
        if (!(oimage instanceof byte[])) {
            oimage = new byte[0];
        }
        
        ImageIcon icon = new ImageIcon((byte[]) oimage);
        ImageThumbnail img = new ImageThumbnail(map, icon); 
        if (ocaption != null) img.setToolTipText(ocaption.toString()); 
        
        add(img); 
    }
    
    private void setSelectedComponent(ImageThumbnail image) {
        ImageThumbnail firstItem = null;        
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            Component c = comps[i];
            if (!c.isVisible()) continue;
            if (!(c instanceof ImageThumbnail)) continue;
            
            ImageThumbnail im = (ImageThumbnail)c;
            if (firstItem == null) firstItem = im;
            
            im.setSelected(false); 
            im.repaint();
        }

        final ImageThumbnail sel = (image == null? firstItem: image);
        if (sel == null) return;
        
        sel.setSelected(true);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                onselect(sel.getData()); 
            }
        });
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        private Component[] getVisibleComponents(Container parent) {
            List<Component> list = new ArrayList();
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (c.isVisible()) list.add(c);
            }
            return (Component[]) list.toArray(new Component[]{}); 
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Component[] comps = getVisibleComponents(parent); 
                int columnCount = getColumnCount();
                if (columnCount < 0) columnCount = comps.length; 
                
                int cols = 0, rows = 0;
                if (comps.length <= columnCount) {
                    cols = comps.length;
                    rows = 1;
                } else {
                    cols = columnCount;
                    rows = comps.length / columnCount;
                    if (comps.length % columnCount > 0) rows += 1;
                } 

                Dimension cellSize = getPreferredCellSize();                 
                int w = cols * cellSize.width;
                w += Math.max(cols-1,0) * getCellSpacing();
                
                int h = rows * cellSize.height;
                h += Math.max(rows-1, 0) * getCellSpacing(); 
                
                Insets margin = parent.getInsets(); 
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = getVisibleComponents(parent); 
                int columnCount = getColumnCount();
                if (columnCount < 0) columnCount = comps.length; 
                
                int cols = 0, rows = 0;
                if (comps.length <= columnCount) {
                    cols = comps.length;
                    rows = 1;
                } else {
                    cols = columnCount;
                    rows = comps.length / columnCount;
                    if (comps.length % columnCount > 0) rows += 1;
                }                 

                boolean has_components = false;                
                for (int r=0; r < rows; r++) {
                    if (r > 0) {
                        x = margin.left;
                        y += getCellSpacing();
                    }
                    
                    for (int i=0; i < cols; i++) {
                        int idx = (r*cols) + i;
                        if (idx >= comps.length) break; 
                        if (i > 0) x += getCellSpacing();
                        
                        Component c = comps[idx];
                        c.setBounds(x, y, cellSize.width, cellSize.height);
                        x += cellSize.width; 
                    }
                    y += cellSize.height;
                }
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ImageThumbnail "> 
       
    private class ImageThumbnail extends JLabel 
    {
        private Map data;
        private ImageIcon icon;
        private boolean selected;
        
        ImageThumbnail(Map data, ImageIcon icon) {
            this.data = data;
            this.icon = icon;
            setPreferredSize(getPreferredCellSize()); 
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e)) return;
                    if (e.getClickCount() == 1) {
                        setSelectedComponent(ImageThumbnail.this); 
                    } else if (e.getClickCount() == 2) {
                        fireOnOpen();
                    }
                }
            }); 
        }
        
        public ImageIcon getOriginalIcon() { return icon; }
        public Map getData() { return data; } 
        
        public boolean isSelected() { return selected; } 
        public void setSelected(boolean selected) {
            this.selected = selected; 
        }
        
        private void fireOnOpen() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                   onopen(getData());  
                }
            });
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (icon == null) return;

            int width = getWidth();
            int height = getHeight();            
            Dimension newsize = getScaledSize(icon, new Dimension(width, height));    
            int nx = (width - newsize.width) / 2;
            int ny = (height - newsize.height) / 2;
            Graphics2D g2 = (Graphics2D)g.create();            
            g2.drawImage(icon.getImage(), nx, ny, newsize.width, newsize.height, null);
            g2.dispose();

            if (isSelected()) {
                g2 = (Graphics2D)g.create(); 
                g2.setColor(Color.WHITE);
                g2.drawRect(0, 0, width-1, height-1); 
                g2.dispose();
                return; 
            }
            
//            g2 = (Graphics2D)g.create(); 
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            Color oldColor = g2.getColor();
//            Composite oldComposite = g2.getComposite(); 
//            Composite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f);
//            g2.setComposite(newComposite); 
//            g2.setColor(Color.BLACK);
//            g2.fillRect(nx, ny, newsize.width, newsize.height);      
//            g2.dispose(); 
        }        
    } 
    
    private Dimension getScaledSize(ImageIcon icon, Dimension size) {
        if (icon == null) return null; 
        
        int iw = icon.getIconWidth(); 
        int ih = icon.getIconHeight(); 
        if (iw < size.width && ih < size.height) {
            return new Dimension(iw, ih); 
        }
        
        double scaleX = (double)size.width  / (double)iw;
        double scaleY = (double)size.height / (double)ih;
        double scale  = (scaleY > scaleX)? scaleX: scaleY;
        int nw = (int) (iw * scale);
        int nh = (int) (ih * scale);
        return new Dimension(nw, nh); 
    }    
    
    // </editor-fold>
    
}
