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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class MySqlSelectTest_14 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM t1 WHERE column1 = (SELECT column1 FROM t2);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        SQLSelect select = selectStmt.getSelect();
        Assert.assertNotNull(select.getQuery());
        MySqlSelectQueryBlock queryBlock = (MySqlSelectQueryBlock) select.getQuery();
        Assert.assertNull(queryBlock.getOrderBy());

//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t1")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t2")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT *\n" +
                        "FROM t1\n" +
                        "WHERE column1 = (\n" +
                        "\tSELECT column1\n" +
                        "\tFROM t2\n" +
                        ");", //
                            output);
        
        String output_lcase = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
        Assert.assertEquals("select *" //
                            + "\nfrom t1"//
                            + "\nwhere column1 = ("//
                            + "\n\tselect column1"//
                            + "\n\tfrom t2"//
                            + "\n);", //
                            output_lcase);
    }
}
