package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Jonas Yang
 */
public class AesDecrypterTest {

    @Test
    public void testDecrypt() throws DecryptException {
        AesDecrypter decrypter = new AesDecrypter();
        decrypter.setKey("123456");
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", "0UNXTyp/r/wdm+b5FAY1Jw=="));

        Assert.assertEquals("It is not same.", "xiaoyu", parameters.getPassword());
    }
}
