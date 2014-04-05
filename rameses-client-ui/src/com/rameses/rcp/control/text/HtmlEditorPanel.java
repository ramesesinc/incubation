/*
 * HtmlEditorPanel.java
 *
 * Created on April 4, 2014, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.common.Task;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.net.URL;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author wflores
 */
public class HtmlEditorPanel extends JPanel 
{
    private JPanel toolbar;
    private JTextPane editor;    
    private JScrollPane scrollpane;
    private HTMLEditorKit htmlkit;
    private HTMLDocument htmldoc;
    
    private String fontStyle;
    
    public HtmlEditorPanel() {
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new BorderLayout()); 

        JTextPane ed = getEditor();
        ed.setDocument(new HTMLDocument());  
        //ed.setContentType("text/html");        
        ed.setEditable(true); 
        
        if (!Beans.isDesignTime()) { 
            htmldoc = (HTMLDocument) ed.getDocument();
            htmlkit = (HTMLEditorKit) ed.getEditorKit();
            htmlkit.getStyleSheet().addRule("A { color: #0000ff; }"); 
        } 
        
        JPanel toolbar = getToolbar(); 
        toolbar.setLayout(new ToolbarLayout()); 
        toolbar.add(new BoldActionButton()); 
        toolbar.add(new ItalicActionButton()); 
        toolbar.add(new UnderlineActionButton()); 
        toolbar.add(new StrikeThroughActionButton()); 
        toolbar.add(Box.createHorizontalStrut(10)); 
        toolbar.add(new BulletActionButton()); 
        //toolbar.add(new NumberListActionButton()); 
        //toolbar.add(Box.createHorizontalStrut(10)); 
        toolbar.add(new LinkActionButton()); 
        //toolbar.add(new TestActionButton()); 
        add(toolbar, BorderLayout.NORTH); 
        
