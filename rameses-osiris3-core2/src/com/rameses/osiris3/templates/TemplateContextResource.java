/*
 * TemplateContextResource.java
 *
 * Created on February 7, 2013, 8:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.templates;

import com.rameses.osiris3.core.ContextResource;
import com.rameses.util.Service;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class TemplateContextResource extends ContextResource {
    
    private Map<String, TemplateBuilder> builders = Collections.synchronizedMap(new HashMap());
    
    public void init() {
        Iterator<TemplateBuilder> iter = Service.providers(TemplateBuilder.class, server.getClass().getClassLoader());
        while(iter.hasNext()) {
            TemplateBuilder tb = iter.next();
            builders.put( tb.getExtension(), tb );
        }
    }
    
    
    public Class getResourceClass() {
        return Template.class;
    }
    
    protected Template findResource(String key) {
        //get the extension first
        InputStream is = null;
        try {
            String ext = key.substring( key.lastIndexOf(".")+1);
            TemplateBuilder tb = builders.get(ext);
            if(tb==null)
                throw new Exception("There is no template builder for  " + ext);
            
            is = context.getClassLoader().getResourceAsStream( "templates/" + key );
            if(is==null)
                throw new Exception("Template " + key + "not found in templates");
            return tb.build( is );
            
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {is.close();} catch(Exception ign){;}
        }
    }

    
    
}
