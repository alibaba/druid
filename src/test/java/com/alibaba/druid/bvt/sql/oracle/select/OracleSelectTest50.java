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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.test.TestUtils;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest50 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "SELECT * from ( SELECT ID,MATTER,CODE,NAME,ADDRESS,AREA,PROPOSER,PROPOSER_CONTACTOR,PROPOSER_PHONE,REG_TIME,ASSIGN_TIME,DEPART,HANDLER,HANDLER_PHONE,STATUS,FINISH_DATE,FINISH_TYPE,DEPART_CODE,SYSTEM_CODE,rownum num FROM gxpt_items WHERE rownum<=20  order by REG_TIME desc )  WHERE num>0"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertEquals(19, visitor.getColumns().size());

        String text = TestUtils.outputOracle(stmt);

        Assert.assertEquals("SELECT *"//
                            + "\nFROM (" //
                            + "\n\tSELECT ID, MATTER, CODE, NAME, ADDRESS"//
                            + "\n\t\t, AREA, PROPOSER, PROPOSER_CONTACTOR, PROPOSER_PHONE, REG_TIME"//
                            + "\n\t\t, ASSIGN_TIME, DEPART, HANDLER, HANDLER_PHONE, STATUS"//
                            + "\n\t\t, FINISH_DATE, FINISH_TYPE, DEPART_CODE, SYSTEM_CODE, rownum AS num"//
                            + "\n\tFROM gxpt_items"//
                            + "\n\tWHERE rownum <= 20"//
                            + "\n\tORDER BY REG_TIME DESC"//
                            + "\n)"//
                            + "\nWHERE num > 0", text);

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
