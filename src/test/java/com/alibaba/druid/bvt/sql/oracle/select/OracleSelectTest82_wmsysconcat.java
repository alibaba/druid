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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest82_wmsysconcat extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT supReg.regId,supReg.Title,supReg.Docnum,supReg.Itemtype,\n" +
                        "supReg.urgent,supReg.Feedbackdate,supReg.Feedbackperoid,\n" +
                        "supReg.Feedbackday ,supReg.dateSend,\n" +
                        "supReg.status ,supReg.analysed,supReg.feedbackWay,t.note,\n" +
                        "COUNT(distinct supReg.regId) OVER() TOTALCOUNT\n" +
                        "from sup_registration supReg\n" +
                        "inner join (select st.regid, to_char(wmsys.WM_CONCAT(distinct so.orgname)) as note from sup_task st\n" +
                        "inner join sys_org so on st.orgid=so.orgid\n" +
                        "group by st.regid ) t on t.regid = supReg.regid\n" +
                        "WHERE supReg.status in (2,3,4) and supType='0'\n" +
                        "\n"; //

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);



        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT supReg.regId, supReg.Title, supReg.Docnum, supReg.Itemtype, supReg.urgent\n" +
                    "\t, supReg.Feedbackdate, supReg.Feedbackperoid, supReg.Feedbackday, supReg.dateSend, supReg.status\n" +
                    "\t, supReg.analysed, supReg.feedbackWay, t.note, COUNT(DISTINCT supReg.regId) OVER () AS TOTALCOUNT\n" +
                    "FROM sup_registration supReg\n" +
                    "\tINNER JOIN (\n" +
                    "\t\tSELECT st.regid, to_char(wmsys.WM_CONCAT(DISTINCT so.orgname)) AS note\n" +
                    "\t\tFROM sup_task st\n" +
                    "\t\t\tINNER JOIN sys_org so ON st.orgid = so.orgid \n" +
                    "\t\tGROUP BY st.regid\n" +
                    "\t) t ON t.regid = supReg.regid \n" +
                    "WHERE supReg.status IN (2, 3, 4)\n" +
                    "\tAND supReg.supType = '0'", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(3, visitor.getTables().size());
        assertEquals(17, visitor.getColumns().size());
        assertEquals(5, visitor.getConditions().size());
        assertEquals(1, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        Assert.assertTrue(visitor.containsTable("sup_registration"));
        Assert.assertTrue(visitor.containsTable("sup_task"));
        Assert.assertTrue(visitor.containsTable("sys_org"));

         Assert.assertTrue(visitor.containsColumn("sup_task", "orgid"));
         Assert.assertTrue(visitor.containsColumn("sup_task", "orgid"));
//
    }
}
