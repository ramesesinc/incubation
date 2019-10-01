/*
 * ContentTemplateCache.java
 *
 * Created on March 17, 2013, 7:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import groovy.text.Template;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ContentTemplateCache {
    
    private Map<String, ContentTemplate> cache = new HashMap();
    
    public void clear() {
        cache.clear(); 
    }
    
    public ContentTemplate getTemplate(String name, ContentTemplateSource src ) throws ResourceNotFoundException {
        String n = src.getType()+":"+name;
        InputStream is = null;
        try {
            if ( !cache.containsKey(n)) {
                AnubisContext actx = AnubisContext.getCurrentContext();
                boolean cached = actx.getProject().isCached();
                
                is = src.getResource( name );
                Template temp = TemplateParser.getInstance().parse(is);
                ContentTemplate ct = new ContentTemplate(temp);
                if ( !cached ) return ct; 

                cache.put( n, ct );
            }
            return cache.get(n);
        } 
        catch(ResourceNotFoundException rnfe) {
            throw rnfe;
        }
        catch(RuntimeException re) {
            throw re; 
        }
        catch(Throwable t) {
            throw new RuntimeException(t);
        }
        finally {
            try { is.close(); } catch(Throwable ign){;}
        }
    }
    
}
