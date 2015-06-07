package com.alibaba.druid.sql.builder.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.builder.SQLUpdateBuilder;

public class SQLUpdateBuilderImpl implements SQLUpdateBuilder {

    private SQLUpdateStatement stmt;
    private String             dbType;

    public SQLUpdateBuilderImpl(String dbType){
        this.dbType = dbType;
    }

    public SQLUpdateBuilderImpl(SQLUpdateStatement stmt, String dbType){
        this.stmt = stmt;
        this.dbType = dbType;
    }

    @Override
    public SQLUpdateBuilderImpl limit(int rowCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLUpdateBuilderImpl limit(int rowCount, int offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLUpdateBuilderImpl from(String table) {
        return from(table, null);
    }

    @Override
    public SQLUpdateBuilderImpl from(String table, String alias) {
        SQLUpdateStatement update = getSQLUpdateStatement();
        SQLExprTableSource from = new SQLExprTableSource(new SQLIdentifierExpr(table), alias);
        update.setTableSource(from);
        return this;
    }

    @Override
    public SQLUpdateBuilderImpl where(String expr) {
        SQLUpdateStatement update = getSQLUpdateStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        update.setWhere(exprObj);

        return this;
    }

    @Override
    public SQLUpdateBuilderImpl whereAnd(String expr) {
        SQLUpdateStatement update = getSQLUpdateStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanAnd, exprObj, false, update.getWhere());
        update.setWhere(newCondition);

        return this;
    }

    @Override
    public SQLUpdateBuilderImpl whereOr(String expr) {
        SQLUpdateStatement update = getSQLUpdateStatement();

        SQLExpr exprObj = SQLUtils.toSQLExpr(expr, dbType);
        SQLExpr newCondition = SQLUtils.buildCondition(SQLBinaryOperator.BooleanOr, exprObj, false, update.getWhere());
        update.setWhere(newCondition);

        return this;
    }

    public SQLUpdateBuilderImpl set(String... items) {
        SQLUpdateStatement update = getSQLUpdateStatement();
        for (String item : items) {
            SQLUpdateSetItem updateSetItem = SQLUtils.toUpdateSetItem(item, dbType);
            update.addItem(updateSetItem);
        }
        
        return this;
    }

    public SQLUpdateStatement getSQLUpdateStatement() {
        if (stmt == null) {
            stmt = createSQLUpdateStatement();
        }
        return stmt;
    }

    public SQLUpdateStatement createSQLUpdateStatement() {
        return new SQLUpdateStatement();
    }
    
    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
