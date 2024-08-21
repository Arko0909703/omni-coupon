package com.project.coupon.authservice.configs;



import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
@Configuration
public class AESKeyGenerator {
    public static void main(String[] args) throws Exception {
        // Create a KeyGenerator instance for AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Specify the key size

        // Generate the secret key
        SecretKey secretKey = keyGen.generateKey();

        // Get the encoded form of the key
        byte[] encodedKey = secretKey.getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(encodedKey);

        // Print the key in Base64 format
        System.out.println("Generated AES-256 Key: " + base64Key);
    }
}