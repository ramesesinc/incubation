/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.io.FileLocType;
import com.rameses.io.FileLocTypeProvider;
import com.rameses.io.FileTransferSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author wflores 
 */
public class FileUploadItem {
        
    public final static String MODE_TEMP_COPY = "TEMP";
    public final static String MODE_UPLOAD    = "UPLOAD";
    public final static String MODE_COMPLETED = "COMPLETED";
    
    public final static String CONF_FILE_LOC_ID = "filelocid";
    public final static String CONF_FILE_SOURCE = "source";
    public final static String CONF_FILE_SIZE = "filesize";
    public final static String CONF_FILE_TYPE = "filetype";
    public final static String CONF_FILE_ID   = "fileid";
    
    private File folder; 
    private ConfigFile confFile;  
    private ContentFile contentFile;
    private Handler handler; 
    
    public FileUploadItem( File file ) {
        this.folder = file; 
    }
    
    public String getName() { 
        return folder.getName(); 
    } 
    
    public void create( Map conf ) { 
        create( conf, false );  
    }    
    public void create( Map conf, boolean immediate ) { 
        verifyFolder();
        if ( immediate ) { 
            createTempFile( ".immediate "); 
        } 
        confFile = new ConfigFile( folder, ".conf" ); 
        confFile.create( conf ); 
        Number filesize = confFile.getPropertyAsNumber( CONF_FILE_SIZE );
        if ( filesize == null ) filesize = 0; 
        
        String str = MODE_TEMP_COPY +","+ filesize +",0";
        contentFile = new ContentFile( folder, "content" ); 
        contentFile.getStatusFile().create( str );  
    }
    public void open() {
        verifyFolder();
        confFile = new ConfigFile( folder, ".conf" ); 
        confFile.read();
        contentFile = new ContentFile( folder, "content" ); 
        contentFile.getStatusFile().read(); 
    } 
    
    public ConfigFile getConfigFile() { 
        return confFile;  
    } 
    public ContentFile getContentFile() {
        return contentFile; 
    }

    public Handler getHandler() { return handler; } 
    public void setHandler( Handler handler ) {
        this.handler = handler; 
    }
    
    public boolean isModelTempCopy() {
        return getContentFile().isModeTempCopy(); 
    }
    public boolean isModeCompleted() { 
        return getContentFile().isModeCompleted(); 
    } 
    public boolean isModeUpload() { 
        return getContentFile().isModeUpload();
    } 
    
    public void remove() { 
        remove( this.folder ); 
    }   
    private void remove( File file ) {
        if ( file==null || !file.exists()) return; 
        
        if ( file.isDirectory()) {
            File[] files = file.listFiles(); 
            for ( File child : files ) {
                remove( child ); 
            }
        } 

        try { 
            file.delete(); 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        }
    }
    
