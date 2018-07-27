/*
 * PageContentProvider.java
 *
 * Created on March 15, 2013, 8:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class PageContentProvider extends ContentProvider {
    
    private PageContentCacheSource contentSource = new PageContentCacheSource();
    
    
    public String getExt() {
        return "pg";
    }
    
    public InputStream getContent(File file, Map params)  throws ResourceNotFoundException{
        AnubisContext ctx = AnubisContext.getCurrentContext();
        Project project = ctx.getProject();
        Module mod = ctx.getModule();
        
        Page page = new Page(file);
        ctx.setCurrentPage( page );
        ContentMap pmap = new ContentMap();
        if ( params != null ) pmap.putAll( params ); 
        
        String result  = "";        
        try {
            ContentTemplate ct = project.getTemplateCache().getTemplate( file.getPath(), contentSource );
            result = ct.render( pmap  );
            pmap.put("content", result );
            result = project.getTemplateManager().applyTemplates( file, pmap );
            return new ByteArrayInputStream(result.getBytes());
        } catch(ResourceNotFoundException rnfe) {
            throw rnfe;
        }
    }
    
    
    
    
    //SOURCE OF THE CONTENT
    private class PageContentCacheSource extends ContentTemplateSource {
        public String getType() {
            return "content";
        }
        public InputStream getResource(String name) throws ResourceNotFoundException{
            AnubisContext ctx = AnubisContext.getCurrentContext();
            ArrayList<String> paths = new ArrayList(); 
            if ( ctx.getModule() != null ) { 
                String[] arr = ProjectUtils.getModuleNameFromFile( name, ctx.getProject() );
                Module module = ctx.getModule(); 
                name = arr[1];                
                paths.add( module.getUrl()+"files"+name+".pg/content"); 
                paths.add( module.getUrl()+"content"+name+"/content"); 
                if ( module.getProvider() != null ) {
                    paths.add( module.getProvider()+"files"+name+".pg/content"); 
                    paths.add( module.getProvider()+"content"+name+"/content"); 
                }
            } else {
                paths.add( ctx.getProject().getUrl()+"/files/"+name+".pg/content"); 
                paths.add( ctx.getSystemUrl()+"/files/"+name+".pg/content"); 
                paths.add( ctx.getProject().getUrl()+"/content/"+name+"/content"); 
                paths.add( ctx.getSystemUrl()+"/content/"+name+"/content"); 
            }
            
            return ContentUtil.getResources( paths.toArray(new String[]{}), name);
        }
    }
    
    
    
}
