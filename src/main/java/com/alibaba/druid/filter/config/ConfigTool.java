package com.alibaba.druid.filter.config;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Cipher;

import com.alibaba.druid.util.Base64;

public class ConfigTool {
    public final static int DEFAULT_KEY_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            return;
        }

        String command = args[0];

        if ("-gen".equals(command)) {
            int keySize = DEFAULT_KEY_SIZE;

            if (args.length > 1) {
                keySize = Integer.parseInt(args[1]);
            }

            Map<String, String> result = gen(keySize);
            print(result);

            return;
        }

        if ("-encrypt".equals(command)) {
            String privateKey = args[1];
            String text = args[2];

            Map<String, String> result = encrypt(privateKey, text);
            print(result);

            return;
        }
        
        if ("-decrypt".equals(command)) {
            String publicKey = args[1];
            String text = args[2];
            
            Map<String, String> result = decrypt(publicKey, text);
            print(result);
            
            return;
        }
    }

    public static void print(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.print(entry.getKey());
            System.out.print(" : ");
            System.out.println();
            System.out.print(entry.getValue());
            System.out.println();
            System.out.println();
        }
    }

    public static Map<String, String> decrypt(String publicKeyString, String cipherText) throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();

        byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyString);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        
        byte[] cipherBytes = Base64.base64ToByteArray(cipherText);
        byte[] plainBytes = cipher.doFinal(cipherBytes);
        String plainText = new String(plainBytes);        
        
        map.put("result", plainText);

        return map;
    }

    public static Map<String, String> encrypt(String privateKeyString, String plain) throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();

        byte[] privateKeyBytes = Base64.base64ToByteArray(privateKeyString);
        PKCS8EncodedKeySpec x509KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(x509KeySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] plainBytes = plain.getBytes();
        byte[] cipherBytes = cipher.doFinal(plainBytes);

        String cipherText = Base64.byteArrayToBase64(cipherBytes);

        map.put("result", cipherText);

        return map;
    }

    public static Map<String, String> gen(int keySize) throws Exception {
        Map<String, String> map = new LinkedHashMap<String, String>();

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(512, new SecureRandom());

        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        map.put("privateKey", Base64.byteArrayToBase64(privateKey.getEncoded()));
        map.put("publicKey", Base64.byteArrayToBase64(publicKey.getEncoded()));

        return map;
    }
}
