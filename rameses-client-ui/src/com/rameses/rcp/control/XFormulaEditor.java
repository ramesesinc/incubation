/*
 * XFormulaEditor.java
 *
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.FormulaEditorModel;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TextDocumentModel;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.editor.FormulaDocument;
import com.rameses.rcp.framework.ActionHandler;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.EventQueue;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;

public class XFormulaEditor extends JTextPane implements UIInput 
{
    private Binding binding;
    private String[] depends;
    private int index;

    private String handler;    
    private Object handlerObject;
    
    private String keywordItems;
    private boolean dynamic;
    private boolean readonly;
    private boolean nullWhenEmpty;
    private TrimSpaceOption trimSpaceOption = TrimSpaceOption.NONE;
    private ActionHandlerImpl actionHandler = new ActionHandlerImpl(); 
    
    private List<String> keywords;
    private FormulaDocument document;
    private FormulaEditorModel model;
        
    public XFormulaEditor() 
    {
        super.setDocument(getFormulaDocument());
    }
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public final void setDocument(Document doc) {
    }
            
    public void setName(String name) 
    {
        super.setName(name);
        
        if (Beans.isDesignTime()) setText(name);
    }

    private FormulaDocument getFormulaDocument() 
    {
        if (document == null) 
            document = new FormulaDocument();
        
        return document;
    }
    
    public List<String> getKeywords() 
    {
        if (keywords == null) keywords = new ArrayList();
        
        return keywords;
    }
    
    public FormulaEditorModel getModel() { return model; }
    public void setModel(FormulaEditorModel model) { this.model = model; } 
    
    public String getKeywordItems() { return keywordItems; }    
    public void setKeywordItems(String varItems) {
        this.keywordItems = varItems;
    }
        
    public boolean isDynamic() { return dynamic; }    
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public String getHandler() { return handler; }
    public void setHandler(String handler) { this.handler = handler; }
    
    public Object getHandlerObject() { return handlerObject; } 
    public void setHandlerObject(Object handlerObject) {
        this.handlerObject = handlerObject; 
    }

    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }
    public void setTrimSpaceOption(TrimSpaceOption trimSpaceOption) {
        this.trimSpaceOption = trimSpaceOption;
    }

    public TextCase getTextCase() { 
        return getFormulaDocument().getTextCase(); 
    } 
    public void setTextCase(TextCase textCase) {
        getFormulaDocument().setTextCase(textCase); 
    }
    
    public void setTextCase(String sTextCase) {
        getFormulaDocument().setTextCaseAsString(sTextCase); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput implementation ">

    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) 
    { 
        //detached the handler from the old binding
        if (this.binding != null) 
            this.binding.getActionHandlerSupport().remove(actionHandler); 
        
        this.binding = binding; 
        
        if (binding != null) 
            binding.getActionHandlerSupport().add(actionHandler); 
    }
    
    public void load() 
    {
        Object value = getHandlerObject(); 
        if (!ValueUtil.isEmpty(getHandler())) 
        {
            try {
                value = UIControlUtil.getBeanValue(this, getHandler());
            } catch(Exception ex) {
                if (ClientContext.getCurrentContext().isDebugMode())
                    ex.printStackTrace();
            } 
        }
        
        if (value instanceof FormulaEditorModel) 
        {
            model = (FormulaEditorModel) value;
            model.setProvider(new DocumentProvider());
        }
        else {
            model = null;
        }
        
        if (!isDynamic()) loadVariables();
        
        setInputVerifier( UIInputUtil.VERIFIER );
    }  
    
    public void refresh() 
    {
        if ( dynamic ) loadVariables();

        int oldCaretPos = getCaretPosition();        
        try 
        {
            Object value = UIControlUtil.getBeanValue(this);
            getFormulaDocument().setValue(value, getInputAttributes().copyAttributes()); 
        } 
        catch(Exception e) 
        {
            if (ClientContext.getCurrentContext().isDebugMode()) 
                e.printStackTrace();
        }
        
        try {
            setCaretPosition(oldCaretPos); 
        } catch(Exception ex){;} 
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }    
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }    
    
    public Object getValue() 
    {
        String text = getText();
        if (ValueUtil.isEmpty(text)) 
            return (isNullWhenEmpty()? null: text); 
        
        if (getTrimSpaceOption() != null)
            return getTrimSpaceOption().trim(text);
        
        return text;
    } 
    
    public void setValue(Object value) {
        setText(value == null? "" : value.toString());
    }

    public boolean isNullWhenEmpty() { return nullWhenEmpty; }    
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }

    public boolean isReadonly() { return readonly; }    
    public void setReadonly(boolean readonly) 
    {
        this.readonly = readonly;
        setEditable(!readonly);
    }

    public void setRequestFocus(boolean focus) {
        if (focus) requestFocus();
    }

    public boolean isImmediate() { return false; }          
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Owned and helper methods ">

    private void loadVariables() 
    {
        List <String> list = new ArrayList();
        
        List keywords = getKeywords();
        if (keywords != null && !keywords.isEmpty()) 
            list.addAll(keywords);
        
        try 
        {
            Object value = null;
            String sname = getKeywordItems();
            if (sname != null) 
                value = UIControlUtil.getBeanValue(this, sname);
            
            if ( value == null ) {
                //do nothing
            }
            else if ( value instanceof Collection ) {
                for (Object o : (Collection) value) list.add( o+"" );
            }
            else if ( value.getClass().isArray() ) {
                for (Object o : (Object[]) value) list.add( o+"" );
            }
        } 
        catch(Exception e) 
        {
            if (ClientContext.getCurrentContext().isDebugMode()) 
                e.printStackTrace();
        }

        document.getKeywords().clear();
        document.getKeywords().addAll( list );
        
        if (model != null) 
        {
            List <String> items = model.getKeywords(); 
            if (items != null) document.getKeywords().addAll( items );
        }
    }
        
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" DocumentProvider (class) ">
    
    private class DocumentProvider implements TextDocumentModel.Provider 
    {
        private XFormulaEditor root = XFormulaEditor.this; 
        
        public String getText() { 
            return root.getText(); 
        }
        public void setText(String text) 
        {
            root.setText((text == null? "": text)); 
            root.repaint();
        }
        
        public void insertText(String text) 
        {
            if (text == null) return;

            int caretPos = root.getCaretPosition();
            try 
            {
                int caretCharPos = (text == null? -1: text.indexOf('|'));
                if (caretCharPos >= 0) 
                {
                    StringBuffer sb = new StringBuffer(); 
                    sb.append(text.substring(0, caretCharPos));
                    sb.append(' ');
                    sb.append(text.substring(caretCharPos+1));
                    text = sb.toString(); 
                }
                
                AttributeSet attr = root.getInputAttributes().copyAttributes();
                root.document.insertString(caretPos, text, attr);                
                
                if (caretCharPos >= 0) root.setCaretPosition(caretPos + caretCharPos);
            } 
            catch (Exception ex) {
                System.out.println("[XFormulaEditor] failed to insert text at position " + caretPos + " caused by " + ex.getMessage());
            } 
            finally {
                repaint(); 
            }
        } 

        public void requestFocus() 
        {
            EventQueue.invokeLater(new Runnable() {
                public void run() 
                {
                    root.requestFocus();
                    root.grabFocus();
                }
            }); 
        }
        
        public void load() { root.load(); }
        public void refresh() { root.refresh(); } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ActionHandlerImpl (class) ">   
    
    private class ActionHandlerImpl implements ActionHandler
    {
        XFormulaEditor root = XFormulaEditor.this;
        
        public void onBeforeExecute() {
        }

        /*
         *  This method is called once a button is clicked.
         */
        public void onAfterExecute() 
        {
            if (!document.isDirty()) return;
            
            UIInputUtil.updateBeanValue(root); 
        } 
    }
    
    // </editor-fold>
}