        JScrollPane scrollpane = new JScrollPane(ed); 
        add(scrollpane);        
    } 
    
    private JTextPane getEditor() {
        if (editor == null) {
            editor = new JTextPane();
        }
        return editor; 
    }
    
    private JPanel getToolbar() {
        if (toolbar == null) {
            toolbar = new JPanel();
        }
        return toolbar;
    }
    
    private Style defaultStyle;
    private Style getDefaultStyle() {
        if (defaultStyle == null) { 
            defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE); 
        } 
        return defaultStyle; 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters "> 
    
    public String getText() {
        String sval = (editor == null? null: editor.getText()); 
        if (sval == null || sval.length() == 0) return null; 
        
        return sval;
    }
    public void setText(String text) {
        if (editor != null) {
            editor.setText(text == null? "": text);
        } 
    }
    
    public Object getValue() {
        return getText(); 
    }
    
    public void setValue(Object value) throws Exception {
        URL url = null; 
        if (value == null) {
            editor.setText("");
            editor.setCaretPosition(0);
        } else if (value instanceof URL) {
            url = (URL) value;
        } else if (value.toString().toLowerCase().matches("^[a-zA-Z0-9]{1,}://.*$")) {
            url = new URL(value.toString()); 
        } else { 
            editor.setText(value.toString()); 
            editor.setCaretPosition(0); 
        } 
        
        if (url != null) { 
            URLWorkerTask uwt = new URLWorkerTask(url);
            ClientContext.getCurrentContext().getTaskManager().addTask(uwt); 
        }        
    }
    
    protected HTMLDocument getDocument() {
        return htmldoc;
    }
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }
    
    public void setEnabled(boolean enabled) { 
        super.setEnabled(enabled); 
        toolbar.setVisible(enabled);
    } 
    
    public boolean isEditable() {
        return editor.isEditable(); 
    }
    public void setEditable(boolean editable) {
        editor.setEditable(editable);
        toolbar.setVisible(editable); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BoldActionButton ">
    
    private ImageIcon getImageIcon(String name) { 
        try {
            ClassLoader loader = null;
            if ( Beans.isDesignTime() ) 
                loader = getClass().getClassLoader();
            else 
                loader = ClientContext.getCurrentContext().getClassLoader();

            URL url = loader.getResource(name);
            return new ImageIcon(url);
        } catch (Throwable ex) {
            System.out.println("[WARN] failed to load icon caused by " + ex.getClass().getName() + ": " + ex.getMessage()); 
            return null; 
        } 
    } 
    
    private class BoldActionButton extends JButton 
    {
        BoldActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            setToolTipText("Bold");
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/bold.png")); 
            addActionListener(new StyledEditorKit.BoldAction());
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ItalicActionButton ">    
    
    private class ItalicActionButton extends JButton 
    {
        ItalicActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("Italic");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/italic.png")); 
            addActionListener(new StyledEditorKit.ItalicAction());
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UnderlineActionButton ">    
    
    private class UnderlineActionButton extends JButton 
    {
        UnderlineActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("Underline");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/underline.png")); 
            addActionListener(new StyledEditorKit.UnderlineAction());
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" StrikeThroughActionButton ">    
    
    private class StrikeThroughActionButton extends JButton implements ActionListener
    {
        StrikeThroughActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("StrikeThrough");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/strikethrough.png")); 
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (!editor.isEnabled()) return;
            if (!editor.hasFocus()) editor.grabFocus(); 

            MutableAttributeSet attr = htmlkit.getInputAttributes();
            boolean toggle = (StyleConstants.isStrikeThrough(attr)? false : true);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setStrikeThrough(sas, toggle);
            setCharacterAttributes(sas, false);            
        }
        
        private void setCharacterAttributes(AttributeSet attr, boolean replace) {
	    int p0 = editor.getSelectionStart();
	    int p1 = editor.getSelectionEnd();
	    if (p0 != p1) {
		htmldoc.setCharacterAttributes(p0, p1 - p0, attr, replace);
	    }
            
	    MutableAttributeSet inputAttributes = htmlkit.getInputAttributes();
	    if (replace) inputAttributes.removeAttributes(inputAttributes);

	    inputAttributes.addAttributes(attr);
	}        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BulletActionButton ">    
    
    private class BulletActionButton extends JButton 
    {
        BulletActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            
            String html = "<ul><li></li></ul>";
            HTMLEditorKit.InsertHTMLTextAction action = new HTMLEditorKit.InsertHTMLTextAction(
                "Bullets",  html, HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL
            );
            setAction(action);
            setText("");
            setToolTipText("Bullet list");
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/bullet.png")); 
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NumberListActionButton ">    
    
    private class NumberListActionButton extends JButton 
    {
        NumberListActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            
            String html = "<ol><li></li></ol>";
            HTMLEditorKit.InsertHTMLTextAction action = new HTMLEditorKit.InsertHTMLTextAction(
                "NumberedList",  html, HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL
            );
            setAction(action);
            setText("");
            setToolTipText("Number list");
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/bullet.png")); 
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LinkActionButton ">    
    
    private class LinkActionButton extends JButton implements ActionListener
    {
        LinkActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("Hyperlink");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/link.png")); 
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (!editor.isEnabled()) return;
            if (!editor.hasFocus()) editor.grabFocus(); 

            StyledDocument doc = editor.getStyledDocument();
            int startpos = editor.getSelectionStart();
            String seltext = editor.getSelectedText();
            if (seltext == null || seltext.length() == 0) { 
                //do nothing
            } else { 
                Object initialValue = null;
                MutableAttributeSet attr = htmlkit.getInputAttributes();  
                Object anchor = attr.getAttribute(HTML.Tag.A);
                if (anchor instanceof SimpleAttributeSet) {
                    SimpleAttributeSet aset = (SimpleAttributeSet)anchor;
                    initialValue = aset.getAttribute(HTML.Attribute.HREF);
                }
                
                HtmlLinkDialog dialog = new HtmlLinkDialog();
                dialog.setCaption("Enter a URL: ");
                dialog.setValue(initialValue);
                if (dialog.open(HtmlEditorPanel.this) != HtmlLinkDialog.APPROVE_OPTION) return; 
                
                //String surl = JOptionPane.showInputDialog(HtmlEditorPanel.this, "Enter a URL: ", (initialValue==null? null: initialValue.toString())); 
                //if (surl == null) return;
                
                String surl = dialog.getValue(); 
                if (surl == null || surl.length() == 0) {
                    if (anchor == null) return;
                    
                    attr.removeAttribute(HTML.Tag.A);
                    try {
                        int len = seltext.length();
                        htmldoc.remove(startpos, len);
                        htmldoc.insertString(startpos, seltext, attr); 
                        editor.select(startpos, startpos+len);
                    } catch(Throwable t) {;}
                } else { 
                    SimpleAttributeSet ahref = new SimpleAttributeSet();
                    ahref.addAttribute(HTML.Attribute.HREF, surl);

                    SimpleAttributeSet alink = new SimpleAttributeSet();
                    alink.addAttribute(HTML.Tag.A, ahref); 
                    setCharacterAttributes(alink, false); 
                } 
            } 
        } 
        
        private void setCharacterAttributes(AttributeSet attr, boolean replace) {
	    int p0 = editor.getSelectionStart();
	    int p1 = editor.getSelectionEnd();
	    if (p0 != p1) {
		htmldoc.setCharacterAttributes(p0, p1 - p0, attr, replace);
	    }
            
	    MutableAttributeSet inputAttributes = htmlkit.getInputAttributes();
	    if (replace) inputAttributes.removeAttributes(inputAttributes);

	    inputAttributes.addAttributes(attr);
	} 
        
        private boolean isStyleApplied(StyledDocument doc, String seltext, int startpos) {
            int len = (seltext == null? 0: seltext.length());
            if (len == 0) return false;

            for (int i=0; i<len; i++) { 
                AttributeSet attrs = doc.getCharacterElement(startpos+i).getAttributes();
                if (!StyleConstants.isStrikeThrough(attrs)) return false; 
            } 
            return true;
        }        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TestActionButton ">    
    
    private class TestActionButton extends JButton implements ActionListener
    {
        private Style style;
        
        TestActionButton() { 
            super("Test"); 
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            addActionListener(this); 
        } 
        
        public void actionPerformed(ActionEvent e) {
            System.out.println(editor.getText());
            Document doc = editor.getDocument();
            String text = "";
            try {
                text = doc.getText(0, doc.getLength());
                System.out.println(text);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            
            StringBuffer sb = new StringBuffer(); 
            int len = doc.getLength();
            StyledDocument sdoc = editor.getStyledDocument();
            for (int i=0; i<len; i++) {
                
            }
        } 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout ">
    
    private class ToolbarLayout implements LayoutManager
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    w += dim.width;
                    h = Math.max(dim.height, h); 
                }
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
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
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, dim.width, h); 
                    x += dim.width;
                }
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" URLWorkerTask (class) "> 
    
    private class URLWorkerTask extends Task 
    { 
        private URL url;
        private boolean done;
        
        URLWorkerTask(URL url) {
            this.url = url; 
        }
        
        public boolean accept() {
            return (done? false: true); 
        }

        public void execute() {
            try {
                editor.setText("<html><body><b>Loading...</b></body></html>");
                if (url != null) editor.setPage(url); 
                
                editor.setCaretPosition(0);
            } catch(Throwable t) { 
                editor.setText("<html><body color=\"red\">error caused by "+t.getMessage()+"</body></html>"); 
                
                if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
            } finally { 
                done = true;                 
            } 
        }    
    }
    
    // </editor-fold>
}
