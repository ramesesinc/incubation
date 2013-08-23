/*
 * MediaContentProvider.java
 *
 * Created on March 15, 2013, 8:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class MediaContentProvider extends ContentProvider {
    
    public String getExt() {
        return "media";
    }
    
    public InputStream getContent(File file, Map params) {
        String block = "content";
        if(params.containsKey("category")) {
            block = (String) params.get("category");
        }
        
        AnubisContext ctx = AnubisContext.getCurrentContext();
        
        String path = file.getPath();
        String arr[] = path.split("\\.");
        path = arr[0] + "/" + block + "." + arr[1];
        
        String moduleName = null;
        String[] arr2 = ProjectUtils.getModuleNameFromFile(path, ctx.getProject());
        if(arr2!=null) {
            moduleName = arr2[0];
            path = arr2[1];
        }
        
        List<String> list = new ArrayList();
        if(moduleName!=null) {
            Module mod = ctx.getProject().getModules().get( moduleName );
            list.add( mod.getUrl()+"content" + path );
            list.add( mod.getProvider()+"content"+ path );
        }
        else {
            list.add( ctx.getProject().getUrl()+"/content" +path );
            list.add( ctx.getSystemUrl()+"/content" +path );
        }
        try {
            return ContentUtil.getResources( (String[]) list.toArray(new String[]{}), path );
        } catch(Exception e) {
            return null;
        }
    }
    
    
}
