package com.alibaba.druid;

import junit.framework.TestCase;

public class TestHistogram extends TestCase {

    public void test_histogram() throws Exception {
        int val = 4;
        for (int i = 0; i < 10; ++i) {
            val *= 4;
            System.out.println(val);
        }
    }
}
