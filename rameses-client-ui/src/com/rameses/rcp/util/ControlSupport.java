package com.rameses.rcp.util;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.Opener;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.framework.*;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.ui.UIControl;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;



public final class ControlSupport {
    
    
    public static void setStyles(Map props, Component component) {
        PropertyResolver resolver = PropertyResolver.getInstance();
        for (Object o : props.entrySet()) {
            Map.Entry me = (Map.Entry)o;
            if (me.getKey() == null) continue;
            
            String key = me.getKey().toString();
            try 
            {
                if ("background".equals(key) || "background-color".equals(key)) {
                    Color color = ColorUtil.decode(me.getValue()+"");
                    if (color != null) component.setBackground(color);
                }
                else if ("foreground".equals(key) || "color".equals(key)) {
                    Color color = ColorUtil.decode(me.getValue()+"");
                    if (color != null) component.setForeground(color);
                }
                else if ("font".equals(key)) {
                    Font oldFont = component.getFont();
                    component.setFont(oldFont.decode(me.getValue().toString())); 
                }
                else if ("font-family".equals(key)) {
                    String sval = me.getValue().toString();
                    Map attrs = new HashMap(); 
                    attrs.put(TextAttribute.FAMILY, sval);
                    Font oldFont = component.getFont();
                    component.setFont(oldFont.deriveFont(attrs)); 
                } 
                else if ("font-style".equals(key)) {
                    String sval = me.getValue().toString();
                    Map attrs = new HashMap(); 
                    if ("normal".equalsIgnoreCase(sval) || "regular".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
                    else if ("bold".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    else if ("demibold".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD);
                    else if ("demilight".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT);
                    else if ("extrabold".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
                    else if ("extralight".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT);
                    else if ("heavy".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY);
                    else if ("light".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT);
                    else if ("medium".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM);
                    else if ("semibold".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
                    else if ("ultrabold".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
                    else if ("ultrabold".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD);
                    else if ("italic".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
                    else if ("oblique".equalsIgnoreCase(sval))
                        attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
                    
                    if (!attrs.isEmpty()) component.setFont(component.getFont().deriveFont(attrs)); 
                } 
                else if ("font-weight".equals(key)) {
                    float weight = Float.parseFloat(me.getValue().toString()); 
                    Map attrs = new HashMap(); 
                    attrs.put(TextAttribute.WEIGHT, weight);
                    component.setFont(component.getFont().deriveFont(attrs)); 
                } 
                else if ("font-size".equals(key)) {
                    float size = Float.parseFloat(me.getValue().toString()); 
                    Map attrs = new HashMap(); 
                    attrs.put(TextAttribute.SIZE, size);
                    component.setFont(component.getFont().deriveFont(attrs)); 
                } 
                else if ("text-decoration".equals(key)) {
                    String sval = me.getValue().toString();
                    Map attrs = new HashMap(); 
                    if ("underline".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                    else if ("underline-dashed".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DASHED);
                    else if ("underline-dotted".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
                    else if ("underline-gray".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
                    else if ("underline-one-pixel".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                    else if ("underline-two-pixel".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
                    else if ("strikethrough".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    else if ("superscript".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
                    else if ("subscript".equalsIgnoreCase(sval)) 
                        attrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
                    
                    if (!attrs.isEmpty()) component.setFont(component.getFont().deriveFont(attrs)); 
                }                 
                else {
                    resolver.setProperty(component, key, me.getValue()); 
                }
            } 
            catch(Throwable ign) {;}   
        }
    }
    
    public static Object init(Object bean, Map params, String action ) {
        setProperties(bean, params);
        return invoke( bean, action, null );
    }
    
    public static void setProperties(Object bean, Map params ) {
        if( params != null ) {
            ClientContext ctx = ClientContext.getCurrentContext();
            PropertyResolver resolver = PropertyResolver.getInstance();
            for( Object oo : params.entrySet()) {
                Map.Entry me = (Map.Entry)oo;
                try {
                    resolver.setProperty(bean, me.getKey()+"", me.getValue() );
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static Object invoke(Object bean, String action, Object[] params  ) 
    {
        ClientContext ctx = ClientContext.getCurrentContext();
        //fire actions
        if ( action != null && action.trim().length() > 0) 
        {
            try {
                return MethodResolver.getInstance().invoke(bean,action,null,params);
            } catch (RuntimeException re) {
                throw re;
            } catch(Exception ex) {
                throw new IllegalStateException(ex.getMessage(), ex);
            }
        }
        return null;
    }
    
    public static void fireNavigation(UIControl source, Object outcome) {
        NavigationHandler nh = ClientContext.getCurrentContext().getNavigationHandler();
        NavigatablePanel navPanel = UIControlUtil.getParentPanel((JComponent)source, null);
        nh.navigate(navPanel, source, outcome);
    }
    
    public static boolean isResourceExist( String name ) {
        try {
            InputStream is = ClientContext.getCurrentContext().getResourceProvider().getResource(name);
            return (is != null); 
        } catch(Throwable t) {
            return false; 
        }
    }
    
    public static byte[] getByteFromResource( String name ) {
        if(name==null || name.trim().length()==0)
            return null;
        ByteArrayOutputStream bos = null;
        InputStream is = ClientContext.getCurrentContext().getResourceProvider().getResource(name);
        if( is != null ) {
            try {
                bos =  new ByteArrayOutputStream();
                int i = 0;
                while((i=is.read())!=-1) {
                    bos.write(i);
                }
                return bos.toByteArray();
            } catch(Exception ex) {
                return null;
            } finally {
                try { bos.close(); } catch(Exception ign){;}
                try { is.close(); } catch(Exception ign){;}
            }
        } else {
            return  null;
        }
    }
    
    public static ImageIcon getImageIcon(String name) {
        byte[] b = ControlSupport.getByteFromResource(name);
        if (b != null) {
            return new ImageIcon( b );
        } else {
            return null;
        }
    }
    
    public static Opener initOpener( Opener opener, UIController caller ) {
        return initOpener(opener, caller, true);
    }
    
    public static Opener initOpener( Opener opener, UIController caller, boolean invokeOpenerAction ) 
    {
        Object invoker = opener.getProperties().get("_INVOKER_");        
        if ( caller != null && ValueUtil.isEmpty(opener.getName()) ) 
        {
            opener.setController( caller );
            if ( opener.getCaption() != null )
                caller.setTitle( opener.getCaption() );
            if ( opener.getId() != null )
                caller.setId( opener.getId() );
            
        } 
        else if ( opener.getController() == null ) 
        {
            ControllerProvider provider = ClientContext.getCurrentContext().getControllerProvider();
            UIController controller = provider.getController(opener.getName(), caller);
            controller.setId( opener.getId() );
            controller.setName( opener.getName() );
            controller.setTitle( opener.getCaption() );

            Object callee = controller.getCodeBean();
            if ( caller != null ) {
                injectCaller( callee, callee.getClass(), caller.getCodeBean());
            }
            
            if (invoker != null) {
                injectInvoker(callee, callee.getClass(), invoker); 
            }
            
            opener.setController( controller );
            
            if ( invokeOpenerAction ) 
            {
                Object[] actionParams = new Object[]{};
                if (invoker != null) actionParams = new Object[]{ invoker };

                Object o = opener.getController().init(opener.getParams(), opener.getAction(), actionParams);
                if ( o == null ) {;} 
                else if ( o instanceof String ) {
                    opener.setOutcome( (String)o );
                } 
                //if the opener action returns another opener,
                //then intialize the opener and return it
                else if ( o instanceof Opener ) 
                {
                    Opener oo = (Opener) o;
                    opener = initOpener(oo, oo.getController(), invokeOpenerAction);
                }
            }            
        }
        
        UIController controller = opener.getController();
        if( controller.getTitle() == null ) {
            controller.setTitle( controller.getName() );
        }
        
        return opener;
    }
    
    public static void injectCaller( Object callee, Class clazz, Object caller ) {
        //if caller is the same as calle do not proceed
        //for cases for subforms having the same controller.
        if( callee!=null && callee.equals(caller)) return;
        
        //inject the caller here..
        for(Field f: clazz.getDeclaredFields()) {
            if( f.isAnnotationPresent(com.rameses.rcp.annotations.Caller.class)) {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                try { f.set(callee, caller); } catch(Exception ign){;}
                f.setAccessible(accessible);
                break;
            }
        }
        Class superClass = clazz.getSuperclass();
        if(superClass!=null) {
            injectCaller( callee, superClass, caller );
        }
    }
    
    public static void injectInvoker( Object object, Class clazz, Object invoker ) {
         for(Field f: clazz.getDeclaredFields()) {
            if( f.isAnnotationPresent(com.rameses.rcp.annotations.Invoker.class)) {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                try { f.set(object, invoker); } catch(Exception ign){;}
                f.setAccessible(accessible);
                break;
            }
        }
        Class superClass = clazz.getSuperclass();
        if(superClass!=null) {
            injectInvoker( object, superClass, invoker );
        }
    }
    
    public static boolean isPermitted(String domain, String role, String permission ) {
        //check if not permitted, block this
        if(permission!=null && permission.trim().length()>0) {
            ClientContext ctx = ClientContext.getCurrentContext();
            if( ctx.getSecurityProvider()==null ) {
                return  true;
            }
            return ctx.getSecurityProvider().checkPermission(domain, role, permission);
        } else {
            return true;
        }
    }
    
}
