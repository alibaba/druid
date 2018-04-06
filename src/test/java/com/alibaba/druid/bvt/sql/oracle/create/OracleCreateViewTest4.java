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
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateViewTest4 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "  CREATE OR REPLACE FORCE VIEW \"SC_001\".\"V_001\" (\"OBJ_OWNER\", \"OBJ_NAME\", \"OBJ_TYPE\", \"OBJ_ROWID\", \"DB_USER\", \"SID\", \"LOCK_TYPE\", \"ROW_WAIT_FILE#\", \"ROW_WAIT_BLOCK#\", \"ROW_WAIT_ROW#\") AS \n" +
                "  SELECT owner obj_owner,\n" +
                "       object_name obj_name,\n" +
                "       object_type  obj_type,\n" +
                "       dbms_rowid.rowid_create(1, row_wait_obj#, ROW_WAIT_FILE#,\n" +
                "                               ROW_WAIT_BLOCK#,ROW_WAIT_ROW#) obj_rowid,\n" +
                "       a.username db_user, a.SID SID, a.TYPE lock_type,\n" +
                "       a.row_wait_file#, a.row_wait_block#, a.row_wait_row#\n" +
                "  FROM TB_001,\n" +
                "       (SELECT /*+ no_merge(a) no_merge(b) */\n" +
                "               a.username, a.SID, a.row_wait_obj#, a.ROW_WAIT_FILE#,\n" +
                "               a.ROW_WAIT_BLOCK#, a.ROW_WAIT_ROW#, b.TYPE\n" +
                "          FROM sys.V_$SESSION a, sys.V_$LOCK b\n" +
                "         WHERE a.username IS NOT NULL\n" +
                "           AND a.row_wait_obj# <> -1\n" +
                "           AND a.SID = b.SID\n" +
                "           AND b.TYPE IN ('TX','TM')\n" +
                "           ) a\n" +
                " WHERE object_id = a.row_wait_obj#   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"SC_001\".\"V_001\" (\n" +
                        "\t\"OBJ_OWNER\", \n" +
                        "\t\"OBJ_NAME\", \n" +
                        "\t\"OBJ_TYPE\", \n" +
                        "\t\"OBJ_ROWID\", \n" +
                        "\t\"DB_USER\", \n" +
                        "\t\"SID\", \n" +
                        "\t\"LOCK_TYPE\", \n" +
                        "\t\"ROW_WAIT_FILE#\", \n" +
                        "\t\"ROW_WAIT_BLOCK#\", \n" +
                        "\t\"ROW_WAIT_ROW#\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT owner AS obj_owner, object_name AS obj_name, object_type AS obj_type\n" +
                        "\t, dbms_rowid.rowid_create(1, row_wait_obj#, ROW_WAIT_FILE#, ROW_WAIT_BLOCK#, ROW_WAIT_ROW#) AS obj_rowid\n" +
                        "\t, a.username AS db_user, a.SID AS SID, a.TYPE AS lock_type, a.row_wait_file#, a.row_wait_block#\n" +
                        "\t, a.row_wait_row#\n" +
                        "FROM TB_001, (\n" +
                        "\tSELECT /*+ no_merge(a) no_merge(b) */ a.username, a.SID, a.row_wait_obj#, a.ROW_WAIT_FILE#, a.ROW_WAIT_BLOCK#\n" +
                        "\t\t, a.ROW_WAIT_ROW#, b.TYPE\n" +
                        "\tFROM sys.V_$SESSION a, sys.V_$LOCK b\n" +
                        "\tWHERE a.username IS NOT NULL\n" +
                        "\t\tAND a.row_wait_obj# <> -1\n" +
                        "\t\tAND a.SID = b.SID\n" +
                        "\t\tAND b.TYPE IN ('TX', 'TM')\n" +
                        ") a\n" +
                        "WHERE object_id = a.row_wait_obj#",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(3, visitor.getTables().size());

        Assert.assertEquals(12, visitor.getColumns().size());

        Assert.assertTrue(visitor.containsColumn("sys.V_$SESSION", "username"));
        Assert.assertTrue(visitor.containsColumn("sys.V_$SESSION", "SID"));
        Assert.assertTrue(visitor.containsColumn("sys.V_$SESSION", "row_wait_obj#"));
    }
}
