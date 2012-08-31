package com.alibaba.druid.support.security.decryptor;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Jonas Yang
 */
public class AesDecrypterTest {

    @Test
    public void testDecrypt() throws DecryptException {
        AesDecrypter decrypter = new AesDecrypter();
        Properties info = new Properties();
        info.setProperty(AesDecrypter.KEY, "123456");
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", "0UNXTyp/r/wdm+b5FAY1Jw=="), info);

        Assert.assertEquals("It is not same.", "xiaoyu", parameters.getPassword());
    }

    @Test
    public void testDecryptWithEmptyKey() throws DecryptException {
        AesDecrypter decrypter = new AesDecrypter();
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", "AzgdbVU3SIK6A8+0qg+Btg=="), null);

        Assert.assertEquals("It is not same.", "xiaoyu", parameters.getPassword());
    }
}
