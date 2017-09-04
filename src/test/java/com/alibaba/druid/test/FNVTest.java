package com.alibaba.druid.test;

import junit.framework.TestCase;

/**
 * Created by wenshao on 28/07/2017.
 */
public class FNVTest extends TestCase {
    public void test_fnv_32() throws Exception {
        System.out.println(" 0x811c9dc5 : " + 0x811c9dc5);
        long x = 0xcbf29ce484222325L;
        System.out.println(x);
        System.out.println(x << 2);

// 14695981039346656037
    }

    public long fnv(String text) {
        long hash = 0x811c9dc5;
        for (int i = 0, len = text.length(); i < len; ++i) {
            char ch = text.charAt(i);

            hash ^= ch;
            hash *= 0x1000193;
        }

        return hash;
    }

    public long fnv_64( String text) {
        long hash = 0xcbf29ce484222325L;
        return fnv_64(hash, text);
    }

    public long fnv_64(long hash, String text) {
        for (int i = 0, len = text.length(); i < len; ++i) {
            char ch = text.charAt(i);

            hash *= 0x100000001b3L;
            hash ^= ch;
        }

        return hash;
    }
}
