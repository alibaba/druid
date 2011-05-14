package com.alibaba.druid.mock.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class MySqlMockExecuteHandlerImpl implements MockExecuteHandler {
    @Override
    public ResultSet executeQuery(MockStatement statement, String sql) throws SQLException {
        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList(); //
        
        if (stmtList.size() > 1) {
            throw new SQLException("not support multi-statment. " + sql);
        }
        
        // TODO Auto-generated method stub
        return null;
    }

}
