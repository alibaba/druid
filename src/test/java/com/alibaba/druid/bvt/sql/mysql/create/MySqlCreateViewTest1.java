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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;

import java.util.List;

public class MySqlCreateViewTest1 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE ALGORITHM=UNDEFINED DEFINER=root@localhost SQL SECURITY DEFINER VIEW view_audit_enroll AS " +
                "SELECT a.enroll_id AS 'enrollId', case when ((SELECT audit FROM actvty_audit WHERE enroll_id = a.enroll_id AND rankjurisdiction = 1) > 0) then \"县站已审核\" else NULL end AS 'countyAudit', case when ((SELECT audit FROM actvty_audit WHERE enroll_id = a.enroll_id AND rankjurisdiction = 2) > 0) then \"市馆已审核\" else NULL end AS 'cityAudit', case when ((SELECT audit FROM actvty_audit WHERE enroll_id = a.enroll_id AND rankjurisdiction = 3) > 0) then \"省馆已审核\" else NULL end AS 'provinceAudit' FROM actvty_audit a GROUP BY a.enroll_id";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateViewStatement stmt = (SQLCreateViewStatement) statementList.get(0);
//        print(statementList);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE ALGORITHM = UNDEFINED\n" +
                        "\tDEFINER = root@localhost\n" +
                        "\tSQL SECURITY = DEFINER\n" +
                        "\tVIEW view_audit_enroll\n" +
                        "AS\n" +
                        "SELECT a.enroll_id AS 'enrollId'\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN (\n" +
                        "\t\t\tSELECT audit\n" +
                        "\t\t\tFROM actvty_audit\n" +
                        "\t\t\tWHERE enroll_id = a.enroll_id\n" +
                        "\t\t\t\tAND rankjurisdiction = 1\n" +
                        "\t\t) > 0 THEN '县站已审核'\n" +
                        "\t\tELSE NULL\n" +
                        "\tEND AS 'countyAudit'\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN (\n" +
                        "\t\t\tSELECT audit\n" +
                        "\t\t\tFROM actvty_audit\n" +
                        "\t\t\tWHERE enroll_id = a.enroll_id\n" +
                        "\t\t\t\tAND rankjurisdiction = 2\n" +
                        "\t\t) > 0 THEN '市馆已审核'\n" +
                        "\t\tELSE NULL\n" +
                        "\tEND AS 'cityAudit'\n" +
                        "\t, CASE \n" +
                        "\t\tWHEN (\n" +
                        "\t\t\tSELECT audit\n" +
                        "\t\t\tFROM actvty_audit\n" +
                        "\t\t\tWHERE enroll_id = a.enroll_id\n" +
                        "\t\t\t\tAND rankjurisdiction = 3\n" +
                        "\t\t) > 0 THEN '省馆已审核'\n" +
                        "\t\tELSE NULL\n" +
                        "\tEND AS 'provinceAudit'\n" +
                        "FROM actvty_audit a\n" +
                        "GROUP BY a.enroll_id", //
                SQLUtils.toMySqlString(stmt));

        Assert.assertEquals("create algorithm = UNDEFINED\n" +
                        "\tdefiner = root@localhost\n" +
                        "\tsql security = DEFINER\n" +
                        "\tview view_audit_enroll\n" +
                        "as\n" +
                        "select a.enroll_id as 'enrollId'\n" +
                        "\t, case \n" +
                        "\t\twhen (\n" +
                        "\t\t\tselect audit\n" +
                        "\t\t\tfrom actvty_audit\n" +
                        "\t\t\twhere enroll_id = a.enroll_id\n" +
                        "\t\t\t\tand rankjurisdiction = 1\n" +
                        "\t\t) > 0 then '县站已审核'\n" +
                        "\t\telse null\n" +
                        "\tend as 'countyAudit'\n" +
                        "\t, case \n" +
                        "\t\twhen (\n" +
                        "\t\t\tselect audit\n" +
                        "\t\t\tfrom actvty_audit\n" +
                        "\t\t\twhere enroll_id = a.enroll_id\n" +
                        "\t\t\t\tand rankjurisdiction = 2\n" +
                        "\t\t) > 0 then '市馆已审核'\n" +
                        "\t\telse null\n" +
                        "\tend as 'cityAudit'\n" +
                        "\t, case \n" +
                        "\t\twhen (\n" +
                        "\t\t\tselect audit\n" +
                        "\t\t\tfrom actvty_audit\n" +
                        "\t\t\twhere enroll_id = a.enroll_id\n" +
                        "\t\t\t\tand rankjurisdiction = 3\n" +
                        "\t\t) > 0 then '省馆已审核'\n" +
                        "\t\telse null\n" +
                        "\tend as 'provinceAudit'\n" +
                        "from actvty_audit a\n" +
                        "group by a.enroll_id", //
                SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("actvty_audit")));

        Assert.assertTrue(visitor.getColumns().contains(new Column("actvty_audit", "audit")));
        Assert.assertTrue(visitor.getColumns().contains(new Column("actvty_audit", "enroll_id")));
    }
}
