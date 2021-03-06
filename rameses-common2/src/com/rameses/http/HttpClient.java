/*
 * HttpClient.java
 * Created on September 19, 2011, 8:52 AM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */
package com.rameses.http;

import com.rameses.util.SealedMessage;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This is a generic utility for accessing servlets in a service style manner
 * To use pass the hosts (including ports) list in the constructor. Hosts can be separated
 * by a semicolon so that it would act as a failover. This is not to be used as HA
 * Example usage:
 *
 * HttpClient c = new HttpClient("localhost:8080;10.0.0.151:8080;10.0.0.152:8080", true);
 * Map map = new HashMap();
 * map.put("id", "12345" );
 * c.get( "osiris2/get" );
 * c.post( "send", map );                                                 //simple post to any website
 * c.post( "ejb-secured/appcontext/SessionService/local.fire", map )   //access the service invoker securely
 * c.post( "ejb/appcontext/SessionService/local.fire", map );
 * map contains env and args
 */
public class HttpClient implements Serializable {
    
    public static HttpClientOutputHandler STRING_OUTPUT = new StringHttpClientOutputHandler();

    private boolean debug;     
    private int readTimeout =  30000;    //default read timeout at 5 seconds
    private int connectionTimeout = 5000;

    private String protocol = "http";
    private HttpClientOutputHandler outputHandler;
    
    //if true, parameters will be written in the outputstream as an object.
    private boolean postAsObject = false;
    
    private String[] hosts;
    
    //encrypted is applicable only if postAsObject is true.
    //by default the transfer type is encrypted
    private boolean encrypted = true;
    
    public HttpClient(String host) {
        this( host, false );
    }
    
    public HttpClient(String host, boolean postAsObject) {
        this.hosts = host.split(";");
        this.postAsObject = postAsObject;
    }
    
