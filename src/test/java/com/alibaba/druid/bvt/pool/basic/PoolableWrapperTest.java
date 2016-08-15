/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.pool.basic;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.PoolableWrapper;

public class PoolableWrapperTest extends TestCase {

    public void test_isWrapper() throws Exception {
        PoolableWrapper wrapper = new PoolableWrapper(new MockConnection());

        Assert.assertEquals(false, wrapper.isWrapperFor(null));
        Assert.assertEquals(true, wrapper.isWrapperFor(PoolableWrapper.class));
        Assert.assertEquals(true, wrapper.isWrapperFor(MockConnection.class));
    }

    public void test_unwrap() throws Exception {
        PoolableWrapper wrapper = new PoolableWrapper(new MockConnection());

        Assert.assertEquals(null, wrapper.unwrap(null));
        Assert.assertEquals(true, wrapper.unwrap(PoolableWrapper.class) != null);
        Assert.assertEquals(true, wrapper.unwrap(MockConnection.class) != null);
    }
}
