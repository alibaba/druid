/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.proxy.filter.encoding;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.encoding.CharsetConvert;

/**
 * @author gang.su
 */
public class CharsetConvertTest extends TestCase {

    private final static String CLIENT_ENCODEING = "gbk";
    private final static String SERVER_ENCODEING = "utf-8";
    public CharsetConvert       charsetConvert   = new CharsetConvert(CLIENT_ENCODEING, SERVER_ENCODEING);

    public void testIsEmpty() {
        Assert.assertTrue(charsetConvert.isEmpty(null));
        Assert.assertTrue(charsetConvert.isEmpty(""));
        Assert.assertTrue(!charsetConvert.isEmpty("a"));
    }

    public void testEncoding() {
        String s = "你好";
        String es = "";
        String ds = "";
        try {
            es = new String(s.getBytes(CLIENT_ENCODEING), SERVER_ENCODEING);
            ds = new String(s.getBytes(SERVER_ENCODEING), CLIENT_ENCODEING);

            Assert.assertEquals(es, charsetConvert.encode(s));
            Assert.assertEquals(ds, charsetConvert.decode(s));
        } catch (UnsupportedEncodingException e) {
        }

    }
}
