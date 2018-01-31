/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt.components;

import com.rameses.filemgmt.FileManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
public class ThumbnailViewPanel2 extends JPanel {
    
    private Dimension cellSize; 
    private int cellSpacing;
    
    private Object selectedItem;
    
    private Dimension dynaPrefSize; 
    
    public ThumbnailViewPanel2() {
        initComponents(); 
    }
    
    public Dimension getCellSize() { return cellSize; }
    public void setCellSize( Dimension cellSize ) {
        this.cellSize = cellSize; 
    }
    
    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing( int cellSpacing ) {
        this.cellSpacing = cellSpacing;
    }
    
    public Object getSelectedItem() { return selectedItem; }
    public void setSelectedItem( Object selectedItem ) {
        this.selectedItem = selectedItem; 
    }
    
    public void selectionChanged() {
    }
    
    public void clearItems() {
        setSelectedItem( null ); 
        selectionChanged();         
        removeAll(); 
        revalidate();
        repaint();
    }
    
    public ItemInfo addItem( Object userObject ) { 
        ThumbnailItem item = new ThumbnailItem( userObject ); 
        add( item );
        
        ItemInfo info = item.getInfo(); 
        info.image = FileManager.getInstance().getFileTypeIcon("docx"); 
        return info; 
    }
    
    public void refresh() {
        Runnable proc = new Runnable() {
            public void run() {
                revalidate();
                repaint(); 
            }
        }; 
        if ( SwingUtilities.isEventDispatchThread() ) {
            proc.run(); 
        } else {
            SwingUtilities.invokeLater(proc);
        }
    }
    
 
    // <editor-fold defaultstate="collapsed" desc="initComponents">
    private void initComponents() { 
        setLayout( new MainLayoutManager()); 
        setPreferredSize(new Dimension(200, 100)); 
        setCellSize(new Dimension(50, 50) ); 
        setCellSpacing( 5 ); 
        
        addItem( null ).title = "Item 1"; 
        addItem( null ).title = "Item 2"; 
        addItem( null ).title = "Item 3"; 
    } 
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="ThumbnailItem">
    public class ItemInfo {
        public String title;
        public Object thumbnail;
        
        private ImageIcon image; 
        
        boolean hasImage() {
            return (image != null); 
        }
    }
    
    private class ThumbnailItem extends JLabel implements FocusListener, MouseListener {
        
        ThumbnailViewPanel2 root = ThumbnailViewPanel2.this; 
        
        private Color focusInBorderColor; 
        private Color focusOutBorderColor; 
        private Color hoverBorderColor; 
        
        private boolean hasfocus;
        private boolean hasMouseFocus;
        private Object userObject;
        private ItemInfo info;
        
        ThumbnailItem() {
            this( null ); 
        }
        
        ThumbnailItem( Object userObject ) {
            this.userObject = userObject; 
            this.info = new ItemInfo();
            
            addFocusListener( this );
            addMouseListener( this );
            setFocusable(true); 
            setHorizontalAlignment( SwingConstants.CENTER); 
            
            focusInBorderColor = Color.BLUE; 
            focusOutBorderColor = Color.decode("#afafaf"); 
            hoverBorderColor = Color.decode("#D3D8FF"); 
            
        }
        
        public ItemInfo getInfo() {
            return info; 
        }

        public void focusGained(FocusEvent e) { 
            hasfocus = true; 
            repaint(); 
            
            root.setSelectedItem( userObject ); 
            root.selectionChanged(); 
        }

        public void focusLost(FocusEvent e) { 
            if ( e.isTemporary() ) return; 
            
            hasfocus = false; 
            repaint(); 
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            requestFocus();
            requestFocusInWindow(); 
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) { 
            hasMouseFocus = true;
            repaint();
        }

        public void mouseExited(MouseEvent e) {
            hasMouseFocus = false;
            repaint(); 
        }

        public void paint(Graphics g) {
            super.paint(g);
            
            int w = getWidth(), h = getHeight(); 
            
            ItemInfo info = getInfo(); 
            if ( info.hasImage() ) {
                Rectangle rect = scaleToFitRect(); 
                Graphics2D g2 = (Graphics2D) g.create(); 
                try { 
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.drawImage( info.image.getImage(), rect.x, rect.y, rect.width, rect.height, null ); 
                } finally { 
                    g2.dispose(); 
                } 
            }

            Graphics2D g2 = (Graphics2D) g.create(); 
            try { 
                if ( hasfocus ) {
                    g2.setColor( focusInBorderColor ); 
                } else {
                    g2.setColor( focusOutBorderColor ); 
                }
                g2.drawRect(0, 0, w-1, h-1);

                if ( hasMouseFocus ) {
                    g2.setColor( hoverBorderColor ); 
                    g2.drawRect(1, 1, w-3, h-3); 
                }             
            } finally {
                g2.dispose();
            } 
        } 
        
