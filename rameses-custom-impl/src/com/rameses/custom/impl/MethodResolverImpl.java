package com.rameses.custom.impl;


import com.rameses.common.MethodResolver;
import com.rameses.common.PropertyResolver;
import groovy.lang.GroovyObject;
import org.apache.commons.beanutils.MethodUtils;


/**
 *
 * @author jaycverg
 */
public class MethodResolverImpl extends MethodResolver {
        
    public Object invoke(Object bean, String action, Class[] paramTypes, Object[] args) throws Exception {
        String xaction = action;
        Object xbean = bean;
        if( xaction.indexOf(".")>0) {
            xaction = action.substring(action.lastIndexOf(".")+1);
            String p = action.substring(0, action.lastIndexOf("."));
            PropertyResolver resolver = PropertyResolver.getInstance();
            xbean = resolver.getProperty(bean, p);
        }
        
        if(xbean instanceof GroovyObject) {
            return ((GroovyObject)xbean).invokeMethod( xaction, args );
        }
        
        if( paramTypes == null )
            return MethodUtils.invokeMethod(xbean, xaction, args);
        else
            return MethodUtils.invokeMethod(xbean,xaction,args,paramTypes );
    }

    public Object invoke(Object bean, String action, Object[] args) throws Exception {
        return invoke( bean, action, null, args);
    }
    
}
