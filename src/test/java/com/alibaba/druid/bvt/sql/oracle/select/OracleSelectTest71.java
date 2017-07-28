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
                "/* SQL Analyze(130,1) *\n" +
                        "SELECT *\n" +
                        "FROM (\n" +
                        "\tSELECT pohjxx_czr0_.RYNBID AS RYNBID, pohjxx_czr0_.RYID AS RYID, pohjxx_czr0_.HHNBID AS HHNBID, pohjxx_czr0_.HHID AS HHID, pohjxx_czr0_.MLPNBID AS MLPNBID\n" +
                        "\t\t, pohjxx_czr0_.MLPID AS MLPID, pohjxx_czr0_.ZPID AS ZPID, pohjxx_czr0_.NBSFZID AS NBSFZID, pohjxx_czr0_.GMSFHM AS GMSFHM, pohjxx_czr0_.XM AS XM\n" +
                        "\t\t, pohjxx_czr0_.X AS X, pohjxx_czr0_.M AS M, pohjxx_czr0_.CYM AS CYM, pohjxx_czr0_.XMPY AS XMPY, pohjxx_czr0_.CYMPY AS CYMPY\n" +
                        "\t\t, pohjxx_czr0_.XB AS XB, pohjxx_czr0_.MZ AS MZ, pohjxx_czr0_.CSRQ AS CSRQ, pohjxx_czr0_.CSSJ AS CSSJ, pohjxx_czr0_.CSDGJDQ AS CSDGJDQ\n" +
                        "\t\t, pohjxx_czr0_.CSDSSXQ AS CSDSSXQ, pohjxx_czr0_.CSDXZ AS CSDXZ, pohjxx_czr0_.CSZMBH AS CSZMBH, pohjxx_czr0_.CSZQFRQ AS CSZQFRQ, pohjxx_czr0_.JGGJDQ AS JGGJDQ\n" +
                        "\t\t, pohjxx_czr0_.JGSSXQ AS JGSSXQ, pohjxx_czr0_.JGXZ AS JGXZ, pohjxx_czr0_.JHRYGMSFHM AS JHRYGMSFHM, pohjxx_czr0_.JHRYXM AS JHRYXM, pohjxx_czr0_.JHRYCYZJDM AS JHRYCYZJDM\n" +
                        "\t\t, pohjxx_czr0_.JHRYZJHM AS JHRYZJHM, pohjxx_czr0_.JHRYWWX AS JHRYWWX, pohjxx_czr0_.JHRYWWM AS JHRYWWM, pohjxx_czr0_.JHRYJHGX AS JHRYJHGX, pohjxx_czr0_.JHRYLXDH AS JHRYLXDH\n" +
                        "\t\t, pohjxx_czr0_.JHREGMSFHM AS JHREGMSFHM, pohjxx_czr0_.JHREXM AS JHREXM, pohjxx_czr0_.JHRECYZJDM AS JHRECYZJDM, pohjxx_czr0_.JHREZJHM AS JHREZJHM, pohjxx_czr0_.JHREWWX AS JHREWWX\n" +
                        "\t\t, pohjxx_czr0_.JHREWWM AS JHREWWM, pohjxx_czr0_.JHREJHGX AS JHREJHGX, pohjxx_czr0_.JHRELXDH AS JHRELXDH, pohjxx_czr0_.FQGMSFHM AS FQGMSFHM, pohjxx_czr0_.FQXM AS FQXM\n" +
                        "\t\t, pohjxx_czr0_.FQCYZJDM AS FQCYZJDM, pohjxx_czr0_.FQZJHM AS FQZJHM, pohjxx_czr0_.FQWWX AS FQWWX, pohjxx_czr0_.FQWWM AS FQWWM, pohjxx_czr0_.MQGMSFHM AS MQGMSFHM\n" +
                        "\t\t, pohjxx_czr0_.MQXM AS MQXM, pohjxx_czr0_.MQCYZJDM AS MQCYZJDM, pohjxx_czr0_.MQZJHM AS MQZJHM, pohjxx_czr0_.MQWWX AS MQWWX, pohjxx_czr0_.MQWWM AS MQWWM\n" +
                        "\t\t, pohjxx_czr0_.POGMSFHM AS POGMSFHM, pohjxx_czr0_.POXM AS POXM, pohjxx_czr0_.POCYZJDM AS POCYZJDM, pohjxx_czr0_.POZJHM AS POZJHM, pohjxx_czr0_.POWWX AS POWWX\n" +
                        "\t\t, pohjxx_czr0_.POWWM AS POWWM, pohjxx_czr0_.ZJXY AS ZJXY, pohjxx_czr0_.WHCD AS WHCD, pohjxx_czr0_.HYZK AS HYZK, pohjxx_czr0_.BYZK AS BYZK\n" +
                        "\t\t, pohjxx_czr0_.SG AS SG, pohjxx_czr0_.XX AS XX, pohjxx_czr0_.DHHM AS DHHM, pohjxx_czr0_.DHHM2 AS DHHM2, pohjxx_czr0_.CYZKDWBM AS CYZKDWBM\n" +
                        "\t\t, pohjxx_czr0_.CYZKDWMC AS CYZKDWMC, pohjxx_czr0_.ZY AS ZY, pohjxx_czr0_.ZYLB AS ZYLB, pohjxx_czr0_.FWCS AS FWCS, pohjxx_czr0_.XXJB AS XXJB\n" +
                        "\t\t, pohjxx_czr0_.HSQL AS HSQL, pohjxx_czr0_.HYQL AS HYQL, pohjxx_czr0_.HQYLDYY AS HQYLDYY, pohjxx_czr0_.HGJDQQL AS HGJDQQL, pohjxx_czr0_.HSSXQQL AS HSSXQQL\n" +
                        "\t\t, pohjxx_czr0_.HXZQL AS HXZQL, pohjxx_czr0_.HSLBZ AS HSLBZ, pohjxx_czr0_.HYLBZ AS HYLBZ, pohjxx_czr0_.HQYLDYYLBZ AS HQYLDYYLBZ, pohjxx_czr0_.HGJDQLBZ AS HGJDQLBZ\n" +
                        "\t\t, pohjxx_czr0_.HSSSQLBZ AS HSSSQLBZ, pohjxx_czr0_.HXZLBZ AS HXZLBZ, pohjxx_czr0_.SWRQ AS SWRQ, pohjxx_czr0_.SWZXLB AS SWZXLB, pohjxx_czr0_.SWYY AS SWYY\n" +
                        "\t\t, pohjxx_czr0_.SWZXRQ AS SWZXRQ, pohjxx_czr0_.QCRQ AS QCRQ, pohjxx_czr0_.QCZXLB AS QCZXLB, pohjxx_czr0_.QCQYLDYY AS QCQYLDYY, pohjxx_czr0_.QWDGJDQ AS QWDGJDQ\n" +
                        "\t\t, pohjxx_czr0_.QWDSSXQ AS QWDSSXQ, pohjxx_czr0_.QWDXZ AS QWDXZ, pohjxx_czr0_.BDFW AS BDFW, pohjxx_czr0_.RYLB AS RYLB, pohjxx_czr0_.RYZT AS RYZT\n" +
                        "\t\t, pohjxx_czr0_.RYSDZT AS RYSDZT, pohjxx_czr0_.LXDBID AS LXDBID, pohjxx_czr0_.JLBZ AS JLBZ, pohjxx_czr0_.YWNR AS YWNR, pohjxx_czr0_.CJHJYWID AS CJHJYWID\n" +
                        "\t\t, pohjxx_czr0_.CCHJYWID AS CCHJYWID, pohjxx_czr0_.QYSJ AS QYSJ, pohjxx_czr0_.JSSJ AS JSSJ, pohjxx_czr0_.CXBZ AS CXBZ, pohjxx_czr0_.XXQYSJ AS XXQYSJ\n" +
                        "\t\t, pohjxx_czr0_.ZXSJ AS ZXSJ, pohjxx_czr0_.GXSJ AS GXSJ, pohjxx_czr0_.SJGSDWDM AS SJGSDWDM, pohjxx_czr0_.SJGSDWMC AS SJGSDWMC, pohjxx_czr0_.HYLB AS HYLB\n" +
                        "\t\t, pohjxx_czr0_.BZ AS BZ, pohjxx_czr0_.SSXQ AS SSXQ, pohjxx_czr0_.JLX AS JLX, pohjxx_czr0_.MLPH AS MLPH, pohjxx_czr0_.MLXZ AS MLXZ\n" +
                        "\t\t, pohjxx_czr0_.PCS AS PCS, pohjxx_czr0_.ZRQ AS ZRQ, pohjxx_czr0_.XZJD AS XZJD, pohjxx_czr0_.JCWH AS JCWH, pohjxx_czr0_.PXH AS PXH\n" +
                        "\t\t, pohjxx_czr0_.HB AS HB, pohjxx_czr0_.HH AS HH, pohjxx_czr0_.HLX AS HLX, pohjxx_czr0_.YHZGX AS YHZGX, pohjxx_czr0_.QTSSXQ AS QTSSXQ\n" +
                        "\t\t, pohjxx_czr0_.QTZZ AS QTZZ, pohjxx_czr0_.XZDCJZT AS XZDCJZT, pohjxx_czr0_.HJDDZBM AS HJDDZBM, pohjxx_czr0_.HJDSSXQ AS HJDSSXQ, pohjxx_czr0_.HJDXXDZ AS HJDXXDZ\n" +
                        "\t\t, pohjxx_czr0_.HJDRHYZBS AS HJDRHYZBS, pohjxx_czr0_.JZDDZBM AS JZDDZBM, pohjxx_czr0_.JZDSSXQ AS JZDSSXQ, pohjxx_czr0_.JZDXXDZ AS JZDXXDZ, pohjxx_czr0_.QFJG AS QFJG\n" +
                        "\t\t, pohjxx_czr0_.YXQXQSRQ AS YXQXQSRQ, pohjxx_czr0_.YXQXJZRQ AS YXQXJZRQ, pohjxx_czr0_.ZJDZ AS ZJDZ, pohjxx_czr0_.ZJLB AS ZJLB, pohjxx_czr0_.ZWYZW AS ZWYZW\n" +
                        "\t\t, pohjxx_czr0_.ZWYZCJG AS ZWYZCJG, pohjxx_czr0_.ZWEZW AS ZWEZW, pohjxx_czr0_.ZWEZCJG AS ZWEZCJG, pohjxx_czr0_.ZWCJJGDM AS ZWCJJGDM, pohjxx_czr0_.SZYCZKDM AS SZYCZKDM\n" +
                        "\t\t, pohjxx_czr0_.DJZT AS DJZT, pohjxx_czr0_.DJSJ AS DJSJ, pohjxx_czr0_.DJYY AS DJYY, pohjxx_czr0_.JCDJSJ AS JCDJSJ, pohjxx_czr0_.HKDJID AS HKDJID\n" +
                        "\t\t, pohjxx_czr0_.TJYXZQH AS TJYXZQH, pohjxx_czr0_.CXSX AS CXSX\n" +
                        "\tFROM TB_001 pohjxx_czr0_\n" +
                        "\tWHERE 1 = 1\n" +
                        "\t\tAND (pohjxx_czr0_.CSRQ = ?\n" +
                        "\t\t\tAND pohjxx_czr0_.JLBZ = ?\n" +
                        "\t\t\tAND ryzt = ?\n" +
                        "\t\t\tAND pohjxx_czr0_.CXBZ = ?\n" +
                        "\t\t\tAND 1 = 1)\n" +
                        ")\n" +
                        "WHERE rownum <= :1"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
            assertEquals(1, statementList.size());
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

        assertEquals(3, visitor.getTables().size());

        assertEquals(8, visitor.getColumns().size());

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT CAST(a.nsrdzdah AS char)\n" +
                    "FROM DJ_NSRXX_DZB a\n" +
                    "INNER JOIN DJ_NSRXX b ON a.nsrdzdah = b.nsrdzdah \n" +
                    "\tINNER JOIN DM_SWJG c ON b.nsr_swjg_dm = c.swjg_dm \n" +
                    "WHERE (a.nsrsbh_old = ?\n" +
                    "\t\tOR a.nsrsbh_new = ?)\n" +
                    "\tAND b.nsrzt_dm <= ?\n" +
                    "\tAND c.JBDM LIKE ?\n" +
                    "\tAND rownum = 1", text);
        }
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("acduser.vw_acd_info", "xzqh")));

        // Assert.assertTrue(visitor.getOrderByColumns().contains(new TableStat.Column("employees", "last_name")));
    }
}
