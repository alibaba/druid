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

public class OdpsSelectTest17 extends TestCase {

    public void test_select() throws Exception {
        String sql = "SELECT prov\n" +
                "  , name\n" +
                "  , cnt\n" +
                "FROM mock_app.adl_mock_v_fct\n" +
                "WHERE ds = 20160920\n" +
                "  AND name != 'none'\n" +
                "  AND prov in ( select prov from (\n" +
                "    SELECT prov, sum(cnt) as cnt\n" +
                "    FROM mock_app.adl_mock_v_fct\n" +
                "    WHERE ds = 20160920\n" +
                "    GROUP BY prov\n" +
                "    ORDER BY cnt DESC LIMIT 5\n" +
                ") top )\n" +
                "ORDER BY cnt DESC\n" +
                "LIMIT 800";//
        assertEquals("SELECT prov, name, cnt\n" +
                "FROM mock_app.adl_mock_v_fct\n" +
                "WHERE ds = 20160920\n" +
                "\tAND name != 'none'\n" +
                "\tAND prov IN (\n" +
                "\t\tSELECT prov\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT prov, SUM(cnt) AS cnt\n" +
                "\t\t\tFROM mock_app.adl_mock_v_fct\n" +
                "\t\t\tWHERE ds = 20160920\n" +
                "\t\t\tGROUP BY prov\n" +
                "\t\t\tORDER BY cnt DESC\n" +
                "\t\t\tLIMIT 5\n" +
                "\t\t) top\n" +
                "\t)\n" +
                "ORDER BY cnt DESC\n" +
                "LIMIT 800", SQLUtils.formatOdps(sql));
        assertEquals("select prov, name, cnt\n" +
                "from mock_app.adl_mock_v_fct\n" +
                "where ds = 20160920\n" +
                "\tand name != 'none'\n" +
                "\tand prov in (\n" +
                "\t\tselect prov\n" +
                "\t\tfrom (\n" +
                "\t\t\tselect prov, sum(cnt) as cnt\n" +
                "\t\t\tfrom mock_app.adl_mock_v_fct\n" +
                "\t\t\twhere ds = 20160920\n" +
                "\t\t\tgroup by prov\n" +
                "\t\t\torder by cnt desc\n" +
                "\t\t\tlimit 5\n" +
                "\t\t) top\n" +
                "\t)\n" +
                "order by cnt desc\n" +
                "limit 800", SQLUtils.formatOdps(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
        
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
        Assert.assertEquals(4, visitor.getColumns().size());
        Assert.assertEquals(3, visitor.getConditions().size());
        
//        Assert.assertTrue(visitor.getColumns().contains(new Column("abc", "name")));
    }
    
}
