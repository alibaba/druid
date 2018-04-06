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
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;

public class MySqlDeleteTest_3 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "DELETE FROM t1 " //
                     + "WHERE s11 > ANY"//
                     + "(SELECT COUNT(*) /* no hint */ FROM t2"//
                     + "  WHERE NOT EXISTS"//
                     + "   (SELECT * FROM t3"//
                     + "    WHERE ROW(5*t2.s1,77)="//
                     + "     (SELECT 50,11*s1 FROM t4 UNION SELECT 50,77 FROM"//
                     + "      (SELECT * FROM t5) AS t5)));";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        Assert.assertEquals("DELETE FROM t1\n" +
                "WHERE s11 > ANY (\n" +
                "\t\tSELECT COUNT(*)\n" +
                "\t\tFROM t2\n" +
                "\t\tWHERE NOT EXISTS (\n" +
                "\t\t\tSELECT *\n" +
                "\t\t\tFROM t3\n" +
                "\t\t\tWHERE ROW(5 * t2.s1, 77) = (\n" +
                "\t\t\t\tSELECT 50, 11 * s1\n" +
                "\t\t\t\tFROM t4\n" +
                "\t\t\t\tUNION\n" +
                "\t\t\t\tSELECT 50, 77\n" +
                "\t\t\t\tFROM (\n" +
                "\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\tFROM t5\n" +
                "\t\t\t\t) t5\n" +
                "\t\t\t)\n" +
                "\t\t)\n" +
                "\t);", SQLUtils.toMySqlString(stmt));
        assertEquals("delete from t1\n" +
                "where s11 > any (\n" +
                "\t\tselect count(*)\n" +
                "\t\tfrom t2\n" +
                "\t\twhere not exists (\n" +
                "\t\t\tselect *\n" +
                "\t\t\tfrom t3\n" +
                "\t\t\twhere ROW(5 * t2.s1, 77) = (\n" +
                "\t\t\t\tselect 50, 11 * s1\n" +
                "\t\t\t\tfrom t4\n" +
                "\t\t\t\tunion\n" +
                "\t\t\t\tselect 50, 77\n" +
                "\t\t\t\tfrom (\n" +
                "\t\t\t\t\tselect *\n" +
                "\t\t\t\t\tfrom t5\n" +
                "\t\t\t\t) t5\n" +
                "\t\t\t)\n" +
                "\t\t)\n" +
                "\t);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println(stmt);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(5, visitor.getTables().size());
        Assert.assertEquals(6, visitor.getColumns().size());
        Assert.assertEquals(1, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t1")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("t2")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("t1", "s11")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t2", "s1")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t2", "*")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t3", "*")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t4", "s1")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("t5", "*")));
    }
}