    private void verifyFolder() {
        if ( !folder.exists()) { 
            folder.mkdir(); 
            
        } else if ( !folder.isDirectory()) {
            folder.delete();  
            folder.mkdir(); 
        } 
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
    public boolean removeTempFile( String name ) {
        try {
            File tmp = new File( folder, name); 
            return (tmp.exists() ? tmp.delete() : true); 
        } catch(Throwable t) {
            return false; 
        } 
    }
    
    public FileUploadItemProc createProcessHandler() {
        if ( getContentFile().isModeTempCopy()) {
             return new ModeTempCopyProcess();  
        } else if ( getContentFile().isModeUpload()) {
            return new ModeUploadProcess(); 
        }
        return null; 
    } 
    
    public static interface Handler { 
        void ontransfer( FileUploadItem item, long filesize, long bytesprocessed );
        void oncompleted( FileUploadItem item );
    } 
    
    public class ConfigFile { 
        private File folder;
        private File idxfile; 
        private Map conf; 
        
        ConfigFile( File folder, String name ) {
            this.folder = folder; 
            this.idxfile = new File( folder, name ); 
            this.conf = new HashMap(); 
        }

        void create( Map conf ) {
            if ( conf == null || conf.isEmpty() ) return; 
            
            this.conf.clear(); 
            this.conf.putAll( conf ); 
                        
            update(); 
        } 

        Map copyData() {
            Map data = new HashMap();
            data.putAll( this.conf ); 
            data.put( CONF_FILE_SIZE, getPropertyAsNumber( CONF_FILE_SIZE));
            return data; 
        }
        
        public void read() { 
            FileInputStream inp = null; 
            try { 
                inp = new FileInputStream( idxfile );
                Properties props = new Properties(); 
                props.load( inp ); 
                this.conf.clear(); 
                this.conf.putAll( props ); 
            } catch(Throwable t) { 
                //do nothing 
            } finally { 
                try { inp.close(); }catch(Throwable t){;} 
            }
        }
        
        public void update() {
            FileOutputStream fos = null; 
            try {
                StringBuilder sb = new StringBuilder();
                Set<Entry<Object,Object>> sets = this.conf.entrySet(); 
                for ( Entry<Object,Object> entry : sets ) {
                    String skey = (entry.getKey()==null? null: entry.getKey().toString()); 
                    if ( skey != null && skey.trim().length() > 0 ) {
                        String sval = (entry.getValue()==null? "": entry.getValue().toString()); 
                        sb.append(skey).append("=").append(sval).append("\n"); 
                    } 
                } 

                fos = new FileOutputStream( idxfile ); 
                fos.write( sb.toString().replace('\\','/').getBytes() ); 
                fos.flush(); 
            } catch(RuntimeException re) { 
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { fos.close(); }catch(Throwable t){;} 
            }
        }
        public String getProperty( String name ) {
            Object value = this.conf.get( name ); 
            return (value == null? null: value.toString()); 
        }
        public Number getPropertyAsNumber( String name ) { 
            try { 
                String value = getProperty( name ); 
                return new Long( value );  
            } catch(Throwable t) { 
                return null; 
            } 
        }
        public void setProperty( String name, Object value ) {
            this.conf.put( name, value ); 
        } 
    }
    
    public class ContentFile {
        
        private File folder; 
        private File child; 
        private StatusFile statusFile;
        
        ContentFile( File folder, String name ) {
            this.folder = folder; 
            this.child = new File( folder, name ); 
            this.statusFile = new StatusFile( folder, name+".index" ); 
        }
        
        public File getFile() { return child; } 
        
        StatusFile getStatusFile() { return statusFile; } 
        
        public boolean isModeTempCopy() { 
            String sval = getStatusFile().getMode(); 
            if ( sval==null || sval.trim().length()==0 ) { 
                return true; 
            } else { 
                return MODE_TEMP_COPY.equalsIgnoreCase(sval); 
            } 
        } 
        public boolean isModeUpload() { 
            String sval = getStatusFile().getMode(); 
            return MODE_UPLOAD.equalsIgnoreCase(sval+""); 
        } 
        public boolean isModeCompleted() { 
            String sval = getStatusFile().getMode(); 
            return MODE_COMPLETED.equalsIgnoreCase(sval+""); 
        }         
    }
    
    private class StatusFile {
        
        private File folder;
        private File file; 
        
        private String mode; 
        private Number numSize;
        private Number numPos;
        
        StatusFile( File folder, String name ) { 
            this.folder = folder; 
            this.file = new File( folder, name );  
        } 
        
        String getMode() {
            return ( mode==null || mode.trim().length()==0 ? MODE_TEMP_COPY : mode ); 
        }
        long getSize() {
            return (numSize == null ? 0 : numSize.longValue()); 
        }
        long getPos() {
            return (numPos == null ? 0 : numPos.longValue()); 
        }
        void setPos( long pos ) {
            this.numPos = pos; 
        }
        
        void create( String data ) {
            OutputStream out = null; 
            try {
                out = new FileOutputStream( file );   
                out.write( data.getBytes()  );  
                out.flush(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally { 
                try { out.close(); }catch(Throwable t){;} 
            } 
            
            read(); 
        }
        void read() {
            FileInputStream inp = null; 
            try {
                inp = new FileInputStream( file );      

                StringBuilder sb = new StringBuilder(); 
                byte[] bytes = new byte[1024]; 
                int read = -1;
                while ((read=inp.read(bytes)) != -1) {
                    sb.append(new String(bytes, 0, read)); 
                }

                mode = null; 
                numSize = null; 
                numPos = null; 
                // mode,size,pos
                String[] arrs = sb.toString().replaceAll("[\\s]{1,}","").split(","); 
                if ( arrs.length >= 1 ) mode = arrs[0]; 
                if ( arrs.length >= 2 ) numSize = convertNumber( arrs[1]); 
                if ( arrs.length >= 3 ) numPos = convertNumber( arrs[2]); 
            } catch(Throwable t) {
                //do nothing 
            } finally { 
                try { inp.close(); }catch(Throwable t){;} 
            } 
        }
        void update() {
            OutputStream out = null; 
            try {
                String str = getMode() +","+ getSize() +","+ getPos();
                out = new FileOutputStream( file );   
                out.write( str.getBytes()  );  
                out.flush(); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally { 
                try { out.close(); }catch(Throwable t){;} 
            } 
        }        
        
        void changeMode( String mode ) { 
            if ( mode == null ) mode = ""; 
            
            if ( MODE_UPLOAD.equalsIgnoreCase(mode)) {
                this.mode = MODE_UPLOAD; 
            } else if ( MODE_COMPLETED.equalsIgnoreCase(mode)) {
                this.mode = MODE_COMPLETED; 
            } else { 
                this.mode = MODE_TEMP_COPY; 
            } 
            update(); 
        } 
        
        Number convertNumber( Object value ) {
            try {
                if( value instanceof Number ) {
                    return (Number) value; 
                }
                return new Long( value.toString());
            } catch(Throwable t) {
                return null; 
            }
        } 
    }    
        
    private class ModeTempCopyProcess implements FileUploadItemProc { 
        
        FileUploadItem root = FileUploadItem.this; 
        
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

            Handler handler = root.getHandler(); 
            if ( handler != null ) {
                handler.oncompleted( root ); 
            } 
        }
        
        private boolean runImpl() throws Exception { 
            ConfigFile conf = root.getConfigFile(); 
            String strsource = conf.getProperty( CONF_FILE_SOURCE ); 
            if ( strsource == null || strsource.trim().length()==0 ) { 
                throw new InterruptedException(); 
            }
            strsource = strsource.replace('\\', '/'); 
            File sourcefile = new java.io.File( strsource ); 
            if ( !sourcefile.exists() ) {
                throw new InterruptedException(); 
            }
            
            File targetfile = root.getContentFile().getFile(); 
            FileInputStream inp = null; 
            FileOutputStream out = null; 
            try {
                out = new FileOutputStream( targetfile );
                inp = new FileInputStream( sourcefile ); 
                byte[] bytes = new byte[1024 * 100]; 
                int read = -1; 
                while ((read=inp.read(bytes)) != -1) { 
                    if ( this.cancelled ) throw new InterruptedException(); 
                    
                    out.write(bytes, 0, read); 
                } 
                out.flush(); 
                
                StatusFile sf = root.getContentFile().getStatusFile(); 
                sf.setPos( 0 ); 
                sf.changeMode( MODE_UPLOAD );  
                return true; 
            } finally { 
                try { inp.close(); }catch(Throwable t){;} 
                try { out.close(); }catch(Throwable t){;} 
            } 
        } 
    } 
    
    private class ModeUploadProcess implements FileUploadItemProc {

        FileUploadItem root = FileUploadItem.this; 

        private boolean cancelled; 
        private FileTransferSession sess; 
        
        public void cancel() {
            cancelled = true; 
            
            try {
                sess.cancel(); 
            } catch(Throwable t){;} 
            
            sess = null; 
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
            
            Handler handler = root.getHandler(); 
            if ( handler != null ) {
                handler.oncompleted( root ); 
            } 
        }
        
        private boolean runImpl() throws Exception { 
            ConfigFile conf = root.getConfigFile(); 
            conf.read(); 
            
            StatusFile sf = getContentFile().getStatusFile();
            sf.read(); 
            
            long filepos = sf.getPos(); 
            long filesize = sf.getSize(); 
            if ( filepos >= filesize ) { 
                sf.changeMode( MODE_COMPLETED ); 
                throw new InterruptedException(); 
            } 

            String filelocid = conf.getProperty( CONF_FILE_LOC_ID ); 
            FileConf fileloc = FileConf.get( filelocid ); 
            if ( fileloc == null ) {
                System.out.println("[mode_upload_process] '"+ filelocid +"' file location not found for "+ root.getName()); 
                throw new InterruptedException(); 
            }
            
            FileLocTypeProvider provider = FileLocType.getProvider( fileloc.getType()); 
            if ( provider == null ) {
                System.out.println("[mode_upload_process] '"+ fileloc.getType() +"' file location type not found for "+ root.getName()); 
                throw new InterruptedException(); 
            }
            
            String filetype = conf.getProperty( CONF_FILE_TYPE ); 
            StringBuilder sb = new StringBuilder(); 
            sb.append( root.getName() ); 
            if ( filetype != null && filetype.trim().length() > 0 ) {
                sb.append(".").append( filetype.trim()); 
            }
            
            sess = provider.createUploadSession(); 
            sess.setFile( root.getContentFile().getFile() ); 
            sess.setLocationConfigId( filelocid );
            sess.setTargetName( sb.toString() );
            sess.setOffset( filepos ); 
            sess.setHandler( new TransferHandler()); 
            sess.run(); 
            return true; 
        } 
    }
    
    private class TransferHandler implements FileTransferSession.Handler {

        FileUploadItem root = FileUploadItem.this; 
        
        public void ontransfer(long filesize, long bytesprocessed) {
            StatusFile sf = root.getContentFile().getStatusFile(); 
            sf.read();
            sf.setPos( bytesprocessed );
            sf.update(); 
            
            Handler handler = root.getHandler(); 
            if ( handler != null ) {
                handler.ontransfer( root, filesize, bytesprocessed ); 
            } 
        }

        public void oncomplete() { 
            root.getContentFile().getStatusFile().changeMode( MODE_COMPLETED ); 
        } 
    } 
}
