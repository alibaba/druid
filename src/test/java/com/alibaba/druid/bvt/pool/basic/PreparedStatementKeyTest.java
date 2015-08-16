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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.PreparedStatementHolder;
import com.alibaba.druid.pool.PreparedStatementPool;
import com.alibaba.druid.pool.PreparedStatementPool.MethodType;

public class PreparedStatementKeyTest extends TestCase {

    public void test_equals_0() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", "c1", MethodType.M1);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", "c2", MethodType.M1);
        PreparedStatementKey k3 = new PreparedStatementKey("x1", "c3", MethodType.M1);

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }

    public void test_equals_2() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", "c1", MethodType.M1);
        PreparedStatementKey k2 = new PreparedStatementKey("x2", "c1", MethodType.M1);
        PreparedStatementKey k3 = new PreparedStatementKey("x3", "c1", MethodType.M1);

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }

    public void test_equals_3() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", "c1", MethodType.M1);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", "c1", MethodType.M2);
        PreparedStatementKey k3 = new PreparedStatementKey("x1", "c1", MethodType.M3);

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }

    public void test_equals_4() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", "c1", MethodType.M1);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", "c2", MethodType.M1);
        PreparedStatementKey k3 = new PreparedStatementKey("x1", null, MethodType.M1);

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }

    public void test_equals_5() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", null, MethodType.M1);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", null, MethodType.M2);
        PreparedStatementKey k3 = new PreparedStatementKey("x1", null, MethodType.M3);

        k1.hashCode();

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }

    public void test_equals_6() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", null, MethodType.M1);
        PreparedStatementKey k2 = new PreparedStatementKey("x2", null, MethodType.M1);
        PreparedStatementKey k3 = new PreparedStatementKey("x3", null, MethodType.M1);

        k1.hashCode();

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }
    
    public void test_equals_7() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", null, MethodType.M1, 0, 0);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", null, MethodType.M1, 1, 0);
        PreparedStatementKey k3 = new PreparedStatementKey("x2", null, MethodType.M1, 0, 1);

        k1.hashCode();

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }
    
    public void test_equals_8() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", null, MethodType.M1, 0, 0, 0);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", null, MethodType.M1, 0, 0, 1);
        PreparedStatementKey k3 = new PreparedStatementKey("x2", null, MethodType.M1, 0, 1, 0);

        k1.hashCode();

        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }
    
    public void test_equals_9() throws Exception {
        PreparedStatementKey k1 = new PreparedStatementKey("x1", null, MethodType.M1, 2);
        PreparedStatementKey k2 = new PreparedStatementKey("x1", null, MethodType.M1, new int[] {});
        PreparedStatementKey k3 = new PreparedStatementKey("x2", null, MethodType.M1, new String[] {});
        
        k1.hashCode();
        
        Assert.assertFalse(k1.equals(k2));
        Assert.assertFalse(k1.equals(k3));
        Assert.assertFalse(k2.equals(k1));
        Assert.assertFalse(k2.equals(k3));
        Assert.assertFalse(k3.equals(k2));
        Assert.assertFalse(k3.equals(k1));
    }

    public void test_contains() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        MockConnection conn = new MockConnection();
        PreparedStatementKey k1 = new PreparedStatementKey("x1", "c1", MethodType.M1);

        PreparedStatementPool pool = new PreparedStatementPool(new DruidConnectionHolder(dataSource, conn));
        MockPreparedStatement raw = new MockPreparedStatement(null, null);
        pool.put(new PreparedStatementHolder(k1, raw));

        Assert.assertTrue(pool.get(k1) != null);
        Assert.assertTrue(pool.get(k1) != null);
    }
}
