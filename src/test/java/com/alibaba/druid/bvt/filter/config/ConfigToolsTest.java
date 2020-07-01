package com.alibaba.druid.bvt.filter.config;

import java.util.Random;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigTools;

public class ConfigToolsTest extends TestCase {

    public void test_0() throws Exception {
        String plainText = "abcdef";
        String cipherText = ConfigTools.encrypt(plainText);
        String decipherText = ConfigTools.decrypt(cipherText);

        Assert.assertEquals(plainText, decipherText);
    }

    public void test_genKeys() throws Exception {
        String plainText = Long.toString(new Random().nextLong());
        String[] keys = ConfigTools.genKeyPair(512);

        String cipherText = ConfigTools.encrypt(keys[0], plainText);
        Assert.assertEquals(plainText, ConfigTools.decrypt(keys[1], cipherText));
    }
    
    public void test_genKeys1024() throws Exception {
        String plainText = Long.toString(new Random().nextLong());
        String[] keys = ConfigTools.genKeyPair(1024);
        
        String cipherText = ConfigTools.encrypt(keys[0], plainText);
        Assert.assertEquals(plainText, ConfigTools.decrypt(keys[1], cipherText));
    }
}
