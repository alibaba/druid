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

public class OdpsSelectTest_over_rows_3 extends TestCase {

    public void test_select() throws Exception {
        String sql = "SELECT sum(c) over(order by ds rows current row) s, ds FROM t1";//
        Assert.assertEquals("SELECT SUM(c) OVER (ORDER BY ds ROWS CURRENT ROW) AS s, ds\n" +
                "FROM t1", SQLUtils.formatOdps(sql));
        Assert.assertEquals("select sum(c) over (order by ds rows current row) as s, ds\n" +
                "from t1", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ODPS);
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
        
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.ODPS);
        stmt.accept(visitor);
        
//        System.out.println("Tables : " + visitor.getTables());
//      System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());
        
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(2, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        
//        Assert.assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }
    
}
