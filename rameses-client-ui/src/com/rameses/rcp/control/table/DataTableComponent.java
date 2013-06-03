/*
 * DataTableComponent.java
 *
 * Created on January 31, 2011
 * @author jaycverg
 */
package com.rameses.rcp.control.table;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.*;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ChangeLog;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.*;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.beans.Beans;
import java.util.*;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;

public class DataTableComponent extends JTable implements TableControl 
{    
    private static final String COLUMN_POINT = "COLUMN_POINT";

    private Map<Integer, JComponent> editors = new HashMap();
    private Binding itemBinding = new Binding();    
    
    private DataTableModel tableModel;
    private TableListener tableListener;
    private ListPageModel pageModel;    
    private EditorListModel editorModel;
    private AbstractListDataProvider dataProvider;
    private PropertyChangeHandlerImpl propertyHandler;
    private TableModelHandlerImpl tableModelHandler;
    
    private String varName = "item";
    
    //internal flags
    private int editingRow = -1;
    private boolean readonly;
    private boolean required;
    private boolean editingMode;
    private boolean editorBeanLoaded;
    private boolean rowCommited = true;
    private boolean processingRequest;
    private JComponent currentEditor;
    private KeyEvent currentKeyEvent;
    private ListItem previousItem;
    
    //row background color options
    private Color evenBackground;
    private Color oddBackground;
    private Color errorBackground = Color.PINK;
    
    //row foreground color options
    private Color evenForeground;
    private Color oddForeground;
    private Color errorForeground = Color.BLACK;
    
    private Binding binding;
    
