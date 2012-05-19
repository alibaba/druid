package com.alibaba.druid.hbase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.alibaba.druid.common.jdbc.PreparedStatementBase;
import com.alibaba.druid.hbase.exec.ExecutePlan;
import com.alibaba.druid.hbase.exec.InsertExecutePlan;
import com.alibaba.druid.hbase.exec.SingleTableQueryExecutePlan;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;

public class HBasePreparedStatement extends PreparedStatementBase implements PreparedStatement, HBaseStatementInterface {

    private final String    sql;
    private String[]        columnNames;

    private HBaseConnection hbaseConnection;

    private ExecutePlan     executePlan;

    public HBasePreparedStatement(HBaseConnection conn, String sql) throws SQLException{
        super(conn);
        this.sql = sql;
        this.hbaseConnection = conn;

        init();
    }

    public void init() throws SQLException {
        String dbType = this.hbaseConnection.getConnectProperties().getProperty("dbType");
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        if (stmtList.size() != 1) {
            throw new SQLException("not support multi-statement");
        }

        SQLStatement sqlStmt = stmtList.get(0);
        if (sqlStmt instanceof SQLSelectStatement) {
            SQLSelectStatement selectStmt = (SQLSelectStatement) sqlStmt;

            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectStmt.getSelect().getQuery();

            SQLExprTableSource tableSource = (SQLExprTableSource) selectQueryBlock.getFrom();
            String tableName = ((SQLIdentifierExpr) tableSource.getExpr()).getName();

            SingleTableQueryExecutePlan singleTableQueryExecuetePlan = new SingleTableQueryExecutePlan();
            singleTableQueryExecuetePlan.setTableName(tableName);

            this.executePlan = singleTableQueryExecuetePlan;
        } else if (sqlStmt instanceof SQLInsertStatement) {
            SQLInsertStatement insertStmt = (SQLInsertStatement) sqlStmt;
            
            String tableName = ((SQLIdentifierExpr) insertStmt.getTableSource().getExpr()).getName();
            
            InsertExecutePlan insertExecutePlan = new InsertExecutePlan();
            insertExecutePlan.setTableName(tableName);
            
            throw new SQLException("TODO");
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
        checkOpen();

        return false;
    }

}
