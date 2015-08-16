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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

public class MySqlInsertTest_4 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO tbl_name (a,b,c) VALUES(1,2,3),(4,5,6),(7,8,9);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        Assert.assertEquals(3, insertStmt.getColumns().size());
        Assert.assertEquals(3, insertStmt.getValuesList().size());
        Assert.assertEquals(3, insertStmt.getValuesList().get(0).getValues().size());
        Assert.assertEquals(3, insertStmt.getValuesList().get(1).getValues().size());
        Assert.assertEquals(3, insertStmt.getValuesList().get(2).getValues().size());

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals("INSERT INTO tbl_name (a, b, c)" + //
                            "\nVALUES (1, 2, 3)," +
                            "\n\t(4, 5, 6)," +
                            "\n\t(7, 8, 9)", SQLUtils.toMySqlString(insertStmt));
    }
}
