/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.service.ServiceProxyInvocationHandler;
import java.lang.reflect.InvocationHandler;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class FileUploadModel {
    
    public void onstart() { 
        // to be called when the upload has started 
    }
    public void oncomplete( Object data ) {
        // to be called when the upload has ended 
    } 
    public void onerror( Throwable error ) { 
        // to be call when an error occured during the upload process 
    } 
    
    public Object getService() { 
        return null; 
    } 
    
    public void register( Object data ) { 
        getUploadService().register( data ); 
    }
    public void addItems( Object data ) {
        getUploadService().addItems( data ); 
    }
    public void removeFile( Object data ) {
        getUploadService().removeFile( data ); 
    }    

    
    // <editor-fold defaultstate="collapsed" desc=" Provider ">  
    
    private Provider provider;
    
    public void setProvider( Provider provider ) {
        this.provider = provider; 
    }
    
    public static interface Provider {
        String getFileName(); 
        String getFileType(); 
        long getFileSize(); 
        int getChunkCount(); 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Proxying Provider ">  
    
    public String getFileName() { 
        return (provider == null? null: provider.getFileName()); 
    } 
    public String getFileType() { 
        return (provider == null? null: provider.getFileType()); 
    } 
    public long getFileSize() { 
        return (provider == null? null: provider.getFileSize()); 
    } 
    public int getChunkCount() { 
        return (provider == null? null: provider.getChunkCount()); 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Service implementation "> 
    
    public static interface IUploadService {
        Object register( Object params ); 
        void addItems( Object params ); 
        void addItem( Object params ); 
        void removeFile( Object params ); 
    } 
    
    private IUploadService uploadSvc; 
    private IUploadService getUploadService() { 
        if ( uploadSvc == null ) { 
            Object osvc = getService(); 
            if ( osvc == null ) {
                Map appenv = ClientContext.getCurrentContext().getAppEnv(); 
                Map headers = ClientContext.getCurrentContext().getHeaders(); 
                ScriptServiceContext ctx = new ScriptServiceContext( appenv ); 
                ServiceProxy sp = ctx.create("FileUploadService", headers); 
                uploadSvc = new DefaultUploadServiceProxy( sp ); 
            } else {
                uploadSvc = new DefaultUploadServiceProxy( osvc ); 
            } 
        } 
        return uploadSvc; 
    } 
    
    private class DefaultUploadServiceProxy implements IUploadService {

        private Object source; 
        private ServiceProxy proxy; 
        private ServiceProxyInvocationHandler handler;
        
        DefaultUploadServiceProxy( Object source ) {
            this.source = source; 
            
            if ( source instanceof ServiceProxy ) {
                this.proxy = (ServiceProxy) source; 
            }
            if ( source instanceof ServiceProxyInvocationHandler ) {
                this.handler = (ServiceProxyInvocationHandler) source; 
            }
        }
        
        public Object register(Object params) { 
            return invokeMethod("register", params);
        }

        public void addItems(Object params) { 
            invokeMethod("addItems", params);
        }

        public void addItem(Object params) { 
            invokeMethod("addItem", params);
        }

        public void removeFile(Object params) {
            invokeMethod("removeFile", params);
        }
        
        Object invokeMethod( String name, Object params ) { 
            try { 
                if ( handler != null ) {
                    return handler.invokeMethod(name, new Object[]{ params }); 
                } else if ( proxy != null ) {
                    return proxy.invoke(name, new Object[]{ params }); 
                } else { 
                    return null; 
                } 
            } catch(RuntimeException re) { 
                throw re; 
            } catch(Throwable e) { 
                throw new RuntimeException(e.getMessage(), e); 
            } 
        } 
    }
    
   
    // </editor-fold> 
}
