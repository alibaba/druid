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

import com.alibaba.druid.sql.DB2Test;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2InsertTest_1 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "INSERT INTO MK.KPI_AREA_SORT_FACT_LATN_ID_MID\n" +
                "SELECT LATN_ID,BUREAU_KEY,\n" +
                "       SUM(ADD_SUM),\n" +
                "       SUM(USER_ACCT),\n" +
                "       SUM(USER_ACCT_LY),\n" +
                "       1 \n" +
                "  FROM (\n" +
                "SELECT LATN_ID,\n" +
                "  CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END BUREAU_KEY,\n" +
                "       SUM(COALESCE(USER_CNT_ADD, 0)) ADD_SUM, \n" +
                "       0 USER_ACCT,\n" +
                "       0 USER_ACCT_LY\n" +
                "  FROM MK.M_USER_COUNT_FACT_CDMA\n" +
                " WHERE THE_DATE BETWEEN SUBSTR('{THISMONTH}', 1, 4) || '-01-01' AND '{THISMONTH}'\n" +
                "   AND PAG_FLAG = 0\n" +
                " GROUP BY LATN_ID,\n" +
                "  CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END\n" +
                "\n" +
                "UNION ALL\n" +
                "SELECT LATN_ID,\n" +
                "  CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END BUREAU_KEY,\n" +
                "       0 ADD_SUM,\n" +
                "       SUM(COALESCE(USER_CNT_AMOUNT, 0)) USER_ACCT, \n" +
                "       0 USER_ACCT_LY\n" +
                "  FROM MK.M_USER_COUNT_FACT_CDMA\n" +
                " WHERE THE_DATE = '{THISMONTH}'\n" +
                "   AND PAG_FLAG = 0\n" +
                " GROUP BY LATN_ID,\n" +
                "  CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END\n" +
                "  \n" +
                "UNION ALL\n" +
                "SELECT LATN_ID,\n" +
                "  CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END BUREAU_KEY,\n" +
                "       0 ADD_SUM,\n" +
                "       0 USER_ACCT,\n" +
                "       SUM(COALESCE(USER_CNT_AMOUNT, 0)) USER_ACCT_LY  \n" +
                "  FROM MK.M_USER_COUNT_FACT_CDMA\n" +
                " WHERE THE_DATE = DATE(SUBSTR('{THISMONTH}', 1, 4) || '-01-01') - 1 MONTHS\n" +
                "   AND PAG_FLAG = 0 \n" +
                " GROUP BY LATN_ID,\n" +
                "  CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END\n" +
                ") T\n" +
                "GROUP BY LATN_ID,BUREAU_KEY\n" +
                " WITH UR\n" +
                ";";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(11, visitor.getColumns().size());
        Assert.assertEquals(4, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("MK.KPI_AREA_SORT_FACT_LATN_ID_MID")));

         Assert.assertTrue(visitor.getColumns().contains(new Column("MK.M_USER_COUNT_FACT_CDMA", "LATN_ID")));
//         Assert.assertTrue(visitor.getColumns().contains(new Column("t", "name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        String output = SQLUtils.toSQLString(stmt, JdbcConstants.DB2);
        Assert.assertEquals("INSERT INTO MK.KPI_AREA_SORT_FACT_LATN_ID_MID\n" +
                        "SELECT LATN_ID, BUREAU_KEY, SUM(ADD_SUM)\n" +
                        "\t, SUM(USER_ACCT), SUM(USER_ACCT_LY)\n" +
                        "\t, 1\n" +
                        "FROM (\n" +
                        "\tSELECT LATN_ID\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\t\tELSE BUREAU_KEY\n" +
                        "\t\tEND AS BUREAU_KEY, SUM(COALESCE(USER_CNT_ADD, 0)) AS ADD_SUM\n" +
                        "\t\t, 0 AS USER_ACCT, 0 AS USER_ACCT_LY\n" +
                        "\tFROM MK.M_USER_COUNT_FACT_CDMA\n" +
                        "\tWHERE THE_DATE BETWEEN SUBSTR('{THISMONTH}', 1, 4) CONCAT '-01-01' AND '{THISMONTH}'\n" +
                        "\t\tAND PAG_FLAG = 0\n" +
                        "\tGROUP BY LATN_ID, CASE \n" +
                        "\t\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\t\tELSE BUREAU_KEY\n" +
                        "\t\tEND\n" +
                        "\tUNION ALL\n" +
                        "\tSELECT LATN_ID\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\t\tELSE BUREAU_KEY\n" +
                        "\t\tEND AS BUREAU_KEY, 0 AS ADD_SUM\n" +
                        "\t\t, SUM(COALESCE(USER_CNT_AMOUNT, 0)) AS USER_ACCT\n" +
                        "\t\t, 0 AS USER_ACCT_LY\n" +
                        "\tFROM MK.M_USER_COUNT_FACT_CDMA\n" +
                        "\tWHERE THE_DATE = '{THISMONTH}'\n" +
                        "\t\tAND PAG_FLAG = 0\n" +
                        "\tGROUP BY LATN_ID, CASE \n" +
                        "\t\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\t\tELSE BUREAU_KEY\n" +
                        "\t\tEND\n" +
                        "\tUNION ALL\n" +
                        "\tSELECT LATN_ID\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\t\tELSE BUREAU_KEY\n" +
                        "\t\tEND AS BUREAU_KEY, 0 AS ADD_SUM, 0 AS USER_ACCT\n" +
                        "\t\t, SUM(COALESCE(USER_CNT_AMOUNT, 0)) AS USER_ACCT_LY\n" +
                        "\tFROM MK.M_USER_COUNT_FACT_CDMA\n" +
                        "\tWHERE THE_DATE = DATE(SUBSTR('{THISMONTH}', 1, 4) CONCAT '-01-01') - 1 MONTHS\n" +
                        "\t\tAND PAG_FLAG = 0\n" +
                        "\tGROUP BY LATN_ID, CASE \n" +
                        "\t\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\t\tELSE BUREAU_KEY\n" +
                        "\t\tEND\n" +
                        ") T\n" +
                        "GROUP BY LATN_ID, BUREAU_KEY\n" +
                        "WITH UR;", //
                            output);
    }
}
