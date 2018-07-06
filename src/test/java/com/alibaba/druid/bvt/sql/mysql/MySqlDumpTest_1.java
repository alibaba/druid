/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;

import java.util.List;

public class MySqlDumpTest_1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "DUMP DATA OVERWRITE INTO 'odps://cod_garuda/wenyu_meta_person' select cid, cname, dept, gender, pid from wenyu_meta_person limit 10;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
//        print(statementList);

        assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsTable("wenyu_meta_person"));
//        assertTrue(visitor.getTables().containsKey(new TableStat.Name("t2")));
        
        assertTrue(visitor.containsColumn("wenyu_meta_person", "cid"));
        assertTrue(visitor.containsColumn("wenyu_meta_person", "cname"));
        assertTrue(visitor.containsColumn("wenyu_meta_person", "dept"));
        assertTrue(visitor.containsColumn("wenyu_meta_person", "gender"));
        assertTrue(visitor.containsColumn("wenyu_meta_person", "pid"));

    }
}
