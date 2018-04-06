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

public class OracleSelectTest68 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "INSERT INTO SB_ZZS_LDSK_2016 SELECT DJXH, :B1 , NVL(SUM(CASE WHEN ZZSLX = '2' AND LDLX = '1' THEN LDSE END), 0) SQLDSE_BQ, NVL(SUM(CASE WHEN ZZSLX = '1' AND LDLX = '1' THEN LDSE END), 0) SQLDSE_LJ, NVL(SUM(CASE WHEN LDLX = '2' THEN LDSE END), 0) SQLDSE_JZ, 0, 0, 0,SYSDATE FROM (SELECT ZZSLX, LDLX, DJXH, SUM(BCLDBHS) LDSE FROM (SELECT ZZSLX, LDLX, DJXH, SUM(T.BCLDBHS) BCLDBHS FROM HX_SB.SB_ZZS_LDSKLSZ@JSCX T WHERE T.SKSSQZ < ADD_MONTHS(:B1 ,-1)+1 AND DJXH IN (SELECT NSRDZDAH FROM DJ_NSRXX_JS_GZSB) AND T.CZLX_DM IN ('10', '66', '68', '53', '56', '61', '64') GROUP BY ZZSLX, LDLX, DJXH UNION ALL SELECT ZZSLX, LDLX, DJXH, -SUM(T.BCLDBHS) BCLDBHS FROM HX_SB.SB_ZZS_LDSKLSZ@JSCX T WHERE T.SKSSQZ = ADD_MONTHS(:B1 ,-1) AND DJXH IN (SELECT NSRDZDAH FROM DJ_NSRXX_JS_GZSB) AND T.CZLX_DM IN ('53', '56', '61', '64') GROUP BY ZZSLX, LDLX, DJXH) GROUP BY ZZSLX, LDLX, DJXH) A GROUP BY DJXH"; //

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

        Assert.assertEquals(3, visitor.getTables().size());

