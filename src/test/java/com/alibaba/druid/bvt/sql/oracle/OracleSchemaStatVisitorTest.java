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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Condition;

public class OracleSchemaStatVisitorTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = "SELECT id, name name from department d" + //
                     "   WHERE d.id = ? order by name desc";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        List<Object> parameters = new ArrayList<Object>();
        parameters.add(23456);
        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        visitor.setParameters(parameters);
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("department")));

        Assert.assertEquals(2, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("department", "id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("department", "name")));
        
        Assert.assertEquals(1, visitor.getConditions().size());
        
        Condition condition = visitor.getConditions().get(0);
        Assert.assertSame(parameters.get(0), condition.getValues().get(0));
        
        Column orderByColumn = visitor.getOrderByColumns().iterator().next();
        Assert.assertEquals(SQLOrderingSpecification.DESC, orderByColumn.getAttributes().get("orderBy.type"));
    }
}
