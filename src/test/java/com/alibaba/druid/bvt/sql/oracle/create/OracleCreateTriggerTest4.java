/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

public class OracleCreateTriggerTest4 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "TRIGGER RRP.TRG_HR_AK_AFTINST AFTER INSERT ON hr_structure FOR EACH ROW\n" +
                "DECLARE\n" +
                "BEGIN\n" +
                "  INSERT INTO hr_structure_temp\n" +
                "    (com_code,CODE,NAME,status,sjcode,\n" +
                "     isdept,type,selfcode,POS,createdate,\n" +
                "     issync,syncdate,deptsale)\n" +
                "  VALUES\n" +
                "    (:NEW.com_code,:NEW.CODE,:NEW.NAME,:NEW.status,:NEW.sjcode,\n" +
                "     :NEW.isdept,:NEW.type,:NEW.selfcode,:NEW.POS,SYSDATE,\n" +
                "     'N',NULL,:NEW.deptsale);\n" +
                "END;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE TRIGGER RRP.TRG_HR_AK_AFTINST\n" +
                        "\tAFTER INSERT\n" +
                        "\tON hr_structure\n" +
                        "\tFOR EACH ROW\n" +
                        "BEGIN\n" +
                        "\tINSERT INTO hr_structure_temp\n" +
                        "\t\t(com_code, CODE, NAME, status, sjcode\n" +
                        "\t\t, isdept, type, selfcode, POS, createdate\n" +
                        "\t\t, issync, syncdate, deptsale)\n" +
                        "\tVALUES (:NEW.com_code, :NEW.CODE, :NEW.NAME, :NEW.status, :NEW.sjcode\n" +
                        "\t\t, :NEW.isdept, :NEW.type, :NEW.selfcode, :NEW.POS, SYSDATE\n" +
                        "\t\t, 'N', NULL, :NEW.deptsale);\n" +
                        "END;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

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
