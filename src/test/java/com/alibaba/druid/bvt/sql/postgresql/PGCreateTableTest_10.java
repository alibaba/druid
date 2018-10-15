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

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class PGCreateTableTest_10 extends PGTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE KTV.ALI_KTV_LEADS_AREA_FIX_TBD (\n" +
                "\tID VARCHAR(200) NOT NULL,\n" +
                "\tTYPE VARCHAR(200),\n" +
                "\tCONSTRAINT idx_ALI_KTV_LEADS_AREA_FIX_TBD_PK0 PRIMARY KEY (ID)\n" +
                ")";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        assertEquals("CREATE TABLE KTV.ALI_KTV_LEADS_AREA_FIX_TBD (\n" +
                "\tID VARCHAR(200) NOT NULL,\n" +
                "\tTYPE VARCHAR(200),\n" +
                "\tCONSTRAINT idx_ALI_KTV_LEADS_AREA_FIX_TBD_PK0 PRIMARY KEY (ID)\n" +
                ")", SQLUtils.toPGString(stmt));
        
        assertEquals("create table KTV.ALI_KTV_LEADS_AREA_FIX_TBD (\n" +
                "\tID VARCHAR(200) not null,\n" +
                "\tTYPE VARCHAR(200),\n" +
                "\tconstraint idx_ALI_KTV_LEADS_AREA_FIX_TBD_PK0 primary key (ID)\n" +
                ")", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("KTV.ALI_KTV_LEADS_AREA_FIX_TBD")));

        assertTrue(visitor.getTables().get(new TableStat.Name("KTV.ALI_KTV_LEADS_AREA_FIX_TBD")).getCreateCount() == 1);

        assertEquals(3, visitor.getColumns().size() );
    }

}
