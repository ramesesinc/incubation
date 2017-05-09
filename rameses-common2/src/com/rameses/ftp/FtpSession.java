/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

/**
 *
 * @author wflores 
 */
public class FtpSession {
    
    private final static int DEFAULT_BUFFER_SIZE = (100 * 1024); 
    
    private FtpLocationConf conf; 
    private FTPClient ftp; 
    
    private int bufferSize; 
    private Handler handler; 
    
    public FtpSession( FtpLocationConf conf ) {
        this.conf = conf; 
    }
    
    public Handler getHandler() { return handler; } 
    public void setHandler( Handler handler ) {
        this.handler = handler; 
    }
    
    public int getBufferSize() { return bufferSize; } 
    public void setBufferSize( int bufferSize ) {
        this.bufferSize = bufferSize; 
    }
    
    public void connect() { 
        if ( ftp != null ) disconnect(); 
        
        try {
            ftp = new FTPClient();
            ftp.connect( conf.getHost(), conf.getPort() ); 
            int respcode = ftp.getReplyCode(); 
            if ( !FTPReply.isPositiveCompletion(respcode)) {
                throw new RuntimeException("[ftp_error] Connection refused"); 
            }
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    public void login() {
        try {
            ftp.login( conf.getUser(), conf.getPassword() );  
            if ( FTPReply.isPositiveCompletion( ftp.getReplyCode())) {
                // authenticated 
            } else { 
                throw new RuntimeException("[ftp_error] "+ ftp.getReplyString()); 
            }
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    public void logout() {
        try { 
            ftp.logout(); 
        } catch(Throwable t){
            //do nothing 
        } 
    }
    public void disconnect() { 
        try { 
            ftp.disconnect(); 
        } catch(Throwable t){
            //do nothing 
        } finally {
            ftp = null; 
        }
    }
    
    public void download( String remoteName, File targetFile ) {
        InputStreamProxy inp = null; 
        try { 
            login(); 
            applySettings(); 
            
            DownloadStreamProxy dsp = new DownloadStreamProxy(targetFile); 
            ftp.setRestartOffset( dsp.getLength()); 
            ftp.retrieveFile(remoteName, dsp);
        } catch(IOException ioe) { 
            throw new RuntimeException(ioe); 
        } finally {
            try { inp.close(); }catch(Throwable t){;} 
            
            logout(); 
        } 
        
        Handler handler = getHandler(); 
        if ( handler != null ) { 
            handler.oncompleted(); 
        }         
    }

    public void upload( String remoteName, File file ) {
        upload( remoteName, file, -1 ); 
    }
    
    public void upload( String remoteName, File file, long startpos ) {
        InputStreamProxy inp = null; 
        try { 
            login(); 
            applySettings(); 
            
            inp = new InputStreamProxy( file ); 
            if ( inp.upload( ftp, remoteName, startpos )) {
                // do nothing 
                
            } else {
                Throwable error = inp.getError(); 
                if ( error instanceof RuntimeException ) {
                    throw (RuntimeException)error;  
                } else { 
                    throw new RuntimeException(error.getMessage(), error); 
                } 
            }
        } finally {
            try { inp.close(); }catch(Throwable t){;} 
            
            logout(); 
        } 
        
        Handler handler = getHandler(); 
        if ( handler != null ) { 
            handler.oncompleted(); 
        } 
    }
    
    private void applySettings() {
        // use local passive mode to pass firewall 
        ftp.enterLocalPassiveMode();
        // set timeout to 5 minutes
        ftp.setControlKeepAliveTimeout( 300 );
        
        int buffsize = getBufferSize(); 
        ftp.setBufferSize( buffsize > 0 ? buffsize : DEFAULT_BUFFER_SIZE ); 
        try { 
            ftp.setFileType( FTP.BINARY_FILE_TYPE );
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    public static interface Handler { 
        void onupload( long filesize, long bytesprocessed ); 
        void oncompleted(); 
    }
    
    private class InputStreamProxy extends InputStream implements CopyStreamListener {
        
        FtpSession root = FtpSession.this; 
        
        private File srcfile; 
        private RandomAccessFile raf; 
        private FileChannel fc; 
        
        private long filesize;
        private long startpos;
        
        private Throwable error; 
        
        InputStreamProxy( File srcfile ) { 
            this.srcfile = srcfile; 
        } 
        
        public int read() throws IOException {
            return raf.read(); 
        }
        
        public int read(byte[] b) throws IOException { 
            return raf.read(b); 
        }

        public int read(byte[] b, int off, int len) throws IOException { 
            return raf.read(b, off, len); 
        }

        public long skip(long n) throws IOException { 
            return raf.skipBytes((int) n);
        }

        public int available() throws IOException {
            Number num = raf.getChannel().size(); 
            return num.intValue(); 
        }

        public void close() throws IOException { 
            try { fc.close(); }catch(Throwable t){;} 
            try { raf.close(); }catch(Throwable t){;} 
        }

        public synchronized void mark(int readlimit) { 
            super.mark(readlimit);
        }

        public synchronized void reset() throws IOException {
            super.reset();
        }

        public boolean markSupported() {
            return super.markSupported();
        }        
        
        Throwable getError() { 
            return error; 
        } 
        
        boolean upload( FTPClient ftp, String remoteName, long offset ) { 
            error = null; 
            try { 
                raf = new RandomAccessFile( srcfile, "r");
                fc = raf.getChannel(); 
                
                filesize = fc.size(); 
                startpos = ( offset >= 0 ? offset : 0 ); 
                
                if ( startpos >= filesize ) {
                    // upload already completed 
                    return true; 
                } 
                
                raf.seek( startpos ); 
                byte[] src = new byte[ ftp.getBufferSize() ];
                int read = raf.read(src); 
                if ( read < 0 ) return true; 

                raf.seek( startpos ); 
                ftp.setRestartOffset( startpos ); 
                ftp.setCopyStreamListener( this); 
                ftp.storeFile( remoteName, this); 
                if ( FTPReply.isPositiveCompletion( ftp.getReplyCode())) {
                    // sucessfully transferred 
                    return true; 
                    
                } else {
                    throw new RuntimeException("[ftp_error] "+ ftp.getReplyString());                     
                } 
            } catch(Throwable t) {
                error = t; 
                return false; 
            } 
        }

        public void bytesTransferred(CopyStreamEvent cse) {
        }
        public void bytesTransferred(long totalBytesTransferred, int byteTransferred, long streamSize) { 
            Handler handler = root.getHandler(); 
            if ( handler == null ) return; 
            
            long procbytes = startpos + totalBytesTransferred; 
            handler.onupload( filesize, procbytes ); 
        }  
    } 
    
    private class DownloadStreamProxy extends OutputStream {

        private File targetFile; 
        private FileOutputStream fos;
        
        DownloadStreamProxy( File targetFile ) {
            this.targetFile = targetFile; 
            
            try { 
                this.fos = new FileOutputStream( targetFile );
            } catch (FileNotFoundException fnfe ) { 
                throw new RuntimeException( fnfe );
            } 
        }
        
        public long getLength() { 
            if ( !targetFile.exists()) return 0;
            
            RandomAccessFile raf = null; 
            FileChannel fc = null; 
            try { 
                raf = new RandomAccessFile( targetFile, "r" );
                fc = raf.getChannel(); 
                return fc.size(); 
            } catch(FileNotFoundException fnfe) {
                throw new RuntimeException(fnfe); 
            } catch(IOException ioe) {
                throw new RuntimeException(ioe); 
            } finally {
                try { fc.close(); }catch(Throwable t){;}
                try { raf.close(); }catch(Throwable t){;}
            }
        } 
        
        public void write(int b) throws IOException {
            fos.write( b ); 
        } 

        public void close() throws IOException { 
            super.close(); 
            
            try { 
                fos.close(); 
            }catch(Throwable ign){;} 
        }

        public void flush() throws IOException {
            super.flush();
            
            try { 
                fos.flush(); 
            }catch(Throwable ign){;} 
        }
    }
}
