package com.alibaba.druid.support.security.tool;

import com.alibaba.druid.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Jonas Yang
 */
public class AesAction extends AbstractAction {

    public AesAction() {
        super(0, 16, "AES");
    }

    @Override
    public String getId() {
        return "AES";
    }

    protected String encrypt(String keyString, String plainString) throws Exception  {
        SecretKeySpec spec = new SecretKeySpec(keyString.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, spec);

        byte[] encryptedBytes = cipher.doFinal(plainString.getBytes());

        return Base64.byteArrayToBase64(encryptedBytes);
    }
}
