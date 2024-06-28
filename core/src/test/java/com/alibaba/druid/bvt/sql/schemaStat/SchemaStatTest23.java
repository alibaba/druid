package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest23 extends TestCase {
    public void test_schemaStat() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);

        String sql = "with par_temp as (\n" +
                " select a from sw001 where ds = '20230531'\n" +
                ")\n" +
                "SELECT * from (\n" +
                " SELECT cast(b as BIGINT) as b \n" +
                " from sw002 where ds = '20230531'\n" +
                ")sw2 LEFT join par_temp sw004\n" +
                "on sw004.a = sw2.b";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println("columns : " + statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(5, statVisitor.getColumns().size());
        assertEquals(4, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());
//
//        assertTrue(statVisitor.containsTable("table1"));
//        assertTrue(statVisitor.containsColumn("table1", "*"));
//        assertTrue(statVisitor.containsColumn("table1", "id"));
//        assertTrue(statVisitor.containsColumn("table2", "id"));
//        assertTrue(statVisitor.containsColumn("table2", "*"));
    }
}
