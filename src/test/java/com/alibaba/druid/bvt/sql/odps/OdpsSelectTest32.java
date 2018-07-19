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

public class OdpsSelectTest32 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "SELECT * from mytable1 a LEFT SEMI JOIN mytable2 b on a.id=b.id;";//
        assertEquals("SELECT *\n" +
                "FROM mytable1 a\n" +
                "LEFT SEMI JOIN mytable2 b\n" +
                "ON a.id = b.id;", SQLUtils.formatOdps(sql));

        assertEquals("select *\n" +
                "from mytable1 a\n" +
                "left semi join mytable2 b\n" +
                "on a.id = b.id;", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        assertEquals(2, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());

//        System.out.println(SQLUtils.formatOdps(sql));
        
//        assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }


}
