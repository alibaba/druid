package com.alibaba.druid.sql.semantic;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;

import java.util.List;

public class SemanticCheck extends SQLASTVisitorAdapter {

    public boolean visit(SQLCreateTableStatement stmt) {
        stmt.containsDuplicateColumnNames(true);
        return true;
    }

    public static boolean check(String sql, DbType dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        SemanticCheck v = new SemanticCheck();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(v);
        }

        return false;
    }
}
