/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt.components;

import com.rameses.common.MethodResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.FileViewModel;
import com.rameses.rcp.control.XComponentPanel;
import com.rameses.rcp.control.XLabel;
import com.rameses.rcp.control.XList;
import com.rameses.rcp.control.XPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.annotations.ComponentBean;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
@ComponentBean("com.rameses.filemgmt.components.FileViewPanelModel")
public class FileViewPanel extends XComponentPanel {

    private String handler; 
    
    private SplitterPanel splitPanel;
    private FileViewModel modelHandler;
    private ThumbnailViewPanel viewPanel; 
    
    private Dimension cellSize;
    private int cellSpacing;
    
    public FileViewPanel() { 
        initComponents(); 
    } 
        
    // <editor-fold defaultstate="collapsed" desc="initComponents">
    private void initComponents() { 
        setLayout(new MainLayoutManager());  
        setPreferredSize(new Dimension(200, 100)); 
        
        splitPanel = new SplitterPanel();
        add( splitPanel );

        XList xlist = new XList();
        xlist.setName("selectedItem"); 
        xlist.setHandler("listHandler"); 
        xlist.setExpression("#{item.title}"); 
        xlist.setFixedCellHeight( 20 ); 
        
        JScrollPane jsp = new JScrollPane( xlist ); 
        jsp.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ); 
        splitPanel.setSideView( jsp ); 
                
        XPanel cardpanel = new XPanel();
        cardpanel.setLayout(new CardLayout()); 
        cardpanel.setDepends(new String[]{"selectedItem"}); 
        cardpanel.setName("cardName"); 

        XLabel loadComp = new XLabel();
        loadComp.setHorizontalAlignment(SwingConstants.CENTER);
        loadComp.setDepends(new String[] {"selectedItem"});
        loadComp.setIconResource("com/rameses/rcp/icons/loading32.gif");
        loadComp.setVisibleWhen("#{cardName == 'loading'}"); 
        cardpanel.add( loadComp, "loading" );
        
        XLabel blankComp = new XLabel(); 
        blankComp.setHorizontalAlignment( SwingConstants.CENTER ); 
        blankComp.setVerticalAlignment( SwingConstants.TOP ); 
        blankComp.setText("<html><br/><h3>No available item(s)</h3></html>");
        blankComp.setForeground( Color.decode("#a0a0a0")); 
        blankComp.setVisibleWhen("#{cardName == 'blank'}"); 
        blankComp.setDepends(new String[] {"selectedItem"});
        blankComp.setOpaque(true);
        cardpanel.add( blankComp, "blank" ); 

        Color borderColor = new Color(150, 150, 150); 
        loadComp.setBorder( BorderFactory.createLineBorder( borderColor, 1)); 
        blankComp.setBorder( BorderFactory.createLineBorder( borderColor, 1)); 
        
        viewPanel = new ThumbnailViewPanel();
        viewPanel.setName("selectedThumbnail"); 
        viewPanel.setHandler("thumbnailListHandler"); 
        viewPanel.setDepends(new String[]{"selectedItem"});
        viewPanel.setVisibleWhen("#{cardName == 'view'}"); 
        viewPanel.setCellSize(new Dimension(100, 80)); 
        cardpanel.add( viewPanel, "view" ); 

        XPanel headerpanel = new XPanel();
        headerpanel.setLayout(new BorderLayout());
        headerpanel.setDepends(new String[]{"selectedItem"}); 
        
        XPanel contentview = new XPanel();
        splitPanel.setContentView( contentview ); 
        
        contentview.setLayout(new BorderLayout());
        contentview.add( BorderLayout.NORTH, headerpanel ); 
        contentview.add( cardpanel ); 
        
        Border bout = BorderFactory.createLineBorder( borderColor, 1); 
        Border bin = BorderFactory.createEmptyBorder(3, 5, 3, 5); 
        
