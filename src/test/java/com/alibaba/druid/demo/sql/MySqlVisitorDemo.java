package com.alibaba.druid.demo.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlVisitorDemo extends TestCase {
    public void test_for_demo() throws Exception {
        String sql = "select * from mytable a where a.id = 3";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);

        ExportTableAliasVisitor visitor = new ExportTableAliasVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        SQLTableSource tableSource = visitor.getAliasMap().get("a");
        System.out.println(tableSource);
    }

    public static class ExportTableAliasVisitor extends MySqlASTVisitorAdapter {
        private Map<String, SQLTableSource> aliasMap = new HashMap<String, SQLTableSource>();
        public boolean visit(SQLExprTableSource x) {
            String alias = x.getAlias();
            aliasMap.put(alias, x);
            return true;
        }

        public Map<String, SQLTableSource> getAliasMap() {
            return aliasMap;
        }
    }
}
