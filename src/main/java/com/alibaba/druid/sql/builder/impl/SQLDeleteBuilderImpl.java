/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.builder.impl;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.builder.SQLDeleteBuilder;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;

import java.util.List;

public class SQLDeleteBuilderImpl implements SQLDeleteBuilder {

    private SQLDeleteStatement stmt;
    private DbType             dbType;

    public SQLDeleteBuilderImpl(DbType dbType){
        this.dbType = dbType;
    }
    
    public SQLDeleteBuilderImpl(String sql, DbType dbType){
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        }

        SQLDeleteStatement stmt = (SQLDeleteStatement) stmtList.get(0);
        this.stmt = stmt;
        this.dbType = dbType;
    }

    public SQLDeleteBuilderImpl(SQLDeleteStatement stmt, DbType dbType){
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
        switch (dbType) {
            case oracle:
                return new OracleDeleteStatement();
            case mysql:
                return new MySqlDeleteStatement();
            case postgresql:
                return new PGDeleteStatement();
            default:
                return new SQLDeleteStatement();
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(stmt, dbType);
    }
}
