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
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;

public class PGSelectTest21 extends PGTest {

    public void test_0() throws Exception {
        String sql = "SELECT DISTINCT(type) FROM dbmis2_databases";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals("SELECT DISTINCT type\nFROM dbmis2_databases", output(statementList));

        Assert.assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());

        Assert.assertEquals(1, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getTables().size());
    }
    
    public void test_1() throws Exception {
    	String sql = "with a(a1,b1) as (select * from b) select * from a";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);
        
        assertTrue(statemen instanceof PGSelectStatement);
        assertTrue(((PGSelectStatement)statemen).getWith().getWithQuery().size()==1);
        StringBuffer sb = new StringBuffer();
        ((PGSelectStatement)statemen).getWith().getWithQuery().get(0).getName().output(sb);
        assertTrue("a".equals(sb.toString()));
        assertTrue(((PGSelectStatement)statemen).getWith().getWithQuery().get(0).getColumns().size()==2);
    }
}
