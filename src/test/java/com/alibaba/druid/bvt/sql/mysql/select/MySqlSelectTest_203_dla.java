package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLAdhocTableSource;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.VisitorFeature;

import java.util.ArrayList;
import java.util.List;

public class MySqlSelectTest_203_dla extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT $1, $2 FROM\n" +
                "TABLE temp_1\n" +
                "(\n" +
                "  $1 int,\n" +
                "  $2 string\n" +
                ")\n" +
                "TBLPROPERTIES (\n" +
                "  CATALOG='oss',\n" +
                "  LOCATION='oss://oss-cn-hangzhou-for-openanalytics-dailybuild/jinluo/tbl1_part/kv1.txt',\n" +
                "  SCHEMA='jinluo_test0810'\n" +
                ")\n" +
                "META LIFECYCLE 1";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT $1, $2\n" +
                "FROM TABLE temp_1 (\n" +
                "\t$1 int,\n" +
                "\t$2 string\n" +
                ")\n" +
                "TBLPROPERTIES (\n" +
                "\t'CATALOG' = 'oss',\n" +
                "\t'LOCATION' = 'oss://oss-cn-hangzhou-for-openanalytics-dailybuild/jinluo/tbl1_part/kv1.txt',\n" +
                "\t'SCHEMA' = 'jinluo_test0810'\n" +
                ")\n" +
                "META LIFECYCLE 1", stmt.toString());

        assertEquals("select $1, $2\n" +
                "from table temp_1 (\n" +
                "\t$1 int,\n" +
                "\t$2 string\n" +
                ")\n" +
                "tblproperties (\n" +
                "\t'CATALOG' = 'oss',\n" +
                "\t'LOCATION' = 'oss://oss-cn-hangzhou-for-openanalytics-dailybuild/jinluo/tbl1_part/kv1.txt',\n" +
                "\t'SCHEMA' = 'jinluo_test0810'\n" +
                ")\n" +
                "meta lifecycle 1", stmt.toLowerCaseString());

        final TempTableNameGen tempTableNameGen = new TempTableNameGen() {
            @Override
            public String generateName() {
                return "__temp_table_1";
            }
        };

        final List<SQLCreateTableStatement> createTableStatementList = new ArrayList<SQLCreateTableStatement>();
        SQLASTVisitorAdapter v = new MySqlASTVisitorAdapter() {
            public boolean visit(SQLAdhocTableSource x) {
                final String tableName = tempTableNameGen.generateName();

                HiveCreateTableStatement createStmt = (HiveCreateTableStatement) x.getDefinition();
                createStmt.setParent(null);
                createStmt.setTableName(tableName);
                createStmt.setExternal(true);

                SQLUtils.replaceInParent(x, new SQLExprTableSource(tableName));
                createTableStatementList.add(createStmt);
                return false;
            }

            public boolean visit(SQLVariantRefExpr x) {
                String name = x.getName();
                if (name != null && name.startsWith("$")) {
                    SQLUtils.replaceInParent(x, new SQLIdentifierExpr(name));
                }
                return false;
            }
        };
        stmt.accept(v);

        for (SQLCreateTableStatement createStmt : createTableStatementList) {
            System.out.println(createStmt.toString(VisitorFeature.OutputNameQuote));
        }

        System.out.println();
        System.out.println(stmt.toString(VisitorFeature.OutputNameQuote));


        //
    }

    static interface TempTableNameGen {
        String generateName();
    }
}