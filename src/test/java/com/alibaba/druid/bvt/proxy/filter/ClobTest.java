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
package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLXML;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class ClobTest extends TestCase {

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_clob() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("stat");
        dataSource.setUrl("jdbc:mock:");

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT NULL");

        Assert.assertTrue(rs.next());

        {
            Clob x = rs.getClob(1);
            Assert.assertNull(x);
        }

        {
            NClob x = rs.getNClob(1);
            Assert.assertNull(x);
        }

        {
            Blob x = rs.getBlob(1);
            Assert.assertNull(x);
        }

        {
            SQLXML x = rs.getSQLXML(1);
            Assert.assertNull(x);
        }

        {
            String x = rs.getString(1);
            Assert.assertNull(x);
        }

        Assert.assertNull(rs.getRowId(1));
        Assert.assertNull(rs.getBigDecimal(1));
        Assert.assertNull(rs.getObject(1));

        rs.close();
        stmt.close();
        conn.close();

        dataSource.close();
    }
}
