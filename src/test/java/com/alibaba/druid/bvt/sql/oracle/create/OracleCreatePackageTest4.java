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
 * WITHOUT WARRANTIES OR XONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreatePackageTest4 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
                "CREATE OR REPLACE PACKAGE         XON_CALC_RIGHT_PRICE_PKG IS\n" +
                        "  RIGHT_PRICE_TYPE varchar2(1) := '0'; -- 鏉冮檺浠风被鍨嬶細1銆佸浗闄呮潈闄愪环锛�2銆佸浗鍐呮潈闄愪环\n" +
                        "  /*鍒濆\uE750鍖栨潈闄愪环璁＄畻鐩稿叧鏁版嵁*/\n" +
                        "  PROCEDURE INIT_CALC_RIGHT_PRICE(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                  IN_CALC_ID       NUMBER,\n" +
                        "                                  OUT_STATUS       OUT VARCHAR2);\n" +
                        "  /*鏇存柊璁惧\uE62C鏉冮檺浠风郴鏁�*/\n" +
                        "  PROCEDURE UPDATE_DEV_RIGHT_PRICE_FACTOR(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                          IN_CALC_ID       NUMBER,\n" +
                        "                                          OUT_STATUS       OUT VARCHAR2);\n" +
                        "  /*鍖归厤鐗规畩鍝佺殑鏉冮檺浠风郴鏁�*/\n" +
                        "  PROCEDURE UPDATE_DEV_SPEC_FACTOR(IN_XON_HEADER_ID NUMBER, --閰嶇疆鍚堝悓ID\n" +
                        "                                   IN_CALC_ID       NUMBER, --璁＄畻ID\n" +
                        "                                   OUT_STATUS       OUT VARCHAR2);\n" +
                        "  /*鏇存柊鍏朵粬绫诲瀷鎴愭湰鍙婃潈闄愪环绯绘暟*/\n" +
                        "  PROCEDURE UPDATE_OTH_RIGHT_PRICE_INFO(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                        IN_CALC_ID       NUMBER,\n" +
                        "                                        OUT_STATUS       OUT VARCHAR2);\n" +
                        "  /*鏍规嵁鏉冮檺浠风郴鏁帮紝璁＄畻鏉冮檺浠�*/\n" +
                        "  PROCEDURE CALC_RIGHT_PRICE_BY_FACTOR(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                       IN_CALC_ID       NUMBER,\n" +
                        "                                       OUT_STATUS       OUT VARCHAR2);\n" +
                        "  /*\\*姹囨�绘眰寰楀晢鍔＄郴鏁�*\\\n" +
                        "  PROCEDURE UPDATE_BUSINESS_FACTOR(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                   IN_CALC_ID       NUMBER,\n" +
                        "                                   OUT_STATUS       OUT VARCHAR2);*/\n" +
                        "  /*鏍规嵁鏉冮檺浠疯\uE178绠楃殑鍩虹\uE505淇℃伅锛屾眹鎬昏\uE178绠楁潈闄愪环*/\n" +
                        "  PROCEDURE CALC_RIGHT_PRICE(IN_XON_HEADER_ID NUMBER, IN_CALC_ID NUMBER);\n" +
                        "  /*鏍规嵁鍥藉唴鏉冮檺浠疯\uE178绠楃殑鍩虹\uE505淇℃伅锛屾眹鎬昏\uE178绠楁潈闄愪环*/\n" +
                        "  PROCEDURE DOM_CALC_RIGHT_PRICE(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                 IN_CALC_ID       NUMBER);\n" +
                        "  /*鏉冮檺浠疯\uE178绠楃殑鎬荤殑璋冨害杩囩▼*/\n" +
                        "  PROCEDURE CALC_RIGHT_PRICE_MAIN(IN_XON_HEADER_ID NUMBER,\n" +
                        "                                  IN_CALC_ID       NUMBER);\n" +
                        "END XON_CALC_RIGHT_PRICE_PKG;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE PACKAGE XON_CALC_RIGHT_PRICE_PKG\n" +
                        "\tRIGHT_PRICE_TYPE varchar2(1) = '0';\n" +
                        "\tPROCEDURE INIT_CALC_RIGHT_PRICE (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER, \n" +
                        "\t\tOUT_STATUS OUT VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE UPDATE_DEV_RIGHT_PRICE_FACTOR (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER, \n" +
                        "\t\tOUT_STATUS OUT VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE UPDATE_DEV_SPEC_FACTOR (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER, \n" +
                        "\t\tOUT_STATUS OUT VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE UPDATE_OTH_RIGHT_PRICE_INFO (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER, \n" +
                        "\t\tOUT_STATUS OUT VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE CALC_RIGHT_PRICE_BY_FACTOR (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER, \n" +
                        "\t\tOUT_STATUS OUT VARCHAR2\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE CALC_RIGHT_PRICE (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE DOM_CALC_RIGHT_PRICE (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "\tPROCEDURE CALC_RIGHT_PRICE_MAIN (\n" +
                        "\t\tIN_XON_HEADER_ID NUMBER, \n" +
                        "\t\tIN_CALC_ID NUMBER\n" +
                        "\t)\n" +
                        "\t;\n" +
                        "END XON_CALC_RIGHT_PRICE_PKG;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

//        Assert.assertEquals(2, visitor.getTables().size());
//
//        Assert.assertEquals(5, visitor.getColumns().size());
//
//        Assert.assertTrue(visitor.containsColumn("employees", "employee_id"));
//        Assert.assertTrue(visitor.containsColumn("employees", "*"));
//        Assert.assertTrue(visitor.containsColumn("departments", "department_id"));
//        Assert.assertTrue(visitor.containsColumn("employees", "salary"));
//        Assert.assertTrue(visitor.containsColumn("employees", "commission_pct"));
    }
}
