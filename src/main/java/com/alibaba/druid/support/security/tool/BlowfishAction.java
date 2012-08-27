package com.alibaba.druid.support.security.tool;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

/**
 * @author Jonas Yang
 */
public class BlowfishAction implements Action {

    @Override
    public String getId() {
        return "Blowfish";
    }

    public void execute() {
        System.out.println();
        String plainString = System.console().readLine("[Blowfish]请输入要需要加密的明文: ");
        if (plainString == null) {
            System.err.println("输入不能为空.");
            return;
        }

        try {
            String encryptedString = encrypt(plainString);
            System.out.println("请记住以下的密文, 长度为[" + encryptedString.length() + "].");
            System.out.println();
            System.out.println(encryptedString);
            System.out.println();
        } catch (Exception e) {
            System.out.println("加密出错.");
            e.printStackTrace();
        }
    }

    private String encrypt(String plainString) throws Exception {
        byte[] kbytes = "jaas is the way".getBytes();
        SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encoding = cipher.doFinal(plainString.getBytes());
        BigInteger n = new BigInteger(encoding);
        return n.toString(16);
    }
}
