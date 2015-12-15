/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.report;

import java.util.ArrayList;
import java.util.List;

public class SimpleTableReport {
    
    private String name;
    private String title;
    private List<ReportColumn> columns = new ArrayList();
    private int maxWidth;
    
    public List<ReportColumn> getColumns() {
        return columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public ReportColumn addColumn(String caption, String name ) {
        return addColumn(caption,name,String.class);
    }

    public ReportColumn addColumn(String caption, String name, Class fieldType ) {
        return addColumn(caption,name,fieldType, 100);
    }
    
    public ReportColumn addColumn(String caption, String name, Class fieldType, int width ) {
        ReportColumn rc = new ReportColumn();
        rc.setCaption(caption);
        rc.setFieldType(fieldType);
        rc.setName(name);
        rc.setWidth(width);
        this.maxWidth += width;
        columns.add(rc);
        return rc;
    }
    
    public int getMaxWidth() {
        return this.maxWidth;
    }
    
}
