package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

/**
 * Created by kaiwang.ckw on 15/05/2017.
 */
public class StatementConverter {

    // remove 'for update' from 'select ... for update'
    public static boolean rewriteSelect(SQLSelectStatement stmt) {
        SQLSelectQuery q = stmt.getSelect().getQuery();
        if (q instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock qb = (MySqlSelectQueryBlock) q;
            if (qb.isForUpdate()) {
                qb.setForUpdate(false);
                return true;
            }
        }
        return false;
    }

    // construct a 'select' from other types of dml
    public static SQLSelectStatement rewrite(SQLStatement stmt) {
        SQLSelectStatement selectStmt;
        if (stmt instanceof SQLSelectStatement) {
            throw new RuntimeException("please use rewriteSelect which does ast modification instead of construction");
        } else if (stmt instanceof SQLUpdateStatement) {
            SQLTableSource tableSource = ((SQLUpdateStatement) stmt).getTableSource();
            SQLExpr where = ((SQLUpdateStatement) stmt).getWhere();
            DbType dbType = ((SQLUpdateStatement) stmt).getDbType();
            selectStmt = buildSelect(tableSource, where, dbType);
        } else if (stmt instanceof SQLDeleteStatement) {
            SQLTableSource tableSource = ((SQLDeleteStatement) stmt).getTableSource();
            SQLExpr where = ((SQLDeleteStatement) stmt).getWhere();
            DbType dbType = ((SQLDeleteStatement) stmt).getDbType();
            selectStmt = buildSelect(tableSource, where, dbType);
        } else if (stmt instanceof SQLInsertStatement) {
            SQLSelect sqlSelect = ((SQLInsertStatement) stmt).getQuery();
            if (sqlSelect != null) {
                selectStmt = new SQLSelectStatement();
                selectStmt.setSelect(sqlSelect);
            } else {
                throw new UnsupportedOperationException("only insert..select.. is supported");
            }
        } else if (stmt instanceof SQLReplaceStatement) {
            SQLQueryExpr sqlQueryExpr = ((SQLReplaceStatement) stmt).getQuery();
            if (sqlQueryExpr != null) {
                SQLSelect sqlSelect = ((SQLReplaceStatement) stmt).getQuery().getSubQuery();
                selectStmt = new SQLSelectStatement();
                selectStmt.setSelect(sqlSelect);
            } else {
                throw new UnsupportedOperationException("only replace..select.. is supported");
            }
        } else {
            throw new UnsupportedOperationException("only select/update/delete/insert/replace are supported");
        }
        return selectStmt;
    }

    // build 'select * from ...'
    static SQLSelectStatement buildSelect(SQLTableSource tableSource, SQLExpr where, DbType dbType) {
        SQLSelectQueryBlock sqlSelectQuery = new SQLSelectQueryBlock();
        sqlSelectQuery.addSelectItem(new SQLSelectItem(new SQLAllColumnExpr()));
        sqlSelectQuery.setWhere(where);
        sqlSelectQuery.setFrom(tableSource);
        tableSource.setParent(sqlSelectQuery);

        SQLSelect sqlSelect = new SQLSelect();
        sqlSelect.setQuery(sqlSelectQuery);

        SQLSelectStatement selectStmt = new SQLSelectStatement();
        selectStmt.setSelect(sqlSelect);
        selectStmt.setDbType(dbType);

        return selectStmt;
    }
}
