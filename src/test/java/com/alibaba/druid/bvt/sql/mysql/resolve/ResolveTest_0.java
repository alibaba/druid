package com.alibaba.druid.bvt.sql.mysql.resolve;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import junit.framework.TestCase;

public class ResolveTest_0 extends TestCase {
    private SchemaRepository repository = new SchemaRepository(DbType.mysql);

    protected void setUp() throws Exception {
        repository.setSchemaLoader(new SchemaRepository.SchemaLoader() {
            @Override
            public String loadDDL(String catalog, String schema, String objectName) {
                if ("t_order".equalsIgnoreCase(objectName)) {
                    return "create table c1.s1.t_order (fid bigint, f_user_id bigint);";
                }

                if ("t_user".equalsIgnoreCase(objectName)) {
                    return "create table c1.s1.t_user (fid bigint, fname varchar(50));";
                }

                if ("nation".equalsIgnoreCase(objectName)) {
                    return "create table nation (fid bigint, fname varchar(50));";
                }
                return null;
            }
        });
    }

    public void test_0() throws Exception {
        String sql = "with x as (select * from t_order o where o.fid = 3) select * from x inner join t_user u on x.fid = u.fid";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);

        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

        SQLJoinTableSource join = (SQLJoinTableSource) queryBlock.getFrom();

        SQLCreateTableStatement createTable = (SQLCreateTableStatement) ((SQLExprTableSource) join.getRight()).getSchemaObject().getStatement();
        SQLExprTableSource left = (SQLExprTableSource) join.getLeft();

        System.out.println(queryBlock.toString());
    }

    public void test_1() throws Exception {
        String sql = "with x as (select * from t_order o where o.fid = 3) select * from x inner join t_user u on x.fid = u.fid";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);

        stmt.accept(new SetCatalogVisitor());

        System.out.println(stmt.toString());
    }

    public void test_2() throws Exception {
        String sql = "select t_order.fid from t_order";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);

        stmt.accept(new SetCatalogVisitor());

        System.out.println(stmt.toString());
    }

    public void test_3() throws Exception {
        String sql = "select s1.t_order.fid from s1.t_order";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);

        stmt.accept(new SetCatalogVisitor());

        System.out.println(stmt.toString());
    }

    public void test_4() throws Exception {
        String sql = "with temp_table as\n" +
                "(\n" +
                " select 0.5 * sum(l_quantity) as col1\n" +
                " from lineitem,\n" +
                "      partsupp\n" +
                " where l_partkey = ps_partkey and l_suppkey = ps_suppkey\n" +
                " and date_parse(l_shipdate, '%Y-%m-%d %H:%i:%s') >= date '1993-01-01'\n" +
                " and date_parse(l_shipdate, '%Y-%m-%d %H:%i:%s') < date '1993-01-01' + interval '1' year\n" +
                ")\n" +
                "select s_name, s_address\n" +
                "from supplier,\n" +
                "     nation\n" +
                "where s_suppkey in (\n" +
                "    select ps_suppkey\n" +
                "    from partsupp,\n" +
                "         temp_table\n" +
                "    where ps_partkey in (\n" +
                "        select p_partkey\n" +
                "        from part\n" +
                "        where p_name like 'dark%' )\n" +
                "        and ps_availqty > temp_table.col1 )\n" +
                "    and s_nationkey = n_nationkey and n_name = 'JORDAN'\n" +
                "order by s_name\n" +
                "limit 1;";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);

        stmt.accept(new SetCatalogVisitor());

        System.out.println(stmt.toString());
    }

    public static class SetCatalogVisitor extends SQLASTVisitorAdapter {
        public boolean visit(SQLExprTableSource x) {
            if (x.getSchemaObject() != null) {
                x.setCatalog("c1", "s1");
            }
            return false;
        }

        public boolean visit(SQLPropertyExpr x) {
            SQLExprTableSource tableSource = (SQLExprTableSource) x.getResolvedTableSource();
            if (tableSource.getSchemaObject() == null) {
                return false;
            }

            SQLExpr owner = x.getOwner();
            if (owner instanceof SQLIdentifierExpr) {
                x.setOwner(
                        new SQLPropertyExpr("c1", "s1", ((SQLIdentifierExpr) owner).getName())
                );
            } else if (owner instanceof SQLPropertyExpr) {
                SQLPropertyExpr owner2 = (SQLPropertyExpr) owner;
                if (owner2.getOwner() instanceof SQLIdentifierExpr) {
                    owner2.setOwner(new SQLPropertyExpr("c1", ((SQLIdentifierExpr) owner2.getOwner()).getName()));
                }
            }

            return false;
        }
    }
}
