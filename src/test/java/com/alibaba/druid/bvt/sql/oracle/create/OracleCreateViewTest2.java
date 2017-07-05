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
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateViewTest2 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "  CREATE OR REPLACE FORCE VIEW \"RMAN\".\"V_001\" (\"DB_KEY\", \"DB_ID\", \"BS_KEY\", \"RECID\", \"STAMP\", \"SET_STAMP\", \"SET_COUNT\", \"BACKUP_TYPE\", \"INCREMENTAL_LEVEL\", \"PIECES\", \"START_TIME\", \"COMPLETION_TIME\", \"ELAPSED_SECONDS\", \"STATUS\", \"CONTROLFILE_INCLUDED\", \"INPUT_FILE_SCAN_ONLY\", \"KEEP\", \"KEEP_UNTIL\", \"KEEP_OPTIONS\") AS \n" +
                "  select db.db_key,\n" +
                "       db.db_id,\n" +
                "       bs.bs_key,\n" +
                "       bs.bs_recid recid,\n" +
                "       bs.bs_stamp stamp,\n" +
                "       bs.set_stamp,\n" +
                "       bs.set_count,\n" +
                "       bs.bck_type backup_type,\n" +
                "       bs.incr_level incremental_level,\n" +
                "       bs.pieces,\n" +
                "       bs.start_time,\n" +
                "       bs.completion_time,\n" +
                "       abs((bs.completion_time - bs.start_time) * 86400) elapsed_seconds,\n" +
                "       bs.status,\n" +
                "       bs.controlfile_included,\n" +
                "       bs.input_file_scan_only,\n" +
                "       decode(keep_options, 0, 'NO',\n" +
                "                               'YES') keep,\n" +
                "       keep_until,\n" +
                "       decode(keep_options, 256,  'LOGS',\n" +
                "                            512,  'NOLOGS',\n" +
                "                            1024, 'CONSISTENT',\n" +
                "                                  NULL) keep_options\n" +
                "from db, bs\n" +
                "where db.db_key = bs.db_key    ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"RMAN\".\"V_001\" (\n" +
                        "\t\"DB_KEY\", \n" +
                        "\t\"DB_ID\", \n" +
                        "\t\"BS_KEY\", \n" +
                        "\t\"RECID\", \n" +
                        "\t\"STAMP\", \n" +
                        "\t\"SET_STAMP\", \n" +
                        "\t\"SET_COUNT\", \n" +
                        "\t\"BACKUP_TYPE\", \n" +
                        "\t\"INCREMENTAL_LEVEL\", \n" +
                        "\t\"PIECES\", \n" +
                        "\t\"START_TIME\", \n" +
                        "\t\"COMPLETION_TIME\", \n" +
                        "\t\"ELAPSED_SECONDS\", \n" +
                        "\t\"STATUS\", \n" +
                        "\t\"CONTROLFILE_INCLUDED\", \n" +
                        "\t\"INPUT_FILE_SCAN_ONLY\", \n" +
                        "\t\"KEEP\", \n" +
                        "\t\"KEEP_UNTIL\", \n" +
                        "\t\"KEEP_OPTIONS\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT db.db_key, db.db_id, bs.bs_key, bs.bs_recid AS recid, bs.bs_stamp AS stamp\n" +
                        "\t, bs.set_stamp, bs.set_count, bs.bck_type AS backup_type, bs.incr_level AS incremental_level, bs.pieces\n" +
                        "\t, bs.start_time, bs.completion_time\n" +
                        "\t, abs((bs.completion_time - bs.start_time) * 86400) AS elapsed_seconds\n" +
                        "\t, bs.status, bs.controlfile_included, bs.input_file_scan_only\n" +
                        "\t, decode(keep_options, 0, 'NO', 'YES') AS keep\n" +
                        "\t, keep_until\n" +
                        "\t, decode(keep_options, 256, 'LOGS', 512, 'NOLOGS', 1024, 'CONSISTENT', NULL) AS keep_options\n" +
                        "FROM db, bs\n" +
                        "WHERE db.db_key = bs.db_key",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(18, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("db", "db_key")));
    }
}
