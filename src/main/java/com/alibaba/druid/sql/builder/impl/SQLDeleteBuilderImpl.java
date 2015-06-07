package com.alibaba.druid.sql.builder.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;

public class SQLDeleteBuilderImpl implements SQLDeleteBuilder {

    private SQLDeleteStatement stmt;
    private String             dbType;

    public SQLDeleteBuilderImpl(String dbType){
        this.dbType = dbType;
    }

    public SQLDeleteBuilderImpl(SQLDeleteStatement stmt, String dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    @Override
    public SQLDeleteBuilderImpl limit(int rowCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLDeleteBuilderImpl limit(int rowCount, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLDeleteBuilder from(String table) {
        return from(table, null);
    }

    @Override
    public SQLDeleteBuilder from(String table, String alias) {
        SQLDeleteStatement delete = getSQLDeleteStatement();
        SQLExprTableSource from = new SQLExprTableSource(new SQLIdentifierExpr(table), alias);
        delete.setTableSource(from);
        return this;
    }

    @Override
    public SQLDeleteBuilder where(String expr) {
        SQLDeleteStatement delete = getSQLDeleteStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        delete.setWhere(exprObj);

        return this;
    }

    @Override
    public SQLDeleteBuilder whereAnd(String expr) {
        SQLDeleteStatement delete = getSQLDeleteStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, exprObj, false, delete.getWhere());
        delete.setWhere(newCondition);

        return this;
    }

    @Override
    public SQLDeleteBuilder whereOr(String expr) {
        SQLDeleteStatement delete = getSQLDeleteStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, exprObj, false, delete.getWhere());
        delete.setWhere(newCondition);

        return this;
    }

    public SQLDeleteStatement getSQLDeleteStatement() {
        if (stmt == null) {
            stmt = createSQLDeleteStatement();
        }
        return stmt;
    }

    public SQLDeleteStatement createSQLDeleteStatement() {
        return new SQLDeleteStatement();
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
