package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES 解密器
 *
 * @author Jonas Yang
 */
public class DesDecrypter extends AbstractDecrypter {

    private static final int KEY_LENGTH = 24;

    private volatile SecretKey key;

    public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException {
        SecretKey key = getKey();
        if (key == null) {
            throw new DecryptException("No key.");
        }

        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, this.key);

            String passwordPlainText = decrypt(cipher, parameters.getPassword());

            return new SensitiveParameters(parameters.getUrl(), parameters.getUsername(), passwordPlainText);
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt JDBC parameters", e);
        }
    }

    public void setKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The key cannot be null");
        }

        if (key.length() < 24) {
            key = KEY_PADDING.substring(0, KEY_LENGTH - key.length()) + key;
        }

        try {
            byte[] keyBytes = key.getBytes();
            DESKeySpec spec = new DESKeySpec(keyBytes);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            this.key = factory.generateSecret(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot get key.", e);
        }
    }

    protected SecretKey getKey() {
        return this.key;
    }
}