        XLabel headerinfo = new XLabel();
        headerinfo.setBorder( BorderFactory.createCompoundBorder(bout, bin));  
        headerinfo.setVerticalAlignment( SwingConstants.TOP ); 
        headerinfo.setMinimumSize(new Dimension(100, 30)); 
        headerinfo.setBackground( Color.WHITE );
        headerinfo.setOpaque(true);
        headerinfo.setText("Header Message");
        headerinfo.setExpression("#{headerMessage}");
        headerinfo.setDepends(new String[]{"selectedItem"}); 
        headerpanel.add( BorderLayout.NORTH, headerinfo ); 
        headerpanel.add( BorderLayout.SOUTH, Box.createVerticalStrut(5)); 
    }
    // </editor-fold> 
        
    public String getHandler() { return handler; } 
    public void setHandler( String handler ) { 
        this.handler = handler; 
    } 
    
    public int getDividerSize() { 
        return (splitPanel == null ? 0 : splitPanel.getDividerSize());
    } 
    public void setDividerSize( int dividerSize ) {
        if ( splitPanel != null ) {
            splitPanel.setDividerSize( dividerSize );
        }
    }
    
    public int getDividerLocation() {  
        return (splitPanel == null ? 0 : splitPanel.getDividerLocation());
    } 
    public void setDividerLocation( int dividerLocation ) {
        if ( splitPanel != null ) {
            splitPanel.setDividerLocation( dividerLocation );
        }
    } 
    
    public Dimension getCellSize() { return cellSize; }
    public void setCellSize( Dimension cellSize ) {
        this.cellSize = cellSize; 
        if ( viewPanel != null ) {
            viewPanel.setCellSize( this.cellSize ); 
        }
    }
    
    public int getCellSpacing() { return cellSpacing; } 
    public void setCellSpacing( int cellSpacing ) {
        this.cellSpacing = cellSpacing; 
        if ( viewPanel != null ) {
            viewPanel.setCellSpacing( this.cellSpacing ); 
        }
    }

    protected void initComponentBean(com.rameses.rcp.common.ComponentBean bean) {
        PropertyResolver pr = PropertyResolver.getInstance();

        FileViewModel newhandler = null; 
        String shandler = getHandler(); 
        if ( shandler != null && shandler.trim().length()>0 ) {
            Object caller = getBean();
            Object oval = pr.getProperty(caller, shandler); 
            if ( oval instanceof FileViewModel ) {
                newhandler = (FileViewModel) oval; 
            }
        }
        
        if ( newhandler == null ) {
            newhandler = new EmptyFileViewModel(); 
        } 
        newhandler.setProvider(new FileViewModelProvider()); 
        modelHandler = newhandler; 
        pr.setProperty(bean, "handlerProxy", modelHandler);        
    } 

    public void afterLoad() {
        super.afterLoad();
    } 
    
    public void afterRefresh() {
        super.afterRefresh();        
    } 
    

    // <editor-fold defaultstate="collapsed" desc="FileViewModel">  
    private class EmptyFileViewModel extends FileViewModel {
    }

    private class FileViewModelProvider implements FileViewModel.Provider {

        FileViewPanel root = FileViewPanel.this; 
        
        public Binding getBinding() {
            return root.getBinding();
        }

        public Binding getInnerBinding() {
            return root.getInnerBinding(); 
        }

        public void addItem(Object item) throws Exception {
            Object bean = root.getInnerBinding().getBean(); 
            MethodResolver.getInstance().invoke(bean, "addItem", new Object[]{ item }); 
        } 
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="MainLayoutManager">
    private class MainLayoutManager implements LayoutManager {

        FileViewPanel root = FileViewPanel.this; 
        
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
                return getLayoutSize( parent );
            }
        }
        
        private Dimension getLayoutSize(Container parent) {
            int w = 0, h = 0;
            if ( root.splitPanel != null ) {
                Dimension dim = root.splitPanel.getPreferredSize(); 
                w = dim.width;
                h = dim.height; 
            }
            Insets margin = parent.getInsets(); 
            w += (margin.left + margin.right); 
            h += (margin.top + margin.bottom);
            return new Dimension( w, h );  
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
                
                if ( root.splitPanel != null ) { 
                    root.splitPanel.setBounds(x, y, w, h); 
                } 
            } 
        }         
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="HeaderPanel">
    private class HeaderPanel extends JPanel { 
        
        FileViewPanel root = FileViewPanel.this; 
        
        HeaderPanel() {
            
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="HeaderLayout">
    
    // </editor-fold>
}
