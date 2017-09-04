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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest71 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "select * from (\n" +
                        "  select a.*,rownum rn from (SELECT\n" +
                        "  A.ID,A.SIM_NO,A.ICCID,A.IMSI,A.IMEI,A.OPERATOR,\n" +
                        "  A.CHARGES_PLAN_ID,A.USED_TRAFFIC,\n" +
                        "  A.STATUS,A.MEMO,A.TYPE,A.TER_ID,B.ID DEVICE_ID,C.TER_CODE,\n" +
                        "  D.ID VECL_ID,D.VIN VECL_VIN,D.LPN VECL_LPN,D.LPC VECL_LPC,E.APP_KEY\n" +
                        "  FROM\n" +
                        "  BAS_SIM A LEFT JOIN BAS_DEVICE B ON A.TER_ID=B.TER_ID\n" +
                        "  LEFT JOIN BAS_TERMINAL C ON A.TER_ID=C.ID\n" +
                        "  LEFT JOIN BAS_VEHICLE D ON B.VECL_ID=D.ID\n" +
                        "  LEFT JOIN BAS_APPLICATION E ON A.APP_ID=E.ID\n" +
                        "   WHERE 1=1\n" +
                        "   \n" +
                        "   \n" +
                        "    AND A.SIM_NO LIKE '%'||?||'%' \n" +
                        "  order by A.SIM_NO ASC ) a\n" +
                        "\n" +
                        "  ) where rn > ?*(?-1) and rn <= ?*?"; //

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

        assertEquals(5, visitor.getTables().size());

        assertEquals(24, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT *\n" +
                    "FROM (\n" +
                    "\tSELECT a.*, rownum AS rn\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT A.ID, A.SIM_NO, A.ICCID, A.IMSI, A.IMEI\n" +
                    "\t\t\t, A.OPERATOR, A.CHARGES_PLAN_ID, A.USED_TRAFFIC, A.STATUS, A.MEMO\n" +
                    "\t\t\t, A.TYPE, A.TER_ID, B.ID AS DEVICE_ID, C.TER_CODE, D.ID AS VECL_ID\n" +
                    "\t\t\t, D.VIN AS VECL_VIN, D.LPN AS VECL_LPN, D.LPC AS VECL_LPC, E.APP_KEY\n" +
                    "\t\tFROM BAS_SIM A\n" +
                    "\t\tLEFT JOIN BAS_DEVICE B ON A.TER_ID = B.TER_ID \n" +
                    "\t\tLEFT JOIN BAS_TERMINAL C ON A.TER_ID = C.ID \n" +
                    "\t\tLEFT JOIN BAS_VEHICLE D ON B.VECL_ID = D.ID \n" +
                    "\t\t\tLEFT JOIN BAS_APPLICATION E ON A.APP_ID = E.ID \n" +
                    "\t\tWHERE 1 = 1\n" +
                    "\t\t\tAND A.SIM_NO LIKE ('%' || ? || '%')\n" +
                    "\t\tORDER BY A.SIM_NO ASC\n" +
                    "\t) a\n" +
                    ")\n" +
                    "WHERE rn > ? * (? - 1)\n" +
                    "\tAND rn <= ? * ?", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
