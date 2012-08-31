package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.util.Base64;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.security.PublicKey;
import java.util.Properties;

/**
 * @author Jonas Yang
 */
public class RsaDecrypterTest {
    String publicKeyContent = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2YdTlkEw7JfRNItCosRf4QO8vOL54ZEdj0LZm" +
            "tKhNFnNRTtHrLDFT44BPYqFSLVv3TOhLxJcEs31kyQhXEC5b9ozpZBvYrJDSt8QQMoYDAMLSojs+" +
            "K4vTJavgbRKKads0di+DuoMmJ5g5dcbDNd1Kb4kgWhrZtUwhfSjQklaiIwIDAQAB";

    String encryptedString = "Zaa9qpyB9snnBUz7UBvcaHcNfWMKEWvhnx0dbWmwxBOJ9vkeZNXjV9MEJezSzL+6yQh7aO29vD67" +
            "qedtD9ublVwwf+jTPuO1NjzAKy0+yWVQ1KdR2KaQuvCxFWiM1n6nbpkUGB6OVpoQgFnFzdKlyC9D" +
            "KU3Q6evGa1hlZ8jnHcA=";

    String keyFilePath;

    @Before
    public void setUp() throws IOException {
        File tmp = File.createTempFile("druid_public_key", Long.toString(System.currentTimeMillis()));
        byte[] keyContent = Base64.base64ToByteArray(this.publicKeyContent);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tmp);
            out.write(keyContent);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        this.keyFilePath = tmp.getAbsolutePath();
    }

    @After
    public void tearDown() {
        File tmp = new File(this.keyFilePath);
        if (tmp.exists()) {
            tmp.delete();
        }

        this.keyFilePath = null;
    }

    @Test
    public void testGetPublicKeyByString() {
        RsaDecrypter rsaDecrypter = new RsaDecrypter();
        PublicKey key = rsaDecrypter.getPublicKeyByString(this.publicKeyContent);
        Assert.assertNotNull("It is null", key);

        key = rsaDecrypter.getPublicKeyByString(null);
        Assert.assertNotNull("It is null", key);
        Assert.assertEquals(RsaDecrypter.DEFAULT_PUBLIC_KEY_STRING, Base64.byteArrayToBase64(key.getEncoded()));

        try {
            key = rsaDecrypter.getPublicKeyByString("abcdef");
            Assert.assertTrue("It is here", false);
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetPublicKeyByPublicKeyFile() {
        RsaDecrypter rsaDecrypter = new RsaDecrypter();
        PublicKey key = rsaDecrypter.getPublicKeyByPublicKeyFile(this.keyFilePath);
        Assert.assertNotNull("It is null", key);

        key = rsaDecrypter.getPublicKeyByPublicKeyFile(null);
        Assert.assertNotNull("It is null", key);
        Assert.assertEquals(RsaDecrypter.DEFAULT_PUBLIC_KEY_STRING, Base64.byteArrayToBase64(key.getEncoded()));

        try {
            key = rsaDecrypter.getPublicKeyByPublicKeyFile("abcdef");
            Assert.assertTrue("It is here", false);
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetPublicKeyByX509File() {
        //No test
    }

    @Test
    public void testGetPublicKey() {
        RsaDecrypter rsaDecrypter = new RsaDecrypter();
        PublicKey key = rsaDecrypter.getPublicKey(null);
        Assert.assertNotNull("It is null", key);
        Assert.assertEquals(RsaDecrypter.DEFAULT_PUBLIC_KEY_STRING, Base64.byteArrayToBase64(key.getEncoded()));

        Properties info = new Properties();
        info.put(RsaDecrypter.KEY, this.publicKeyContent);
        key = rsaDecrypter.getPublicKey(info);
        Assert.assertNotNull("It is null", key);
        Assert.assertEquals(this.publicKeyContent, Base64.byteArrayToBase64(key.getEncoded()));

        info = new Properties();
        info.put(RsaDecrypter.KEY_FILE, this.keyFilePath);
        key = rsaDecrypter.getPublicKey(info);
        Assert.assertNotNull("It is null", key);
        Assert.assertEquals(this.publicKeyContent, Base64.byteArrayToBase64(key.getEncoded()));
    }

    @Test
    public void testDecrypt() throws DecryptException {
        RsaDecrypter decrypter = new RsaDecrypter();
        Properties info = new Properties();
        info.put(RsaDecrypter.KEY_FILE, this.keyFilePath);
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", encryptedString), info);

        Assert.assertEquals("It is not same.", "xiaoyu", parameters.getPassword());
    }

    @Test
    public void testDecryptWithEmptyKey() throws DecryptException {
        RsaDecrypter decrypter = new RsaDecrypter();
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", "OJfUm6WCHi7EuXqE6aEc+Po2xFrAGBeSNy8O2jWhV2FTG8/5kbRRr2rjNKhptlevm/03Y0048P7h88gdUOXAYg=="), null);

        Assert.assertEquals("It is not same.", "xiaoyu", parameters.getPassword());
    }

}
