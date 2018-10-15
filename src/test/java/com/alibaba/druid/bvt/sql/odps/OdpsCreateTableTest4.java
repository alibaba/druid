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
package com.alibaba.druid.bvt.sql.odps;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class OdpsCreateTableTest4 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "CREATE TABLE T1 (a string, b string, c bigint) CLUSTERED BY (c) SORTED by (c) INTO 1024 BUCKETS;";//
        assertEquals("CREATE TABLE T1 (\n" +
                "\ta STRING,\n" +
                "\tb STRING,\n" +
                "\tc BIGINT\n" +
                ")\n" +
                "CLUSTERED BY (c)\n" +
                "SORTED BY (c)\n" +
                "INTO 1024 BUCKETS;", SQLUtils.formatOdps(sql));

        assertEquals("create table T1 (\n" +
                "\ta string,\n" +
                "\tb string,\n" +
                "\tc bigint\n" +
                ")\n" +
                "clustered by (c)\n" +
                "sorted by (c)\n" +
                "into 1024 buckets;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

//        System.out.println(SQLUtils.formatOdps(sql));
        
//        assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }


}
