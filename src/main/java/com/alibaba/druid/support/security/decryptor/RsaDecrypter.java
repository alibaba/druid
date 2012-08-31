package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.Base64;
import com.alibaba.druid.util.JdbcUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

/**
 * RSA 解密器， 支持证书
 *
 */
public class RsaDecrypter extends AbstractDecrypter {

    private static Log log = LogFactory.getLog(RsaDecrypter.class);

    public static final String KEY = "config.decrypt.key";
    public static final String KEY_FILE = "config.decrypt.keyFile";
    public static final String X509_FILE = "config.decrypt.x509File";

    protected static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";

    public String getId() {
        return "RSA";
    }

    /**
     * <pre>
     * 密钥 放在 <code>Properties</code> 参数中,
     * 如果是密钥字符串, key 是 config.decrypt.key
     * 如果是密钥文件, key 是 config.decrypt.keyFile
     * 如果是证书文件, key 是 config.decrypt.x509File
     *
     * 如果不指定密钥, 将使用默认密钥
     * </pre>
     * @param parameters 密文参数
     * @param info
     * @return
     * @throws DecryptException
     */
    public SensitiveParameters decrypt(SensitiveParameters parameters, Properties info) throws DecryptException {
        PublicKey publicKey = getPublicKey(info);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            String passwordPlainText = decrypt(cipher, parameters.getPassword());

            return new SensitiveParameters(parameters.getUrl(), parameters.getUsername(), passwordPlainText);
        } catch (DecryptException e) {
            throw e;
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt parameters", e);
        }
    }

    /**
     * 通过指定
     * @param info
     * @return
     */
    PublicKey getPublicKey(Properties info) {
        String key = null;
        String publicKeyFile = null;
        String x509File = null;

        if (info != null) {
            key = info.getProperty(KEY);
            publicKeyFile = info.getProperty(KEY_FILE);
            x509File = info.getProperty(X509_FILE);
        }

        if (publicKeyFile != null) {
            if (log.isDebugEnabled()) {
                log.debug("Decrypt by public key file");
            }
            return getPublicKeyByPublicKeyFile(publicKeyFile);
        }

        if (x509File != null) {
            if (log.isDebugEnabled()) {
                log.debug("Decrypt by X509 file");
            }
            return getPublicKeyByX509(x509File);
        }

        if (log.isDebugEnabled()) {
            log.debug("Decrypt by public key string");
        }

        return getPublicKeyByString(key);
    }

    PublicKey getPublicKeyByString(String publicKeyString) {
        if (publicKeyString == null || publicKeyString.length() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("Decrypt by default key");
            }
            publicKeyString = DEFAULT_PUBLIC_KEY_STRING;
        }

        try {
            byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyString);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        }
    }

    PublicKey getPublicKeyByPublicKeyFile(String publicKeyFile) {
        if (publicKeyFile == null || publicKeyFile.length() == 0) {
            return getPublicKeyByString(null);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(publicKeyFile);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = 0;
            byte[] b = new byte[512/8];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }

            byte[] publicKeyBytes = out.toByteArray();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            JdbcUtils.close(in);
        }
    }

    PublicKey getPublicKeyByX509(String x509File) {
        if (x509File == null || x509File.length() == 0) {
            return getPublicKeyByString(null);
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(x509File);

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cer = factory.generateCertificate(in);
            return cer.getPublicKey();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            JdbcUtils.close(in);
        }
    }
}
