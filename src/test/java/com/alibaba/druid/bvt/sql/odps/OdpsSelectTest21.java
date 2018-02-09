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
import org.junit.Assert;

import java.util.List;

public class OdpsSelectTest21 extends TestCase {

    public void test_select() throws Exception {
        // 1095288847322
        String sql = "select wm_concat(',', a) from values('abc', 1), (null, 2) t (a, i);";//
        Assert.assertEquals("SELECT WM_CONCAT(',', a)\n" +
                "FROM VALUES ('abc', 1), (NULL, 2) t (a, i);", SQLUtils.formatOdps(sql));
        Assert.assertEquals("select wm_concat(',', a)\n" +
                "from values ('abc', 1), (null, 2) t (a, i);", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(0, visitor.getTables().size());
        Assert.assertEquals(0, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        
//        Assert.assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }


}
