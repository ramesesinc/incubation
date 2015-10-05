/*
 * URLUtil.java
 *
 * Created on September 12, 2010, 7:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.util;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

/**
 *
 * @author elmo
 */
public final class URLUtil {

    //this returns the parent URL. Just removes the last /
    public static URL getParentUrl(URL u) {
        try {
            String s = u.toExternalForm();
            s = s.substring(0, s.lastIndexOf("/"));
            return new URL(s);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static URL getSubUrl(URL u, String subDirectory) {
        try {
            String s = u.toExternalForm();
            if (subDirectory.startsWith("/")) {
                subDirectory = subDirectory.substring(1);
            }
            if (s.endsWith("/")) {
                s = s + subDirectory;
            } else {
                s = s + "/" + subDirectory;
            }
            return new URL(s);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean exists(URL url, String fileName) {
        try {
            URLConnection conn = url.openConnection();
            if (conn instanceof JarURLConnection) {
                JarURLConnection jurl = (JarURLConnection) conn;
                JarFile jf = jurl.getJarFile();
                return (jf.getEntry(fileName) != null);
            } else if (conn instanceof HttpURLConnection) {
                throw new Exception("Http URL Connection is not supported at this time.");
            } else {
                String urlFile = url.getFile();
                if(urlFile.endsWith(".jar")) {
                    //handle jar files
                    throw new Exception("URLUtil.exists error. File names that does not end with .jar not yet handled");
                }
                else {
                    File f = new File(url.getFile(), fileName);
                    return f.exists();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
}
