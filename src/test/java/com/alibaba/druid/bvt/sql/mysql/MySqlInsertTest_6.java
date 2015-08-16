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

public class MySqlInsertTest_6 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "insert into document(the_key,the_namespace,Gmt_create,Gmt_modify,Expired_time,the_value) " + //
                     "values (?,?,now(),now(),date_add(now(),interval ? second),?) " + //
                     "on duplicate key update Gmt_modify = values(Gmt_modify),Expired_time = values(Expired_time),the_value = values(the_value)";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;

        Assert.assertEquals(6, insertStmt.getColumns().size());
        Assert.assertEquals(1, insertStmt.getValuesList().size());
        Assert.assertEquals(6, insertStmt.getValuesList().get(0).getValues().size());

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals("INSERT INTO document (the_key, the_namespace, Gmt_create, Gmt_modify, Expired_time" + //
                                    "\n\t, the_value)" + //
                                    "\nVALUES (?, ?, now(), now(), date_add(now(), INTERVAL ? SECOND)" + //
                                    "\n\t, ?)" + //
                                    "\nON DUPLICATE KEY UPDATE Gmt_modify = VALUES(Gmt_modify), Expired_time = VALUES(Expired_time), the_value = VALUES(the_value)",
                            SQLUtils.toMySqlString(insertStmt));
    }
}
