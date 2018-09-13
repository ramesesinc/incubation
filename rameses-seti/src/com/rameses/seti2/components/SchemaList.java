/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.components;

import com.rameses.common.MethodResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.control.XComponentPanel;
import com.rameses.rcp.control.table.SelectionHandler;
import com.rameses.rcp.framework.Binding;

@com.rameses.rcp.ui.annotations.ComponentBean("com.rameses.seti2.components.SchemaListComponent")
public class SchemaList extends XComponentPanel {

    private String schemaName;
    private String customFilter;
    private String queryName;
    private String handlerName;
    private String orderBy;
    private String groupBy;
    private String hiddenCols;
        
    private boolean multiSelect;
    private boolean allowCreate;
    private boolean allowDelete;
    private boolean allowOpen = true;
    private boolean allowSearch;
    
    
    private String id; 
    private String handler; 
    private String actionContext;
    private String menuContext; 
    private String visibleWhen;
    private String styleRule;
    private String formActions;
    private String entityName;
    
    private Column[] columns;     
    private int rows = 20;
    
    public SchemaList() { 
        initComponents(); 
        datatable.putClientProperty(SelectionHandler.class, null);
    } 

    public Column[] getColumns() { return columns; }
    public void setColumns(Column[] columns) { 
        this.columns = columns; 
        if ( datatable != null ) {
            datatable.setColumns( this.columns ); 
        } 
    } 
    
    public String getSchemaName() { return schemaName; }
    public void setSchemaName( String schemaName ) {
        this.schemaName = schemaName; 
    }

    public String getCustomFilter() { return customFilter; }
    public void setCustomFilter( String customFilter ) {
        this.customFilter = customFilter; 
    }

    public String getQueryName() { return queryName; }
    public void setQueryName( String queryName ) {
        this.queryName = queryName; 
    }

    public String getOrderBy() { return orderBy; }
    public void setOrderBy( String orderBy ) {
        this.orderBy = orderBy; 
    }

    public String getGroupBy() { return groupBy; }
    public void setGroupBy( String groupBy ) {
        this.groupBy = groupBy; 
    }

    public String getHiddenCols() { return hiddenCols; }
    public void setHiddenCols( String hiddenCols ) {
        this.hiddenCols = hiddenCols; 
    }

    public boolean isMultiSelect() { return multiSelect; } 
    public void setMultiSelect( boolean multiSelect ) {
        this.multiSelect = multiSelect; 
    }
    
    public boolean isAllowCreate() { return allowCreate; } 
    public void setAllowCreate( boolean allowCreate ) {
        this.allowCreate = allowCreate; 
    }
    
    public boolean isAllowOpen() { return allowOpen; } 
    public void setAllowOpen( boolean allowOpen ) {
        this.allowOpen = allowOpen; 
    }
    
    public boolean isAllowDelete() { return allowDelete; } 
    public void setAllowDelete( boolean allowDelete ) {
        this.allowDelete = allowDelete; 
    }
    
    public boolean isDynamic() { 
        return (datatable == null ? false : datatable.isDynamic()); 
    } 
    public void setDynamic( boolean dynamic ) {
        if ( datatable != null ) {
            datatable.setDynamic( dynamic ); 
        }
    }
    
    
    public boolean isAutoResize() { 
        return (datatable == null ? true : datatable.isAutoResize()); 
    }    
    public void setAutoResize(boolean autoResize) { 
        if ( datatable != null ) {
            datatable.setAutoResize( autoResize ); 
        } 
    }
    
    public boolean isShowHorizontalLines() {
        return (datatable == null ? true : datatable.isShowHorizontalLines()); 
    }
    public void setShowHorizontalLines( boolean showHorizontalLines ) {
        if ( datatable != null ) {
            datatable.setShowHorizontalLines( showHorizontalLines ); 
        }
    }
    
    public boolean isShowVerticalLines() {
        return (datatable == null ? true : datatable.isShowVerticalLines()); 
    }
    public void setShowVerticalLines( boolean showVerticalLines ) {
        if ( datatable != null ) {
            datatable.setShowVerticalLines( showVerticalLines ); 
        }
    }
    
