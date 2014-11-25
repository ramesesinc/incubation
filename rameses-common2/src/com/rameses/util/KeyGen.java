/*
 * KeyGen.java
 *
 * Created on August 20, 2010, 1:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.util;

import java.security.SecureRandom;

/**
 *
 * @author elmo
 */
public final class KeyGen {
    
    private final static Provider impl = new KeyGen().newProviderImpl(); 
    
    public static final Provider newProvider() { 
        return new KeyGen().newProviderImpl();  
    } 
    
    public static final String generateIntKey( String prefix, int length ) {
        return impl.generateIntKey(prefix, length); 
    }
    
    public static final String generateAlphanumKey( String prefix, int length ) {
        return impl.generateAlphanumKey(prefix, length); 
    } 
    
    public static interface Provider {
        String generateIntKey( String prefix, int length ); 
        String generateAlphanumKey( String prefix, int length );
        String generateRandomKey( String prefix );
    }
    
    private final Provider newProviderImpl() { 
        return new ProviderImpl(); 
    } 
    
    private class ProviderImpl implements Provider { 
        
        public String generateIntKey(String prefix, int length) { 
            String snum = getSecuredRandom().nextInt()+""; 
            StringBuffer key = new StringBuffer();
            if (prefix!=null) {
                key.append(prefix.toString().replaceAll(" ","").trim());
            }

            if (snum.length() < 10) { 
                int len = 10 - snum.length(); 
                for (int i=0; i<len; i++) { 
                    key.append("0"); 
                } 
            } 
            key.append(snum); 
            return key.toString(); 
        } 

        public String generateAlphanumKey(String prefix, int length) { 
            String alphanum = "ACDEFHJKLMNPQRTUVWXY1234567890";
            StringBuffer sbuff = new StringBuffer();
            if (prefix != null) sbuff.append(prefix);

            SecureRandom sr = getSecuredRandom();
            for(int i=0; i<length;i++) {
                int idx = (int) (sr.nextDouble() * alphanum.length());
                sbuff.append(alphanum.substring(idx, idx+1));
            } 
            return sbuff.toString(); 
        } 
        
        public String generateRandomKey(String prefix) { 
            SecureRandom sr = getSecuredRandom();
            int inum = sr.nextInt();
            String spre = (inum < 0? "1": "0"); 
            String snum = inum+"";
            snum = snum.replaceFirst("-", ""); 
            String sval = spre + padLeft(snum, 10, "0");
            if (prefix == null || prefix.trim().length() == 0) {
                return sval; 
            } else {
                return prefix + sval; 
            }
        }
        
        private SecureRandom getSecuredRandom() {
            SecureRandom sr = new SecureRandom();
            sr.setSeed(System.currentTimeMillis() + System.identityHashCode(sr));
            return sr; 
        }
        
        private String padLeft(String text, int length, String prefix) {
            StringBuilder sb = new StringBuilder();
            int diff = Math.max(length - text.length(), 0); 
            if (diff > 0 && prefix != null) {
                for (int i=0; i<diff; i++) {
                    sb.append(prefix);
                }
            }
            sb.append(text);
            return sb.toString(); 
        }
    } 
} 
