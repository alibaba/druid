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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * for pr #5877
 */
public class OracleSqlCommentTest extends TestCase {
    public void test_0() throws Exception {
//        原来是只要||和/之间连着就会解析报错，中间加了空格就没问题，现在下面的sql也不会解析报错
        String sql = "select id||/*用户Id*/ name||/*用户名称*/age||/*用户年龄*/gender from user";
        System.out.println(SQLUtils.parseSingleStatement(sql, JdbcConstants.ORACLE, true));
    }
    public void test_1() throws Exception {
        String sql =  "insert into user (id,name) select '501'||/*啊打发*/'|502|' as id, s,name from order s";
        System.out.println(SQLUtils.parseSingleStatement(sql, JdbcConstants.ORACLE, true));
    }
    public void test_2() throws Exception {
        String sql =  "update user set age=3 where id in ( select '501'||/*啊打发*/'|502|' as id from order s)";
        System.out.println(SQLUtils.parseSingleStatement(sql, JdbcConstants.ORACLE, true));
    }
    public void test_3() throws Exception {
        String sql =  "delete from user where id in (select '501'||/*啊打发*/'|502|' as id from order s)";
        System.out.println(SQLUtils.parseSingleStatement(sql, JdbcConstants.ORACLE, true));
    }
}
