package com.alibaba.druid.filter.config.security.decrypter;

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

        Assert.assertNull(DecrypterFactory.getDecrypter("ajfdjksfklds;"));
    }
}
