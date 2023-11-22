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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * test java.sql.Time parameter format and Boolean parameterized
 * @author lizongbo
 */
public class MySqlFormatTest4 extends TestCase {

    public void test_0() throws Exception {
        String text = "select * from tabletest\n"
            + "where ccc like '%tidb_txn_mode%' and open_flag = true "
            + "and bbb = 1 and ddd= 4567.9888  "
            + "and added_time > ? and  added_time < ?;";
        List<Object> parameters = new ArrayList<>();
        java.sql.Time startTime= new java.sql.Time(System.currentTimeMillis());
        startTime.setHours(11);
        startTime.setMinutes(22);
        startTime.setSeconds(33);
        java.sql.Time endTime= new java.sql.Time(System.currentTimeMillis());
        endTime.setHours(20);
        endTime.setMinutes(21);
        endTime.setSeconds(23);
        parameters.add(startTime);
        parameters.add(endTime);
        String formrtedSql = SQLUtils.format(text, JdbcUtils.MYSQL, parameters);
        System.out.println(formrtedSql);
        String mergedSql = ParameterizedOutputVisitorUtils.parameterize(formrtedSql, JdbcUtils.MYSQL);
        System.out.println("orgSql=========" + text);
        System.out.println("formrtedSql=========" + formrtedSql);
        System.out.println("mergedSql=========" + mergedSql);
        Assert.assertEquals("SELECT *\n"
            + "FROM tabletest\n"
            + "WHERE ccc LIKE '%tidb_txn_mode%'\n"
            + "\tAND open_flag = true\n"
            + "\tAND bbb = 1\n"
            + "\tAND ddd = 4567.9888\n"
            + "\tAND added_time > TIME '11:22:33'\n"
            + "\tAND added_time < TIME '20:21:23';", formrtedSql);
        Assert.assertEquals("SELECT *\n"
            + "FROM tabletest\n"
            + "WHERE ccc LIKE ?\n"
            + "\tAND open_flag = ?\n"
            + "\tAND bbb = ?\n"
            + "\tAND ddd = ?\n"
            + "\tAND added_time > TIME ?\n"
            + "\tAND added_time < TIME ?;", mergedSql);
    }
}
