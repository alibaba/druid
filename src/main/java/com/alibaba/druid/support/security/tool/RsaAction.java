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

    private static final String DEFAULT_PRIVATE_KEY_STRING = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAocbCrurZGbC5GArEHKlAfDSZi7gFBnd4yxOt0rwTqKBFzGyhtQLu5PRKjEiOXVa95aeIIBJ6OhC2f8FjqFUpawIDAQABAkAPejKaBYHrwUqUEEOe8lpnB6lBAsQIUFnQI/vXU4MV+MhIzW0BLVZCiarIQqUXeOhThVWXKFt8GxCykrrUsQ6BAiEA4vMVxEHBovz1di3aozzFvSMdsjTcYRRo82hS5Ru2/OECIQC2fAPoXixVTVY7bNMeuxCP4954ZkXp7fEPDINCjcQDywIgcc8XLkkPcs3Jxk7uYofaXaPbg39wuJpEmzPIxi3k0OECIGubmdpOnin3HuCP/bbjbJLNNoUdGiEmFL5hDI4UdwAdAiEAtcAwbm08bKN7pwwvyqaCBC//VnEWaq39DCzxr+Z2EIk=";

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

        if ("1".equals(input)) {
            encrypt();
        } else if ("2".equals(input)) {
            generateKeys();
        } else {
            System.err.println("不是一个合法的输入.");
        }
    }

    private void encrypt() {
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

    private byte[] getPrivateKeyFromFile(String filePath) throws IOException {
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

    private void generateKeys() {
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