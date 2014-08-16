/*
 * TreeTableComponentModel.java
 *
 * Created on January 31, 2011
 * @author jaycverg
 */
package com.rameses.rcp.control.treetable;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.AbstractListModel;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.control.table.TableControlModel;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.PropertyResolver;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

public class TreeTableComponentModel extends AbstractTableModel implements TableControlModel {

    private AbstractListDataProvider dataProvider;
    private List<Column> columnList = new ArrayList();
    private String varStatus;

    public TreeTableComponentModel(AbstractListDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        columnList.clear();
        indexColumns();
    }

    private void indexColumns() {
        if (dataProvider == null) {
            return;
        }

        for (Column col : dataProvider.getColumns()) {
            if (col.isVisible()) {
                columnList.add(col);
            }
        }
    }

    public AbstractListDataProvider getDataProvider() {
        return dataProvider;
    }

    public int getRowCount() {
        return this.dataProvider.getRowCount();
    }

    public Column getColumn(int index) {
        if (index >= 0 && index < columnList.size()) {
            return columnList.get(index);
        }

        return null;
    }

    public int getColumnCount() {
        return columnList.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        PropertyResolver resolver = PropertyResolver.getInstance();
        try {
            ListItem item = this.dataProvider.getListItem(rowIndex);
            if (item != null) {
                String name = columnList.get(columnIndex).getName();
                if (!ValueUtil.isEmpty(name)) {
                    if (name.startsWith("_status")) {
                        Map bean = new HashMap();
                        bean.put("_status", item);
                        return resolver.getProperty(bean, name);
                    } else if (item.getItem() != null) {
                        return resolver.getProperty(item.getItem(), name);
                    }
                }

                return item.getItem();
            }
        } catch (Exception e) {
            if (ClientContext.getCurrentContext().isDebugMode()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getColumnName(int column) {
        return columnList.get(column).getCaption();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public String getVarStatus() {
        return varStatus;
    }

    public void setVarStatus(String varStatus) {
        this.varStatus = varStatus;
    }
}