//        Assert.assertEquals(10, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("INSERT INTO SB_ZZS_LDSK_2016\n" +
                    "SELECT DJXH, :B1\n" +
                    "\t, NVL(SUM(CASE \n" +
                    "\t\tWHEN ZZSLX = '2'\n" +
                    "\t\tAND LDLX = '1' THEN LDSE\n" +
                    "\tEND), 0) AS SQLDSE_BQ\n" +
                    "\t, NVL(SUM(CASE \n" +
                    "\t\tWHEN ZZSLX = '1'\n" +
                    "\t\tAND LDLX = '1' THEN LDSE\n" +
                    "\tEND), 0) AS SQLDSE_LJ\n" +
                    "\t, NVL(SUM(CASE \n" +
                    "\t\tWHEN LDLX = '2' THEN LDSE\n" +
                    "\tEND), 0) AS SQLDSE_JZ\n" +
                    "\t, 0, 0, 0, SYSDATE\n" +
                    "FROM (\n" +
                    "\tSELECT ZZSLX, LDLX, DJXH, SUM(BCLDBHS) AS LDSE\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT ZZSLX, LDLX, DJXH, SUM(T.BCLDBHS) AS BCLDBHS\n" +
                    "\t\tFROM HX_SB.SB_ZZS_LDSKLSZ@JSCX T\n" +
                    "\t\tWHERE T.SKSSQZ < ADD_MONTHS(:B1, -1) + 1\n" +
                    "\t\t\tAND DJXH IN (\n" +
                    "\t\t\t\tSELECT NSRDZDAH\n" +
                    "\t\t\t\tFROM DJ_NSRXX_JS_GZSB\n" +
                    "\t\t\t)\n" +
                    "\t\t\tAND T.CZLX_DM IN (\n" +
                    "\t\t\t\t'10', \n" +
                    "\t\t\t\t'66', \n" +
                    "\t\t\t\t'68', \n" +
                    "\t\t\t\t'53', \n" +
                    "\t\t\t\t'56', \n" +
                    "\t\t\t\t'61', \n" +
                    "\t\t\t\t'64'\n" +
                    "\t\t\t)\n" +
                    "\t\tGROUP BY ZZSLX, LDLX, DJXH\n" +
                    "\t\tUNION ALL\n" +
                    "\t\tSELECT ZZSLX, LDLX, DJXH, -SUM(T.BCLDBHS) AS BCLDBHS\n" +
                    "\t\tFROM HX_SB.SB_ZZS_LDSKLSZ@JSCX T\n" +
                    "\t\tWHERE T.SKSSQZ = ADD_MONTHS(:B1, -1)\n" +
                    "\t\t\tAND DJXH IN (\n" +
                    "\t\t\t\tSELECT NSRDZDAH\n" +
                    "\t\t\t\tFROM DJ_NSRXX_JS_GZSB\n" +
                    "\t\t\t)\n" +
                    "\t\t\tAND T.CZLX_DM IN ('53', '56', '61', '64')\n" +
                    "\t\tGROUP BY ZZSLX, LDLX, DJXH\n" +
                    "\t)\n" +
                    "\tGROUP BY ZZSLX, LDLX, DJXH\n" +
                    ") A\n" +
                    "GROUP BY DJXH", text);
        }

        {
            String text = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);

            Assert.assertEquals("insert into SB_ZZS_LDSK_2016\n" +
                    "select DJXH, :B1\n" +
                    "\t, NVL(sum(case \n" +
                    "\t\twhen ZZSLX = '2'\n" +
                    "\t\tand LDLX = '1' then LDSE\n" +
                    "\tend), 0) as SQLDSE_BQ\n" +
                    "\t, NVL(sum(case \n" +
                    "\t\twhen ZZSLX = '1'\n" +
                    "\t\tand LDLX = '1' then LDSE\n" +
                    "\tend), 0) as SQLDSE_LJ\n" +
                    "\t, NVL(sum(case \n" +
                    "\t\twhen LDLX = '2' then LDSE\n" +
                    "\tend), 0) as SQLDSE_JZ\n" +
                    "\t, 0, 0, 0, sysdate\n" +
                    "from (\n" +
                    "\tselect ZZSLX, LDLX, DJXH, sum(BCLDBHS) as LDSE\n" +
                    "\tfrom (\n" +
                    "\t\tselect ZZSLX, LDLX, DJXH, sum(T.BCLDBHS) as BCLDBHS\n" +
                    "\t\tfrom HX_SB.SB_ZZS_LDSKLSZ@JSCX T\n" +
                    "\t\twhere T.SKSSQZ < ADD_MONTHS(:B1, -1) + 1\n" +
                    "\t\t\tand DJXH in (\n" +
                    "\t\t\t\tselect NSRDZDAH\n" +
                    "\t\t\t\tfrom DJ_NSRXX_JS_GZSB\n" +
                    "\t\t\t)\n" +
                    "\t\t\tand T.CZLX_DM in (\n" +
                    "\t\t\t\t'10', \n" +
                    "\t\t\t\t'66', \n" +
                    "\t\t\t\t'68', \n" +
                    "\t\t\t\t'53', \n" +
                    "\t\t\t\t'56', \n" +
                    "\t\t\t\t'61', \n" +
                    "\t\t\t\t'64'\n" +
                    "\t\t\t)\n" +
                    "\t\tgroup by ZZSLX, LDLX, DJXH\n" +
                    "\t\tunion all\n" +
                    "\t\tselect ZZSLX, LDLX, DJXH, -sum(T.BCLDBHS) as BCLDBHS\n" +
                    "\t\tfrom HX_SB.SB_ZZS_LDSKLSZ@JSCX T\n" +
                    "\t\twhere T.SKSSQZ = ADD_MONTHS(:B1, -1)\n" +
                    "\t\t\tand DJXH in (\n" +
                    "\t\t\t\tselect NSRDZDAH\n" +
                    "\t\t\t\tfrom DJ_NSRXX_JS_GZSB\n" +
                    "\t\t\t)\n" +
                    "\t\t\tand T.CZLX_DM in ('53', '56', '61', '64')\n" +
                    "\t\tgroup by ZZSLX, LDLX, DJXH\n" +
                    "\t)\n" +
                    "\tgroup by ZZSLX, LDLX, DJXH\n" +
                    ") A\n" +
                    "group by DJXH", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
