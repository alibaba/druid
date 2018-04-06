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
package com.alibaba.druid.bvt.pool;

import java.util.Hashtable;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

public class DruidDataSourceFactoryTest extends TestCase {

    private DruidDataSource dataSource;
    
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_factory() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();

        Reference ref = new Reference(DataSource.class.getName());
        ref.add(new StringRefAddr(DruidDataSourceFactory.PROP_REMOVEABANDONED, "true"));
        ref.add(new StringRefAddr(DruidDataSourceFactory.PROP_MAXACTIVE, "20"));

        Hashtable<String, String> env = new Hashtable<String, String>();

        dataSource = (DruidDataSource) factory.getObjectInstance(ref, null, null, env);

        Assert.assertTrue(dataSource.isRemoveAbandoned());
        Assert.assertEquals(20, dataSource.getMaxActive());
    }
}
