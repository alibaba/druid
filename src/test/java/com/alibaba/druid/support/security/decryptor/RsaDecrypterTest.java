package com.alibaba.druid.support.security.decryptor;

import com.alibaba.druid.pool.DecryptException;
import com.alibaba.druid.pool.SensitiveParameters;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Jonas Yang
 */
public class RsaDecrypterTest {
    String encryptedString = "Zaa9qpyB9snnBUz7UBvcaHcNfWMKEWvhnx0dbWmwxBOJ9vkeZNXjV9MEJezSzL+6yQh7aO29vD67" +
            "qedtD9ublVwwf+jTPuO1NjzAKy0+yWVQ1KdR2KaQuvCxFWiM1n6nbpkUGB6OVpoQgFnFzdKlyC9D" +
            "KU3Q6evGa1hlZ8jnHcA=";

    @Test
    public void testDecrypt() throws DecryptException {
        RsaDecrypter decrypter = new RsaDecrypter();
        decrypter.setPublicKeyFile("/Users/yangkingsel/IdeaProjects/HelloWorld/out/production/HelloWorld/a.public.key");
        SensitiveParameters parameters = decrypter.decrypt(new SensitiveParameters("", "", encryptedString));

        Assert.assertEquals("It is not same.", "xiaoyu", parameters.getPassword());
    }
}
