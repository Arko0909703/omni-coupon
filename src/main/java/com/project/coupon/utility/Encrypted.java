package com.project.coupon.utility;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import java.security.InvalidKeyException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import java.util.Base64;
@Service
public class Encrypted {
      private static final String SECRET_KEY="9818693055";
      private static final String SALTVALUE="abcdefghij";
	private static SecretKeyFactory SecretKeyFactory;
      public static String encrypt(String strToEncrypt) {
    	  try {
    		  byte []iv= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    		  IvParameterSpec ivspec=new IvParameterSpec(iv);
    		  SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    		  PBEKeySpec spec=new PBEKeySpec(SECRET_KEY.toCharArray(),SALTVALUE.getBytes(),65536,256);
    		  SecretKey tmp=factory.generateSecret(spec);
    		  SecretKeySpec secretKey=new SecretKeySpec(tmp.getEncoded(),"AES");
    		  Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
    		  cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivspec);
    		  return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    	  }
    	  catch(InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
    		  System.out.println("Error occurred during encryption:"+e.toString());
    		  //return null;
    	  }
    	  return null;
      }
    	  public static String decrypt(String strtoDecrypt) {
    		  try {
    			  byte []iv= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    			  IvParameterSpec ivspec=new IvParameterSpec(iv);
    			  SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    			  PBEKeySpec spec=new PBEKeySpec(SECRET_KEY.toCharArray(),SALTVALUE.getBytes(),65536,256);
    			  SecretKey tmp=factory.generateSecret(spec);
    			  SecretKeySpec secretKey=new SecretKeySpec(tmp.getEncoded(),"AES");
    			  Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
    			  cipher.init(Cipher.DECRYPT_MODE, secretKey,ivspec);
    			  return new String(cipher.doFinal(Base64.getDecoder().decode(strtoDecrypt)));
    		  }
    		  catch(InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e){
    			  System.out.println("Error occurred during decryption:"+e.toString());
    		  }
    		  return null;
    		  }
    	 
      
}