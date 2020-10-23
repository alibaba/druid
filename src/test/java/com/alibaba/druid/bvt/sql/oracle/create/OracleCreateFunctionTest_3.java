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

public class OracleCreateFunctionTest_3 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "FUNCTION        FN_LGN_CHK(USERINFO IN VARCHAR2)\n" +
                "  RETURN VARCHAR2 IS\n" +
                "  -- Author  : yuguoq\n" +
                "  -- Created : 2013-10-14\n" +
                "  -- Purpose : ??????\n" +
                "  RESULT VARCHAR2(20);\n" +
                "  TYPE TYPE_ARRAY IS VARRAY(3) OF VARCHAR2(50);\n" +
                "  VAR_ARRAY TYPE_ARRAY := TYPE_ARRAY();\n" +
                "  CURSOR VALUECURSOR IS\n" +
                "    SELECT *\n" +
                "      FROM TABLE(CAST(FN_SPLIT(USERINFO, ',') AS TY_STR_SPLIT))\n" +
                "     WHERE ROWNUM < 4;\n" +
                "  CURPOLICYINFO VALUECURSOR%ROWTYPE; ---??????\n" +
                "  I             INTEGER := 1;\n" +
                "BEGIN\n" +
                "  OPEN VALUECURSOR; ---open cursor\n" +
                "  LOOP\n" +
                "    --deal with extraction data from DB\n" +
                "    FETCH VALUECURSOR\n" +
                "      INTO CURPOLICYINFO;\n" +
                "    EXIT WHEN VALUECURSOR%NOTFOUND;\n" +
                "    VAR_ARRAY.EXTEND;\n" +
                "    VAR_ARRAY(I) := CURPOLICYINFO.COLUMN_VALUE;\n" +
                "    I := I + 1;\n" +
                "  END LOOP;\n" +
                "  if VAR_ARRAY.count <> 3 then\n" +
                "    RESULT := '1';\n" +
                "  else\n" +
                "    IF VAR_ARRAY(3) = md5(VAR_ARRAY(1) || VAR_ARRAY(2)) THEN\n" +
                "      RESULT := VAR_ARRAY(1);\n" +
                "    ELSE\n" +
                "      RESULT := '1';\n" +
                "    END IF;\n" +
                "  end if;\n" +
                "  RETURN(RESULT);\n" +
                "EXCEPTION\n" +
                "  WHEN OTHERS THEN\n" +
                "    CLOSE VALUECURSOR;\n" +
                "    DBMS_OUTPUT.PUT_LINE(SQLERRM);\n" +
                "    IF VALUECURSOR%ISOPEN THEN\n" +
                "      --close cursor\n" +
                "      CLOSE VALUECURSOR;\n" +
                "    END IF;\n" +
                "END FN_LGN_CHK;";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("FUNCTION FN_LGN_CHK (\n" +
                        "\tUSERINFO IN VARCHAR2\n" +
                        ")\n" +
                        "RETURN VARCHAR2\n" +
                        "IS\n" +
                        "RESULT VARCHAR2(20);\n" +
                        "\tTYPE TYPE_ARRAY IS VARRAY(3) OF VARCHAR2(50);\n" +
                        "\tVAR_ARRAY TYPE_ARRAY := TYPE_ARRAY();\n" +
                        "\tCURSOR VALUECURSOR IS\n" +
                        "\t\tSELECT *\n" +
                        "\t\tFROM TABLE(CAST(FN_SPLIT(USERINFO, ',') AS TY_STR_SPLIT))\n" +
                        "\t\tWHERE ROWNUM < 4;\n" +
                        "\tCURPOLICYINFO VALUECURSOR%ROWTYPE;\n" +
                        "\tI INTEGER := 1;\n" +
                        "BEGIN\n" +
                        "\tOPEN VALUECURSOR;\n" +
                        "\tLOOP\n" +
                        "\t\tFETCH VALUECURSOR INTO CURPOLICYINFO;\n" +
                        "\t\tEXIT WHEN VALUECURSOR%NOTFOUND;\n" +
                        "\t\tVAR_ARRAY.EXTEND;\n" +
                        "\t\tVAR_ARRAY(I) := CURPOLICYINFO.COLUMN_VALUE;\n" +
                        "\t\tI := I + 1;\n" +
                        "\tEND LOOP;\n" +
                        "\tIF VAR_ARRAY.count <> 3 THEN\n" +
                        "\t\tRESULT := '1';\n" +
                        "\tELSE\n" +
                        "\t\tIF VAR_ARRAY(3) = md5(VAR_ARRAY(1) || VAR_ARRAY(2)) THEN\n" +
                        "\t\t\tRESULT := VAR_ARRAY(1);\n" +
                        "\t\tELSE\n" +
                        "\t\t\tRESULT := '1';\n" +
                        "\t\tEND IF;\n" +
                        "\tEND IF;\n" +
                        "\tRETURN RESULT;\n" +
                        "EXCEPTION\n" +
                        "\tWHEN OTHERS THEN\n" +
                        "\t\tCLOSE VALUECURSOR;\n" +
                        "\t\tDBMS_OUTPUT.PUT_LINE(SQLERRM);\n" +
                        "\t\tIF VALUECURSOR % ISOPEN THEN\n" +
                        "\t\t\tCLOSE VALUECURSOR;\n" +
                        "\t\tEND IF;\n" +
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

        assertEquals(1, visitor.getColumns().size());

//        assertTrue(visitor.getColumns().contains(new TableStat.Column("orders", "order_total")));
    }
}
