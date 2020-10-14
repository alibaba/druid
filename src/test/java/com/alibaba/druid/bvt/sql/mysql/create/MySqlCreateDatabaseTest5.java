/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateDatabaseTest5 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE SCHEMA IF NOT EXISTS schema_name with (property_name = 'expression')\n";

        List<SQLStatement> stmtList = SQLUtils.toStatementList(sql, JdbcConstants.MYSQL);

        SQLStatement stmt = stmtList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE DATABASE IF NOT EXISTS schema_name\n" +
                "WITH (\n" +
                "\tproperty_name = 'expression'\n" +
                ")", output);
    }


    public void test_1() throws Exception {
        String sql = "CREATE SCHEMA IF NOT EXISTS `schema_name` with (property_name = 'expression')\n";

        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql, SQLParserFeature.IgnoreNameQuotes);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE DATABASE IF NOT EXISTS schema_name\n" +
                "WITH (\n" +
                "\tproperty_name = 'expression'\n" +
                ")", output);
    }
}
