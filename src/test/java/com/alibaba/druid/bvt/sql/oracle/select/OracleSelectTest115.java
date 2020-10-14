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

public class OracleSelectTest115 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "select * from ( select row_.*, rownum rownum_ from ( select HYKY as HYKY,WHCD as WHCD,ZASD as ZASD,TX as TX,XZFS as XZFS,SSGAJGMC as SSGAJGMC,XZDX as XZDX,HJDDZQH as HJDDZQH,XX as XX,MZ as MZ,TSTX as TSTX,SJJZDDZQH as SJJZDDZQH,HDDQ as HDDQ,ZY as ZY,XZCS as XZCS,BMCH as BMCH,CSD as CSD,ZAGJ as ZAGJ,XB as XB,XZWP as XZWP,CSRQ as CSRQ,SSGAJGJGDM as SSGAJGJGDM,AJLB as AJLB,TBTSBJ as TBTSBJ,LX as LX,XP as XP, '' as  from QQFW_ZYK.GZDX where (XM='忘轻春') ) row_ where rownum <= 5)";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        System.out.println(statementList.toString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT *\n" +
                    "FROM (\n" +
                    "\tSELECT row_.*, rownum AS rownum_\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT HYKY AS HYKY, WHCD AS WHCD, ZASD AS ZASD, TX AS TX, XZFS AS XZFS\n" +
                    "\t\t\t, SSGAJGMC AS SSGAJGMC, XZDX AS XZDX, HJDDZQH AS HJDDZQH, XX AS XX, MZ AS MZ\n" +
                    "\t\t\t, TSTX AS TSTX, SJJZDDZQH AS SJJZDDZQH, HDDQ AS HDDQ, ZY AS ZY, XZCS AS XZCS\n" +
                    "\t\t\t, BMCH AS BMCH, CSD AS CSD, ZAGJ AS ZAGJ, XB AS XB, XZWP AS XZWP\n" +
                    "\t\t\t, CSRQ AS CSRQ, SSGAJGJGDM AS SSGAJGJGDM, AJLB AS AJLB, TBTSBJ AS TBTSBJ, LX AS LX\n" +
                    "\t\t\t, XP AS XP, NULL\n" +
                    "\t\tFROM QQFW_ZYK.GZDX\n" +
                    "\t\tWHERE XM = '忘轻春'\n" +
                    "\t) row_\n" +
                    "\tWHERE rownum <= 5\n" +
                    ")", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(27, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

    }
   
}
