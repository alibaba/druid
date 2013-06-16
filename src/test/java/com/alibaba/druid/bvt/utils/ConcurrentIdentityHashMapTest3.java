package com.alibaba.druid.bvt.utils;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.util.ConcurrentIdentityHashMap;

public class ConcurrentIdentityHashMapTest3 extends TestCase {

    public void test_0() throws Exception {
        ConcurrentIdentityHashMap<Integer, Object> map = new ConcurrentIdentityHashMap<Integer, Object>(16, 0.75f, 2);

        Integer[] keys = new Integer[10000];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = i;
        }
        for (int i = 0; i < keys.length; ++i) {
            map.put(keys[i], "");
        }
        Assert.assertEquals(keys.length, map.size());
        for (int i = 0; i < keys.length; ++i) {
            map.put(keys[i], "");
        }
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            entry.getKey();
            entry.getValue();
        }
        Assert.assertEquals(keys.length, map.size());

        for (int i = 0; i < keys.length; ++i) {
            map.replace(keys[i], "", "a");
        }
        for (int i = 0; i < keys.length; ++i) {
            map.replace(keys[i], "", "b");
        }
        for (int i = 0; i < keys.length; ++i) {
            map.replace(keys[i], "");
        }
        Assert.assertEquals(keys.length, map.size());

        map.keys();
        map.keys().hasMoreElements();
        map.keys().nextElement();

        for (int i = 0; i < keys.length; ++i) {
            map.remove(keys[i]);
        }
        Assert.assertEquals(0, map.size());

        map.keys();
        map.entrySet();
        map.size();
        map.clear();
        map.isEmpty();

        map.entrySet().size();
        map.entrySet().isEmpty();
        map.entrySet().remove(null);
        map.entrySet().clear();
        map.entrySet().contains("");

        map.values().size();
        map.values().isEmpty();
        map.values().remove(null);
        map.values().clear();
        map.values().contains("");

        map.keySet().size();
        map.keySet().isEmpty();
        map.keySet().remove(null);
        map.keySet().clear();
        map.keySet().contains("");

    }
}
