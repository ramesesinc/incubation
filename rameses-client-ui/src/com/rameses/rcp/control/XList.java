/*
 * XList.java
 *
 * Created on October 29, 2010, 10:59 AM
 * @author jaycverg
 */
package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.ListPaneModel;
import com.rameses.rcp.common.MapObject;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class XList extends JList implements UIControl 
{
    private ListSelectionSupport selectionSupport;    
    
    private Binding binding;
    private String[] depends;
    private String varName;
    private String varStatus;
    private String expression;
    private String items;
    private String handler;
    private String openAction;  
    private boolean dynamic;
    private int index;    

    private ListPaneModel listPaneModel;
    private DefaultListModel model;
    private Insets padding = new Insets(1,3,1,3);    
    private int cellVerticalAlignment = SwingConstants.CENTER;
    private int cellHorizontalAlignment = SwingConstants.LEADING;
        
    public XList() 
    {
        super.addListSelectionListener(getSelectionSupport()); 
        setCellRenderer(new DefaultCellRenderer());
        setMultiselect(false);
        setVarName("item");
        
        if ( Beans.isDesignTime() ) 
        {
            setPreferredSize(new Dimension(80, 100));
            super.setModel(new javax.swing.AbstractListModel() 
            {
                String[] strings = { "Item 1", "Item 2" };
                public int getSize() { return strings.length; }
                public Object getElementAt(int i) { return strings[i]; }
            });
        }
        else 
        {
            registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireOpenItem();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        }        
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public final void setModel(ListModel model) {;}    
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) { this.handler = handler; } 
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public String getVarStatus() { return varStatus; } 
    public void setVarStatus(String varStatus) { this.varStatus = varStatus; }    
            
    public String getExpression() { return expression; }    
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    public String getItems() { return items; }    
    public void setItems(String items) { this.items = items; }
    
    public boolean isDynamic() { return dynamic; }    
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isMultiselect() {
        return getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    }       
    public void setMultiselect(boolean multi) 
    {
        if ( multi )
            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        else
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public Insets getPadding() { return padding; }    
    public void setPadding(Insets padding) {
        this.padding = (padding == null? new Insets(0,0,0,0): padding);
    }
    
    public int getCellVerticalAlignment() { return cellVerticalAlignment; }    
    public void setCellVerticalAlignment(int cellVerticalAlignment) {
        this.cellVerticalAlignment = cellVerticalAlignment;
    }
    
    public int getCellHorizontalAlignment() { return cellHorizontalAlignment; }    
    public void setCellHorizontalAlignment(int cellHorizontalAlignment) {
        this.cellHorizontalAlignment = cellHorizontalAlignment;
    }
    
    public String getOpenAction() { return openAction; }
    public void setOpenAction(String openAction) {
        this.openAction = openAction;
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">

    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }  
    
    public void load() 
    {
        model = new DefaultListModel();
        super.setModel(model);
        
        if (!dynamic) buildList();
    }
    
    public void refresh() { 
        refresh(dynamic);  
    } 
        
    private void refresh(boolean reload) {
        if (reload) buildList();
        
        selectSelectedItems();        
    }    
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }    
    
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc=" Owned / helper methods ">

    public void addListSelectionListener(ListSelectionListener listener) {
        getSelectionSupport().add(listener); 
    }
    
    public void removeListSelectionListener(ListSelectionListener listener) {
        getSelectionSupport().remove(listener); 
    }
    
    private ListSelectionSupport getSelectionSupport() 
    {
        if (selectionSupport == null) 
            selectionSupport = new ListSelectionSupport(); 
        
        return selectionSupport; 
    }
    
    private void fireOpenItem() 
    {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                openItem(); 
            }
        }); 
    }
    
    protected void openItem() 
    {
        try 
        {
            if ( ValueUtil.isEmpty(openAction) ) return;
            
            MethodResolver mr = MethodResolver.getInstance();
            Object outcome = mr.invoke(binding.getBean(), openAction, null, null);
            if ( outcome == null ) return;

            binding.fireNavigation(outcome);
        }
        catch(Exception e) {
            MsgBox.err(e); 
        }
    }

    protected void processMouseEvent(MouseEvent e) 
    {
        if (e.getID() == MouseEvent.MOUSE_PRESSED && e.getClickCount() == 2) 
        {
            e.consume(); 
            fireOpenItem();
        }
        else { 
            super.processMouseEvent(e); 
        } 
    }    
    
    private void buildList() 
    {
        String strHandler = getHandler();
        String strItems = getItems();        
        boolean hasHandler = (strHandler != null && strHandler.length() > 0);
        boolean hasItems = (strItems != null && strItems.length() > 0);         
        if (!hasHandler && !hasItems) return; 
        
        ListPaneModel newModel = null;
        if (hasHandler) {            
            Object value = null; 
            try { 
                value = UIControlUtil.getBeanValue(this, strHandler); 
            } catch(Throwable t) {
                System.out.println("[WARN] error get bean value caused by " + t.getMessage());
            }
            //--
            if (value instanceof ListPaneModel) {
                newModel = (ListPaneModel)value;
            }        
        } else if (hasItems) {
            Object value = null; 
            try { 
                value = UIControlUtil.getBeanValue(this, strItems); 
            } catch(Throwable t) {
                System.out.println("[WARN] error get bean value caused by " + t.getMessage());
            }
            //--
            newModel = new DefaultListPaneModel(value); 
        }
        loadItems(newModel);         
    } 
    
    private void loadItems(ListPaneModel newModel) {
        if (newModel == null) return;

        List list = new ArrayList();        
        Object value = newModel.getItems();
        if (value == null) {
            //do nothing 
        } else if (value instanceof Collection) {
            list.addAll((Collection) value);
        } else if (value.getClass().isArray()) {
            for (Object o: (Object[]) value) {
                list.add(o);
            }
        }
        //--
        model.clear(); 
        int i = 0; 
        for (Object o: list) { 
            model.add(i++, o); 
        } 
        
        ListPaneModel oldModel = listPaneModel;
        if (oldModel != null) oldModel.setProvider(null);
        
        listPaneModel = newModel;
        newModel.setProvider(new ProviderImpl()); 
        newModel.afterLoadItems(); 
    } 
        
    private void selectSelectedItems() 
    {
        Object value = null;
        String name = getName();
        if (name != null && name.length() > 0) {
            try {
                value = UIControlUtil.getBeanValue(this);
            } catch(Throwable e) {
                System.out.println("[WARN] error get bean value caused by " + e.getMessage());
            }            
        }

        if (value == null) {
            setSelectedIndex(0); 
            
        } else if ( isMultiselect() ) {
            List list = new ArrayList();
            if ( value instanceof Collection )
                list.addAll( (Collection) value );
            else if ( value.getClass().isArray() ) {
                for(Object o: (Object[]) value) list.add( o );
            }
            
            if ( list.size() == 0 ) return;
            
            List indices = new ArrayList();
            for( int i=0; i < model.getSize(); i++ ) {
                Object item = model.getElementAt(i);
                if ( list.remove( item ) ) indices.add(i);
            }
            if ( indices.size() == 0 ) return;
            
            ListSelectionModel sm = getSelectionModel();
            sm.clearSelection();
            int size = getModel().getSize();
            for(int i = 0; i < indices.size(); i++) {
                int idx = Integer.parseInt( indices.get(i)+"" );
                if ( idx < size) {
                    sm.addSelectionInterval(idx, idx);
                }
            }
            
        } else {
            setSelectedValue(value, true);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultCellRenderer (class) ">
    
    private class DefaultCellRenderer implements ListCellRenderer 
    {
        XList root = XList.this; 
        private JLabel cellLabel;
        private FontSupport fontSupport; 
        
        DefaultCellRenderer() 
        {
            cellLabel = new JLabel();
            cellLabel.setOpaque(true);
            fontSupport = new FontSupport(); 
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            Insets pads = root.getPadding();
            if (pads == null) pads = new Insets(1,3,1,3); 

            cellLabel.setBorder(BorderFactory.createEmptyBorder(pads.top, pads.left, pads.bottom, pads.right)); 
            cellLabel.setComponentOrientation(list.getComponentOrientation());
            cellLabel.setSize(list.getFixedCellWidth(), list.getFixedCellHeight());
            cellLabel.setEnabled(list.isEnabled());
            cellLabel.setFont(list.getFont());
            cellLabel.setVerticalAlignment(getCellVerticalAlignment());
            cellLabel.setHorizontalAlignment(getCellHorizontalAlignment());
            
            if (isSelected) {
                cellLabel.setBackground(list.getSelectionBackground());
                cellLabel.setForeground(list.getSelectionForeground());
                fontSupport.applyStyles(cellLabel, "font-weight:bold;");
            } else {                
                cellLabel.setBackground(list.getBackground());
                cellLabel.setForeground(list.getForeground());
            }
            
            if (Beans.isDesignTime()) {
                cellLabel.setText( value+"" );
                return cellLabel;
            }
            
            Object cellValue = value;
            String expr = getExpression();
            if (expr != null) {
                try {
                    Object exprBean = createExpressionBean(value);
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expr); 
                } catch(Throwable e) {;}
            } 
            
            cellLabel.setText((cellValue == null? " ": cellValue.toString()));            
            
            String strIcon = new MapObject(value).getString("icon");
            if (strIcon == null || strIcon.length() == 0) 
                strIcon = (root.listPaneModel == null? null: root.listPaneModel.getDefaultIcon()); 
            if (strIcon != null && strIcon.length() > 0) {
                Icon anIcon = ImageIconSupport.getInstance().getIcon(strIcon);
                if (anIcon == null) {
                    try { 
                        anIcon = UIManager.getLookAndFeelDefaults().getIcon(strIcon); 
                    } catch(Throwable t){;} 
                }
                cellLabel.setIcon(anIcon); 
            }
            return cellLabel;
        }
        
        private Object createExpressionBean(Object itemBean) 
        {
            ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
            beanSupport.setItem(getVarName(), itemBean); 
            return beanSupport.createProxy(); 
        } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ListSelectionSupport (class) "> 
    
    private class ListSelectionSupport implements ListSelectionListener
    {
        XList root = XList.this; 
        List<ListSelectionListener> listeners = new ArrayList(); 
        
        void remove(ListSelectionListener listener) 
        {
            if (listener != null) listeners.remove(listener); 
        }
        
        void add(ListSelectionListener listener) 
        {
            if (listener != null) 
            {
                listeners.remove(listener); 
                listeners.add(listener); 
            }
        }
        
        public void valueChanged(final ListSelectionEvent evt) 
        {
            try 
            {
                int selIndex = root.getSelectedIndex(); 
                if (selIndex != -1 && !evt.getValueIsAdjusting()) 
                {
                    Object value = (root.isMultiselect()? root.getSelectedValues(): root.getSelectedValue());
                    UIControlUtil.setBeanValue(root.getBinding(), root.getName(), value);
                    
                    if (root.getVarStatus() != null) 
                    {
                        ItemStatus stat = new ItemStatus();
                        stat.multiSelect = root.isMultiselect(); 
                        stat.index = root.getSelectedIndex();
                        stat.name = root.getName();                        
                        stat.value = value;
                        UIControlUtil.setBeanValue(root.getBinding(), root.getVarStatus(), stat); 
                    }

                    EventQueue.invokeLater(new Runnable(){
                        public void run() {
                            try { 
                                if (root.listPaneModel == null) return;

                                root.listPaneModel.onselect(root.getSelectedValue()); 
                            } catch(Throwable e) {
                                System.out.println("[WARN] error onselect caused by " + e.getMessage()); 
                            } 
                        } 
                    });
                    
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            binding.notifyDepends(XList.this);                    
                        }
                    });
                } 
                
                //notify listeners
                notifyListeners(evt);
            }
            catch(Exception ex) {
                MsgBox.err(ex); 
            }
        }

        private void notifyListeners(ListSelectionEvent evt) 
        {
            for (ListSelectionListener listener : listeners) { 
                listener.valueChanged(evt); 
            } 
        }    
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ItemStatus (class) ">
    
    public class ItemStatus 
    {
        private Object value;        
        private String name;
        private int index;
        private boolean multiSelect;

        public Object getValue() { return value; }        
        public String getName() { return name; }        
        public int getIndex() { return index; }
        public boolean isMultiSelect() { return multiSelect; }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultListPaneModel ">
    
    private class DefaultListPaneModel extends ListPaneModel 
    {
        private Object items;
        
        DefaultListPaneModel(Object items) {
            this.items = items;
        }
        
        public Object getItems() { return items; } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl ">
    
    private class ProviderImpl implements ListPaneModel.Provider 
    {
        XList root = XList.this; 
        
        public Object getBinding() {
            return root.getBinding(); 
        }

        public void refresh() {
            root.refresh();
        }

        public void reload() {
            root.refresh(true); 
        }        

        public void setSelectedIndex(int index) {
            int size = root.getModel().getSize();
            if (index >= 0 && index < size) {
                root.setSelectedIndex(index); 
            }
        }
    }
    
    // </editor-fold>

    public void setSelectionInterval(int anchor, int lead) {
        if (listPaneModel != null) {
            try {
                Object o = (lead < 0? null: getModel().getElementAt(lead)); 
                if (!listPaneModel.beforeSelect(o)) return;
            } catch(Throwable t) { 
                MsgBox.err(t); 
            }
        }
        super.setSelectionInterval(anchor, lead); 
    }
}
