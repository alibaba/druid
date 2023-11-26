package com.alibaba.druid.util;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author shenjianeng [ishenjianeng@qq.com]
 */
public class ConcurrentLruCacheTest extends TestCase {


    public void testConcurrentLruCache() {
        ConcurrentLruCache<String, String> cache = new ConcurrentLruCache<>(2);
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals("k1value", cache.computeIfAbsent("k1", key -> key + "value"));


        Assert.assertEquals(1, cache.size());
        Assert.assertTrue(cache.contains("k1"));

        Assert.assertEquals("k2value", cache.computeIfAbsent("k2", key -> key + "value"));
        Assert.assertEquals(2, cache.size());
        Assert.assertTrue(cache.contains("k1"));
        Assert.assertTrue(cache.contains("k2"));

        Assert.assertEquals("k1value", cache.get("k1"));
        Assert.assertEquals("k2value", cache.get("k2"));


        Assert.assertEquals("k3value", cache.computeIfAbsent("k3", key -> key + "value"));

        Assert.assertEquals(2, cache.size());
        Assert.assertEquals(2, cache.keys().size());

        Assert.assertFalse(cache.contains("k1"));
        Assert.assertTrue(cache.contains("k2"));
        Assert.assertTrue(cache.contains("k3"));


        cache.clear();
        Assert.assertEquals(0, cache.size());

    }
}
