package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ThemeUI;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UIOutput;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author jaycverg
 */
public class XLabel extends JLabel implements UIOutput, ActiveControl 
{
    private ControlProperty property = new ControlProperty();
    private Binding binding; 
    private String[] depends;
    private String expression;
    private String visibleWhen;
    private String varName;    
    private int index;    
    private boolean useHtml;
    
    private Font sourceFont;
    private String fontStyle; 
    private Insets padding; 
    private Format format; 
    
    /**
     * ActiveControl support fields/properties
     * this is used when this UIControl is used as a label for an ActiveControl
     */
    private String labelFor;
    private boolean addCaptionColon = true;
    private boolean forceUseActiveCaption;
    private ControlProperty activeProperty;
    private JComponent activeComponent;
    private ActiveControlSupport activeControlSupport;

    private Logger logger;     
    private Border sourceBorder;
    
    public XLabel() 
    {  
        this(false); 
    } 
    
    public XLabel(boolean forceUseActiveCaption) 
    {
        super();
        this.forceUseActiveCaption = forceUseActiveCaption;
        
        setPadding(new Insets(1,3,1,1));
        
        //default font
        Font f = ThemeUI.getFont("XLabel.font");
        if (f != null) setFont(f);
    }
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    private Logger getLogger() 
    {
        if (logger == null) 
            logger = Logger.getLogger(getClass().getName());
        
        return logger;
    }
    
    public boolean isUseHtml() { return useHtml; } 
    public void setUseHtml(boolean useHtml) 
    { 
        this.useHtml = useHtml; 
        
        if (Beans.isDesignTime()) showDesignTimeValue(); 
    }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
            
    public String getExpression() 
    {
        if (!Beans.isDesignTime()) return expression;
        
        return expression;
    }
    
    public void setExpression(String expression) 
    {
        this.expression = expression;
        
        if (Beans.isDesignTime()) showDesignTimeValue(); 
    }    
    
