package com.alibaba.druid.bvt.sql.sqlserver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class SQLServerCollateTest extends TestCase {

    public void testCollateWithColumn() {
        String sql = "SELECT name COLLATE Chinese_PRC_CI_AI_WS FROM users";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());

        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);
        stmtList.get(0).accept(visitor);

        assertEquals("SELECT name COLLATE Chinese_PRC_CI_AI_WS\nFROM users", out.toString());
    }

    public void testCollateWithStringLiteral() {
        String sql = "SELECT 'abc' COLLATE Chinese_PRC_CI_AI_WS";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
    }

    public void testCollateInWhereClause() {
        String sql = "SELECT * FROM users WHERE name = '张三' COLLATE Chinese_PRC_CI_AI_WS";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
    }

    public void testCollateComplexExpression() {
        String sql = "SELECT id FROM table_a a, table_b b WHERE a.REPORTLINE = b.REPORT_ITEM_ID COLLATE Chinese_PRC_CI_AI_WS AND a.referencedate = ?";
        SQLServerStatementParser parser = new SQLServerStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        assertEquals(1, stmtList.size());
    }

    public void testCollateWithWallFilter() {
        WallProvider provider = new SQLServerWallProvider();

        String sql = "SELECT * FROM table_a a, table_b b WHERE a.id = b.id " +
                "AND a.REPORTLINE = b.REPORT_ITEM_ID COLLATE Chinese_PRC_CI_AI_WS " +
                "AND a.referencedate=?";

        WallCheckResult result = provider.check(sql);
        boolean isValid = result.getViolations().isEmpty();

        Assert.assertTrue("SQL应该通过校验", isValid);
    }

    public void testCollateWithStringsAndWallFilter() {
        WallProvider provider = new SQLServerWallProvider();

        String sql = "SELECT * FROM users WHERE name = '张三' COLLATE Chinese_PRC_CI_AI_WS";

        WallCheckResult result = provider.check(sql);
        boolean isValid = result.getViolations().isEmpty();

        Assert.assertTrue("字符串COLLATE语句应该通过校验", isValid);
    }

    public void testCollateWithColumnsAndWallFilter() {
        WallProvider provider = new SQLServerWallProvider();

        String sql = "SELECT * FROM table_a a, table_b b WHERE a.name COLLATE Chinese_PRC_CI_AI_WS = b.name";

        WallCheckResult result = provider.check(sql);
        boolean isValid = result.getViolations().isEmpty();

        Assert.assertTrue("列间COLLATE语句应该通过校验", isValid);
    }

    public void testValidSqlWithoutCollate() {
        WallProvider provider = new SQLServerWallProvider();

        String sql = "SELECT * FROM users WHERE name = '张三'";

        WallCheckResult result = provider.check(sql);
        boolean isValid = result.getViolations().isEmpty();

        Assert.assertTrue("正常SQL应该通过校验", isValid);
    }
}