    private JLabel lblProcessing;
    private boolean fetching;
    
    
    public DataTableComponent() {
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc="  initComponents  ">
    
    private void initComponents() 
    {
        super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        propertyHandler = new PropertyChangeHandlerImpl(); 
        tableModelHandler = new TableModelHandlerImpl();
        tableModel = new DataTableModel();

        setTableHeader(new DataTableHeader(this));
        getTableHeader().setReorderingAllowed(false);
        addKeyListener(new TableKeyAdapter());       
        
        int cond = WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
        getInputMap(cond).put(enter, "selectNextColumnCell");
        
        KeyStroke shiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 1);
        getInputMap(cond).put(shiftEnter, "selectPreviousColumnCell");
        
        new TableEnterAction().install(this);
        new TableEscapeAction().install(this);
        
        //row editing ctrl+Z support
        KeyStroke ctrlZ = KeyStroke.getKeyStroke("ctrl Z");
        registerKeyboardAction(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) 
            {
                if (!rowCommited) 
                {
                    ChangeLog log = itemBinding.getChangeLog();
                    if (log.hasChanges()) undo();
                    
                    //clear row editing flag of everything is undone
                    if (!log.hasChanges()) 
                    {
                        rowCommited = true;
                        oncancelRowEdit();
                    }
                }
            }
            
        }, ctrlZ, JComponent.WHEN_FOCUSED);
        
        addComponentListener(new ComponentListener() {
            
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}
            
            public void componentResized(ComponentEvent e) 
            {
                if (currentEditor == null) return;
                
                Point colPoint = (Point) currentEditor.getClientProperty(COLUMN_POINT); 
                Rectangle bounds = getCellRect(colPoint.y, colPoint.x, false);
                currentEditor.setBounds(bounds); 
                currentEditor.requestFocus(); 
                currentEditor.grabFocus(); 
            }            
        });        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    protected void uninstall(AbstractListDataProvider dataProvider) {}
    protected void install(AbstractListDataProvider dataProvider) {}
    
    public AbstractListDataProvider getDataProvider() { return dataProvider; }    
    public void setDataProvider(AbstractListDataProvider dataProvider) 
    {
        if (Beans.isDesignTime())
        {
            Column[] columns = (dataProvider == null? null: dataProvider.getColumns());
            setModel(new DataTableModelDesignTime(columns)); 
            return; 
        }
        
        if (this.dataProvider != null) 
        { 
            this.dataProvider.removeHandler(propertyHandler); 
            this.dataProvider.removeHandler(tableModelHandler);
            uninstall(this.dataProvider); 
        }
        
        this.dataProvider = dataProvider; 
        this.editorModel = null;
        this.pageModel = null;  
        
        if (this.dataProvider != null) 
            this.dataProvider.addHandler(propertyHandler); 
        if (dataProvider instanceof ListPageModel) 
            this.pageModel = (ListPageModel) dataProvider; 
        if (dataProvider instanceof EditorListModel) 
            this.editorModel = (EditorListModel) dataProvider; 
        
        tableModel.setDataProvider(dataProvider); 
        if (dataProvider != null) 
        {
            dataProvider.addHandler(tableModelHandler); 
            install(dataProvider);
        } 
        
        tableModel.setBinding(itemBinding); 
        setModel(tableModel);
        buildColumns();
    }
    
    public DataTableModel getDataTableModel() { return tableModel; } 
    
    public boolean isProcessingRequest() { 
        return (processingRequest || fetching); 
    } 
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public void setBinding(Binding binding) { this.binding = binding; }
    public Binding getBinding() { return binding; }
    
    public void setListener(TableListener listener) { this.tableListener = listener; }
    
    public boolean isRequired() { return required; }
    public boolean isEditingMode() { return editingMode; }
    
    public boolean isAutoResize() {
        return getAutoResizeMode() != super.AUTO_RESIZE_OFF;
    }
    
    public void setAutoResize(boolean autoResize) {
        if ( autoResize ) {
            setAutoResizeMode(super.AUTO_RESIZE_LAST_COLUMN);
        } else {
            setAutoResizeMode(super.AUTO_RESIZE_OFF);
        }
    }
    
    public boolean isReadonly() { return readonly; }
    public void setReadonly(boolean readonly) { this.readonly = readonly; }
    
    public Color getEvenBackground() { return evenBackground; }
    public void setEvenBackground(Color evenBackground) { this.evenBackground = evenBackground; }
    
    public Color getOddBackground() { return oddBackground; }
    public void setOddBackground(Color oddBackground) { this.oddBackground = oddBackground; }
    
    public Color getErrorBackground() { return errorBackground; }
    public void setErrorBackground(Color errorBackground) { this.errorBackground = errorBackground; }
    
    public Color getEvenForeground() { return evenForeground; }
    public void setEvenForeground(Color evenForeground) { this.evenForeground = evenForeground; }
    
    public Color getOddForeground() { return oddForeground; }
    public void setOddForeground(Color oddForeground) { this.oddForeground = oddForeground; }
    
    public Color getErrorForeground() { return errorForeground; }
    public void setErrorForeground(Color errorForeground) { this.errorForeground = errorForeground; }
    
    public String getVarStatus()            { return tableModel.getVarStatus(); }
    public void setVarStatus(String status) { tableModel.setVarStatus(status); }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  buildColumns  ">
    
    private void buildColumns() 
    {
        removeAll(); //remove all editors
        editors.clear(); //clear column editors map
        required = false; //reset flag to false
        
        int length = tableModel.getColumnCount();        
        for ( int i=0; i<length; i++ ) 
        {
            Column col = tableModel.getColumn(i);
            TableCellRenderer cellRenderer = TableUtil.getCellRenderer(col);
            TableColumn tableCol = getColumnModel().getColumn(i);
            tableCol.setCellRenderer(cellRenderer);
            applyColumnProperties(tableCol, col);
            
            if (!ValueUtil.isEmpty(col.getEditableWhen())) 
                col.setEditable(true);
            
            if (!col.isEditable()) continue;
            if (editors.containsKey(i)) continue;
            
            JComponent editor = TableUtil.createCellEditor(col);
            if (editor == null) continue; 
            if (!(editor instanceof UIControl))
            {
                System.out.println("Column editor must be an instance of UIControl "); 
                continue;
            }

            editor.setVisible(false);
            editor.setName(col.getName());
            editor.setBounds(-10, -10, 10, 10);
            editor.putClientProperty(JTable.class, true); 
            editor.putClientProperty(Binding.class, getBinding()); 
            editor.putClientProperty(UIInputUtil.Support.class, new EditorInputSupport()); 
            editor.putClientProperty(Validatable.class, new TableColumnValidator(itemBinding, col));
            
            editor.addFocusListener(new EditorFocusSupport());
            addKeyboardAction(editor, KeyEvent.VK_ENTER, true);
            addKeyboardAction(editor, KeyEvent.VK_TAB, true);
            addKeyboardAction(editor, KeyEvent.VK_ESCAPE, false);
            
            UIControl uicomp = (UIControl) editor;
            uicomp.setBinding(itemBinding);
            itemBinding.register(uicomp);
            
            if (editor instanceof Validatable) 
            {
                Validatable vi = (Validatable) editor;
                vi.setRequired(col.isRequired());
                vi.setCaption(col.getCaption());
                
                if (vi.isRequired()) required = true;
            }
            
            editors.put(i, editor);
            add(editor);
        }
        itemBinding.setOwner( binding.getOwner() );
        itemBinding.setViewContext( binding.getViewContext() );
        itemBinding.init(); //initialize item binding
    } 
    
    public void rebuildColumns() 
    {
        tableModel = new DataTableModel();
        tableModel.setDataProvider(dataProvider);
        setModel(tableModel);
        buildColumns();
    }    
    
    private void addKeyboardAction(JComponent comp, int key, boolean commit) {
        EditorKeyBoardAction kba = new EditorKeyBoardAction(comp, key, commit);
        comp.registerKeyboardAction(kba, kba.keyStroke, JComponent.WHEN_FOCUSED);
    }
    
    private void applyColumnProperties(TableColumn tc, Column c) {
        if ( c.getMaxWidth() > 0 ) tc.setMaxWidth( c.getMaxWidth() );
        if ( c.getMinWidth() > 0 ) tc.setMinWidth( c.getMinWidth() );
        
        if ( c.getWidth() > 0 ) {
            tc.setWidth( c.getWidth() );
            tc.setPreferredWidth( c.getWidth() );
        }
        
        tc.setResizable( c.isResizable() );
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  JTable properties  ">
    
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        if ( dataProvider != null && fetching ) 
        {
            if ( lblProcessing == null ) 
            {
                lblProcessing = new JLabel("<html><h1>Loading...</h1></html>");
                lblProcessing.setForeground(Color.GRAY);
                lblProcessing.setVerticalAlignment(SwingUtilities.TOP);
                lblProcessing.setBorder(new EmptyBorder(5,10,10,10));
            } 
            
            Rectangle rec = getVisibleRect();
            Graphics g2 = g.create();
            g2.translate(rec.x, rec.y);
            lblProcessing.setSize(rec.width, rec.height);
            lblProcessing.paint(g2);
            g2.dispose();
        }
    }
    
    public void setTableHeader(JTableHeader tableHeader) 
    {
        super.setTableHeader(tableHeader);
        
        tableHeader = getTableHeader(); 
        if (tableHeader == null) return;
        
        tableHeader.setDefaultRenderer(TableUtil.getHeaderRenderer());
        tableHeader.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) 
            {
                if (currentEditor == null) return;
                
                Point p = new Point(me.getX(), me.getY());
                int colIndex = columnAtPoint(p);
                if (colIndex < 0) return;
                
                Point colPoint = (Point) currentEditor.getClientProperty(COLUMN_POINT); 
                if (colPoint.x-1 == colIndex || colPoint.x == colIndex || colPoint.x+1 == colIndex) 
                {
                    Rectangle bounds = getCellRect(colPoint.y, colPoint.x, false);
                    currentEditor.setBounds(bounds); 
                    currentEditor.requestFocus(); 
                    currentEditor.grabFocus(); 
                } 
                else { 
                    hideEditor(false);
                } 
            }
        });
    }
    
    protected void onopenItem() {}
    private void openItem() 
    {
        // do not do anything if there is an active process running
        if (processingRequest) return;
        
        try 
        { 
            processingRequest = true;
            onopenItem(); 
        } 
        catch(Exception ex) {
            MsgBox.err(ex); 
        } finally {
            processingRequest = false;
        }
    }    
    
    protected void onprocessMouseEvent(MouseEvent me) {}    
    protected void processMouseEvent(MouseEvent me) 
    {
        // do not do anything if there is an active process running
        if (processingRequest) return;
        
        if (me.getID()==MouseEvent.MOUSE_CLICKED && me.getClickCount()==2) 
        {
            Point p = new Point(me.getX(), me.getY());
            int colIndex = columnAtPoint(p);
            Column dc = tableModel.getColumn(colIndex);
            if (dc != null && !dc.isEditable()) 
            {
                me.consume();
                openItem(); 
                return;
            }
        }
    
        onprocessMouseEvent(me); 
        if (me.isConsumed()) {
            //do nothing
        } else { 
            super.processMouseEvent(me);
        }
    }
    
    public boolean editCellAt(int rowIndex, int colIndex, EventObject e) 
    {
        if (isReadonly()) return false;
        if (editorModel == null) return false; 
        
        if (e instanceof MouseEvent) 
        {
            MouseEvent me = (MouseEvent) e;
            if (me.getClickCount()==2 && SwingUtilities.isLeftMouseButton(me)) {
                //do nothing
            } else {
                return false; 
            }
        }
        
        editItem(rowIndex, colIndex, e); 
        return false;
    }
    
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) 
    {
        // do not do anything if there is an active process running
        if (processingRequest) return;
        
        int oldColIndex = getSelectedColumn();
        int oldRowIndex = getSelectedRow();
        if (editingMode) 
        {
            Point point = (Point) currentEditor.getClientProperty(COLUMN_POINT);
            if (rowIndex != point.y || columnIndex != point.x) {
                hideEditor(currentEditor, point.y, point.x, true, true);
            }            
        }

        if (rowIndex != oldRowIndex && editorModel != null && editingRow >= 0) 
        {
            ListItem li = editorModel.getListItem(editingRow);
            if (li != null && (editorModel.isTemporaryItem(li) || li.getState()==ListItem.STATE_EDIT)) 
            { 
                try 
                {
                    if (!validateRow(editingRow))
                    {
                        String errmsg = editorModel.getMessageSupport().getErrorMessage(editingRow); 
                        if (errmsg != null) throw new Exception(errmsg); 

                        //exit from this process
                        return;
                    } 

                    if (li.getState() == ListItem.STATE_DRAFT)
                        editorModel.flushTemporaryItem(li);


                    editorModel.fireCommitItem(li);
                    itemBinding.getChangeLog().clear(); 
                    editingRow = -1;
                } 
                catch(Exception ex) 
                {
                    tableModel.fireTableRowsUpdated(editingRow, editingRow);       
                    MsgBox.err(ex); 
                    return; 
                } 
            } 
        }
        
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
        putClientProperty("selectionPoint", new Point(columnIndex, rowIndex));        
        if (rowIndex != oldRowIndex) editingRow = -1;
            
        if (columnIndex != oldColIndex && dataProvider != null)
        {
            Column oColumn = tableModel.getColumn(columnIndex);
            dataProvider.setSelectedColumn((oColumn == null? null: oColumn.getName()));
        }
            
        if (rowIndex != oldRowIndex) rowSelectionChanged(rowIndex);
    }
    
    protected void processKeyEvent(KeyEvent e) 
    {
        // do not do anything if there is an active process running
        if (processingRequest) return;
        if (currentEditor != null) return; 
        
        currentKeyEvent = e;
        super.processKeyEvent(e);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  row movements/actions support  ">
        
    public void tableChanged(TableModelEvent e) 
    {
        if (getSelectedRow() >= getRowCount()) 
            setRowSelectionInterval(0, 0); 

        super.tableChanged(e); 
    }        
        
    protected void onrowChanged() {}     
    private void rowSelectionChanged(int index) 
    {
        dataProvider.setSelectedItem(index);
        editorBeanLoaded = false;
        rowCommited = true;
        previousItem = null; 
        onrowChanged(); 
    } 
    
    protected void oncancelRowEdit() {}
    public final void cancelRowEdit() 
    {
        if (!rowCommited) 
        {
            ChangeLog log = itemBinding.getChangeLog();
            List<ChangeLog.ChangeEntry> ceList = log.undoAll();
            for (ChangeLog.ChangeEntry ce : ceList) 
            {
                //dataProvider.setSelectedColumn(ce.getFieldName());
                //dataProvider.updateSelectedItem();
            }
            
            rowCommited = true;
            int row = getSelectedRow();
            tableModel.fireTableRowsUpdated(row, row);
            oncancelRowEdit();
        }
    }
    
    public void undo() 
    {
        int row = getSelectedRow();
        ChangeLog.ChangeEntry ce = itemBinding.getChangeLog().undo();
        tableModel.fireTableRowsUpdated(row, row);
        //dataProvider.setSelectedColumn(ce.getFieldName());
        //dataProvider.updateSelectedItem();
    }
    
    public final void removeItem() 
    {
        if (isReadonly()) return;
        if (editorModel == null) return;
        
        int rowIndex = getSelectedRow();
        if (rowIndex < 0) return;        
        //if the ListModel has error messages
        //allow editing only to the row that caused the error
        if (editorModel.getMessageSupport().hasErrorMessages() && 
            editorModel.getMessageSupport().getErrorMessage(rowIndex) == null) 
            return;
        
        try 
        {
            editorModel.setSelectedItem(rowIndex); 
            editorModel.fireRemoveItem(editorModel.getListItem(rowIndex)); 
        }
        catch(Exception ex) 
        {
            MsgBox.err(ex); 
        }
    } 
    
    public Object createExpressionBean(Object itemBean) 
    {
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    }
    
    protected void onchangedItem(ListItem item) {} 
    
    public void editItem(int rowIndex, int colIndex, EventObject e) 
    {
        if (editorModel == null) return;         
        
        /*
            if ListItem has error messages, 
            allow editing only to the row that caused the error
         */
        if (dataProvider.getMessageSupport().hasErrorMessages() && 
            dataProvider.getMessageSupport().getErrorMessage(getSelectedRow()) == null) 
            return;
        
        ListItem oListItem = tableModel.getListItem(rowIndex);
        if (!editorModel.isAllowedForEditing(oListItem)) return;
        
        Column col = tableModel.getColumn(colIndex);
        if (col == null || !col.isEditable()) return;
                
        try 
        {
            if (oListItem.getItem() == null || oListItem.getState() == ListItem.STATE_EMPTY) 
            {
                editorModel.loadTemporaryItem(oListItem);
                oListItem.setRoot(binding.getBean()); 
                tableModel.fireTableRowsUpdated(rowIndex, rowIndex); 
            }       
        } 
        catch(Exception ex) 
        {
            MsgBox.err(ex); 
            return; 
        }
        
        // evaluate the editableWhen expression 
        if ( !ValueUtil.isEmpty(col.getEditableWhen()) ) 
        {
            boolean passed = false;
            ExpressionResolver er = ExpressionResolver.getInstance();
            try 
            {
                Object exprBean = createExpressionBean(oListItem.getItem());
                passed = UIControlUtil.evaluateExprBoolean(exprBean, col.getEditableWhen());
            } 
            catch(Exception ex) { 
                System.out.println("Failed to evaluate expression " + col.getEditableWhen() + " caused by " + ex.getMessage());
            }
            
            if (!passed) 
            {
                if (dataProvider.getListItem(rowIndex+1) == null) 
                {
                    oListItem.loadItem(null, ListItem.STATE_EMPTY);
                    tableModel.fireTableRowsUpdated(rowIndex, rowIndex); 
                }                
                return;
            }
        }
        
        JComponent editor = editors.get(colIndex);
        if ( editor == null ) return;
        
        if (editorModel.isLastItem(oListItem)) 
            editorModel.addEmptyItem(); 
        
        oListItem.setRoot(binding.getBean());
        tableModel.fireTableRowsUpdated(rowIndex, rowIndex); 
        
        try {
            onchangedItem(oListItem); 
        } catch(Exception ex) {
            MsgBox.err(ex); 
        }
        
        showEditor(editor, rowIndex, colIndex, e);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  helper/supporting methods  ">
    
    protected void onfocusGained(FocusEvent e) {} 
    protected void onfocusLost(FocusEvent e) {}    
    protected final void processFocusEvent(FocusEvent e) 
    {
        if (e.getID() == FocusEvent.FOCUS_GAINED) 
            onfocusGained(e);
        else if (e.getID() == FocusEvent.FOCUS_LOST) 
            onfocusLost(e);
        
        super.processFocusEvent(e); 
    }
    
    private void log(String msg) 
    {
        String name = getClass().getSimpleName();
        System.out.println("["+name+"] " + msg);
    }
    
    private boolean isPrintableKey(EventObject e) 
    {
        KeyEvent ke = null;
        if (e instanceof KeyEvent) ke = (KeyEvent) e;
        if (ke == null) ke = currentKeyEvent; 
        if (ke == null) return false; 
        
        if (ke.isActionKey() || ke.isControlDown() || ke.isAltDown()) return false;
        
        switch (ke.getKeyCode()) 
        {
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_DELETE:
            case KeyEvent.VK_ENTER:
                return false;
        }         
        return true;
    }
    
    private boolean isEditKey(EventObject e) 
    {
        if (!(e instanceof KeyEvent)) return false; 
        
        KeyEvent ke = (KeyEvent) e;
        switch (ke.getKeyCode()) 
        {
            case KeyEvent.VK_F2:
            case KeyEvent.VK_INSERT:
            case KeyEvent.VK_BACK_SPACE:
                return true;
        }        
        return false;
    }
    
    private void selectAll(JComponent editor, EventObject evt) 
    {
        if (editor instanceof JTextComponent) 
            ((JTextComponent) editor).selectAll();
        else if (editor instanceof JCheckBox) 
            ((UIInput) editor).setValue(evt);
    }
    
    private void focusNextCellFrom(int rowIndex, int colIndex) 
    {
        int nextCol = findNextEditableColFrom(colIndex);
        int firstEditable = findNextEditableColFrom(-1);
        
        if (nextCol >= 0) 
            this.changeSelection(rowIndex, nextCol, false, false);
        
        else if (rowIndex+1 < tableModel.getRowCount()) 
            this.changeSelection(rowIndex+1, firstEditable, false, false);
        
        else 
        {
            ListItem item = dataProvider.getSelectedItem();
            /*
            boolean lastRow = !(rowIndex + dataProvider.getTopRow() < dataProvider.getMaxRows());
            
            if ( item.getState() == ListItem.STATE_EMPTY ) lastRow = false;
            
            if ( !lastRow ) 
            {
                this.changeSelection(rowIndex, firstEditable, false, false);
                moveNextRecord();
            } 
            else 
            {
                this.changeSelection(0, firstEditable, false, false);
                dataProvider.moveFirstPage();
            }*/
        }
    }
    
    private int findNextEditableColFrom(int colIndex) 
    {
        for (int i=colIndex+1; i<tableModel.getColumnCount(); i++ ) {
            if (editors.get(i) != null) return i;
        }
        return -1;
    }
    
    private void hideEditor(boolean commit) {
        hideEditor(commit, true);
    }
    
    private void hideEditor(boolean commit, boolean grabFocus) 
    {
        if ( !editingMode || currentEditor == null ) return;
        
        Point point = (Point) currentEditor.getClientProperty(COLUMN_POINT);
        hideEditor(currentEditor, point.y, point.x, commit, grabFocus);
    }
    
    private void hideEditor(JComponent editor, int rowIndex, int colIndex, boolean commit, boolean grabFocus) 
    {
        /*
         * force to invoke the setValue of the editor support when editor is instanceof JCheckBox 
         * to make sure that the data has been sent to the temporary storage before committing. 
         */
        if (editor instanceof JCheckBox && editor instanceof UIInput) 
        {
            UIInput uiinput = (UIInput) editor;
            uiinput.putClientProperty("cellEditorValue", uiinput.getValue()); 
        }
        
        editor.setVisible(false);
        editor.setInputVerifier(null);
        editingMode = false;        
        currentEditor = null;
        
        if (commit) 
        {
            Object value = editor.getClientProperty("cellEditorValue"); 
            tableModel.setBinding(itemBinding); 
            tableModel.setValueAt(value, rowIndex, colIndex); 

            try 
            {
                if (editorModel != null) 
                {
                    ListItem oListItem = editorModel.getListItem(editingRow);
                    editorModel.fireColumnUpdate(oListItem);
                }
            } 
            catch(Exception ex) {
                MsgBox.alert(ex);
            }
        }
        
        tableModel.fireTableRowsUpdated(rowIndex, rowIndex); 
        if (grabFocus) grabFocus(); 
    } 
    
    private boolean validateRow(int rowIndex) 
    {
        //exit right away if no editor model specified 
        if (editorModel == null) return true;
        
        ActionMessage ac = new ActionMessage();
        itemBinding.validate(ac);
        if ( ac.hasMessages() )
            dataProvider.getMessageSupport().addErrorMessage(rowIndex, ac.toString());
        else 
            dataProvider.getMessageSupport().removeErrorMessage(rowIndex);
        
        if ( ac.hasMessages() ) return false;
        
        try 
        {
            editorModel.fireValidateItem( dataProvider.getListItem(rowIndex) );
            dataProvider.getMessageSupport().removeErrorMessage(rowIndex);
        } 
        catch (Exception e ) 
        {
            if (ClientContext.getCurrentContext().isDebugMode()) 
                e.printStackTrace(); 
            
            String msg = getMessage(e)+"";
            dataProvider.getMessageSupport().addErrorMessage(rowIndex, msg);
            return false;
        }        
        return true;
    }
    
    private String getMessage(Throwable t) {
        if (t == null) return null;
        
        String msg = t.getMessage();
        Throwable cause = t.getCause();
        while (cause != null) {
            String s = cause.getMessage();
            if (s != null) msg = s;
            
            cause = cause.getCause();
        }
        return msg;
    }
    
    private void showEditor(final JComponent editor, int rowIndex, int colIndex, EventObject e) 
    {
        Rectangle bounds = getCellRect(rowIndex, colIndex, false);
        editor.putClientProperty(COLUMN_POINT, new Point(colIndex, rowIndex));
        editor.setBounds(bounds);
        
        UIControl ui = (UIControl) editor;
        boolean refreshed = false;
        if ( !editorBeanLoaded ) 
        {
            itemBinding.update(); //clear change log
            Object bean = dataProvider.getSelectedItem().getItem();
            itemBinding.setBean(bean);
            itemBinding.refresh();
            refreshed = true;
            editorBeanLoaded = true;
        }
        
        if (e == null) e = currentKeyEvent; 
        
        if (e instanceof MouseEvent || isEditKey(e)) 
        {
            if (!refreshed) ui.refresh();
            
            selectAll(editor, e);
        } 
        else if (isPrintableKey(e))  
        {
            char ch = currentKeyEvent.getKeyChar();
            boolean dispatched = false; 
            if (editor instanceof JTextComponent) 
            {
                try 
                {
                    JTextComponent jtxt = (JTextComponent) editor;
                    jtxt.setText(ch+""); 
                    dispatched = true; 
                } 
                catch (Exception ex) {;} 
            }

            if (!dispatched && (editor instanceof UIInput)) 
            {
                UIInput uiinput = (UIInput) editor;
                uiinput.setValue((KeyEvent) e);
            }
        } 
        else {
            return;
        }
        
        oneditCellAt(rowIndex, colIndex);
        previousItem = dataProvider.getSelectedItem();
        
        InputVerifier verifier = (InputVerifier) editor.getClientProperty(InputVerifier.class);
        if ( verifier == null ) 
        {
            verifier = editor.getInputVerifier();
            editor.putClientProperty(InputVerifier.class, verifier);
        }

        editor.putClientProperty("cellEditorValue", null); 
        editor.setInputVerifier( verifier );
        editor.setVisible(true);
        editor.grabFocus();       
        
        editingRow = rowIndex; 
        editingMode = true;
        rowCommited = false;
        currentEditor = editor;
    }

    private boolean isValidKeyCode(int keyCode) { 
        return (keyCode >= 32 && keyCode <= 126); 
    } 
    
    protected void oneditCellAt(int rowIndex, int colIndex) {}
    
    public AbstractListModel getListModel() {
        return null;
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="  EditorInputSupport (class)  ">
    
    private class EditorInputSupport implements UIInputUtil.Support 
    {       
        public void setValue(String name, Object value) 
        {
            if (currentEditor == null) return;
            //temporarily stores the editor value 
            //the value is committed once the cell selection is about to changed
            //System.out.println("setValue: name="+name + ", value="+value);
            currentEditor.putClientProperty("cellEditorValue", value); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  EditorFocusSupport (class)  ">
    
    private class EditorFocusSupport implements FocusListener 
    {        
        private boolean fromTempFocus;
        
        public void focusGained(FocusEvent e) 
        {
            if (fromTempFocus) 
            {
                if (editingMode) 
                {
                    JComponent comp = (JComponent) e.getSource();
                    String ubv = comp.getClientProperty("updateBeanValue")+""; 
                    if ("false".equals(ubv)) return; 
                    
                    hideEditor(true);
                    Point point = (Point) comp.getClientProperty(COLUMN_POINT);                    
                    focusNextCellFrom(point.y, point.x);
                }
                fromTempFocus = false;
            } 
        } 
        
        public void focusLost(FocusEvent e) 
        {
            fromTempFocus = e.isTemporary();
            //if (!e.isTemporary()) hideEditor(true, false);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  EditorKeyBoardAction (class) ">
    
    private class EditorKeyBoardAction implements ActionListener {
        
        KeyStroke keyStroke;
        private boolean commit;
        private ActionListener[] listeners;
        
        EditorKeyBoardAction(JComponent comp, int key, boolean commit) 
        {
            this.commit = commit;
            this.keyStroke = KeyStroke.getKeyStroke(key, 0);
            
            //hold only action on enter key
            //this is usually used by lookup
            if ( key == KeyEvent.VK_ENTER && comp instanceof JTextField ) 
            {
                JTextField jtf = (JTextField) comp;
                listeners = jtf.getActionListeners();
            }
        }
        
        public void actionPerformed(ActionEvent e) 
        {
            if ( listeners != null && listeners.length > 0 ) 
            {
                for ( ActionListener l: listeners) {
                    l.actionPerformed(e);
                }
            } 
            else 
            {
                JComponent comp = (JComponent) e.getSource();
                Point point = (Point) comp.getClientProperty(COLUMN_POINT);
                if (commit) 
                    focusNextCellFrom( point.y, point.x );
                
                else 
                {
                    comp.firePropertyChange("enableInputVerifier", true, false); 
                    hideEditor(comp, point.y, point.x, false, true);
                }
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  TableKeyAdapter (class)  ">
    
    private class TableKeyAdapter extends KeyAdapter 
    {        
        public void keyPressed(KeyEvent e) 
        {
            // do not do anything if there is an active process running
            if (processingRequest) return;
        
            switch (e.getKeyCode()) 
            {
                case KeyEvent.VK_DOWN:
                    if (dataProvider.isLastItem(getSelectedRow())) 
                    {
                        e.consume();
                        dataProvider.moveNextRecord(); 
                    }
                    break;
                    
                case KeyEvent.VK_UP:
                    if (dataProvider.isFirstItem(getSelectedRow())) 
                    {
                        e.consume();
                        dataProvider.moveBackRecord(); 
                    } 
                    break;
                    
                case KeyEvent.VK_HOME:
                    if (pageModel != null && e.isControlDown()) 
                    {
                        e.consume();
                        pageModel.moveFirstPage(); 
                    }
                    break;
                    
                case KeyEvent.VK_PAGE_DOWN:
                    if (pageModel != null) 
                    {
                        e.consume();
                        pageModel.moveNextPage(); 
                    }
                    break;
                    
                case KeyEvent.VK_PAGE_UP:
                    if (pageModel != null) 
                    {
                        e.consume();
                        pageModel.moveBackPage(); 
                    }
                    break;
                    
                case KeyEvent.VK_DELETE:
                    removeItem();       
                    EventQueue.invokeLater(new Runnable() {
                        public void run() 
                        {
                            requestFocusInWindow(); 
                            grabFocus();
                        }
                    });
                    break;
                    
                case KeyEvent.VK_ENTER:
                    if (e.isControlDown()) openItem();
                    
                    break;
                    
                case KeyEvent.VK_ESCAPE:
                    cancelRowEdit();
                    break;
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  TableEnterAction (class)  ">
    
    private class TableEnterAction implements ActionListener 
    {
        private JComponent component;
        private ActionListener oldAction;
        
        void install(JComponent component) 
        {
            this.component = component;
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
            oldAction = component.getActionForKeyStroke(ks);
            component.registerKeyboardAction(this, ks, JComponent.WHEN_FOCUSED); 
        }
        
        public void actionPerformed(ActionEvent e) 
        {
            if ( !isReadonly()  && editors.size() > 0 ) 
            {
                JTable tbl = DataTableComponent.this;
                int row = tbl.getSelectedRow();
                int col = tbl.getSelectedColumn();
                focusNextCellFrom(row, col);
            }
            else 
            {
                JRootPane rp = component.getRootPane();
                if (rp != null && rp.getDefaultButton() != null ) 
                {
                    JButton btn = rp.getDefaultButton();
                    btn.doClick();
                } 
                else if (oldAction != null) { 
                    oldAction.actionPerformed(e); 
                } 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  TableEscapeAction (class)  ">
    
    private class TableEscapeAction implements ActionListener 
    {        
        private ActionListener oldAction;
        
        void install(JComponent comp) 
        {
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);            
            oldAction = comp.getActionForKeyStroke(ks);
            comp.registerKeyboardAction(this, ks, JComponent.WHEN_FOCUSED); 
        }
        
        public void actionPerformed(ActionEvent e) 
        {
            if (editorModel != null) 
            {
                fireAction(); 
                return;
            }

            ActionListener actionL = (ActionListener) getRootPane().getClientProperty("Window.closeAction"); 
            if (actionL != null) 
                actionL.actionPerformed(e); 
            else if (oldAction != null) 
                oldAction.actionPerformed(e); 
        } 
        
        private void fireAction() 
        {
            int rowIndex = getSelectedRow(); 
            ListItem li = editorModel.getListItem(rowIndex); 
            if (li == null) return; 
            
            if (editorModel.isTemporaryItem(li))
            {
                editorModel.getMessageSupport().removeErrorMessage(li.getIndex());
                editorModel.removeTemporaryItem(li); 
                
                Point sel = (Point) getClientProperty("selectionPoint"); 
                if (sel == null) sel = new Point(0, 0);
                
                changeSelection(rowIndex, sel.x, false, false);                 
            }
            else if (li.getState() == ListItem.STATE_EDIT && editorModel.getMessageSupport().hasErrorMessages()) 
            {
                editorModel.getMessageSupport().removeErrorMessage(li.getIndex()); 
                li.setState(ListItem.STATE_SYNC); 
                tableModel.fireTableRowsUpdated(rowIndex, rowIndex); 
            } 
            else {
                tableModel.fireTableRowsUpdated(rowIndex, rowIndex);                 
            }             
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  PropertyChangeHandlerImpl (class)  ">    
    
    private class PropertyChangeHandlerImpl implements PropertyChangeHandler 
    {
        DataTableComponent root = DataTableComponent.this; 
        
        public void firePropertyChange(String name, int value) {
        }

        public void firePropertyChange(String name, boolean value) 
        {
            if ("loading".equals(name)) 
            {
                root.fetching = value;
                root.repaint(); 
            }             
        }

        public void firePropertyChange(String name, String value) {
        }

        public void firePropertyChange(String name, Object value) 
        {
            if ("focusSelectedItem".equals(name)) 
                focusSelectedItem();
        } 
        
        void focusSelectedItem() 
        {
            Point loc = (Point) getClientProperty("selectionPoint");
            if (loc == null) loc = new Point(); 
            
            ListItem li = root.dataProvider.getSelectedItem();
            int rowIndex = (li == null? 0: li.getIndex()); 
            if (!root.dataProvider.validRange(rowIndex)) rowIndex = 0;
            
            root.tableModel.fireTableRowsUpdated(rowIndex, rowIndex); 
            //root.changeSelection(rowIndex, loc.x, false, false); 
            root.setRowSelectionInterval(rowIndex, rowIndex); 
        }
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="  TableModelHandlerImpl (class)  ">    
    
    private class TableModelHandlerImpl implements TableModelHandler 
    {
        DataTableComponent root = DataTableComponent.this; 

        public void fireTableCellUpdated(int row, int column) {}
        public void fireTableDataChanged() {}
        public void fireTableRowsDeleted(int firstRow, int lastRow) {}
        public void fireTableRowsInserted(int firstRow, int lastRow) {}
        public void fireTableRowsUpdated(int firstRow, int lastRow) {}
        public void fireTableStructureChanged() {}

        public void fireTableRowSelected(int row, boolean focusOnItemDataOnly) 
        {
            Point sel = (Point) root.getClientProperty("selectionPoint"); 
            if (sel == null) sel = new Point();
            
            ListItem li = root.dataProvider.getListItem(row); 
            if (li == null) 
                root.getSelectionModel().setSelectionInterval(0, 0); 
            
            else 
            {
                int preferredRow = 0;
                int itemCount = root.dataProvider.getListItemCount();
                for (int i=row; i>=0; i--) 
                {
                    if (focusOnItemDataOnly) 
                    { 
                        //select the ListItem whose item bean is not null
                        if (root.dataProvider.getListItemData(i) != null) 
                        {
                            preferredRow = i;
                            break;
                        }
                    }
                    else if (i < itemCount) 
                    {
                        //retain the focus index as long the range index is still valid
                        preferredRow = i;
                        break;
                    }
                }
                root.getSelectionModel().setSelectionInterval(preferredRow, preferredRow); 
            }
        }
    }
    
    // </editor-fold>             
}
