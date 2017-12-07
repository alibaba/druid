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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class OracleCreateFunctionTest_4 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "FUNCTION SPLITSTR(STR IN CLOB,\n" +
                "                                    I   IN NUMBER := 0,\n" +
                "                                    SEP IN VARCHAR2 := ',') RETURN VARCHAR2\n" +
                "  DETERMINISTIC\n" +
                "/**************************************\n" +
                "  * NAME:        SPLITSTR\n" +
                "  * AUTHOR:      SEAN ZHANG.\n" +
                "  * DATE:        2012-09-03.\n" +
                "  * FUNCTION:    ??????????????????????\n" +
                "  * PARAMETERS:  STR: ????????\n" +
                "                 I: ?????????I?0??STR????????I ??????????????\n" +
                "                 SEP: ????????????????????????????????STR????SEP?????\n" +
                "  * EXAMPLE:     SELECT SPLITSTR('ABC,DEF', 1) AS STR FROM DUAL;  ?? ABC\n" +
                "                 SELECT SPLITSTR('ABC,DEF', 3) AS STR FROM DUAL;  ?? ?\n" +
                "  **************************************/\n" +
                " IS\n" +
                "  T_COUNT NUMBER;\n" +
                "  T_STR   VARCHAR2(4000);\n" +
                "BEGIN\n" +
                "  IF I = 0 THEN\n" +
                "    T_STR := STR;\n" +
                "  ELSIF INSTR(STR, SEP) = 0 THEN\n" +
                "    T_STR := SEP;\n" +
                "  ELSE\n" +
                "    SELECT COUNT(*) INTO T_COUNT FROM TABLE(SPLIT(STR, SEP));\n" +
                "    IF I <= T_COUNT THEN\n" +
                "      SELECT STR\n" +
                "        INTO T_STR\n" +
                "        FROM (SELECT ROWNUM AS ITEM, COLUMN_VALUE AS STR\n" +
                "                FROM TABLE(SPLIT(STR, SEP)))\n" +
                "       WHERE ITEM = I;\n" +
                "    END IF;\n" +
                "  END IF;\n" +
                "  RETURN T_STR;\n" +
                "END;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("FUNCTION SPLITSTR (\n" +
                        "\tSTR IN CLOB, \n" +
                        "\tI IN NUMBER := 0, \n" +
                        "\tSEP IN VARCHAR2 := ','\n" +
                        ")\n" +
                        "RETURN VARCHAR2DETERMINISTIC \n" +
                        "IS\n" +
                        "T_COUNT NUMBER;\n" +
                        "\tT_STR VARCHAR2(4000);\n" +
                        "BEGIN\n" +
                        "\tIF I = 0 THEN\n" +
                        "\t\tT_STR := STR;\n" +
                        "\tELSE IF INSTR(STR, SEP) = 0 THEN\n" +
                        "\t\tT_STR := SEP;\n" +
                        "\tELSE\n" +
                        "\t\tSELECT COUNT(*)\n" +
                        "\t\tINTO T_COUNT\n" +
                        "\t\tFROM TABLE(SPLIT(STR, SEP));\n" +
                        "\t\tIF I <= T_COUNT THEN\n" +
                        "\t\t\tSELECT STR\n" +
                        "\t\t\tINTO T_STR\n" +
                        "\t\t\tFROM (\n" +
                        "\t\t\t\tSELECT ROWNUM AS ITEM, COLUMN_VALUE AS STR\n" +
                        "\t\t\t\tFROM TABLE(SPLIT(STR, SEP))\n" +
                        "\t\t\t)\n" +
                        "\t\t\tWHERE ITEM = I;\n" +
                        "\t\tEND IF;\n" +
                        "\tEND IF;\n" +
                        "\tRETURN T_STR;\n" +
                        "END;",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());

        assertEquals(3, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
