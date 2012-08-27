package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Jonas Yang
 */
public class DesDecrypterTest {

    @Test
    public void testDecrypt() throws DecryptException {
        DesDecrypter decrypter = new DesDecrypter();
        decrypter.setKey("123456");
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", "rIsr38vJDyQ="));

        Assert.assertEquals("It is not same", parameters.getPassword(), "xiaoyu");
    }
}
