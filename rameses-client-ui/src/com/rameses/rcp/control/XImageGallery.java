/*
 * XImageGallery.java
 *
 * Created on April 21, 2014, 2:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.ImageGalleryModel;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.image.ThumbnailPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author wflores
 */
public class XImageGallery extends JPanel implements UIControl
{
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String visibleWhen;
    private String handler;
    
    private ImageGalleryModel model; 
    private ThumbnailPanelImpl panel;
    private JScrollPane scrollPane;
    private int cellSpacing;
    private Dimension cellSize;
        
    public XImageGallery() {
        super();
        super.setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 80));
        
        panel = new ThumbnailPanelImpl(); 
        panel.setOpaque(false); 
        panel.setCellSize(new Dimension(50,40));
        
        scrollPane = new JScrollPane(panel);
        setBackground(Color.decode("#808080"));        
        add(scrollPane); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public void setLayout(LayoutManager layout) {} 
    
    public void setBackground(Color background) {
        super.setBackground(background);
        if (scrollPane != null) { 
            scrollPane.setBackground(background);
            scrollPane.getViewport().setBackground(background); 
        } 
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    }
    
    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing = cellSpacing;
        if (panel != null) panel.setCellSpacing(cellSpacing); 
    }
    
    public Dimension getCellSize() { return cellSize; } 
    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize; 
        if (panel != null) panel.setCellSize(cellSize); 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding; 
    }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex(int index) {
        this.index = index; 
    }

    public void load() {
        Object bean = getBinding().getBean();
        Object ohandler = UIControlUtil.getBeanValue(bean, getHandler()); 
        if (ohandler instanceof ImageGalleryModel) { 
            model = (ImageGalleryModel)ohandler; 
        } else { 
            model = new DefaultImageGalleryModel(); 
        } 
        model.setProvider(new DefaultImageGalleryModelProvider()); 
    }

    private boolean list_loaded;
    
    public void refresh() {
        try { 
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                Object bean = getBinding().getBean();
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(bean, visibleWhen);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                setVisible(b); 
            } 
        } catch(Throwable t) {;} 
        
        if (!list_loaded) {
            refreshImages();
            list_loaded = true; 
        } 
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    } 

    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
        
    private void refreshImages() { 
        panel.removeAll();
        panel.setColumnCount(model.getCols());
        Map params = new HashMap(); 
        List list = model.fetchList( params ); 
        for (Object item : list) { 
            Map map = (Map) item; 
            panel.add(map); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultImageGalleryModel ">
    
    private class DefaultImageGalleryModel extends ImageGalleryModel 
    {
        
    }
    
    private class DefaultImageGalleryModelProvider implements ImageGalleryModel.Provider 
    {
        public void reload() {
            refreshImages(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ThumbnailPanelImpl ">
    
    private class ThumbnailPanelImpl extends ThumbnailPanel 
    {
        XImageGallery root = XImageGallery.this;
        
        protected void onselect(Object item) { 
            Object bean = getBinding().getBean();
            String sname = root.getName();
            if (bean == null || sname == null) return;

            UIControlUtil.setBeanValue(bean, sname, item); 
            if (model != null) model.onselect(item); 
        } 

        protected void onopen(Object item) {
            if (model == null) return;

            Object outcome = model.onopen(item); 
            if (outcome != null) getBinding().fireNavigation(outcome);
        }        
    }
    
    // </editor-fold>
}
