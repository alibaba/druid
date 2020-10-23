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
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest38 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "select * from " + "(with vw_kreis_statics_t as"
                     + "  (select substr(xzqh,1,6) xzqh,swrslx,sum(swrs_count) acd_totle from"
                     + "    (select xzqh,sglx,case when (swrs7 <  3) then '1'"
                     + "       when (swrs7 <  5) then '2' when (swrs7 <=  9) then '3' else '4' end  swrslx,1 swrs_count"
                     + "       from acduser.vw_acd_info where sglx='1' " + "       "
                     + "                    and sgfssj  >=   ?" + "                 " + "                 "
                     + "        )" + "   group by substr(xzqh,1,6),swrslx)" + ""
                     + "   select e.\"XZQH\",e.\"LESS3\",e.\"F3TO5\",e.\"F5TO9\",e.\"MORE9\",kreis_code, kreis_name,px1,py1,px2,py2 from"
                     + "    ( select" + "     xzqh," + "     nvl(max(decode(swrslx,'1',acd_totle)),0)  less3,"
                     + "     nvl(max(decode(swrslx,'2',acd_totle)),0)  f3to5,"
                     + "     nvl(max(decode(swrslx,'3',acd_totle)),0)  f5to9,"
                     + "     nvl(max(decode(swrslx,'4',acd_totle)),0)  more9"
                     + "     from( select * from acduser.vw_kreis_statics_t) group by xzqh  " + "     ) e" + ""
                     + "  left join" + " acduser.vw_sc_kreis_code_lv2 f on e.xzqh = f.short_kreis_code) "
                     + "   where kreis_code in" + "(select * from "
                     + "  (select tbek_code from acduser.vw_kreis_code start with tbek_code = ? connect by prior tbek_pk=tbek_parent ) "
                     + "where  tbek_code != ?)"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

//        System.out.println(stmt.toString());

        {
            String result = SQLUtils.toOracleString(stmt);
            Assert.assertEquals("SELECT *\n" +
                    "FROM (\n" +
                    "\tWITH vw_kreis_statics_t AS (\n" +
                    "\t\t\tSELECT substr(xzqh, 1, 6) AS xzqh, swrslx\n" +
                    "\t\t\t\t, sum(swrs_count) AS acd_totle\n" +
                    "\t\t\tFROM (\n" +
                    "\t\t\t\tSELECT xzqh, sglx\n" +
                    "\t\t\t\t\t, CASE \n" +
                    "\t\t\t\t\t\tWHEN swrs7 < 3 THEN '1'\n" +
                    "\t\t\t\t\t\tWHEN swrs7 < 5 THEN '2'\n" +
                    "\t\t\t\t\t\tWHEN swrs7 <= 9 THEN '3'\n" +
                    "\t\t\t\t\t\tELSE '4'\n" +
                    "\t\t\t\t\tEND AS swrslx, 1 AS swrs_count\n" +
                    "\t\t\t\tFROM acduser.vw_acd_info\n" +
                    "\t\t\t\tWHERE sglx = '1'\n" +
                    "\t\t\t\t\tAND sgfssj >= ?\n" +
                    "\t\t\t)\n" +
                    "\t\t\tGROUP BY substr(xzqh, 1, 6), swrslx\n" +
                    "\t\t)\n" +
                    "\tSELECT e.\"XZQH\", e.\"LESS3\", e.\"F3TO5\", e.\"F5TO9\", e.\"MORE9\"\n" +
                    "\t\t, kreis_code, kreis_name, px1, py1, px2\n" +
                    "\t\t, py2\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT xzqh\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '1', acd_totle)), 0) AS less3\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '2', acd_totle)), 0) AS f3to5\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '3', acd_totle)), 0) AS f5to9\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '4', acd_totle)), 0) AS more9\n" +
                    "\t\tFROM (\n" +
                    "\t\t\tSELECT *\n" +
                    "\t\t\tFROM acduser.vw_kreis_statics_t\n" +
                    "\t\t)\n" +
                    "\t\tGROUP BY xzqh\n" +
                    "\t) e\n" +
                    "\t\tLEFT JOIN acduser.vw_sc_kreis_code_lv2 f ON e.xzqh = f.short_kreis_code \n" +
                    ")\n" +
                    "WHERE kreis_code IN (\n" +
                    "\tSELECT *\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT tbek_code\n" +
                    "\t\tFROM acduser.vw_kreis_code\n" +
                    "\t\tSTART WITH tbek_code = ?\n" +
                    "\t\tCONNECT BY PRIOR tbek_pk = tbek_parent\n" +
                    "\t)\n" +
                    "\tWHERE tbek_code != ?\n" +
                    ")", result);
        }
        {
            String result = SQLUtils.toOracleString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select *\n" +
                    "from (\n" +
                    "\twith vw_kreis_statics_t as (\n" +
                    "\t\t\tselect substr(xzqh, 1, 6) as xzqh, swrslx\n" +
                    "\t\t\t\t, sum(swrs_count) as acd_totle\n" +
                    "\t\t\tfrom (\n" +
                    "\t\t\t\tselect xzqh, sglx\n" +
                    "\t\t\t\t\t, case \n" +
                    "\t\t\t\t\t\twhen swrs7 < 3 then '1'\n" +
                    "\t\t\t\t\t\twhen swrs7 < 5 then '2'\n" +
                    "\t\t\t\t\t\twhen swrs7 <= 9 then '3'\n" +
                    "\t\t\t\t\t\telse '4'\n" +
                    "\t\t\t\t\tend as swrslx, 1 as swrs_count\n" +
                    "\t\t\t\tfrom acduser.vw_acd_info\n" +
                    "\t\t\t\twhere sglx = '1'\n" +
                    "\t\t\t\t\tand sgfssj >= ?\n" +
                    "\t\t\t)\n" +
                    "\t\t\tgroup by substr(xzqh, 1, 6), swrslx\n" +
                    "\t\t)\n" +
                    "\tselect e.\"XZQH\", e.\"LESS3\", e.\"F3TO5\", e.\"F5TO9\", e.\"MORE9\"\n" +
                    "\t\t, kreis_code, kreis_name, px1, py1, px2\n" +
                    "\t\t, py2\n" +
                    "\tfrom (\n" +
                    "\t\tselect xzqh\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '1', acd_totle)), 0) as less3\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '2', acd_totle)), 0) as f3to5\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '3', acd_totle)), 0) as f5to9\n" +
                    "\t\t\t, nvl(max(decode(swrslx, '4', acd_totle)), 0) as more9\n" +
                    "\t\tfrom (\n" +
                    "\t\t\tselect *\n" +
                    "\t\t\tfrom acduser.vw_kreis_statics_t\n" +
                    "\t\t)\n" +
                    "\t\tgroup by xzqh\n" +
                    "\t) e\n" +
                    "\t\tleft join acduser.vw_sc_kreis_code_lv2 f on e.xzqh = f.short_kreis_code \n" +
                    ")\n" +
                    "where kreis_code in (\n" +
                    "\tselect *\n" +
                    "\tfrom (\n" +
                    "\t\tselect tbek_code\n" +
                    "\t\tfrom acduser.vw_kreis_code\n" +
                    "\t\tstart with tbek_code = ?\n" +
                    "\t\tconnect by prior tbek_pk = tbek_parent\n" +
                    "\t)\n" +
                    "\twhere tbek_code != ?\n" +
                    ")", result);
        }

        Assert.assertEquals(1, statementList.size());

        System.out.println(stmt);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(4, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("acduser.vw_acd_info")));

        Assert.assertEquals(18, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "sglx")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_sc_kreis_code_lv2", "kreis_code")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
