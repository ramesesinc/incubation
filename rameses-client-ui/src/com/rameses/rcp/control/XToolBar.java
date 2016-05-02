package com.rameses.rcp.control;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Windhel
 */

public class XToolBar extends JPanel implements UIControl, ActiveControl {
    
    private static String ALIGN_RIGHT = "RIGHT";
    private static String ALIGN_CENTER = "CENTER";
    private static int HGAP = 5;
    private static Color UPCLR = new Color(245, 245, 245);
    private static Color LOWCLR = new Color(193, 205, 193);
    
    private Collection<Action> actions;
    private String[] depends;
    private int index;
    private Binding binding;
    private FlowLayout flowLayout = new FlowLayout();
    private String orientation = "LEFT";
    
    private int stretchWidth;
    private int stretchHeight;     
    
    public XToolBar() {
        setBorder(new XToolBarBorder());
    }
    
    //<editor-fold defaultstate="collapsed" desc="  Getter / Setter ">
    public String[] getDepends() {
        return depends;
    }
    
    public void setDepends(String[] depends) {
        this.depends = depends;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    
    public Binding getBinding() {
        return binding;
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public String getOrientation() {
        return orientation;
    }
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property; 
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>        
    
    public void refresh() {
    }
    
    public void load() {
        setBorder(new EtchedBorder());
        setOpaque(false);
        Object value = UIControlUtil.getBeanValue(this);
        if(value == null) return;
        if(value instanceof Collection)
            actions = (Collection) value;
        if(ALIGN_RIGHT.equals(orientation.toUpperCase()))
            flowLayout.setAlignment(FlowLayout.RIGHT);
        else if(ALIGN_CENTER.equals(orientation.toUpperCase()))
            flowLayout.setAlignment(FlowLayout.CENTER);
        else
            flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setHgap(HGAP);
        setLayout(flowLayout);
        for(Action a : actions) {
            XButton ib = new XButton();
            ib.setBinding(binding);
            ib.setName(a.getName());
            ib.setPermission(a.getPermission());
            
            ib.setPreferredSize(new Dimension(40,40));
            if(a.getTooltip() != null)
                ib.setToolTipText(a.getCaption());
            add(ib);
            ib.load();
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    private class XToolBarBorder extends AbstractBorder {
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            
            GradientPaint gradient = new GradientPaint(0, 0, UPCLR, 0, height, LOWCLR);
            g2.setPaint(gradient);
            g2.fillRect(0,0, width-1, height-1);
            
            g2.dispose();
        }
    }
    
}
