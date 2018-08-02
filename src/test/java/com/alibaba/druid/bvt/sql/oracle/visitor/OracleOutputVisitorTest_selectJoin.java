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
package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.util.List;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;

public class OracleOutputVisitorTest_selectJoin extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT e.salary from employee e join department d where e.depId = d.id";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(true, visitor.containsTable("employee"));
        Assert.assertEquals(true, visitor.containsTable("department"));

        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("employee", "salary")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("employee", "depId")));
        Assert.assertEquals(true, visitor.getColumns().contains(new Column("department", "id")));

        StringBuilder buf = new StringBuilder();
        OracleOutputVisitor outputVisitor = new OracleOutputVisitor(buf);
        stmt.accept(outputVisitor);
        Assert.assertEquals("SELECT e.salary\nFROM employee e\n\tJOIN department d\nWHERE e.depId = d.id",
                            buf.toString());

    }
}
