package com.alibaba.druid.bvt.filter.config;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigTools;

public class ConfigToolsTest extends TestCase {

    public void test_0() throws Exception {
        String plainText = "abcdef";
        String cipherText = ConfigTools.encrypt(plainText);
        String decipherText = ConfigTools.decrypt(cipherText);
        
        Assert.assertEquals(plainText, decipherText);
    }
}
