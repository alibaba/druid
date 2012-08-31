package com.alibaba.druid.bvt.filter.config;

import junit.framework.Assert;
import org.junit.Test;

import com.alibaba.druid.filter.config.security.decrypter.DecryptException;
import com.alibaba.druid.filter.config.security.decrypter.DecrypterFactory;
import com.alibaba.druid.filter.config.security.decrypter.RsaDecrypter;

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
