package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class SQLSelectQueryBlockTest extends TestCase {
    private final DbType dbType = JdbcConstants.MYSQL;
    private SchemaRepository repository;

    protected void setUp() throws Exception {
        repository = new SchemaRepository(dbType);
    }

    public void test_findTableSource() throws Exception {

        repository.console("create table t_emp(emp_id bigint, name varchar(20));");
        repository.console("create table t_org(org_id bigint, name varchar(20));");

        String sql = "SELECT emp_id, a.name AS emp_name, org_id, b.name AS org_name\n" +
                "FROM t_emp a\n" +
                "\tINNER JOIN t_org b ON a.emp_id = b.org_id";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        assertEquals(1, stmtList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) stmtList.get(0);
        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

        // 大小写不敏感
        assertNotNull(queryBlock.findTableSource("A"));
        assertSame(queryBlock.findTableSource("a"), queryBlock.findTableSource("A"));

        assertNull(queryBlock.findTableSourceWithColumn("emp_id"));

        // 使用repository做column resolve
        repository.resolve(stmt);

        assertNotNull(queryBlock.findTableSourceWithColumn("emp_id"));

        SQLExprTableSource tableSource = (SQLExprTableSource) queryBlock.findTableSourceWithColumn("emp_id");
        assertNotNull(tableSource.getSchemaObject());

        SQLCreateTableStatement createTableStmt = (SQLCreateTableStatement) tableSource.getSchemaObject().getStatement();
        assertNotNull(createTableStmt);

        SQLSelectItem selectItem = queryBlock.findSelectItem("org_name");
        assertNotNull(selectItem);
        SQLPropertyExpr selectItemExpr = (SQLPropertyExpr) selectItem.getExpr();
        SQLColumnDefinition column = selectItemExpr.getResolvedColumn();
        assertNotNull(column);
        assertEquals("name", column.getName().toString());
        assertEquals("t_org", (((SQLCreateTableStatement)column.getParent()).getName().toString()));

        assertSame(queryBlock.findTableSource("B"), selectItemExpr.getResolvedTableSource());
    }
}