    public void setText(String text) 
    {
        if (Beans.isDesignTime()) 
            setExpression(text); 
        else 
            setTextValue(text); 
    } 
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) { this.visibleWhen = visibleWhen;  }
    
    public void setBorder(Border border) 
    {
        BorderWrapper wrapper = new BorderWrapper(border, getPadding()); 
        super.setBorder(wrapper); 
        this.sourceBorder = wrapper.getBorder(); 
    }
    
    public void setBorder(String uiresource) 
    {
        try 
        { 
            Border border = UIManager.getLookAndFeelDefaults().getBorder(uiresource); 
            if (border != null) setBorder(border); 
        } 
        catch(Exception ex) {;} 
    }
        
    public String getCaption() {
        return property.getCaption();
    }    
    public void setCaption(String caption) {
        property.setCaption(caption);
    }
    
    public boolean isShowCaption() {
        return property.isShowCaption();
    }    
    public void setShowCaption(boolean show) {
        property.setShowCaption(show);
    }
    
    public char getCaptionMnemonic() {
        return property.getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
    }
    
    public Font getCaptionFont() {
        return property.getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }
        
    public String getFor() { return labelFor; }    
    public void setFor(String name) { this.labelFor = name; }
    
    public void setLabelFor(Component c) 
    {
        activeComponent = (JComponent) c;
        if (c instanceof ActiveControl) 
        {
            ActiveControl ac = (ActiveControl) c;
            activeProperty = ac.getControlProperty();
            String acCaption = activeProperty.getCaption();
            if ( forceUseActiveCaption || (!ValueUtil.isEmpty(acCaption) && !acCaption.equals("Caption")) ) 
            {
                setName(null);
                setExpression(null);
                formatText(activeProperty.getCaption(), activeProperty.isRequired());
                super.setDisplayedMnemonic(activeProperty.getCaptionMnemonic());
            }
            
            activeControlSupport = new ActiveControlSupport();
            activeProperty.addPropertyChangeListener(activeControlSupport);
        }
        super.setLabelFor(c);
    }
    
    public Insets getPadding() { return padding; }    
    public void setPadding(Insets padding) 
    {
        this.padding = padding;
        setBorder(this.sourceBorder); 
    }
    
    public boolean isAddCaptionColon() { return addCaptionColon; }    
    public void setAddCaptionColon(boolean addCaptionColon) 
    {
        this.addCaptionColon = addCaptionColon;
        formatText( activeProperty.getCaption(), activeProperty.isRequired() );
    }
    
    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } else {
            super.setFont(sourceFont); 
        } 
        new FontSupport().applyStyles(this, fontStyle);
    }

    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIOutput implementation ">    
    
    public Object getValue() 
    {
        Object beanValue = null;
        boolean hasName = !ValueUtil.isEmpty(getName());
        if ( hasName ) beanValue = UIControlUtil.getBeanValue(this);
        
        if ( !ValueUtil.isEmpty(expression) ) 
        {
            Object exprBean = binding.getBean(); 
            if (getVarName() != null) exprBean = createExpressionBean(beanValue); 
            
            return UIControlUtil.evaluateExpr(exprBean, expression);
        }
        
        else if ( hasName ) 
            return beanValue; 
        else 
            return super.getText();
    }
    
    public void setName(String name) 
    {
        super.setName(name);
        
        if (Beans.isDesignTime()) showDesignTimeValue(); 
    }

    public int getIndex() { return index; }    
    public void setIndex(int idx) { index = idx; }
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }    
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public void load() 
    {
        if ( !ValueUtil.isEmpty(labelFor) ) 
        {
            UIControl c = binding.find(labelFor);
            if (c instanceof JComponent) 
                this.setLabelFor((JComponent) c);
        }
    } 
    
    public void refresh() 
    {
        try 
        {
            String name = getName();
            boolean hasName = (name != null && name.length() > 0); 
            
            Object beanValue = null;             
            if (hasName) beanValue = UIControlUtil.getBeanValue(getBinding(), name); 

            Object exprBean = createExpressionBean(beanValue);             
            String exprWhen = getVisibleWhen();
            if (exprWhen != null && exprWhen.length() > 0) {
                boolean result = UIControlUtil.evaluateExprBoolean(exprBean, exprWhen);
                setVisible(result); 
                if (!result) return;
            }

            Object value = null;            
            String exprStr = getExpression(); 
            if (exprStr != null && exprStr.length() > 0) {
                value = UIControlUtil.evaluateExpr(exprBean, exprStr);
            } else if (hasName) {
                value = beanValue;
                if (beanValue != null && format != null)
                    value = format.format(beanValue);
            } else { 
                value = super.getText();
            } 
            
            setTextValue((value == null? "": value.toString()));            
        } 
        catch(Throwable e) { 
            setTextValue("");
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace();
        }
    }   
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }    
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    public ControlProperty getControlProperty() { return property; }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Owned and helper methods ">   
       
    private void showDesignTimeValue() 
    {
        if (!Beans.isDesignTime()) return;
        
        String str = null; 
        if ((str = getExpression()) != null) 
            super.setText(resolveText(str)); 
        else if ((str = getName()) != null) 
            super.setText(str); 
        else 
            super.setText("");
    }
    
    private String originalText;    
    private void setTextValue(String text) 
    {
        this.originalText = text; 
        super.setText(resolveText(text));
    }
    
    private String resolveText(String text) 
    {
        if (isUseHtml() && text != null) 
        {
            StringBuffer sb = new StringBuffer();
            if (text.toLowerCase().indexOf("<html>") < 0) 
                sb.append("<html>"); 
            
            sb.append(text); 
            
            if (text.toLowerCase().lastIndexOf("</html>") < 0)
                sb.append("</html>");

            return sb.toString(); 
        }
        return text; 
    }
    
    private Object createExpressionBean(Object itemBean) 
    {
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    }  
    
    private void formatText(String text, boolean required) 
    {
        StringBuffer sb = new StringBuffer(text);
        if (addCaptionColon && !ValueUtil.isEmpty(text)) sb.append(" :");

        if (required) 
        {
            int mnem = getDisplayedMnemonic();
            int idx = findDisplayedMnemonicIndex(sb, mnem);
            if (idx != -1) 
                sb.replace(idx, idx+1, "<u>" + sb.charAt(idx) + "</u>");
            
            sb.insert(0, "<html>");
            sb.append(" <font color=\"red\">*</font>");
            sb.append("</html>");
        }
        
        super.setText(sb.toString());
    }
    
    static int findDisplayedMnemonicIndex(StringBuffer text, int mnemonic) 
    {
        if (text == null || mnemonic == '\0') return -1;
        
        char uc = Character.toUpperCase((char)mnemonic);
        char lc = Character.toLowerCase((char)mnemonic);
        
        int uci = text.indexOf(uc+"");
        int lci = text.indexOf(lc+"");
        
        if (uci == -1) {
            return lci;
        } else if(lci == -1) {
            return uci;
        } else {
            return (lci < uci) ? lci : uci;
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControlSupport (class) ">
    
    private class ActiveControlSupport implements PropertyChangeListener 
    {        
        private Color oldFg;
        
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            Object value = evt.getNewValue();
            
            if ( "caption".equals(propName) ) {
                String text = (value == null)? "" : value+"";
                formatText( text, activeProperty.isRequired() );
                
            } else if ( "captionMnemonic".equals(propName) ) {
                setDisplayedMnemonic( (value+"").charAt(0) );
                formatText( activeProperty.getCaption(), activeProperty.isRequired());
                
            } else if ( "required".equals(propName) ) {
                boolean req = Boolean.parseBoolean(value+"");
                formatText( activeProperty.getCaption(), req);
                
            } else if ( "errorMessage".equals(propName) ) {
                String message = (value != null)? value+"" : null;
                boolean error = !ValueUtil.isEmpty(message);
                if ( error ) {
                    oldFg = getForeground();
                    setForeground(Color.RED);
                } else {
                    setForeground(oldFg);
                }
                setToolTipText(message);
                if(activeComponent != null) {
                    activeComponent.setToolTipText(message);
                }
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BorderWrapper (class) ">

    private class BorderWrapper extends AbstractBorder
    {   
        XLabel root = XLabel.this;
        private Border border;
        private Insets padding;
        
        BorderWrapper(Border border, Insets padding) {
            if (border instanceof BorderWrapper) 
                this.border = ((BorderWrapper) border).getBorder(); 
            else 
                this.border = border; 
            
            this.padding = copy(padding); 
        }
        
        public Border getBorder() { return border; } 
        
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }
        
        public Insets getBorderInsets(Component c, Insets ins) {
            if (ins == null) new Insets(0,0,0,0);
            
            ins.top = ins.left = ins.bottom = ins.right = 0;
            if (border != null) 
            {
                Insets ins0 = border.getBorderInsets(c); 
                ins.top += ins0.top;
                ins.left += ins0.left;
                ins.bottom += ins0.bottom;
                ins.right += ins0.right;
            }
            
            ins.top += padding.top;
            ins.left += padding.left;
            ins.bottom += padding.bottom;
            ins.right += padding.right;
            return ins; 
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (border != null) border.paintBorder(c, g, x, y, w, h); 
        }
        
        private Insets copy(Insets padding) {
            if (padding == null) return new Insets(0, 0, 0, 0);
            
            return new Insets(padding.top, padding.left, padding.bottom, padding.right); 
        }
    }

    // </editor-fold>
}
