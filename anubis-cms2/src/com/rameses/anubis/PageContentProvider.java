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
            if(ctx.getModule()!=null) {
                final Module module = ctx.getModule();
                String[] arr = ProjectUtils.getModuleNameFromFile( name, ctx.getProject() );
                name = arr[1];
                return ContentUtil.getResources( new String[]{
                    module.getUrl()+"content"+name+"/content",
                    module.getProvider()+"content"+name+"/content"
                },name);
            } else {
                return ContentUtil.getResources(new String[]{
                    ctx.getProject().getUrl()+"/content/"+name+"/content",
                    ctx.getSystemUrl()+"/content/"+name+"/content"
                },name);
            }
        }
    }
    
    
    
}
