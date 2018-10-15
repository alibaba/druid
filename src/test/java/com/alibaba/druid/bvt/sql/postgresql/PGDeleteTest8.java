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
import com.alibaba.druid.stat.TableStat;

public class PGDeleteTest8 extends PGTest {

    public void test_0() throws Exception {
        String sql = "WITH RECURSIVE included_parts(sub_part, part) AS ("
                + "    SELECT sub_part, part FROM parts WHERE part = 'our_product'"
                + "  UNION ALL"
                + "    SELECT p.sub_part, p.part"
                + "    FROM included_parts pr, parts p"
                + "    WHERE p.part = pr.sub_part"
                + "  )"
                + "DELETE FROM parts\n"
                + "  WHERE part IN (SELECT part FROM included_parts);";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("parts")));

        assertEquals(2, visitor.getColumns().size() );
        
        assertTrue(visitor.containsColumn("parts", "sub_part"));
        assertTrue(visitor.containsColumn("parts", "sub_part"));
    }

    
}
