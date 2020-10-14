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
package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.PGTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class PGSelectTest16 extends PGTest {

    public void test_0() throws Exception {
        String sql = "WITH RECURSIVE search_graph(id, link, data, depth, path, cycle) AS ("
                + "        SELECT g.id, g.link, g.data, 1,"
                + "          ARRAY[g.id],"
                + "          false"
                + "        FROM graph g"
                + "      UNION ALL"
                + "        SELECT g.id, g.link, g.data, sg.depth + 1,"
                + "          path || g.id,"
                + "          g.id = ANY(path)"
                + "        FROM graph g, search_graph sg"
                + "        WHERE g.id = sg.link AND NOT cycle"
                + ")"
                + "SELECT * FROM search_graph;";

        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        System.out.println(stmt);

        assertEquals(1, statementList.size());

        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(3, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
        
        assertTrue(visitor.getColumns().contains(new TableStat.Column("graph", "id")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("graph", "link")));
        assertTrue(visitor.getColumns().contains(new TableStat.Column("graph", "data")));
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("search_graph", "depth")));
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("graph", "path")));
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("search_graph", "link")));
//        assertTrue(visitor.getColumns().contains(new TableStat.Column("graph", "cycle")));
    }
}

// select categoryId , offerIds from cnres.function_select_get_spt_p4p_offer_list (' 1031918 , 1031919 , 1037004 ') as
// a(categoryId numeric,offerIds character varying(4000))
// select memberId , offerIds from cnres.function_select_get_seller_hot_offer_list('\'gzyyd168\'') as a(memberId
// character varying(20),offerIds character varying(4000))

