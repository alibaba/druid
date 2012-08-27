package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 解密器， 支持证书
 *
 */
public class RsaDecrypter extends AbstractDecrypter {

    private static Log log = LogFactory.getLog(RsaDecrypter.class);

    private static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";

    private volatile PublicKey publicKey;

    public SensitiveParameters decrypt(SensitiveParameters parameters) throws DecryptException {
        PublicKey publicKey = getPublicKey();

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            String passwordPlainText = decrypt(cipher, parameters.getPassword());

            return new SensitiveParameters(parameters.getUrl(), parameters.getUsername(), passwordPlainText);
        } catch (DecryptException e) {
            throw e;
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt JDBC parameters", e);
        }
    }

    protected PublicKey getPublicKey() {
        if (this.publicKey == null) {
            setPublicKeyString(DEFAULT_PUBLIC_KEY_STRING);
        }

        return this.publicKey;
    }

    public void setPublicKeyString(String publicKeyString) {
        if (publicKeyString == null) {
            throw new IllegalArgumentException("The publicKeyString cannot be null");
        }

        try {
            byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyString);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        }
    }

    public void setPublicKeyFile(String publicKeyFile) {
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
            this.publicKey = factory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {}
            }
        }
    }

    public void setX509File(String x509File) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(x509File);

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate cer = factory.generateCertificate(in);
            this.publicKey = cer.getPublicKey();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("Cannot close file [" + x509File + "] in initialized public key.");
                }
            }
        }
    }
}
