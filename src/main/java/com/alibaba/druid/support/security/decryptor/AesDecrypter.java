package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Properties;

/**
 * <pre>
 * AES 解密器, 如果不指定密钥, 使用默认的密钥.
 * 通过一下方式指定密钥:
 * Properties info = new Properties();
 * info.put("config.decrypt.key", "密钥");
 *
 * String 明文 = new AesDecrypter().decrypt("密文", info);
 * </pre>
 *
 * @author Jonas Yang
 */
public class AesDecrypter extends AbstractDecrypter {
    private static Log log = LogFactory.getLog(AesDecrypter.class);

    private final static int KEY_LENGTH = 16; //128

    public String getId() {
        return "AES";
    }

    /**
     * 密钥 放在 <code>Properties</code> 参数中, key 是 config.decrypt.key, 如果不指定密钥, 将使用默认密钥
     * @param parameters 密文参数
     * @param info
     * @return
     * @throws DecryptException
     */
    public SensitiveParameters decrypt(SensitiveParameters parameters, Properties info) throws DecryptException {
        SecretKeySpec spec = getSpec(info);

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);

            String passwordPlainText = decrypt(cipher, parameters.getPassword());

            return new SensitiveParameters(parameters.getUrl(), parameters.getUsername(), passwordPlainText);
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt JDBC password’", e);
        }
    }

    SecretKeySpec getSpec(Properties info) {
        String key = null;

        if (info != null) {
            key = info.getProperty(KEY);
        }

        if (key == null) {
            if (log.isDebugEnabled()) {
                log.debug("Decrypt by default key");
            }
            key = "";
        }

        if (key.length() < 16) {
            key = KEY_PADDING.substring(0, KEY_LENGTH - key.length()) + key;
        }

        return new SecretKeySpec(key.getBytes(), "AES");
    }

}
