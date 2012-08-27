package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 解密器
 *
 * @author Jonas Yang
 */
public class AesDecrypter extends AbstractDecrypter {

    private final static int KEY_LENGTH = 16; //128

    private volatile SecretKeySpec spec;

    public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException {
        SecretKeySpec spec = getSpec();
        if (spec == null) {
            throw new DecryptException("No key.");
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);

            String passwordPlainText = decrypt(cipher, parameters.getPassword());

            return new SensitiveParameters(parameters.getUrl(), parameters.getUsername(), passwordPlainText);
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt JDBC password’", e);
        }
    }

    protected SecretKeySpec getSpec() {
        if (this.spec == null) {
            setKey("");
        }

        return this.spec;
    }

    public void setKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The key cannot be null");
        }

        if (key.length() < 16) {
            key = KEY_PADDING.substring(0, KEY_LENGTH - key.length()) + key;
        }

        this.spec = new SecretKeySpec(key.getBytes(), "AES");
    }

}
