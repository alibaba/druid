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
package com.alibaba.druid.bvt.sql.db2;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcConstants;

public class DB2SelectTest_11 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "SELECT A.F_0201, A.F_0301, A.F_0802, A.F_2100"
                + "\nFROM A, B"
                + "\nWHERE B.F_2211 = '5'"
                + "\n    AND A.F_0301 = B.F_0301"
                + "    AND Substr(B.F_0815, 1, 4) concat  Substr ( B.F_0815,5,2 ) > A.F_0802"
                + "    AND A.F_2100 > 0"
                + "\nUNION"
                + "\nSELECT A.F_0201, A.F_0301, A.F_0802, A.F_2100"
                + "\nFROM A"
                + "\nWHERE A.F_0301 NOT IN (SELECT F_0301"
                + "\n        FROM B)"
                + "\n    AND A.F_2100 > 0";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(6, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("A")));

         Assert.assertTrue(visitor.getColumns().contains(new Column("A", "F_0201")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("SELECT A.F_0201, A.F_0301, A.F_0802, A.F_2100\n" +
                        "FROM A, B\n" +
                        "WHERE B.F_2211 = '5'\n" +
                        "\tAND A.F_0301 = B.F_0301\n" +
                        "\tAND (Substr(B.F_0815, 1, 4) CONCAT Substr(B.F_0815, 5, 2)) > A.F_0802\n" +
                        "\tAND A.F_2100 > 0\n" +
                        "UNION\n" +
                        "SELECT A.F_0201, A.F_0301, A.F_0802, A.F_2100\n" +
                        "FROM A\n" +
                        "WHERE A.F_0301 NOT IN (\n" +
                        "\t\tSELECT F_0301\n" +
                        "\t\tFROM B\n" +
                        "\t)\n" +
                        "\tAND A.F_2100 > 0", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        assertEquals("select A.F_0201, A.F_0301, A.F_0802, A.F_2100\n" +
                        "from A, B\n" +
                        "where B.F_2211 = '5'\n" +
                        "\tand A.F_0301 = B.F_0301\n" +
                        "\tand (Substr(B.F_0815, 1, 4) concat Substr(B.F_0815, 5, 2)) > A.F_0802\n" +
                        "\tand A.F_2100 > 0\n" +
                        "union\n" +
                        "select A.F_0201, A.F_0301, A.F_0802, A.F_2100\n" +
                        "from A\n" +
                        "where A.F_0301 not in (\n" +
                        "\t\tselect F_0301\n" +
                        "\t\tfrom B\n" +
                        "\t)\n" +
                        "\tand A.F_2100 > 0", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
