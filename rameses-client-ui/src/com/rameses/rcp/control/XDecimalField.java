package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.text.AbstractNumberDocument;
import com.rameses.rcp.control.text.AbstractNumberField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.TextEditorSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;

/**
 *
 * @author wflores
 */
public class XDecimalField extends AbstractNumberField implements UIInput, Validatable, ActiveControl 
{   
    protected ControlProperty property = new ControlProperty();
    protected ActionMessage actionMessage = new ActionMessage();
    private Binding binding;    
    
    private DecimalDocument model = new DecimalDocument(); 
    private boolean nullWhenEmpty;
    private String[] depends; 
    private String pattern;  
    private int index;
        
    public XDecimalField() 
    {
        super();
        TextEditorSupport.install(this); 
        
        if (Beans.isDesignTime()) 
        {
            Font font = (Font) getClientProperty("TextField.font"); 
            if (font != null) super.setFont(font); 
        }
    } 

    protected AbstractNumberDocument createDocument() 
    {
        if (model == null) model = new DecimalDocument(); 
        
        return model; 
    } 

    protected void oncancelEditing() { 
        try {
            refresh();
        } catch(Exception ex){;} 
    }
        
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public void setName(String name) 
    {
        super.setName(name);
        
        if (Beans.isDesignTime()) super.setText(name);
    }
    
    public Object getValue() 
    {
        Number num = getModel().getValue(); 
        if (num == null) {
            return (isUsePrimitiveValue()? 0.0: null); 
        }
        else {
            return (isUsePrimitiveValue()? num.doubleValue(): num); 
        }
    }
    
    public void setValue(Object value) 
    {
        if (value instanceof KeyEvent) 
        {
            KeyEvent e = (KeyEvent) value; 
            char c = e.getKeyChar(); 
            if (Character.isDigit(c) || c == '.' || c == '-') 
                getModel().setValue(c+"");
            else 
                setText("");
        }
        else if (value instanceof BigDecimal) {
            getModel().setValue((BigDecimal) value);
        }
        else {
            getModel().setValue((value == null? "": value.toString()));
        } 
        
        revalidate();
        repaint(); 
    }
    
    public String getPattern() { return model.getFormat(); }     
    public void setPattern(String pattern) { model.setFormat(pattern); } 
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (info == null) return;
        
        PropertyInfoWrapper pi = new PropertyInfoWrapper(info);
        setPattern(pi.getFormat()); 
        setMinValue(pi.getMinValue());
        setMaxValue(pi.getMaxValue());
        setUsePrimitiveValue(pi.isUsePrimitiveValue()); 
    }    
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  DecimalDocument (Class)  "> 
    
    private class DecimalDocument extends AbstractNumberDocument
    {
        int scale = 2;      
        
        public Number decode(String value) 
        {
            try { 
                return new BigDecimal(value); 
            } catch(Exception ex) {
                return null; 
            } 
        } 

        public Number convertValue(Number value) 
        {
            BigDecimal bd = null; 
            if (value instanceof BigDecimal) 
                bd = (BigDecimal) value;
            else 
                bd = new BigDecimal(value.doubleValue());  
            
            return bd.setScale(scale, RoundingMode.HALF_UP); 
        }
        
        protected Number getPrimitiveValue(Number value) {
            return value; 
        }        
        
        public void refresh() 
        {
            revalidate();
            repaint(); 
        } 
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="  UIInput implementations  ">     
    
    public boolean isShowCaption() { return property.isShowCaption(); }    
    public void setShowCaption(boolean showCaption) {
        property.setShowCaption(showCaption);
    }
    
    public Font getCaptionFont() { return property.getCaptionFont(); }    
    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() { return property.getCellPadding(); }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }
    
    public char getCaptionMnemonic() { return property.getCaptionMnemonic(); }    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() { return property.getCaptionWidth(); }    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
    }    
    
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty; 
    }

    public void setRequestFocus(boolean focus) {
        if ( focus ) requestFocus();
    }

    public boolean isImmediate() { return false; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }

    public void refresh() 
    {
        try 
        {
            updateBackground();
            
            Object value = UIControlUtil.getBeanValue(this);
            
            Number number = null;
            if (value == null) {
                //do nothing 
            }
            else if (value instanceof BigDecimal) { 
                number = (BigDecimal) value; 
            } 
            else 
            {
                try {
                     number = getModel().decode(value.toString()); 
                } catch(Exception e) {;} 
            }

            getModel().setValue(number); 
            getModel().showFormattedText(true); 
            getModel().refresh();
        } 
        catch(Exception e) 
        {
            setText("");
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace();
        }
    }

    protected InputVerifier getChildInputVerifier() {
        return UIInputUtil.VERIFIER; 
    }
    
    public void load() {
    } 

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Validatable implementations  ">
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { property.setCaption(caption); }

    public boolean isRequired() { return property.isRequired(); }
    public void setRequired(boolean required) { property.setRequired(required); }

    public void validateInput() 
    { 
        actionMessage.clearMessages();
        property.setErrorMessage(null);
        if ( ValueUtil.isEmpty(getText()) ) 
        {
            if (isRequired()) 
                actionMessage.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
        } 
        
        if ( actionMessage.hasMessages() ) 
            property.setErrorMessage( actionMessage.toString() );
    }

    public ActionMessage getActionMessage() { return actionMessage; }
    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="  ActiveControl implementations  ">
    
    public ControlProperty getControlProperty() { return property; }
    
    // </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc=" PropertyInfoWrapper (Class)  "> 
    
    private class PropertyInfoWrapper 
    {
        private PropertySupport.DecimalPropertyInfo property;
        private Map map = new HashMap(); 
        
        PropertyInfoWrapper(PropertySupport.PropertyInfo info) 
        {
            if (info instanceof Map) map = (Map)info;
            if (info instanceof PropertySupport.DecimalPropertyInfo)
                property = (PropertySupport.DecimalPropertyInfo) info;
        }
        
        public String getFormat() 
        {
            Object value = map.get("format");
            if (value == null && property != null)
                value = property.getFormat();
            
            return (value == null? null: value.toString());
        }
        
        public double getMinValue() 
        {
            Object value = map.get("minValue");
            if (value == null && property != null)
                value = property.getMinValue();
            
            Number num = convertNumber(value);
            return (num == null? -1.0: num.doubleValue());
        }
        
        public double getMaxValue() 
        {
            Object value = map.get("maxValue");
            if (value == null && property != null)
                value = property.getMaxValue();
            
            Number num = convertNumber(value);
            return (num == null? -1.0: num.doubleValue());            
        }    
        
        public boolean isUsePrimitiveValue() 
        {
            Object value = map.get("usePrimitiveValue");
            if (value == null && property != null)
                value = property.isUsePrimitiveValue(); 
            
            Boolean bool = convertBoolean(value);
            return (bool == null? false: bool.booleanValue()); 
        }     
        
        private Number convertNumber(Object value) 
        {
            if (value instanceof Number)
                return (Number) value;
            
            try {
                return Double.parseDouble(value.toString()); 
            } catch(Exception ex) {
                return null; 
            }
        }
        
        private Boolean convertBoolean(Object value) 
        {
            if (value instanceof Boolean)
                return (Boolean) value;
            
            try {
                return Boolean.parseBoolean(value.toString()); 
            } catch(Exception ex) {
                return null; 
            }
        }
    }
    
    // </editor-fold>    
}
