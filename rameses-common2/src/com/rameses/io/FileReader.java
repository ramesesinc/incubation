/*
 * FileReader.java
 *
 * Created on April 11, 2014, 11:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.io;

import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Encoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class FileReader 
{
    private ScriptServiceContext serviceContext;
    
    private Map conf;
    private Map env;
    private boolean cache;
    
    private OutputListener outputListener;
    
    public FileReader() {
        this(null);
    }
    
    public FileReader(Map conf) {
        this.conf = conf;
        this.cache = true; 
    }    

    public void setConf(Map conf) {
        this.conf = conf;
    }
    
    public void setEnv(Map env) {
        this.env = (env == null? new HashMap(): env); 
    }
    
    public boolean isCache() { return cache; } 
    public void setCache(boolean cache) {
        this.cache = cache;
    }
    
    public OutputListener getOutputListener() { return outputListener; } 
    public void setOutputListener(OutputListener outputListener) {
        this.outputListener = outputListener; 
    }
    
    public byte[] read(String id) {
        try { 
            ResourceObject res = new ResourceObject(id);
            new CleanupTask(res.getBaseFolder()).start();
            return res.getData(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    public void remove(String id) {
        try { 
            String serviceName = getPropertyString("serviceName");
            if (serviceName == null) serviceName = "FileService"; 
            if (env == null) env = new HashMap();
                        
            ServiceProxy proxy = getServiceContext().create(serviceName, env);
            
            Map params = new HashMap(); 
            params.put("objid", id); 
            proxy.invoke("removeFile", new Object[]{ params }); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private Object getProperty(String name) {
        return (conf == null? null: conf.get(name)); 
    }
    
    private String getPropertyString(String name) {
        Object value = getProperty(name);
        return (value == null? null: value.toString()); 
    }
    
    private ScriptServiceContext getServiceContext() {
        if (serviceContext == null) {
            if (conf == null) throw new RuntimeException("Please specify a conf for the service context");
            
            serviceContext = new ScriptServiceContext(conf); 
        }
        return serviceContext; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ResourceObject "> 
    
    private class ResourceObject 
    {
        private String id;
        private String encid;
        
        ResourceObject(String id) {
            this.id = id;
            this.encid = Encoder.MD5.encode(id); 
        }
        
        byte[] getData() throws Exception {
            Map params = new HashMap();
            params.put("objid", id);
            
            File folder = getBaseFolder(); 
            File fhead = new File(folder, "header"); 
            if (!fhead.exists()) downloadData(fhead, "getHeader", params);
            /*
            if (!isCache()) {
                byte[] bytes = downloadData(fhead, "getHeader", params);
                header = (Map) readObject(bytes); 
            } else if (!fhead.exists()) {
                downloadData(fhead, "getHeader", params);
            }*/
            
            Map header = (Map) read(fhead); 
            if (header == null) return null; 
            
            MapWrapper map = new MapWrapper(header); 
            Integer chunkcount = map.getInteger("chunkcount");
            if (chunkcount == null) return null; 
            
            params.put("context", map.getString("context"));
            
            OutputListener outputListener = getOutputListener();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(32*1024);
            int count = chunkcount.intValue();
            for (int i=0; i<count; i++) {
                int indexno = i+1;
                File fitem = new File(folder, "item-"+indexno); 
                if (!fitem.exists()) {
                    params.put("indexno", indexno);
                    downloadData(fitem, "getDetail", params);
                }
                
                Object oitem = read(fitem);
                if (oitem instanceof Map) {
                    oitem = ((Map)oitem).get("content"); 
                }
                if (oitem instanceof byte[]) {
                    byte[] itembytes = (byte[]) oitem; 
                    if (outputListener == null) { 
                        baos.write(itembytes); 
                    } else {
                        outputListener.write(itembytes); 
                    }
                }
            }
            try { 
                return baos.toByteArray(); 
            } finally {
                try { baos.close(); }catch(Throwable t){;} 
            }
        }
        
        Object read(File file) throws Exception {
            byte[] bytes = IOStream.toByteArray(file);
            if (bytes == null || bytes.length == 0) return null;
            
            return new Base64Cipher().decode(new String(bytes));
        }
                
        private File getBaseFolder() {
            String tempdir = getPropertyString("tempdir");
            if (tempdir == null || tempdir.length() == 0) {
                tempdir = System.getProperty("java.io.tmpdir");
            }
            File ftempdir = new File(tempdir);
            File basedir = new File(ftempdir, "rameses");
            if (!basedir.exists()) basedir.mkdir();
            
            File maindir = new File(basedir, encid);
            if (!maindir.exists()) maindir.mkdir(); 
            
            return maindir; 
        }
        
        byte[] downloadData(File file, String action, Map params) throws Exception {
            String serviceName = getPropertyString("serviceName");
            if (serviceName == null) serviceName = "FileService"; 
            if (env == null) env = new HashMap();
            ServiceProxy proxy = getServiceContext().create(serviceName, env);

            Map data = (Map) proxy.invoke(action, new Object[]{ params }); 
            if (data == null) data = new HashMap();
            
            byte[] data_bytes = IOStream.toByteArray(data);
            if (data_bytes == null) data_bytes = new byte[0]; 
            
            String encstr = new Base64Cipher().encode(data_bytes); 
            if (isCache()) new IOStream().write(encstr.getBytes(), file); 
            
            return data_bytes; 
        }
        
        Object readObject(byte[] bytes) {
            ByteArrayInputStream bais = null; 
            ObjectInputStream ois = null;
            try {
                bais = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bais);
                return ois.readObject(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { ois.close(); }catch(Throwable t){;} 
                try { bais.close(); }catch(Throwable t){;} 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MapWrapper "> 
    
    private class MapWrapper 
    {
        private Map data;
        
        MapWrapper(Map data) {
            this.data = data; 
        }
        
        Object get(String name) {
            return (data == null? null: data.get(name)); 
        }
        String getString(String name) {
            Object value = get(name); 
            return (value == null? null: value.toString()); 
        }
        Integer getInteger(String name) {
            try {
                Object value = get(name);
                if (value instanceof Integer) {
                    return (Integer) value;
                }
                
                int num = Integer.parseInt(value.toString()); 
                return new Integer(num); 
            } catch(Throwable t) {
                return null; 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" OutputListener "> 
    
    public static interface OutputListener 
    {
        void write(byte[] bytes); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CleanupTask "> 
    
    private class CleanupTask 
    {
        private File basedir; 
        
        CleanupTask(File dir) {
            this.basedir = (dir == null? null: dir.getParentFile());
        }
        
        void start() {
            new Thread(new Runnable() {
                public void run() {
                    removeObsoleteFiles();
                }
            }).start();
        }
        
        void removeObsoleteFiles() {
            if (basedir == null) return;
            
            Calendar cal = Calendar.getInstance();
            File[] files = basedir.listFiles();
            for (File f : files) { 
                cal.setTimeInMillis(f.lastModified()); 
                cal.add(Calendar.HOUR, 24); 
                if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                    delete(f);
                }
            }
        }
        
        void delete(File f) { 
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (File o : files) delete(o); 
            } 
            f.delete(); 
        } 
    }
    
    // </editor-fold>
}
