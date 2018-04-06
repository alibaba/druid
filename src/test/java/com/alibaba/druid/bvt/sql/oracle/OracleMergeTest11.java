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
package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLMergeStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Assert;

import java.util.List;

public class OracleMergeTest11 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "MERGE INTO (\n" +
                "SELECT *\n" +
                "FROM KPI_M_CW_INCOME_FACT_BAK\n" +
                "WHERE THE_DATE = '{THISMONTH}'\n" +
                "AND AREA_LEVEL <= 1\n" +
                "AND TYPE_ID = '2'\n" +
                ") A\n" +
                "USING (\n" +
                "SELECT *\n" +
                "FROM M_HEALTH_APPRAISE_LOAD\n" +
                "WHERE THE_DATE = TRUNC(SYSDATE)\n" +
                "AND AREA_LEVEL <= 1\n" +
                ") B ON (A.AREA_ID = B.AREA_ID\n" +
                "AND A.AREA_LEVEL = B.AREA_LEVEL)\n" +
                "WHEN MATCHED THEN UPDATE SET A.SUM_CHRG_YS = ROUND(B.TOTAL_CHARGE * 1.00 / 10000, 2), A.CHARGE = B.THIS_CHARGE;";

        SQLStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLMergeStatement mergeStatement = (SQLMergeStatement) stmtList.get(0);
        String result = SQLUtils.toOracleString(mergeStatement);
        Assert.assertEquals("MERGE INTO (\n" +
                        "\tSELECT *\n" +
                        "\tFROM KPI_M_CW_INCOME_FACT_BAK\n" +
                        "\tWHERE THE_DATE = '{THISMONTH}'\n" +
                        "\t\tAND AREA_LEVEL <= 1\n" +
                        "\t\tAND TYPE_ID = '2'\n" +
                        ") A\n" +
                        "USING (\n" +
                        "\tSELECT *\n" +
                        "\tFROM M_HEALTH_APPRAISE_LOAD\n" +
                        "\tWHERE THE_DATE = TRUNC(SYSDATE)\n" +
                        "\t\tAND AREA_LEVEL <= 1\n" +
                        ") B ON (A.AREA_ID = B.AREA_ID\n" +
                        "AND A.AREA_LEVEL = B.AREA_LEVEL) \n" +
                        "WHEN MATCHED THEN UPDATE SET A.SUM_CHRG_YS = ROUND(B.TOTAL_CHARGE * 1.00 / 10000, 2), A.CHARGE = B.THIS_CHARGE;",
                            result);

        SQLSelect select = ((SQLSubqueryTableSource)mergeStatement.getInto()).getSelect();
        assertEquals("SELECT *\n" +
                "FROM KPI_M_CW_INCOME_FACT_BAK\n" +
                "WHERE THE_DATE = '{THISMONTH}'\n" +
                "\tAND AREA_LEVEL <= 1\n" +
                "\tAND TYPE_ID = '2'", select.toString());

        SQLUpdateSetItem updateSetItem = mergeStatement.getUpdateClause().getItems().get(0);
        SQLExpr value = updateSetItem.getValue();

        assertEquals("ROUND(B.TOTAL_CHARGE * 1.00 / 10000, 2)", value.toString());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "salary")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "employee_id")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("bonuses", "bonus")));
    }

}
