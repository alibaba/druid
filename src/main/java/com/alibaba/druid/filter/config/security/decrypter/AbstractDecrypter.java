package com.alibaba.druid.filter.config.security.decrypter;

import com.alibaba.druid.util.Base64;

import javax.crypto.Cipher;

/**
 * 加密抽象类
 *
 * @author Jonas Yang
 */
public abstract class AbstractDecrypter implements Decrypter {

    public final static String KEY = "config.decrypt.key";

    /**
     * 传入密文， 返回明文
     * @param cipher 解密器
     * @param cipherString 密文
     * @return
     * @throws Exception
     */
    protected String decrypt(Cipher cipher, String cipherString) throws Exception {
        if (cipherString == null || cipherString.length() == 0) {
            return cipherString;
        }

        byte[] cipherBytes = Base64.base64ToByteArray(cipherString);
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return new String(plainBytes);
    }
}
