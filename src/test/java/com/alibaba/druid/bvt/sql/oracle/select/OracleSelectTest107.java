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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest107 extends OracleTest {
    //xml 解析 暂不支持，明宣 反馈的

    public void test_0() throws Exception {
//        String sql = //
//                "SELECT xmlagg(xmlelement(\"operation\", XMLATTRIBUTES (operation AS \"name\", OPTIONS AS \"options\", id AS \"id\", depth AS\n"
//                + "                         \"depth\", position AS \"pos\"), nvl2(object_name, xmlelement(\"object\", object_name), NULL),\n"
//                + "                         DECODE(:rowFormat, 'BASIC', NULL, nvl2(cardinality, xmlelement(\"card\", cardinality), NULL)),\n"
//                + "                         DECODE(:rowFormat, 'BASIC', NULL, nvl2(bytes, xmlelement(\"bytes\", bytes), NULL)),\n"
//                + "                         nvl2(temp_space, xmlelement(\"temp_space\", temp_space), NULL),\n"
//                + "                         DECODE(:rowFormat, 'BASIC', NULL, nvl2(cost, xmlelement(\"cost\", cost), NULL)),\n"
//                + "                         nvl2(io_cost, xmlelement(\"io_cost\", io_cost), NULL),\n"
//                + "                         nvl2(cpu_cost, xmlelement(\"cpu_cost\", cpu_cost), NULL), DECODE(:rowFormat, 'BASIC', NULL,\n"
//                + "                                                                                        nvl2(TIME, xmlelement(\"time\",\n"
//                + "                                                                                                              sys.dbms_xplan.format_time_s(\n"
//                + "                                                                                                                  TIME)),\n"
//                + "                                                                                             NULL)),\n"
//                + "                         nvl2(partition_start,\n"
//                + "                              xmlelement(\"partition\", XMLATTRIBUTES (partition_start AS \"start\", partition_stop AS\n"
//                + "                                         \"stop\")), NULL), nvl2(object_node, xmlelement(\"node\", object_node), NULL),\n"
//                + "                         nvl2(distribution, xmlelement(\"distrib\", distribution), NULL), nvl2(projection, xmlelement(\n"
//                + "    \"project\", projection), NULL), nvl2(access_predicates, xmlelement(\"predicates\", XMLATTRIBUTES (\n"
//                + "                                                                      DECODE(SUBSTR(OPTIONS, 1, 8), 'STORAGE ',\n"
//                + "                                                                             'storage', 'access') AS \"type\"),\n"
//                + "                                                                      access_predicates), NULL), nvl2(filter_predicates,\n"
//                + "                                                                                                      xmlelement(\n"
//                + "                                                                                                          \"predicates\",\n"
//                + "                                                                                                          XMLATTRIBUTES\n"
//                + "                                                                                                          ('filter' AS\n"
//                + "                                                                                                          \"type\"),\n"
//                + "                                                                                                          filter_predicates),\n"
//                + "                                                                                                      NULL),\n"
//                + "                         nvl2(qblock_name, xmlelement(\"qblock\", qblock_name), NULL),\n"
//                + "                         nvl2(object_alias, xmlelement(\"object_alias\", object_alias), NULL), (\n"
//                + "                           CASE\n" + "                           WHEN other_xml IS NULL\n"
//                + "                             THEN NULL\n" + "                           ELSE xmltype(other_xml)\n"
//                + "                           END))) plan\n" + "FROM\n" + "  (SELECT\n"
//                + "     /*+ opt_param('parallel_execution_enabled','false') */\n" + "     /* EXEC_FROM_DBMS_XPLAN */\n"
//                + "     id,\n" + "     position,\n" + "     depth,\n" + "     operation,\n" + "     OPTIONS,\n"
//                + "     object_name,\n" + "     cardinality,\n" + "     bytes,\n" + "     temp_space,\n"
//                + "     cost,\n" + "     io_cost,\n" + "     cpu_cost,\n" + "     TIME,\n" + "     partition_start,\n"
//                + "     partition_stop,\n" + "     object_node,\n" + "     other_tag,\n" + "     distribution,\n"
//                + "     projection,\n" + "     access_predicates,\n" + "     filter_predicates,\n" + "     other,\n"
//                + "     qblock_name,\n" + "     object_alias,\n" + "     NVL(other_xml, remarks) other_xml,\n"
//                + "     NULL                    sql_profile,\n" + "     NULL                    sql_plan_baseline,\n"
//                + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL,\n"
//                + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL,\n"
//                + "     NULL,\n" + "     NULL,\n" + "     NULL,\n" + "     NULL\n" + "   FROM PLAN_TABLE\n"
//                + "   WHERE plan_id =\n" + "         (SELECT MAX(plan_id)\n" + "          FROM PLAN_TABLE\n"
//                + "          WHERE id = 0\n" + "         )\n" + "   ORDER BY id\n" + "  )";
//
//        System.out.println(sql);
//
//        OracleStatementParser parser = new OracleStatementParser(sql);
//        List<SQLStatement> statementList = parser.parseStatementList();
//        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);
//        System.out.println(stmt.toString());
//
//        assertEquals(1, statementList.size());
//
//        SchemaRepository repository = new SchemaRepository(DbType.oracle);
//        repository.resolve(stmt);
//
//        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
//        stmt.accept(visitor);
//
//        TableStat.Column cid = visitor.getColumn("a", "cid");
//        assertNotNull(cid);
//        assertTrue(cid.isWhere());
//
//        TableStat.Condition condition = visitor.getConditions().get(2);
//        assertTrue(condition.getColumn().isWhere());
//        assertSame(cid, condition.getColumn());
//
//        {
//            String text = SQLUtils.toOracleString(stmt);
//
//            assertEquals("SELECT *\n" +
//                    "FROM a\n" +
//                    "\tJOIN b ON a.id = b.aid \n" +
//                    "WHERE a.cid = 1", text);
//        }
//
//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("relationships : " + visitor.getRelationships());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
//
//        assertEquals(2, visitor.getTables().size());
//        assertEquals(3, visitor.getColumns().size());
//        assertEquals(3, visitor.getConditions().size());
//        assertEquals(1, visitor.getRelationships().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
//
//
    }

   
}
