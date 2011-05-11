/**
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.druid.bvt.proxy.filter.encoding;

import java.io.UnsupportedEncodingException;

import junit.framework.Assert;
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
