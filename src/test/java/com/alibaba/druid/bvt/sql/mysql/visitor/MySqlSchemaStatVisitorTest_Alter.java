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
package com.alibaba.druid.bvt.sql.mysql.visitor;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class MySqlSchemaStatVisitorTest_Alter extends TestCase {

    public void test_0() throws Exception {
        String sql = "alter table sql_perf add index `idx_instance_8` (`host`,`port`,`hashcode`,`item`,`time`,`value`);";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statemen.accept(visitor);

//        System.out.println(sql);
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());

        assertEquals(1, visitor.getTables().size());
        assertTrue(visitor.containsTable("sql_perf"));

        assertEquals(6, visitor.getColumns().size());
        assertTrue(visitor.getColumns().contains(new Column("sql_perf", "host")));
        assertTrue(visitor.getColumns().contains(new Column("sql_perf", "port")));
        assertTrue(visitor.getColumns().contains(new Column("sql_perf", "hashcode")));
        assertTrue(visitor.getColumns().contains(new Column("sql_perf", "item")));
        assertTrue(visitor.getColumns().contains(new Column("sql_perf", "time")));
        assertTrue(visitor.getColumns().contains(new Column("sql_perf", "value")));

    }

}
