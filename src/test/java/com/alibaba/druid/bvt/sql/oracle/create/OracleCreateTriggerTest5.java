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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateTriggerTest5 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "CREATE OR REPLACE TRIGGER XONN.GAPM_PROJECT_INFO_SYNC_IOA_t\n" +
                        "\tBEFORE INSERT\n" +
                        "\tON XONN.GAPM_PROJECT_INFO_SYNC_IOA\n" +
                        "\tFOR EACH ROW\n" +
                        "BEGIN\n" +
                        "\tSELECT XONN.GAPM_PROJECT_INFO_SYNC_IOA_S.Nextval\n" +
                        "\tINTO :New.PROJECT_SYN_ID\n" +
                        "\tFROM dual;\n" +
                        "END;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE TRIGGER XONN.GAPM_PROJECT_INFO_SYNC_IOA_t\n" +
                        "\tBEFORE INSERT\n" +
                        "\tON XONN.GAPM_PROJECT_INFO_SYNC_IOA\n" +
                        "\tFOR EACH ROW\n" +
                        "BEGIN\n" +
                        "\tSELECT XONN.GAPM_PROJECT_INFO_SYNC_IOA_S.Nextval\n" +
                        "\tINTO :New.PROJECT_SYN_ID\n" +
                        "\tFROM dual;\n" +
                        "END;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        Assert.assertEquals("CREATE OR REPLACE TRIGGER XONN.GAPM_PROJECT_INFO_SYNC_IOA_t\n" +
                        "\tBEFORE INSERT\n" +
                        "\tON XONN.GAPM_PROJECT_INFO_SYNC_IOA\n" +
                        "\tFOR EACH ROW\n" +
                        "BEGIN\n" +
                        "\tSELECT XONN.GAPM_PROJECT_INFO_SYNC_IOA_S.Nextval\n" +
                        "\tINTO :New.PROJECT_SYN_ID\n" +
                        "\tFROM dual;\n" +
                        "END;",//
                SQLUtils.toSQLString(stmt, JdbcConstants.POSTGRESQL));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
