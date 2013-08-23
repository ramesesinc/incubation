/*
 * Service.java
 *
 * Created on April 11, 2013, 10:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package osiris3.groovy.tester;

/**
 *
 * @author Elmo
 */


import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.service.ServiceProxyInvocationHandler;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class Service  {
    
    private GroovyClassLoader classLoader = new GroovyClassLoader(getClass().getClassLoader());
    private Map env;
    
    public Service(Map env) {
        this.env = env;
    }
    
    
    private interface ScriptInfoInf  {
        String getStringInterface();
    }
    
    public Object lookup(String name) {
        return lookup(name, null, new HashMap());
    }
    
    public Object lookup(String name, Class localInterface ) {
        return lookup(name, localInterface, new HashMap());
    }
    
    public Object lookup(String name, Class localInterface, Map genv) {
        try {
            ScriptServiceContext ect = new ScriptServiceContext(env);
            if(localInterface != null) {
                return ect.create( name, localInterface );
            } else {
                ScriptInfoInf si = ect.create( name,  ScriptInfoInf.class  );
                Class clz = classLoader.parseClass( si.getStringInterface() );
                
                Map map = new HashMap();
                map.putAll( genv );
                ServiceProxy sp = ect.create( name, map );
                
                return Proxy.newProxyInstance( classLoader, new Class[]{clz}, new ServiceProxyInvocationHandler(sp)  );
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    
    
}
