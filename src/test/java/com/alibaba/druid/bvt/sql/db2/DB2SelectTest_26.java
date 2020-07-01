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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class DB2SelectTest_26 extends DB2Test {

    public void test_0() throws Exception {
        String sql = "SELECT LATN_ID,\n" +
                "CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END BUREAU_KEY,\n" +
                "SUM(COALESCE(ADD_USER_CNT, 0)) ADD_SUM,\n" +
                "0 USER_ACCT,\n" +
                "0 USER_ACCT_LY\n" +
                "FROM MK.M_BROAD_BAND_USER_FACT\n" +
                "WHERE THE_DATE BETWEEN SUBSTR('{THISMONTH}', 1, 4) || '-01-01' AND '{THISMONTH}'\n" +
                "GROUP BY LATN_ID,\n" +
                "CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END\n" +
                "UNION ALL\n" +
                "SELECT LATN_ID,\n" +
                "CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END BUREAU_KEY,\n" +
                "0 ADD_SUM,\n" +
                "SUM(COALESCE(BILL_USER_CNT, 0)) USER_ACCT,\n" +
                "0 USER_ACCT_LY\n" +
                "FROM MK.M_BROAD_BAND_USER_FACT\n" +
                "WHERE THE_DATE = '{THISMONTH}'\n" +
                "GROUP BY LATN_ID,\n" +
                "CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END\n" +
                "UNION ALL\n" +
                "SELECT LATN_ID,\n" +
                "CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END BUREAU_KEY,\n" +
                "0 ADD_SUM,\n" +
                "0 USER_ACCT,\n" +
                "SUM(COALESCE(BILL_USER_CNT, 0)) USER_ACCT_LY\n" +
                "FROM MK.M_BROAD_BAND_USER_FACT\n" +
                "WHERE THE_DATE = DATE(SUBSTR('{THISMONTH}', 1, 4) || '-01-01') - 1 MONTHS\n" +
                "GROUP BY LATN_ID,\n" +
                "CASE WHEN BUREAU_KEY = 116 THEN 46 ELSE BUREAU_KEY END";

        DB2StatementParser parser = new DB2StatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.getSelect().getQuery());

        Assert.assertEquals(1, statementList.size());

        DB2SchemaStatVisitor visitor = new DB2SchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(3, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("MK.M_BROAD_BAND_USER_FACT")));

//         Assert.assertTrue(visitor.getColumns().contains(new Column("DSN8B10.EMP", "WORKDEPT")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "first_name")));
        // Assert.assertTrue(visitor.getColumns().contains(new Column("mytable", "full_name")));

        Assert.assertEquals("SELECT LATN_ID\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\tELSE BUREAU_KEY\n" +
                        "\tEND AS BUREAU_KEY, SUM(COALESCE(ADD_USER_CNT, 0)) AS ADD_SUM\n" +
                        "\t, 0 AS USER_ACCT, 0 AS USER_ACCT_LY\n" +
                        "FROM MK.M_BROAD_BAND_USER_FACT\n" +
                        "WHERE THE_DATE BETWEEN SUBSTR('{THISMONTH}', 1, 4) CONCAT '-01-01' AND '{THISMONTH}'\n" +
                        "GROUP BY LATN_ID, CASE \n" +
                        "\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\tELSE BUREAU_KEY\n" +
                        "\tEND\n" +
                        "UNION ALL\n" +
                        "SELECT LATN_ID\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\tELSE BUREAU_KEY\n" +
                        "\tEND AS BUREAU_KEY, 0 AS ADD_SUM\n" +
                        "\t, SUM(COALESCE(BILL_USER_CNT, 0)) AS USER_ACCT\n" +
                        "\t, 0 AS USER_ACCT_LY\n" +
                        "FROM MK.M_BROAD_BAND_USER_FACT\n" +
                        "WHERE THE_DATE = '{THISMONTH}'\n" +
                        "GROUP BY LATN_ID, CASE \n" +
                        "\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\tELSE BUREAU_KEY\n" +
                        "\tEND\n" +
                        "UNION ALL\n" +
                        "SELECT LATN_ID\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\tELSE BUREAU_KEY\n" +
                        "\tEND AS BUREAU_KEY, 0 AS ADD_SUM, 0 AS USER_ACCT\n" +
                        "\t, SUM(COALESCE(BILL_USER_CNT, 0)) AS USER_ACCT_LY\n" +
                        "FROM MK.M_BROAD_BAND_USER_FACT\n" +
                        "WHERE THE_DATE = DATE(SUBSTR('{THISMONTH}', 1, 4) CONCAT '-01-01') - 1 MONTHS\n" +
                        "GROUP BY LATN_ID, CASE \n" +
                        "\t\tWHEN BUREAU_KEY = 116 THEN 46\n" +
                        "\t\tELSE BUREAU_KEY\n" +
                        "\tEND", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2));
        
        Assert.assertEquals("select LATN_ID\n" +
                        "\t, case \n" +
                        "\t\twhen BUREAU_KEY = 116 then 46\n" +
                        "\t\telse BUREAU_KEY\n" +
                        "\tend as BUREAU_KEY, sum(COALESCE(ADD_USER_CNT, 0)) as ADD_SUM\n" +
                        "\t, 0 as USER_ACCT, 0 as USER_ACCT_LY\n" +
                        "from MK.M_BROAD_BAND_USER_FACT\n" +
                        "where THE_DATE between SUBSTR('{THISMONTH}', 1, 4) concat '-01-01' and '{THISMONTH}'\n" +
                        "group by LATN_ID, case \n" +
                        "\t\twhen BUREAU_KEY = 116 then 46\n" +
                        "\t\telse BUREAU_KEY\n" +
                        "\tend\n" +
                        "union all\n" +
                        "select LATN_ID\n" +
                        "\t, case \n" +
                        "\t\twhen BUREAU_KEY = 116 then 46\n" +
                        "\t\telse BUREAU_KEY\n" +
                        "\tend as BUREAU_KEY, 0 as ADD_SUM\n" +
                        "\t, sum(COALESCE(BILL_USER_CNT, 0)) as USER_ACCT\n" +
                        "\t, 0 as USER_ACCT_LY\n" +
                        "from MK.M_BROAD_BAND_USER_FACT\n" +
                        "where THE_DATE = '{THISMONTH}'\n" +
                        "group by LATN_ID, case \n" +
                        "\t\twhen BUREAU_KEY = 116 then 46\n" +
                        "\t\telse BUREAU_KEY\n" +
                        "\tend\n" +
                        "union all\n" +
                        "select LATN_ID\n" +
                        "\t, case \n" +
                        "\t\twhen BUREAU_KEY = 116 then 46\n" +
                        "\t\telse BUREAU_KEY\n" +
                        "\tend as BUREAU_KEY, 0 as ADD_SUM, 0 as USER_ACCT\n" +
                        "\t, sum(COALESCE(BILL_USER_CNT, 0)) as USER_ACCT_LY\n" +
                        "from MK.M_BROAD_BAND_USER_FACT\n" +
                        "where THE_DATE = DATE(SUBSTR('{THISMONTH}', 1, 4) concat '-01-01') - 1 months\n" +
                        "group by LATN_ID, case \n" +
                        "\t\twhen BUREAU_KEY = 116 then 46\n" +
                        "\t\telse BUREAU_KEY\n" +
                        "\tend", //
                            SQLUtils.toSQLString(stmt, JdbcConstants.DB2, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }
}
