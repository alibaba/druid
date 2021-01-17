package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest21_issue3980 extends TestCase {

    public void test_schemaStat() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);

        String sql = "select n.user_name,\n" +
                "n.user_passwd,\n" +
                "n.user_project,\n" +
                "n.start_date,\n" +
                "n.end_date\n" +
                "from (\n" +
                "select t.name as user_name,\n" +
                "t.passwd as user_passwd,\n" +
                "cast(from_unixtime(t.from_time, \"yyyyMMdd\") as int) as start_date,\n" +
                "cast(from_unixtime(t.to_time, \"yyyyMMdd\") as int) as end_date\n" +
                "from tableA as t\n" +
                "where t.user_id = 1\n" +
                "union all\n" +
                "select p.project as user_project\n" +
                "from tableB as p\n" +
                "where p.project_id = 10\n" +
                ") as n;";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println("columns : " + statVisitor.getColumns());
        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println("conditions : " + statVisitor.getConditions());
        System.out.println("functions : " + statVisitor.getFunctions());

        assertEquals(7, statVisitor.getColumns().size());
        assertEquals(2, statVisitor.getConditions().size());
        assertEquals(2, statVisitor.getFunctions().size());

        SQLPropertyExpr expr = (SQLPropertyExpr) statVisitor.getFunctions().get(0).getArguments().get(0);
        SQLIdentifierExpr tableAlias = (SQLIdentifierExpr) expr.getOwner();
        tableAlias.getResolvedTableSource();
    }
}
