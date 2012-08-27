package com.alibaba.druid.support.security.tool;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author Jonas Yang
 */
public class DesAction extends AbstractAction {

    public DesAction() {
        super(6, 24, "DES");
    }

    @Override
    public String getId() {
        return "DES";
    }

    public String encrypt(String keyString, String plainString) throws Exception {
        DESKeySpec spec = new DESKeySpec(keyString.getBytes());

        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey key = factory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(plainString.getBytes());

        return new BASE64Encoder().encode(encryptedBytes);
    }
}
