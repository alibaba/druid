package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaObjectType;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest15 extends TestCase {

    public void test_schemaStat() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        repository.acceptDDL("create table table1 (fid bigint, f1 varchar(100), f2 varchar(100))");

        String sql = "select * from table1 t where t.f3 = 3";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = parser.parseStatementList().get(0);

        System.out.println(stmt);

        repository.resolve(stmt);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(4, statVisitor.getColumns().size());
        assertEquals(1, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("table1"));
        assertTrue(statVisitor.containsColumn("table1", "f1"));
        assertTrue(statVisitor.containsColumn("table1", "f2"));
        assertTrue(statVisitor.containsColumn("UNKNOWN", "f3"));
    }
    public void test_schemaStat_2() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        repository.acceptDDL("create table table1 (fid bigint, f1 varchar(100), f2 varchar(100));");
        repository.acceptDDL("create view view1 as select fid table1 where fid = 1;");

        SchemaObject viewObject = repository.findView("view1");
        System.out.println(viewObject);

        assertEquals("view1", viewObject.getName());
        assertEquals(SchemaObjectType.View, viewObject.getType());
    }
}
