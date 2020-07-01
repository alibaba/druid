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
package com.alibaba.druid.bvt.sql.postgresql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.stat.TableStat;

public class PGCommentTest extends PGTest {
	
	public void test_0() {
		String sql = "/*multiline comment test"
				+ ".\r\n multiline comment test select * from a;";

		PGSQLStatementParser parser;
		try {
			parser = new PGSQLStatementParser(sql);
			parser.parseStatementList();
		} catch (Exception e) {
			assertTrue(e instanceof ParserException);
		}
	}
	
	public void test_1() {
		String sql = "select a.id,--single line comment test a.name from a;";

		PGSQLStatementParser parser;
		try {
			parser = new PGSQLStatementParser(sql);
			parser.parseStatementList();
		} catch (Exception e) {
			assertTrue(e instanceof ParserException);
		}
	}

    public void test_2() throws Exception {
        String sql = "/*multiline comment test"
        		+ ".\r\n multiline comment test*"
        		+ "/select --single line comment test\r\na.id,"
        		+ "/*multiline comment test*/"
        		+ "a.name from a;";

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

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("a")));

    }
}