    public boolean isDebug() { return debug; } 
    public void setDebug( boolean debug ) {
        this.debug = debug; 
    }
    
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    public void setConnectionTimeout(int timeout) {
        this.connectionTimeout = timeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public void setOutputHandler(HttpClientOutputHandler h) {
        this.outputHandler = h;
    }
    
    /**********************************************
     * //GET METHODS
     *********************************************/
    public Object get() throws Exception {
        return get(null, null);
    }
    
    public Object get(String path) throws Exception {
        return get(path, null);
    }
    
    public Object get(String path, Map params) throws Exception {
        String parms = "";
        if( params !=null) parms = "?"+HttpClientUtils.stringifyParameters(params);
        LinkedList list = new LinkedList();
        for(String s: hosts) {
            //String ctx = (appContext!=null&&appContext.trim().length()>0) ? "/"+appContext : "";
            String p = (path!=null && path.trim().length()>0) ? "/" + path : "";
            list.add( protocol + "://" + s +  p + parms );
        }
        return invoke(list, null, "GET");
    }
    
   
    
    /**********************************************
     * //POST METHODS
     *********************************************/
    public Object post(Map params) throws Exception {
        return post(null, params);
    }
    
    public Object post(String path) throws Exception {
        return post(path, null);
    }
    
    public Object post(String path, Object args) throws Exception {
        LinkedList list = new LinkedList();
        for(String s: hosts) {
            //String ctx = (appContext!=null&&appContext.trim().length()>0) ? "/"+appContext : "";
            String p = (path!=null && path.trim().length()>0) ? "/" + path : "";
            list.add( protocol + "://" +  s  + p );
        }
        if( !postAsObject && (args instanceof Map) ) {
            args = HttpClientUtils.stringifyParameters( (Map)args);
        }
        return invoke(list, args, "POST");
    }
    
    private Object invoke(Queue<String> queue, Object parms, String methodType) throws Exception { 
        try { 
            return invokeImpl( queue, parms, methodType ); 
        } catch( Exception ex ) {
            if ( 
                (ex instanceof UnknownHostException) || 
                (ex instanceof SocketException) || 
                (ex instanceof ConnectException) || 
                (ex instanceof SocketTimeoutException) 
            ) { 
                try {
                    return invokeImpl( queue, parms, methodType );
                } catch(AllConnectionFailed ae) {
                    throw ex; 
                } 
            } else { 
                throw ex; 
            } 
        } 
    }
    
    private Object invokeImpl(Queue<String> queue, Object parms, String methodType) throws Exception {
        HttpURLConnection conn = null;
        InputStream is = null;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        String uhost = null;
        URL url = null;
        try {
            uhost = queue.poll();
            if( uhost == null ) { 
                throw new AllConnectionFailed("Cannot connect to "+uhost);
            } 
            
            url = new URL(uhost);
            //System.out.println("invoke "+ url + " started");
            conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsc = (HttpsURLConnection)conn; 
                bypassSSLSecurityCheck(httpsc); 
            }
            
            if( readTimeout > 0 ) conn.setReadTimeout(readTimeout);            
            if( connectionTimeout > 0 ) conn.setConnectTimeout(connectionTimeout);
            
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(methodType);
            
            boolean _asObject = postAsObject;
            if(!_asObject && parms==null) _asObject = true;
            if(!_asObject && !(parms instanceof String)) _asObject = true;            
            
            if ( isDebug() ) { 
                System.out.format("[HttpClient.invoke] Host=%s, Type=%s, ConnTimeout=%d, ReadTimeout=%d, PostAsObject=%s%n", uhost, methodType, connectionTimeout, readTimeout, _asObject);
            }
            
            if(_asObject && !methodType.equalsIgnoreCase("GET")) {
                conn.setRequestProperty( "CONTENT-TYPE",HttpConstants.APP_CONTENT_TYPE);
                out = new ObjectOutputStream(conn.getOutputStream());
                if(encrypted && !(parms instanceof SealedMessage)) {
                    parms = new SealedMessage(parms);
                }
                if(parms!=null) out.writeObject( parms );
                out.flush();
            } else if(methodType.equalsIgnoreCase("POST")) {
                conn.setRequestProperty( "CONTENT-TYPE", "application/x-www-form-urlencoded");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write( (String)parms );
                writer.flush();
            }
            else {
                conn.setRequestProperty( "CONTENT-TYPE", "application/x-www-form-urlencoded");
            }
            //read the input stream. we cannot use this
            try {
                is = conn.getInputStream();
            } catch(Exception e) {
                InputStream es = conn.getErrorStream();
                if ( es != null ) {
                    String errMsg = conn.getHeaderField("Error-Message");
                    Exception orig = null;
                    if ( errMsg != null ) {
                        orig = new Exception(errMsg);
                    }
                    throw new ResponseError(conn.getResponseCode(), conn.getResponseMessage(), orig);
                } else { 
                    throw e; 
                } 
            } 
            
            if( outputHandler ==null ) {
                Object retval = null;
                if(is!=null) {
                    String t = conn.getContentType();
                    if(t!=null && (t.startsWith("text") || t.indexOf("json")>0) ) {
                        StringBuilder b = new StringBuilder();
                        int i = 0;
                        while((i=is.read())!=-1) {
                            b.append((char)i);
                        }
                        retval = b.toString();
                    } else {
                        try {
                            in = new ObjectInputStream(is);
                            retval =  in.readObject();
                        } catch(Throwable tt){
                            //System.out.println("error HttpClient. " + tt.getClass().getName() + ": "+ tt.getMessage());
                            //tt.printStackTrace(); 
                        }
                    }
                }
                
               //check first if the result is sealed. If true, unseal it  
                if(retval!=null && (retval instanceof SealedMessage)) {
                    SealedMessage sm = (SealedMessage)retval;
                    retval = sm.getMessage();
                }
                
                if(retval==null) {
                    //do nothing
                } else if( (retval instanceof String) && retval.equals("#NULL")  ) {
                    retval = null;
                } else if( retval instanceof Exception ) { 
                    throw (Exception)retval;
                } 
                return retval;
            } else {
                return outputHandler.getResult(is);
            }
        } finally {
            try { in.close(); } catch(Throwable ign){;}
            try { is.close(); } catch(Throwable ign){;}
            try { out.close(); } catch(Throwable ign){;}
            try { conn.disconnect(); } catch (Throwable ign){;}
            
            //System.out.println("invoke "+ url + " ended");
        }
    }

    public boolean isEncrypted() {
        return encrypted;
    }
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
    
    
    
    private void bypassSSLSecurityCheck(HttpsURLConnection conn) {
        SSLSocketFactory sslsf = getSSLSocketFactory();
        if (sslsf == null) return;
        
        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { 
                return true; 
            }
        };        
        conn.setSSLSocketFactory(sslsf); 
        conn.setHostnameVerifier(hv); 
    }
    
    
    private SSLSocketFactory sslSocketFactory;
    private SSLSocketFactory getSSLSocketFactory() {
        if (sslSocketFactory == null) {
            sslSocketFactory = createSSLSocketFactory();
        }
        return sslSocketFactory;             
    }
    
    private SSLSocketFactory createSSLSocketFactory() {
        try { 
            TrustManager[] trustAllCerts = new TrustManager[] { 
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { 
                        return new X509Certificate[0]; 
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance( "SSL" );
            sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
            // Create an ssl socket factory with our all-trusting manager
            return sslContext.getSocketFactory();  
        } catch(Throwable t) {
            return null; 
        }
    }
}
