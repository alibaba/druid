package com.alibaba.druid.filter.config.impl;

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Jonas Yang
 */
public class HttpConfigLoaderTest {

    @Test
    public void testIsSupported() {
        HttpConfigLoader configLoader = new HttpConfigLoader();
        boolean isSupported = configLoader.isSupported(HttpConfigLoader.PROTOCOL_PREFIX + "www.5-nb.com");
        Assert.assertTrue("The value is " + isSupported + ", not true", isSupported);

        isSupported = configLoader.isSupported("jdbc:/opt/mytest");
        Assert.assertFalse("The value is " + isSupported + ", not false", isSupported);
    }

    @Test
    public void testGetUrl() throws MalformedURLException {
        String protocol = HttpConfigLoader.PROTOCOL_PREFIX + "www.5-nb.com";
        HttpConfigLoader configLoader = new HttpConfigLoader();
        URL url = configLoader.getUrl(protocol);
        Assert.assertEquals("The url is " + url, "http://www.5-nb.com", url.toString());
    }
}
