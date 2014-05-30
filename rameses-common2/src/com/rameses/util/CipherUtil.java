package com.rameses.util;

import java.io.Serializable;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/**
 *
 * @author elmo
 */
public final class CipherUtil 
{    
    private static String ENC_MODE = "AES"; 
    private static CipherUtil instance = new CipherUtil(); 
    
    
    public static Object encode( Serializable obj ) throws Exception {
        return encode(obj,ENC_MODE);
    }
    
    public static Object encode( Serializable obj, String encmode ) throws Exception {
        return instance.encodeObject(obj, encmode);
    }
    
    public static Object decode( Serializable obj ) throws Exception {
        return decode(obj,ENC_MODE);
    }
    
    public static Object decode( Serializable obj, String encmode ) throws Exception {
        return instance.decodeObject(obj, encmode); 
    }
    
    
    public Object encodeObject( Serializable obj )  {
        return encodeObject( obj, ENC_MODE );
    }
    public Object encodeObject( Serializable obj, String encmode ) { 
        try { 
            SecretKey sk = KeyGenerator.getInstance(encmode).generateKey(); 
            Cipher enc = Cipher.getInstance(encmode); 
            enc.init(Cipher.ENCRYPT_MODE, sk); 
            SealedObject so = new SealedObject(obj, enc); 
            return  new Object[]{sk, so}; 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    } 

    public Object decodeObject( Serializable obj )  {
        return decodeObject( obj, ENC_MODE );
    }
    public Object decodeObject( Serializable obj, String encmode ) { 
        try { 
             Object[] o = (Object[])obj;
            if( o.length != 2  ) throw new Exception( "Error secured parameter count");
            SecretKey sk = (SecretKey) o[0];
            SealedObject so = (SealedObject) o[1];
            Cipher dec = Cipher.getInstance(encmode);
            dec.init(Cipher.DECRYPT_MODE, sk);
            return so.getObject(dec); 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    } 
}
