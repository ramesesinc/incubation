/*
 * Base64Coder.java
 *
 * Created on July 9, 2013, 1:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.util;

public final class Base64Coder 
{
    // The line separator string of the operating system.
    private final static String systemLineSeparator = System.getProperty("line.separator");
    
    private final static Base64Cipher cipher = new Base64Cipher();
        
    /**
     * Encodes a string into Base64 format.
     * No blanks or line breaks are inserted.
     * @param s  A String to be encoded.
     * @return   A String containing the Base64 encoded data.
     */
    public static String encodeString(String s) {
        return cipher.encodeString(s); 
    }
    
    /**
     * Encodes a byte array into Base 64 format and breaks the output into lines of 76 characters.
     * This method is compatible with <code>sun.misc.BASE64Encoder.encodeBuffer(byte[])</code>.
     * @param in  An array containing the data bytes to be encoded.
     * @return    A String containing the Base64 encoded data, broken into lines.
     */
    public static String encodeLines(byte[] in) {
        return cipher.encodeLines(in);
    }
    
    /**
     * Encodes a byte array into Base 64 format and breaks the output into lines.
     * @param in            An array containing the data bytes to be encoded.
     * @param iOff          Offset of the first byte in <code>in</code> to be processed.
     * @param iLen          Number of bytes to be processed in <code>in</code>, starting at <code>iOff</code>.
     * @param lineLen       Line length for the output data. Should be a multiple of 4.
     * @param lineSeparator The line separator to be used to separate the output lines.
     * @return              A String containing the Base64 encoded data, broken into lines.
     */
    public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator) {
        return cipher.encodeLines(in, iOff, iLen, lineLen, lineSeparator);
    }
    
    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in  An array containing the data bytes to be encoded.
     * @return    A character array containing the Base64 encoded data.
     */
    public static char[] encode(byte[] in) {
        return encode(in, in.length); 
    }
    
    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in    An array containing the data bytes to be encoded.
     * @param iLen  Number of bytes to process in <code>in</code>.
     * @return      A character array containing the Base64 encoded data.
     */
    public static char[] encode(byte[] in, int iLen) {
        return encode(in, 0, iLen); 
    }
    
    /**
     * Encodes a byte array into Base64 format.
     * No blanks or line breaks are inserted in the output.
     * @param in    An array containing the data bytes to be encoded.
     * @param iOff  Offset of the first byte in <code>in</code> to be processed.
     * @param iLen  Number of bytes to process in <code>in</code>, starting at <code>iOff</code>.
     * @return      A character array containing the Base64 encoded data.
     */
    public static char[] encode(byte[] in, int iOff, int iLen) {
        return cipher.encode(in, iOff, iLen); 
    }
        
    public char[] encode(Object value) {
        String encstr = cipher.encode(value); 
        return (encstr == null? null: encstr.toCharArray()); 
    }
    
    /**
     * Decodes a string from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param s  A Base64 String to be decoded.
     * @return   A String containing the decoded data.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static String decodeString(String s) {
        Object o = cipher.decode(s); 
        return (o == null? null: o.toString()); 
    }
    
    /**
     * Decodes a byte array from Base64 format and ignores line separators, tabs and blanks.
     * CR, LF, Tab and Space characters are ignored in the input data.
     * This method is compatible with <code>sun.misc.BASE64Decoder.decodeBuffer(String)</code>.
     * @param s  A Base64 String to be decoded.
     * @return   An array containing the decoded data bytes.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decodeLines(String s) {
        return cipher.decodeLines(s); 
    }
    
    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param s  A Base64 String to be decoded.
     * @return   An array containing the decoded data bytes.
     * @throws   IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode(String s) {
        return decode(s.toCharArray()); 
    }
    
    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param in  A character array containing the Base64 encoded data.
     * @return    An array containing the decoded data bytes.
     * @throws    IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode(char[] in) {
        return decode(in, 0, in.length); 
    }
    
    /**
     * Decodes a byte array from Base64 format.
     * No blanks or line breaks are allowed within the Base64 encoded input data.
     * @param in    A character array containing the Base64 encoded data.
     * @param iOff  Offset of the first character in <code>in</code> to be processed.
     * @param iLen  Number of characters to process in <code>in</code>, starting at <code>iOff</code>.
     * @return      An array containing the decoded data bytes.
     * @throws      IllegalArgumentException If the input is not valid Base64 encoded data.
     */
    public static byte[] decode(char[] in, int iOff, int iLen) { 
        return cipher.decode(in, iOff, iLen); 
    } 
} 
