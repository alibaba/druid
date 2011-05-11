/**
 * Copyright 1999-2011 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.druid.bvt.proxy.filter.encoding;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.encoding.CharsetParameter;

/**
 * @author gang.su
 */
public class CharsetParameterTest extends TestCase {

    public void testQ() {
        CharsetParameter c = new CharsetParameter();
        c.setClientEncoding("1");
        c.setServerEncoding("2");
        Assert.assertEquals("1", c.getClientEncoding());
        Assert.assertEquals("2", c.getServerEncoding());

    }
}
