/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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

import java.util.List;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLBeginStatement;
import com.alibaba.druid.sql.ast.statement.SQLBlockStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import org.junit.Assert;

/**
 * @author lizongbo
 */
public class TiDBBeginTest extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "BEGIN ;";
        testSql(sql);
    }

    public void test_1() throws Exception {
        String sql = "BEGIN";
        testSql(sql);
    }

    public void test_2() throws Exception {
        String sql = "BEGIN PESSIMISTIC;";
        testSql(sql);
    }

    public void test_3() throws Exception {
        String sql = "BEGIN PESSIMISTIC";
        testSql(sql);
    }

    public void test_4() throws Exception {
        String sql = "BEGIN OPTIMISTIC;";
        testSql(sql);
    }

    public void test_5() throws Exception {
        String sql = "BEGIN OPTIMISTIC";
        testSql(sql);
    }

    public void test_6() throws Exception {
        String sql = "BEGIN /*T! PESSIMISTIC */";
        testSql(sql);
    }

    public void test_7() throws Exception {
        String sql = "BEGIN /*T! PESSIMISTIC */;";
        testSql(sql);
    }

    public void test_8() throws Exception {
        String sql = "BEGIN /*T! OPTIMISTIC */";
        testSql(sql);
    }

    public void test_9() throws Exception {
        String sql = "BEGIN /*T! OPTIMISTIC */;";
        testSql(sql);
    }

    public void test_10() throws Exception {
        String sql = "BEGIN\n"
            + "    INSERT INTO `table_test` (`111`);\n"
            + "    INSERT INTO `table_test` (`222`);\n"
            + "    SELECT * FROM table_test where id = 1;\n"
            + "    END";
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.TIDB);
        SQLStatement statement = statementList.get(0);
        //System.out.println(statement.getClass());
        print(statementList);
        Assert.assertEquals(1, statementList.size());
        Assert.assertTrue(statement instanceof SQLBlockStatement);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
        Assert.assertEquals(1, visitor.getTables().size());
    }


    void testSql(String sql) throws Exception {

        // MySqlStatementParser parser = new MySqlStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.TIDB);
        SQLStatement statement = statementList.get(0);
        //System.out.println(statement.getClass());
        print(statementList);
        Assert.assertEquals(1, statementList.size());
        Assert.assertTrue(statement instanceof SQLBeginStatement);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());

        Assert.assertEquals(0, visitor.getTables().size());
    }


}
