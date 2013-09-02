/*
 * ActiveDBService.java
 *
 * Created on August 30, 2013, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.activedb;

import com.rameses.osiris3.core.ContextService;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 *
 * @author Elmo
 * we need to register so it can be instantiated only once and we dont have to
 * resort to static classes
 */
public class ActiveDBService extends ContextService {
    
    private Class metaClass;
    private GroovyClassLoader classLoader;
    
    public Class getProviderClass() {
        return ActiveDBService.class;
    }
   
    public void start() throws Exception {
        classLoader = new GroovyClassLoader(context.getClassLoader());
        StringBuilder builder = new StringBuilder();
        builder.append( "public class ActiveDBClass  { \n" );
        builder.append( "    def invoker; \n");
        builder.append( "    public Object invokeMethod(String string, Object args) { \n");
        builder.append( "        return invoker.invokeMethod(string, args); \n" );
        builder.append( "    } \n");
        builder.append(" } ");
        metaClass = classLoader.parseClass( builder.toString() );
    }

    public void stop() throws Exception {
    }

    public Object create(ActiveDBInvoker dbi) {
        try {
            //search first from pool
            Object obj =  metaClass.newInstance();
            ((GroovyObject)obj).setProperty( "invoker", dbi );
            return obj;
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
}