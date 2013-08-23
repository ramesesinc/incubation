/*
 * XList.java
 *
 * Created on October 29, 2010, 10:59 AM
 * @author jaycverg
 */
package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
    private String openAction;    
    private boolean dynamic;
    private int index;    

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
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public final void setModel(ListModel model) {;}    
    
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
        
        if ( !dynamic ) buildList();
    }
    
    public void refresh() 
    {
        if ( dynamic ) buildList();
        
        selectSelectedItems();
    }    
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }    
    
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc="  Owned / helper methods  ">

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
        if (ValueUtil.isEmpty(items)) return;
        
        model.clear();
        
        List list = new ArrayList();
        try 
        {
            Object value = UIControlUtil.getBeanValue(this, items);
            if (value == null) return;
            
            if (value instanceof Collection)
                list.addAll((Collection) value);
            
            else if (value.getClass().isArray()) 
            {
                for (Object o: (Object[]) value) {
                    list.add( o );
                }
            }
            
            if (list.size() == 0) return;
        } 
        catch(Exception e) {;}
        
        int i = 0;
        for (Object o: list) { 
            model.add(i++, o); 
        } 
    }
    
    private void selectSelectedItems() 
    {
        if ( ValueUtil.isEmpty(getName()) ) return;
        
        Object value = null;
        try {
            value = UIControlUtil.getBeanValue(this);
        } catch(Exception e) {
            if( ClientContext.getCurrentContext().isDebugMode() ) {
                e.printStackTrace();
            }
        }
        
        if ( value == null ) return;
        
        //set selected item(s)
        if ( isMultiselect() ) {
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
    
    // <editor-fold defaultstate="collapsed" desc="  DefaultCellRenderer (class)  ">
    
    private class DefaultCellRenderer implements ListCellRenderer 
    {
        private JLabel cellLabel;
        
        DefaultCellRenderer() 
        {
            cellLabel = new JLabel();
            cellLabel.setOpaque(true);
            cellLabel.setBorder(BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right));            
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            cellLabel.setComponentOrientation(list.getComponentOrientation());
            cellLabel.setSize( list.getFixedCellWidth(), list.getFixedCellHeight() );
            cellLabel.setEnabled(list.isEnabled());
            cellLabel.setFont(list.getFont());
            cellLabel.setVerticalAlignment( getCellVerticalAlignment() );
            cellLabel.setHorizontalAlignment( getCellHorizontalAlignment() );
            
            if (isSelected) 
            {
                cellLabel.setBackground(list.getSelectionBackground());
                cellLabel.setForeground(list.getSelectionForeground());
            } 
            else 
            {
                cellLabel.setBackground(list.getBackground());
                cellLabel.setForeground(list.getForeground());
            }
            
            if ( Beans.isDesignTime() ) 
            {
                cellLabel.setText( value+"" );
                return cellLabel;
            }
            
            Object cellValue = value;
            String expr = getExpression();
            if (expr != null) 
            {
                try 
                {
                    Object exprBean = createExpressionBean(value);
                    cellValue = UIControlUtil.evaluateExpr(exprBean, expr); 
                } 
                catch(Exception e) {;}
            } 
            
            cellLabel.setText((cellValue == null? " " : cellValue.toString()));            
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
    
    // <editor-fold defaultstate="collapsed" desc="  ItemStatus (class)  ">
    
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
}
