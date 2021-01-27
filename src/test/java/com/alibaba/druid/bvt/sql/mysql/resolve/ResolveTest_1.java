package com.alibaba.druid.bvt.sql.mysql.resolve;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import junit.framework.TestCase;

public class ResolveTest_1 extends TestCase {
    private SchemaRepository repository = new SchemaRepository(DbType.mysql, DbType.hive);

    protected void setUp() throws Exception {
        repository.setSchemaLoader(new SchemaRepository.SchemaLoader() {
            @Override
            public String loadDDL(String catalog, String schema, String objectName) {
                if ("b".equalsIgnoreCase(objectName)) {
                    return "create table ots.a.b ( id int not null, shippingaddress STRUCT<address1:string, `address2`:string, `city`:string, `state`:string>);\n";
                }

                if ("foo".equalsIgnoreCase(objectName)) {
                    return mockDDL("mysql", "hello", "foo");
                } else if ("bar".equalsIgnoreCase(objectName)) {
                    return mockDDL("oss", "world", "bar");
                } else if ("oss".equalsIgnoreCase(objectName)) {
                    return mockDDL("oss", "world", "error");
                }

                return null;
            }
        });
    }

    private String mockDDL(String catalogType, String schemaName, String tableName) {
        return "CREATE EXTERNAL TABLE " + catalogType + "." + schemaName + "." + tableName + " (\n" +
                "  `name_text` VARCHAR" +
                ")";
    }

    public void test_0() throws Exception {
        String sql = "select * from a.b";
        SQLSelectStatement stmt = (SQLSelectStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);
        stmt.accept(new SetCatalogVisitor());

        SQLSelectQueryBlock queryBlock = stmt.getSelect().getQueryBlock();

        SQLExprTableSource tableSource = (SQLExprTableSource) queryBlock.getFrom();

        final SchemaObject schemaObject = tableSource.getSchemaObject();
        assertEquals("b", schemaObject.getName());
        assertEquals("ots.a", schemaObject.getSchema().getName());
        assertEquals("ots", schemaObject.getSchema().getCatalog());
        SQLCreateTableStatement createTable = (SQLCreateTableStatement) schemaObject.getStatement();
        assertNotNull(createTable);

        System.out.println(queryBlock.toString());
    }

    public void test_1() throws Exception {
        String sql = "insert into foo select * from bar";
        SQLInsertStatement stmt = (SQLInsertStatement) SQLUtils.parseStatements(sql, DbType.mysql).get(0);
        repository.resolve(stmt);
        stmt.accept(new SetCatalogVisitor());

        SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) stmt.getQuery().getQuery();

        SQLExprTableSource tableSource = (SQLExprTableSource) queryBlock.getFrom();

        final SchemaObject schemaObject = tableSource.getSchemaObject();
        assertEquals("bar", schemaObject.getName());
        assertEquals("oss.world", schemaObject.getSchema().getName());
        assertEquals("oss", schemaObject.getSchema().getCatalog());
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