        private Rectangle scaleToFitRect() {
            ImageIcon iicon = getInfo().image; 
            if ( iicon == null ) return null;

            Dimension cellsize = root.getCellSize();
            int iw = iicon.getIconWidth();
            int ih = iicon.getIconHeight(); 
            int cw = cellsize.width;
            int ch = cellsize.height;
            double scaleX = (double)cw  / (double)iw;
            double scaleY = (double)ch / (double)ih;
            double scale  = (scaleY > scaleX)? scaleX: scaleY;
            int nw = (int) (iw * scale);
            int nh = (int) (ih * scale);
            int nx = (cw/2)-(nw/2);
            int ny = (ch/2)-(nh/2); 
            return new Rectangle(nx, ny, nw, nh);         
        }        
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="MainLayoutManager">
    private class CellLayouter {
        
        ThumbnailViewPanel2 parent = ThumbnailViewPanel2.this; 

        Dimension layout( boolean autoSetBounds ) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth(); 
                int ph = parent.getHeight(); 
                int x = margin.left; 
                int y = margin.top;
                int w = pw - (margin.left + margin.right); 
                int h = ph - (margin.top + margin.bottom); 
                
                int rMaxPos = pw - margin.right; 
                int spacing = parent.getCellSpacing();
                Dimension cellsize = parent.getCellSize(); 
                Component[] comps = getVisibleComponents(); 

                int col = 0, row = 0; 
                int cx = margin.left, cy = margin.top; 
                int fixW = cellsize.width, fixH = cellsize.height; 
                
                ArrayList<CellInfo> cells = new ArrayList();
                for (int i=0; i<comps.length; i++) { 
                    CellInfo ci = new CellInfo();
                    ci.colIndex = col; 
                    ci.rowIndex = row; 
                    ci.x = cx; 
                    ci.y = cy; 
                    ci = layoutCell(ci, margin, rMaxPos); 
                    if ( ci.rowIndex == row ) {
                        col = ci.colIndex + 1; 
                        cx = ci.x + fixW + spacing; 
                        cy = ci.y;
                    } else {
                        col = ci.colIndex + 1; 
                        row = ci.rowIndex; 
                        cx = ci.x + fixW + spacing; 
                        cy = ci.y; 
                    }
                    
                    cells.add( ci ); 
                    if ( autoSetBounds ) {
                        comps[i].setBounds(ci.x, ci.y, fixW, fixH); 
                    }
                } 
                
                int maxW = 0, maxH = 0;
                for (int i=0; i<=row; i++) {
                    Dimension dim = computeRowDimension( cells, i, cellsize, spacing ); 
                    if ( dim != null ) {
                        maxW = Math.max( maxW, dim.width ); 
                        maxH += (dim.height + spacing); 
                    } 
                }
                maxW += (margin.left + margin.right); 
                maxH += (margin.top + margin.bottom); 
                cells.clear(); 
                
                return new Dimension( maxW, maxH ); 
            }             
        }
        
        Component[] getVisibleComponents() {
            ArrayList<Component> list = new ArrayList();
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++ ) { 
                if ((comps[i] instanceof ThumbnailItem) && comps[i].isVisible() ) {
                    list.add( comps[i]); 
                }
            }
            return list.toArray(new Component[]{}); 
        }    
        
        CellInfo layoutCell( CellInfo ci, Insets margin, int rMaxPos ) { 
            int fixW = parent.getCellSize().width; 
            int fixH = parent.getCellSize().height;
            int spacing = parent.getCellSpacing();
            int npos = ci.x + fixW; 
            if ( npos > rMaxPos ) { 
                ci.colIndex = 0; 
                ci.rowIndex += 1; 
                ci.x = margin.left; 
                ci.y += (fixH + spacing);
            } 
            return ci;
        } 
        
        Dimension computeRowDimension( ArrayList<CellInfo> cells, int row, Dimension celldim, int spacing ) {
            int w = 0;
            for (int i=0; i<cells.size(); i++) {
                CellInfo ci = cells.get(i); 
                if ( ci.rowIndex == row ) {
                    w += (celldim.width + spacing ); 
                }
            } 
            if ( w == 0 ) return null; 
            return new Dimension( w, celldim.height );  
        }        
    }
    
    
    private class MainLayoutManager implements LayoutManager {

        ThumbnailViewPanel2 root = ThumbnailViewPanel2.this; 
        
        public void addLayoutComponent(String name, Component comp) { 
        }
        public void removeLayoutComponent(Component comp) { 
        }

        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return getLayoutSize( parent );
            }
        }

        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension dim = root.getCellSize();
                int w = dim.width; 
                int h = dim.height; 
                Insets margin = parent.getInsets(); 
                w += (margin.left + margin.right); 
                h += (margin.top + margin.bottom);
                return new Dimension( w, h );  
            }
        }
        
        private Dimension getLayoutSize(Container parent) {
            Dimension dim = root.getCellSize();
            int w = dim.width; 
            int h = dim.height; 
            Insets margin = parent.getInsets(); 
            w += (margin.left + margin.right); 
            h += (margin.top + margin.bottom);
            return new Dimension( w, h );  
        }
        
        public void layoutContainer(Container parent) {
            CellLayouter c = new CellLayouter(); 
            c.layout( true ); 
        }  
    }
    
    private class CellInfo {
        int rowIndex;
        int colIndex;
        int x;
        int y;
    }
    // </editor-fold> 
    
}
