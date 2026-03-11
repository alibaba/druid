package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Fix infinite loop when parsing PostgreSQL ANALYZE/VACUUM without trailing table names or semicolons.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6595">Issue #6595</a>
 */
public class Issue6595 {
    @Test
    public void test_analyze_skip_locked_no_semicolon() {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {
            for (String sql : new String[]{
                "ANALYZE SKIP_LOCKED",
                "ANALYZE VERBOSE",
                "ANALYZE VERBOSE SKIP_LOCKED",
                "ANALYZE SKIP_LOCKED VERBOSE",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> stmtList = parser.parseStatementList();
                assertEquals(1, stmtList.size());
                assertNotNull(stmtList.get(0));
            }
        }
    }

    @Test
    public void test_vacuum_options_no_semicolon() {
        for (DbType dbType : new DbType[]{DbType.postgresql, DbType.greenplum, DbType.edb}) {
            for (String sql : new String[]{
                "VACUUM FULL",
                "VACUUM VERBOSE",
                "VACUUM FREEZE",
                "VACUUM FULL VERBOSE FREEZE",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> stmtList = parser.parseStatementList();
                assertEquals(1, stmtList.size());
                assertNotNull(stmtList.get(0));
            }
        }
    }
}
