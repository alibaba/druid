package com.alibaba.druid.support.security.decryptor;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Jonas Yang
 */
public class DecrypterFactoryTest {

    @Test
    public void testGetDecrypter() throws DecryptException {
        Assert.assertNotNull(DecrypterFactory.getDecrypter("RSA"));
        Assert.assertEquals(DecrypterFactory.getDecrypter("RSA").getClass(), RsaDecrypter.class);

        Assert.assertNotNull(DecrypterFactory.getDecrypter("AES"));
        Assert.assertEquals(DecrypterFactory.getDecrypter("AES").getClass(), AesDecrypter.class);

        Assert.assertNull(DecrypterFactory.getDecrypter("ajfdjksfklds;"));
    }
}
