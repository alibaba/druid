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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class OracleCreateProcedureTest3 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "PROCEDURE PRC_DEALSMS IS\n" +
                "  LV_HOUR NUMBER; --??\n" +
                "BEGIN\n" +
                "  SELECT TO_CHAR(SYSDATE, 'HH24') INTO LV_HOUR FROM DUAL;\n" +
                "  IF LV_HOUR > 8 AND LV_HOUR < 19 THEN\n" +
                "    UPDATE CRMSMS_MT_SEND T\n" +
                "       SET T.SENDFLAG = 0, T.FLAGID = NULL\n" +
                "     WHERE T.SENDFLAG = 2\n" +
                "       AND TRUNC(T.SENDREALTIME) = TRUNC(SYSDATE);\n" +
                "    COMMIT;\n" +
                "  END IF;\n" +
                "END PRC_DEALSMS;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("CRMSMS_MT_SEND")));

        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(2, visitor.getConditions().size());
        Assert.assertEquals(0, visitor.getRelationships().size());

         Assert.assertTrue(visitor.containsColumn("CRMSMS_MT_SEND", "SENDFLAG"));
         Assert.assertTrue(visitor.containsColumn("CRMSMS_MT_SEND", "FLAGID"));
    }
}
