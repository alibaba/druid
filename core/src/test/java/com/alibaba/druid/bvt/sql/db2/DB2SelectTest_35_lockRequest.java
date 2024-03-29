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
package com.alibaba.druid.bvt.sql.db2;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2SelectTest_35_lockRequest extends DB2Test {
    public void test_0() throws Exception {
        String sql = "SELECT KEYID, KEYVALUE FROM KEYIDS WHERE KEYID = ? FOR READ ONLY WITH RS USE AND KEEP UPDATE LOCKS;";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("KEYIDS")));

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("KEYIDS", "KEYVALUE")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("KEYIDS", "KEYVALUE")));

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.DB2);
        Assert.assertEquals("SELECT KEYID, KEYVALUE"
                        + "\nFROM KEYIDS"
                        + "\nWHERE KEYID = ?"
                        + "\nFOR READ ONLY"
                        + "\nWITH RS"
                        + "\nUSE AND KEEP UPDATE LOCKS;",
                output);
    }
}
