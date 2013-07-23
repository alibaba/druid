package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.util.ConcurrentIdentityHashMap;

public class ConcurrentIdentityHashMapTest2 extends TestCase {

    public void test_0() throws Exception {
        ConcurrentIdentityHashMap<Integer, Object> map = new ConcurrentIdentityHashMap<Integer, Object>();
        map.put(Integer.valueOf(1), "");
        Assert.assertEquals(map.get(Integer.valueOf(1)), "");
        Assert.assertEquals(map.get(new Integer(1)), null);

        Assert.assertTrue(map.containsValue(""));

        {
            Exception error = null;
            try {
                map.containsValue(null);
            } catch (NullPointerException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                map.put(Integer.valueOf(1), null);
            } catch (NullPointerException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                map.putIfAbsent(Integer.valueOf(1), null);
            } catch (NullPointerException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        map.put(Integer.valueOf(1), "123");
        Assert.assertEquals(map.get(Integer.valueOf(1)), "123");
        Assert.assertEquals(map.get(new Integer(1)), null);

        map.putIfAbsent(Integer.valueOf(1), "234");
        Assert.assertEquals(map.get(Integer.valueOf(1)), "123");
        Assert.assertEquals(map.get(new Integer(1)), null);

        map.remove(new Integer(1));
        Assert.assertEquals(map.get(Integer.valueOf(1)), "123");
        Assert.assertFalse(map.isEmpty());

        map.remove(Integer.valueOf(1));
        Assert.assertEquals(map.get(Integer.valueOf(1)), null);
        Assert.assertTrue(map.isEmpty());
    }
}
