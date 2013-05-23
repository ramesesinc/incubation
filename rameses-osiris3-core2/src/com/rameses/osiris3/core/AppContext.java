/*
 * AppContext.java
 *
 * Created on January 26, 2013, 8:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.io.LineReader;
import com.rameses.io.LineReader.Handler;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class AppContext extends MainContext {
    
    public AppContext(OsirisServer s, String name) {
        super(s);
        super.setName( name );
    }
    
    public SharedContext getSharedContext() {
        String sname =(String) super.getConf().get("shared");
        if(sname==null) return null;
        return server.getContext( SharedContext.class, sname );
    }
    
    public Map findProperties(String name) {
        InputStream is = null;
        try {
            is = super.getClassLoader().getResourceAsStream(name);
            if(is==null)
                is = getSharedContext().getClassLoader().getResourceAsStream(name);
            if(is==null)
                return null;
            final LinkedHashMap map = new LinkedHashMap();
            LineReader rdr = new LineReader();
            rdr.read(is, new Handler(){
                public void read(String text) {
                    int pos = text.indexOf("=");
                    String k = text.substring(0,pos).trim();
                    String v = text.substring(pos+1).trim();
                    map.put(k,v);
                }
            });
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {is.close();} catch(Exception ign){;}
        }
    }
    
}
