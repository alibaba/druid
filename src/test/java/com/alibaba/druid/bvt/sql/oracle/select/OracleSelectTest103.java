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

public class OracleSelectTest103 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
                "SELECT *\n" +
                        "FROM\n" +
                        "  (SELECT '- Tablespace ->',\n" +
                        "    t.tablespace_name ktablespace,\n" +
                        "    '- Type->',\n" +
                        "    SUBSTR(t.contents, 1, 1) tipo,\n" +
                        "    '- Used(MB)->',\n" +
                        "    TRUNC((d.tbs_size-NVL(s.free_space, 0))/1024/1024) ktbs_em_uso,\n" +
                        "    '- ActualSize(MB)->',\n" +
                        "    TRUNC(d.tbs_size/1024/1024) ktbs_size,\n" +
                        "    '- MaxSize(MB)->',\n" +
                        "    TRUNC(d.tbs_maxsize/1024/1024) ktbs_maxsize,\n" +
                        "    '- FreeSpace(MB)->',\n" +
                        "    TRUNC(NVL(s.free_space, 0)/1024/1024) kfree_space,\n" +
                        "    '- Space->',\n" +
                        "    TRUNC((d.tbs_maxsize - d.tbs_size + NVL(s.free_space, 0))/1024/1024) kspace,\n" +
                        "    '- Perc->',\n" +
                        "    DECODE(d.tbs_maxsize, 0, 0, TRUNC((d.tbs_size-NVL(s.free_space, 0))*100/d.tbs_maxsize)) kperc\n" +
                        "  FROM\n" +
                        "    (SELECT SUM(bytes) tbs_size,\n" +
                        "      SUM(DECODE(SIGN(maxbytes - bytes), -1, bytes, maxbytes)) tbs_maxsize,\n" +
                        "      tablespace_name TABLESPACE\n" +
                        "    FROM\n" +
                        "      (SELECT NVL(bytes, 0) bytes,\n" +
                        "        NVL(maxbytes, 0) maxbytes,\n" +
                        "        tablespace_name\n" +
                        "      FROM dba_data_files\n" +
                        "      UNION ALL\n" +
                        "      SELECT NVL(bytes, 0) bytes,\n" +
                        "        NVL(maxbytes, 0) maxbytes,\n" +
                        "        tablespace_name\n" +
                        "      FROM dba_temp_files\n" +
                        "      )\n" +
                        "    GROUP BY tablespace_name\n" +
                        "    ) d,\n" +
                        "    (SELECT SUM(bytes) free_space,\n" +
                        "      tablespace_name TABLESPACE\n" +
                        "    FROM dba_free_space\n" +
                        "    GROUP BY tablespace_name\n" +
                        "    ) s,\n" +
                        "    dba_tablespaces t\n" +
                        "  WHERE t.tablespace_name = d.tablespace(+)\n" +
                        "  AND t.tablespace_name   = s.tablespace(+)\n" +
                        "  ORDER BY 8\n" +
                        "  )\n" +
                        "WHERE kperc > 95\n" +
                        "AND tipo   <>'T'\n" +
                        "AND tipo   <>'U'";

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

            assertEquals("SELECT *\n" +
                    "FROM (\n" +
                    "\tSELECT '- Tablespace ->', t.tablespace_name AS ktablespace, '- Type->'\n" +
                    "\t\t, SUBSTR(t.contents, 1, 1) AS tipo\n" +
                    "\t\t, '- Used(MB)->'\n" +
                    "\t\t, TRUNC((d.tbs_size - NVL(s.free_space, 0)) / 1024 / 1024) AS ktbs_em_uso\n" +
                    "\t\t, '- ActualSize(MB)->', TRUNC(d.tbs_size / 1024 / 1024) AS ktbs_size\n" +
                    "\t\t, '- MaxSize(MB)->', TRUNC(d.tbs_maxsize / 1024 / 1024) AS ktbs_maxsize\n" +
                    "\t\t, '- FreeSpace(MB)->'\n" +
                    "\t\t, TRUNC(NVL(s.free_space, 0) / 1024 / 1024) AS kfree_space\n" +
                    "\t\t, '- Space->'\n" +
                    "\t\t, TRUNC((d.tbs_maxsize - d.tbs_size + NVL(s.free_space, 0)) / 1024 / 1024) AS kspace\n" +
                    "\t\t, '- Perc->'\n" +
                    "\t\t, DECODE(d.tbs_maxsize, 0, 0, TRUNC((d.tbs_size - NVL(s.free_space, 0)) * 100 / d.tbs_maxsize)) AS kperc\n" +
                    "\tFROM (\n" +
                    "\t\tSELECT SUM(bytes) AS tbs_size\n" +
                    "\t\t\t, SUM(DECODE(SIGN(maxbytes - bytes), -1, bytes, maxbytes)) AS tbs_maxsize\n" +
                    "\t\t\t, tablespace_name AS TABLESPACE\n" +
                    "\t\tFROM (\n" +
                    "\t\t\tSELECT NVL(bytes, 0) AS bytes\n" +
                    "\t\t\t\t, NVL(maxbytes, 0) AS maxbytes, tablespace_name\n" +
                    "\t\t\tFROM dba_data_files\n" +
                    "\t\t\tUNION ALL\n" +
                    "\t\t\tSELECT NVL(bytes, 0) AS bytes\n" +
                    "\t\t\t\t, NVL(maxbytes, 0) AS maxbytes, tablespace_name\n" +
                    "\t\t\tFROM dba_temp_files\n" +
                    "\t\t)\n" +
                    "\t\tGROUP BY tablespace_name\n" +
                    "\t) d, (\n" +
                    "\t\tSELECT SUM(bytes) AS free_space, tablespace_name AS TABLESPACE\n" +
                    "\t\tFROM dba_free_space\n" +
                    "\t\tGROUP BY tablespace_name\n" +
                    "\t) s, dba_tablespaces t\n" +
                    "\tWHERE t.tablespace_name = d.tablespace(+)\n" +
                    "\t\tAND t.tablespace_name = s.tablespace(+)\n" +
                    "\tORDER BY 8\n" +
                    ")\n" +
                    "WHERE kperc > 95\n" +
                    "\tAND tipo <> 'T'\n" +
                    "\tAND tipo <> 'U'", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(4, visitor.getTables().size());
        assertEquals(10, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(1, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());
    }

   
}
