/*
 * XPhoto.java
 *
 * Created on December 4, 2013, 11:10 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.beans.Beans;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class XPhoto extends JLabel implements UIControl, MouseEventSupport.ComponentInfo 
{
    final static String DEFAULT_NO_IMAGE_ICON = "com/rameses/rcp/icons/photo.png";
            
    private Binding binding;
    private String[] depends;
    private int index; 
    
    private boolean dynamic;
    private ImageIcon iicon; 
    
    private NoImageCanvas noImageCanvas;
    private ImageCanvas imageCanvas;

    private String noImageIcon;    
    private Color noImageBackground;
    private Color noImageForeground; 
    private boolean showNoImageText;
    private boolean showNoImageIcon;
    
    public XPhoto() { 
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new DefaultLayout()); 
        setOpaque(false); 
        setPreferredSize(new Dimension(120, 100));
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        setNoImageIcon(DEFAULT_NO_IMAGE_ICON); 
        setShowNoImageIcon(true);
        setShowNoImageText(true);
        
        noImageCanvas = new NoImageCanvas(); 
        imageCanvas = new ImageCanvas(); 

        Font oldFont = getFont(); 
        if (oldFont != null) {
            setFont(oldFont.deriveFont(Font.BOLD, 14.0f));
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    public void setLayout(LayoutManager mgr) {}
    
    public void setName(String name) { 
        super.setName(name); 
        
        if (Beans.isDesignTime()) 
            super.setText((name == null? "": name));
    } 
        
    public void setText(String text) { 
        if (Beans.isDesignTime()) super.setText(text); 
    }
    
    public void setFont(Font font) {
        super.setFont(font);
        if (noImageCanvas != null) noImageCanvas.setFont(font); 
    }
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; } 
    
    public String getNoImageIcon() { return noImageIcon; } 
    public void setNoImageIcon(String noImageIcon) {
        this.noImageIcon = noImageIcon; 
    }
    
    public Color getNoImageBackground() { return noImageBackground; } 
    public void setNoImageBackground(Color noImageBackground) { 
        this.noImageBackground = noImageBackground; 
    } 
    
    public Color getNoImageForeground() { return noImageForeground; } 
    public void setNoImageForeground(Color noImageForeground) { 
        this.noImageForeground = noImageForeground; 
    } 
    
    public boolean isShowNoImageText() { return showNoImageText; } 
    public void setShowNoImageText(boolean showNoImageText) { 
        this.showNoImageText = showNoImageText; 
    } 
    
    public boolean isShowNoImageIcon() { return showNoImageIcon; } 
    public void setShowNoImageIcon(boolean showNoImageIcon) { 
        this.showNoImageIcon = showNoImageIcon; 
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">    
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }

    public void load() {
    }

    public void refresh() {
        try {
            Object beanValue = null; 
            String name = getName(); 
            if (name != null && name.length() > 0) {
                beanValue = UIControlUtil.getBeanValue(getBinding(), name);
            } 
            
            if (beanValue instanceof byte[]) { 
                iicon = new ImageIcon((byte[]) beanValue); 
            } else if (beanValue instanceof URL) {
                iicon = new ImageIcon((URL) beanValue); 
            } else if (beanValue instanceof ImageIcon) {
                iicon = (ImageIcon) beanValue;
            } else if (beanValue instanceof String) { 
                String str = beanValue.toString().toLowerCase(); 
                if (str.matches("[a-zA-Z]{1,}://.*")) { 
                    iicon = new ImageIcon(new URL(beanValue.toString())); 
                } else { 
                    iicon = null; 
                } 
            } else { 
                iicon = null; 
            }             
        } 
        catch(Throwable e) { 
            iicon = null; 
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace();
        } 
        
        removeAll();
        revalidate(); 
        repaint(); 
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }  
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation "> 
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        return map;
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods "> 
    
    public void paint1(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        if (iicon == null) { 
            paintNoImage(g2, w, h); 
        } else { 
            paintImage(g2, w, h, iicon);
        } 
        g2.dispose();
    }
    
    protected void paintNoImage(Graphics2D g2, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(Color.BLACK);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, 0, width-1, height-1);
        g2.drawLine(0, height-1, width-1, 0);

        String str = "No Available"; 
        FontMetrics metrics = g2.getFontMetrics(getFont());
        int fw = metrics.stringWidth(str);
        int fh = metrics.getHeight();
        int x = Math.max(((width - fw) / 2), 0);
        int y = Math.max(((height / 2) - (fh / 2)), 0) + 4;
        
        g2.setFont(getFont());
        g2.setColor(Color.WHITE);
        g2.drawString(str, x, y);
        
        str = "Photo";
        fw = metrics.stringWidth(str);
        fh = metrics.getHeight();        
        x = Math.max(((width - fw) / 2), 0);
        y = Math.max(((height / 2) + (fh / 2)), 0) + 4;
        g2.drawString(str, x, y);
    }
    
    protected void paintImage(Graphics2D g2, int width, int height, ImageIcon anIcon) {
        g2.setBackground(getBackground()); 
        g2.clearRect(0, 0, width, height);
        g2.setBackground(Color.YELLOW);
        g2.fillRect(0, 0, width, height); 
        g2.drawImage(anIcon.getImage(), 0, 0, null); 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NoImageCanvas "> 
        
    private class NoImageCanvas extends JLabel 
    {
        private ImageIcon photoIcon;    
    
        private ImageIcon getPhotoIcon() {
            if (photoIcon == null) {
                try {
                    ClassLoader loader = null;
                    if ( Beans.isDesignTime() ) 
                        loader = getClass().getClassLoader();
                    else 
                        loader = ClientContext.getCurrentContext().getClassLoader();

                    URL url = loader.getResource(getNoImageIcon());
                    photoIcon = new ImageIcon(url);
                } catch (Throwable ex) {
                    System.out.println("[WARN] failed to load icon caused by " + ex.getClass().getName() + ": " + ex.getMessage()); 
                }            
            }
            return photoIcon; 
        }
        
        public void paint(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D)g.create(); 
            Color oldColor = g2.getColor();            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color bgcolor = getNoImageBackground();
            if (bgcolor == null) bgcolor = Color.DARK_GRAY;
            
            g2.setColor(bgcolor);
            g2.fillRect(0, 0, width, height);

            if (isShowNoImageIcon()) { 
                ImageIcon photoIcon = getPhotoIcon();
                if (photoIcon != null) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, width, height);                    
                    g2.drawImage(photoIcon.getImage(), 0, 0, width, height, null); 
                }
            } 
            if (isShowNoImageText()) { 
                String str = "No Available"; 
                FontMetrics metrics = g2.getFontMetrics(getFont());
                int fw = metrics.stringWidth(str);
                int fh = metrics.getHeight();
                int x = Math.max(((width - fw) / 2), 0);
                int y = Math.max(((height / 2) - (fh / 2)), 0) + 4;
                
                Color shadowColor = Color.decode("#808080");
                Color forecolor = getNoImageForeground();
                if (forecolor == null) forecolor = Color.WHITE;

                g2.setFont(getFont());
                g2.setColor(shadowColor);
                g2.drawString(str, x, y+1);            
                g2.setColor(forecolor);
                g2.drawString(str, x, y);

                str = "Photo";
                fw = metrics.stringWidth(str);
                fh = metrics.getHeight();        
                x = Math.max(((width - fw) / 2), 0);
                y = Math.max(((height / 2) + (fh / 2)), 0) + 4;
                g2.setColor(shadowColor);
                g2.drawString(str, x, y+1);            
                g2.setColor(forecolor);            
                g2.drawString(str, x, y); 
            } 
            g2.dispose();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ImageCanvas "> 
    
    private class ImageCanvas extends JLabel 
    {
        XPhoto root = XPhoto.this;
        
        ImageCanvas() {
            setHorizontalAlignment(SwingConstants.CENTER); 
            setOpaque(true); 
            setBackground(Color.WHITE); 
        }

        public void paintComponent(Graphics g) { 
            super.paintComponent(g);
            if (root.iicon == null) return;

            ImageIcon iicon = root.iicon;  
            int iw = iicon.getIconWidth(); 
            int ih = iicon.getIconHeight(); 
            int width = getWidth();
            int height = getHeight(); 
            int nw=0, nh=0;
            if (iw / width <  ih / height) {
                nw = width;
                nh = (int) Math.floor((double)ih * (double)width / (double)iw);
            } else {
                nh = height;
                nw = (int) Math.floor((double)iw * (double)height / (double)ih);
            } 
            
            if (nw > width) nw = width;
            if (nh > height) nh = height; 
            
            int x = Math.max((width - nw) / 2, 0);
            int y = Math.max((height - nh) / 2, 0);            
            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.drawImage(iicon.getImage(), x, y, nw, nh, null); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
        XPhoto root = XPhoto.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int width = margin.left + margin.right;
                int height = margin.top + margin.bottom;
                return new Dimension(width, height); 
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
                
                Component c = null;
                if (iicon == null) {
                    c = noImageCanvas; 
                } else {
                    c = imageCanvas;
                }                 
                if (c.getParent() == null) {
                    parent.add(c);
                } 
                c.setBounds(x, y, w, h); 
            } 
        }
    }
    
    // </editor-fold>
}
