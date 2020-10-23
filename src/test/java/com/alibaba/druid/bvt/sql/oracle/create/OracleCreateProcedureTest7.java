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

import java.util.List;

public class OracleCreateProcedureTest7 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "CREATE OR REPLACE PROCEDURE proc_helloworld\n" + "AS\n" + "\tV_TMP_NUM1 NUMBER(2);\n"
                     + "\tV_TMP_NUM2 NUMBER(2);\n" + "\tTYPE T_VARRAY IS VARRAY(30) OF NUMBER(2);\n"
                     + "\tV_VAR T_VARRAY := T_VARRAY(11, 12, 13, 14, 21, 22, 23, 24, 31, 32, 33, 34, 44, 41, 42, 43, 51, 52, 53, 61, 62, 63, 71, 72, 73, 81, 82, 83, 91, 92);\n"
                     + "BEGIN\n" + "\tFOR ROWDATA IN (\n" + "\t\tSELECT *\n" + "\t\tFROM user_defer_ctrl_cfg\n"
                     + "\t\tWHERE HOME_CITY = 592\n" + "\t\t\tAND DEFER_TYPE = 1\n" + "\t\t\tAND STATUS = 0\n"
                     + "\t\t\tAND DEFER_END_DATE < to_date(20171217, 'yyyymmdd')\n" + "\t)\n" + "\tLOOP\n"
                     + "\t\tBEGIN\n" + "\t\t\tV_USER_ID := ROWDATA.USER_ID;\n" + "\t\t\tV_SEQ_ID := ROWDATA.SEQ_ID;\n"
                     + "\t\t\tV_TMP_NUM1 := MOD(V_USER_ID, 10);\n" + "\t\t\tV_TMP_NUM2 := MOD(V_USER_ID, 3);\n"
                     + "\t\t\tV_INDEX := V_TMP_NUM1 * 3 + V_TMP_NUM2 + 1;\n" + "\t\t\tINSERT INTO CREDIT_CTRL_REQ\n"
                     + "\t\t\t\t(PROC_NO, REQ_SRC, HOME_CITY, EXEC_STATUS, REQ_ID\n"
                     + "\t\t\t\t, CTRL_ID, CTRL_TYPE, ID_TYPE, INSERT_TIME, REQUEST_SEQ)\n"
                     + "\t\t\tVALUES (V_VAR(V_INDEX), 1, 592, 0, Seq_Credit_Ctrl_Req_ID.NEXTVAL\n"
                     + "\t\t\t\t, V_USER_ID, 1, 1, SYSDATE, V_SEQ_ID);\n" + "\t\t\tUPDATE user_defer_ctrl_cfg\n"
                     + "\t\t\tSET STATUS = 9\n" + "\t\t\tWHERE SEQ_ID = V_SEQ_ID;\n" + "\t\tEND;\n" + "\tEND LOOP;\n"
                     + "\tCOMMIT;\n" + "END;";
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        assertEquals(1, statementList.size());

        SQLStatement stmt = (SQLStatement) statementList.get(0);

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        assertEquals("CREATE OR REPLACE PROCEDURE proc_helloworld\n" + "AS\n" + "\tV_TMP_NUM1 NUMBER(2);\n"
                     + "\tV_TMP_NUM2 NUMBER(2);\n" + "\tTYPE T_VARRAY IS VARRAY(30) OF NUMBER(2);\n"
                     + "\tV_VAR T_VARRAY := T_VARRAY(11, 12, 13, 14, 21, 22, 23, 24, 31, 32, 33, 34, 44, 41, 42, 43, 51, 52, 53, 61, 62, 63, 71, 72, 73, 81, 82, 83, 91, 92);\n"
                     + "BEGIN\n" + "\tFOR ROWDATA IN (\n" + "\t\tSELECT *\n" + "\t\tFROM user_defer_ctrl_cfg\n"
                     + "\t\tWHERE HOME_CITY = 592\n" + "\t\t\tAND DEFER_TYPE = 1\n" + "\t\t\tAND STATUS = 0\n"
                     + "\t\t\tAND DEFER_END_DATE < to_date(20171217, 'yyyymmdd')\n" + "\t)\n" + "\tLOOP\n"
                     + "\t\tBEGIN\n" + "\t\t\tV_USER_ID := ROWDATA.USER_ID;\n" + "\t\t\tV_SEQ_ID := ROWDATA.SEQ_ID;\n"
                     + "\t\t\tV_TMP_NUM1 := MOD(V_USER_ID, 10);\n" + "\t\t\tV_TMP_NUM2 := MOD(V_USER_ID, 3);\n"
                     + "\t\t\tV_INDEX := V_TMP_NUM1 * 3 + V_TMP_NUM2 + 1;\n" + "\t\t\tINSERT INTO CREDIT_CTRL_REQ\n"
                     + "\t\t\t\t(PROC_NO, REQ_SRC, HOME_CITY, EXEC_STATUS, REQ_ID\n"
                     + "\t\t\t\t, CTRL_ID, CTRL_TYPE, ID_TYPE, INSERT_TIME, REQUEST_SEQ)\n"
                     + "\t\t\tVALUES (V_VAR(V_INDEX), 1, 592, 0, Seq_Credit_Ctrl_Req_ID.NEXTVAL\n"
                     + "\t\t\t\t, V_USER_ID, 1, 1, SYSDATE, V_SEQ_ID);\n" + "\t\t\tUPDATE user_defer_ctrl_cfg\n"
                     + "\t\t\tSET STATUS = 9\n" + "\t\t\tWHERE SEQ_ID = V_SEQ_ID;\n" + "\t\tEND;\n" + "\tEND LOOP;\n"
                     + "\tCOMMIT;\n" + "END;", stmt.toString());


        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());


        assertEquals(17, visitor.getColumns().size());
        assertEquals(6, visitor.getConditions().size());
        assertEquals(1, visitor.getRelationships().size());

    }
}
