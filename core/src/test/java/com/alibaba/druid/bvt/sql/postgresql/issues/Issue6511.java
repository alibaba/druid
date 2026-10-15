package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PostgreSQL DELETE ... RETURNING should accept specific column list, not only `*`.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6511">Issue #6511</a>
 */
public class Issue6511 {
    @Test
    public void test_delete_returning_single_column() {
        String sql = "DELETE FROM xxx WHERE id IN (?) RETURNING id";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertEquals(
                "DELETE FROM xxx\n"
                        + "WHERE id IN (?)\n"
                        + "RETURNING id",
                SQLUtils.toPGString(stmtList.get(0)));
    }

    @Test
    public void test_delete_returning_multiple_columns() {
        String sql = "DELETE FROM xxx WHERE id = ? RETURNING id, name, age";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertEquals(
                "DELETE FROM xxx\n"
                        + "WHERE id = ?\n"
                        + "RETURNING id, name, age",
                SQLUtils.toPGString(stmtList.get(0)));
    }

    @Test
    public void test_delete_returning_star_still_works() {
        String sql = "DELETE FROM xxx WHERE id IN (?) RETURNING *";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertEquals(
                "DELETE FROM xxx\n"
                        + "WHERE id IN (?)\n"
                        + "RETURNING *",
                SQLUtils.toPGString(stmtList.get(0)));
    }

    @Test
    public void test_delete_returning_qualified_column() {
        String sql = "DELETE FROM xxx x WHERE x.id = ? RETURNING x.id, x.name";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.postgresql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        assertEquals(1, stmtList.size());
        assertEquals(
                "DELETE FROM xxx AS x\n"
                        + "WHERE x.id = ?\n"
                        + "RETURNING x.id, x.name",
                SQLUtils.toPGString(stmtList.get(0)));
    }
}
