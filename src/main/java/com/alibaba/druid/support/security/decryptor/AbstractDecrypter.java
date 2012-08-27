package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.Decrypter;
import com.alibaba.druid.util.Base64;

import javax.crypto.Cipher;

/**
 * 加密抽象类
 *
 * @author Jonas Yang
 */
public abstract class AbstractDecrypter implements Decrypter {

    /**
     * DES, AES 密钥都有长度限制， 如果不够， 默认使用该字段补长
     */
    public static final String KEY_PADDING = "FOLLOW YOUR HEART. YOU CAN DO BEST THAN ANY ONE.";

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
