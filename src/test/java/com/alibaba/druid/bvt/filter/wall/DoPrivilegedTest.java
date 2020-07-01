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
package com.alibaba.druid.bvt.filter.wall;

import java.security.PrivilegedAction;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallUtils;

public class DoPrivilegedTest extends TestCase {

    public void test_0() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql("select @@version_compile_os FROM X"));
    }

    public void test_0_0() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select * FROM X where version=@@version_compile_os"));
    }

    public void test_1() throws Exception {
        final WallConfig config = new WallConfig();
        config.setDoPrivilegedAllow(true);

        WallProvider.doPrivileged(new PrivilegedAction<Object>() {

            @Override
            public Object run() {
                Assert.assertTrue(WallUtils.isValidateMySql("select @@version_compile_os FROM X", config));
                return null;
            }
        });

    }
}
