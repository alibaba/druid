package com.alibaba.druid.bvt.filter.config;

import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigTool;

public class ConfigToolTest extends TestCase {

    public void test_configTool() throws Exception {
        Map<String, String> result = ConfigTool.gen(1024);

        String publicKey = result.get("publicKey");
        String privateKey = result.get("privateKey");

        System.out.println("publicKey : " + publicKey);
        System.out.println("privateKey : " + privateKey);

        String cipher = ConfigTool.encrypt(privateKey, "abcdef").get("result");
        System.out.println("cipher : " + cipher);

        String plain = ConfigTool.decrypt(publicKey, cipher).get("result");
        System.out.println("plain : " + plain);
        
        Assert.assertEquals("abcdef", plain);
    }
}
