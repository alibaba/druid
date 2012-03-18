package com.alibaba.druid;

import java.util.LinkedHashMap;

import junit.framework.TestCase;

public class TestLRU extends TestCase {

    public void test_lru() throws Exception {
        LinkedHashMap<Integer, Object> cache = new LinkedHashMap<Integer, Object>(100, 0.75f, true);

        cache.put(2, "22");
        cache.put(3, "33");

        System.out.println(cache);

        cache.put(2, "22");

        System.out.println(cache);
        cache.get(3);
        System.out.println(cache);

    }
}
