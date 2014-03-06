/*
 * FileManager.java
 *
 * Created on March 24, 2013, 5:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.anubis;

import com.rameses.io.StreamUtil;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class FileManager {
    
    private Project project;
    private Map<String, File> files = new Hashtable();
    
    
    /**
     * Creates a new instance of FileManager
     */
    public FileManager(Project project) {
        this.project = project;
    }
    
    private InputStream findSource(String name, String moduleName)  {
        try {
            if( moduleName != null) {
                Module module = project.getModules().get(moduleName);
                List<String> urls = new ArrayList();
                urls.add( ContentUtil.correctUrlPath(module.getUrl(), "files", name) );
                if(module.getProvider()!=null) {
                    urls.add( ContentUtil.correctUrlPath(module.getProvider(), "files", name) );
                }
                String[] strs = (String[])urls.toArray(new String[]{});
                return ContentUtil.getResources( strs, name );
            } else {
                AnubisContext ctx = AnubisContext.getCurrentContext();
                String[] urls = new String[] {
                    ContentUtil.correctUrlPath(project.getUrl(), "files", name) ,
                    ContentUtil.correctUrlPath(ctx.getSystemUrl(), "files", name) 
                };
                return ContentUtil.getResources(urls,name);
            }
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        //locate all sources until you find the file source
        
    }
    
    public File getFile(String name) 
    {
        if( !files.containsKey(name) ) {
            //check first if file is requested from module or from the project
            
            String moduleName = null;
            String fileName = name;
            String[] arr = ProjectUtils.getModuleNameFromFile( name, project );
            if(arr!=null) {
                moduleName = arr[0];
                fileName = arr[1];
            }
            
            Map map = JsonUtil.toMap( StreamUtil.toString( findSource(fileName, moduleName) ));
            
            map.put("id", name);
            map.put( "ext", name.substring(name.lastIndexOf(".")+1));
            
            //check if file has items. This is done by checking if folders exist.
            //calculate the parent path
            String parentPath = name.substring(0, name.lastIndexOf("/"));
            if(parentPath==null || parentPath.trim().length()==0) parentPath = "/";
            map.put( "parentPath", parentPath );
            if( moduleName !=null ) {
                map.put("module", moduleName);
            }
            
            //check also if page is secured
            //adjust sort order
            if(!map.containsKey("sortorder")) {
                map.put("sortorder", 0);
            }
        
            //set path
            String path = name.substring(0, name.lastIndexOf("."));
            map.put("path", path);

            //set secured
            if(!map.containsKey("secured")) {
                boolean secured = false;
                if(project.getSecuredPages()!=null && path.matches(project.getSecuredPages())) {
                    secured = true;
                }
                map.put("secured", secured);
            }
            
            if( !map.containsKey("version") ) {
                map.put("version", "1.0");
            }
        
            if(!map.containsKey("name")) {
                map.put("name", name.substring(1,name.lastIndexOf(".")).replace("/", "-"));
            }            
            
            if(!map.containsKey("hidden")) {
                map.put("hidden", false );
            }
            
            if(!map.containsKey("fragment")) {
                map.put("fragment",false);
            }
            
            map.put("pagename", name.substring( name.lastIndexOf("/")+1, name.lastIndexOf(".")) );
            
            if(!map.containsKey("hashid")) {
                String hname = (String)map.get("name");
                map.put("hashid", hname);
            }
            files.put( name, new File(map) );
        }
        return files.get(name);
    }
    
    public void clear() {
        files.clear();
    }
}
