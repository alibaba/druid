package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by wenshao on 16/8/22.
 */
public class MySqlParameterizedOutputVisitorTest_9 extends com.alibaba.druid.bvt.sql.mysql.param.MySQLParameterizedTest {
    public void test_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "select * from t limit 3, 4";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        String expected = "SELECT *\n" +
                "FROM t\n" +
                "LIMIT ?, ?";
        assertEquals(expected, psql);

        paramaterizeAST(sql, expected);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        assertEquals(2, visitor.getParameters().size());
        assertEquals(3, visitor.getParameters().get(0));
        assertEquals(4, visitor.getParameters().get(1));
    }
}

