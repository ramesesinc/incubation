/*
 * ImageUtil.java
 *
 * Created on April 21, 2014, 4:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
/**
 *
 * @author wflores
 */

public class ImageUtil 
{
    private static ImageUtil instance;
    public static synchronized ImageUtil getInstance() {
        if (instance == null) {
            instance = new ImageUtil();
        }
        return instance;
    }    
    
    public ImageUtil() {
    }
    
    public byte[] createThumbnail(File file) {
        return createThumbnail(file, 35, 35);
    }
    
    public byte[] createThumbnail(File file, int width, int height) {
        try {
            return createThumbnail(new FileInputStream(file), width, height); 
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex.getMessage(), ex); 
        } 
    }
    
    public byte[] createThumbnail(InputStream inp) {
        return createThumbnail(inp, 35, 35);
    }
    
    public byte[] createThumbnail(InputStream inp, int width, int height) {
        byte[] bytes = toByteArray(inp);
        return createThumbnail(bytes, width, height); 
    }
    
    public byte[] createThumbnail(byte[] bytes) {
        return createThumbnail(bytes, 35, 35);
    }
    
    public byte[] createThumbnail(byte[] bytes, int width, int height) {
        if (bytes == null) return null;
        
        ImageIcon icon = new ImageIcon(bytes); 
        Dimension dim = getScaledSize(icon, new Dimension(width, height)); 
        int x = Math.max((width-dim.width)/2, 0);
        int y = Math.max((height-dim.height)/2, 0);
        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.SCALE_DEFAULT); 
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(Color.WHITE); 
        g2.fillRect(0, 0, width, height); 
        g2.drawImage(icon.getImage(), x, y, dim.width, dim.height, null); 
        g2.dispose(); 
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        try {
            ImageIO.write(bi, "JPG", baos); 
            bytes = baos.toByteArray(); 
            return bytes;
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    private Dimension getScaledSize(ImageIcon icon, Dimension size) {
        if (icon == null) return null; 
        
        int iw = icon.getIconWidth(); 
        int ih = icon.getIconHeight(); 
        if (iw < size.width && ih < size.height) {
            return new Dimension(iw, ih); 
        }
        
        double scaleX = (double)size.width  / (double)iw;
        double scaleY = (double)size.height / (double)ih;
        double scale  = (scaleY > scaleX)? scaleX: scaleY;
        int nw = (int) (iw * scale);
        int nh = (int) (ih * scale);
        return new Dimension(nw, nh); 
    } 
    
    private byte[] toByteArray(InputStream inp) {
        ByteArrayOutputStream baos = null;
        byte[] buffer = new byte[1024*2]; 
        int len = 0;
        try {
            baos = new ByteArrayOutputStream();
            while ((len=inp.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }  
            baos.flush();
            
            byte[] bytes = baos.toByteArray();
            return bytes; 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try { baos.close(); }  catch(Exception ign){;}
            try { inp.close(); } catch(Exception ign){;}            
        } 
    }
}

