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
package com.alibaba.druid.bvt.sql.mysql.param;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlParameterizedOutputVisitorTest_1 extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM T WHERE ID IN (?)";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.HSQL), sql);
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM T WHERE ID = ?";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.HSQL), sql);
    }
    
    public void test_2() throws Exception {
        String sql = "SELECT * FROM T WHERE ID = ? AND Name = ?";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.HSQL), sql);
    }
    
    public void test_3() throws Exception {
        String sql = "SELECT * FROM T WHERE ID IS NULL";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.HSQL), sql);
    }
    
    public void test_4() throws Exception {
        String sql = "INSERT INTO T (FID, FNAME) VALUES(?, ?)";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.HSQL), sql);
    }
    
    public void test_mysql() throws Exception {
        String sql = "INSERT INTO T (FID, FNAME) VALUES(?, ?), (?, ?)";
        Assert.assertNotSame(ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL), sql);
    }
}
