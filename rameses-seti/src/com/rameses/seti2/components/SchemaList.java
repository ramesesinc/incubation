/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.components;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.control.XComponentPanel;

@com.rameses.rcp.ui.annotations.ComponentBean("com.rameses.seti2.components.SchemaListComponent")
public class SchemaList extends XComponentPanel {

    private String schemaName;
    private String customFilter;
    private String queryName;
    private String orderBy;
    private String groupBy;
    private String hiddenCols;
    
    private boolean multiSelect;
    private boolean allowCreate;
    private boolean allowDelete;
    private boolean allowOpen = true;
    
    private String handler; 
    private String actionContext;
    private String menuContext; 
    private String visibleWhen;
    
    private Column[] columns;     
    private int rows = 20;
    
    public SchemaList() { 
        initComponents(); 
    } 

    public void setName(String name) {
        super.setName(name);
        if ( datatable != null ) { 
            datatable.setName( getName() ); 
        } 
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
    
    public String getHandler() { return handler; } 
    public void setHandler( String handler ) {
        this.handler = handler; 
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
        
        bean.setProperty("ui", this);         
    } 
    
    public void setProperty( String name, Object value ) { 
        if ( name != null && name.trim().length() > 0 ) {
            Object b = getBinding(); 
            Object bean = (b == null ? null : getBinding().getBean()); 
            PropertyResolver.getInstance().setProperty(bean, name, value);
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
        xButton2 = new com.rameses.rcp.control.XButton();
        xButton1 = new com.rameses.rcp.control.XButton();
        datatable = new com.rameses.rcp.control.XDataTable();
        jToolBar2 = new javax.swing.JToolBar();
        btnMoveFirst = new com.rameses.rcp.control.XButton();
        btnMovePrev = new com.rameses.rcp.control.XButton();
        btnMoveNext = new com.rameses.rcp.control.XButton();
        btnMoveLast = new com.rameses.rcp.control.XButton();
        jPanel5 = new javax.swing.JPanel();
        lblRecordCount = new com.rameses.rcp.control.XLabel();
        lblPageCount = new com.rameses.rcp.control.XLabel();

        setPreferredSize(new java.awt.Dimension(351, 183));
        setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.BorderLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnCreate.setName("create"); // NOI18N
        btnCreate.setAccelerator("ctrl N");
        btnCreate.setCaption("");
        btnCreate.setFocusable(false);
        btnCreate.setIconResource("images/toolbars/create.png");
        btnCreate.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnCreate.setVisibleWhen("#{allowCreate==true}");
        jToolBar1.add(btnCreate);

        btnOpen.setName("open"); // NOI18N
        btnOpen.setAccelerator("ctrl O");
        btnOpen.setCaption("");
        btnOpen.setFocusable(false);
        btnOpen.setIconResource("images/toolbars/open.png");
        btnOpen.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnOpen.setVisibleWhen("#{allowOpen==true}");
        jToolBar1.add(btnOpen);

        btnDelete.setName("removeEntity"); // NOI18N
        btnDelete.setCaption("");
        btnDelete.setDepends(new String[] {"selectedItem"});
        btnDelete.setFocusable(false);
        btnDelete.setIconResource("images/toolbars/trash.png");
        btnDelete.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnDelete.setVisibleWhen("#{allowDelete==true}");
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

        btnFilter.setName("showFilter"); // NOI18N
        btnFilter.setAccelerator("ctrl F");
        btnFilter.setCaption("");
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

        btnRefresh.setName("refresh"); // NOI18N
        btnRefresh.setAccelerator("ctrl R");
        btnRefresh.setCaption("");
        btnRefresh.setFocusable(false);
        btnRefresh.setIconResource("images/toolbars/refresh.png");
        btnRefresh.setImmediate(true);
        btnRefresh.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jToolBar1.add(btnRefresh);

        actionBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        jToolBar1.add(actionBar);

        jPanel8.add(jToolBar1, java.awt.BorderLayout.WEST);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        jPanel7.setLayout(new java.awt.BorderLayout());

        xLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        xLabel2.setName("filterText"); // NOI18N
        xLabel2.setCellPadding(new java.awt.Insets(5, 0, 0, 5));
        xLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        xLabel2.setForeground(new java.awt.Color(204, 0, 0));
        xLabel2.setText("xLabel2");
        jPanel7.add(xLabel2, java.awt.BorderLayout.WEST);

        xSubFormPanel1.setHandler("queryForm");

        javax.swing.GroupLayout xSubFormPanel1Layout = new javax.swing.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jPanel7.add(xSubFormPanel1, java.awt.BorderLayout.CENTER);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        xActionTextField1.setName("searchText"); // NOI18N
        xActionTextField1.setActionName("search");
        xActionTextField1.setFocusKeyStroke("F3");
        xActionTextField1.setMaxLength(50);
        xActionTextField1.setPreferredSize(new java.awt.Dimension(180, 20));
        xActionTextField1.setVisibleWhen("#{allowSearch == true}");
        jToolBar3.add(xActionTextField1);

        xButton2.setName("showInfo"); // NOI18N
        xButton2.setBackground(new java.awt.Color(255, 255, 255));
        xButton2.setCaption("");
        xButton2.setFocusable(false);
        xButton2.setIconResource("images/info.png");
        xButton2.setImmediate(true);
        jToolBar3.add(xButton2);

        xButton1.setName("showHelp"); // NOI18N
        xButton1.setBackground(new java.awt.Color(255, 255, 255));
        xButton1.setCaption("\\");
            xButton1.setFocusable(false);
            xButton1.setIconResource("images/help.png");
            xButton1.setImmediate(true);
            jToolBar3.add(xButton1);

            jPanel7.add(jToolBar3, java.awt.BorderLayout.EAST);

            jPanel8.add(jPanel7, java.awt.BorderLayout.CENTER);

            add(jPanel8, java.awt.BorderLayout.NORTH);

            datatable.setName("selectedItem"); // NOI18N
            datatable.setHandler("listModel");
            add(datatable, java.awt.BorderLayout.CENTER);

            jToolBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            jToolBar2.setRollover(true);

            btnMoveFirst.setFocusable(false);
            btnMoveFirst.setIconResource("images/navbar/first.png");
            btnMoveFirst.setName("listModel.moveFirstPage"); // NOI18N
            jToolBar2.add(btnMoveFirst);

            btnMovePrev.setFocusable(false);
            btnMovePrev.setIconResource("images/navbar/previous.png");
            btnMovePrev.setName("listModel.moveBackPage"); // NOI18N
            jToolBar2.add(btnMovePrev);

            btnMoveNext.setFocusable(false);
            btnMoveNext.setIconResource("images/navbar/next.png");
            btnMoveNext.setImmediate(true);
            btnMoveNext.setName("listModel.moveNextPage"); // NOI18N
            jToolBar2.add(btnMoveNext);

            btnMoveLast.setFocusable(false);
            btnMoveLast.setIconResource("images/navbar/last.png");
            btnMoveLast.setName("listModel.moveLastPage"); // NOI18N
            jToolBar2.add(btnMoveLast);

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

            jToolBar2.add(jPanel5);

            add(jToolBar2, java.awt.BorderLayout.SOUTH);
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private com.rameses.rcp.control.XLabel lblPageCount;
    private com.rameses.rcp.control.XLabel lblRecordCount;
    private com.rameses.rcp.control.XActionTextField xActionTextField1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables

}
