/*
 * XProgressBar.java
 *
 * Created on July 21, 2010, 5:24 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.ProgressListener;
import com.rameses.rcp.common.ProgressModel;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.NavigatablePanel;
import com.rameses.rcp.framework.NavigationHandler;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.common.MethodResolver;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.util.ValueUtil;
import java.awt.Font;
import java.awt.Insets;
import java.util.Map;
import javax.swing.JProgressBar;


public class XProgressBar extends JProgressBar implements UIControl, 
    ProgressListener, MouseEventSupport.ComponentInfo, ActiveControl  
{    
    private Binding binding;
    private String[] depends;
    private int index;
    private String onComplete;
    
    private ProgressModel model;
    
    private int stretchWidth;
    private int stretchHeight; 
    private String visibleWhen; 
    
    public XProgressBar() {
        new MouseEventSupport(this).install(); 
    }
    
    public void refresh() { 
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }
    }
    
    public void load() {
        Object value = UIControlUtil.getBeanValue(this);
        if ( value instanceof ProgressModel ) {
            model = (ProgressModel) value;
            model.addListener(this);
        }
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public Map getInfo() { 
        return null; 
    }    
    
    public void onStart(int min, int max) {
        if ( model.isIndeterminate() ) {
            setIndeterminate( true );
        } else {
            setMinimum(min);
            setMaximum(max);
            setStringPainted(true);
        }
        binding.notifyDepends(this);
    }
    
    public void onProgress(int totalFetched, int maxSize) {
        if ( model.isIndeterminate() ) return;
        
        setMaximum(maxSize);
        setValue(totalFetched);
        binding.notifyDepends(this);
    }
    
    public void onStop() {
        if ( model.isIndeterminate() ) {
            setIndeterminate( false );
        }
        
        binding.notifyDepends(this);
        if( model.isCompleted() ) {
            fireAction();
        }
    }
    
    public void onSuspend() {
        binding.notifyDepends(this);
    }
    
    private void fireAction() {
        if ( ValueUtil.isEmpty(onComplete) ) return;
        
        try 
        {
            ClientContext ctx = ClientContext.getCurrentContext();
            MethodResolver mr = MethodResolver.getInstance();
            Object outcome = mr.invoke(binding.getBean(), onComplete, null, null);
            
            NavigationHandler nh = ctx.getNavigationHandler();
            NavigatablePanel panel = UIControlUtil.getParentPanel(this, null);
            
            nh.navigate(panel, this, outcome);            
        }
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception e) {
            throw new IllegalStateException("XProgressBar::fireAction", e);
        }
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    

    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public String[] getDepends() {
        return depends;
    }
    
    public void setDepends(String[] depends) {
        this.depends = depends;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    
    public Binding getBinding() {
        return binding;
    }
    
    public String getOnComplete() {
        return onComplete;
    }
    
    public void setOnComplete(String onComplete) {
        this.onComplete = onComplete;
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property; 
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>        
    
}
