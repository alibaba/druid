package com.alibaba.druid.util;

import junit.framework.TestCase;
import org.junit.Assert;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author: ruansheng
 * @date: 2024-01-22
 */
public class MySqlUtilsTest extends TestCase {

    public void testParseMillis() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> MySqlUtils.parseMillis(null, TimeZone.getTimeZone("GMT+8")));
    }

    public void testParseDateTime() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> MySqlUtils.parseDateTime(null, 0, 0, ZoneId.systemDefault()));
    }
}