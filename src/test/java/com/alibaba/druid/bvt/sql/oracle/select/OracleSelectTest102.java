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
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import org.junit.Assert;

import java.util.List;

public class OracleSelectTest102 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT COUNT(:\"SYS_B_000\") AS zhx_cnt, COUNT(BILL_ID) AS BILL_ID\n" +
                        "\t, SUM(BILL_NUM) AS BILL_NUM, SUM(BILL_GETNUM) AS BILL_GETNUM\n" +
                        "\t, SUM(REMAINNUM) AS REMAINNUM, SUM(QTY_OVERLOAD) AS QTY_OVERLOAD\n" +
                        "FROM (\n" +
                        "\tSELECT P0.BILL_STATE, P0.P0670_ID, P0.BILL_IDCODE || P0.BILL_ID AS BILL_ID\n" +
                        "\t\t, NVL(P.PLAN_ID, :\"SYS_B_001\") AS PLAN_ID\n" +
                        "\t\t, NVL(P.PLAN_NAME, :\"SYS_B_002\") AS PLAN_NAME\n" +
                        "\t\t, :\"SYS_B_003\" || B.DEPARTMENT_USER_ID || :\"SYS_B_004\" || B.DEPARTMENT_NAME AS SET_DEPARTMENT_NAME\n" +
                        "\t\t, H.DICT_NAME, :\"SYS_B_005\" || C.DEPARTMENT_USER_ID || :\"SYS_B_006\" || C.DEPARTMENT_NAME AS GET_DEPARTMENT_NAME\n" +
                        "\t\t, C.DEPARTMENT_ID AS STYLE_DEPARTMENT_ID, P0.BILL_NUM, P0.BILL_XSUM, P0.BILL_SSUM, P0.BILL_REMARK\n" +
                        "\t\t, TO_CHAR(P0.BILL_SETDATE, :\"SYS_B_007\") AS BILL_SETDATE, P0.BILL_CREATE_NAME\n" +
                        "\t\t, TO_CHAR(P0.BILL_CREATE_DATE, :\"SYS_B_008\") AS BILL_CREATE_DATE\n" +
                        "\t\t, F1.BILL_IDCODE || F1.BILL_ID AS BILL_REFID\n" +
                        "\t\t, TO_CHAR(P0.DATE_IN, :\"SYS_B_009\") AS BILL_REMINDDATE, P0.CIRCLE AS CYCDAYS\n" +
                        "\t\t, DAYS AS SUBMITDAYS\n" +
                        "\t\t, :\"SYS_B_010\" || P0.BILL_TYPE || :\"SYS_B_011\" || NVL(F.DICT_NAME, :\"SYS_B_012\") AS BILL_SELLTYPE\n" +
                        "\t\t, P0.CHECKER AS BILL_SURENAME, TO_CHAR(P0.CHECK_DATE, :\"SYS_B_013\") AS BILL_SUREDATE, P0.TERMINATOR AS BILL_OVERNAME\n" +
                        "\t\t, TO_CHAR(P0.TERMINATE_DATE, :\"SYS_B_014\") AS BILL_OVERDATE\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN P0.EXPIRE_STATUS = :\"SYS_B_015\" THEN :\"SYS_B_016\"\n" +
                        "\t\t\tELSE :\"SYS_B_017\"\n" +
                        "\t\tEND AS EXPIRE_STATUS, P0.EXPIRE_STATUS AS EXPIRE_STATUS_V, P0.STORES\n" +
                        "\t\t, TO_CHAR(P0.ESTIMATED_DELIVERY_DATE, :\"SYS_B_018\") AS ESTIMATED_DELIVERY_DATE, P0.BILL_GETNUM\n" +
                        "\t\t, SUM(CASE \n" +
                        "\t\t\tWHEN A1.BILLSUB_NUMS - NVL(A1.BILLSUB_GETNUMS, :\"SYS_B_019\") < :\"SYS_B_020\" THEN :\"SYS_B_021\"\n" +
                        "\t\t\tELSE A1.BILLSUB_NUMS - NVL(A1.BILLSUB_GETNUMS, :\"SYS_B_022\")\n" +
                        "\t\tEND) AS REMAINNUM\n" +
                        "\t\t, SUM(NVL(A1.QTY_OVERLOAD, :\"SYS_B_023\")) AS QTY_OVERLOAD\n" +
                        "\t\t, P0.STYLE_ID, P0.CHECKED, P0.DELED, P0.DEL, P0.DEL_DATE\n" +
                        "\t\t, P0.CHARGED, P0.TERMINATED, P0.BILL_NUM_GETSURE, P0.BILL_REFID AS BILL_REFID_0\n" +
                        "\t\t, :\"SYS_B_024\" || CODE.CODE || :\"SYS_B_025\" || CODE.NAME AS IN_TYPE_NAME\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN P0.CHECKED = :\"SYS_B_026\"\n" +
                        "\t\t\tAND P0.TERMINATED = :\"SYS_B_027\"\n" +
                        "\t\t\tAND P0.DAYS > :\"SYS_B_028\"\n" +
                        "\t\t\tAND P0.BILL_TYPE <> :\"SYS_B_029\"\n" +
                        "\t\t\tAND SUM(CASE \n" +
                        "\t\t\t\tWHEN A1.BILLSUB_NUMS - NVL(A1.BILLSUB_GETNUMS, :\"SYS_B_030\") < :\"SYS_B_031\" THEN :\"SYS_B_032\"\n" +
                        "\t\t\t\tELSE A1.BILLSUB_NUMS - NVL(A1.BILLSUB_GETNUMS, :\"SYS_B_033\")\n" +
                        "\t\t\tEND) > :\"SYS_B_034\" THEN \n" +
                        "\t\t\t\tCASE \n" +
                        "\t\t\t\t\tWHEN P0.EXPIRE_STATUS = :\"SYS_B_035\" THEN :\"SYS_B_036\"\n" +
                        "\t\t\t\t\tWHEN P0.CIRCLE <> :\"SYS_B_037\"\n" +
                        "\t\t\t\t\tAND (P0.DATE_IN - TRUNC(SYSDATE)) / P0.CIRCLE <= :\"SYS_B_038\" THEN :\"SYS_B_039\"\n" +
                        "\t\t\t\t\tWHEN P0.CIRCLE <> :\"SYS_B_040\"\n" +
                        "\t\t\t\t\tAND (P0.DATE_IN - TRUNC(SYSDATE)) / P0.CIRCLE > :\"SYS_B_041\" THEN :\"SYS_B_042\"\n" +
                        "\t\t\t\t\tELSE :\"SYS_B_043\"\n" +
                        "\t\t\t\tEND\n" +
                        "\t\t\tELSE :\"SYS_B_044\"\n" +
                        "\t\tEND AS COLOR\n" +
                        "\t\t, CASE \n" +
                        "\t\t\tWHEN NVL(EX.ATT_CODE, :\"SYS_B_045\") = :\"SYS_B_046\" THEN :\"SYS_B_047\"\n" +
                        "\t\t\tELSE :\"SYS_B_048\"\n" +
                        "\t\tEND AS TERMINATED_CENTER\n" +
                        "\tFROM V0670 P0\n" +
                        "\tLEFT JOIN P0671 A1 ON P0.P0670_ID = A1.P0670_ID \n" +
                        "\tLEFT JOIN D0050 A2 ON A1.CLOTHING_ID = A2.CLOTHING_ID \n" +
                        "\tINNER JOIN D0060 B ON P0.SET_DEPARTMENT_ID = B.DEPARTMENT_ID \n" +
                        "\tLEFT JOIN V0010 H ON B.DEPARTMENT_PROPERTY = H.DICT_CODE\n" +
                        "\tAND H.PROPERTY_FIELD = :\"SYS_B_049\"\n" +
                        "\tAND H.PROPERTY_TABLE = :\"SYS_B_050\"\n" +
                        "\tAND H.DICT_GROUP_ID = :\"SYS_B_051\" \n" +
                        "\tINNER JOIN V0060 C ON P0.GET_DEPARTMENT_ID = C.DEPARTMENT_ID\n" +
                        "\tAND C.LANGUAGE_ID = :\"SYS_B_052\" \n" +
                        "\tINNER JOIN (\n" +
                        "\t\tSELECT /*+ MATERIALIZE */ D.DEPARTMENT_ID AS DEPARTMENT_ID\n" +
                        "\t\tFROM S0070 A\n" +
                        "\t\tINNER JOIN S0061 B ON B.DATATYPE_ID = A.DATATYPE_ID\n" +
                        "\t\tAND B.USER_ID = :\"SYS_B_053\"\n" +
                        "\t\tAND B.POWER_OPERATE = :\"SYS_B_054\" \n" +
                        "\t\tINNER JOIN S0071 C ON C.DATATYPE_ID = A.DATATYPE_ID \n" +
                        "\t\tINNER JOIN D0065 D ON C.DATA_ID = D.DEPARTMENT_ID \n" +
                        "\t\t\tINNER JOIN (\n" +
                        "\t\t\t\tSELECT TO_NUMBER(C.DATA_ID) AS BRAND_ID\n" +
                        "\t\t\t\tFROM S0070 A\n" +
                        "\t\t\t\tINNER JOIN S0061 B ON B.DATATYPE_ID = A.DATATYPE_ID\n" +
                        "\t\t\t\tAND B.USER_ID = :\"SYS_B_055\"\n" +
                        "\t\t\t\tAND B.POWER_OPERATE = :\"SYS_B_056\" \n" +
                        "\t\t\t\t\tINNER JOIN S0071 C ON C.DATATYPE_ID = A.DATATYPE_ID \n" +
                        "\t\t\t\tWHERE A.DATATYPE_PARENT_ID = :\"SYS_B_057\"\n" +
                        "\t\t\t\t\tAND A.DATATYPE_STATE = :\"SYS_B_058\"\n" +
                        "\t\t\t) E ON D.DICT_GROUP_ID = E.BRAND_ID \n" +
                        "\t\tWHERE A.DATATYPE_PARENT_ID = :\"SYS_B_059\"\n" +
                        "\t\t\tAND A.DATATYPE_STATE = :\"SYS_B_060\"\n" +
                        "\t\tGROUP BY D.DEPARTMENT_ID\n" +
                        "\t) D ON P0.GET_DEPARTMENT_ID = D.DEPARTMENT_ID \n" +
                        "\tINNER JOIN (\n" +
                        "\t\tSELECT /*+ MATERIALIZE */ TO_NUMBER(C.DATA_ID) AS DEPARTMENT_ID\n" +
                        "\t\tFROM S0070 A\n" +
                        "\t\tINNER JOIN S0061 B ON B.DATATYPE_ID = A.DATATYPE_ID\n" +
                        "\t\tAND B.USER_ID = :\"SYS_B_061\"\n" +
                        "\t\tAND B.POWER_OPERATE = :\"SYS_B_062\" \n" +
                        "\t\t\tINNER JOIN S0071 C ON C.DATATYPE_ID = A.DATATYPE_ID \n" +
                        "\t\tWHERE A.DATATYPE_PARENT_ID = :\"SYS_B_063\"\n" +
                        "\t\t\tAND A.DATATYPE_STATE = :\"SYS_B_064\"\n" +
                        "\t\tGROUP BY C.DATA_ID\n" +
                        "\t) E ON P0.SET_DEPARTMENT_ID = E.DEPARTMENT_ID \n" +
                        "\tINNER JOIN V0010 F ON F.PROPERTY_FIELD = UPPER(:\"SYS_B_065\")\n" +
                        "\tAND F.PROPERTY_TABLE = UPPER(:\"SYS_B_066\")\n" +
                        "\tAND F.LANGUAGE_ID = :\"SYS_B_067\"\n" +
                        "\tAND F.DICT_GROUP_ID = :\"SYS_B_068\"\n" +
                        "\tAND F.DICT_CODE = P0.BILL_TYPE \n" +
                        "\tLEFT JOIN P0670 F1 ON F1.P0670_ID = P0.BILL_CZID \n" +
                        "\tINNER JOIN TABLE(PKG_DRP_PUBLIC.SPLIT(:\"SYS_B_069\", :\"SYS_B_070\")) REG ON REG.COL1 = C.REGION_ID \n" +
                        "\tLEFT JOIN BAS_CODE CODE ON P0.IN_TYPE = CODE.CODE\n" +
                        "\tAND UPPER(CODE.CAT) = :\"SYS_B_071\" \n" +
                        "\tLEFT JOIN BILL_EXPAND EX ON P0.P0670_ID = EX.P0670_ID\n" +
                        "\tAND EX.BILL_TYPE = :\"SYS_B_072\"\n" +
                        "\tAND EX.ATT_ID = :\"SYS_B_073\" \n" +
                        "\t\tLEFT JOIN P0830 P ON P0.BILL_CZID = P.PLAN_ID \n" +
                        "\tWHERE P0.SYSTEM_TYPE = :\"SYS_B_074\"\n" +
                        "\t\tAND ((P0.BILL_IDCODE || P0.BILL_ID || P0.BILL_REFID || P0.BILL_CREATE_NAME || P0.BILL_NUM_SURENAME || P0.BILL_OVERNAME) LIKE (:\"SYS_B_075\" || :\"SYS_B_076\" || :\"SYS_B_077\")\n" +
                        "\t\t\tOR P0.BILL_CREATE_NAME LIKE (:\"SYS_B_078\" || :\"SYS_B_079\" || :\"SYS_B_080\")\n" +
                        "\t\t\tOR P0.CHECKER LIKE (:\"SYS_B_081\" || :\"SYS_B_082\" || :\"SYS_B_083\")\n" +
                        "\t\t\tOR A2.STYLE_ID LIKE (:\"SYS_B_084\" || :\"SYS_B_085\" || :\"SYS_B_086\")\n" +
                        "\t\t\tOR P0.BILL_CREATE_NAME IN (\n" +
                        "\t\t\t\tSELECT USER_NAME\n" +
                        "\t\t\t\tFROM WF_USER\n" +
                        "\t\t\t\tWHERE UPPER(USER_CODE) = :\"SYS_B_087\"\n" +
                        "\t\t\t))\n" +
                        "\t\tAND ((INSTR(:\"SYS_B_088\", :\"SYS_B_089\", :\"SYS_B_090\", :\"SYS_B_091\") = :\"SYS_B_092\"\n" +
                        "\t\t\t\tAND INSTR(:\"SYS_B_093\", :\"SYS_B_094\", :\"SYS_B_095\", :\"SYS_B_096\") = :\"SYS_B_097\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_098\", :\"SYS_B_099\", :\"SYS_B_100\", :\"SYS_B_101\") > :\"SYS_B_102\"\n" +
                        "\t\t\t\tAND NVL(P0.CHECKED, :\"SYS_B_103\") = :\"SYS_B_104\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_105\") = :\"SYS_B_106\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_107\", :\"SYS_B_108\", :\"SYS_B_109\", :\"SYS_B_110\") > :\"SYS_B_111\"\n" +
                        "\t\t\t\tAND NVL(P0.CHECKED, :\"SYS_B_112\") = :\"SYS_B_113\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_114\") = :\"SYS_B_115\"))\n" +
                        "\t\tAND ((INSTR(:\"SYS_B_116\", :\"SYS_B_117\", :\"SYS_B_118\", :\"SYS_B_119\") = :\"SYS_B_120\"\n" +
                        "\t\t\t\tAND INSTR(:\"SYS_B_121\", :\"SYS_B_122\", :\"SYS_B_123\", :\"SYS_B_124\") = :\"SYS_B_125\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_126\", :\"SYS_B_127\", :\"SYS_B_128\", :\"SYS_B_129\") > :\"SYS_B_130\"\n" +
                        "\t\t\t\tAND NVL(P0.TERMINATED, :\"SYS_B_131\") = :\"SYS_B_132\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_133\") = :\"SYS_B_134\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_135\", :\"SYS_B_136\", :\"SYS_B_137\", :\"SYS_B_138\") > :\"SYS_B_139\"\n" +
                        "\t\t\t\tAND NVL(P0.TERMINATED, :\"SYS_B_140\") = :\"SYS_B_141\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_142\") = :\"SYS_B_143\"))\n" +
                        "\t\tAND ((INSTR(:\"SYS_B_144\", :\"SYS_B_145\", :\"SYS_B_146\", :\"SYS_B_147\") = :\"SYS_B_148\"\n" +
                        "\t\t\t\tAND INSTR(:\"SYS_B_149\", :\"SYS_B_150\", :\"SYS_B_151\", :\"SYS_B_152\") = :\"SYS_B_153\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_154\", :\"SYS_B_155\", :\"SYS_B_156\", :\"SYS_B_157\") > :\"SYS_B_158\"\n" +
                        "\t\t\t\tAND NVL(P0.EXPIRE_STATUS, :\"SYS_B_159\") = :\"SYS_B_160\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_161\") = :\"SYS_B_162\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_163\", :\"SYS_B_164\", :\"SYS_B_165\", :\"SYS_B_166\") > :\"SYS_B_167\"\n" +
                        "\t\t\t\tAND NVL(P0.EXPIRE_STATUS, :\"SYS_B_168\") = :\"SYS_B_169\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_170\") = :\"SYS_B_171\"))\n" +
                        "\t\tAND ((INSTR(:\"SYS_B_172\", :\"SYS_B_173\", :\"SYS_B_174\", :\"SYS_B_175\") = :\"SYS_B_176\"\n" +
                        "\t\t\t\tAND INSTR(:\"SYS_B_177\", :\"SYS_B_178\", :\"SYS_B_179\", :\"SYS_B_180\") = :\"SYS_B_181\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_182\", :\"SYS_B_183\", :\"SYS_B_184\", :\"SYS_B_185\") > :\"SYS_B_186\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_187\") = :\"SYS_B_188\")\n" +
                        "\t\t\tOR (INSTR(:\"SYS_B_189\", :\"SYS_B_190\", :\"SYS_B_191\", :\"SYS_B_192\") > :\"SYS_B_193\"\n" +
                        "\t\t\t\tAND NVL(P0.DELED, :\"SYS_B_194\") = :\"SYS_B_195\"))\n" +
                        "\t\tAND P0.BILL_SETDATE BETWEEN to_date(:\"SYS_B_196\", :\"SYS_B_197\") AND to_date(:\"SYS_B_198\", :\"SYS_B_199\") + :\"SYS_B_200\"\n" +
                        "\tGROUP BY P0.BILL_STATE, P0.P0670_ID, P0.BILL_IDCODE, P0.BILL_ID, NVL(P.PLAN_ID, :\"SYS_B_201\"), NVL(P.PLAN_NAME, :\"SYS_B_202\"), B.DEPARTMENT_USER_ID, B.DEPARTMENT_NAME, H.DICT_NAME, C.DEPARTMENT_USER_ID, C.DEPARTMENT_ID, C.DEPARTMENT_NAME, P0.BILL_NUM, P0.BILL_XSUM, P0.BILL_SSUM, P0.BILL_REMARK, P0.BILL_SETDATE, P0.BILL_CREATE_NAME, P0.BILL_CREATE_DATE, F1.BILL_IDCODE, F1.BILL_ID, P0.DATE_IN, P0.CIRCLE, DAYS, P0.BILL_TYPE, NVL(F.DICT_NAME, :\"SYS_B_203\"), P0.CHECKER, P0.CHECK_DATE, P0.TERMINATOR, P0.TERMINATE_DATE, P0.EXPIRE_STATUS, P0.EXPIRE_STATUS, P0.STORES, P0.ESTIMATED_DELIVERY_DATE, P0.BILL_GETNUM, P0.STYLE_ID, P0.CHECKED, P0.DELED, P0.DEL, P0.DEL_DATE, P0.CHARGED, P0.TERMINATED, P0.BILL_NUM_GETSURE, P0.BILL_REFID, CODE.CODE, CODE.NAME, NVL(EX.ATT_CODE, :\"SYS_B_204\")\n" +
                        "\tORDER BY ESTIMATED_DELIVERY_DATE ASC NULLS FIRST, P0.P0670_ID DESC";

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

//        SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) stmt.getSelect().getQueryBlock().getSelectList().get(0).getExpr();
//        SQLMethodInvokeExpr param0 = (SQLMethodInvokeExpr) expr.getParameters().get(0);
//        assertTrue(param0.getParameters().get(0)
//                instanceof SQLAggregateExpr);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT e.area_name AS 区域, e.department_user_id AS 店铺代码, e.department_name AS 店铺名称, a.card_id AS 会员卡号, a.vip_name AS 姓名\n" +
                    "\t, CASE \n" +
                    "\t\tWHEN a.vip_sex = '1' THEN '男'\n" +
                    "\t\tWHEN a.vip_sex = '2' THEN '女'\n" +
                    "\t\tELSE '保密'\n" +
                    "\tEND AS 性别\n" +
                    "\t, a.vip_birthday_year || '-' || a.vip_birthday_month || '-' || a.vip_birthday_day AS 出生日期\n" +
                    "\t, a.vip_create_date AS 会员注册日期, a.vip_mobile AS 手机号, a.vip_job AS 职业, a.wechat AS 微信号, a.vip_email AS 邮箱\n" +
                    "\t, d.viptype_name AS 会员等级\n" +
                    "FROM D0210 a\n" +
                    "JOIN D0180 b ON a.vip_id = b.vip_id \n" +
                    "JOIN D0160 c ON c.viptype_id = b.viptype_id \n" +
                    "JOIN D0169 d ON d.viptype_id = c.viptype_id\n" +
                    "AND d.language_id = 'zh-cn' \n" +
                    "\tJOIN area_store_hn e ON a.department_id = e.department_id \n" +
                    "WHERE a.vip_create_date BETWEEN TRUNC(SYSDATE) - 4 - 10 / 24 AND TRUNC(SYSDATE) - 10 / 24 -- --注册日期\n" +
                    "\tAND (a.vip_state = '0'\n" +
                    "\t\tOR a.vip_state = '1')\n" +
                    "\tAND e.department_user_id IN (\n" +
                    "\t\t'44011', \n" +
                    "\t\t'44012', \n" +
                    "\t\t'44021', \n" +
                    "\t\t'44026', \n" +
                    "\t\t'44027', \n" +
                    "\t\t'44028', \n" +
                    "\t\t'44056', \n" +
                    "\t\t'44062', \n" +
                    "\t\t'44081', \n" +
                    "\t\t'44083', \n" +
                    "\t\t'44086', \n" +
                    "\t\t'44095', \n" +
                    "\t\t'44097', \n" +
                    "\t\t'44118', \n" +
                    "\t\t'44122', \n" +
                    "\t\t'44126', \n" +
                    "\t\t'44161', \n" +
                    "\t\t'44182', \n" +
                    "\t\t'44204', \n" +
                    "\t\t'44209', \n" +
                    "\t\t'44211', \n" +
                    "\t\t'44247', \n" +
                    "\t\t'44252', \n" +
                    "\t\t'44254', \n" +
                    "\t\t'44282', \n" +
                    "\t\t'44284', \n" +
                    "\t\t'44288', \n" +
                    "\t\t'44298', \n" +
                    "\t\t'44058', \n" +
                    "\t\t'44068', \n" +
                    "\t\t'44084', \n" +
                    "\t\t'44162'\n" +
                    "\t)\n" +
                    "ORDER BY a.vip_create_date", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(5, visitor.getTables().size());
        assertEquals(24, visitor.getColumns().size());
        assertEquals(11, visitor.getConditions().size());
        assertEquals(4, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());
    }

   
}
