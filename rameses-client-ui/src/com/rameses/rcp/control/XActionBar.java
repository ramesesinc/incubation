/*
 * XActionBar.java
 *
 * Created on July 23, 2010, 1:21 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.border.BorderProxy;
import com.rameses.rcp.control.border.XToolbarBorder;
import com.rameses.rcp.framework.ActionProvider;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.ComponentSupport;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.ui.UIComposite;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.constant.UIConstants;
import com.rameses.rcp.control.layout.ToolbarLayout;
import com.rameses.rcp.framework.UIController;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class XActionBar extends JPanel implements UIComposite 
{        
    private Binding binding;
    private String[] depends;
    private boolean useToolBar;
    private boolean dynamic;
    private int spacing = 0;
    private int index;
    
    private Insets padding = new Insets(0, 0, 0, 0);
    private BorderProxy borderProxy = new BorderProxy();        
    private ToolbarLayout toolbarLayout = new ToolbarLayout();
    private ContainerLayout containerLayout = new ContainerLayout(); 
    private ComponentSupport componentSupport = new ComponentSupport(); 
    
    private String orientation = UIConstants.HORIZONTAL;
    private String orientationHAlignment = UIConstants.LEFT;
    private String orientationVAlignment = UIConstants.TOP;
    
    //XButton target
    private String target;
    
    private List<XButton> buttons = new ArrayList();
    private JComponent toolbarComponent;
    
    //flag
    private boolean dirty;
    
    //button template
    private XButton buttonTpl = new XButton();
    private String textAlignment = "CENTER_LEFT";
    private String textPosition = "CENTER_TRAILING";
    
    private int buttonCaptionOrientation = SwingConstants.CENTER;
    private boolean buttonTextInHtml;
    private boolean buttonAsHyperlink;
    private boolean showCaptions = true;
    
    public XActionBar() 
    {
        borderProxy.setBorder(new XToolbarBorder());
        super.setBorder(borderProxy); 
        super.setLayout(new ContainerLayout());
        setUseToolBar(true);
        
        if(Beans.isDesignTime()) {
            buttonTpl.setText(getClass().getSimpleName());
        }
    }
    
    public void setLayout(LayoutManager mgr) {;}

    public Border getBorder() { 
        return (borderProxy == null? null: borderProxy.getBorder()); 
    }
    
    public void setBorder(Border border) { 
        if (border instanceof BorderProxy) {
            //do not accept BorderProxy class 
        }
        else if (borderProxy != null) {
            borderProxy.setBorder(border); 
        } 
    }
    
    public void refresh() {
        buildToolbar();
    }
    
    public void load() {
        buildButtons();
    }
    
    public void reload() {
        buildButtons();
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="  helper methods  ">
    
    private void buildButtons() 
    {
        buttons.clear();
        List<Action> actions = new ArrayList();
        
        //--get actions defined from the code bean
        Object value = null;
        try {
            value = UIControlUtil.getBeanValue(this);
        } catch(Exception e) {;}
        
        if (value == null) {
            //do nothing
        } 
        else if (value.getClass().isArray()) 
        {
            for (Action aa: (Action[]) value) {
                actions.add(aa);
            }
        } 
        else if (value instanceof Collection) {
            actions.addAll((Collection) value);
        }
        
        if (actions.isEmpty()) 
        {
            //--get actions defined from the action provider
            ActionProvider actionProvider = ClientContext.getCurrentContext().getActionProvider();
            if (actionProvider != null) 
            {
                UIController controller = binding.getController();
                List <Action> aa = actionProvider.getActionsByType(getName(), controller);
                if (aa != null) actions.addAll(aa);
            }
        } 
        
        if (actions.size() > 0) 
        {
            Collections.sort(actions);
            for (Action action: actions) 
            {
                //check permission
                String permission = action.getPermission();
                String role = action.getRole();
                String domain = action.getDomain();
                /*
                if (permission != null && binding.getController().getName() != null)
                    permission = binding.getController().getName() + "." + permission;
                */
                boolean allowed = ControlSupport.isPermitted(domain, role, permission);
                if (!allowed) continue;
                
                XButton btn = createButton(action);
                Object actionInvoker = action.getProperties().get("Action.Invoker");
                btn.putClientProperty("Action.Invoker", actionInvoker); 
                buttons.add(btn);                
            }
        }
        
        //set dirty flag to true
        dirty = true;
    }
    
    private XButton createButton(Action action) 
    {
        XButton btn = new XButton();
        btn.setFocusable(false);
        
        //map properties from the button template
        btn.setName(action.getName());        
        btn.setFont(buttonTpl.getFont());
        btn.setForeground(buttonTpl.getForeground());        
//        btn.setBorderPainted(buttonTpl.isBorderPainted());
//        btn.setContentAreaFilled(buttonTpl.isContentAreaFilled());
//        btn.setVerticalTextPosition(buttonTpl.getVerticalTextPosition());
//        btn.setHorizontalTextPosition(buttonTpl.getHorizontalTextPosition());
        if ( buttonTpl.isPreferredSizeSet() )
            btn.setPreferredSize(buttonTpl.getPreferredSize());
        
        if ( !ValueUtil.isEmpty(action.getCaption()) ) 
        {
            if ( isButtonAsHyperlink() ) 
            {
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setText("<html><a href='#'>" + action.getCaption() + "</a></html>");
            } 
            else if ( isButtonTextInHtml() ) 
            {
                if ( (getTextAlignment()+"").toUpperCase().indexOf("CENTER") >= 0 )
                    btn.setText("<html><center>" + action.getCaption() + "</center></html>");
                else
                    btn.setText("<html>" + action.getCaption() + "</html>");
            } 
            else {
                btn.setText(action.getCaption());
            }
        }
        
        componentSupport.alignText(btn, getTextAlignment());
        componentSupport.alignTextPosition(btn, getTextPosition());
        btn.setIndex(action.getIndex());
        btn.setUpdate(action.isUpdate());
        btn.setImmediate(action.isImmediate());
        btn.setMnemonic(action.getMnemonic());
        btn.setToolTipText(action.getTooltip());
        
        if (action.getIcon() != null) 
            btn.setIcon(ControlSupport.getImageIcon(action.getIcon()));

        btn.putClientProperty("visibleWhen", action.getVisibleWhen());
        btn.setBinding(binding);
        
        Map props = new HashMap(action.getProperties());
        Object depends = props.get("depends"); 
        if (depends != null && !(depends instanceof Object[])) 
            props.put("depends", new String[]{depends.toString()}); 
        
        if ( props.get("shortcut") != null ) btn.setAccelerator(props.remove("shortcut")+"");
        if ( props.get("target") != null ) btn.setTarget(props.remove("target")+"");
        if ( props.get("default") != null ) 
        {
            String dfb = props.remove("default")+"";
            if ( dfb.equals("true")) btn.putClientProperty("default.button", true);
        }
        
        //map out other properties
        if ( !props.isEmpty() ) 
        {
            PropertyResolver res = PropertyResolver.getInstance();
            for (Object entry : props.entrySet()) 
            {
                Map.Entry me = (Map.Entry) entry;
                if ("action".equals(me.getKey())) continue;
                if ("type".equals(me.getKey())) continue;
                try {
                    res.setProperty( btn, (String) me.getKey(), me.getValue());
                } catch(Exception e){;}
            }
        }
        
        Map params = action.getParams();
        if (params != null && params.size() > 0) btn.getParams().putAll(params);
        
        if ( !action.getClass().getName().equals(Action.class.getName()) ) {
            btn.putClientProperty(Action.class.getName(), action);
        }
               
        boolean b = action.isShowCaption();
        if (!b) b = isShowCaptions();
        
        if (b && action.getCaption() != null) 
        {
            String s = btn.getText(); 
            if (!s.trim().startsWith("<html>")) 
                btn.setText("<html>"+ s +"</html>"); 
        } 
        else {     
            btn.setText("");
        } 

        if (action.getTooltip() != null) 
            btn.setToolTipText(action.getTooltip()); 
        else if (action.getCaption() != null) 
            btn.setToolTipText(action.getCaption()); 
        
        return btn;
    }
    
    private void buildToolbar() 
    {
        if ( dirty ) toolbarComponent.removeAll();
        
        ExpressionResolver expResolver = ExpressionResolver.getInstance();
        for (XButton btn: buttons) 
        {
            String expression = (String) btn.getClientProperty("visibleWhen");
            if (!ValueUtil.isEmpty(expression)) 
            {
                boolean result = UIControlUtil.evaluateExprBoolean(binding.getBean(), expression);
                btn.setVisible(result);
            } 
            else 
            {
                if ( btn.getClientProperty("default.button") != null ) 
                {
                    if ( getRootPane() != null )
                        getRootPane().setDefaultButton( btn );
                    else
                        binding.setDefaultButton( btn );
                }
            }
            
            if ( dirty ) toolbarComponent.add(btn);
        }
        
        SwingUtilities.updateComponentTreeUI(this);
        dirty = false;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public String getTextAlignment() { return this.textAlignment; } 
    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }
    
    public String getTextPosition() { return this.textPosition; } 
    public void setTextPosition(String textPosition) {
        this.textPosition = textPosition;
    }
    
    public boolean isShowCaptions() { return showCaptions; }
    public void setShowCaptions(boolean showCaptions) { this.showCaptions = showCaptions; }
    
    public Insets getPadding() { return padding; }
    public void setPadding(Insets padding) { this.padding = padding; }
    
    public int getSpacing() { return spacing; }
    public void setSpacing(int spacing) { this.spacing = spacing; }
    
    public List<? extends UIControl> getControls() { return buttons; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public boolean isUseToolBar() { return useToolBar; }
    public void setUseToolBar(boolean useToolBar) {
        this.useToolBar = useToolBar;
        
        super.removeAll();
        if (useToolBar) 
        {
            JToolBar tlb = new JToolBar();
            tlb.setFocusable(false); 
            tlb.setFloatable(false);
            tlb.setRollover(true);
            toolbarComponent = tlb;
        } 
        else {
            toolbarComponent = new JPanel();
        }
                
        toolbarComponent.setLayout(toolbarLayout); 
        toolbarComponent.setName("toolbar");
        toolbarComponent.setOpaque(false);
        add(toolbarComponent);
        
        if (Beans.isDesignTime())
            toolbarComponent.add(buttonTpl);
    }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public int getHorizontalAlignment() { return 0; }
    public void setHorizontalAlignment(int horizontalAlignment) {}
    
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    
    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) 
    {
        if ( orientation != null )
            this.orientation = orientation.toUpperCase();
        else
            this.orientation = UIConstants.HORIZONTAL;
        
        this.toolbarLayout.setOrientation(this.orientation); 
    }
    
    public String getOrientationHAlignment() { return orientationHAlignment; }
    public void setOrientationHAlignment(String alignment) {
        if ( alignment != null )
            this.orientationHAlignment = alignment.toUpperCase();
        else
            this.orientationHAlignment = UIConstants.LEFT;
        
        this.toolbarLayout.setAlignment(this.orientationHAlignment); 
    }
    
    public String getOrientationVAlignment() { return orientationVAlignment; }
    public void setOrientationVAlignment(String alignment) {
        if ( alignment != null )
            this.orientationVAlignment = alignment.toUpperCase();
        else
            this.orientationVAlignment = UIConstants.TOP;
        
        this.toolbarLayout.setAlignment(this.orientationVAlignment); 
    }
    
    public boolean focusFirstInput() {
        return false;
    }
    
    //button template support
    public Font getButtonFont()       { return buttonTpl.getFont(); }
    public void setButtonFont(Font f) { buttonTpl.setFont(f); }
    
    public boolean getButtonBorderPainted()       { return buttonTpl.isBorderPainted(); }
    public void setButtonBorderPainted(boolean b) { buttonTpl.setBorderPainted(b); }
    
    public boolean getButtonContentAreaFilled()       { return buttonTpl.isContentAreaFilled(); }
    public void setButtonContentAreaFilled(boolean b) { buttonTpl.setContentAreaFilled(b); }
    
    public Dimension getButtonPreferredSize()       { return buttonTpl.getPreferredSize(); }
    public void setButtonPreferredSize(Dimension d) { buttonTpl.setPreferredSize(d); }
    
    public int getButtonCaptionOrientation() { return this.buttonCaptionOrientation; }
    public void setButtonCaptionOrientation(int orientation) {
        if( orientation == SwingConstants.TOP || orientation == SwingConstants.BOTTOM ) {
            buttonTpl.setVerticalTextPosition(orientation);
            buttonTpl.setHorizontalTextPosition(SwingConstants.CENTER);
        } else {
            buttonTpl.setVerticalTextPosition(SwingConstants.CENTER);
            buttonTpl.setHorizontalTextPosition(orientation);
        }
        this.buttonCaptionOrientation = orientation;
    }
    
    public boolean isButtonTextInHtml() {
        return buttonTextInHtml;
    }
    
    public void setButtonTextInHtml(boolean buttonTextInHtml) {
        this.buttonTextInHtml = buttonTextInHtml;
    }
    
    public Color getButtonForeground()       { return buttonTpl.getForeground(); }
    public void setButtonForeground(Color f) { buttonTpl.setForeground(f); }
    
    public boolean isButtonAsHyperlink()                        { return buttonAsHyperlink; }
    public void setButtonAsHyperlink(boolean buttonAsHyperlink) { this.buttonAsHyperlink = buttonAsHyperlink; }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout (Class) ">
    
    private class ContainerLayout implements LayoutManager 
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        public Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int w=0, h=0;
                if (toolbarComponent == null) {
                    //do nothing
                }
                else if (toolbarComponent.getComponents().length > 0) 
                {
                    Insets margin = parent.getInsets();
                    Dimension dim = toolbarComponent.getPreferredSize();
                    w = (margin.left + dim.width + margin.right);
                    h = (margin.top + dim.height + margin.bottom);

                    Insets pads = getPadding();
                    if (pads != null) 
                    {
                        w += (pads.left + pads.right);
                        h += (pads.top + pads.bottom);
                    }
                }
                return new Dimension(w, h);
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                if (toolbarComponent != null) {
                    Insets margin = parent.getInsets();
                    int x = margin.left;
                    int y = margin.top;
                    int w = parent.getWidth() - (margin.left + margin.right);
                    int h = parent.getHeight() - (margin.top + margin.bottom);
                    
                    Insets pads = getPadding();
                    if (pads != null) {
                        x += pads.left;
                        y += pads.top;
                        w -= (pads.left + pads.right);
                        h -= (pads.top + pads.bottom);
                    }
                    
                    toolbarComponent.setBounds(x, y, w, h);
                }
            }
        }
    }
    
    //</editor-fold>
                
}
