package com.alibaba.druid.hbase.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.alibaba.druid.hbase.exec.ExecutePlan;
import com.alibaba.druid.hbase.exec.InsertExecutePlan;
import com.alibaba.druid.hbase.exec.ShowTablesPlan;
import com.alibaba.druid.hbase.exec.SingleTableQueryExecutePlan;
import com.alibaba.druid.hbase.hbql.ast.HBQLShowStatement;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.jdbc.PreparedStatementBase;
import com.alibaba.druid.util.jdbc.ResultSetMetaDataBase.ColumnMetaData;

public class HPreparedStatementImpl extends PreparedStatementBase implements HPreparedStatement {

    private final String    sql;
    private String[]        columnNames;

    private HBaseConnection hbaseConnection;

    private ExecutePlan     executePlan;

    private String          dbType = "hbase";

    public HPreparedStatementImpl(HBaseConnection conn, String sql) throws SQLException{
        super(conn);
        this.sql = sql;
        this.hbaseConnection = conn;

        init();
    }

    private void splitCondition(List<SQLExpr> conditions, SQLExpr expr) {
        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) expr;
            if (binaryExpr.getOperator() == SQLBinaryOperator.BooleanAnd) {
                splitCondition(conditions, binaryExpr.getLeft());
                splitCondition(conditions, binaryExpr.getRight());
                return;
            }
        }
        conditions.add(expr);
    }

    public void init() throws SQLException {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new SQLException("not support multi-statement");
        }

        SQLStatement sqlStmt = stmtList.get(0);
        if (sqlStmt instanceof SQLSelectStatement) {
            SQLSelectStatement selectStmt = (SQLSelectStatement) sqlStmt;

            SQLEvalVisitor evalVisitor = SQLEvalVisitorUtils.createEvalVisitor(dbType);
            selectStmt.accept(evalVisitor);

            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectStmt.getSelect().getQuery();

            SQLExprTableSource tableSource = (SQLExprTableSource) selectQueryBlock.getFrom();
            String tableName = ((SQLIdentifierExpr) tableSource.getExpr()).getName();

            SingleTableQueryExecutePlan singleTableQueryExecuetePlan = new SingleTableQueryExecutePlan();
            singleTableQueryExecuetePlan.setTableName(tableName);

            splitCondition(singleTableQueryExecuetePlan.getConditions(), selectQueryBlock.getWhere());

            HResultSetMetaData resultMetaData = new HResultSetMetaData();
            for (SQLSelectItem selectItem : selectQueryBlock.getSelectList()) {
                ColumnMetaData columnMetaData = new ColumnMetaData();
                if (selectItem.getAlias() != null) {
                    columnMetaData.setColumnLabel(selectItem.getAlias());
                }
                columnMetaData.setColumnName(SQLUtils.toSQLString(selectItem.getExpr(), dbType));
                columnMetaData.setColumnType(Types.BINARY);
                resultMetaData.getColumns().add(columnMetaData);
            }
            singleTableQueryExecuetePlan.setResultMetaData(resultMetaData);

            this.executePlan = singleTableQueryExecuetePlan;
        } else if (sqlStmt instanceof SQLInsertStatement) {
            SQLInsertStatement insertStmt = (SQLInsertStatement) sqlStmt;

            SQLEvalVisitor evalVisitor = SQLEvalVisitorUtils.createEvalVisitor(dbType);
            insertStmt.accept(evalVisitor);

            String tableName = ((SQLIdentifierExpr) insertStmt.getTableSource().getExpr()).getName();

            InsertExecutePlan insertExecutePlan = new InsertExecutePlan();
            insertExecutePlan.setTableName(tableName);

            for (int i = 0; i < insertStmt.getColumns().size(); ++i) {
                SQLExpr columnExpr = insertStmt.getColumns().get(i);
                SQLExpr valueExpr = insertStmt.getValues().getValues().get(i);

                String columnName = ((SQLIdentifierExpr) columnExpr).getName();
                insertExecutePlan.getColumns().put(columnName, valueExpr);
            }

            this.executePlan = insertExecutePlan;
        } else if (sqlStmt instanceof HBQLShowStatement) {
            ShowTablesPlan showTablePlan = new ShowTablesPlan();
            this.executePlan = showTablePlan;
        } else {
            throw new SQLException("TODO");
        }
    }

    public ExecutePlan getExecutePlan() {
        return executePlan;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public HBaseConnection getConnection() throws SQLException {
        return hbaseConnection;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return this.executePlan.executeQuery(this);
    }

    @Override
    public int executeUpdate() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean execute() throws SQLException {
        this.executePlan.execute(this);

        return false;
    }

}
