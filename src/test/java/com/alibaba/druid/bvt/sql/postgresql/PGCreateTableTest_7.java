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

public class PGCreateTableTest_7 extends PGTest {

    public void test_0() throws Exception {
        String sql = "create table test_site_data_select_111 AS select * from postman_trace_info_one  where lng>0 and lat>0  and site_id='17814' ;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        
        assertEquals("CREATE TABLE test_site_data_select_111\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM postman_trace_info_one\n" +
                "WHERE lng > 0\n" +
                "\tAND lat > 0\n" +
                "\tAND site_id = '17814';", SQLUtils.toPGString(stmt));
        
        assertEquals("create table test_site_data_select_111\n" +
                "as\n" +
                "select *\n" +
                "from postman_trace_info_one\n" +
                "where lng > 0\n" +
                "\tand lat > 0\n" +
                "\tand site_id = '17814';", SQLUtils.toPGString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("test_site_data_select_111")));

        assertTrue(visitor.getTables().get(new TableStat.Name("test_site_data_select_111")).getCreateCount() == 1);

        assertEquals(4, visitor.getColumns().size() );
    }

}
