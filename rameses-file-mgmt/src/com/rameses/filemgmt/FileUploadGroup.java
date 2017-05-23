/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.util.Base64Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class FileUploadGroup { 
    
    private final static Object CONFIG_FILE_LOCKED = new Object();
    
    private File folder;
    private ArrayList<FileUploadItem> items; 
    
    private ConfigFile confFile;
    private String fileid;
    
    public FileUploadGroup( File file ) {
        this.folder = file; 
        this.items = new ArrayList();
    }
    
    public String getId() { return fileid; } 
    public void setId( String fileid ) {
        this.fileid = fileid; 
    }
    
    private void verifyFolder() {
        if ( !folder.exists()) { 
            folder.mkdir(); 
            
        } else if ( !folder.isDirectory()) {
            folder.delete();  
            folder.mkdir(); 
        } 
    }    
        
    public void open() {
        verifyFolder();
        confFile = new ConfigFile( folder, ".conf" ); 
        confFile.read(); 
        setId( confFile.getConf().fileid ); 
}
    
    public void create() {
        verifyFolder();
        createTempFile( ".immediate "); 
        
        GroupConf gc = new GroupConf();
        gc.fileid = getId(); 
        confFile = new ConfigFile( folder, ".conf" ); 
        confFile.create( gc ); 
    }
    
    public void addItem( FileUploadItem item ) {
        if ( item != null && !items.contains(item)) {
            items.add( item ); 
        }
    }

    public void persistItems() {
        FileUploadItem[] arr = items.toArray(new FileUploadItem[]{}); 
        for ( int i=0; i<arr.length; i++ ) {
            GroupEntry e = new GroupEntry();
//            e.fileid = arr[i].getFileId(); 
//            e.filesize = arr[i].getFileSize(); 
            e.status = "TEMP"; 
            confFile.getConf().items.add( e ); 
            confFile.getConf().itemsForCopy.add( e ); 
        } 
        confFile.update();
    }
        
    public void createTempFile( String name ) {
        FileOutputStream fos = null; 
        try {
            fos = new FileOutputStream( new File( folder, name)); 
            fos.write("".getBytes()); 
            fos.flush(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException( e.getMessage(), e ); 
        } finally {
            try { fos.close(); }catch(Throwable t){;} 
        }
    } 
    
    
    public FileUploadGroupProc createProcessHandler() {
        if ( !confFile.getConf().itemsForCopy.isEmpty()) {
            
        }
        
//        if ( getContentFile().isModeTempCopy()) {
//            return new ModeTempCopyProcess();  
//        } else if ( getContentFile().isModeUpload()) {
//            return new ModeUploadProcess(); 
//        }
        return null; 
    } 

    
    
    public class ConfigFile { 
        private File folder;
        private File idxfile; 
        private GroupConf conf; 
        private Base64Cipher base64; 
        
        ConfigFile( File folder, String name ) {
            this.folder = folder; 
            this.base64 = new Base64Cipher(); 
            this.idxfile = new File( folder, name ); 
            this.conf = new GroupConf();
        }
        
        GroupConf getConf() {
            return conf; 
        }

        Map copyData() {
            Map data = new HashMap();
            if ( this.conf != null ) {
                data.put("fileid", this.conf.fileid );
            } 
            return data; 
        }

        void create( GroupConf gc ) { 
            if ( gc == null ) return; 

            this.conf = gc; 
            update();                 
        } 
        
        public void read() { 
            synchronized ( FileUploadGroup.CONFIG_FILE_LOCKED ) {
                int read = -1;
                byte[] bytes = new byte[ 1024 * 100 ]; 
                StringBuilder sb = new StringBuilder();            
                FileInputStream inp = null; 
                try {                 
                    inp = new FileInputStream( idxfile );
                    while ((read=inp.read(bytes)) != -1) {
                        sb.append(new String(bytes, 0, read));
                    }

                    if ( sb.length() > 0 ) {
                        this.conf = (GroupConf) base64.decode( sb.toString() ); 
                    } 
                } catch(Throwable t) { 
                    //do nothing 
                } finally { 
                    try { inp.close(); }catch(Throwable t){;} 
                } 
            } 
        }
        
        public void update() {  
            synchronized ( FileUploadGroup.CONFIG_FILE_LOCKED ) {
                FileOutputStream fos = null; 
                try {
                    fos = new FileOutputStream( idxfile ); 
                    fos.write( base64.encode( this.conf ).getBytes() ); 
                    fos.flush(); 
                } catch(RuntimeException re) { 
                    throw re; 
                } catch(Exception e) {
                    throw new RuntimeException(e.getMessage(), e); 
                } finally {
                    try { fos.close(); }catch(Throwable t){;} 
                } 
            } 
        }
    }
    
    public static class GroupConf implements java.io.Serializable {
        public String fileid; 
        ArrayList<GroupEntry> items = new ArrayList();
        ArrayList<GroupEntry> itemsForCopy = new ArrayList();
        ArrayList<GroupEntry> itemsForUpload = new ArrayList();
    }
    public static class GroupEntry implements java.io.Serializable {
        public String fileid;
        public long filesize;
        public long bytestransferred; 
        public String status;
    } 
    
    private class ModeTempCopyProcess implements FileUploadGroupProc { 
        
        FileUploadGroup root = FileUploadGroup.this; 
        
        private boolean cancelled; 
        
        public void cancel() { 
            cancelled = true; 
        }
        
        public void run() { 
            while (true) {
                try {
                    if ( cancelled ) { 
                        throw new InterruptedException();
                    } else if ( runImpl()) { 
                        break; 
                    } 
                } catch(InterruptedException ie) {
                    break; 
                } catch(Throwable t) {
                    t.printStackTrace(); 
                } 
            }

//            Handler handler = root.getHandler(); 
//            if ( handler != null ) {
//                handler.oncompleted( root ); 
//            } 
        }
        
        private boolean runImpl() throws Exception { 
            return true;
//            ConfigFile conf = root.getConfigFile(); 
//            String strsource = root.getConf().source; 
//            if ( strsource == null || strsource.trim().length()==0 ) { 
//                throw new InterruptedException(); 
//            }
//            strsource = strsource.replace('\\', '/'); 
//            File sourcefile = new java.io.File( strsource ); 
//            if ( !sourcefile.exists() ) {
//                throw new InterruptedException(); 
//            }
            
//            File targetfile = root.getContentFile().getFile(); 
//            FileInputStream inp = null; 
//            FileOutputStream out = null; 
//            try {
//                out = new FileOutputStream( targetfile );
//                inp = new FileInputStream( sourcefile ); 
//                byte[] bytes = new byte[1024 * 100]; 
//                int read = -1; 
//                while ((read=inp.read(bytes)) != -1) { 
//                    if ( this.cancelled ) throw new InterruptedException(); 
//                    
//                    out.write(bytes, 0, read); 
//                } 
//                out.flush(); 
                
//                StatusFile sf = root.getContentFile().getStatusFile(); 
//                sf.setPos( 0 ); 
//                sf.changeMode( MODE_UPLOAD );  
//                return true; 
//            } finally { 
//                try { inp.close(); }catch(Throwable t){;} 
//                try { out.close(); }catch(Throwable t){;} 
//            } 
        } 
    } 
    
}
