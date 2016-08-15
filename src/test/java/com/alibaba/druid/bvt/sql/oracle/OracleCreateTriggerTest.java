/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OracleCreateTriggerTest extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "CREATE TRIGGER \"AO_4AEACD_WEBHOOK_D367380484\" " //
                + "BEFORE INSERT"//
                + "    ON \"AO_4AEACD_WEBHOOK_DAO\"   FOR EACH ROW "//
                + "BEGIN"//
                + "    SELECT \"AO_4AEACD_WEBHOOK_DAO_ID_SEQ\".NEXTVAL INTO :NEW.\"ID\" FROM DUAL;"//
                + "END;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TRIGGER \"AO_4AEACD_WEBHOOK_D367380484\"" //
                            + "\n\tBEFORE INSERT"//
                            + "\n\tON \"AO_4AEACD_WEBHOOK_DAO\""//
                            + "\n\tFOR EACH ROW"//
                            + "\nBEGIN"//
                            + "\n\tSELECT \"AO_4AEACD_WEBHOOK_DAO_ID_SEQ\".NEXTVAL"//
                            + "\n\tINTO :NEW.\"ID\"" //
                            + "\n\tFROM DUAL;" + "\nEND",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(0, visitor.getTables().size());

        // Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("cdc.en_complaint_ipr_stat_fdt0")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
