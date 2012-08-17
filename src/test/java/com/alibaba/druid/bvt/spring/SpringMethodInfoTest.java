package com.alibaba.druid.bvt.spring;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.support.spring.stat.SpringMethodInfo;
import com.alibaba.druid.support.spring.stat.SpringMethodStat;
import com.alibaba.druid.support.spring.stat.SpringStat;

public class SpringMethodInfoTest extends TestCase {

    public void test_equals() throws Exception {
        SpringMethodInfo b1 = new SpringMethodInfo(B.class, B.class.getMethod("f", int.class));
        SpringMethodInfo c = new SpringMethodInfo(C.class, C.class.getMethod("f", int.class));
        SpringMethodInfo b2 = new SpringMethodInfo(B.class, B.class.getMethod("f", int.class));

        Assert.assertFalse(b1.equals(c));
        Assert.assertTrue(b1.equals(b1));
        Assert.assertTrue(b1.equals(b2));

        Assert.assertEquals(B.class.getName(), b1.getClassName());
        Assert.assertEquals(C.class.getName(), c.getClassName());
    }

    public void test_get() throws Exception {
        SpringStat springStat = new SpringStat();

        SpringMethodInfo b1 = new SpringMethodInfo(B.class, B.class.getMethod("f", int.class));
        SpringMethodInfo b2 = new SpringMethodInfo(B.class, B.class.getMethod("f", int.class));

        SpringMethodStat methodStat1 = springStat.getMethodStat(b1, true);
        SpringMethodStat methodStat2 = springStat.getMethodStat(b2, true);
        Assert.assertSame(methodStat1, methodStat2);
    }

    public static class A {

        public void f(int i) {

        }
    }

    public static class B extends A {

    }

    public static class C extends A {

    }
}
