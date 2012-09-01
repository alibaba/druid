package com.alibaba.druid.filter.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.alibaba.druid.util.Base64;
import com.alibaba.druid.util.JdbcUtils;

public class ConfigTools {

    private static final String DEFAULT_PRIVATE_KEY_STRING = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAocbCrurZGbC5GArEHKlAfDSZi7gFBnd4yxOt0rwTqKBFzGyhtQLu5PRKjEiOXVa95aeIIBJ6OhC2f8FjqFUpawIDAQABAkAPejKaBYHrwUqUEEOe8lpnB6lBAsQIUFnQI/vXU4MV+MhIzW0BLVZCiarIQqUXeOhThVWXKFt8GxCykrrUsQ6BAiEA4vMVxEHBovz1di3aozzFvSMdsjTcYRRo82hS5Ru2/OECIQC2fAPoXixVTVY7bNMeuxCP4954ZkXp7fEPDINCjcQDywIgcc8XLkkPcs3Jxk7uYofaXaPbg39wuJpEmzPIxi3k0OECIGubmdpOnin3HuCP/bbjbJLNNoUdGiEmFL5hDI4UdwAdAiEAtcAwbm08bKN7pwwvyqaCBC//VnEWaq39DCzxr+Z2EIk=";
    public static final String  DEFAULT_PUBLIC_KEY_STRING  = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";

    public static void main(String[] args) {
        System.out.println("**************************************");
        System.out.println("*                                    *");
        System.out.println("*           Druid 加密工具           *");
        System.out.println("*                                    *");
        System.out.println("**************************************");

        System.out.println();
        System.out.println("RSA 选项: ");
        System.out.println("1. 通过私钥加密");
        System.out.println("2. 创建 RSA 密钥");
        System.out.println("3. 退出");
        System.out.println("");

        String input = System.console().readLine("[RSA]请输入选项: ");
        input = input.toLowerCase();

        if ("1".equals(input)) {
            encrypt();
        } else if ("2".equals(input)) {
            generateKeys();
        } else if ("q".equals(input) || "3".equals(input)) {
            System.exit(0);
        } else {
            System.err.println("不是一个合法的输入.");
        }
    }
    
    public static String decrypt(String cipherText) throws Exception {
        return decrypt((String) null, cipherText);
    }
    
    public static String decrypt(String publicKeyText, String cipherText) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyText);
        
        return decrypt(publicKey, cipherText);
    }
    
    public static PublicKey getPublicKeyByX509(String x509File) {
        if (x509File == null || x509File.length() == 0) {
            return ConfigTools.getPublicKey(null);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(x509File);

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cer = factory.generateCertificate(in);
            return cer.getPublicKey();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            JdbcUtils.close(in);
        }
    }
    
    public static PublicKey getPublicKey(String publicKeyText) {
        if (publicKeyText == null || publicKeyText.length() == 0) {
            publicKeyText = ConfigTools.DEFAULT_PUBLIC_KEY_STRING;
        }

        try {
            byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyText);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        }
    }
    
    public static PublicKey getPublicKeyByPublicKeyFile(String publicKeyFile) {
        if (publicKeyFile == null || publicKeyFile.length() == 0) {
            return ConfigTools.getPublicKey(null);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(publicKeyFile);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = 0;
            byte[] b = new byte[512 / 8];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }

            byte[] publicKeyBytes = out.toByteArray();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            JdbcUtils.close(in);
        }
    }
    
    public static String decrypt(PublicKey publicKey, String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        
        if (cipherText == null || cipherText.length() == 0) {
            return cipherText;
        }

        byte[] cipherBytes = Base64.base64ToByteArray(cipherText);
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return new String(plainBytes);
    }

    private static void encrypt() {
        System.out.println("注意: 如果输入的不是该工具生成的私钥, 请转成PKCS#8格式, 使用下面命令");
        System.out.println("openssl pkcs8 -topk8 -inform PEM -outform DER -in 原来的Key -nocrypt > 输出文件");
        String filePath = System.console().readLine("[RSA]请输入私钥文件路径[如果直接回车将使用默认私钥]: ");

        byte[] key = null;
        if (filePath == null || filePath.length() == 0) {
            key = Base64.base64ToByteArray(DEFAULT_PRIVATE_KEY_STRING);
        } else {
            try {
                key = getPrivateKeyFromFile(filePath);
            } catch (Exception e) {
                System.err.println("读取文件出错. " + e.getMessage());
                return;
            }
        }

        String plainString = System.console().readLine("[RSA]请输入要需要加密的明文: ");

        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(spec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedBytes = cipher.doFinal(plainString.getBytes());
            String encryptedString = Base64.byteArrayToBase64(encryptedBytes);

            System.out.println("请记住以下的密文, 长度为[" + encryptedString.length() + "].");
            System.out.println();
            System.out.println(encryptedString);
            System.out.println();
        } catch (Exception e) {
            System.err.println("加密错误. ");
            e.printStackTrace();
        }
    }
    
    public static String encrypt(String plainText) throws Exception {
        return encrypt((String) null, plainText);
    }
    
    public static String encrypt(String key, String plainText) throws Exception {
        if (key == null) {
            key = DEFAULT_PRIVATE_KEY_STRING;
        }
        
        byte[] keyBytes = Base64.base64ToByteArray(key);
        return encrypt(keyBytes, plainText);
    }
    
    public static String encrypt(byte[] keyBytes, String plainText) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = factory.generatePrivate(spec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        String encryptedString = Base64.byteArrayToBase64(encryptedBytes);
        
        return encryptedString;
    }

    private static byte[] getPrivateKeyFromFile(String filePath) throws IOException {
        if (!new File(filePath).exists()) {
            System.err.println("文件不存在[" + filePath + "]");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            byte[] b = new byte[512 / 8];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return out.toByteArray();
    }

    private static void generateKeys() {
        System.out.println();
        System.out.println("[RSA]请选择密钥长度: \n1. 512 \n2. 1024 \n3. 2048");
        String keySizeString = System.console().readLine("[RSA]请输入选项: ");

        int keySize = 512;
        if ("1".equals(keySizeString) || "2".equals(keySizeString) || "3".equals(keySizeString)) {
            int power = (Integer.valueOf(keySizeString) - 1);
            keySize = keySize << power;
        } else {
            System.err.println("不是一个合法的输入.");
            return;
        }

        String input = System.console().readLine("[RSA]请输入输出路径: ");
        if (input == null) {
            input = "";
        }

        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(keySize, new SecureRandom());
            KeyPair pair = gen.generateKeyPair();

            String privateKeyPath = input + ".private.key";
            saveKey(pair.getPrivate(), privateKeyPath);

            String publicKeyPath = input + ".public.key";
            saveKey(pair.getPublic(), publicKeyPath);

            System.out.println("Private Key: " + privateKeyPath);
            System.out.println("public  Key: " + publicKeyPath);
            System.out.println();
        } catch (Exception e) {
            System.err.println("[RSA]创建 RSA 密钥出错. " + e.getMessage());
        }

    }

    private static void saveKey(Key key, String path) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(key.getEncoded());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
