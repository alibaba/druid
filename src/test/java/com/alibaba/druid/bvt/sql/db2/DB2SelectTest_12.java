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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcConstants;

public class DB2SelectTest_12 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "SELECT * FROM TASK_POLICE_QUERY_BATCH WHERE STATUS = '0' ORDER BY PRIORITY FETCH FIRST 100 ROWS ONLY";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("TASK_POLICE_QUERY_BATCH")));

         Assert.assertTrue(visitor.getColumns().contains(new Column("TASK_POLICE_QUERY_BATCH", "STATUS")));
         Assert.assertTrue(visitor.getColumns().contains(new Column("TASK_POLICE_QUERY_BATCH", "*")));
         Assert.assertTrue(visitor.getColumns().contains(new Column("TASK_POLICE_QUERY_BATCH", "PRIORITY")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("SELECT *"
                + "\nFROM TASK_POLICE_QUERY_BATCH"
                + "\nWHERE STATUS = '0'"
                + "\nORDER BY PRIORITY"
                + "\nFETCH FIRST 100 ROWS ONLY", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        Assert.assertEquals("select *"
                + "\nfrom TASK_POLICE_QUERY_BATCH"
                + "\nwhere STATUS = '0'"
                + "\norder by PRIORITY"
                + "\nfetch first 100 rows only", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
