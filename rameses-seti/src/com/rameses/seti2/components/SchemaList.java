/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.seti2.components;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.control.XComponentPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.util.UIInputUtil;

@com.rameses.rcp.ui.annotations.ComponentBean("com.rameses.seti2.components.SchemaListComponent")
public class SchemaList extends XComponentPanel {

    private String schemaName;
    private String customFilter;
    private String queryName;
    private String orderBy;
    private String groupBy;
    private String hiddenCols;
    
    private boolean allowCreate;
    private boolean allowOpen;
    private boolean allowDelete;
    
    private String visibleWhen;
    private String readonlyWhen;
    private String handler; 
    
    private Column[] columns;     
    
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
    
    public int getRowHeight() {
        return (datatable == null ?  null : datatable.getRowHeight());
    }
    public void setRowHeight( int rowHeight ) {
        if ( datatable != null ) {
            datatable.setRowHeight(rowHeight);
        }
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

        jPanel1 = new javax.swing.JPanel();
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

        jPanel1.setPreferredSize(new java.awt.Dimension(456, 30));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 351, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        datatable.setHandler("listModel");
        datatable.setName("selectedItem"); // NOI18N
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
    private com.rameses.rcp.control.XButton btnMoveFirst;
    private com.rameses.rcp.control.XButton btnMoveLast;
    private com.rameses.rcp.control.XButton btnMoveNext;
    private com.rameses.rcp.control.XButton btnMovePrev;
    private com.rameses.rcp.control.XDataTable datatable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JToolBar jToolBar2;
    private com.rameses.rcp.control.XLabel lblPageCount;
    private com.rameses.rcp.control.XLabel lblRecordCount;
    // End of variables declaration//GEN-END:variables
}
