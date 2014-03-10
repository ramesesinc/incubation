/*
 * XImageCanvas.java
 *
 * Created on March 8, 2014, 2:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.*;
import com.rameses.rcp.control.image.ImageView;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores
 */
public class XImageCanvas extends ImageView implements UIControl, MouseEventSupport.ComponentInfo  
{
    private Binding binding;
    private String[] depends;
    private int index; 
    
    private boolean dynamic;

    public XImageCanvas() {
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">    
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    } 

    public int getIndex() { return index; }
    public void setIndex(int index) { 
        this.index = index; 
    }

    public void load() {
    }

    public void refresh() {
        ImageIcon imgobj = null;        
        try {
            Object beanValue = null; 
            String name = getName(); 
            if (name != null && name.length() > 0) {
                beanValue = UIControlUtil.getBeanValue(getBinding(), name);
            } 
            
            if (beanValue instanceof byte[]) { 
                imgobj = new ImageIcon((byte[]) beanValue); 
            } else if (beanValue instanceof URL) {
                imgobj = new ImageIcon((URL) beanValue); 
            } else if (beanValue instanceof ImageIcon) {
                imgobj = (ImageIcon) beanValue;
            } else if (beanValue instanceof String) { 
                String str = beanValue.toString().toLowerCase(); 
                if (str.matches("[a-zA-Z]{1,}://.*")) { 
                    imgobj = new ImageIcon(new URL(beanValue.toString())); 
                } else { 
                    imgobj = null; 
                } 
            } else { 
                imgobj = null; 
            }             
        } 
        catch(Throwable e) { 
            imgobj = null; 
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace();
        } 
        
        setValue(imgobj); 
        refreshCanvas(); 
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }  
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation "> 
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        return map;
    } 
    
    // </editor-fold>    
}
