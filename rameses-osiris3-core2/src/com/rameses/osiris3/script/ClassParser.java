/*
 * ClassParser.java
 *
 * Created on January 19, 2013, 8:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.Async;
import com.rameses.annotations.ProxyMethod;
import java.lang.reflect.Method;

/**
 *
 * @author Elmo
 */
public class ClassParser {
    
      public static void parse( Class sc, Handler handler ) {
        boolean proxyMethodExist = false;
        handler.start(sc);
        
        for( Method m : sc.getDeclaredMethods() ) {
            if( m.isAnnotationPresent(ProxyMethod.class)) {
                boolean isAsync = m.isAnnotationPresent(Async.class);
                handler.handleMethod(  m.getName(), m.getParameterTypes(), m.getReturnType(), isAsync  );
            }
        }
        handler.end();
    }
    
      
    public static interface Handler {
        void start(Class c);
        void handleMethod(String methodName, Class[] paramTypes, Class returnType, boolean async);
        void end();        
    }  
    
}
