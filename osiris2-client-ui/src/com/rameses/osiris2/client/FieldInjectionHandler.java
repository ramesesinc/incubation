/*
 * FieldInjectionHandler.java
 *
 * Created on August 11, 2010, 2:21 PM
 * @author jaycverg
 */

package com.rameses.osiris2.client;

import com.rameses.classutils.AnnotationFieldHandler;
import com.rameses.rcp.annotations.Script;
import com.rameses.rcp.annotations.Service;
import com.rameses.rcp.framework.DependencyInjector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class FieldInjectionHandler implements AnnotationFieldHandler 
{
    private DependencyInjector injector;
    
    public FieldInjectionHandler() {
    }
    
    public Object getResource(Field f, Annotation a) throws Exception {
        if ( a.annotationType() == Service.class ) {
            Service s = (Service) f.getAnnotation(Service.class);
            String serviceName = s.value();
            String hostKey = s.host();
            if (serviceName == null || serviceName.trim().length() == 0) { 
                return InvokerProxy.getInstance();
            } else {
                String connectionName = s.connection();                
                Class intfClass = s.interfaceClass();
                if( intfClass != Object.class) {
                    return InvokerProxy.getInstance().create(serviceName, intfClass, connectionName);
                } else {
                    return InvokerProxy.getInstance().create(serviceName, null, connectionName);
                }
            }
        } else if ( a.annotationType() == Script.class ) {
            Script s = (Script) f.getAnnotation(Script.class);
            String scriptName = s.value();
            if ( scriptName == null || scriptName.trim().length() == 0 ) { 
                return ScriptProvider.getInstance(); 
            } else { 
                return ScriptProvider.getInstance().create(scriptName); 
            } 
        } else {
            try {
                Object resource = getInjector().getResource(a, null);
                if (resource != null) return resource;
            } catch(Throwable t) {
                System.out.println("ERROR injecting caused by " + t.getClass().getName() + ": " + t.getMessage() );
            } 
            return null; 
        } 
    }
    
    private DependencyInjector getInjector() {
        if (injector == null) { 
            injector = new DependencyInjector(); 
        } 
        return injector;
    }
}
