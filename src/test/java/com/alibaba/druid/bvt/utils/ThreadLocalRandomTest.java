package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import com.alibaba.druid.util.ThreadLocalRandom;

public class ThreadLocalRandomTest extends TestCase {
    public void test_random() throws Exception {
        ThreadLocalRandom.current().nextBoolean();
        ThreadLocalRandom.current().nextDouble();
        ThreadLocalRandom.current().nextFloat();
        ThreadLocalRandom.current().nextInt();
        ThreadLocalRandom.current().nextLong();
    }
}
