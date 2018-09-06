/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package client.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.rsa.crypto.RsaRawEncryptor;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class EncryptUtil {  

	private static Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
	
    private static final String UTF8 = "utf-8";  
    
    private static final String PUBLIC_KEY = "publicKey";
    
    private static final String PRIVATE_KEY = "privateKey";
    
  
    /** 
     * MD5数字签名 
     * @param src 
     * @return 
     * @throws Exception 
     */  
    public String md5Digest(String src) throws Exception {  
       // 定义数字签名方法, 可用：MD5, SHA-1  
       MessageDigest md = MessageDigest.getInstance("MD5");  
       byte[] b = md.digest(src.getBytes(UTF8));  
       return this.byte2HexStr(b);  
    }  
    
    /** 
     * BASE64编码
     * @param src 
     * @return 
     * @throws Exception 
     */  
	public String base64Encoder(String src) throws Exception {  
        BASE64Encoder encoder = new BASE64Encoder();  
        return encoder.encode(src.getBytes(UTF8));  
    }  
      
    /** 
     * BASE64解码
     * @param dest 
     * @return 
     * @throws Exception 
     */  
	public String base64Decoder(String dest) throws Exception {  
        BASE64Decoder decoder = new BASE64Decoder();  
        return new String(decoder.decodeBuffer(dest), UTF8);  
    }  
      
	/**
     * base64加密
     * 
     * @param bt 被sha256加密过后的byte
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String base64(byte[] bt)
    {
        String s = null;
        if (null != bt)
        {
            s = new BASE64Encoder().encode(bt);
        }

        return s;
    }
	
    /** 
     * 字节数组转化为大写16进制字符串 
     * @param b 
     * @return 
     */  
    private String byte2HexStr(byte[] b) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < b.length; i++) {  
            String s = Integer.toHexString(b[i] & 0xFF);  
            if (s.length() == 1) {  
                sb.append("0");  
            }  
            sb.append(s.toUpperCase());  
        }  
        return sb.toString();  
    }  
    
    /**
     * sha256加密
     * 
     * @param param  加密的参数
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @see [类、类#方法、类#成员]
     */
    public static byte[] SHA256(String param)
        throws NoSuchAlgorithmException
    {
        byte[] bt = param.getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bt);
        return md.digest();
    }
    
    
    private  static KeyPair generateKeyPair() {
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			return keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}

	}
    /**
     * MD5 加密
     * @param key
     * @return
     */
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    /**
     * UTF-8编码
     * 获取RSA 密钥
     * @return
     */
    public static Map<String, String> getRsakey() {
       Map<String, String> keysMap = new HashMap<>();
       KeyPair keyPair =  generateKeyPair();
       keysMap.put(PUBLIC_KEY, base64(keyPair.getPublic().getEncoded()));
       keysMap.put(PRIVATE_KEY,base64( keyPair.getPrivate().getEncoded()));
       return keysMap;
    }
    
    /**
     * 将公钥字符串转码为PublicKey
     * @param publickeyStr
     * @return
     */
    public static RSAPublicKey str2Public(String publickeyStr) {
    	 PublicKey publicKey = null;
    	 byte[] keyBytes;  
    	 try {
         keyBytes = (new BASE64Decoder()).decodeBuffer(publickeyStr);  
         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
         publicKey = keyFactory.generatePublic(keySpec);  
    	 }catch (Exception e) {
			logger.debug("公钥字符串转码失败，异常原因 : {}",e.getMessage());
		}
         return (RSAPublicKey)publicKey;  
    }
    
    /**
     * 将私钥字符串转码为PrivateKey
     * @param privateStr
     * @return
     */
    public static RSAPrivateKey str2Private(String privateStr) {
	     PrivateKey privateKey = null;
	   	 byte[] keyBytes;  
	   	 try {
	        keyBytes = (new BASE64Decoder()).decodeBuffer(privateStr);  
	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);  
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	        privateKey = keyFactory.generatePrivate(keySpec);  
	   	 }catch (Exception e) {
				logger.debug("私钥字符串转码失败，异常原因 : {}",e.getMessage());
			}
	        return (RSAPrivateKey)privateKey;  
   }
    
    /**
     * RAS 公钥加密加密
     * @param paramsStr
     * @param publicKey
     */
    public static String  RSAEncoderByPublic(String paramsStr,String publicKeyStr) {
    	PublicKey publicKey = str2Public(publicKeyStr) ;
    	RsaRawEncryptor rsaEncryptor = new RsaRawEncryptor(publicKey);
    	return  rsaEncryptor.encrypt(paramsStr);
    	
    }
    
    /**
     * RAS 解密
     * @param paramsStr
     * @param publicKey
     */
    public static String RSADecoder(String paramsStr,String publicKeyStr,String privateKeyStr) {
    	PublicKey publicKey = str2Public(publicKeyStr) ;
    	PrivateKey privateKey  = str2Private(privateKeyStr);
    	RsaRawEncryptor rsaEncryptor = new RsaRawEncryptor(UTF8, publicKey, privateKey);
    	return rsaEncryptor.decrypt(paramsStr);
    }
    
    
    public static void main(String[] args) {

    	String encoryStr = ""; //"{\"userId\":\"29219dbdd34a4baca61dc42e12a4c665\",\"token\":\"453d18543e6b439d9a4a74a669ce04ab\"}";//zidGLhBNgb3COM68yeiN7WYWLSO6FZ";    	
    	Map<String, String> keysMap = getRsakey();
    	String publicStr ="";
    	String privateStr = "";
    	
    	String encyStr = RSAEncoderByPublic(encoryStr, publicStr);
    	System.out.println("加密字符串:"+encyStr);
    	String decoderStr = RSADecoder(encyStr, publicStr, privateStr); 
    	System.out.println("解密字符串:"+decoderStr);
    	
	}
}  
