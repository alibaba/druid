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

public class OracleCreateViewTest5 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   CREATE OR REPLACE FORCE VIEW \"SC_001\".\"TB_001\" (\"OBJECT_NAME\", \"SESSION_ID\", \"ORACLE_USERNAME\", \"OS_USER_NAME\", \"SQL_ACTIONS\", \"LOCK_MODE\") AS \n" +
                "  SELECT /*+ no_merge(lo) */\n" +
                "       DO.object_name, lo.SESSION_ID, lo.oracle_username, lo.OS_USER_NAME,\n" +
                "       DECODE(locked_mode,\n" +
                "              1, 'SELECT',\n" +
                "              2, 'SELECT FOR UPDATE / LOCK ROW SHARE',\n" +
                "              3, 'INSERT/UPDATE/DELETE/LOCK ROW EXCLUSIVE',\n" +
                "              4, 'CREATE INDEX/LOCK SHARE',\n" +
                "              5, 'LOCK SHARE ROW EXCLUSIVE',\n" +
                "              6, 'ALTER TABLE/DROP TABLE/DROP INDEX/TRUNCATE TABLE/LOCK EXCLUSIVE') sql_actions,\n" +
                "       DECODE(locked_mode, 1, 'NULL', 2, 'SS - SUB SHARE', 3, 'SX - SUB EXCLUSIVE',\n" +
                "              4, 'S - SHARE', 5, 'SSX - SHARE/SUB EXCLUSIVE', 6, 'X - EXCLUSIVE') Lock_mode\n" +
                "  FROM sys.V_$LOCKED_OBJECT lo, TB_002 DO\n" +
                " WHERE DO.object_id = lo.object_id   ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"SC_001\".\"TB_001\" (\n" +
                        "\t\"OBJECT_NAME\", \n" +
                        "\t\"SESSION_ID\", \n" +
                        "\t\"ORACLE_USERNAME\", \n" +
                        "\t\"OS_USER_NAME\", \n" +
                        "\t\"SQL_ACTIONS\", \n" +
                        "\t\"LOCK_MODE\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT /*+ no_merge(lo) */ DO.object_name, lo.SESSION_ID, lo.oracle_username, lo.OS_USER_NAME\n" +
                        "\t, DECODE(locked_mode, 1, 'SELECT', 2, 'SELECT FOR UPDATE / LOCK ROW SHARE', 3, 'INSERT/UPDATE/DELETE/LOCK ROW EXCLUSIVE', 4, 'CREATE INDEX/LOCK SHARE', 5, 'LOCK SHARE ROW EXCLUSIVE', 6, 'ALTER TABLE/DROP TABLE/DROP INDEX/TRUNCATE TABLE/LOCK EXCLUSIVE') AS sql_actions\n" +
                        "\t, DECODE(locked_mode, 1, 'NULL', 2, 'SS - SUB SHARE', 3, 'SX - SUB EXCLUSIVE', 4, 'S - SHARE', 5, 'SSX - SHARE/SUB EXCLUSIVE', 6, 'X - EXCLUSIVE') AS Lock_mode\n" +
                        "FROM sys.V_$LOCKED_OBJECT lo, TB_002 DO\n" +
                        "WHERE DO.object_id = lo.object_id",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(7, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("TB_002", "object_id")));
    }
}