    public String getVisibleWhen() { return visibleWhen; }
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen; 
    }
    
    public String getStyleRule() { return styleRule; } 
    public void setStyleRule( String styleRule ) {
        this.styleRule = styleRule;
    }
    
    public String getHandler() { return handler; } 
    public void setHandler( String handler ) {
        this.handler = handler; 
    }
    
    public String getId() { 
        return (datatable == null ? null: datatable.getId());
    } 
    public void setId( String id ) {
        if ( datatable != null ) {
            datatable.setId(id); 
        }
    }
    
    public String getActionContext() { return actionContext; } 
    public void setActionContext( String actionContext ) {
        this.actionContext = actionContext; 
        if ( actionBar != null ) {
            actionBar.setName( actionContext ); 
            actionBar.setFormName(""); 
        } 
    } 
    
    public String getMenuContext() { return menuContext; } 
    public void setMenuContext( String menuContext ) {
        this.menuContext = menuContext; 
    }
    
    public int getRowHeight() {
        return (datatable == null ?  null : datatable.getRowHeight());
    }
    public void setRowHeight( int rowHeight ) {
        if ( datatable != null ) {
            datatable.setRowHeight(rowHeight);
        }
    }
    
    public int getRows() { return rows; } 
    public void setRows( int rows ) {
        this.rows = rows;
    }

    @Override
    protected void initComponentBean(com.rameses.rcp.common.ComponentBean bean) { 
        bean.setProperty("allowDelete", isAllowDelete()); 
        bean.setProperty("allowCreate", isAllowCreate()); 
        bean.setProperty("allowOpen", isAllowOpen()); 
        bean.setProperty("allowSearch", isAllowSearch()); 
        bean.setProperty("formActionContext", getFormActions() ); 
        
        bean.setProperty("schemaName", getSchemaName()); 
        bean.setProperty("hiddenCols", getHiddenCols()); 
        bean.setProperty("customFilter", getCustomFilter()); 
        bean.setProperty("orderBy", getOrderBy()); 
        bean.setProperty("groupBy", getGroupBy()); 
        bean.setProperty("query", getProperty(getQueryName())); 
        bean.setProperty("handler", getProperty(getHandler())); 
        bean.setProperty("actionContext", getActionContext()); 
        bean.setProperty("menuContext", getMenuContext()); 
        bean.setProperty("rows", getRows()); 
        bean.setProperty("entityName", getEntityName()); 
        

        Object sr = getProperty(getStyleRule()); 
        bean.setProperty("stylerule", (sr == null ? getStyleRule() : sr)); 
        
        bean.setProperty("ui", this); 
        
        String handlerName = getHandlerName();
        if ( handlerName != null && handlerName.trim().length() > 0 ) {
            Object listModel = PropertyResolver.getInstance().getProperty(bean, "listModel");
            setProperty(getHandlerName(), listModel, getBean());
        }       
    } 
    
    public void setProperty( String name, Object value ) { 
        if ( name != null && name.trim().length() > 0 ) {
            Object b = getBinding(); 
            Object bean = (b == null ? null : getBinding().getBean()); 
            setProperty(name, value, bean); 
        }
    }
    public void setProperty( String name, Object value, Object bean ) { 
        if ( name != null && name.trim().length() > 0 ) { 
            PropertyResolver.getInstance().setProperty(bean, name, value); 
            Binding b = getBinding(); 
            if ( b != null ) { 
                b.getValueChangeSupport().notify(name, value); 
            } 
        } 
    }
    
    public void notifyDepends( String name ) {
        if ( name != null && name.trim().length() > 0 ) {
            Object b = getBinding(); 
            if ( b != null ) {
                getBinding().notifyDepends( name ); 
            }
        }
    }

    public void afterRefresh() { 
        try { 
            Object bean = getComponentBean(); 
            setProperty("query", getProperty(getQueryName()), bean); 
            MethodResolver.getInstance().invoke(bean, "search",  new Object[]{}); 
        } catch(Throwable t) {
            MsgBox.err( t ); 
        }
    }

    /**
     * @return the allowSearch
     */
    public boolean isAllowSearch() {
        return allowSearch;
    }

    /**
     * @param allowSearch the allowSearch to set
     */
    public void setAllowSearch(boolean allowSearch) {
        this.allowSearch = allowSearch;
    }

    /**
     * @return the handlerName
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * @param handlerName the handlerName to set
     */
    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    /**
     * @return the formActions
     */
    public String getFormActions() {
        return formActions;
    }

    /**
     * @param formActions the formActions to set
     */
    public void setFormActions(String formActions) {
        this.formActions = formActions;
    }

    /**
     * @return the showFilter
     */
    public boolean isShowFilter() {
        return btnFilter.isVisible();
    }

    /**
     * @param showFilter the showFilter to set
     */
    public void setShowFilter(boolean showFilter) {
        btnFilter.setVisible( showFilter );
    }

    /**
     * @return the showRefresh
     */
    public boolean isShowRefresh() {
        return btnRefresh.isVisible();
    }

    /**
     * @param showRefresh the showRefresh to set
     */
    public void setShowRefresh(boolean showRefresh) {
        btnRefresh.setVisible(showRefresh);
    }

    public boolean isShowInfo() {
        return infoButton.isVisible();
    }
    
    public void setShowInfo(boolean b) {
        infoButton.setVisible(b);
    }
    
    public boolean isShowNavbar() {
        return navToolbar.isVisible();
    }
    
    public void setShowNavbar(boolean b) {
        navToolbar.setVisible(b);
    }
    
    public boolean isShowRowHeader() {
        return datatable.isShowRowHeader();
    }
    
    public void setShowRowHeader(boolean b) {
        datatable.setShowRowHeader( b );
    }
    
    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
        
    private class SelectionHandlerImpl implements SelectionHandler {

        SchemaList root = SchemaList.this; 
        
        public void setBeanValue(String name, Object value) {
            root.setProperty( getName(), value ); 
        }

        public void setStatusValue(String name, Object value) {
        }

        public void notifyDepends(String name) {
            root.notifyDepends( name ); 
        }
    }
    
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnCreate = new com.rameses.rcp.control.XButton();
        btnOpen = new com.rameses.rcp.control.XButton();
        btnDelete = new com.rameses.rcp.control.XButton();
        btnPrint = new com.rameses.rcp.control.XButton();
        btnFilter = new com.rameses.rcp.control.XButton();
        btnSelectColumn = new com.rameses.rcp.control.XButton();
        btnRefresh = new com.rameses.rcp.control.XButton();
        actionBar = new com.rameses.rcp.control.XActionBar();
        jPanel7 = new javax.swing.JPanel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        jToolBar3 = new javax.swing.JToolBar();
        xActionTextField1 = new com.rameses.rcp.control.XActionTextField();
        infoButton = new com.rameses.rcp.control.XButton();
        datatable = new com.rameses.rcp.control.XDataTable();
        navToolbar = new javax.swing.JToolBar();
        btnMoveFirst = new com.rameses.rcp.control.XButton();
        btnMovePrev = new com.rameses.rcp.control.XButton();
        btnMoveNext = new com.rameses.rcp.control.XButton();
        btnMoveLast = new com.rameses.rcp.control.XButton();
        jPanel5 = new javax.swing.JPanel();
        lblRecordCount = new com.rameses.rcp.control.XLabel();
        lblPageCount = new com.rameses.rcp.control.XLabel();
        xStyleRule1 = new com.rameses.rcp.control.XStyleRule();

        setPreferredSize(new java.awt.Dimension(351, 183));
        setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.BorderLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnCreate.setCaption("");
        btnCreate.setName("create"); // NOI18N
        btnCreate.setVisibleWhen("#{allowCreate==true}");
        btnCreate.setAccelerator("ctrl N");
        btnCreate.setAutoRefresh(false);
        btnCreate.setFocusable(false);
        btnCreate.setIconResource("images/toolbars/create.png");
        btnCreate.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jToolBar1.add(btnCreate);

        btnOpen.setCaption("");
        btnOpen.setName("open"); // NOI18N
        btnOpen.setVisibleWhen("#{allowOpen==true}");
        btnOpen.setAccelerator("ctrl O");
        btnOpen.setAutoRefresh(false);
        btnOpen.setFocusable(false);
        btnOpen.setIconResource("images/toolbars/open.png");
        btnOpen.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jToolBar1.add(btnOpen);

        btnDelete.setCaption("");
        btnDelete.setDepends(new String[] {"selectedItem"});
        btnDelete.setName("removeEntity"); // NOI18N
        btnDelete.setVisibleWhen("#{allowDelete==true}");
        btnDelete.setFocusable(false);
        btnDelete.setIconResource("images/toolbars/trash.png");
        btnDelete.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jToolBar1.add(btnDelete);

        btnPrint.setName("print"); // NOI18N
        btnPrint.setAccelerator("ctrl P");
        btnPrint.setCaption("");
        btnPrint.setFocusable(false);
        btnPrint.setIconResource("images/toolbars/printer.png");
        btnPrint.setImmediate(true);
        btnPrint.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnPrint.setVisible(false);
        btnPrint.setVisibleWhen("#{printAllowed}");
        jToolBar1.add(btnPrint);

        btnFilter.setCaption("");
        btnFilter.setName("showFilter"); // NOI18N
        btnFilter.setAccelerator("ctrl F");
        btnFilter.setAutoRefresh(false);
        btnFilter.setFocusable(false);
        btnFilter.setIconResource("images/toolbars/filter.png");
        btnFilter.setImmediate(true);
        btnFilter.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jToolBar1.add(btnFilter);

        btnSelectColumn.setName("selectColumns"); // NOI18N
        btnSelectColumn.setCaption("");
        btnSelectColumn.setFocusable(false);
        btnSelectColumn.setIconResource("images/toolbars/table-column.png");
        btnSelectColumn.setImmediate(true);
        btnSelectColumn.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnSelectColumn.setVisible(false);
        btnSelectColumn.setVisibleWhen("#{showColsAllowed}");
        jToolBar1.add(btnSelectColumn);

        btnRefresh.setCaption("");
        btnRefresh.setName("refresh"); // NOI18N
        btnRefresh.setAccelerator("ctrl R");
        btnRefresh.setAutoRefresh(false);
        btnRefresh.setFocusable(false);
        btnRefresh.setIconResource("images/toolbars/refresh.png");
        btnRefresh.setImmediate(true);
        btnRefresh.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jToolBar1.add(btnRefresh);

        actionBar.setName("formActions"); // NOI18N
        actionBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        jToolBar1.add(actionBar);

        jPanel8.add(jToolBar1, java.awt.BorderLayout.WEST);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        jPanel7.setLayout(new java.awt.BorderLayout());

        xLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        xLabel2.setName("filterText"); // NOI18N
        xLabel2.setCellPadding(new java.awt.Insets(5, 0, 0, 5));
        xLabel2.setFontStyle("font-weight: bold;");
        xLabel2.setForeground(new java.awt.Color(204, 0, 0));
        xLabel2.setText("xLabel2");
        jPanel7.add(xLabel2, java.awt.BorderLayout.WEST);

        xSubFormPanel1.setHandler("queryForm");

        javax.swing.GroupLayout xSubFormPanel1Layout = new javax.swing.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 227, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jPanel7.add(xSubFormPanel1, java.awt.BorderLayout.CENTER);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        xActionTextField1.setActionName("search");
        xActionTextField1.setName("searchText"); // NOI18N
        xActionTextField1.setVisibleWhen("#{allowSearch == true}");
        xActionTextField1.setFocusKeyStroke("F3");
        xActionTextField1.setMaxLength(50);
        xActionTextField1.setPreferredSize(new java.awt.Dimension(180, 20));
        jToolBar3.add(xActionTextField1);

        infoButton.setCaption("");
        infoButton.setName("showInfo"); // NOI18N
        infoButton.setAutoRefresh(false);
        infoButton.setBackground(new java.awt.Color(255, 255, 255));
        infoButton.setFocusable(false);
        infoButton.setIconResource("images/info.png");
        infoButton.setImmediate(true);
        jToolBar3.add(infoButton);

        jPanel7.add(jToolBar3, java.awt.BorderLayout.EAST);

        jPanel8.add(jPanel7, java.awt.BorderLayout.CENTER);

        add(jPanel8, java.awt.BorderLayout.NORTH);

        datatable.setHandler("listModel");
        datatable.setName("selectedItem"); // NOI18N
        add(datatable, java.awt.BorderLayout.CENTER);

        navToolbar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        navToolbar.setRollover(true);

        btnMoveFirst.setName("listModel.moveFirstPage"); // NOI18N
        btnMoveFirst.setAutoRefresh(false);
        btnMoveFirst.setFocusable(false);
        btnMoveFirst.setIconResource("images/navbar/first.png");
        navToolbar.add(btnMoveFirst);

        btnMovePrev.setName("listModel.moveBackPage"); // NOI18N
        btnMovePrev.setAutoRefresh(false);
        btnMovePrev.setFocusable(false);
        btnMovePrev.setIconResource("images/navbar/previous.png");
        navToolbar.add(btnMovePrev);

        btnMoveNext.setName("listModel.moveNextPage"); // NOI18N
        btnMoveNext.setAutoRefresh(false);
        btnMoveNext.setFocusable(false);
        btnMoveNext.setIconResource("images/navbar/next.png");
        btnMoveNext.setImmediate(true);
        navToolbar.add(btnMoveNext);

        btnMoveLast.setName("listModel.moveLastPage"); // NOI18N
        btnMoveLast.setAutoRefresh(false);
        btnMoveLast.setFocusable(false);
        btnMoveLast.setIconResource("images/navbar/last.png");
        navToolbar.add(btnMoveLast);

        jPanel5.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel5.setLayout(new com.rameses.rcp.control.layout.XLayout());

        lblRecordCount.setDepends(new String[] {"selectedItem"});
        lblRecordCount.setExpression("Page #{listModel.pageIndex}");
        lblRecordCount.setUseHtml(true);
        jPanel5.add(lblRecordCount);

        lblPageCount.setDepends(new String[] {"selectedItem"});
        lblPageCount.setExpression("of #{listModel.pageCount}");
        lblPageCount.setUseHtml(true);
        jPanel5.add(lblPageCount);

        xStyleRule1.setName("stylerule"); // NOI18N

        javax.swing.GroupLayout xStyleRule1Layout = new javax.swing.GroupLayout(xStyleRule1);
        xStyleRule1.setLayout(xStyleRule1Layout);
        xStyleRule1Layout.setHorizontalGroup(
            xStyleRule1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        xStyleRule1Layout.setVerticalGroup(
            xStyleRule1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanel5.add(xStyleRule1);

        navToolbar.add(jPanel5);

        add(navToolbar, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XActionBar actionBar;
    private com.rameses.rcp.control.XButton btnCreate;
    private com.rameses.rcp.control.XButton btnDelete;
    private com.rameses.rcp.control.XButton btnFilter;
    private com.rameses.rcp.control.XButton btnMoveFirst;
    private com.rameses.rcp.control.XButton btnMoveLast;
    private com.rameses.rcp.control.XButton btnMoveNext;
    private com.rameses.rcp.control.XButton btnMovePrev;
    private com.rameses.rcp.control.XButton btnOpen;
    private com.rameses.rcp.control.XButton btnPrint;
    private com.rameses.rcp.control.XButton btnRefresh;
    private com.rameses.rcp.control.XButton btnSelectColumn;
    private com.rameses.rcp.control.XDataTable datatable;
    private com.rameses.rcp.control.XButton infoButton;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private com.rameses.rcp.control.XLabel lblPageCount;
    private com.rameses.rcp.control.XLabel lblRecordCount;
    private javax.swing.JToolBar navToolbar;
    private com.rameses.rcp.control.XActionTextField xActionTextField1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XStyleRule xStyleRule1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables

}
