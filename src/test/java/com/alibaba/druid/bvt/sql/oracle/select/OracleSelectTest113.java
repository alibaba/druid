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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import java.util.List;

public class OracleSelectTest113 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "select zs,ybl,zs-ybl-wxbl wbl,wxbl,xzcs,xscs,dw dwid,pkg_unit.getDwmcById(dw)dwmc from ( select count(1)zs, count(case when l.tbbz = '1' then 1 end)ybl,count(case when l.tbbz = '3' then 1 end)wxbl,count(case when s.a_ajfl='10' and nvl(l.blsj,sysdate)-l.lrsj>1 and l.lrsj>to_date('20150713','yyyymmdd') and l.tbbz!='3' then 1end)xscs, count(case when s.a_ajfl='20' and nvl(l.blsj,sysdate)-l.lrsj>3 and l.lrsj>to_date('20150713','yyyymmdd') and l.tbbz!='3' then 1end)xzcs, substr(sys_dwdm, 0, 8)||'0000' dw from case_m_detail l,case_s_process s where l.a_ajbh=s.a_ajbh and l.sys_dwdm is not null and l.scbz = '0' and l.lrsj >= to_date('2018-01-17','yyyy-mm-dd') and l.lrsj <= to_date('2018-01-24 23:59','yyyy-mm-dd hh24:mi') and l.sys_dwdm like '331126%' group by substr(sys_dwdm, 0, 8)||'0000' ) order by dw";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        System.out.println(statementList.toString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT zs, ybl, zs - ybl - wxbl AS wbl\n" +
                    "\t, wxbl, xzcs, xscs, dw AS dwid\n" +
                    "\t, pkg_unit.getDwmcById(dw) AS dwmc\n" +
                    "FROM (\n" +
                    "\tSELECT count(1) AS zs\n" +
                    "\t\t, count(CASE \n" +
                    "\t\t\tWHEN l.tbbz = '1' THEN 1\n" +
                    "\t\tEND) AS ybl\n" +
                    "\t\t, count(CASE \n" +
                    "\t\t\tWHEN l.tbbz = '3' THEN 1\n" +
                    "\t\tEND) AS wxbl\n" +
                    "\t\t, count(CASE \n" +
                    "\t\t\tWHEN s.a_ajfl = '10'\n" +
                    "\t\t\t\tAND nvl(l.blsj, SYSDATE) - l.lrsj > 1\n" +
                    "\t\t\t\tAND l.lrsj > to_date('20150713', 'yyyymmdd')\n" +
                    "\t\t\t\tAND l.tbbz != '3'\n" +
                    "\t\t\tTHEN 1\n" +
                    "\t\tEND) AS xscs\n" +
                    "\t\t, count(CASE \n" +
                    "\t\t\tWHEN s.a_ajfl = '20'\n" +
                    "\t\t\t\tAND nvl(l.blsj, SYSDATE) - l.lrsj > 3\n" +
                    "\t\t\t\tAND l.lrsj > to_date('20150713', 'yyyymmdd')\n" +
                    "\t\t\t\tAND l.tbbz != '3'\n" +
                    "\t\t\tTHEN 1\n" +
                    "\t\tEND) AS xzcs\n" +
                    "\t\t, substr(sys_dwdm, 0, 8) || '0000' AS dw\n" +
                    "\tFROM case_m_detail l, case_s_process s\n" +
                    "\tWHERE l.a_ajbh = s.a_ajbh\n" +
                    "\t\tAND l.sys_dwdm IS NOT NULL\n" +
                    "\t\tAND l.scbz = '0'\n" +
                    "\t\tAND l.lrsj >= to_date('2018-01-17', 'yyyy-mm-dd')\n" +
                    "\t\tAND l.lrsj <= to_date('2018-01-24 23:59', 'yyyy-mm-dd hh24:mi')\n" +
                    "\t\tAND l.sys_dwdm LIKE '331126%'\n" +
                    "\tGROUP BY substr(sys_dwdm, 0, 8) || '0000'\n" +
                    ")\n" +
                    "ORDER BY dw", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(9, visitor.getColumns().size());
        assertEquals(11, visitor.getConditions().size());
        assertEquals(1, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());

    }
   
}
