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

import java.util.List;

public class OracleCreateViewTest15 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "CREATE OR REPLACE FORCE VIEW \"ZJGT_DKXY\".\"V_ZJ_DKXMXXEXP\" (\"PRJ_NO\", \"EXPLST\") AS \n" +
                "  select b.prj_no,explst\n" +
                "  from (with temp_exp as (select t.prj_no, t.explx,t.expname\n" +
                "                            from v_zjk_xmlist t\n" +
                "                           where t.expname is not null\n" +
                "                             and t.explx not like '%组员%')\n" +
                "         select prj_no,\n" +
                "                dbms_lob.substr(wmsys.wm_concat(explx||': '||expname) over(partition by prj_no)，4000,1) as explst\n" +
                "           from temp_exp) b\n" +
                "          group by b.prj_no,explst"
               ;

        System.out.println(sql);

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        assertEquals(1, statementList.size());

        assertEquals("CREATE OR REPLACE VIEW \"ZJGT_DKXY\".\"V_ZJ_DKXMXXEXP\" (\n" +
                        "\t\"PRJ_NO\", \n" +
                        "\t\"EXPLST\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT b.prj_no, explst\n" +
                        "FROM (\n" +
                        "\tWITH temp_exp AS (\n" +
                        "\t\t\tSELECT t.prj_no, t.explx, t.expname\n" +
                        "\t\t\tFROM v_zjk_xmlist t\n" +
                        "\t\t\tWHERE t.expname IS NOT NULL\n" +
                        "\t\t\t\tAND t.explx NOT LIKE '%组员%'\n" +
                        "\t\t)\n" +
                        "\tSELECT prj_no\n" +
                        "\t\t, dbms_lob.substr(wmsys.wm_concat(explx || ': ' || expname) OVER (PARTITION BY prj_no ), 4000, 1) AS explst\n" +
                        "\tFROM temp_exp\n" +
                        ") b\n" +
                        "GROUP BY b.prj_no, explst",//
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

        assertTrue(visitor.getColumns().contains(new TableStat.Column("v_zjk_xmlist", "prj_no")));
    }
}
