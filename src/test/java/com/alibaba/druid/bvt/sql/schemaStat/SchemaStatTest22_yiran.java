package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest22_yiran extends TestCase {

    public void test_schemaStat() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);
        repository.console("CREATE TABLE DEPT (depno INT4, dname ENC_TEXT)");

        String sql = "SELECT * FROM ( SELECT dname FROM DEPT) X WHERE X.dname='cs';";
//        sql = "SELECT * FROM ( SELECT * FROM DEPT) X WHERE X.dname='cs'";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(repository);
        stmt.accept(statVisitor);

        SQLSelectQueryBlock q = ((SQLSelectStatement) stmt).getSelect().getFirstQueryBlock();
        SQLPropertyExpr whereLeft = (SQLPropertyExpr) ((SQLBinaryOpExpr) q.getWhere()).getLeft();
        SQLColumnDefinition resolvedColumn = whereLeft.getResolvedColumn();
        assertNotNull(resolvedColumn);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println("columns : " + statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println("conditions : " + statVisitor.getConditions());

        assertEquals(1, statVisitor.getColumns().size());
        assertEquals(1, statVisitor.getConditions().size());
        assertEquals(0, statVisitor.getFunctions().size());
    }
}
