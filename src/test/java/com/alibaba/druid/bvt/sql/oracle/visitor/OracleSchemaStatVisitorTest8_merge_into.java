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
package com.alibaba.druid.bvt.sql.oracle.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class OracleSchemaStatVisitorTest8_merge_into extends TestCase {
    private final static String dbType = JdbcConstants.ORACLE;

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

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, dbType);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

        System.out.println(stmt.toString());

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("relationShip : " + visitor.getRelationships());
        System.out.println("where : " + visitor.getConditions());
        System.out.println("groupBy : " + visitor.getGroupByColumns());

        assertEquals(2, visitor.getTables().size());
        assertTrue(visitor.containsTable("KPI_M_CW_INCOME_FACT_BAK"));
        assertTrue(visitor.containsTable("M_HEALTH_APPRAISE_LOAD"));
//
        assertEquals(7, visitor.getColumns().size());
        assertTrue(visitor.containsColumn("M_HEALTH_APPRAISE_LOAD", "*"));
        assertTrue(visitor.containsColumn("M_HEALTH_APPRAISE_LOAD", "THE_DATE"));
        assertTrue(visitor.containsColumn("M_HEALTH_APPRAISE_LOAD", "AREA_LEVEL"));
        assertTrue(visitor.containsColumn("KPI_M_CW_INCOME_FACT_BAK", "*"));
        assertTrue(visitor.containsColumn("KPI_M_CW_INCOME_FACT_BAK", "THE_DATE"));
        assertTrue(visitor.containsColumn("KPI_M_CW_INCOME_FACT_BAK", "AREA_LEVEL"));
        assertTrue(visitor.containsColumn("KPI_M_CW_INCOME_FACT_BAK", "TYPE_ID"));

    }

}
