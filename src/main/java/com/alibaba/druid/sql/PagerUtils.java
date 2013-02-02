package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;

public class PagerUtils {

    public static String count(String sql, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        if (!(stmt instanceof SQLSelectStatement)) {
            throw new IllegalArgumentException("sql not support count : " + sql);
        }

        SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

        return count(selectStmt, dbType);
    }

    private static String count(SQLSelectStatement stmt, String dbType) {
        SQLSelect select = stmt.getSelect();
        if (select.getOrderBy() != null) {
            select.setOrderBy(null);
        }
        SQLSelectQuery query = select.getQuery();
        clearOrderBy(query);

        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectItem countItem = createCountItem(dbType);
            
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            queryBlock.getSelectList().clear();
            queryBlock.getSelectList().add(countItem);
            return SQLUtils.toSQLString(stmt, dbType);
        } else if (query instanceof SQLUnionQuery) {
            SQLSelectQueryBlock countSelectQuery = createQueryBlock(dbType);
            
            SQLSelectItem countItem = createCountItem(dbType);
            countSelectQuery.getSelectList().add(countItem);
            
            countSelectQuery.setFrom(new SQLSubqueryTableSource(select));
            
            SQLSelect countSelect = new SQLSelect(countSelectQuery);
            SQLSelectStatement countStmt = new SQLSelectStatement(countSelect);
            
            return SQLUtils.toSQLString(countStmt, dbType);
        }

        throw new IllegalStateException();
    }

    private static SQLSelectQueryBlock createQueryBlock(String dbType) {
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return new MySqlSelectQueryBlock();
        }
        
        if (JdbcConstants.ORACLE.equals(dbType)) {
            return new OracleSelectQueryBlock();
        }
        
        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return new PGSelectQueryBlock();
        }
        
        if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            return new SQLServerSelectQueryBlock();
        }
        
        return new SQLSelectQueryBlock();
    }

    private static SQLSelectItem createCountItem(String dbType) {
        SQLAggregateExpr countExpr;

        if (JdbcConstants.ORACLE.equals(dbType)) {
            countExpr = new OracleAggregateExpr("COUNT");
        } else {
            countExpr = new SQLAggregateExpr("COUNT");
        }
        countExpr.getArguments().add(new SQLAllColumnExpr());

        SQLSelectItem countItem = new SQLSelectItem(countExpr);
        return countItem;
    }

    private static void clearOrderBy(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            if (queryBlock instanceof MySqlSelectQueryBlock) {
                MySqlSelectQueryBlock mysqlQueryBlock = (MySqlSelectQueryBlock) queryBlock;
                if (mysqlQueryBlock.getOrderBy() != null) {
                    mysqlQueryBlock.setOrderBy(null);
                }
            } else if (queryBlock instanceof PGSelectQueryBlock) {
                PGSelectQueryBlock pgQueryBlock = (PGSelectQueryBlock) queryBlock;
                if (pgQueryBlock.getOrderBy() != null) {
                    pgQueryBlock.setOrderBy(null);
                }
            }
            return;
        }

        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery union = (SQLUnionQuery) query;
            if (union.getOrderBy() != null) {
                union.setOrderBy(null);
            }
            clearOrderBy(union.getLeft());
            clearOrderBy(union.getRight());
        }
    }
}
