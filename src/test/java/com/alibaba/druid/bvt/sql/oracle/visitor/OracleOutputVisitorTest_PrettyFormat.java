package com.alibaba.druid.bvt.sql.oracle.visitor;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;

/**
 *
 * User: carvin
 * Date: 12-11-13
 * Time: 下午7:16
 * Test OralceOutputVisitor and start with ... connect by parser.
 */
public class OracleOutputVisitorTest_PrettyFormat {
    @Test
    public void testConnectByParserAndPrettyFormatOutput() {
        String sql = "select * from ge_rms_company start with comcode = '00' connect by nocycle prior comcode = uppercomcode";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.ORACLE);
        List<SQLStatement> stmtList = parser.parseStatementList();
        StringBuilder out = new StringBuilder();
        // PrettyFormat use default : true
        SQLASTOutputVisitor visitor = new OracleOutputVisitor(out);
        for(SQLStatement statement : stmtList) {
            statement.accept(visitor);
        }

        String expectResult = "SELECT *\nFROM ge_rms_company\nSTART WITH comcode = '00'\nCONNECT BY NOCYCLE PRIOR comcode = uppercomcode";
        Assert.assertEquals(expectResult, out.toString());

        out.setLength(0);
        visitor = new OracleOutputVisitor(out);
        // setPrettyFormat : false
        visitor.setPrettyFormat(false);
        for(SQLStatement statement : stmtList) {
            statement.accept(visitor);
        }
        expectResult = "SELECT * FROM ge_rms_company START WITH comcode = '00' CONNECT BY NOCYCLE PRIOR comcode = uppercomcode";
        Assert.assertEquals(expectResult, out.toString());
    }
}
