package com.alibaba.druid.support.security.tool;

import com.alibaba.druid.util.Base64;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author Jonas Yang
 */
public class RsaAction implements Action {

    @Override
    public String getId() {
        return "RSA";
    }

    public void execute() {
        help();
    }

    private void help() {
        System.out.println();
        System.out.println("RSA 选项: ");
        System.out.println("1. 通过私钥加密");
        System.out.println("2. 创建 RSA 密钥");

        String input = System.console().readLine("[RSA]请输入选项: ");
        if (input == null || input.length() == 0) {
            System.err.println("不是一个合法的输入.");
        }

        if ("1".equals(input)) {
            encrypt();
            return;
        }

        if ("2".equals(input)) {
            generateKeys();
        }
    }

    private void encrypt() {
        String filePath = System.console().readLine("[RSA]请输入私钥文件路径: ");
        if (!new File(filePath).exists()) {
            System.err.println("文件不存在[" + filePath + "]");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            byte[] b = new byte[512/8];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
        } catch (Exception e) {
            System.err.println("读取文件出错. " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        byte[] key = out.toByteArray();
        String plainString = System.console().readLine("[RSA]请输入要需要加密的明文: ");

        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(spec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedBytes = cipher.doFinal(plainString.getBytes());
            String encryptedString = Base64.byteArrayToBase64(encryptedBytes);;

            System.out.println("请记住以下的密文, 长度为[" + encryptedString.length() + "].");
            System.out.println();
            System.out.println(encryptedString);
            System.out.println();
        } catch (Exception e) {
            System.err.println("加密错误. ");
            e.printStackTrace();
        }
    }

    private void generateKeys() {
        String input = System.console().readLine("[RSA]请输入输出路径: ");
        if (input == null) {
            input = "";
        }

        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024, new SecureRandom());
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

    private void saveKey(Key key, String path) throws Exception {
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