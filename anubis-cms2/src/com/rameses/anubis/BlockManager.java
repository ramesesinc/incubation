/*
 * BlockManager.java
 *
 * Created on March 19, 2013, 5:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import com.rameses.util.ConfigProperties;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class BlockManager {
    
    private List<MappingEntry> mappings = new ArrayList();
    private BlockCacheSource blockSource = new BlockCacheSource();
    private GlobalBlockCacheSource globalBlockSource = new GlobalBlockCacheSource();
    
    public void init(ConfigProperties conf) {
        Map map = conf.getProperties( "block-mapping" );
        if(map!=null) {
            //load master template mapping
            for(Object o: map.entrySet()) {
                Map.Entry me = (Map.Entry)o;
                mappings.add( new MappingEntry(me.getKey()+"", me.getValue()+"") );
            }
        }
    }
    
    public String getBlockContent(String name) throws Exception {
        AnubisContext ctx = AnubisContext.getCurrentContext();
        Project project = ctx.getProject();
        File file = ctx.getCurrentPage().getFile();
        String blockname = file.getPath() + "/" + name;
        ContentTemplate ct = null;
        ContentMap m = new ContentMap();
        try {
            ct = project.getTemplateCache().getTemplate(blockname, blockSource);
            return ct.render( m );
        } catch (ResourceNotFoundException ex) {} catch( Exception e) {
            throw e;
        }
        
        //check the mappings
        for( MappingEntry me: mappings) {
            if(me.matches(blockname)) {
                try {
                    String blockName = me.getTemplates()[0];
                    ct = project.getTemplateCache().getTemplate(blockName, globalBlockSource );
                    return ct.render( m );
                } catch(ResourceNotFoundException rnfe){;} catch(Exception e) {
                    throw e;
                }
            }
        }
        
        //if not found in mapping, we find in global blocks
        try {
            ct = project.getTemplateCache().getTemplate(name, globalBlockSource );
            return ct.render( m );
        } catch(ResourceNotFoundException rnfe){;} catch(Exception e) {
            throw e;
        }
        return "";
    }
    
    private class BlockCacheSource extends ContentTemplateSource {
        public String getType() {
            return "blocks";
        }
        public InputStream getResource(String name) throws ResourceNotFoundException{
            AnubisContext ctx = AnubisContext.getCurrentContext();
            List<String> list = new ArrayList();
            if(ctx.getModule()!=null) {
                Module module = ctx.getModule();
                String spath = name = ProjectUtils.correctModuleFilePath(name, module );
                list.add( module.getUrl()+"content"+spath );
                list.add( module.getProvider()+"content"+spath );
            }
            list.add( ctx.getProject().getUrl()+"/content/"+name );
            list.add( ctx.getSystemUrl()+"/content/"+name );
            return ContentUtil.getResources( (String[]) list.toArray(new String[]{}), name );
        }
    }
    
    private class GlobalBlockCacheSource extends ContentTemplateSource {
        public String getType() {
            return "global-blocks";
        }
        public InputStream getResource(String name) throws ResourceNotFoundException {
            AnubisContext ctx = AnubisContext.getCurrentContext();
            List<String> list = new ArrayList();
            if(ctx.getModule()!=null) {
                Module module = ctx.getModule();
                list.add( module.getUrl()+"blocks/"+name );
                list.add( module.getProvider()+"blocks/"+name );
            }
            list.add( ctx.getProject().getUrl()+"/blocks/"+name );
            list.add( ctx.getSystemUrl()+"/blocks/"+name );
            return ContentUtil.getResources( (String[]) list.toArray(new String[]{}), name );
        }
    }
}
