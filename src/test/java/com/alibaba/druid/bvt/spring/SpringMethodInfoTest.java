/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.spring;

import org.junit.Assert;
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
