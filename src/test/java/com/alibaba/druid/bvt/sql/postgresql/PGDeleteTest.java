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
package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class PGDeleteTest extends PGTest {

    public void test_0() throws Exception {
        String sql = "DELETE FROM films USING producers WHERE producer_id = producers.id AND producers.name = 'foo';";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("films")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("producers")));

        //Assert.assertTrue(visitor.getFields().size() == 0);
    }

    public void test_1() {
		String sql = "delete from test01 as a where a.id = 2";
		PGSQLStatementParser parser = new PGSQLStatementParser(sql);
		List<SQLStatement> statementList = parser.parseStatementList();
		SQLStatement statemen = statementList.get(0);
		print(statementList);
		assertTrue(statementList.size() == 1);
		assertTrue(statemen instanceof PGDeleteStatement);
		PGDeleteStatement delete = (PGDeleteStatement) statemen;
		assertTrue(delete.getAlias().equalsIgnoreCase("a"));
		assertTrue(delete.getTableName().getSimpleName().equals("test01"));
	}
    
}
