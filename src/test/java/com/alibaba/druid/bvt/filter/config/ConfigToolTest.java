/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
