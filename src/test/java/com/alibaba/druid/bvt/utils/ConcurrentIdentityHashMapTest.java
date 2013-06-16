package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.util.ConcurrentIdentityHashMap;

public class ConcurrentIdentityHashMapTest extends TestCase {

    public void test_0() throws Exception {
        ConcurrentIdentityHashMap<Integer, String> map = new ConcurrentIdentityHashMap<Integer, String>();

        Integer v1 = new Integer(3);
        map.put(v1, "3");

        Assert.assertTrue(map.contains("3"));
        Assert.assertTrue(map.containsKey(v1));

        Assert.assertFalse(map.contains("4"));
        Assert.assertFalse(map.containsKey(new Integer(3)));

        map.put(v1, "33");
        Assert.assertFalse(map.contains("3"));
        Assert.assertTrue(map.contains("33"));

        Assert.assertEquals(1, map.size());
        
        map.clear();
        Assert.assertEquals(0, map.size());
    }
}
