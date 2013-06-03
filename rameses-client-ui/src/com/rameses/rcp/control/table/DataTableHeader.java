/*
 * DataTableHeader.java
 *
 * Created on May 31, 2013, 4:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.support.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.AbstractBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author wflores
 */
public class DataTableHeader extends JTableHeader 
{
    private JTable table;
    
    public DataTableHeader(JTable table) 
    {
        super(table.getColumnModel());
        super.setBorder(new CustomBorder(table, null));
        this.table = table; 
    }
    
    public void paint(Graphics g) 
    {
        Rectangle clip = g.getClipBounds();
        super.paint(g);
        
        Rectangle compClip = new Rectangle();
        TableColumnModel tcm = getColumnModel();
        for (int i=0; i<tcm.getColumnCount(); i++) 
        {
            Rectangle rect = getHeaderRect(i);
            compClip.x = rect.x + rect.width;
            compClip.y = rect.y;
            compClip.width = rect.x + rect.width;
            compClip.height = Math.max(compClip.height, rect.height);
        }
        
        int nWidth = clip.width - compClip.width;
        if (nWidth <= 0) return;
        
        Graphics2D g2 = (Graphics2D) g.create();
        Color bg = Color.LIGHT_GRAY;
        GradientPaint gp = new GradientPaint(0, 0, ColorUtil.brighter(bg, 30), 0, (clip.height-1)/2, ColorUtil.brighter(bg, 15));
        g2.setPaint(gp);
        g2.fillRect(compClip.x, 0, nWidth, clip.height-1);
        g2.dispose();
    } 
    
    // <editor-fold defaultstate="collapsed" desc="  Painter (class)  ">    
    
    public static class Painter 
    {
        public void paint(JComponent comp, Graphics g, int x, int y, int w, int h) 
        {
            Graphics2D g2 = (Graphics2D) g.create();
            Color bg = Color.LIGHT_GRAY;
            GradientPaint gp = new GradientPaint(0, 0, ColorUtil.brighter(bg, 30), 0, (h-1)/2, ColorUtil.brighter(bg, 15));
            g2.setPaint(gp);
            g2.fillRect(x, y, w, h-1);
            g2.dispose();    
        }
    }
    
    // </editor-fold>
    
   // <editor-fold defaultstate="collapsed" desc="  CustomBorder (class)  ">    
    
    public static class CustomBorder extends AbstractBorder 
    {
        private JTable table;
        private Insets margin;
        
        public CustomBorder(JTable table) {
            this(table, null); 
        }
        
        public CustomBorder(JTable table, Insets margin) 
        {
            this.table = table;
            this.margin = (margin == null? new Insets(0,0,0,0): margin); 
        }
        
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, margin); 
        }
        
        public Insets getBorderInsets(Component c, Insets insets) {
            return margin;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
        {
            Color color = table.getGridColor();
            if (color == null) return;
                    
            Color oldColor = g.getColor();
            g.setColor(color);
            g.drawRect(-1, -1, w, h); 
            g.setColor(oldColor); 
        } 
    }
    
    // </editor-fold>    
    
   // <editor-fold defaultstate="collapsed" desc="  CornerBorder (class)  ">    
    
    public static class CornerBorder extends AbstractBorder 
    {
        private JTable table;
        private String cornerType;
        private Color color;
        
        public CornerBorder(JTable table, String cornerType) {
            this.table = table;
            this.cornerType = cornerType;
        }

        public CornerBorder(Color color, String cornerType) {
            this.color = color;
            this.cornerType = cornerType;
        }
        
        public JComponent createComponent() 
        {
            JLabel lbl = new JLabel("")
            {
                Painter painter = new Painter();
                
                public void paint(Graphics g) 
                {
                    Rectangle rect = g.getClipBounds(); 
                    painter.paint(this, g, rect.x, rect.y, rect.width, rect.height); 
                    super.paintBorder(g); 
                }
            };
            lbl.setBorder(this); 
            return lbl;
        } 
        
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }
        
        public Insets getBorderInsets(Component c, Insets insets) {
            return new Insets(0,0,0,0);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) 
        {
            Color preferredColor = color;
            if (preferredColor == null && table != null) 
                preferredColor = table.getGridColor();
            
            if (preferredColor == null) return;
            
            Color oldColor = g.getColor();
            g.setColor(preferredColor);
            if (JScrollPane.UPPER_LEFT_CORNER.equals(cornerType)) 
                g.drawRect(-1, -1, w, h); 
            else if (JScrollPane.UPPER_RIGHT_CORNER.equals(cornerType)) 
                g.drawRect(-1, 0, w, h-1); 
            else 
                g.drawRect(-1, -1, w+1, h+1); 
            
            g.setColor(oldColor); 
        } 
    }
    
    // </editor-fold>        
    
}
