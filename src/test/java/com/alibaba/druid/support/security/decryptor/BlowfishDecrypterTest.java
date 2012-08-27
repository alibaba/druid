package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Jonas Yang
 */
public class BlowfishDecrypterTest {

    @Test
    public void testDecrypt() throws DecryptException {
        BlowfishDecrypter decrypter = new BlowfishDecrypter();
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", "64c5fd2979a86168"));

        Assert.assertEquals("It is not same.", "123456", parameters.getPassword());
    }
}
