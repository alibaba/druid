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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_49_for_update_no_wait extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select /*MS-ARCORE-AR-II-M-QUERY-IN-LOCK-BY-AR-NOS*/ /*+INDEX(AR_II_M_004 PRIMARY) */         tnt_inst_id,   ar_no,   ar_nm,   ar_tp_code,   ar_entity_type       " +
                "from         tb_001       " +
                "where        tnt_inst_id = ?" +
                "       and      ar_no    in         (             ?         )       for update no_wait";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(1, visitor.getColumns().size());
//        assertEquals(0, visitor.getConditions().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT /*MS-ARCORE-AR-II-M-QUERY-IN-LOCK-BY-AR-NOS*/\n" +
                            "/*+INDEX(AR_II_M_004 PRIMARY) */ tnt_inst_id, ar_no, ar_nm, ar_tp_code, ar_entity_type\n" +
                            "FROM tb_001\n" +
                            "WHERE tnt_inst_id = ?\n" +
                            "\tAND ar_no IN (?)\n" +
                            "FOR UPDATE NOWAIT", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select /*MS-ARCORE-AR-II-M-QUERY-IN-LOCK-BY-AR-NOS*/\n" +
                            "/*+INDEX(AR_II_M_004 PRIMARY) */ tnt_inst_id, ar_no, ar_nm, ar_tp_code, ar_entity_type\n" +
                            "from tb_001\n" +
                            "where tnt_inst_id = ?\n" +
                            "\tand ar_no in (?)\n" +
                            "for update nowait", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT /*+INDEX(AR_II_M_004 PRIMARY) */ tnt_inst_id, ar_no, ar_nm, ar_tp_code, ar_entity_type\n" +
                            "FROM tb\n" +
                            "WHERE tnt_inst_id = ?\n" +
                            "\tAND ar_no IN (?)\n" +
                            "FOR UPDATE NOWAIT", //
                    output);
        }
    }
}
