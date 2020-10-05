package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

/**
 * Created by wenshao on 16/8/22.
 */
public class MySqlParameterizedOutputVisitorTest_8 extends com.alibaba.druid.bvt.sql.mysql.param.MySQLParameterizedTest {

    public void test_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;

        String sql = "insert into test values(2,1) on duplicate key update ts=ts % 10000 +1";
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        String expected = "INSERT INTO test\n" +
                "VALUES (?, ?)\n" +
                "ON DUPLICATE KEY UPDATE ts = ts % ? + ?";
        Assert.assertEquals(expected, psql);

        paramaterizeAST(sql, expected);


        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        ExportParameterVisitor visitor = new MySqlExportParameterVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        Assert.assertEquals(4, visitor.getParameters().size());
        Assert.assertEquals(2, visitor.getParameters().get(0));
        Assert.assertEquals(1, visitor.getParameters().get(1));
        Assert.assertEquals(10000, visitor.getParameters().get(2));
        Assert.assertEquals(1, visitor.getParameters().get(3));
    }
}

