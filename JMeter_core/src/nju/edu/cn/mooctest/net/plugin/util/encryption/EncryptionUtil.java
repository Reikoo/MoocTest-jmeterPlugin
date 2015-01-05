package nju.edu.cn.mooctest.net.plugin.util.encryption;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



public class EncryptionUtil {
    private static String Algorithm = "DES";
    private static String defaultKey = "witest.net";
	
    public static String encryptMD5(String str) {
		String s = str;
		if (s == null) {
			return "";
		} else {
			String value = null;
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(EncryptionUtil.class.getName()).log(Level.SEVERE, null, ex);
			}
			sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
			try {
				value = baseEncoder.encode(md5.digest(s.getBytes("utf-8")));
			} catch (Exception ex) {
			}
			return value;
		}
	}
	
    public static String encryptDES(String content) throws Exception{
    	return encryptDES(content , defaultKey);
    }
    
    public static String decryptDES(String content) throws Exception{
    	return decryptDES(content , defaultKey);
    }
    


    public static String encryptDES(String data, String key) throws Exception {
        byte[] bt = encryptDES(data.getBytes(), key.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }
 
    public static String decryptDES(String data, String key) throws IOException,
            Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decryptDES(buf,key.getBytes());
        return new String(bt);
    }
 
    private static byte[] encryptDES(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
 
        DESKeySpec dks = new DESKeySpec(key);
 
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        Cipher cipher = Cipher.getInstance(Algorithm);
 
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
     
     
    private static byte[] decryptDES(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
 
        DESKeySpec dks = new DESKeySpec(key);
 
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        Cipher cipher = Cipher.getInstance(Algorithm);
 
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
}
