package com.alibaba.druid.bvt.sql.dm;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://github.com/alibaba/druid/issues/6583">Issue来源</a>
 */
public class DM_IdentityTest {

    @Test
    public void test_identity() {
        String sql = "CREATE TABLE TEST.TEST_CREATE (\n"
                + "    id INT IDENTITY(1,1),\n"
                + "    name VARCHAR(64) NOT NULL,\n"
                + "    PRIMARY KEY (id)\n"
                + ")";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.dm);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_identity_with_not_null() {
        String sql = "CREATE TABLE t (id INT IDENTITY(1,1) NOT NULL PRIMARY KEY, val VARCHAR(100))";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.dm);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
    }
}
