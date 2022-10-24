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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest128 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT id,\n" +
                "  MAX(myRank) area\n" +
                "FROM\n" +
                "  (SELECT t2.id ,\n" +
                "    wmsys.wm_concat(t2.name) over (partition BY t2.id order by to_number(t2.cc) ASC ) myRank\n" +
                "  FROM\n" +
                "    (SELECT adr.name,\n" +
                "      t1.*\n" +
                "    FROM\n" +
                "      (SELECT t.id,\n" +
                "        t.addresscode,\n" +
                "        regexp_substr(t.addresscode, '[^_]+', 1, x.n) cc\n" +
                "      FROM\n" +
                "        ( SELECT id,addresscode FROM srm1.CONSIGNEE_ADDRESS ca\n" +
                "        ) t,\n" +
                "        (SELECT ROWNUM n FROM dual CONNECT BY ROWNUM <= 5\n" +
                "        ) x\n" +
                "      ORDER BY 1\n" +
                "      ) t1\n" +
                "    LEFT JOIN srm1.address adr\n" +
                "    ON adr.id    = t1.cc\n" +
                "    WHERE t1.cc IS NOT NULL\n" +
                "    ) t2\n" +
                "  )\n" +
                "GROUP BY id";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT id, MAX(myRank) AS area\n" +
                "FROM (\n" +
                "\tSELECT t2.id, wmsys.wm_concat(t2.name) OVER (PARTITION BY t2.id ORDER BY to_number(t2.cc) ASC) AS myRank\n" +
                "\tFROM (\n" +
                "\t\tSELECT adr.name, t1.*\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT t.id, t.addresscode\n" +
                "\t\t\t\t, regexp_substr(t.addresscode, '[^_]+', 1, x.n) AS cc\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT id, addresscode\n" +
                "\t\t\t\tFROM srm1.CONSIGNEE_ADDRESS ca\n" +
                "\t\t\t) t, (\n" +
                "\t\t\t\tSELECT ROWNUM AS n\n" +
                "\t\t\t\tFROM dual\n" +
                "\t\t\t\tCONNECT BY ROWNUM <= 5\n" +
                "\t\t\t) x\n" +
                "\t\t\tORDER BY 1\n" +
                "\t\t) t1\n" +
                "\t\t\tLEFT JOIN srm1.address adr ON adr.id = t1.cc \n" +
                "\t\tWHERE t1.cc IS NOT NULL\n" +
                "\t) t2\n" +
                ")\n" +
                "GROUP BY id", stmt.toString());

        assertEquals("select id, max(myRank) as area\n" +
                "from (\n" +
                "\tselect t2.id, wmsys.wm_concat(t2.name) over (partition by t2.id order by to_number(t2.cc) asc) as myRank\n" +
                "\tfrom (\n" +
                "\t\tselect adr.name, t1.*\n" +
                "\t\tfrom (\n" +
                "\t\t\tselect t.id, t.addresscode\n" +
                "\t\t\t\t, regexp_substr(t.addresscode, '[^_]+', 1, x.n) as cc\n" +
                "\t\t\tfrom (\n" +
                "\t\t\t\tselect id, addresscode\n" +
                "\t\t\t\tfrom srm1.CONSIGNEE_ADDRESS ca\n" +
                "\t\t\t) t, (\n" +
                "\t\t\t\tselect ROWNUM as n\n" +
                "\t\t\t\tfrom dual\n" +
                "\t\t\t\tconnect by ROWNUM <= 5\n" +
                "\t\t\t) x\n" +
                "\t\t\torder by 1\n" +
                "\t\t) t1\n" +
                "\t\t\tleft join srm1.address adr on adr.id = t1.cc \n" +
                "\t\twhere t1.cc is not null\n" +
                "\t) t2\n" +
                ")\n" +
                "group by id", stmt.toLowerCaseString());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(2, visitor.getTables().size());
        assertEquals(4, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsColumn("srm1.CONSIGNEE_ADDRESS", "id"));
    }

}