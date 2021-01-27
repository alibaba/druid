package com.alibaba.druid.bvt.sql.mysql.resolve;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.repository.SchemaResolveVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import junit.framework.TestCase;

import java.util.List;

public class Resolve_AllColumn_Test extends TestCase {
    public void test_resolve() throws Exception {
        SchemaRepository repository = new SchemaRepository(DbType.mysql);

        repository.acceptDDL("create table t_emp(emp_id bigint, name varchar(20));");

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement("select * from t_emp");
        repository.resolve(stmt, SchemaResolveVisitor.Option.ResolveAllColumn);

        assertEquals("SELECT emp_id, name\n" +
                "FROM t_emp", stmt.toString());

        SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) stmt).getSelect().getQueryBlock();
        SQLIdentifierExpr expr = (SQLIdentifierExpr) queryBlock.getSelectList().get(0).getExpr();
        assertNotNull(expr.getResolvedColumn());

        new SQLASTVisitorAdapter() {
          public boolean visit(SQLSelectQueryBlock queryBlock) {
              final List<SQLSelectItem> selectList = queryBlock.getSelectList();
              for (int i = 0; i < selectList.size(); i++) {
                  final SQLSelectItem selectItem = selectList.get(i);
                  final SQLExpr expr = selectItem.getExpr();
                  if (expr instanceof SQLAllColumnExpr) {

                  } else if (expr instanceof SQLPropertyExpr && ((SQLPropertyExpr) expr).getName().equals("*")) {

                  }

              }
              return true;
          }
        };
    }

    public void test_resolve_1() throws Exception {
        SchemaRepository repository = new SchemaRepository(DbType.mysql);

        repository.acceptDDL("create table t_emp(emp_id bigint, name varchar(20));");


        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement("select * from (select * from t_emp) x");
        repository.resolve(stmt, SchemaResolveVisitor.Option.ResolveAllColumn);

        assertEquals("SELECT emp_id, name\n" +
                "FROM (\n" +
                "\tSELECT emp_id, name\n" +
                "\tFROM t_emp\n" +
                ") x", stmt.toString());
    }

    public void test_resolve_2() throws Exception {
        SchemaRepository repository = new SchemaRepository(DbType.mysql);

        repository.acceptDDL("create table t_emp(emp_id bigint, name varchar(20));");


        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement("select * from (select * from t_emp union all select * from t_emp) x");
        repository.resolve(stmt, SchemaResolveVisitor.Option.ResolveAllColumn);

        assertEquals("SELECT emp_id, name\n" +
                "FROM (\n" +
                "\tSELECT emp_id, name\n" +
                "\tFROM t_emp\n" +
                "\tUNION ALL\n" +
                "\tSELECT emp_id, name\n" +
                "\tFROM t_emp\n" +
                ") x", stmt.toString());
    }
}
