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
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.image.ThumbnailPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class XImageGallery extends JPanel implements UIControl
{
    private Binding binding;
    private String[] depends;
    private int index;

    private String handler;    
    private String visibleWhen;
    private String enabledWhen;
    
    private ImageGalleryModel model; 
    private ThumbnailPanelImpl panel;
    private JScrollPane scrollPane;
    private Dimension cellSize;    
    private int cellSpacing;
    private int cols;
    private boolean dynamic;
    
    private Color selectionBorderColor;
    private Border cellBorder;
    private int scrollbarHPolicy;
    private int scrollbarVPolicy;
        
    public XImageGallery() {
        super();
        super.setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 80));
        
        panel = new ThumbnailPanelImpl(); 
        panel.setOpaque(false); 
        cols = panel.getColumnCount(); 
        cellSize = panel.getCellSize();
        cellSpacing = panel.getCellSpacing(); 
        cellBorder = panel.getCellBorder();
        selectionBorderColor = panel.getSelectionBorderColor(); 
        
        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        scrollbarHPolicy = scrollPane.getHorizontalScrollBarPolicy();
        scrollbarVPolicy = scrollPane.getVerticalScrollBarPolicy();
        setBackground(Color.WHITE);        
        add(scrollPane);          
        scrollPane.addMouseWheelListener(new MouseWheelListenerImpl()); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public void setLayout(LayoutManager layout) {} 
    
    public int getScrollbarHPolicy() { return scrollbarHPolicy; } 
    public void setScrollbarHPolicy(int scrollbarHPolicy) {
        this.scrollbarHPolicy = scrollbarHPolicy; 
        if (scrollPane != null) {
            scrollPane.setHorizontalScrollBarPolicy(scrollbarHPolicy);
        }
    }
    
    public int getScrollbarVPolicy() { return scrollbarVPolicy; } 
    public void setScrollbarVPolicy(int scrollbarVPolicy) {
        this.scrollbarVPolicy = scrollbarVPolicy; 
        if (scrollPane != null) {
            scrollPane.setVerticalScrollBarPolicy(scrollbarVPolicy);
        }
    }    
    
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
    
    public String getEnabledWhen() { return enabledWhen; } 
    public void setEnabledWhen(String enabledWhen) {
        this.enabledWhen = enabledWhen; 
    } 
    
    public boolean isEnabled() { 
        if (panel == null) {
            return super.isEnabled(); 
        } else {
            return panel.isEnabled(); 
        }
    }
    public void setEnabled(boolean enabled) {
        if (panel == null) {
            super.setEnabled(enabled);
        } else {
            panel.setEnabled(enabled); 
        }
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
    
    public int getCols() { return cols; }
    public void setCols(int cols) {
        this.cols = cols;
        if(panel != null) {
            panel.setColumnCount(cols);
            updateScrollbarPolicy(); 
        } 
    }
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    public Color getSelectionBorderColor() { return selectionBorderColor; } 
    public void setSelectionBorderColor(Color selectionBorderColor) {
        this.selectionBorderColor = selectionBorderColor; 
        if (panel != null) panel.setSelectionBorderColor(selectionBorderColor); 
    }
    
    public Border getCellBorder() { return cellBorder; } 
    public void setCellBorder(Border cellBorder) {
        this.cellBorder = cellBorder; 
        if (panel != null) panel.setCellBorder(cellBorder); 
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

        try { 
            String enabledWhen = getEnabledWhen(); 
            if (enabledWhen != null && enabledWhen.length() > 0) { 
                Object bean = getBinding().getBean();
                try { 
                    boolean b = UIControlUtil.evaluateExprBoolean(bean, enabledWhen);
                    panel.setEnabled(b); 
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
            } 
        } catch(Throwable t) {;} 
        
        if (!list_loaded || isDynamic()) {
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
    
    private void updateScrollbarPolicy() {
        if (panel == null || scrollPane == null) return;
        
        if (panel.getColumnCount() <= 0) { 
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        } else {
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        }
    }
    
    private void refreshImages() { 
        panel.removeAll();
        if (model == null) return;
        
        int cols = model.getCols();
        if (cols > 0) panel.setColumnCount(cols);
     
        updateScrollbarPolicy(); 
        
        Map params = new HashMap(); 
        Map query = model.getQuery(); 
        if (query != null) params.putAll(query); 
        
        int rows = model.getRows();
        params.put("_limit", (rows > 0? rows: 10)); 
        params.put("_start", 0);
        
        List list = model.fetchList( params ); 
        for (Object item : list) { 
            Map map = (Map) item; 
            panel.add(map); 
        } 
        
        Object item = panel.getSelectedItem(); 
        if (item == null) panel.selectFirstItem(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultImageGalleryModel ">
    
    private class DefaultImageGalleryModel extends ImageGalleryModel 
    {
        
    }
    
    private class DefaultImageGalleryModelProvider implements ImageGalleryModel.Provider 
    {
        XImageGallery root = XImageGallery.this;

        public Object getBinding() {
            return root.getBinding(); 
        }
        
        public void reload() {
            refreshImages(); 
        }

        public void refresh() {
            root.panel.refresh(); 
        }        
        
        public void moveNext() {
            Component c = root.panel.moveNext();
            if (c == null) return;
            
            Rectangle rect = c.getBounds();
            root.panel.scrollRectToVisible(rect);
        }

        public void movePrevious() {
            Component c = root.panel.movePrevious(); 
            if (c == null) return;
            
            Rectangle rect = c.getBounds();
            root.panel.scrollRectToVisible(rect);            
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ThumbnailPanelImpl ">
    
    private class ThumbnailPanelImpl extends ThumbnailPanel 
    {
        XImageGallery root = XImageGallery.this;
        
        protected void onselect(Object item) { 
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    scrollPane.requestFocus(); 
                }
            }); 
            Binding binding = getBinding();
            Object bean = binding.getBean();
            String sname = root.getName();
            if (bean == null || sname == null) return;

            UIControlUtil.setBeanValue(bean, sname, item); 
            Object outcome = (model == null? null: model.onselect(item));
            binding.notifyDepends(root); 
            if (outcome instanceof Opener) { 
                binding.fireNavigation(outcome); 
            }
        } 

        protected void onopen(Object item) {
            if (model == null) return;

            Object outcome = model.onopen(item); 
            if (outcome != null) getBinding().fireNavigation(outcome);
        }  
        
        protected void onrefresh() {
            Binding binding = getBinding();
            if (binding != null) binding.notifyDepends(root); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MouseWheelListenerImpl ">
    
    private class MouseWheelListenerImpl implements MouseWheelListener 
    {
        XImageGallery root = XImageGallery.this; 
        
        public void mouseWheelMoved(MouseWheelEvent e) {
            int vpolicy = root.scrollPane.getVerticalScrollBarPolicy(); 
            if (vpolicy != JScrollPane.VERTICAL_SCROLLBAR_NEVER) return;
            
            JScrollBar scroller = scrollPane.getVerticalScrollBar();
            Dimension dim = panel.getCellSize();
            int scrollAmount = (dim == null? 10: dim.height); 
            int value = scroller.getValue();            
            if (e.getWheelRotation() > 0) {
                scroller.setValue(value + scrollAmount); 
            } else {
                scroller.setValue(Math.max(value-scrollAmount, 0)); 
            }
        }
    }
    
    // </editor-fold>
}
