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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleUpdateTest2 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "UPDATE wrh$_tempfile tfh " //
                     + "    SET (snap_id, filename, tsname) =" //
                     + "    (SELECT :lah_snap_id, tf.name name, ts.name tsname"
                     + //
                     "      FROM v$tempfile tf, ts$ ts"
                     + //
                     "      WHERE tf.ts# = ts.ts# AND tfh.file# = tf.file# AND tfh.creation_change# = tf.creation_change#"
                     + //
                     "  )" + //
                     "WHERE (file#, creation_change#) IN        (" + //
                     "          SELECT tf.tfnum, to_number(tf.tfcrc_scn) creation_change#           " + //
                     "          FROM x$kcctf tf           " + //
                     "          WHERE tf.tfdup != 0)    AND dbid    = :dbid    AND snap_id < :snap_id"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("wrh$_tempfile")));
        assertTrue(visitor.getTables().containsKey(new TableStat.Name("x$kcctf")));

        assertEquals(4, visitor.getTables().size());
        assertEquals(15, visitor.getColumns().size());

        assertTrue(visitor.containsColumn("wrh$_tempfile", "snap_id"));
        assertTrue(visitor.containsColumn("wrh$_tempfile", "filename"));
        assertTrue(visitor.containsColumn("wrh$_tempfile", "tsname"));
        assertTrue(visitor.containsColumn("ts$", "name"));
        assertTrue(visitor.containsColumn("v$tempfile", "ts#"));
    }

}
