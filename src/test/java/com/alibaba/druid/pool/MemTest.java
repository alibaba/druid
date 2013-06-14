package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class MemTest {

    public static void main(String[] args) throws Exception {
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setUrl("jdbc:mock:xxx");
//        dataSource.setInitialSize(1);
//        dataSource.setDbType("mysql");
//        dataSource.setFilters("mergeStat");
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(50);

        // for (int i = 1000; i < 2000; ++i) {
        // String tableName = "t" + i;
        // String sql = "select * from " + tableName + " where " + tableName + ".id = " + i;
        // Connection conn = dataSource.getConnection();
        //
        // PreparedStatement stmt = conn.prepareStatement(sql);
        // stmt.execute();
        // stmt.close();
        //
        // conn.close();
        // }

        // Connection conn = dataSource.getConnection();
        //
        String sql = "SELECT UMID, HWID, MAC, GUID, RID , GMT_CREATE, GMT_MODIFIED FROM umid_rid " + //
                     "WHERE RID = ? " + //
                     "LIMIT 10";
        // PreparedStatement stmt = conn.prepareStatement(sql);
        // stmt.execute();

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, "mysql");
        List<SQLStatement> statementList = parser.parseStatementList();

        SQLStatement stmt = statementList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = ParameterizedOutputVisitorUtils.createParameterizedOutputVisitor(out, "mysql");
        stmt.accept(visitor);

        Thread.sleep(1000 * 1000);
//        dataSource.close();
    }
}
