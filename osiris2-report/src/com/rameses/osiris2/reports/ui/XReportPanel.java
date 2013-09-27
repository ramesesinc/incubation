/*
 * XReportPanel.java
 *
 * Created on November 25, 2009, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports.ui;

import com.rameses.osiris2.reports.ReportModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.net.URL;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

/**
 *
 * @author elmo
 */
public class XReportPanel extends JPanel implements UIControl {
    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private ReportModel model; 
    
    public XReportPanel() {
        super.setLayout(new BorderLayout());
        if (Beans.isDesignTime()) { 
            super.setPreferredSize(new Dimension(40, 40));
            super.setOpaque(true); 
            super.setBackground(Color.LIGHT_GRAY); 
        } 
    } 

    public void setLayout(LayoutManager mgr) {;}
        
    public void setStyle(Map props) {
    }
        
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public void load() {
    }
    
    public void refresh() {
        render(); 
    } 
    
    private void render() {
        if ( ValueUtil.isEmpty(getName()) ) 
            throw new IllegalStateException("Report Panel name must be provided");
        
        Object value = UIControlUtil.getBeanValue(this);
        model = (value instanceof ReportModel? (ReportModel)value: null);
        
        JasperPrint jasperPrint = null; 
        if (model != null) 
            jasperPrint = model.getReport();
        else if (value instanceof JasperPrint)  
            jasperPrint = (JasperPrint) value; 
        
        if (jasperPrint == null) 
            throw new IllegalStateException("No report found at " + getName());
        
        JRViewer jrv = new JRViewer(jasperPrint); 
        //new Customizer(jrv, value).customize(); 
        
        removeAll(); 
        add(jrv); 
        SwingUtilities.updateComponentTreeUI(this); 
    }  
        
    private void doBack() {
        try {
            Object outcome = (model == null? null: model.back()); 
            if (outcome == null) return;
            
            getBinding().fireNavigation(outcome); 
        } catch(Throwable t) {
            MsgBox.alert(t); 
        }
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" Customizer "> 
    
    private JButton btnBack; 
    
    private JButton getBackButton() {
        if (btnBack == null) {
            btnBack = new JButton(); 
            btnBack.setMargin(new Insets(2,2,2,2)); 
            btnBack.setMnemonic('b');
            btnBack.setToolTipText("Go back");
            btnBack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    doBack(); 
                }
            });
            
            try { 
                URL url = XReportPanel.class.getResource("images/arrow_left.png");
                btnBack.setIcon(new ImageIcon(url)); 
            } catch(Throwable t){;} 
        }
        return btnBack; 
    }
    
    private class Customizer 
    {
        private JRViewer jviewer;
        private boolean allowSave = false; 
        private boolean allowPrint = true;

        Customizer(JRViewer jviewer, Object value) { 
            this.jviewer = jviewer; 
            if (value instanceof ReportModel) {
                ReportModel rm = (ReportModel)value;
                this.allowSave = rm.isAllowSave();
                this.allowPrint = rm.isAllowPrint(); 
            }
        } 
        
        void customize() {
            if (jviewer == null) return;
            
            LayoutManager lm = jviewer.getLayout();
            if (lm instanceof BorderLayout) {
                Component comp = ((BorderLayout) lm).getLayoutComponent(BorderLayout.NORTH);                 
                if (comp instanceof Container) {
                    Container con = (Container) comp;
                    if (con.getLayout() instanceof CustomLayout) {
                        //already customized. exit right away
                        return;
                    } 
                    
                    Component sysc0 = getBackButton();
                    
                    CustomLayout clayout = new CustomLayout(); 
                    clayout.allowSave = allowSave;
                    clayout.allowPrint = allowPrint; 
                    clayout.setSystemComponents( new Component[]{sysc0} );
                    clayout.setComponents( con.getComponents() );
                    con.add(sysc0); 
                    con.setLayout(clayout); 
                    if (con instanceof JComponent) {
                        ((JComponent)con).setBorder(BorderFactory.createEmptyBorder(2,2,2,0)); 
                    }
                }
            }
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CustomLayout "> 
    
    private class CustomLayout implements LayoutManager 
    {
        private Component[] systemComponents; 
        private Component[] components; 

        private boolean allowSave = false;
        private boolean allowPrint = true;
        
        void setComponents(Component[] components) {
            this.components = components;             
            if (components != null && components.length > 0) {
                Component c0 = components[0];
                c0.setVisible(allowSave);
                c0.setEnabled(allowSave); 
                setMnemonic(c0, 's');
                
                if (components.length > 1) {
                    Component c1 = components[1]; 
                    c1.setVisible(allowPrint);
                    c1.setEnabled(allowPrint); 
                    setMnemonic(c1, 'p');
                }
            }
        }
        
        void setSystemComponents(Component[] systemComponents) {
            this.systemComponents = systemComponents; 
        }
        
        void setMnemonic(Component c, char key) {
            if (c == null || key == '\u0000') return;
            if (!(c instanceof JButton)) return;
            
            ((JButton)c).setMnemonic(key); 
        }
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) { 
                int w=0, h=0;
                if (systemComponents != null) {
                    for (int i=0; i<systemComponents.length; i++) {
                        Component c = systemComponents[i];
                        if (c == null || !c.isVisible()) continue;
                        
                        Dimension dim = c.getPreferredSize(); 
                        w += dim.width;
                        h = Math.max(h, dim.height); 
                    }
                }
                
                if (components != null) {
                    for (int i=0; i<components.length; i++) {
                        Component c = components[i];
                        if (!c.isVisible()) continue;
                        
                        Dimension dim = c.getPreferredSize(); 
                        w += dim.width;
                        h = Math.max(h, dim.height); 
                    }
                }
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w,h);
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth(), ph = parent.getHeight();
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right); 
                int h = ph - (margin.top + margin.bottom);
                
                if (systemComponents != null) {
                    for (int i=0; i<systemComponents.length; i++) {
                        Component c = systemComponents[i];
                        if (c == null || !c.isVisible()) continue;
                        
                        Dimension dim = c.getPreferredSize(); 
                        c.setBounds(x, y, dim.width, h); 
                        x += dim.width; 
                    }
                }
                
                if (components != null) {
                    for (int i=0; i<components.length; i++) {
                        Component c = components[i];
                        if (c == null || !c.isVisible()) continue;
                        
                        Dimension dim = c.getPreferredSize(); 
                        c.setBounds(x, y, dim.width, h); 
                        x += dim.width; 
                    }
                }                
            }
        } 
    }
    
    // </editor-fold>    
}
